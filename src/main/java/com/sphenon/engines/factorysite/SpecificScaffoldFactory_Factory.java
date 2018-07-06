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
import com.sphenon.basics.system.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.javacode.*;
import com.sphenon.basics.javacode.classes.*;

import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import com.sphenon.engines.aggregator.annotations.*;

import java.lang.reflect.*;

import java.util.Hashtable;
import java.util.Vector;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

import java.io.StringWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class SpecificScaffoldFactory_Factory implements SpecificScaffoldFactory, ContextAware {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static protected boolean goals_enabled;
    static {
        notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.SpecificScaffoldFactory_Factory");
        goals_enabled = GoalLocationContext.getGoalsEnabled(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.SpecificScaffoldFactory_Factory");
    };

    protected Type type;
    protected Type factory_type;
    protected String method_name;
    protected boolean allow_dynamic_type_check;
    protected Method create_method;
    protected Method precreate_method;
    protected Method set_parameters_at_once;
    protected Method new_instance_method;
    protected Method get_generic_component_type_method;
    protected Method set_component_type_method;
    protected Type component_type = null;
    protected Class factoryclass;
    protected Constructor constructor;
    protected boolean cons_context_par;
    protected String build_string;

    protected Hashtable par_entries;
    protected Vector<ParEntry> formal_scaffold_parameters;

    public Vector<ParEntry> getFormalScaffoldParameters (CallContext context) {
        try {
            initialise(context);
        } catch (InvalidFactory ifac) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ifac, "Cannot retrieve formal parameters for scaffold, underlying factory is invalid");
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

    public String getBuildString(CallContext context) throws InvalidFactory {
        if (is_initialised == false) { return this.build_string; }
        return "Factory|"
            + this.type.getId(context) + "|"
            + this.factory_type.getId(context) + "|"
            + (this.method_name == null ? "" : this.method_name) + "|"
            + this.allow_dynamic_type_check + "|"
            + this.type_context;
    }

    static public SpecificScaffoldFactory_Factory buildFromString(CallContext context, String build_string) {
        String[] args = build_string.split("\\|");
        Type type                        = TypeManager.tryGetById(context, args[1]);
        Type factory_type                = TypeManager.tryGetById(context, args[2]);
        String method_name               = (args[3] == null || args[3].length() == 0 ? null : args[3]);
        boolean allow_dynamic_type_check = new Boolean(args[4]);
        String type_context              = args[5];
        context = Context.create(context);
        TypeContext tc = TypeContext.create((Context)context);
        tc.setSearchPathContext(context, type_context);
        try {
            return new SpecificScaffoldFactory_Factory(context, type, factory_type, method_name, allow_dynamic_type_check, false, build_string, type_context);
        } catch (InvalidFactory ifac) { return null; /* cannot happen */ }
    }

    public SpecificScaffoldFactory_Factory (CallContext context, Type type, Type factory_type, String method_name, boolean allow_dynamic_type_check) throws InvalidFactory {
        this(context, type, factory_type, method_name, allow_dynamic_type_check, true, null, null);
    }

    public SpecificScaffoldFactory_Factory (CallContext context, Type type, Type factory_type, String method_name, boolean allow_dynamic_type_check, boolean do_initialise, String build_string, String type_context) throws InvalidFactory {
        this.allow_dynamic_type_check = allow_dynamic_type_check;

        // - we need to register for Java core type only, since
        // factory might be generic, but then it's applicable
        // to all template instances anyhow
        // - if we do not, e.g. return type of create method
        // does not match target type anymore
        // - maybe this is only valid for java generic instances,
        // but currently at runtime there are no others
        if (type != null && type.getName(context).matches(".*Reloadable.*")) {
            System.err.println("#### OINK ####");
            System.err.println("T" + type.getName(context));
            (new Throwable()).printStackTrace();
        }
        while (type instanceof TypeParametrised) {
            type = ((TypeParametrised) type).getBaseType(context);
            if (type != null && type.getName(context).matches(".*Reloadable.*")) {
                System.err.println("#### GRUNZ ####");
                System.err.println("T" + type.getName(context));
                (new Throwable()).printStackTrace();
            }
        }
        this.type = type;

        this.factory_type = factory_type;
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
        return "Scaffold factory (F) '" + factory_type.getName(context) + "'";
    }

    protected volatile boolean is_initialised;

    protected void initialise (CallContext call_context) throws InvalidFactory {
        if (is_initialised == false) {
            synchronized(this) {
                if (is_initialised == false) {
                    is_initialised = true;

                    Context context = Context.create(call_context);
                    CustomaryContext cc = CustomaryContext.create(context);

                    this.par_entries = new java.util.Hashtable();

                    TypeContext tc = TypeContext.get((Context)context);
                    this.type_context = tc.getSearchPathContext(context);
                    
                    try {
                        factoryclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, ((TypeImpl) (factory_type)).getJavaClassName(context));
                    } catch (ClassNotFoundException e) {
                        cc.throwImpossibleState (context, "Factory class retrieved from Type instance not found: %(class)", "class", factory_type.getName(context));
                        throw (ExceptionImpossibleState) null;
                    }
                    
                    Method[] methods = factoryclass.getMethods();
                    String typename = null;
                    String bad_return_types = null;
                    if (type instanceof JavaType) {
                        typename = ((JavaType) type).getJavaClassName(context);
                    } else if (type instanceof TypeParametrised) {
                        TypeParametrised tp = (TypeParametrised) type;
                        if (tp.getBaseType(context) instanceof JavaType) {
                            typename = ((JavaType) tp.getBaseType(context)).getJavaClassName(context);
                        } else {
                            cc.throwLimitation(context, "Type not an instance of 'JavaType', but of '%(class)'", "class", tp.getBaseType(context).getClass());
                            throw (ExceptionLimitation) null;
                        }
                    } else {
                        cc.throwLimitation(context, "Type neither an instance of 'JavaType' nor of 'TypeParametrised', but of '%(class)'", "class", type.getClass());
                        throw (ExceptionLimitation) null;
                    }
                    
                    if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "SpecificScaffoldFactory_Factory, examining '%(factory)' for type '%(type)'...", "factory", factory_type.getName(context), "type", typename); }
                    
                    int j=0;
                    methodcheck: for (int i=0; i<methods.length; i++) {
                        String name = methods[i].getName();
                        if ((methods[i].getAnnotation(OCPIgnore.class)) != null) { continue methodcheck; }
                        if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "Method '%(method)'...", "method", name); }
                        if (name.equals("set_ParametersAtOnce")) {
                            Class[] parameters_types = methods[i].getParameterTypes();
                            if (parameters_types.length != 3) { continue methodcheck; }
                            if (!parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) { continue methodcheck; }
                            if (!parameters_types[1].getName().equals("[Ljava.lang.String;")) { continue methodcheck; }
                            if (!parameters_types[2].isArray()) { continue methodcheck; }
                            
                            if (!methods[i].getReturnType().getName().equals("void")) { continue methodcheck; }
                            
                            component_type = TypeManager.get(context, parameters_types[2].getComponentType());
                            this.set_parameters_at_once = methods[i];
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...recognised: parameters at once"); }
                        } else if (name.equals("get_GenericComponentTypeMethod")) {
                            // This is an exceptionally stupid name: ..........*Method*
                            // -> to be refactored
                            Class[] parameters_types = methods[i].getParameterTypes();
                            if (parameters_types.length != 2) { continue methodcheck; }
                            if (!parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) { continue methodcheck; }
                            if (!parameters_types[1].getName().equals("com.sphenon.basics.metadata.Type")) { continue methodcheck; }
                            
                            if (!methods[i].getReturnType().getName().equals("com.sphenon.basics.metadata.Type")) { continue methodcheck; }
                            
                            this.get_generic_component_type_method = methods[i];
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...recognised: get generic component type method"); }
                        } else if (name.equals("set_ComponentType")) {
                            Class[] parameters_types = methods[i].getParameterTypes();
                            if (parameters_types.length != 2) { continue methodcheck; }
                            if (!parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) { continue methodcheck; }
                            if (!parameters_types[1].getName().equals("com.sphenon.basics.metadata.Type")) { continue methodcheck; }
                            
                            if (!methods[i].getReturnType().getName().equals("void")) { continue methodcheck; }
                            
                            this.set_component_type_method = methods[i];
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...recognised: set component type method"); }
                        } else if ((method_name != null && name.equals(method_name)) || ((method_name == null || method_name.length() == 0) && name.length() >= 6 && name.substring(0,6).equals("create"))) {
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, formal create method..."); }
                            if ((method_name == null || method_name.length() == 0) && name.length() > 6) {
                                Type type_from_method_name = null;
                                String typename_from_method_name = name.substring(6);
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
                                // the following code proved crucial
                                // reason: when prelaoding factories, a factory for D(dervied)
                                // can be preloaded for a type B(ase); then, this factory reports
                                // "B" as target, but actually can return a "D" (is more capable,
                                // so to speak) - this caused trouble when same factory was used
                                // in a derived ocp constellation, where the target type check
                                // failed (where D was expected, but B reported)
                                boolean is_derived = false;
                                if (! ((is_derived = type_from_method_name.isA(context, type)) || (allow_dynamic_type_check && type.isA(context, type_from_method_name)))) { continue methodcheck; }
                                if (is_derived && type_from_method_name.equals(type) == false) {
                                    this.type = type_from_method_name;
                                    typename = ((TypeImpl) (type)).getJavaClassName(context);
                                }
                            }
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, type fits..."); }
                            Class[] parameters_types = methods[i].getParameterTypes();
                            if (parameters_types.length != 1) { continue methodcheck; }
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, has 1 parameter..."); }
                            if (!parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) { continue methodcheck; }
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, context parameter ok..."); }
                            Type return_type = TypeManager.get(context, methods[i].getReturnType());
                            if (!(return_type.isA(context, type) || (allow_dynamic_type_check && type.isA(context, return_type)))) {
                                continue methodcheck;
                            }
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, return type ok..."); }
                            if (create_method != null) {
                                if ((notification_level & Notifier.MONITORING) != 0) { NotificationContext.sendCaution(context, "More than one create method matches in factory '%(factory)' (consider marking one of them as OCPIgnore, specifically if one is derived from the other)", "factory", factory_type.getName(context)); }
                            }
                            create_method = methods[i];
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...recognised: create method"); }
                        } else if ((method_name != null && name.equals("pre" + method_name)) || ((method_name == null || method_name.length() == 0) && name.length() >= 9 && name.substring(0,9).equals("precreate"))) {
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, formal precreate method..."); }
                            if ((method_name == null || method_name.length() == 0) && name.length() > 9) {
                                Type type_from_method_name = null;
                                String typename_from_method_name = name.substring(9);
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
                                if (! (type_from_method_name.isA(context, type) || (allow_dynamic_type_check && type.isA(context, type_from_method_name)))) { continue methodcheck; }
                            }
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, type fits..."); }
                            Class[] parameters_types = methods[i].getParameterTypes();
                            if (parameters_types.length != 1) { continue methodcheck; }
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, has 1 parameter..."); }
                            if (!parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) { continue methodcheck; }
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, context parameter ok..."); }
                            Type return_type = TypeManager.get(context, methods[i].getReturnType());
                            if (!(return_type.isA(context, type) || (allow_dynamic_type_check && type.isA(context, return_type)))) {
                                continue methodcheck;
                            }
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, return type ok..."); }
                            if (precreate_method != null) {
                                if ((notification_level & Notifier.MONITORING) != 0) { NotificationContext.sendCaution(context, "More than one precreate method matches in factory '%(factory)' (consider marking one of them as OCPIgnore, specifically if one is derived from the other)", "factory", factory_type.getName(context)); }
                            }
                            precreate_method = methods[i];
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...recognised: precreate method"); }
                        } else if (name.equals("newInstance") && new_instance_method == null) {
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, formal newInstance method..."); }
                            Class[] parameters_types = methods[i].getParameterTypes();
                            if (parameters_types.length != 1) { continue methodcheck; }
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, has 1 parameter..."); }
                            if (!parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) { continue methodcheck; }
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, context parameter ok..."); }
                            Type return_type = TypeManager.get(context, methods[i].getReturnType());
                            if (!(return_type.isA(context, factory_type))) {
                                if (bad_return_types == null) { bad_return_types = ""; } else { bad_return_types += ", "; }
                                bad_return_types += methods[i].getReturnType().getName();
                                continue methodcheck;
                            }
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, return type ok..."); }
                            bad_return_types = null;
                            new_instance_method = methods[i];
                            if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...recognised: newInstance method"); }
                        } else {
                            if (name.length() > 3 && name.regionMatches(false, 0, "set", 0, 3)) {
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, formal set method..."); }
                                String parname;
                                try { parname = name.substring(3); } catch (StringIndexOutOfBoundsException e) { continue methodcheck; }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, setter..."); }
                                
                                Class[] parameters_types = methods[i].getParameterTypes();
                                java.lang.reflect.Type[] generic_parameters_types = methods[i].getGenericParameterTypes();
                                
                                boolean has_context = false;
                                if (  (    (has_context = (parameters_types.length == 2))
                                        ||                (parameters_types.length == 1)
                                      ) == false
                                   ) {
                                    if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "Factory '%(factory)' method '%(method)' has %(given) parameters (method is not a usable set method, where 1 or 2 parameters are required, namely optionally a context and a mandatory value)", "factory", factory_type.getName(context), "method", name, "given", t.o(parameters_types.length)); }
                                    continue methodcheck;
                                }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, has 1 or 2 parameters..."); }
                                if (has_context && ! parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) {
                                    if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "Factory '%(factory)' method's '%(method)' 1st parameter is of type '%(got)', not a 'CallContext' (method is not a usable set method, where 1 or 2 parameters are required, namely optionally a context and a mandatory value)", "factory", factory_type.getName(context), "method", name, "given", t.o(parameters_types.length), "got", parameters_types[0].getName()); }
                                    continue methodcheck;
                                }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, context parameter ok..."); }
                                
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
                                Type partype = TypeManager.get(context, generic_parameters_types[has_context ? 1 : 0]);
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
                                    cc.throwPreConditionViolation(context, "Invalid factory interface: multiple 'set%(parname)' methods found", "parname", parname);
                                    throw (ExceptionPreConditionViolation) null;
                                } else {
                                    if (par_entry.type != null && par_entry.default_method == null) {
                                        cc.throwImpossibleState(context, "Invalid ParEntry for 'set/default%(parname)': no methods defined yet, but type", "parname", parname);
                                        throw (ExceptionImpossibleState) null;
                                    }
                                }
                                if (par_entry.default_method != null) {
                                    if (! par_entry.type.isA(context, partype)) {
                                        cc.throwPreConditionViolation(context, "Invalid factory interface: 'set%(parname)' and 'default%(parname)' methods have incompatible types, 'set' requires a '%(settype)', 'default' returns a '%(defaulttype)'", "parname", parname, "settype", partype, "defaulttype", par_entry.type);
                                        throw (ExceptionPreConditionViolation) null;
                                    }
                                }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, parameter 2 fits..."); }
                                par_entry.set_method = methods[i];
                                par_entry.set_method_has_context = has_context;
                                par_entry.type = partype;
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...recognised: set method"); }
                            } else if (name.length() > 7 && name.regionMatches(false, 0, "default", 0, 7)) {
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, formal default method..."); }
                                String parname;
                                try { parname = name.substring(7); } catch (StringIndexOutOfBoundsException e) { continue methodcheck; }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, defaultter..."); }
                                
                                Class[] parameters_types = methods[i].getParameterTypes();
                                boolean has_context = false;
                                if (  (    (has_context = (parameters_types.length == 1))
                                        ||                (parameters_types.length == 0)
                                      ) == false
                                   ) {
                                    if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "Factory '%(factory)' method '%(method)' has %(given) parameters (method is not a usable default method, where 0 or 1 parameter is required, namely an optional context)", "factory", factory_type.getName(context), "method", name, "given", t.o(parameters_types.length)); }
                                    continue methodcheck;
                                }
                                if (has_context && ! parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext")) {
                                    if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "Factory '%(factory)' method's '%(method)' 1st parameter is of type '%(got)', not a 'CallContext' (method is not a usable default method, where 0 or 1 parameter is required, namely an optional context)", "factory", factory_type.getName(context), "method", name, "given", t.o(parameters_types.length), "got", parameters_types[0].getName()); }
                                }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, parameters fit..."); }
                                
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
                                    cc.throwPreConditionViolation(context, "Invalid factory interface: multiple 'default%(parname)' methods found", "parname", parname);
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
                                        cc.throwPreConditionViolation(context, "Invalid factory interface: 'set%(parname)' and 'default%(parname)' methods have incompatible types, 'set' requires a '%(settype)', 'default' returns a '%(defaulttype)'", "parname", parname, "settype", par_entry.type, "defaulttype", return_type);
                                        throw (ExceptionPreConditionViolation) null;
                                    }
                                } else {
                                    par_entry.type = return_type;
                                }
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...interesting, return type ok..."); }
                                par_entry.default_method = methods[i];
                                par_entry.is_optional = true;
                                par_entry.default_method_has_context = has_context;
                                if ((this.notification_level & Notifier.VERBOSE) != 0) { cc.sendTrace(context, Notifier.VERBOSE, "...recognised: default method"); }
                            }
                        }
                    }
                    if (bad_return_types != null) {
                        cc.throwPreConditionViolation(context, "Factory found ('%(factory)'), but 'newInstance' methods return '%(returntypes)', not a type matching to '%(expected)', as expected", "factory", factory_type.getName(context), "returntype", bad_return_types, "expected", factory_type.getName(context));
                        throw (ExceptionPreConditionViolation) null;
                    }
                    if (this.create_method == null) {
                        InvalidFactory.createAndThrow(context, "Factory found ('%(factory)'), but no (appropriate) 'create', 'create%(typename)' or 'create[DerivedClass]' method", "factory", factory_type.getName(context), "typename", typename);
                        throw (InvalidFactory) null;
                    }

                    if (Modifier.isStatic(this.create_method.getModifiers())) {
                        this.set_parameters_at_once = null;
                        this.new_instance_method = null;
                        this.constructor = null;
                        this.par_entries = new java.util.Hashtable();
                        this.formal_scaffold_parameters = null;
                    } else {
                        if (this.set_parameters_at_once != null && par_entries.size() != 0) {
                            InvalidFactory.createAndThrow(context, "Factory found ('%(factory)'), but does provide 'set' as well as 'set_ParametersAtOnce' methods, which are mutually exclusive for factories", "factory", factory_type.getName(context));
                            throw (InvalidFactory) null;
                        }
                        if (new_instance_method == null) {
                            Constructor[] cons = this.factoryclass.getConstructors();
                            this.constructor = null;
                            cons_test: for (int i=0; i<cons.length; i++) {
                                if ( ! Modifier.isPublic(cons[i].getModifiers())) { continue cons_test; }
                                Class[] cons_parameters_types = cons[i].getParameterTypes();
                                this.cons_context_par = (cons_parameters_types.length != 0 && cons_parameters_types[0].getName().equals("com.sphenon.basics.context.CallContext"));
                                if (cons_parameters_types.length - (cons_context_par?1:0) != 0) {
                                    continue cons_test;
                                }
                                this.constructor = cons[i];
                                break;
                            }
                            if (this.constructor == null) {
                                InvalidFactory.createAndThrow(context, "Invalid factory, no (appropriate) constructor (required: either no parameters or only 'CallContext' parameter)");
                                throw (InvalidFactory) null;
                            }
                        }
                    }

                    if (formal_scaffold_parameters != null) {
                        java.util.Collections.sort(formal_scaffold_parameters, new ParEntryComparator(context));
                        int index = 0;
                        for (ParEntry pe : formal_scaffold_parameters) {
                            pe.index = index++;
                        }
                    }
                }
            }
        }
    }

    protected class ParEntryComparator implements java.util.Comparator {
        protected CallContext context;
        public ParEntryComparator(CallContext context) {
            this.context = context;
        }
        public int compare(Object o1, Object o2) {
            ParEntry pe1 = (ParEntry) o1;
            ParEntry pe2 = (ParEntry) o2;
            String index1 = pe1.name;
            String index2 = pe2.name;
            return index1.compareTo(index2);
        }
    }

    public MatchResult isMatching (CallContext call_context, Type actual_matched_type, Vector_ScaffoldParameter_long_ parameters, Map_TypeOrNull_String_ parameters_by_name, boolean allow_missing_arguments) throws InvalidFactory {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        initialise(context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Is matching? ('%(type)' [F|'%(factory_type)'])", "type", this.type, "factory_type", this.factory_type); }

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
                    if (t.equals(this.factory_type)) {
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
                MessageText mt = MessageText.create(context, "No, parameter names used multiply, but factory requires uniqueness (no set-at-once method)");
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
                                MessageText mt = MessageText.create(context, "No, actual ('%(acttype)') and formal ('%(fortype)') parameter do not match for '%(parname)'", "parname", parname, "acttype", actual_partype, "fortype", parentry.type);
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
                Set<String> uuset = new HashSet<String>();
                for (ScaffoldParameter sp : parameters.getIterable_ScaffoldParameter_(context)) {
                    String name = sp.getName(context);
                    uuset.add(name);
                }
                java.util.Iterator iter;
                iter = par_entries.entrySet().iterator();
                while (iter.hasNext()) {
                    java.util.Map.Entry me;
                    String parname;
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
                    uuset.remove(parname);
                }

                if (non_applicable_ones != null) {
                    iter = non_applicable_ones.entrySet().iterator();
                    while (iter.hasNext()) {
                        java.util.Map.Entry me;
                        String parname;
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
                        uuset.remove(parname);
                    }
                }

                String unused = null;
                for (String uu : uuset) {
                    if (unused == null) {
                        unused = "'" + uu + "'";
                    } else {
                        unused += ", " + "'" + uu + "'";
                    }
                }

                MessageText mt = MessageText.create(context, "No, the following parameters are provided, but not part of the signature: '%(unused)', actual parameters '(%(actpars))' not applicable ones '(%(napars))', parameters to be defaulted '(%(defpars))', formal ones '(%(forpars))'", "unused", unused, "actpars", actpars_info, "forpars", forpars_info, "defpars", defpars_info, "napars", napars_info);
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

    public Scaffold create (CallContext context, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidFactory {

        initialise(context);

        Type actual_component_type = this.component_type;
        Type actual_scaffold_type  = this.type;

        if (    match_result.actual_matched_type != null
             && match_result.actual_matched_type instanceof TypeParametrised
           ) {
            actual_scaffold_type = match_result.actual_matched_type;

            if (this.get_generic_component_type_method != null) {
                Type generic_component_type = (Type) ReflectionUtilities.invoke(context, this.get_generic_component_type_method, null, context, match_result.actual_matched_type);
                if (generic_component_type != null && actual_component_type != null && generic_component_type.isA(context, actual_component_type) == false) {
                    InvalidFactory.createAndThrow(context, "During creation of scaffold for factory '%(factory)', generic component type '%(generictype)' is not derived from nongeneric type '%(nongenerictype)'", "factory", factory_type.getName(context), "generictype", generic_component_type, "nongenerictype", actual_component_type);
                    throw (InvalidFactory) null;
                }
                actual_component_type = generic_component_type;
            }
        }

        return new ScaffoldGenericFactory(context, this, actual_scaffold_type, this.factoryclass, this.allow_dynamic_type_check, this.create_method, this.precreate_method, this.set_parameters_at_once, this.set_component_type_method, actual_component_type, this.par_entries, this.new_instance_method, this.constructor, this.cons_context_par, parameters, match_result, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
    }

    public String printCOCPCode(CallContext context, FactorySite factory_site) {
        String package_name = "cache.cocp.accessors." + this.factoryclass.getName().replaceFirst("\\.[^\\.]+$", "");
        String class_name   = "COCPAccessorFactory_" + this.factoryclass.getName().replaceFirst(".*\\.", "");

        if (this instanceof SpecificScaffoldFactory_FactoryEnum) {
            TypeImpl ti;
            try {
                ti = (TypeImpl) type;
            } catch (ClassCastException cce) {
                CustomaryContext.create((Context)context).throwInvalidState(context, "Enumeration type in factorysite was not represented by TypeManager Java wrapper (TypeImpl)");
                throw (ExceptionInvalidState) null; // compiler insists
            }
            class_name += "_" + ti.getJavaClass(context).getName().replace(".","_");
        }

        String full_class_name   = package_name + "." + class_name;

        DynamicCOCPAccessorFactory dcocpa = new DynamicCOCPAccessorFactory(context, full_class_name, factory_site);

        if (dcocpa.needsGeneration(context) == false) {
            return full_class_name;
        }

        JavaCodeManager jcm = dcocpa.getJavaCodeManager(context);
        BufferedWriter bw = jcm.getDefaultResource(context).getWriter(context);

        try {
            java.util.Iterator iter = null;

            bw.write("package " + package_name + ";\n");
            bw.write("\n");
            bw.write("import com.sphenon.basics.context.*;\n");
            bw.write("import com.sphenon.basics.configuration.*;\n");
            bw.write("import com.sphenon.basics.exception.*;\n");
            bw.write("import com.sphenon.basics.notification.*;\n");
            bw.write("import com.sphenon.basics.customary.*;\n");
            bw.write("import com.sphenon.basics.metadata.*;\n");
            bw.write("import com.sphenon.basics.expression.*;\n");
            bw.write("import com.sphenon.basics.expression.returncodes.*;\n");
            bw.write("\n");
            bw.write("import com.sphenon.engines.factorysite.*;\n");
            bw.write("import com.sphenon.engines.factorysite.returncodes.*;\n");
            bw.write("import com.sphenon.engines.factorysite.exceptions.*;\n");
            bw.write("import com.sphenon.engines.factorysite.tplinst.*;\n");
            bw.write("\n");

            {
                Vector_String_long_ sp = TypeManager.getSearchPath(context);
                if (sp != null) {
                    for (String spe : sp.getIterable_String_(context)) {
                        if (spe != null && spe.length() != 0) {
                            bw.write("import " + spe + ".*;\n");
                        }
                    }
                }
            }

            bw.write("\n");
            bw.write("import java.util.Vector;\n");
            bw.write("\n");
            bw.write("public class " + class_name + " implements COCPAccessorFactory {\n");
            bw.write("    \n");
            bw.write("    static protected " + class_name + " singleton;\n");
            bw.write("    \n");
            bw.write("    public " + class_name + "(CallContext context) {\n");
            bw.write("    }\n");
            bw.write("    \n");
            bw.write("    static public " + class_name + " getSingleton(CallContext context) {\n");
            bw.write("        if (singleton == null) {;\n");
            bw.write("            singleton = new " + class_name + "(context);\n");
            bw.write("        };\n");
            bw.write("        return singleton;\n");
            bw.write("    }\n");
            bw.write("    \n");
            bw.write("    public " + this.factoryclass.getName() + " getFactoryInstance(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp) {\n");
            bw.write("        return (" + this.factoryclass.getName() + ") (sgfcocp.getFactoryInstance(context));\n");
            bw.write("    }\n");
            bw.write("    \n");
            bw.write("    public " + this.factoryclass.getName() + " createFactoryInstance(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp) {\n");
            if (Modifier.isStatic(this.create_method.getModifiers())) {
                bw.write("        return null;\n");
            } else {
                bw.write("        " + this.factoryclass.getName() + " factory_instance = null;\n");
                if (this.new_instance_method != null) {
                    bw.write("        factory_instance = " + this.factoryclass.getName() + "." + new_instance_method.getName() + "(context);\n");
                } else {
                    if (this.cons_context_par) {
                        bw.write("        factory_instance = new " + this.factoryclass.getName() + "(context);\n");
                    } else {
                        bw.write("        factory_instance = new " + this.factoryclass.getName() + "();\n");
                    }
                }
                if (this instanceof SpecificScaffoldFactory_FactoryEnum) {
                    TypeImpl ti;
                    try {
                        ti = (TypeImpl) type;
                    } catch (ClassCastException cce) {
                        CustomaryContext.create((Context)context).throwInvalidState(context, "Enumeration type in factorysite was not represented by TypeManager Java wrapper (TypeImpl)");
                        throw (ExceptionInvalidState) null; // compiler insists
                    }
                    bw.write("        factory_instance.attachEnumClass(context, " + ti.getJavaClass(context).getName() + ".class);\n");
                }
                bw.write("        return factory_instance;\n");
            }
            bw.write("    }\n");
            bw.write("    \n");
            bw.write("    public Object precreateInstance(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp) {\n");
            if (precreate_method != null) {
                Class[] exs = precreate_method.getExceptionTypes();
                if (exs != null && exs.length != 0) {
                    bw.write("        try {\n    ");
                }                
                if (Modifier.isStatic(this.create_method.getModifiers())) {
                    bw.write("        return " + this.factoryclass.getName() + "." + precreate_method.getName() + "(context);\n");
                } else {
                    bw.write("        return getFactoryInstance(context, sgfcocp)." + precreate_method.getName() + "(context);\n");
                }
                if (exs != null && exs.length != 0) {
                    for (Class ex : exs) {
                        bw.write("        } catch (" + ex.getName() + " e) {\n");
                        bw.write("            CustomaryContext.create((Context)context).throwPreConditionViolation(context, e, \"Factory precreate method invoked in scaffold '%(scaffold)' failed\", \"scaffold\", \"" + class_name + "\");\n");
                        bw.write("            throw (ExceptionPreConditionViolation) null; // compiler insists\n");
                    }
                    bw.write("        }\n");
                }                
            } else {
                bw.write("        return null;\n");
            }
            bw.write("    }\n");
            bw.write("    \n");
            bw.write("    public Object createInstance(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp) {\n");
            Class[] exs = create_method.getExceptionTypes();
            if (exs != null && exs.length != 0) {
                bw.write("        try {\n    ");
            }                
            if (Modifier.isStatic(this.create_method.getModifiers())) {
                bw.write("        return " + this.factoryclass.getName() + "." + create_method.getName() + "(context);\n");
            } else {
                bw.write("        return getFactoryInstance(context, sgfcocp)." + create_method.getName() + "(context);\n");
            }
            if (exs != null && exs.length != 0) {
                for (Class ex : exs) {
                    bw.write("        } catch (" + ex.getName() + " e) {\n");
                    bw.write("            CustomaryContext.create((Context)context).throwPreConditionViolation(context, e, \"Factory create method invoked in scaffold '%(scaffold)' failed\", \"scaffold\", \"" + class_name + "\");\n");
                    bw.write("            throw (ExceptionPreConditionViolation) null; // compiler insists\n");
                }
                bw.write("        }\n");
            }                
            bw.write("    }\n");

            Class java_component_type = TypeManager.getJavaClass(context, this.component_type);
            bw.write("    public void setParametersAtOnce(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp, String[] names, Object values) {\n");
            if (set_parameters_at_once != null) {
                bw.write("        getFactoryInstance(context, sgfcocp)." + set_parameters_at_once.getName() + "(context, names, (" + java_component_type.getName() + "[]) values);\n");
            }
            bw.write("    }\n");
            bw.write("    \n");
            bw.write("    public Object createParametersAtOnceArray(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp, Vector values_vector) {\n");
            if (set_parameters_at_once != null) {
                bw.write("        " + java_component_type.getName() + "[] array = new " + java_component_type.getName() + "[values_vector.size()];\n");
                bw.write("        int vi = 0;\n");
                bw.write("        for (Object value : values_vector) {\n");
                bw.write("            array[vi++] = (" + java_component_type.getName() + ") value;\n");
                bw.write("        }\n");
                bw.write("        return array;\n");
            } else {
                bw.write("        return null;\n");
            }
            bw.write("    }\n");
            bw.write("    \n");

            // iter = par_entries.entrySet().iterator();
            // while (iter.hasNext()) {
            //     java.util.Map.Entry me = (java.util.Map.Entry) iter.next();
            //     String   pn = (String) me.getKey();
            //     ParEntry pe = (ParEntry) me.getValue();
            //     String   pc = ReflectionUtilities.convertToSubClassOfObject(context, pe.set_method.getParameterTypes()[pe.set_method_has_context ? 1 : 0]);
            //     bw.write("    static public final int MEMBER_" + pn + " = " + pe.index + ";\n");
            // }
            // bw.write("    \n");

            bw.write("    public boolean hasFactoryDefaultValue(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp, int member) {\n");
            bw.write("        switch (member) {\n");

            iter = par_entries.entrySet().iterator();
            while (iter.hasNext()) {
                java.util.Map.Entry me = (java.util.Map.Entry) iter.next();
                String   pn = (String) me.getKey();
                ParEntry pe = (ParEntry) me.getValue();
                String   pc = ReflectionUtilities.convertToSubClassOfObject(context, pe.set_method.getParameterTypes()[pe.set_method_has_context ? 1 : 0]);
                if (pe.default_method != null) {
                    bw.write("            case " + pe.index + ": return true;\n");
                }
            }
            bw.write("            default: return false;\n");
            bw.write("        }\n");
            bw.write("    }\n");
            bw.write("    \n");

            bw.write("    public Object getFactoryDefaultValue(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp, int member) {\n");
            bw.write("        switch (member) {\n");
            iter = par_entries.entrySet().iterator();
            while (iter.hasNext()) {
                java.util.Map.Entry me = (java.util.Map.Entry) iter.next();
                String   pn = (String) me.getKey();
                ParEntry pe = (ParEntry) me.getValue();
                String   pc = ReflectionUtilities.convertToSubClassOfObject(context, pe.set_method.getParameterTypes()[pe.set_method_has_context ? 1 : 0]);
                if (pe.default_method != null) {
                    if (pe.default_method_has_context) {
                        bw.write("            case " + pe.index + ": return getFactoryInstance(context, sgfcocp)." + pe.default_method.getName() + "(context);\n");
                    } else {
                        bw.write("            case " + pe.index + ": return getFactoryInstance(context, sgfcocp)." + pe.default_method.getName() + "();\n");
                    }
                }
            }
            bw.write("            default: return null;\n");
            bw.write("        }\n");
            bw.write("    }\n");
            bw.write("    \n");
            bw.write("    public void setFactoryValue(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp, int member, Object value) {\n");
            bw.write("        switch (member) {\n");
            iter = par_entries.entrySet().iterator();
            while (iter.hasNext()) {
                java.util.Map.Entry me = (java.util.Map.Entry) iter.next();
                String   pn = (String) me.getKey();
                ParEntry pe = (ParEntry) me.getValue();
                String   pc = ReflectionUtilities.convertToSubClassOfObject(context, pe.set_method.getParameterTypes()[pe.set_method_has_context ? 1 : 0]);

                if (pe.set_method_has_context) {
                    bw.write("            case " + pe.index + ": getFactoryInstance(context, sgfcocp)." + pe.set_method.getName() + "(context, (" + pc + ") value); break;\n");
                } else {
                    bw.write("            case " + pe.index + ": getFactoryInstance(context, sgfcocp)." + pe.set_method.getName() + "((" + pc + ") value) break;\n");
                }
            }
            bw.write("        }\n");
            bw.write("    }\n");
            bw.write("    public void setComponentType(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp, Type component_type) {\n");
            if (set_component_type_method != null) {
                bw.write("        getFactoryInstance(context, sgfcocp)." + set_component_type_method.getName() + "(context, component_type);\n");
            }
            bw.write("    }\n");
            bw.write("}\n");

        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Cannot write to '%(file)'", "file", jcm.getDefaultResource(context).getJavaFilePath(context));
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }

        jcm.closeResources(context);
        dcocpa.notifyCodeGenerationCompleted(context);

        return full_class_name;
    }
}
