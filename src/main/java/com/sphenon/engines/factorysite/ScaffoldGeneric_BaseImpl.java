package com.sphenon.engines.factorysite;

/****************************************************************************
  Copyright 2001-2024 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.monitoring.*;
import com.sphenon.basics.accessory.classes.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import com.sphenon.engines.factorysite.BuildAssertion.AssertionType;
import com.sphenon.engines.factorysite.BuildScript.ScriptType;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Vector;

abstract public class ScaffoldGeneric_BaseImpl
  implements Scaffold
{
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.ScaffoldGeneric_BaseImpl"); };

    protected Type type;
    protected String problem_monitor_oid;
    protected FactorySite factory_site;

    public FactorySite getFactorySite (CallContext context) {
        return this.factory_site;
    }

    public void setFactorySite (CallContext context, FactorySite factory_site) {
        CustomaryContext.create((Context)context).throwConfigurationError(context, "FactorySite can only be attached afterwards if final implementation class overrides this method and provides pre/post build material");
        throw (ExceptionConfigurationError) null; // compiler insists
    }

    protected void setFactorySite (CallContext context, FactorySite factory_site, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts) {
        this.factory_site = factory_site;
        if (this.factory_site != null) {
            if (pre_conditions != null && pre_conditions.size() != 0) {
                this.pre_conditions = new Vector<BuildAssertion>();
                for (String[] pre_condition : pre_conditions) {
                    BuildAssertion ba = new BuildAssertion(context, pre_condition[0], pre_condition[1], pre_condition[2], AssertionType.PRE, this);
                    this.pre_conditions.add(ba);
                    if (ba.getPass(context) != 1) {
                        factory_site.addPostponedPreCondition(context, ba);
                    }
                }
            }
            if (post_conditions != null && post_conditions.size() != 0) {
                this.post_conditions = new Vector<BuildAssertion>();
                for (String[] post_condition : post_conditions) {
                    BuildAssertion ba = new BuildAssertion(context, post_condition[0], post_condition[1], post_condition[2], AssertionType.POST, this);
                    this.post_conditions.add(ba);
                    if (ba.getPass(context) != 1) {
                        factory_site.addPostponedPostCondition(context, ba);
                    }
                }
            }
            if (pre_build_scripts != null && pre_build_scripts.size() != 0) {
                this.pre_build_scripts = new Vector<BuildScript>();
                for (String[] pre_build_script : pre_build_scripts) {
                    BuildScript bs = new BuildScript(context, pre_build_script[0], pre_build_script[1], pre_build_script[2], ScriptType.PRE, this);
                    this.pre_build_scripts.add(bs);
                    if (bs.getPass(context) != 1) {
                        factory_site.addPostponedPreBuildScript(context, bs);
                    }
                }
            }
            if (post_build_scripts != null && post_build_scripts.size() != 0) {
                this.post_build_scripts = new Vector<BuildScript>();
                for (String[] post_build_script : post_build_scripts) {
                    BuildScript bs = new BuildScript(context, post_build_script[0], post_build_script[1], post_build_script[2], ScriptType.POST, this);
                    this.post_build_scripts.add(bs);
                    if (bs.getPass(context) != 1) {
                        factory_site.addPostponedPostBuildScript(context, bs);
                    }
                }
            }
        }
    }

    public ScaffoldGeneric_BaseImpl (CallContext context, FactorySite factory_site, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String problem_monitor_oid) {
        this.factory_site = factory_site;
        this.problem_monitor_oid = problem_monitor_oid;
        this.skip = false;

        this.setFactorySite (context, factory_site, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts);
    }

    protected void performPreBuildActions(CallContext context) throws DataSourceUnavailable {
        Class_Scope local_scope = new Class_Scope(context, null, this.factory_site.getCurrentScope(context));

        if (this.pre_conditions != null) {
            for (BuildAssertion pre_condition : this.pre_conditions) {
                pre_condition.setScope(context, local_scope);
                pre_condition.check(context, 1);
            }
        }
        if (this.pre_build_scripts != null) {
            for (BuildScript pre_build_script : this.pre_build_scripts) {
                pre_build_script.setScope(context, local_scope);
                pre_build_script.evaluate(context, 1);
            }
        }
    }

    protected void performPostBuildActions(CallContext context, Object result) throws DataSourceUnavailable {
        Class_Scope local_scope = new Class_Scope(context, null, this.factory_site.getCurrentScope(context));
        local_scope.set(context, "result", result);

        if (this.post_build_scripts != null) {
            for (BuildScript post_build_script : this.post_build_scripts) {
                post_build_script.setScope(context, local_scope);
                post_build_script.evaluate(context, 1);
            }
        }
        if (this.post_conditions != null) {
            for (BuildAssertion post_condition : this.post_conditions) {
                post_condition.setScope(context, local_scope);
                post_condition.check(context, 1);
            }
        }
    }

    protected Vector<BuildAssertion> pre_conditions;

    protected Vector<BuildAssertion> post_conditions;

    protected Vector<BuildScript>    pre_build_scripts;

    protected Vector<BuildScript>    post_build_scripts;

    protected boolean o2b(CallContext context, Object o) {
        try {
            return (Boolean) o;
        } catch (ClassCastException cce) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, cce, "If expression in OCP does not evaluate to a Boolean instance, but to a '%(got)' (at '%(info)')", "got", o.getClass(), "info", this.source_location_info);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    protected Expression getIfExpression(CallContext context, DynamicScaffoldParameter dsp) {
        if (    dsp != null
             && dsp.getIfExpression(context) != null
            ) {
            return dsp.getIfExpression(context);
        }
        return null;
    }

    protected boolean getIf(CallContext context, DynamicScaffoldParameter dsp) throws DataSourceUnavailable {
        if (    dsp != null
             && dsp.getIfExpression(context) != null
            ) {
            try {
                return o2b(context, dsp.getIfExpression(context).evaluate(context, this.factory_site.getCurrentScope(context)));
            } catch (EvaluationFailure ef) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of expression '%(expression)' failed (at '%(info)')", "expression", dsp.getIfExpression(context).getExpression(context), "info", this.source_location_info);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        } else {
            return true;
        }
    }

    protected Scope previous_scope_override;
    protected Scope previous_scope;
    protected boolean pushed;

    protected Scope pushScope(CallContext context, DynamicScaffoldParameter dsp, boolean allow_loop) throws DataSourceUnavailable {
        this.previous_scope = null;
        this.pushed = false;
        Class_Scope local_scope = null;
        boolean vde = false;
        if (    dsp != null
             && (    (vde = (dsp.getVariableDefinitionExpression(context) != null))
                  || dsp.getForeachVariableDefinitionExpression(context) != null
                  || (allow_loop && dsp.getForeachExpression(context) != null)
                )
            ) {
            this.pushed = true;
            this.previous_scope_override = this.factory_site.getCurrentScopeOverride(context);
            this.previous_scope = this.factory_site.getCurrentScope(context);
            local_scope = new Class_Scope(context, null, this.previous_scope);
            this.factory_site.setCurrentScopeOverride(context, local_scope);
            
            if (vde) {
                int index = 0;
                for (String name : dsp.getVariableName(context)) {
                    Object object = null;
                    try {
                        object = dsp.getVariableDefinitionExpression(context)[index].evaluate(context, local_scope);
                    } catch (EvaluationFailure ef) {
                        CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of expression '%(expression)' failed (at '%(info)')", "expression", dsp.getVariableDefinitionExpression(context)[index].getExpression(context), "info", this.source_location_info);
                        throw (ExceptionPreConditionViolation) null; // compiler insists
                    }
                    local_scope.set(context, name, object);
                    index++;
                }
            }
        }
        return local_scope;
    }

    protected void popScope(CallContext context) {
        if (this.pushed) {
            this.pushed = false;
            this.factory_site.setCurrentScopeOverride(context, this.previous_scope_override);
        }
    }

    protected void updateScope(CallContext context, DynamicScaffoldParameter dsp, Scope local_scope, Object index_object) {
        this.updateScope(context, dsp, local_scope, index_object, null);
    }

    protected void updateScope(CallContext context, DynamicScaffoldParameter dsp, Scope local_scope, Object index_object, String index_name) {
        if (index_name == null) {
            index_name = dsp.getForeachIndexName(context);
        }
        local_scope.set(context, index_name, index_object);
        if (dsp.getForeachVariableDefinitionExpression(context) != null
           ) {
            int index = 0;
            for (String name : dsp.getForeachVariableName(context)) {
                Object object = null;
                try {
                    object = dsp.getForeachVariableDefinitionExpression(context)[index].evaluate(context, local_scope);
                } catch (EvaluationFailure ef) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of expression '%(expression)' failed (at '%(info)')", "expression", dsp.getForeachVariableDefinitionExpression(context)[index].getExpression(context), "info", this.source_location_info);
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
                local_scope.set(context, name, object);
                index++;
            }
        }
    }

    protected Iterable o2i(CallContext context, Object o) {
        if (o instanceof GenericIterable) {
            return ((GenericIterable) o).getIterable(context);
        }
        try {
            return (Iterable) (o == null ? (new java.util.ArrayList()) : o.getClass().isArray() ? java.util.Arrays.asList((Object[]) o) : o);
        } catch (ClassCastException cce) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, cce, "Foreach expression in OCP does not evaluate to an Iterable instance, but to a '%(got)' (at '%(info)')", "got", o.getClass(), "info", this.source_location_info);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    protected Expression getForeachExpression(CallContext context, DynamicScaffoldParameter dsp) {
        if (    dsp != null
             && dsp.getForeachExpression(context) != null
            ) {
            return dsp.getForeachExpression(context);
        }
        return null;
    }

    protected Iterable getForeach(CallContext context, DynamicScaffoldParameter dsp, Scope local_scope) {
        if (    dsp != null
             && dsp.getForeachExpression(context) != null
            ) {
            try {
                return o2i(context,dsp.getForeachExpression(context).evaluate(context, local_scope != null ? local_scope : this.previous_scope));
            } catch (EvaluationFailure ef) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of expression '%(expression)' failed (at '%(info)')", "expression", dsp.getForeachExpression(context).getExpression(context), "info", this.source_location_info);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        }
        return (Iterable) null;
    }

    protected String source_location_info;
    
    public String getSourceLocationInfo (CallContext context) {
        return this.source_location_info;
    }
    
    public void setSourceLocationInfo (CallContext context, String source_location_info) {
        this.source_location_info = source_location_info;
    }

    protected Type component_type;

    public Type getComponentType (CallContext context) {
        return this.component_type;
    }

    protected void checkComponentType(CallContext context, Object o, int index) {
        // From ...Class:
        // if (o != null && ! TypeManager.get(context, o.getClass()).isA(context, this.component_type)) {
        if (o != null && ! TypeManager.getJavaClass(context, this.component_type).isAssignableFrom(o.getClass())) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Object of type '%(type)' cannot be delivered, dynamic parameters in use, but parameter #%(index) (counting from 0) is not a '%(componenttype)', as expected, but a '%(got)' (at '%(info)')", "type", this.type.getName(context), "index", t.s(index), "componenttype", this.component_type, "got", o.getClass().getName(), "info", this.source_location_info);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public void reset(CallContext context) {
        this.problem_monitor = null;
        this.local_problem_monitor = null;
        this.instance_problem_monitor = null;
        this.skip = false;
    }

    protected boolean skip;

    public void skip(CallContext context) {
        this.skip = true;
    }

    public boolean isSkipped(CallContext context) {
        return this.skip;
    }

    protected ProblemMonitor problem_monitor;

    protected ProblemMonitor getProblemMonitor(CallContext context) {
        if (this.problem_monitor == null && this.problem_monitor_oid != null && this.problem_monitor_oid.length() != 0) {
            DataSource ds = this.factory_site.getDataSourceById(context, this.problem_monitor_oid);
            if ((ds instanceof Scaffold) == false) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, "Problem monitor '%(oid)' is not a Scaffold (at '%(info)')", "info", this.source_location_info);
                throw (ExceptionConfigurationError) null; // compiler insists
            }
            this.problem_monitor = ((Scaffold) ds).getLocalProblemMonitor(context);
        }
        return this.problem_monitor;
    }

    protected void handleProblem(CallContext context, Throwable throwable) throws DataSourceUnavailable, IgnoreErraneousDataSource {
        ProblemMonitor pm = this.getProblemMonitor(context);
        if (pm == null) {
            if (throwable instanceof DataSourceUnavailable) { throw (DataSourceUnavailable) throwable; }
            if (throwable instanceof ExceptionError) { throw (ExceptionError) throwable; }
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, throwable, "Exception handler unable to handle this exception");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        pm.addProblemStatus(context, ProblemState.ERROR, throwable);
        IgnoreErraneousDataSource.createAndThrow(context, "");
    }

    protected ProblemMonitor local_problem_monitor;
    protected ProblemMonitor instance_problem_monitor;

    public ProblemMonitor getLocalProblemMonitor(CallContext context) {
        if (this.instance_problem_monitor != null) {
            return this.instance_problem_monitor;
        }
        if (this.local_problem_monitor == null) {
            this.local_problem_monitor = new Class_MonitorableObject(context);
        }
        return this.local_problem_monitor;
    }

    protected void optionallyAttachProblemsToInstance(CallContext context, Object instance) {
        if (this.local_problem_monitor != null) {
            if ((instance instanceof ProblemMonitor) == false) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, "The instance with id ''%(oid)' is referenced as a problem monitor, but not of type MonitorableObject (at '%(info)')", "info", this.source_location_info);
                throw (ExceptionConfigurationError) null; // compiler insists
            }
            this.instance_problem_monitor = ((ProblemMonitor) instance);
            for (ProblemStatus ps : this.local_problem_monitor.getProblemStatusDetails(context)) {
                this.instance_problem_monitor.addProblemStatus(context, ps);
            }
            this.local_problem_monitor = null;
        } else if (instance instanceof ProblemMonitor) {
            this.instance_problem_monitor = ((ProblemMonitor) instance);
        }
    }        
}
