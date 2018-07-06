package com.sphenon.engines.factorysite;

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

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
import com.sphenon.basics.tracking.*;
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
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Vector;

public class ScaffoldGenericFactory_Aggregate
  extends ScaffoldGeneric_BaseImpl
  implements OriginAware
{
    static final public Class _class = ScaffoldGenericFactory_Aggregate.class;

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
    private TypeImpl_Aggregate aggregate_type;
    private boolean allow_dynamic_type_check;
    private java.util.Hashtable par_entries;
    private Vector_ParEntry_long_ parameters_to_be_set;
    private Vector_ParEntry_long_ parameters_to_be_defaulted;

    private boolean is_singleton;
    private boolean have_dynamic_parameters;
    private Object singleton = null;
    private Object cached_result;
    private String cached_result_var;
    private String cached_factory_var;
    private int already_visited_count;
    protected java.util.Hashtable current_parameters;
    private boolean creation_done;

    protected FactorySiteListener listener;
    protected String oid;
    protected int pass;

    protected Factory_Aggregate factory_aggregate;

    public ScaffoldGenericFactory_Aggregate (CallContext call_context, Type type, TypeImpl_Aggregate aggregate_type, boolean allow_dynamic_type_check, java.util.Hashtable par_entries, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidFactory {
        super(call_context, factory_site, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, problem_monitor_oid);

        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        this.parameters = parameters; // Factory_Vector_ScaffoldParameter_long_ .construct(context);

        this.type = type;
        this.aggregate_type = aggregate_type;
        this.allow_dynamic_type_check = allow_dynamic_type_check;
        this.par_entries = par_entries;
        this.parameters_to_be_set = match_result.parameters_to_be_set;
        this.parameters_to_be_defaulted = match_result.parameters_to_be_defaulted;
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

    public Origin getOrigin(CallContext context) {
        try {
            this.getFactoryInstance(context);
        } catch (DataSourceUnavailable dsu) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, dsu, "Exception unexpected here");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        return this.factory_aggregate.getOrigin(context);
    }

    public void reset(CallContext context) {
        super.reset(context);
        this.cached_result = null;
        this.cached_result_var = null;
        this.cached_factory_var = null;
        this.already_visited_count = 0;
        this.factory_aggregate = null;
        this.current_parameters = null;
        this.creation_done = false;
    }

    public boolean hasVariableSignature (CallContext context) {
        return false;
    }

    public Type getComponentType (CallContext context) {
        return null;
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

    public void completeProcessing(CallContext context) throws DataSourceUnavailable {
    }

    protected Object getFactoryInstance(CallContext context) throws DataSourceUnavailable {
        if (this.factory_aggregate == null) {
            if (listener != null) listener.notifyBeforeCreation(context, this);
            this.factory_aggregate = new Factory_Aggregate(context);
            this.factory_aggregate.setAggregateClass(context, this.aggregate_type.getName(context));
            try {
                this.factory_aggregate.validateAggregateClass(context);
            } catch (ValidationFailure vf) {
                DataSourceUnavailable.createAndThrow(context, vf, "Invalid aggregate factory for aggregate type '%(aggregate_type)'", "aggregate_type", aggregate_type.getName(context));
                throw (DataSourceUnavailable) null;
            }
            this.current_parameters = new java.util.Hashtable();
        }
        return this.factory_aggregate;
    }

    final static protected int GRMPF = 1; /* 2 */
    // what's this? this is the ">=GRMPF" business, see below
    // was implemented to allow precreating with diving in two times
    // since precreate method can be anywhere on cycle
    // BUT: the implementation was creepy and buggy, therefore it
    // was disabled again
    // maybe the whole approach isn't really doable (- maybe it is)
    // anyway: since aggregates now themselves are precreatable
    // (if their top datasource supports it), this may be unnecessary anyway

    public Object getValueAsObject (CallContext call_context) throws DataSourceUnavailable, IgnoreErraneousDataSource {
        if (this.skip) { return null; }

        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        RuntimeStep runtime_step = null;

        try {
            if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { runtime_step = RuntimeStep.create(context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Building subaggregate at '%(info)' using aggregate factory for '%(type)'", "info", source_location_info, "type", this.aggregate_type.getName(context)); }

            boolean first_invocation = true;

            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericFactory_Aggregate for '%(type)', getValueAsObject (%(info)), cache '%(cache)', visited '%(visited)', created '%(created)', current pass '%(currentpass)', scaffold pass '%(scaffoldpass)'...", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "cache", this.cached_result, "visited", this.already_visited_count, "created", this.creation_done, "currentpass", this.factory_site.getCurrentPass(context), "scaffoldpass", this.pass, "info", this.getSourceLocationInfo(context)); }

            boolean just_precreate = (this.pass > this.factory_site.getCurrentPass(context));

            if (this.cached_result != null && (this.already_visited_count >= GRMPF || this.creation_done || just_precreate)) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericFactory_Aggregate for '%(type)', getValueAsObject (%(info)) - done, result (cached): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", this.cached_result); }
                if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                this.optionallyAttachProblemsToInstance(context, this.cached_result);
                return this.cached_result;
            }

            // ooohh yeah
            // this ">=GRMPF" business is quite dangerous
            // and should be RETHOUGHT
            // (isn't the instance created two times below there??)
            
            if (this.already_visited_count >= GRMPF || just_precreate) {
                // why "count >= GRMPF" ?
                // because we give the cycle a second try, since the
                // precreate method may be implemented *somewhere*
                // in the cycle, not necessarily here
                
                if (this.already_visited_count >= 1) { first_invocation = false; }
                
                this.getFactoryInstance(context);
                
                Object result;
                try {
                    result = this.factory_aggregate.getFactorySite(context).prebuild(context);
                } catch (BuildFailure e) {
                    cc.throwConfigurationError(context, e, "Could not build object aggregate of type ('%(aggregatetype)')", "aggregatetype", this.aggregate_type);
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
                
                this.cached_result = result;
                
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericFactory_Aggregate for '%(type)', getValueAsObject (%(info)) - done, result (precreated): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", result); }
                
                if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                this.optionallyAttachProblemsToInstance(context, result);
                return result;
            } else {
                if (this.is_singleton && this.singleton != null) {
                    this.already_visited_count = GRMPF;
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericFactory_Aggregate for '%(type)', getValueAsObject (%(info)) - done, result (singleton): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", this.singleton); }
                    if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                    this.optionallyAttachProblemsToInstance(context, this.singleton);
                    return this.singleton;
                }
                this.already_visited_count++;
                this.getFactoryInstance(context);
                if (parameters_to_be_set != null) {
                    for (int i=0; i<parameters_to_be_set.getSize(context); i++) {
                        ParEntry pe = parameters_to_be_set.tryGet(context, i);
                        
                        Object set_value;
                        ScaffoldParameter        sp  = parameters.tryGet(context, i);
                        DynamicScaffoldParameter dsp = (sp instanceof DynamicScaffoldParameter ? (DynamicScaffoldParameter) sp : null);
                        
                        Scope previous_scope = null;
                        Class_Scope local_scope = null;
                        if (    dsp != null
                             && dsp.getVariableDefinitionExpression(context) != null
                           ) {
                            previous_scope = this.factory_site.getCurrentScope(context);
                            local_scope = new Class_Scope(context, null, previous_scope);
                            this.factory_site.setCurrentScopeOverride(context, local_scope);
                            
                            if (dsp.getVariableDefinitionExpression(context) != null) {
                                int index = 0;
                                for (String name : dsp.getVariableName(context)) {
                                    Object object = null;
                                    try {
                                        object = dsp.getVariableDefinitionExpression(context)[index].evaluate(context, previous_scope);
                                    } catch (EvaluationFailure ef) {
                                        CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of expression '%(expression)' failed", "expression", dsp.getVariableDefinitionExpression(context)[index].getExpression(context));
                                        throw (ExceptionPreConditionViolation) null; // compiler insists
                                    }
                                    local_scope.set(context, name, object);
                                    index++;
                                }
                            }
                        }
                        if (    dsp != null
                             && dsp.getIfExpression(context) != null
                           ) {
                            Object ifo = null;
                            try {
                                ifo = dsp.getIfExpression(context).evaluate(context, this.factory_site.getCurrentScope(context));
                            } catch (EvaluationFailure ef) {
                                CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of expression '%(expression)' failed", "expression", dsp.getIfExpression(context).getExpression(context));
                                throw (ExceptionPreConditionViolation) null; // compiler insists
                            }
                            Boolean ifb;
                            try {
                                ifb = (Boolean) ifo;
                            } catch (ClassCastException cce) {
                                CustomaryContext.create((Context)context).throwConfigurationError(context, cce, "If expression in OCP does not evaluate to a Boolean instance, but to a '%(got)'", "got", ifo.getClass());
                                throw (ExceptionConfigurationError) null; // compiler insists
                            }
                            if (ifb == false) {
                                if (sp.getValue(context) instanceof Scaffold) {
                                    ((Scaffold)(sp.getValue(context))).skip(context);
                                }
                                continue;
                            } else {
                                set_value = sp.getValue(context).getValueAsObject(context);
                            }
                        } else {
                            set_value = sp.getValue(context).getValueAsObject(context);
                        }
                        if (previous_scope != null) {
                            this.factory_site.setCurrentScopeOverride(context, previous_scope);
                        }
                        
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericFactory_Aggregate for '%(type)', setting parameter '%(name)' to value '%(value)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "name", pe.name, "value", set_value); }
                        
                        this.current_parameters.put(pe.name, set_value == null ? new FactorySiteNullParameter(context, pe.type) : set_value);
                    }
                }

                Object result = null;
                // note: we may get here twice (second invoction cycle loop)
                // if (this.cached_result == null) {
                this.factory_aggregate.setParameters(context, this.current_parameters);
                try {
                    this.factory_aggregate.validateParameters(context);
                } catch (ValidationFailure vf) {
                    DataSourceUnavailable.createAndThrow(context, vf, "Invalid parameters for aggregate factory for aggregate type '%(aggregate_type)'", "aggregate_type", aggregate_type.getName(context));
                    throw (DataSourceUnavailable) null;
                }
                
                this.performPreBuildActions(context);
                
                if (Factory_Aggregate.debug_classloader) {
                    Factory_Aggregate.debugClassLoader("ScaffoldGenericFactory_Aggregate", this.getClass());
                    Factory_Aggregate.debugClassLoader("ScaffoldGenericFactory_Aggregate.factory_aggregate", this.factory_aggregate.getClass());
                }
                result = this.factory_aggregate.create(context);
                
                this.performPostBuildActions(context, result);
                
                if (this.is_singleton) { this.singleton = result; }
                this.cached_result = result;
                
                this.creation_done = true;
                
                if (listener != null) listener.notifyAfterCreation(context, this, result);
                
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericFactory_Aggregate for '%(type)', getValueAsObject (%(info)) - done, result: '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", result); }
                
                if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                this.optionallyAttachProblemsToInstance(context, result);
                return result;
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

    protected String source_location_info;

    public String getSourceLocationInfo (CallContext context) {
        return this.source_location_info;
    }

    public void setSourceLocationInfo (CallContext context, String source_location_info) {
        this.source_location_info = source_location_info;
    }
}
