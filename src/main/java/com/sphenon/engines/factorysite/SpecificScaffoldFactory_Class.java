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
import com.sphenon.basics.system.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import com.sphenon.engines.aggregator.annotations.*;

import java.lang.reflect.*;

import java.util.Hashtable;
import java.util.Vector;
import java.util.List;
import java.util.LinkedList;

public class SpecificScaffoldFactory_Class implements SpecificScaffoldFactory, ContextAware {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static protected boolean goals_enabled;
    static {
        notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.SpecificScaffoldFactory_Class");
        goals_enabled = GoalLocationContext.getGoalsEnabled(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.SpecificScaffoldFactory_Class");
    };

    protected Type type;
    protected Type class_type;
    protected String method_name;
    protected boolean allow_dynamic_type_check;
    protected Method set_parameters_at_once;
    protected Method new_instance_method;
    protected Type component_type = null;
    protected Class classclass;
    protected Constructor constructor;
    protected boolean cons_context_par;
    protected Method initialise_method;
    protected String build_string;

    protected java.util.Hashtable par_entries;
    protected Vector<ParEntry> formal_scaffold_parameters;

    public Vector<ParEntry> getFormalScaffoldParameters (CallContext context) {
        try {
            initialise(context);
        } catch (InvalidClass ic) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ic, "Cannot retrieve formal parameters for scaffold, underlying class is invalid");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        return this.formal_scaffold_parameters;
    }

    public Type getComponentTypeOfCollection(CallContext context) {
        return this.component_type;
    }

    protected String type_context;

    public String getTypeContext(CallContext context) {
        return this.type_context;
    }

    public String getBuildString(CallContext context) throws InvalidClass {
        if (is_initialised == false) { return this.build_string; }
        return "Class|"
            + this.type.getId(context) + "|"
            + this.class_type.getId(context) + "|"
            + (this.method_name == null ? "" : this.method_name) + "|"
            + this.allow_dynamic_type_check + "|"
            + this.type_context;
    }

    static public SpecificScaffoldFactory_Class buildFromString(CallContext context, String build_string) {
        String[] args = build_string.split("\\|");
        Type type                        = TypeManager.tryGetById(context, args[1]);
        Type class_type                  = TypeManager.tryGetById(context, args[2]);
        String method_name               = (args[3] == null || args[3].length() == 0 ? null : args[3]);
        boolean allow_dynamic_type_check = new Boolean(args[4]);
        String type_context              = args[5];
        context = Context.create(context);
        TypeContext tc = TypeContext.create((Context)context);
        tc.setSearchPathContext(context, type_context);
        try {
            return new SpecificScaffoldFactory_Class(context, type, class_type, method_name, allow_dynamic_type_check, false, build_string, type_context);
        } catch (InvalidClass icla) { return null; /* cannot happen */ }
    }

    public SpecificScaffoldFactory_Class (CallContext context, Type type, Type class_type, String method_name, boolean allow_dynamic_type_check) throws InvalidClass {
        this(context, type, class_type, method_name, allow_dynamic_type_check, true, null, null);
    }

    public SpecificScaffoldFactory_Class (CallContext context, Type type, Type class_type, String method_name, boolean allow_dynamic_type_check, boolean do_initialise, String build_string, String type_context) throws InvalidClass {
        this.allow_dynamic_type_check = allow_dynamic_type_check;

        // - we need to register for Java core type only, since
        // factory might be generic, but then it's applicable
        // to all template instances anyhow
        // - if we do not, e.g. return type of create method
        // does not match target type anymore
        // - maybe this is only valid for java generic instances,
        // but currently at runtime there are no others
        while (type instanceof TypeParametrised) {
            type = ((TypeParametrised) type).getBaseType(context);
        }
        this.type = type;

        this.class_type = class_type;
        this.method_name = method_name;

        this.build_string = build_string;
        if (type_context != null) {
            this.type_context = type_context;
        } else if (do_initialise == false){
            TypeContext tc = TypeContext.get((Context)context);
            this.type_context = tc.getSearchPathContext(context);
        }
        if (do_initialise) {
            initialise(context);
        }
    }

    public String toString(CallContext context) {
        return "Scaffold factory (C) '" + class_type.getName(context) + "'";
    }

    protected volatile boolean is_initialised;

    protected void initialise (CallContext call_context) throws InvalidClass {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        if (is_initialised == false) {
            synchronized(this) {
                if (is_initialised == false) {
                    is_initialised = true;

                    this.par_entries = new java.util.Hashtable();

                    TypeContext tc = TypeContext.get((Context)context);
                    this.type_context = tc.getSearchPathContext(context);

                    JavaType jt =   class_type instanceof TypeParametrised
                                  ? ((JavaType) ((TypeParametrised)class_type).getBaseType(context))
                                  : ((JavaType) class_type);
                    classclass = jt.getJavaClass(context);

                    String bad_return_types = null;

                    if (this.method_name != null && this.method_name.isEmpty() == false) {
                        String[] mn = this.method_name.split("/");
                        this.new_instance_method = (new ReflectionUtilities(context)).tryGetMethod(context, mn[0], mn[1], CallContext.class);
                        if (this.new_instance_method == null) {
                            this.new_instance_method = (new ReflectionUtilities(context)).tryGetMethod(context, mn[0], mn[1]);
                        }                            
                        if (this.new_instance_method == null) {
                            cc.throwPreConditionViolation(context, "Method '%(method)' not found", "method", this.method_name);
                            throw (ExceptionPreConditionViolation) null;
                        }                            
                    }

            //         try {
            //             classclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, ((TypeImpl) (class_type)).getJavaClassName(context));
            //         } catch (ClassNotFoundException e) {
            //             cc.throwImpossibleState (context, "Class class retrieved from Type instance not found: %(class)", "class", class_type.getName(context));
            //             throw (ExceptionImpossibleState) null;
            //         }

                    Method[] methods = classclass.getMethods();
                    String typename = jt.getJavaClassName(context);
                    int j=0;
                    methodcheck: for (int i=0; i<methods.length; i++) {
                        String name = methods[i].getName();
                        if ((methods[i].getAnnotation(OCPIgnore.class)) != null) { continue methodcheck; }
                        if (name.equals("set_ParametersAtOnce")) {
                            Class[] parameters_types = methods[i].getParameterTypes();
                            if (parameters_types.length != 3) { continue methodcheck; }
                            if (!parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) { continue methodcheck; }
                            if (!parameters_types[1].getName().equals("[Ljava.lang.String;")) { continue methodcheck; }
                            if (!parameters_types[2].isArray()) { continue methodcheck; }

                            if (!methods[i].getReturnType().getName().equals("void")) { continue methodcheck; }
                            component_type = TypeManager.get(context, parameters_types[2].getComponentType());
                            this.set_parameters_at_once = methods[i];
                        } else {
                            if (name.length() > 3 && name.regionMatches(false, 0, "set", 0, 3)) {
                                String parname;
                                try { parname = name.substring(3); } catch (StringIndexOutOfBoundsException e) { continue methodcheck; }

                                Class[] parameters_types = methods[i].getParameterTypes();
                                java.lang.reflect.Type[] generic_parameters_types = methods[i].getGenericParameterTypes();

                                boolean has_context = false;
                                if (  (    (has_context = (parameters_types.length == 2))
                                        ||                (parameters_types.length == 1)
                                      ) == false
                                   ) {
                                    if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "Class '%(class)' method '%(method)' has %(given) parameters (method is not a usable set method, where 1 or 2 parameters are required, namely optionally a context and a mandatory value)", "class", class_type.getName(context), "method", name, "given", t.o(parameters_types.length)); }
                                    continue methodcheck;
                                }
                                if (has_context && ! parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) {
                                    if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "Class '%(class)' method's '%(method)' 1st parameter is of type '%(got)', not a 'CallContext' (method is not a usable set method, where 1 or 2 parameters are required, namely optionally a context and a mandatory value)", "class", class_type.getName(context), "method", name, "given", t.o(parameters_types.length), "got", parameters_types[0].getName()); }
                                }

                                ParEntry par_entry = null;
                                try {
                                    par_entry = (ParEntry) par_entries.get(parname);
                                } catch (ClassCastException cce) {
                                    cc.throwImpossibleState(context, cce, "Internal hash contains invalid entry, expected 'ParEntry', got '%(got)'", "got", par_entries.get(parname).getClass());
                                }
                                if (par_entry == null) {
                                    par_entry = new ParEntry();
                                    par_entry.name = parname;
                                    par_entries.put(parname, par_entry);
                                    if (this.formal_scaffold_parameters == null) {
                                        this.formal_scaffold_parameters = new Vector<ParEntry>();
                                    }
                                    formal_scaffold_parameters.add(par_entry);
                                }
                                if ((methods[i].getAnnotation(OCPOptional.class)) != null) {
                                    par_entry.is_optional = true;
                                }
                                Type partype = TypeManager.get(context, generic_parameters_types[has_context ? 1 : 0], class_type);
                                if (par_entry.set_method != null) {
                                    if (par_entry.type != null && par_entry.type.equals(partype)) {
                                        if (par_entry.type.isA(context, partype)) {
                                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "There is already a set method which is identical except for the return type, and the first return type is compatibel to second return type, therefore this looks just ok and we use the first one"); }
                                            continue methodcheck;
                                        }
                                        if (partype.isA(context, par_entry.type)) {
                                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "There is already a set method which is identical except for the return type, and the second return type is compatibel to the first return type, therefore this looks ok and we use the second one"); }
                                            par_entry.set_method = methods[i];
                                            par_entry.type = partype;
                                            // possibly an additional check regarding default type, but it's more knifflig...
                                            continue methodcheck;
                                        }
                                    }
                                    cc.throwPreConditionViolation(context, "Invalid class interface: multiple 'set%(parname)' methods found", "parname", parname);
                                    throw (ExceptionPreConditionViolation) null;
                                } else {
                                    if (par_entry.type != null && par_entry.default_method == null) {
                                        cc.throwImpossibleState(context, "Invalid ParEntry for 'set/default%(parname)': no methods defined yet, but type", "parname", parname);
                                        throw (ExceptionImpossibleState) null;
                                    }
                                }
                                if (par_entry.default_method != null) {
                                    if (! par_entry.type.isA(context, partype)) {
                                        cc.throwPreConditionViolation(context, "Invalid class interface: 'set%(parname)' and 'default%(parname)' methods have incompatible types, 'set' requires a '%(settype)', 'default' returns a '%(defaulttype)'", "parname", parname, "settype", partype, "defaulttype", par_entry.type);
                                        throw (ExceptionPreConditionViolation) null;
                                    }
                                }
                                par_entry.set_method = methods[i];
                                par_entry.set_method_has_context = has_context;
                                par_entry.type = partype;
                            } else if (name.length() >= 10 && name.substring(0,10).equals("initialise")) {
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, formal initialise method..."); }
                                if (name.length() > 10) {
                                    Type type_from_method_name = null;
                                    String typename_from_method_name = name.substring(10);
                                    type_from_method_name = TypeManager.tryGet(context, typename_from_method_name);
                                    if (type_from_method_name == null) {
                                        if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...hmm, type from method name '%(name)' not found...", "name", typename_from_method_name); }
                                        int pos = typename.lastIndexOf('.');
                                        if (pos != -1) {
                                            String package_name = typename.substring(0, pos+1);
                                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...trying '%(name)'...", "name", package_name + typename_from_method_name); }
                                            type_from_method_name = TypeManager.tryGet(context, package_name + typename_from_method_name);
                                            if (type_from_method_name == null) {
                                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...still no success, giving up..."); }
                                                continue methodcheck;
                                            }
                                        } else {
                                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...and no package info available, giving up..."); }
                                            continue methodcheck;
                                        }
                                    }
                                    if (! (type_from_method_name.isA(context, type))) { continue methodcheck; }
                                }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, type fits..."); }
                                Class[] parameters_types = methods[i].getParameterTypes();
                                if (parameters_types.length != 1) { continue methodcheck; }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, has 1 parameter..."); }
                                if (!parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) { continue methodcheck; }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, context parameter ok..."); }
                                if (!methods[i].getReturnType().getName().equals("void")) { continue methodcheck; }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, return type ok..."); }
                                this.initialise_method = methods[i];
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...recognised: create method"); }
                            } else if (name.length() > 7 && name.regionMatches(false, 0, "default", 0, 7)) {
                                String parname;
                                try { parname = name.substring(7); } catch (StringIndexOutOfBoundsException e) { continue methodcheck; }

                                Class[] parameters_types = methods[i].getParameterTypes();
                                boolean has_context = false;

                                if (  (    (has_context = (parameters_types.length == 1))
                                        ||                (parameters_types.length == 0)
                                      ) == false
                                   ) {
                                    if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "Class '%(class)' method '%(method)' has %(given) parameters (method is not a usable default method, where 0 or 1 parameter is required, namely an optional context)", "class", class_type.getName(context), "method", name, "given", t.o(parameters_types.length)); }
                                    continue methodcheck;
                                }
                                if (has_context && ! parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) {
                                    if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "Class '%(class)' method's '%(method)' 1st parameter is of type '%(got)', not a 'CallContext' (method is not a usable default method, where 0 or 1 parameter is required, namely an optional context)", "class", class_type.getName(context), "method", name, "given", t.o(parameters_types.length), "got", parameters_types[0].getName()); }
                                }

                                ParEntry par_entry = null;
                                try {
                                    par_entry = (ParEntry) par_entries.get(parname);
                                } catch (ClassCastException cce) {
                                    cc.throwImpossibleState(context, cce, "Internal hash contains invalid entry, expected 'ParEntry', got '%(got)'", "got", par_entries.get(parname).getClass());
                                }
                                if (par_entry == null) {
                                    par_entry = new ParEntry();
                                    par_entry.name = parname;
                                    par_entries.put(parname, par_entry);
                                    if (this.formal_scaffold_parameters == null) {
                                        this.formal_scaffold_parameters = new Vector<ParEntry>();
                                    }
                                    formal_scaffold_parameters.add(par_entry);
                                }
                                if (par_entry.default_method != null) {
                                    cc.throwPreConditionViolation(context, "Invalid class interface: multiple 'default%(parname)' methods found", "parname", parname);
                                    throw (ExceptionPreConditionViolation) null;
                                } else {
                                    if (par_entry.type != null && par_entry.set_method == null) {
                                        cc.throwImpossibleState(context, "Invalid ParEntry for 'set/default%(parname)': no methods defined yet, but type", "parname", parname);
                                        throw (ExceptionImpossibleState) null;
                                    }
                                }
                                Type return_type = TypeManager.get(context, methods[i].getGenericReturnType());
                                if (par_entry.set_method != null) {
                                    if (! return_type.isA(context, par_entry.type)) {
                                        cc.throwPreConditionViolation(context, "Invalid class interface: 'set%(parname)' and 'default%(parname)' methods have incompatible types, 'set' requires a '%(settype)', 'default' returns a '%(defaulttype)'", "parname", parname, "settype", par_entry.type, "defaulttype", return_type);
                                        throw (ExceptionPreConditionViolation) null;
                                    }
                                } else {
                                    par_entry.type = return_type;
                                }
                                par_entry.default_method = methods[i];
                                par_entry.is_optional = true;
                                par_entry.default_method_has_context = has_context;
                            } else if (name.equals("newInstance") && new_instance_method == null) {
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, formal newInstance method..."); }
                                Class[] parameters_types = methods[i].getParameterTypes();
                                if (parameters_types.length != 1) { continue methodcheck; }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, has 1 parameter..."); }
                                if (!parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) { continue methodcheck; }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, context parameter ok..."); }
                                Type return_type = TypeManager.get(context, methods[i].getReturnType());
                                if (!(return_type.isA(context, class_type))) {
                                    if (bad_return_types == null) { bad_return_types = ""; } else { bad_return_types += ", "; }
                                    bad_return_types += methods[i].getReturnType().getName();
                                    continue methodcheck;
                                }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, return type ok..."); }
                                bad_return_types = null;
                                new_instance_method = methods[i];
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...recognised: newInstance method"); }
                            }
                        }
                    }
                    if (bad_return_types != null) {
                        cc.throwPreConditionViolation(context, "Class found ('%(class)'), but 'newInstance' methods return '%(returntypes)', not a type matching to '%(expected)', as expected", "class", class_type.getName(context), "returntype", bad_return_types, "expected", class_type.getName(context));
                        throw (ExceptionPreConditionViolation) null;
                    }
                    if (this.set_parameters_at_once != null && par_entries.size() != 0) {
                        InvalidClass.createAndThrow(context, "Class found ('%(class)'), but does provide 'set' as well as 'set_ParametersAtOnce' methods, which are mutually exclusive for factories", "class", class_type.getName(context));
                        throw (InvalidClass) null;
                    }
                    if (this.new_instance_method == null) {
                        Constructor[] cons = this.classclass.getConstructors();
                        this.constructor = null;
                        cons_test: for (int i=0; i<cons.length; i++) {
                            Class[] cons_parameters_types = cons[i].getParameterTypes();
                            this.cons_context_par = (cons_parameters_types.length != 0 && cons_parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext"));
                            if (cons_parameters_types.length - (cons_context_par?1:0) != 0) {
                                continue cons_test;
                            }
                            this.constructor = cons[i];
                            break;
                        }
                        if (this.constructor == null) {
                            InvalidClass.createAndThrow(context, "Invalid class '%(class)', no (appropriate) constructor (required: either no parameters or only 'CallContext' parameter)", "class", class_type.getName(context));
                            throw (InvalidClass) null;
                        }
                    }
                }
            }
        }
    }

    public MatchResult isMatching (CallContext call_context, Type actual_matched_type, Vector_ScaffoldParameter_long_ parameters, Map_TypeOrNull_String_ parameters_by_name, boolean allow_missing_arguments) throws InvalidClass {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        initialise(context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Is matching? ('%(type)' [C|'%(class_type)'])", "type", this.type, "class_type", this.class_type); }

        List actpars_info = null;
        List forpars_info = null;
        List defpars_info = null;
        List setpars_info = null;
        List ignpars_info = null;
        Vector_ParEntry_long_ parameters_to_be_defaulted = null;
        Vector_ParEntry_long_ parameters_to_be_set = null;

        int unnamned_count = 0;

        int non_applicable_count = 0;
        Hashtable non_applicable_ones = null;
        List napars_info = null;
        for (ScaffoldParameter sp : parameters.getIterable_ScaffoldParameter_(context)) {
            if (sp.getAppliesTo(context) != null) {
                boolean does_apply = false;
                for (Type t : sp.getAppliesTo(context)) {
                    if (t.equals(this.class_type)) {
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

        if (this.set_parameters_at_once == null) {
            if (parameters_by_name == null) {
                MessageText mt = MessageText.create(context, "No, parameter names used multiply, but class requires uniqueness (no set-at-once method)");
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                    cc.sendTrace(context, Notifier.DIAGNOSTICS, mt);
                }
                return new MatchResult(mt, this);
            }
            for (int pass=1; pass<=2; pass++) {
                java.util.Iterator iter = par_entries.entrySet().iterator();
                paracheck: while (iter.hasNext()) {
                    java.util.Map.Entry me;
                    String parname;
                    ParEntry parentry;
                    try {
                        me = (java.util.Map.Entry) iter.next();
                    } catch (ClassCastException cce) {
                        cc.throwImpossibleState(context, cce, "entrySet of Hashtable contains entry which is not a Map.Entry");
                        throw (ExceptionImpossibleState) null;
                    }
                    try {
                        parname = (String) me.getKey();
                    } catch (ClassCastException cce) {
                        cc.throwImpossibleState(context, cce, "entrySet of Hashtable contains a MapEntry whose key is not a String");
                        throw (ExceptionImpossibleState) null;
                    }
                    try {
                        parentry = (ParEntry) me.getValue();
                    } catch (ClassCastException cce) {
                        cc.throwImpossibleState(context, cce, "entrySet of Hashtable contains a MapEntry whose value is not a ParEntry");
                        throw (ExceptionImpossibleState) null;
                    }
                    if (parentry.set_method == null) {
                        continue paracheck;
                    }
                    if (pass == 1 && (this.notification_level & Notifier.OBSERVATION) != 0) {
                        if (forpars_info == null) {
                            forpars_info = new LinkedList();
                        }
                        forpars_info.add(parname);
                    }

                    if (    pass == 1 && parentry.default_method == null
                         || pass == 2 && parentry.default_method != null
                       ) {

                        boolean using_unnamed_entry = false;

                        TypeOrNull actual_partype_entry = (    non_applicable_ones != null
                                                            && non_applicable_ones.get(parname) != null
                                                          ) ? null : parameters_by_name.tryGet(context, parname);
                        if (    actual_partype_entry == null
                             // && parameters_by_name.getSize(context) == 1
                             && (pass == 1 || unnamned_count == 0)
                           ) {
                            actual_partype_entry = parameters_by_name.tryGet(context, "");
                            using_unnamed_entry = true;
                        }
                        if (actual_partype_entry == null) {
                            if (parentry.default_method == null) {
                                if (allow_missing_arguments == false && parentry.is_optional == false) {
                                    MessageText mt = MessageText.create(context, "No, no actual parameter and no default value for '%(parname)'", "parname", parname);
                                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                                        cc.sendTrace(context, Notifier.DIAGNOSTICS, mt);
                                    }
                                    return new MatchResult(mt, this);
                                } else {
                                    if ((this.notification_level & Notifier.OBSERVATION) != 0) {
                                        if (ignpars_info == null) {
                                            ignpars_info = new LinkedList();
                                        }
                                        ignpars_info.add(parname);
                                    }
                                }
                            } else {
                                if (parameters_to_be_defaulted == null) {
                                    parameters_to_be_defaulted = Factory_Vector_ParEntry_long_.construct(context);
                                }
                                parameters_to_be_defaulted.append(context, parentry);
                                if ((this.notification_level & Notifier.OBSERVATION) != 0) {
                                    if (defpars_info == null) {
                                        defpars_info = new LinkedList();
                                    }
                                    defpars_info.add(parname);
                                }
                            }
                        } else {
                            Type actual_partype = actual_partype_entry.type;

                            if (parameters_to_be_set == null) {
                                parameters_to_be_set = Factory_Vector_ParEntry_long_.construct(context);
                            }
                            parameters_to_be_set.append(context, parentry);
                            
                            if (actual_partype != null && ! actual_partype.isA(context, parentry.type)) {
                                MessageText mt = MessageText.create(context, "No, actual ('%(acttype)') and formal ('%(fortype)')parameter do not match", "parname", parname, "acttype", actual_partype, "fortype", parentry.type);
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                                    cc.sendTrace(context, Notifier.DIAGNOSTICS, mt);
                                }
                                return new MatchResult(mt, this);
                            }
                            
                            if (using_unnamed_entry) { unnamned_count++; }

                            if ((this.notification_level & Notifier.OBSERVATION) != 0) {
                                if (setpars_info == null) {
                                    setpars_info = new LinkedList();
                                }
                                setpars_info.add(parname);
                            }
                        }
                    }
                }
            }
            if (unnamned_count > 1) {
                MessageText mt = MessageText.create(context, "No, there is more than one formal parameter matching the unnamed actual parameter");
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                    cc.sendTrace(context, Notifier.DIAGNOSTICS, mt);
                }
                return new MatchResult(mt, this);
            }
            if ((this.notification_level & Notifier.OBSERVATION) != 0) {
                for (Iterator_ScaffoldParameter_ isp = parameters.getNavigator(context);
                     isp.canGetCurrent(context);
                     isp.next(context)) {
                    if (actpars_info == null) {
                        actpars_info = new LinkedList();
                    }
                    actpars_info.add(isp.tryGetCurrent(context).getName(context));
                }
            }
            if (     (parameters_to_be_set == null ? 0 : parameters_to_be_set.getSize(context))
                  != (parameters.getSize(context) - non_applicable_count)) {
                MessageText mt = MessageText.create(context, "No, number of actual parameters %(nract):'(%(actpars))' minus number of not applicable ones %(nrna):'(%(napars))' plus number of parameters to be defaulted %(nrdef):'(%(defpars))' differs from formal ones %(nrfor):'(%(forpars))'", "actpars", actpars_info, "forpars", forpars_info, "napars", napars_info, "defpars", defpars_info, "nract", t.s(parameters.getSize(context)), "nrna", t.s(non_applicable_count), "nrdef", t.s(parameters_to_be_defaulted == null ? 0 : parameters_to_be_defaulted.getSize(context)), "nrfor", t.s(parameters_to_be_set == null ? 0 : parameters_to_be_set.getSize(context)));
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                    cc.sendTrace(context, Notifier.DIAGNOSTICS, mt);
                }
                return new MatchResult(mt, this);
            }

            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Actual parameters: (%(actpars))", "actpars", actpars_info); }
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Formal parameters: (%(forpars))", "forpars", forpars_info); }
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Parameters to be set: (%(setpars))", "setpars", setpars_info); }
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Parameters to be defaulted: (%(defpars))", "defpars", defpars_info); }
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Parameters to be ignored: (%(ignpars))", "ignpars", ignpars_info); }
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Yes"); }
            return new MatchResult(actual_matched_type, parameters_to_be_set, parameters_to_be_defaulted);
        } else {
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Yes"); }
            return new MatchResult(actual_matched_type);
        }
    }

    public Scaffold create (CallContext context, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidClass {

        initialise(context);

        return new ScaffoldGenericClass(context, this.type, this.classclass, this.allow_dynamic_type_check, this.set_parameters_at_once, this.component_type, this.par_entries, this.new_instance_method, this.constructor, this.cons_context_par, this.initialise_method, parameters, match_result, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
    }
}
