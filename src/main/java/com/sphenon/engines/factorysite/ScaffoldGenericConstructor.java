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
import com.sphenon.basics.exception.*;
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

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.factories.Factory_Aggregate;
import com.sphenon.engines.factorysite.tplinst.*;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Vector;

public class ScaffoldGenericConstructor
  extends ScaffoldGeneric_BaseImpl
{
    static final public Class _class = ScaffoldGenericConstructor.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    static protected long runtimestep_level;
    static public    long adjustRuntimeStepLevel(long new_level) { long old_level = runtimestep_level; runtimestep_level = new_level; return old_level; }
    static public    long getRuntimeStepLevel() { return runtimestep_level; }
    static { runtimestep_level = RuntimeStepLocationContext.getLevel(_class); };

    private Vector_ScaffoldParameter_long_ parameters;

    private boolean is_singleton;
    private boolean have_dynamic_parameters;
    private Object singleton = null;
    private Object cached_result;
    private String cached_result_var;
    private boolean already_visited;

    private Type type;
    private Constructor constructor;
    private Class[] parameter_classes;
    private Type[] parameter_types;
    private boolean context_par;
    private int number_of_parameters;
    private Vector_ParEntry_long_ parameters_to_be_set;

    protected FactorySiteListener listener;
    protected String oid;

    public ScaffoldGenericConstructor (CallContext context, Type type, Constructor constructor, Class[] parameter_classes, Type[] parameter_types, boolean context_par, int number_of_parameters, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) {
        super(context, factory_site, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, problem_monitor_oid);

        this.parameters = parameters;

        this.type = type;
        this.constructor = constructor;
        this.parameter_classes = parameter_classes;
        this.parameter_types = parameter_types;
        this.context_par = context_par;
        this.number_of_parameters = number_of_parameters;
        this.listener = listener;
        this.is_singleton = is_singleton;
        this.have_dynamic_parameters = have_dynamic_parameters;
        this.oid = oid;
        this.parameters_to_be_set = match_result.parameters_to_be_set;

        this.already_visited = false;
        this.cached_result = null;

        this.source_location_info = source_location_info;
        
        if (Factory_Aggregate.debug_classloader) {
            Factory_Aggregate.debugClassLoader("ScaffoldGenericConcstructor.constructor.declaring", constructor.getDeclaringClass());
            Factory_Aggregate.debugClassLoader("ScaffoldGenericConcstructor.constructor", constructor.getClass());
            Factory_Aggregate.debugClassLoader("ScaffoldGenericConcstructor.CTOR", this.getClass());
        }

        if (pass != 1) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Scaffold based on constructor can only be executed in first pass, specified pass is '%(pass)'", "pass", pass);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    public String getOID(CallContext context) {
        return this.oid;
    }

    public void reset(CallContext context) {
        super.reset(context);
        this.cached_result = null;
        this.cached_result_var = null;
        this.already_visited = false;
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
        return 1;
    }

    public void completeProcessing(CallContext context) throws DataSourceUnavailable {
    }

    public Object getValueAsObject (CallContext call_context) throws DataSourceUnavailable, IgnoreErraneousDataSource {
        if (this.skip) { return null; }

        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        RuntimeStep runtime_step = null;

        try {
            if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { runtime_step = RuntimeStep.create(context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Building subaggregate at '%(info)' using constructor", "info", source_location_info); }

            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericConstructor for '%(type)', getValueAsObject (%(info))...", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context)); }

            if (this.cached_result != null) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericConstructor for '%(type)', getValueAsObject (%(info)) - done, result (cached): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", this.cached_result); }
                if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                this.optionallyAttachProblemsToInstance(context, this.cached_result);
                return this.cached_result;
            }
            if (this.already_visited) {
                DataSourceUnavailable.createAndThrow(context, FactorySiteStringPool.get(context, "0.7.4" /* Object of type '%(type)' cannot be delivered, object aggregate contains a cyclic reference and there is no factory available (which could provide a precreate method) */), "type", this.type.getName(context));
                throw (DataSourceUnavailable) null; // compiler insists
            } else {
                if (this.is_singleton && this.singleton != null) {
                    this.already_visited = true;
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericConstructor for '%(type)', getValueAsObject (%(info)) - done, result (singelton): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", this.singleton); }
                    if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                    this.optionallyAttachProblemsToInstance(context, this.singleton);
                    return this.singleton;
                }
                if (listener != null) listener.notifyBeforeCreation(context, this);
                this.already_visited = true;
                Object[] actual_parameters = new Object[number_of_parameters];
                if (this.context_par) {
                    actual_parameters[0] = context;
                }
                for (int i=0; i<parameters.getSize(context); i++) {
                    ParEntry pe = parameters_to_be_set.tryGet(context, i);
                    
                    Object set_value;
                    ScaffoldParameter        sp  = parameters.tryGet(context, i);
                    DynamicScaffoldParameter dsp = (sp instanceof DynamicScaffoldParameter ? (DynamicScaffoldParameter) sp : null);
                    
                    Scope local_scope = this.pushScope(context, dsp, false);
                            
                    if (getIf(context, dsp) == false) {
                        if (sp.getValue(context) instanceof Scaffold) {
                            ((Scaffold)(sp.getValue(context))).skip(context);
                        }
                        set_value = null;
                    } else {
                        set_value = sp.getValue(context).getValueAsObject(context);
                    }

                    this.popScope(context);
                    
                    actual_parameters[i+(this.context_par?1:0)] = set_value;
                }
                try {
                    
                    this.performPreBuildActions(context);
                    Object result = constructor.newInstance(actual_parameters);
                    
                    this.performPostBuildActions(context, result);
                    
                    if (this.is_singleton) { this.singleton = result; }
                    this.cached_result = result;
                    if (listener != null) listener.notifyAfterCreation(context, this, result);
                    
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericConstructor for '%(type)', getValueAsObject (%(info)) - done, result: '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", result); }
                    
                    if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                    this.optionallyAttachProblemsToInstance(context, result);
                    return result;
                } catch (InstantiationException e) {
                    DataSourceUnavailable.createAndThrow(context, e, FactorySiteStringPool.get(context, "0.7.5" /* Object of type '%(type)' cannot be delivered, class is abstract */), "type", this.type.getName(context), "info", this.getSourceLocationInfo(context));
                    throw (DataSourceUnavailable) null; // compiler insists
                } catch (IllegalAccessException e) {
                    DataSourceUnavailable.createAndThrow(context, e, FactorySiteStringPool.get(context, "0.7.6" /* Object of type '%(type)' cannot be delivered, constructor is inaccessible */), "type", this.type.getName(context), "info", this.getSourceLocationInfo(context));
                    throw (DataSourceUnavailable) null; // compiler insists
                } catch (IllegalArgumentException e) {
                    DataSourceUnavailable.createAndThrow(context, e, FactorySiteStringPool.get(context, "0.7.7" /* Object of type '%(type)' cannot be delivered, signature mismatch or unwrapping or method invocation */), "type", this.type.getName(context), "info", this.getSourceLocationInfo(context));
                    throw (DataSourceUnavailable) null; // compiler insists
                } catch (InvocationTargetException e) {
                    DataSourceUnavailable.createAndThrow(context, e.getTargetException(), FactorySiteStringPool.get(context, "0.7.8" /* Object of type '%(type)' cannot be delivered, constructor throwed an exception */), "type", this.type.getName(context), "info", this.getSourceLocationInfo(context));
                    throw (DataSourceUnavailable) null; // compiler insists
                } catch (Throwable e) {
                    DataSourceUnavailable.createAndThrow(context, e, "Object of type '%(type)' cannot be delivered, constructor throwed an exception (%(info))", "type", this.type.getName(context), "info", this.getSourceLocationInfo(context));
                    throw (DataSourceUnavailable) null; // compiler insists
                }
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

    protected String optPrimConv (CallContext context, Type t, Class c) {
        if (t.equals(TypeManager.get(context, Boolean.class)) && c.getName().equals("boolean")) {
            return ".booleanValue()";
        }
        return "";
    }

    protected String source_location_info;

    public String getSourceLocationInfo (CallContext context) {
        return this.source_location_info;
    }

    public void setSourceLocationInfo (CallContext context, String source_location_info) {
        this.source_location_info = source_location_info;
    }
}
