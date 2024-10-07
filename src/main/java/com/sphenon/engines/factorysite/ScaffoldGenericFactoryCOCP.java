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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.util.Vector;

public class ScaffoldGenericFactoryCOCP extends ScaffoldBaseFactory {

    protected Vector<String[]> pre_conditions;
    protected Vector<String[]> post_conditions;
    protected Vector<String[]> pre_build_scripts;
    protected Vector<String[]> post_build_scripts;

    protected boolean static_create_method;
    protected boolean set_parameters_at_once;
    protected int parameters_to_be_set;
    protected int parameters_to_be_defaulted;
    // DELETEME, if COCPAccessorFactory works reliably
    protected COCPBuildTextFactory cocpbtf;
    protected COCPAccessorFactory cocpaf;
    protected int[][] index_map;

    // DELETEME, if COCPAccessorFactory works reliably
    public ScaffoldGenericFactoryCOCP (CallContext context, Type type, Class factoryclass, boolean allow_dynamic_type_check, Type component_type, Vector_ScaffoldParameter_long_ parameters, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid, boolean has_variable_signature, boolean can_precreate, String scaffold_id, boolean static_create_method, COCPBuildTextFactory cocpbtf, String scaffold_index, boolean set_parameters_at_once, int parameters_to_be_set, int parameters_to_be_defaulted) {
        super(context, type, factoryclass, allow_dynamic_type_check, component_type, parameters, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
        this.pre_conditions = pre_conditions;
        this.post_conditions = post_conditions;
        this.pre_build_scripts = pre_build_scripts;
        this.post_build_scripts = post_build_scripts;
        this.has_variable_signature = has_variable_signature;
        this.can_precreate = can_precreate;
        this.scaffold_id = scaffold_id;
        this.static_create_method = static_create_method;
        this.cocpbtf = cocpbtf;
        this.cocpaf = null;
        this.index_map = null;
        this.scaffold_index = scaffold_index;
        this.set_parameters_at_once = set_parameters_at_once;
        this.parameters_to_be_set = parameters_to_be_set;
        this.parameters_to_be_defaulted = parameters_to_be_defaulted;
    }

    static protected Vector<String[]> prepareExpressions(CallContext context, String[][][] expressions, int index) {
        Vector<String[]> vector = new Vector<String[]>();
        if (expressions != null && expressions.length != 0) {
            for (String[] entry : expressions[index]) {
                vector.add(entry);
            }
        }
        return vector;
    }

    public ScaffoldGenericFactoryCOCP (CallContext context, Type type, Class factoryclass, boolean allow_dynamic_type_check, Type component_type, Vector_ScaffoldParameter_long_ parameters, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, String[][][] expressions, String source_location_info, String problem_monitor_oid, boolean has_variable_signature, boolean can_precreate, String scaffold_id, boolean static_create_method, COCPAccessorFactory cocpaf, int[][] index_map, boolean set_parameters_at_once, int parameters_to_be_set, int parameters_to_be_defaulted) {
        this(context, type, factoryclass, allow_dynamic_type_check, component_type, parameters, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, prepareExpressions(context, expressions, 0), prepareExpressions(context, expressions, 1), prepareExpressions(context, expressions, 2), prepareExpressions(context, expressions, 3), source_location_info, problem_monitor_oid, has_variable_signature, can_precreate, scaffold_id, static_create_method, cocpaf, index_map, set_parameters_at_once, parameters_to_be_set, parameters_to_be_defaulted);
    }

    protected ScaffoldGenericFactoryCOCP (CallContext context, Type type, Class factoryclass, boolean allow_dynamic_type_check, Type component_type, Vector_ScaffoldParameter_long_ parameters, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid, boolean has_variable_signature, boolean can_precreate, String scaffold_id, boolean static_create_method, COCPAccessorFactory cocpaf, int[][] index_map, boolean set_parameters_at_once, int parameters_to_be_set, int parameters_to_be_defaulted) {
        super(context, type, factoryclass, allow_dynamic_type_check, component_type, parameters, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
        this.pre_conditions = pre_conditions;
        this.post_conditions = post_conditions;
        this.pre_build_scripts = pre_build_scripts;
        this.post_build_scripts = post_build_scripts;
        this.has_variable_signature = has_variable_signature;
        this.can_precreate = can_precreate;
        this.scaffold_id = scaffold_id;
        this.static_create_method = static_create_method;
        this.cocpbtf = null;
        this.cocpaf = cocpaf;
        this.index_map = index_map;
        this.scaffold_index = scaffold_index;
        this.set_parameters_at_once = set_parameters_at_once;
        this.parameters_to_be_set = parameters_to_be_set;
        this.parameters_to_be_defaulted = parameters_to_be_defaulted;
    }

    public void setFactorySite(CallContext context, FactorySite factory_site) {
        this.setFactorySite(context, factory_site, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts);
    }

    protected boolean has_variable_signature;
    public boolean hasVariableSignature (CallContext context) {
        return this.has_variable_signature;
    }

    protected boolean can_precreate;
    protected boolean canPrecreate(CallContext context) {
        return this.can_precreate;
    }

    protected Object factory_instance;
    public Object getFactoryInstance(CallContext context) {
        if (this.static_create_method) {
            return null;
        } else {
            if (this.factory_instance == null) {
                this.factory_instance = (this.cocpaf != null ? this.cocpaf.createFactoryInstance(context, this) : this.cocpbtf.createFactoryInstance(context, this));
                if (this.component_type != null) {
                    if (this.cocpaf != null) {
                        this.cocpaf.setComponentType(context, this, this.component_type);
                        // [DELETEME] legacy, not implemented anymore - this.cocpbtf 
                    }
                }
                this.factoryinstance = this.factory_instance;
            }
            return this.factory_instance;
        }
    }

    protected Object precreateInstance(CallContext context) {
        return (this.cocpaf != null ? this.cocpaf.precreateInstance(context, this) : this.cocpbtf.precreateInstance(context, this));
    }

    protected Object createInstance(CallContext context) {
        return (this.cocpaf != null ? this.cocpaf.createInstance(context, this) : this.cocpbtf.createInstance(context, this));
    }

    public void reset(CallContext context) {
        this.factory_instance = null;
        super.reset(context);
    }

    protected String scaffold_id;
    public String getScaffoldId(CallContext context) {
        return this.scaffold_id;
    }

    protected String scaffold_index;
    public String getScaffoldIndex(CallContext context) {
        return this.scaffold_index;
    }

    public void setParameters(CallContext context) throws DataSourceUnavailable {
       
        if (this.static_create_method && this.factory_instance == null) { return; }

        Vector_ScaffoldParameter_long_ mypars = parameters;
        
        ScaffoldParameter sp;
        DynamicScaffoldParameter dsp;
        
        Scope local_scope = null;
        boolean skip_parameter = false;
        Iterable it = null;
        String exp = null;
        
        if (this.set_parameters_at_once) {
            Vector<String> names_vector  = new Vector<String>();
            Vector<Object> values_vector = new Vector<Object>();
            
            if (this.have_dynamic_parameters) {
                // hier könnte man überlegen (switch-o.ä.-gesteuert) ob man die
                // Dynamik auch rausnimmt und das hart runtercodiert generiert
                
                {   Object o = null;
                    try {
                        o = parameters.tryGet(context, 0).getValue(context).getValueAsObject(context);
                        mypars = (Vector_ScaffoldParameter_long_) o;
                    } catch (ClassCastException cce) {
                        CustomaryContext.create((Context)context).throwPreConditionViolation(context, cce, "Object of type '%(type)' cannot be delivered, dynamic parameters in use, but parameter is not a 'Vector_ScaffoldParameter_long_', as expected, but a '%(got)'", "type", this.type == null ? "(null)" : this.type.getName(context), "got", o == null ? "(null)" : o.getClass().getName());
                        throw (ExceptionPreConditionViolation) null; // compiler insists
                    } catch (IgnoreErraneousDataSource ieds) {
                        return;
                    }
                }
                for (int i=0; i<mypars.getSize(context); i++) {
                    sp  = mypars.tryGet(context, i);
                    dsp = (sp instanceof DynamicScaffoldParameter ? (DynamicScaffoldParameter) sp : null);
                    
                    local_scope = this.pushScope(context, dsp, true);
                    
                    it = getForeach(context, dsp, local_scope);
                    if (it != null) {
                        for (Object index_object : it) {
                            updateScope(context, dsp, local_scope, index_object);
                            if (getIf(context, dsp) == false) {
                                continue;
                            }
                            DataSource my_ds = sp.getValue(context);
                            FactorySiteTextBased.resetSubTree(context, my_ds);
                            Object o = null;
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

                        Object o = null;
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
            } else {

                for (int i=0; i<mypars.getSize(context); i++) {
                    sp  = mypars.tryGet(context, i);
                    dsp = (sp instanceof DynamicScaffoldParameter ? (DynamicScaffoldParameter) sp : null);

                    local_scope = this.pushScope(context, dsp, true);

                    it = getForeach(context, dsp, local_scope);
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
            }

            String[] setnames = new String[names_vector.size()];
            int ni = 0;
            for (String name : names_vector) {
                setnames[ni++] = name;
            }
            Object setvalues = (this.cocpaf != null ? this.cocpaf.createParametersAtOnceArray(context, this, values_vector) : this.cocpbtf.createParametersAtOnceArray(context, this, values_vector));
            
            if (this.cocpaf != null) {
                this.cocpaf.setParametersAtOnce(context, this, setnames, setvalues);
            } else {
                this.cocpbtf.setParametersAtOnce(context, this, setnames, setvalues);
            }
        } else { // else parameters at once
            
            if (parameters_to_be_set != 0) {
                for (int i=0; i<parameters_to_be_set; i++) {
                    Object set_value;
                    sp  = parameters.tryGet(context, i);
                    dsp = (sp instanceof DynamicScaffoldParameter ? (DynamicScaffoldParameter) sp : null);
                    local_scope = this.pushScope(context, dsp, false);
                    if (getIf(context, dsp) == false) {
                        if (sp.getValue(context) instanceof Scaffold) {
                            ((Scaffold)(sp.getValue(context))).skip(context);
                        }
                        if ((this.cocpaf != null ? this.cocpaf.hasFactoryDefaultValue(context, this, this.index_map[0][i]) : this.cocpbtf.hasFactoryDefaultValue(context, this, i, false))) {
                            set_value = (this.cocpaf != null ? this.cocpaf.getFactoryDefaultValue(context, this, this.index_map[0][i]) : this.cocpbtf.getFactoryDefaultValue(context, this, i, false));
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
                    
                    if (this.cocpaf != null) {
                        this.cocpaf.setFactoryValue(context, this, this.index_map[0][i], set_value);
                    } else {
                        this.cocpbtf.setFactoryValue(context, this, i, set_value, false);
                    }
                }
            }

            if (parameters_to_be_defaulted != 0) {
                for (int i=0; i<parameters_to_be_defaulted; i++) {
                    Object set_value;
                    set_value = (this.cocpaf != null ? this.cocpaf.getFactoryDefaultValue(context, this, this.index_map[1][i]) : this.cocpbtf.getFactoryDefaultValue(context, this, i, true));
                    if (this.cocpaf != null) {
                        this.cocpaf.setFactoryValue(context, this, this.index_map[1][i], set_value);
                    } else {
                        this.cocpbtf.setFactoryValue(context, this, i, set_value, true);
                    }
                }
            }
        }
    }
}
