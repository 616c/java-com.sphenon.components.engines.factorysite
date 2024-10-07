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
import com.sphenon.basics.debug.*;
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

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Vector;

public class ScaffoldGenericArray
  extends ScaffoldGeneric_BaseImpl
{
    static final public Class _class = ScaffoldGenericArray.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    static protected long runtimestep_level;
    static public    long adjustRuntimeStepLevel(long new_level) { long old_level = runtimestep_level; runtimestep_level = new_level; return old_level; }
    static public    long getRuntimeStepLevel() { return runtimestep_level; }
    static { runtimestep_level = RuntimeStepLocationContext.getLevel(_class); };

    private Vector_ScaffoldParameter_long_ parameters;

    private Type type;
    private Type component_type;

    private boolean is_singleton;
    private boolean have_dynamic_parameters;
    private Object singleton = null;
    private Object cached_result;
    private Object precreated_result;
    private String cached_result_var;
    private String cached_factory_var;
    private boolean already_visited;
    private boolean creation_done;

    private Object factoryinstance;

    protected FactorySiteListener listener;
    protected String oid;
    protected int pass;

    public ScaffoldGenericArray (CallContext call_context, Type type, Type component_type, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidFactory {
        super(call_context, factory_site, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, problem_monitor_oid);

        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        this.parameters = parameters; // Factory_Vector_ScaffoldParameter_long_ .construct(context);

        this.type = type;
        this.component_type = component_type;
        this.listener = listener;
        this.is_singleton = is_singleton;
        this.have_dynamic_parameters = have_dynamic_parameters;
        this.oid = oid;
        this.pass = pass;

        this.source_location_info = source_location_info;

        this.reset(context);
    }

    public String getOID(CallContext context) {
        return this.oid;
    }

    public void reset(CallContext context) {
        super.reset(context);
        this.precreated_result = null;
        this.cached_result = null;
        this.cached_result_var = null;
        this.cached_factory_var = null;
        this.already_visited = false;
        this.creation_done = false;
    }

    public boolean hasVariableSignature (CallContext context) {
        return true;
    }

    public Type getComponentType (CallContext context) {
        return this.component_type;
    }

    public Vector_ScaffoldParameter_long_ getParameters (CallContext context) {
        return this.parameters;
    }

    public Type getType (CallContext context) {
        return this.type;
    }

    public int getPass(CallContext context) {
        return this.pass;
    }

    public Object getValueAsObject (CallContext call_context) throws DataSourceUnavailable, IgnoreErraneousDataSource {
        if (this.skip) { return null; }

        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        RuntimeStep runtime_step = null;

        boolean first_invocation = true;

        try {
            if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { runtime_step = RuntimeStep.create(context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Building subaggregate at '%(info)' using array constructor", "info", source_location_info); }

            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericArray for '%(type)', getValueAsObject (%(info)), cache '%(cache)', visited '%(visited)', created '%(created)', current pass '%(currentpass)', scaffold pass '%(scaffoldpass)'...", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "cache", this.cached_result, "visited", this.already_visited, "created", this.creation_done, "currentpass", this.factory_site.getCurrentPass(context), "scaffoldpass", this.pass, "info", this.getSourceLocationInfo(context)); }

            boolean just_precreate = (this.pass > this.factory_site.getCurrentPass(context));
            
            if (this.cached_result != null && (this.already_visited || this.creation_done || just_precreate)) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericArray for '%(type)', getValueAsObject (%(info)) - done, result (cached): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", this.cached_result); }
                if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }
                
                this.optionallyAttachProblemsToInstance(context, this.cached_result);
                return this.cached_result;
            }
            try {
                if (this.already_visited || just_precreate) {
                    if (this.already_visited) { first_invocation = false; }
                    if (this.have_dynamic_parameters) {
                        DataSourceUnavailable.createAndThrow(context, "Object of type '%(type)' cannot be delivered, object aggregate contains a cyclic reference and factory for array cannot be created if dynamic parameters are in use (size not known in advance)", "type", this.type.getName(context));
                        throw (DataSourceUnavailable) null; // compiler insists
                    }
                    for (int i=0; i<parameters.getSize(context); i++) {
                        ScaffoldParameter        sp  = parameters.tryGet(context, i);
                        DynamicScaffoldParameter dsp = (sp instanceof DynamicScaffoldParameter ? (DynamicScaffoldParameter) sp : null);
                        if (    dsp != null
                             && dsp.getIfExpression(context) != null
                           ) {
                            DataSourceUnavailable.createAndThrow(context, "Object of type '%(type)' cannot be delivered, object aggregate contains a cyclic reference and factory for array cannot be created if any of the parameters is conditional (i.e. 'IF=' attribute); (size not known in advance)", "type", this.type.getName(context));
                            throw (DataSourceUnavailable) null; // compiler insists
                        }
                    }
                    Object result = java.lang.reflect.Array.newInstance(TypeManager.getJavaClass(context, this.component_type), (int) parameters.getSize(context));
                    this.precreated_result = result;
                    this.cached_result = result;
                    
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericArray for '%(type)', getValueAsObject (%(info)) - done, result (precreated): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", result); }
                    
                    if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                    this.optionallyAttachProblemsToInstance(context, result);
                    return result;
                } else {
                    if (this.is_singleton && this.singleton != null) {
                        this.already_visited = true;
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericArray for '%(type)', getValueAsObject (%(info)) - done, result (singleton): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", this.singleton); }
                        if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                        this.optionallyAttachProblemsToInstance(context, this.singleton);
                        return this.singleton;
                    }
                    if (listener != null) listener.notifyBeforeCreation(context, this);
                    this.already_visited = true;
                    
                    Vector_ScaffoldParameter_long_ mypars = parameters;
                    if (this.have_dynamic_parameters) {
                        Object o = null;
                        try {
                            o = parameters.tryGet(context, 0).getValue(context).getValueAsObject(context);
                            mypars = (Vector_ScaffoldParameter_long_) o;
                        } catch (ClassCastException cce) {
                            cc.throwPreConditionViolation(context, cce, "Object of type '%(type)' cannot be delivered, dynamic parameters in use, but parameter is not a 'Vector_ScaffoldParameter_long_', as expected, but a '%(got)'", "type", this.type.getName(context), "got", o.getClass().getName());
                            throw (ExceptionPreConditionViolation) null; // compiler insists
                        }
                    }
                    Vector<String> names_vector  = new Vector<String>();
                    Vector<Object> values_vector = new Vector<Object>();
                    
                    for (int i=0; i<mypars.getSize(context); i++) {
                        ScaffoldParameter        sp  = mypars.tryGet(context, i);
                        DynamicScaffoldParameter dsp = (sp instanceof DynamicScaffoldParameter ? (DynamicScaffoldParameter) sp : null);
                        
                        Scope local_scope = this.pushScope(context, dsp, true);

                        Iterable it = getForeach(context, dsp, local_scope);
                        if (it != null) {
                            for (Object index_object : it) {
                                updateScope(context, dsp, local_scope, index_object);
                                if (getIf(context, dsp) == false) {
                                    continue;
                                }
                                names_vector.add(dsp == null || dsp.getNameTemplate(context) == null ? sp.getName(context) : dsp.getNameTemplate(context).get(context, this.factory_site.getCurrentScope(context)));
                                DataSource my_ds = sp.getValue(context);
                                FactorySiteTextBased.resetSubTree(context, my_ds);
                                Object o = my_ds.getValueAsObject(context);
                                this.checkComponentType(context, o, i);
                                values_vector.add(o);
                            }
                            
                            this.factory_site.setCurrentScopeOverride(context, previous_scope);
                            
                        } else {
                            if (getIf(context, dsp) == false) {
                                if (sp.getValue(context) instanceof Scaffold) {
                                    ((Scaffold)(sp.getValue(context))).skip(context);
                                }
                                continue;
                            }
                            names_vector.add(dsp == null || dsp.getNameTemplate(context) == null ? sp.getName(context) : dsp.getNameTemplate(context).get(context, this.factory_site.getCurrentScope(context)));
                            Object o = sp.getValue(context).getValueAsObject(context);
                            this.checkComponentType(context, o, i);
                            values_vector.add(o);
                        }

                        this.popScope(context);
                            
                    }

                    this.performPreBuildActions(context);

                    Object result = this.precreated_result != null ? this.precreated_result : java.lang.reflect.Array.newInstance(TypeManager.getJavaClass(context, this.component_type), (int) values_vector.size());
                    this.precreated_result = null;
                    
                    int vi = 0;
                    for (Object value : values_vector) {
                        Array.set(result, vi++, value);
                    }

                    this.performPostBuildActions(context, result);
                    
                    if (this.is_singleton) { this.singleton = result; }
                    this.cached_result = result;
                    this.creation_done = true;
                    if (listener != null) listener.notifyAfterCreation(context, this, result);
                    
                    if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                    this.optionallyAttachProblemsToInstance(context, result);
                    return result;
                }
            } catch (IllegalArgumentException e) {
                DataSourceUnavailable.createAndThrow(context, e, FactorySiteStringPool.get(context, "0.8.10" /* Object of type '%(type)' cannot be delivered, factory invocation failed %(cycle), signature mismatch or unwrapping or method invocation */), "type", this.type.getName(context), "cycle", (first_invocation ? "" : " (in cycle)"));
                throw (DataSourceUnavailable) null; // compiler insists
            }
        } catch(DataSourceUnavailable exception) {
            if (runtime_step != null) { runtime_step.setFailed(context, exception, "Aggregate build failed"); runtime_step = null; }
            this.handleProblem(context, exception);
            throw (DataSourceUnavailable) null; // compiler insists
        } catch(ExceptionError exception) {
            if (runtime_step != null) { runtime_step.setFailed(context, exception, "Aggregate build failed"); runtime_step = null; }
            this.handleProblem(context, exception);
            throw (ExceptionError) null; // compiler insists
        }
    }

    public void compile(CallContext call_context, java.io.PrintStream code, String var_prefix, String indent, Vector_String_long_ pars, boolean do_not_append_par) throws DataSourceUnavailable {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        boolean first_invocation = true;
    }
}
