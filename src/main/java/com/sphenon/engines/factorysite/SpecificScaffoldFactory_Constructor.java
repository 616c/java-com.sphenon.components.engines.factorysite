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
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.goal.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.returncodes.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.factories.Factory_Aggregate;
import com.sphenon.engines.factorysite.tplinst.*;

import java.lang.reflect.*;

import java.util.Hashtable;
import java.util.Vector;
import java.util.List;
import java.util.LinkedList;

public class SpecificScaffoldFactory_Constructor implements SpecificScaffoldFactory, ContextAware {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static protected boolean goals_enabled;
    static {
        notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.SpecificScaffoldFactory_Constructor");
        goals_enabled = GoalLocationContext.getGoalsEnabled(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.SpecificScaffoldFactory_Constructor");
    };

    protected Constructor cons;
    protected Type type;
    protected Class[] parameter_classes;
    protected Type[]  parameter_types;
    protected boolean context_par;
    protected int number_of_parameters;
    protected String build_string;

    protected String type_context;

    protected Vector<ParEntry> formal_scaffold_parameters;

    public Vector<ParEntry> getFormalScaffoldParameters (CallContext context) {
        if (this.formal_scaffold_parameters == null) {
            this.formal_scaffold_parameters = new Vector<ParEntry>();

            for (Type t : this.parameter_types) {
                ParEntry par_entry = new ParEntry(t, "?", false);

                formal_scaffold_parameters.add(par_entry);
            }
        }
        return this.formal_scaffold_parameters;
    }

    public Type getComponentTypeOfCollection(CallContext context) {
        return null;
    }

    public String getTypeContext(CallContext context) {
        return this.type_context;
    }

    public String getBuildString(CallContext context) {
        String result = "Constructor|"
            + this.type.getId(context) + "|"
            + this.type_context + "|";
        boolean first = true;
        for (Class pt : this.cons.getParameterTypes()) {
            if (first == false) { result += "#"; }
            result += pt.getName();
            first = false;
        }
        return result;
    }

    static public SpecificScaffoldFactory_Constructor buildFromString(CallContext context, String build_string) {
        String[] args           = build_string.split("\\|", -1);
        Type     type           = TypeManager.tryGetById(context, args[1]);
        String   type_context   = args[2];
        String[] arg_type_names = (args[3] != null && args[3].length() != 0 ? args[3].split("#") : new String[0]);
        Class[] arg_types       = new Class[arg_type_names.length];
        int     index           = 0;
        try {
            for (String arg_type_name : arg_type_names) {
                Class cls;
                if      (arg_type_name.equals("boolean")) { cls = boolean.class; }
                else if (arg_type_name.equals("byte"))    { cls = byte.class; }
                else if (arg_type_name.equals("char"))    { cls = char.class; }
                else if (arg_type_name.equals("int"))     { cls = int.class; }
                else if (arg_type_name.equals("short"))   { cls = short.class; }
                else if (arg_type_name.equals("long"))    { cls = long.class; }
                else if (arg_type_name.equals("float"))   { cls = float.class; }
                else if (arg_type_name.equals("double"))  { cls = double.class; }
                else                                      { cls = com.sphenon.basics.cache.ClassCache.getClassForName(context, arg_type_name); }
                arg_types[index++] = cls;
            }
        } catch (ClassNotFoundException cnfe) {
             CustomaryContext.create((Context)context).throwConfigurationError(context, cnfe, "Could rebuild scaffold factory from string");
             throw (ExceptionConfigurationError) null; // compiler insists
        }
        try {
            context = Context.create(context);
            TypeContext tc = TypeContext.create((Context)context);
            tc.setSearchPathContext(context, type_context);
            Class cls = ((TypeImpl) type).getJavaClass(context);
            Constructor cons = cls.getConstructor(arg_types);
            return new SpecificScaffoldFactory_Constructor(context, type, cons, false, build_string, type_context);
        } catch (NoSuchMethodException nsme) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, nsme, "Could rebuild scaffold factory from string");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    public SpecificScaffoldFactory_Constructor (CallContext context, Type type, Constructor cons) {
        this(context, type, cons, true, null, null);
    }

    protected SpecificScaffoldFactory_Constructor (CallContext context, Type type, Constructor cons, boolean do_initialise, String build_string, String type_context) {
        this.cons = cons;
        
        if (Factory_Aggregate.debug_classloader) {
            Factory_Aggregate.debugClassLoader("SpecificcaffoldFactory_Constructor.constructor.declaring", this.cons.getDeclaringClass());
            Factory_Aggregate.debugClassLoader("SpecificcaffoldFactory_Constructor.constructor", this.cons.getClass());
        }

        // - we need to register for Java core type only, since
        //   factory might be generic, but then it's applicable
        //   to all template instances anyhow
        // - if we do not, e.g. return type of create method
        //   does not match target type anymore
        // - maybe this is only valid for java generic instances,
        //   but currently at runtime there are no others
        while (type instanceof TypeParametrised) {
            type = ((TypeParametrised) type).getBaseType(context);
        }
        this.type = type;

        if (type_context != null) {
            this.type_context = type_context;
        } else {
            TypeContext tc = TypeContext.get((Context)context);
            this.type_context = tc.getSearchPathContext(context);
        }

        this.parameter_classes = cons.getParameterTypes();
        java.lang.reflect.Type[] generic_parameter_types = cons.getGenericParameterTypes();
        this.number_of_parameters = this.parameter_classes.length;
        this.parameter_types   = new Type[this.number_of_parameters]; 
        this.context_par = (this.number_of_parameters != 0 && parameter_classes[0].getName().equals("com.sphenon.basics.context.CallContext"));

        for (int p=0; p<this.number_of_parameters; p++) {
            this.parameter_types[p] = TypeManager.get(context, generic_parameter_types[p]);
        }

        this.build_string = build_string;
    }

    public String toString(CallContext context) {
        return "Scaffold factory (CO) '" + this.cons.getClass().getName() + "'";
    }

    public MatchResult isMatching (CallContext call_context, Type actual_matched_type, Vector_ScaffoldParameter_long_ parameters, Map_TypeOrNull_String_ parameters_by_name, boolean allow_missing_arguments) {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Is matching? ('%(type)', [Constructor])", "type", type); }

        int non_applicable_count = 0;
        Hashtable non_applicable_ones = null;
        List napars_info = null;
        for (ScaffoldParameter sp : parameters.getIterable_ScaffoldParameter_(context)) {
            if (sp.getAppliesTo(context) != null) {
                boolean does_apply = false;
                for (Type t : sp.getAppliesTo(context)) {
                    if (t.equals(this.type)) {
                        does_apply = true;
                        break;
                    }
                }
                if (does_apply == false) {
                    non_applicable_count++;
                    if (non_applicable_ones == null) {
                        non_applicable_ones = new Hashtable();
                    }
                    String name = sp.getName(context);
                    non_applicable_ones.put(name, sp);
                    if (napars_info == null) {
                        napars_info = new LinkedList();
                    }
                    napars_info.add(name);
                }
            }
        }

        if (this.number_of_parameters - (context_par?1:0) != (parameters.getSize(context) - non_applicable_count)) {
            MessageText mt = MessageText.create(context, "No, different number of parameters, got %(got) minus number of not applicable ones %(nrna), expected %(expected)%({'',' [context parameter substracted]'}[cp])", "got", t.s(parameters.getSize(context)), "expected", t.s(this.number_of_parameters), "cp", t.o(context_par ? 1 : 0), "nrna", t.s(non_applicable_count));
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                cc.sendTrace(context, Notifier.DIAGNOSTICS, mt);
            }
            return new MatchResult(mt, this);
        }

        Vector_ParEntry_long_ parameters_to_be_set = Factory_Vector_ParEntry_long_.construct(context);

        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Checking parameters..."); }
        for (int p=context_par?1:0; p<this.number_of_parameters; p++) {
            String pname = parameters.tryGet(context, p-(context_par?1:0)).getName(context);
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Checking parameter '%(name)', type '%(type1)' is a type '%(type2)' ?", "name", pname, "type1", parameters.tryGet(context, p-(context_par?1:0)).getType(context), "type2", this.parameter_types[p]); }

            if (non_applicable_ones != null && non_applicable_ones.get(pname) != null) {
                continue;
            }

            if (parameters.tryGet(context, p-(context_par?1:0)).getType(context) == null) {
                int i = 0;
            } else {
                String typename = parameters.tryGet(context, p-(context_par?1:0)).getType(context).toString();
                if (! parameters.tryGet(context, p-(context_par?1:0)).getType(context).isA(context, this.parameter_types[p])) {
                    MessageText mt = MessageText.create(context, "No, parameter #%(nr) '%(name)' is not a '%(expected)', as expected, but a '%(got)'", "nr", t.s(p), "name", pname, "got", parameters.tryGet(context, p-(context_par?1:0)).getType(context), "expected", this.parameter_types[p]);
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                        cc.sendTrace(context, Notifier.DIAGNOSTICS, mt);
                    }
                    return new MatchResult(mt, this);
                }
            }

            parameters_to_be_set.append(context, new ParEntry(this.parameter_types[p], pname, false));
        }
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Checking parameters - done.."); }

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Yes"); }
        return new MatchResult(actual_matched_type, parameters_to_be_set, null);
    }

    public Scaffold create (CallContext context, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) {
        return new ScaffoldGenericConstructor(context, this.type, this.cons, this.parameter_classes, this.parameter_types, this.context_par, this.number_of_parameters, parameters, match_result, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
    }
}
