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
import com.sphenon.basics.system.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
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

import java.io.StringWriter;

public class ScaffoldGenericRetriever
    extends ScaffoldBaseRetriever
    implements ScaffoldGenericCOCPEnabled
{
    static final public Class _class = ScaffoldGenericRetriever.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    protected SpecificScaffoldFactory_Retriever scaffold_factory;
    protected Method retrieve_method;
    protected Method preretrieve_method;
    protected Method set_parameters_at_once;
    protected Method set_component_type_method;
    protected Method new_instance_method;
    protected Vector_ParEntry_long_ parameters_to_be_set;
    protected Vector_ParEntry_long_ parameters_to_be_defaulted;
    protected Constructor constructor;
    protected boolean cons_context_par;
    protected java.util.Hashtable par_entries;

    public ScaffoldGenericRetriever (CallContext context, SpecificScaffoldFactory_Retriever scaffold_factory, Type type, Class retrieverclass, boolean allow_dynamic_type_check, Method retrieve_method, Method preretrieve_method, Method set_parameters_at_once, Method set_component_type_method, Type component_type, java.util.Hashtable par_entries, Method new_instance_method, Constructor constructor, boolean cons_context_par, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) {

        super(context, type, retrieverclass, allow_dynamic_type_check, component_type, parameters, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);

        this.scaffold_factory = scaffold_factory;
        this.retrieve_method = retrieve_method;
        this.preretrieve_method = preretrieve_method;
        this.set_parameters_at_once = set_parameters_at_once;
        this.set_component_type_method = set_component_type_method;
        this.new_instance_method = new_instance_method;
        this.constructor = constructor;
        this.cons_context_par = cons_context_par;
        this.parameters_to_be_set = match_result.parameters_to_be_set;
        this.parameters_to_be_defaulted = match_result.parameters_to_be_defaulted;
        this.par_entries = par_entries;
    }

    public boolean hasVariableSignature (CallContext context) {
        return (this.set_parameters_at_once != null);
    }

    protected boolean canPreretrieve(CallContext context) {
        return (this.preretrieve_method == null ? false : true);
    }

    protected Object getRetrieverInstance(CallContext context) {
        if (this.retrieverinstance == null && Modifier.isStatic(this.retrieve_method.getModifiers()) == false) {
            if (this.new_instance_method != null) {
                this.retrieverinstance = ReflectionUtilities.invoke(context, new_instance_method, null, context);
            } else {
                this.retrieverinstance = 
                    this.cons_context_par
                    ? ReflectionUtilities.newInstance(context, this.constructor, context)
                    : ReflectionUtilities.newInstance(context, this.constructor);
            }
            if (this.set_component_type_method != null && this.component_type != null) {
                ReflectionUtilities.invoke(context, this.set_component_type_method, retrieverinstance, context, this.component_type);
            }
        }
        return this.retrieverinstance;
    }

    protected Object preretrieveInstance(CallContext context) {
        Object result = ReflectionUtilities.invoke(context, preretrieve_method, this.getRetrieverInstance(context), context);
        return result;
    }

    protected Object retrieveInstance(CallContext context) {
        Object result = ReflectionUtilities.invoke(context, retrieve_method, retrieverinstance, context);
        return result;
    }

    protected String getScaffoldId(CallContext context) {
        return "Scaffold['" + (this.getType(context) == null ? "(null)" : this.getType(context).getName(context)) + "'/GenericRetriever '" + this.retrieverclass.getName() + "']";
    }

    public void setParameters(CallContext context) throws DataSourceUnavailable {
        if (this.retrieverinstance == null && Modifier.isStatic(this.retrieve_method.getModifiers()) == true) {
            return;
        }
        if (this.set_parameters_at_once != null) {
            Vector_ScaffoldParameter_long_ mypars = parameters;
            if (this.have_dynamic_parameters) {
                Object o = null;
                try {
                    o = parameters.tryGet(context, 0).getValue(context).getValueAsObject(context);
                    mypars = (Vector_ScaffoldParameter_long_) o;
                } catch (ClassCastException cce) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, cce, "Object of type '%(type)' cannot be delivered, dynamic parameters in use, but parameter is not a 'Vector_ScaffoldParameter_long_', as expected, but a '%(got)'", "type", this.type.getName(context), "got", o.getClass().getName());
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                } catch (IgnoreErraneousDataSource ieds) {
                    return;
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
                        DataSource my_ds = sp.getValue(context);
                        FactorySiteTextBased.resetSubTree(context, my_ds);
                        Object o;
                        try {
                            o = my_ds.getValueAsObject(context);
                        } catch (IgnoreErraneousDataSource ieds) {
                            continue;
                        }
                        this.checkComponentType(context, o, i);
                        values_vector.add(o);
                        names_vector.add(dsp == null || dsp.getNameTemplate(context) == null ? sp.getName(context) : dsp.getNameTemplate(context).get(context, this.factory_site.getCurrentScope(context)));
                    }
                } else {
                    if (getIf(context, dsp) == false) {
                        if (sp.getValue(context) instanceof Scaffold) {
                            ((Scaffold)(sp.getValue(context))).skip(context);
                        }
                        this.popScope(context);
                        continue;
                    }
                    Object o;
                    try {
                        o = sp.getValue(context).getValueAsObject(context);
                    } catch (IgnoreErraneousDataSource ieds) {
                        this.popScope(context);
                        continue;
                    }
                    this.checkComponentType(context, o, i);
                    values_vector.add(o);
                    names_vector.add(dsp == null || dsp.getNameTemplate(context) == null ? sp.getName(context) : dsp.getNameTemplate(context).get(context, this.factory_site.getCurrentScope(context)));
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
            
            try {
                ReflectionUtilities.invoke(context, set_parameters_at_once, retrieverinstance, context, setnames, setvalues);
            } catch (Throwable t) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, t, "Could not set parameters at once, retriever '%(retriever)', names '%(names)'", "retriever", retrieverinstance.getClass().getName(), "names", StringUtilities.join(context, setnames, ","));
                throw (ExceptionConfigurationError) null; // compiler insists
            }
        } else {
            if (parameters_to_be_set != null) {
                for (int i=0; i<parameters_to_be_set.getSize(context); i++) {
                    ParEntry pe = parameters_to_be_set.tryGet(context, i);
                    
                    Object set_value;
                    ScaffoldParameter        sp  = parameters.tryGet(context, i);
                    DynamicScaffoldParameter dsp = (sp instanceof DynamicScaffoldParameter ? (DynamicScaffoldParameter) sp : null);
                    Scope local_scope = this.pushScope(context, dsp, false);

                    if (getIf(context, dsp) == false) {
                        if (sp.getValue(context) instanceof Scaffold) {
                            ((Scaffold)(sp.getValue(context))).skip(context);
                        }
                        if (pe.default_method != null) {
                            try {
                                if (pe.default_method_has_context) {
                                    set_value = ReflectionUtilities.invoke(context, pe.default_method, retrieverinstance, context);
                                } else {
                                    set_value = ReflectionUtilities.invoke(context, pe.default_method, retrieverinstance);
                                }
                            } catch (Throwable t) {
                                CustomaryContext.create((Context)context).throwConfigurationError(context, t, "Could not get default value via '%(method)', retriever '%(retriever)'", "retriever", retrieverinstance.getClass().getName(), "method", pe.default_method.getName());
                                throw (ExceptionConfigurationError) null; // compiler insists
                            }
                        } else {
                            this.popScope(context);
                            continue;
                        }
                    } else {
                        try {
                            set_value = sp.getValue(context).getValueAsObject(context);
                        } catch (IgnoreErraneousDataSource ieds) {
                            this.popScope(context);
                            continue;
                        }
                    }

                    this.popScope(context);
                    
                    try {
                        if (pe.set_method_has_context) {
                            ReflectionUtilities.invoke(context, pe.set_method, retrieverinstance, context, set_value);
                        } else {
                            ReflectionUtilities.invoke(context, pe.set_method, retrieverinstance, set_value);
                        }
                    } catch (Throwable t) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, t, "Could not set value via '%(method)', retriever '%(retriever)', value '%(value)'", "retriever", retrieverinstance.getClass().getName(), "method", pe.set_method.getName(), "value", set_value);
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
                            set_value = ReflectionUtilities.invoke(context, pe.default_method, retrieverinstance, context);
                        } else {
                            set_value = ReflectionUtilities.invoke(context, pe.default_method, retrieverinstance);
                        }
                    } catch (Throwable t) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, t, "Could not get default value via '%(method)', retriever '%(retriever)'", "retriever", retrieverinstance.getClass().getName(), "method", pe.default_method.getName());
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                    try {
                        if (pe.set_method_has_context) {
                            ReflectionUtilities.invoke(context, pe.set_method, retrieverinstance, context, set_value);
                        } else {
                            ReflectionUtilities.invoke(context, pe.set_method, retrieverinstance, set_value);
                        }
                    } catch (Throwable t) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, t, "Could not set value via '%(method)', retriever '%(retriever)', value '%(value)'", "retriever", retrieverinstance.getClass().getName(), "method", pe.set_method.getName(), "value", set_value);
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                }
            }
        }
    }

    public void printCOCPCode2(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String scaffold_index) {
        String accessor_name = scaffold_factory.printCOCPCode(context, factory_site);

        dr.add(COCPIndices.COCPItem_Scaffold);
        dr.add(coder.writeText(context, scaffold_index));
        dr.add(COCPIndices.COCPScaffold_Retriever);

        dr.add((type == null ? ((Integer) 0) : coder.writeText(context, type.getId(context))));
        dr.add(coder.writeCode(context, this.retrieverclass.getName() + ".class"));
        dr.add(coder.writeBoolean(context, allow_dynamic_type_check));
        dr.add((component_type == null ? ((Integer) 0) : coder.writeText(context, component_type.getId(context))));

        dr.add((this.parameters == null ? ((Integer) 0) : (int) this.parameters.getSize(context)));
        if (this.parameters != null) {
            for (ScaffoldParameter sp : this.parameters.getIterable_ScaffoldParameter_(context)) {
                sp.printCOCPCode(context, sw, dr, coder);
            }
        }

        if (this.listener == null) {
            dr.add((Integer) 0);
        } else {
            if (this.listener instanceof FactorySiteMultiListener) {
                FactorySiteListener[] listeners = ((FactorySiteMultiListener) this.listener).getListeners(context);
                String code = "new FactorySiteMultiListener(context";
                for (FactorySiteListener fsl : listeners) {
                    code += ", new " + fsl.getClass().getName() + "(context)";
                }
                code += ")";
                dr.add(coder.writeCode(context, code));
            } else {
                String code = "new " + this.listener.getClass().getName() + "(context)";
                dr.add(coder.writeCode(context, code));
            }
        }

        dr.add(coder.writeBoolean(context, this.is_singleton));
        dr.add(coder.writeBoolean(context, this.have_dynamic_parameters));
        dr.add((Integer) 0);
        dr.add(coder.writeText(context, this.oid));
        dr.add(this.pass);

        int prec_size = (this.pre_conditions == null ? 0 : this.pre_conditions.size());
        dr.add(prec_size);
        if (prec_size != 0) {
            for (BuildAssertion pre_condition : this.pre_conditions) {
                dr.add(coder.writeText(context, pre_condition.getExpression(context).getExpression(context)));
                dr.add(coder.writeText(context, new Integer(pre_condition.getPass(context)).toString()));
                dr.add(coder.writeText(context, pre_condition.getSourceCodeLocation(context)));
            }
        }
        int postc_size = (this.post_conditions == null ? 0 : this.post_conditions.size());
        dr.add(postc_size);
        if (postc_size != 0) {
            for (BuildAssertion post_condition : this.post_conditions) {
                dr.add(coder.writeText(context, post_condition.getExpression(context).getExpression(context)));
                dr.add(coder.writeText(context, new Integer(post_condition.getPass(context)).toString()));
                dr.add(coder.writeText(context, post_condition.getSourceCodeLocation(context)));
            }
        }
        int prebs_size = (this.pre_build_scripts == null ? 0 : this.pre_build_scripts.size());
        dr.add(prebs_size);
        if (prebs_size != 0) {
            for (BuildScript pre_build_script : this.pre_build_scripts) {
                dr.add(coder.writeText(context, pre_build_script.getExpression(context).getExpression(context)));
                dr.add(coder.writeText(context, new Integer(pre_build_script.getPass(context)).toString()));
                dr.add(coder.writeText(context, pre_build_script.getSourceCodeLocation(context)));
            }
        }
        int postbs_size = (this.post_build_scripts == null ? 0 : this.post_build_scripts.size());
        dr.add(postbs_size);
        if (postbs_size != 0) {
            for (BuildScript post_build_script : this.post_build_scripts) {
                dr.add(coder.writeText(context, post_build_script.getExpression(context).getExpression(context)));
                dr.add(coder.writeText(context, new Integer(post_build_script.getPass(context)).toString()));
                dr.add(coder.writeText(context, post_build_script.getSourceCodeLocation(context)));
            }
        }

        dr.add(coder.writeText(context, source_location_info));
        dr.add((problem_monitor_oid == null ? ((Integer) 0) : coder.writeText(context, problem_monitor_oid)));
        dr.add(coder.writeBoolean(context, this.set_parameters_at_once != null));
        dr.add(coder.writeBoolean(context, this.preretrieve_method != null));
        dr.add(coder.writeText(context, "Scaffold['" + (this.getType(context) == null ? "(null)" : this.getType(context).getName(context)) + "'/GenericRetriever '" + this.retrieverclass.getName() + "']"));
        dr.add(coder.writeBoolean(context, Modifier.isStatic(this.retrieve_method.getModifiers())));
        dr.add(coder.writeCode(context, accessor_name + ".getSingleton(context)"));

        int ptbs_size = (parameters_to_be_set == null ? 0 : (int) parameters_to_be_set.getSize(context));
        dr.add(ptbs_size);
        for (int i=0; i<ptbs_size; i++) {
            ParEntry pe = parameters_to_be_set.tryGet(context, i);
            dr.add(pe.index);
        }
        int ptbd_size = (parameters_to_be_defaulted == null ? 0 : (int) parameters_to_be_defaulted.getSize(context));
        dr.add(ptbd_size);
        for (int i=0; i<ptbd_size; i++) {
            ParEntry pe = parameters_to_be_defaulted.tryGet(context, i);
            dr.add(pe.index);
        }

        dr.add((set_parameters_at_once == null ? ((Integer) 0) : ((Integer) 1)));
        dr.add((parameters_to_be_set == null ? ((Integer) 0) : (int) parameters_to_be_set.getSize(context)));
        dr.add((parameters_to_be_defaulted == null ? ((Integer) 0) : (int) parameters_to_be_defaulted.getSize(context)));
    }
}
