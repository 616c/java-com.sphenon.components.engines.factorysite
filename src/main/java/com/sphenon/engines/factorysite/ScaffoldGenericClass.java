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
import com.sphenon.basics.system.*;
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

public class ScaffoldGenericClass
  extends ScaffoldGeneric_BaseImpl
{
    static final public Class _class = ScaffoldGenericClass.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    static protected long runtimestep_level;
    static public    long adjustRuntimeStepLevel(long new_level) { long old_level = runtimestep_level; runtimestep_level = new_level; return old_level; }
    static public    long getRuntimeStepLevel() { return runtimestep_level; }
    static { runtimestep_level = RuntimeStepLocationContext.getLevel(_class); };

    private Vector_ScaffoldParameter_long_ parameters;

    private Class classclass;
    private boolean allow_dynamic_type_check;
    private Method set_parameters_at_once;
    private Method new_instance_method;
    private java.util.Hashtable par_entries;
    private Vector_ParEntry_long_ parameters_to_be_set;
    private Vector_ParEntry_long_ parameters_to_be_defaulted;
    private Constructor constructor;
    private boolean cons_context_par;
    private Method initialise_method;

    private boolean is_singleton;
    private boolean have_dynamic_parameters;
    private Object singleton = null;
    private Object cached_result;
    private String cached_result_var;
    private String cached_class_var;
    private boolean already_visited;
    private boolean creation_done;

    private Object classinstance;

    protected FactorySiteListener listener;
    protected String oid;
    protected int pass;

    public ScaffoldGenericClass (CallContext call_context, Type type, Class classclass, boolean allow_dynamic_type_check, Method set_parameters_at_once, Type component_type, java.util.Hashtable par_entries, Method new_instance_method, Constructor constructor, boolean cons_context_par, Method initialise_method, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidClass {
        super(call_context, factory_site, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, problem_monitor_oid);

        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        this.parameters = parameters; // Factory_Vector_ScaffoldParameter_long_ .construct(context);

        this.type = type;
        this.classclass = classclass;
        this.allow_dynamic_type_check = allow_dynamic_type_check;
        this.set_parameters_at_once = set_parameters_at_once;
        this.component_type = component_type;
        this.par_entries = par_entries;
        this.new_instance_method = new_instance_method;
        this.constructor = constructor;
        this.cons_context_par = cons_context_par;
        this.initialise_method = initialise_method;
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

    public void reset(CallContext context) {
        super.reset(context);
        this.cached_result = null;
        this.cached_result_var = null;
        this.cached_class_var = null;
        this.already_visited = false;
        this.classinstance = null;
        this.creation_done = false;
    }

    public boolean hasVariableSignature (CallContext context) {
        return (this.set_parameters_at_once != null);
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

    protected Object getClassInstance(CallContext context) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (this.classinstance == null) {
            if (this.new_instance_method != null) {
                this.classinstance = ReflectionUtilities.invoke(context, new_instance_method, null, context);
            } else {
                Object[] parameters = new Object[this.cons_context_par ? 1 : 0];
                if (this.cons_context_par) {
                    parameters[0] = context;
                }
                this.classinstance = this.constructor.newInstance(parameters);
            }
        }
        return this.classinstance;
    }

    public Object getValueAsObject (CallContext call_context) throws DataSourceUnavailable, IgnoreErraneousDataSource {
        if (this.skip) { return null; }

        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        RuntimeStep runtime_step = null;

        boolean first_invocation = true;

        try {
            if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { runtime_step = RuntimeStep.create(context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Building subaggregate at '%(info)' using factory '%(factory)'", "info", source_location_info, "factory", classclass.getName()); }

            try {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericClass for '%(type)', getValueAsObject (%(info)), cache '%(cache)', visited '%(visited)', created '%(created)', current pass '%(currentpass)', scaffold pass '%(scaffoldpass)'...", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "cache", this.cached_result, "visited", this.already_visited, "created", this.creation_done, "currentpass", this.factory_site.getCurrentPass(context), "scaffoldpass", this.pass, "info", this.getSourceLocationInfo(context)); }

                boolean just_precreate = (this.pass > this.factory_site.getCurrentPass(context));

                if (this.cached_result != null && (this.already_visited || this.creation_done || just_precreate)) {
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericClass for '%(type)', getValueAsObject (%(info)) - done, result (cached): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", this.cached_result); }
                    if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                    this.optionallyAttachProblemsToInstance(context, this.cached_result);
                    return this.cached_result;
                }
                if (this.already_visited || just_precreate) {
                    if (this.already_visited) { first_invocation = false; }
                    this.cached_result = this.getClassInstance(context);

                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericClass for '%(type)', getValueAsObject (%(info)) - done, result (precreated): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", this.cached_result); }

                    if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                    this.optionallyAttachProblemsToInstance(context, this.cached_result);
                    return this.cached_result;
                } else {
                    if (this.is_singleton && this.singleton != null) {
                        this.already_visited = true;
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericClass for '%(type)', getValueAsObject (%(info)) - done, result (singleton): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", this.singleton); }
                        if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                        this.optionallyAttachProblemsToInstance(context, this.singleton);
                        return this.singleton;
                    }
                    if (listener != null) listener.notifyBeforeCreation(context, this);
                    this.already_visited = true;
                    this.cached_result = this.getClassInstance(context);
                    if (this.set_parameters_at_once != null) {
                        Vector_ScaffoldParameter_long_ mypars = parameters;
                        if (have_dynamic_parameters) {
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

                        String[] setnames = new String[names_vector.size()];
                        int ni = 0;
                        for (String name : names_vector) {
                            setnames[ni++] = name;
                        }
                        Object   setvalues = Array.newInstance(TypeManager.getJavaClass(context, this.component_type), values_vector.size());
                        int vi = 0;
                        for (Object value : values_vector) {
                            Array.set(setvalues, vi++, value);
                        }

                        Object[] setparspar = new Object[3];
                        setparspar[0] = context;
                        setparspar[1] = setnames;
                        setparspar[2] = setvalues;
                        try {
                            set_parameters_at_once.invoke(classinstance, setparspar);
                        } catch (Throwable t) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, t, "Could not set parameters at once, class '%(class)', names '%(names)'", "class", classinstance.getClass().getName(), "names", StringUtilities.join(context, setnames, ","));
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                    } else {
                        if (parameters_to_be_set != null) {
                            for (int i=0; i<parameters_to_be_set.getSize(context); i++) {
                                ParEntry pe = parameters_to_be_set.tryGet(context, i);

                                Object set_value;
                                ScaffoldParameter        sp  = parameters.tryGet(context, i);
                                DynamicScaffoldParameter dsp = (sp instanceof DynamicScaffoldParameter ? (DynamicScaffoldParameter) sp : null);

                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericClass for '%(type)', setting parameter '%(name)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "name", sp.getName(context)); }

                                Scope local_scope = this.pushScope(context, dsp, false);

                                if (getIf(context, dsp) == false) {
                                    if (sp.getValue(context) instanceof Scaffold) {
                                        ((Scaffold)(sp.getValue(context))).skip(context);
                                    }
                                    if (pe.default_method != null) {
                                        try {
                                            if (pe.default_method_has_context) {
                                                set_value = ReflectionUtilities.invoke(context, pe.default_method, classinstance, context);
                                            } else {
                                                set_value = ReflectionUtilities.invoke(context, pe.default_method, classinstance);
                                            }
                                        } catch (Throwable t) {
                                            CustomaryContext.create((Context)context).throwConfigurationError(context, t, "Could not get default value via '%(method)', class '%(class)'", "class", classinstance.getClass().getName(), "method", pe.default_method.getName());
                                            throw (ExceptionConfigurationError) null; // compiler insists
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    set_value = sp.getValue(context).getValueAsObject(context);
                                }

                                this.popScope(context);

                                try {
                                    if (pe.set_method_has_context) {
                                        ReflectionUtilities.invoke(context, pe.set_method, classinstance, context, set_value);
                                    } else {
                                        ReflectionUtilities.invoke(context, pe.set_method, classinstance, set_value);
                                    }
                                } catch (Throwable t) {
                                    CustomaryContext.create((Context)context).throwConfigurationError(context, t, "Could not set value via '%(method)', class '%(class)', value '%(value)'", "class", classinstance.getClass().getName(), "method", pe.set_method.getName(), "value", set_value);
                                    throw (ExceptionConfigurationError) null; // compiler insists
                                }
                            }
                        }
                        if (parameters_to_be_defaulted != null) {
                            for (int i=0; i<parameters_to_be_defaulted.getSize(context); i++) {
                                ParEntry pe = parameters_to_be_defaulted.tryGet(context, i);
                                Object set_value;
                                try {
                                    if (pe.default_method_has_context) {
                                        set_value = ReflectionUtilities.invoke(context, pe.default_method, classinstance, context);
                                    } else {
                                        set_value = ReflectionUtilities.invoke(context, pe.default_method, classinstance);
                                    }
                                } catch (Throwable t) {
                                    CustomaryContext.create((Context)context).throwConfigurationError(context, t, "Could not get default value via '%(method)', class '%(class)'", "class", classinstance.getClass().getName(), "method", pe.default_method.getName());
                                    throw (ExceptionConfigurationError) null; // compiler insists
                                }
                                try {
                                    if (pe.set_method_has_context) {
                                        ReflectionUtilities.invoke(context, pe.set_method, classinstance, context, set_value);
                                    } else {
                                        ReflectionUtilities.invoke(context, pe.set_method, classinstance, set_value);
                                    }
                                } catch (Throwable t) {
                                    CustomaryContext.create((Context)context).throwConfigurationError(context, t, "Could not set value via '%(method)', class '%(class)', value '%(value)'", "class", classinstance.getClass().getName(), "method", pe.set_method.getName(), "value", set_value);
                                    throw (ExceptionConfigurationError) null; // compiler insists
                                }
                            }
                        }
                    }

                    this.performPreBuildActions(context);

                    Object result = classinstance;

                    this.performPostBuildActions(context, result);

                    this.cached_result = result;
                    this.creation_done = true;
                    if (initialise_method != null) {
                        initialise_method.invoke(result, context);
                    }
                    if (listener != null) listener.notifyAfterCreation(context, this, result);
                    if (this.is_singleton) { this.singleton = result; }

                    if (   this.allow_dynamic_type_check
                        && result != null
                        && TypeManager.get(context, result.getClass()).isA(context, this.type) == false
                       ) {
                        DataSourceUnavailable.createAndThrow(context, "Dynamic type check failed in ScaffoldGenericClass for '%(class)', expected '%(expected)', got '%(got)'", "class", this.classclass.getName(), "expected", this.type.getName(context), "got", result.getClass().getName());
                        throw (DataSourceUnavailable) null; // compiler insists
                    }

                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "ScaffoldGenericClass for '%(type)', getValueAsObject (%(info)) - done, result: '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", result); }

                    if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                    this.optionallyAttachProblemsToInstance(context, result);
                    return result;
                }
            } catch (InstantiationException e) {
                DataSourceUnavailable.createAndThrow(context, e, FactorySiteStringPool.get(context, "0.11.1" /* Object of type '%(type)' cannot be delivered, class invocation failed%(cycle), abstract class */), "type", this.type.getName(context), "cycle", (first_invocation ? "" : " (in cycle)"));
                throw (DataSourceUnavailable) null; // compiler insists
            } catch (IllegalAccessException e) {
                DataSourceUnavailable.createAndThrow(context, e, FactorySiteStringPool.get(context, "0.11.2" /* Object of type '%(type)' cannot be delivered, class invocation failed %(cycle), constructor is inaccessible */), "type", this.type.getName(context), "cycle", (first_invocation ? "" : " (in cycle)"));
                throw (DataSourceUnavailable) null; // compiler insists
            } catch (IllegalArgumentException e) {
                DataSourceUnavailable.createAndThrow(context, e, FactorySiteStringPool.get(context, "0.11.3" /* Object of type '%(type)' cannot be delivered, class invocation failed %(cycle), signature mismatch or unwrapping or method invocation */), "type", this.type.getName(context), "cycle", (first_invocation ? "" : " (in cycle)"));
                throw (DataSourceUnavailable) null; // compiler insists
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof java.lang.RuntimeException) {
                    throw (java.lang.RuntimeException) e.getTargetException();
                }
                if (e.getTargetException() instanceof java.lang.Error) {
                    throw (java.lang.Error) e.getTargetException();
                }
                DataSourceUnavailable.createAndThrow(context, e.getTargetException(), FactorySiteStringPool.get(context, "0.11.6" /* Object of type '%(type)' cannot be delivered, class invocation failed%(cycle), constructor throwed an exception */), "type", this.type.getName(context), "cycle", (first_invocation ? "" : " (in cycle)"));
                throw (DataSourceUnavailable) null; // compiler insists
            } catch (Throwable e) {
                DataSourceUnavailable.createAndThrow(context, e, "Object of type '%(type)' cannot be delivered, throwed an exception", "type", this.type.getName(context));
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
}
