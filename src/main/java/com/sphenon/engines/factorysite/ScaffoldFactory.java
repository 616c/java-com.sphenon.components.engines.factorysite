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

/*
  Various issues:

  cache_by_type does not take typecontext into account, but it
  is relevant for search; we should check hierarchically
  whether already available resp. in the parent and then
  cache it tc dependent.

  (yet that change is risky, so take care)

  related: currently initialisation is performed ondemand,
  since during cache reading (and also during dumpCache!)
  typecontexts are possibly not correctly set

  a better solution for the first issue would help the
  second as well

  in scaffold cache various entries are duplicated, the cause
  is possibly that they are registered from within different
  classloaders; to be examined
  
  to do that:: check on registration whether scaffold with same
  buildstring already in liste and if so, stackdump
 */

import com.sphenon.basics.context.*;
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.goal.*;
import com.sphenon.basics.performance.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.factory.returncodes.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.factories.Factory_Aggregate;
import com.sphenon.engines.factorysite.tplinst.*;

import com.sphenon.engines.aggregator.annotations.*;

import java.lang.reflect.*;

import java.util.regex.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.Hashtable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

public class ScaffoldFactory {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static protected boolean goals_enabled;

    static protected Configuration config;
    static { config = Configuration.create(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.ScaffoldFactory"); };
    static {
        notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.ScaffoldFactory");
        goals_enabled = GoalLocationContext.getGoalsEnabled(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.ScaffoldFactory");
    };

    static protected boolean try_type_itself = false;

    static protected Map<Type,Object> cache_by_type;
    static protected Map<Type,Object> cache_by_factory;
    static protected Map<Type,Object> cache_by_retriever;
    static protected Map<Type,Object> cache_by_class;
    static protected Map<Type,Object> cache_by_factory_aggregate;
    static protected Map<Type,Object> cache_by_array;

    static protected Map<Type,Object> preload_cache_by_type;
    static protected Map<Type,Object> preload_cache_by_factory;
    static protected Map<Type,Object> preload_cache_by_retriever;
    static protected Map<Type,Object> preload_cache_by_class;
    static protected Map<Type,Object> preload_cache_by_factory_aggregate;
    static protected Map<Type,Object> preload_cache_by_array;

    static {
        preload_cache_by_type = new HashMap<Type,Object>();
        preload_cache_by_factory = new HashMap<Type,Object>();
        preload_cache_by_retriever = new HashMap<Type,Object>();
        preload_cache_by_class = new HashMap<Type,Object>();
        preload_cache_by_factory_aggregate = new HashMap<Type,Object>();
        preload_cache_by_array = new HashMap<Type,Object>();

        resetCaches(null);
    }

    static public void resetCaches(CallContext context) {
        cache_by_type = new HashMap<Type,Object>();
        cache_by_factory = new HashMap<Type,Object>();
        cache_by_retriever = new HashMap<Type,Object>();
        cache_by_class = new HashMap<Type,Object>();
        cache_by_factory_aggregate = new HashMap<Type,Object>();
        cache_by_array = new HashMap<Type,Object>();
    }

    public ScaffoldFactory (CallContext context) {
    }

    public Scaffold get (CallContext call_context, Type type, String factory_name, String retriever_name, String method_name, Vector_ScaffoldParameter_long_ parameters, Map_TypeOrNull_String_ parameters_by_name, boolean allow_dynamic_type_check, boolean allow_missing_arguments, String listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]>  pre_build_scripts, Vector<String[]>  post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidFactory, InvalidRetriever, InvalidClass {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        Vector_SpecificScaffoldFactory_long_ specific_scaffold_factories = getSpecificScaffoldFactories(context, type, factory_name, retriever_name, method_name, allow_dynamic_type_check);
        return getMatchingFactoryAndCreate(context, specific_scaffold_factories, type, parameters, parameters_by_name, allow_dynamic_type_check, allow_missing_arguments, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
    }

    public void unloadScaffold (CallContext call_context, Type type) {
        preload_cache_by_type.remove(type);
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    static protected boolean DEBUG_CHECK = true;
    static protected long check_counter;
    protected void checkAndAppend(CallContext context, Vector_SpecificScaffoldFactory_long_ items, SpecificScaffoldFactory new_item) {
        if (DEBUG_CHECK) {
            try {
                String bs_new = new_item.getBuildString(context);
                for (SpecificScaffoldFactory item : items.getIterable_SpecificScaffoldFactory_(context)) {
                    String bs = item.getBuildString(context);
                    if (bs.equals(bs_new)) {
                        System.err.println("SFDEBUG: Item already in list: " + bs_new);
                        if (check_counter == 0) {
                            (new Throwable()).printStackTrace();
                        }
                        check_counter++;                
                    }
                }
            } catch (Throwable t) {
            }
        }
        items.append(context, new_item);
    }

    public void preloadScaffoldFactory (CallContext call_context, Type type, Type factory_type, boolean allow_dynamic_type_check) throws InvalidFactory {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        String fully_qualified_factory_name = ((TypeImpl) factory_type).getJavaClassName(context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Preloading factory '%(factory)' for type '%(type)'...", "factory", fully_qualified_factory_name, "type", type); }

        Vector_SpecificScaffoldFactory_long_ results = null;
        try {
            results = (Vector_SpecificScaffoldFactory_long_) preload_cache_by_type.get(type);
        } catch (ClassCastException cce) { /* boo */ }
        if (results == null) {
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No entry for type '%(type)' yet, creating...", "factory", fully_qualified_factory_name, "type", type); }

            results = Factory_Vector_SpecificScaffoldFactory_long_.construct(context);
            preload_cache_by_type.put(type, results);
        }

        checkAndAppend(context, results, getSpecificScaffoldFactory_Factory(context, type, factory_type, null, allow_dynamic_type_check, false));

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Preloaded"); }
    }

    public void preloadScaffoldRetriever (CallContext call_context, Type type, Type retriever_type, boolean allow_dynamic_type_check) throws InvalidRetriever {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        String fully_qualified_retriever_name = ((TypeImpl) retriever_type).getJavaClassName(context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Preloading retriever '%(retriever)' for type '%(type)'...", "retriever", fully_qualified_retriever_name, "type", type); }

        Vector_SpecificScaffoldFactory_long_ results = null;
        try {
            results = (Vector_SpecificScaffoldFactory_long_) preload_cache_by_type.get(type);
        } catch (ClassCastException cce) { /* boo */ }
        if (results == null) {
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No entry for type '%(type)' yet, creating...", "retriever", fully_qualified_retriever_name, "type", type); }

            results = Factory_Vector_SpecificScaffoldFactory_long_.construct(context);
            preload_cache_by_type.put(type, results);
        }

        checkAndAppend(context, results, getSpecificScaffoldFactory_Retriever(context, type, retriever_type, null, allow_dynamic_type_check, false));

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Preloaded"); }
    }

    public void preloadScaffoldClass (CallContext call_context, Type type, Type class_type, boolean allow_dynamic_type_check) throws InvalidClass {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        String fully_qualified_class_name = ((TypeImpl) class_type).getJavaClassName(context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Preloading class '%(class)' for type '%(type)'...", "class", fully_qualified_class_name, "type", type); }

        Vector_SpecificScaffoldFactory_long_ results = null;
        try {
            results = (Vector_SpecificScaffoldFactory_long_) preload_cache_by_type.get(type);
        } catch (ClassCastException cce) { /* boo */ }
        if (results == null) {
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No entry for type '%(type)' yet, creating...", "class", fully_qualified_class_name, "type", type); }

            results = Factory_Vector_SpecificScaffoldFactory_long_.construct(context);
            preload_cache_by_type.put(type, results);
        }

        checkAndAppend(context, results, getSpecificScaffoldFactory_Class(context, type, class_type, null, allow_dynamic_type_check, false));

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Preloaded"); }
    }

    public void preloadScaffoldFactory_Aggregate (CallContext call_context, Type type, String aggregate_name, boolean allow_dynamic_type_check) throws InvalidFactory {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Preloading factory aggregate '%(aggregate)' for type '%(type)'...", "aggregate", aggregate_name, "type", type); }

        Vector_SpecificScaffoldFactory_long_ results = null;
        try {
            results = (Vector_SpecificScaffoldFactory_long_) preload_cache_by_type.get(type);
        } catch (ClassCastException cce) { /* boo */ }
        if (results == null) {
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No entry for type '%(type)' yet, creating...", "type", type); }

            results = Factory_Vector_SpecificScaffoldFactory_long_.construct(context);
            preload_cache_by_type.put(type, results);
        }

        try {
            checkAndAppend(context, results, getSpecificScaffoldFactory_AggregateFactory (context, type, new TypeImpl_Aggregate(context, aggregate_name), allow_dynamic_type_check));
        } catch (NoSuchClass nsc) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, nsc, "Could not preload aggregate '%(aggregate)'", "aggregate", aggregate_name);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Preloaded"); }
    }

    public void preloadScaffoldArray (CallContext call_context, Type type) throws InvalidFactory {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Preloading array for type '%(type)'...", "type", type); }

        Vector_SpecificScaffoldFactory_long_ results = null;
        try {
            results = (Vector_SpecificScaffoldFactory_long_) preload_cache_by_type.get(type);
        } catch (ClassCastException cce) { /* boo */ }
        if (results == null) {
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No entry for type '%(type)' yet, creating...", "type", type); }

            results = Factory_Vector_SpecificScaffoldFactory_long_.construct(context);
            preload_cache_by_type.put(type, results);
        }

        checkAndAppend(context, results, getSpecificScaffoldFactory_Array(context, type));

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Preloaded"); }
    }

    protected Vector_SpecificScaffoldFactory_long_ getSpecificScaffoldFactories (CallContext call_context, Type type, String factory_name, String retriever_name, String method_name, boolean allow_dynamic_type_check) throws InvalidFactory, InvalidRetriever, InvalidClass {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        if (goals_enabled) { cc.createGoal(context, "Retrieving ScaffoldFactories for type '%(type)', factory '%(factory)', retriever '%(retriever)'", "type", type, "factory", factory_name, "retriever", retriever_name); }
        Vector_SpecificScaffoldFactory_long_ results = null;

        // create instance via method and use class or interface as is
        if (    (factory_name == null || factory_name.length() == 0)
             && (retriever_name == null || retriever_name.length() == 0)
             && (method_name != null && method_name.length() != 0)
           ) {
            if (goals_enabled) { cc.createSubGoal(context, "Creating type (instance or class) directly via explicitly given method and wire afterwards"); }
            try {
                results = Factory_Vector_SpecificScaffoldFactory_long_.construct(context);
                checkAndAppend(context, results, getSpecificScaffoldFactory_Class(context, type, type, method_name, allow_dynamic_type_check, true));
            } catch (InvalidClass icls) {
                if (goals_enabled) { cc.missed(context); }
                if (goals_enabled) { cc.doneSubGoals(context); }
                if (goals_enabled) { cc.missed(context); }
                InvalidClass.createAndThrow(context, icls, "Explicitly requested method or type is invalid");
                throw (InvalidClass) null;
            }
            if (goals_enabled) { cc.done(context); }
            if (goals_enabled) { cc.doneSubGoals(context); }
            if (goals_enabled) { cc.done(context); }
            return results;
        }

        // if factory name explicitly given, only try this
        if (factory_name != null && factory_name.length() != 0) {
            if (goals_enabled) { cc.createSubGoal(context, "Getting factory by explicit request"); }
            if (method_name == null || method_name.isEmpty()) {
                int pos = factory_name.indexOf('/');
                if (pos != -1) {
                    method_name = factory_name.substring(pos+1, factory_name.length());
                    factory_name = factory_name.substring(0, pos);
                }
            }
            try {
                Type factory_type = TypeManager.tryGet(context, factory_name);
                if (factory_type != null) {
                    results = Factory_Vector_SpecificScaffoldFactory_long_.construct(context);
                    checkAndAppend(context, results, getSpecificScaffoldFactory_Factory(context, type, factory_type, method_name, allow_dynamic_type_check, true));
                } else {
                    if (goals_enabled) { cc.missed(context); }
                    if (goals_enabled) { cc.doneSubGoals(context); }
                    if (goals_enabled) { cc.missed(context); }
                    InvalidFactory.createAndThrow(context, "Explicitly requested factory '%(factoryname)' does not exist", "factoryname", factory_name);
                    throw (InvalidFactory) null;
                }
            } catch (InvalidFactory ifac) {
                if (goals_enabled) { cc.missed(context); }
                if (goals_enabled) { cc.doneSubGoals(context); }
                if (goals_enabled) { cc.missed(context); }
                InvalidFactory.createAndThrow(context, ifac, "Explicitly requested factory is invalid");
                throw (InvalidFactory) null;
            }
            if (goals_enabled) { cc.done(context); }
            if (goals_enabled) { cc.doneSubGoals(context); }
            if (goals_enabled) { cc.done(context); }
            return results;
        }

        // if retriever name explicitly given, only try this
        if (retriever_name != null && retriever_name.length() != 0) {
            if (goals_enabled) { cc.createSubGoal(context, "Getting retriever '%(retriever)' by explicit request", "retriever", retriever_name); }
            if (method_name == null || method_name.isEmpty()) {
                int pos = retriever_name.indexOf('/');
                if (pos != -1) {
                    method_name = retriever_name.substring(pos+1, retriever_name.length());
                    retriever_name = retriever_name.substring(0, pos);
                }
            }
            try {
                Type retriever_type = TypeManager.tryGet(context, retriever_name);
                if (retriever_type != null) {
                    results = Factory_Vector_SpecificScaffoldFactory_long_.construct(context);
                    checkAndAppend(context, results, getSpecificScaffoldFactory_Retriever(context, type, retriever_type, method_name, allow_dynamic_type_check, true));
                } else {
                    if (goals_enabled) { cc.missed(context); }
                    if (goals_enabled) { cc.doneSubGoals(context); }
                    if (goals_enabled) { cc.missed(context); }
                    InvalidRetriever.createAndThrow(context, "Explicitly requested retriever '%(retrievername)' does not exist", "retrievername", retriever_name);
                    throw (InvalidRetriever) null;
                }
            } catch (InvalidRetriever iret) {
                if (goals_enabled) { cc.missed(context); }
                if (goals_enabled) { cc.doneSubGoals(context); }
                if (goals_enabled) { cc.missed(context); }
                InvalidRetriever.createAndThrow(context, iret, "Explicitly requested retriever is invalid");
                throw (InvalidRetriever) null;
            }
            if (goals_enabled) { cc.done(context); }
            if (goals_enabled) { cc.doneSubGoals(context); }
            if (goals_enabled) { cc.done(context); }
            return results;
        }

        if (goals_enabled) { cc.createSubGoal(context, "Checking type cache"); }
        try {
            results = (Vector_SpecificScaffoldFactory_long_) cache_by_type.get(type);
            if (results == null) {
                results = (Vector_SpecificScaffoldFactory_long_) preload_cache_by_type.get(type);
            }
        } catch (ClassCastException cce) { /* boo */ }
        if (results != null) {
            if (goals_enabled) { cc.done(context); }
            if (goals_enabled) { cc.doneSubGoals(context); }
            if (goals_enabled) { cc.done(context); }
            return results;
        }
        if (goals_enabled) { cc.missed(context); }

        results = Factory_Vector_SpecificScaffoldFactory_long_.construct(context);

        if (type instanceof TypeImpl_Aggregate) {

            if (goals_enabled) { cc.createNextGoal(context, "Got aggregate, preparing scaffolg"); }
            SpecificScaffoldFactory result = getSpecificScaffoldFactory_AggregateFactory (call_context, type, (TypeImpl_Aggregate) type, allow_dynamic_type_check);
            checkAndAppend(context, results, result);
            if (goals_enabled) { cc.done(context); }

        } else if (type instanceof TypeImpl && ((TypeImpl) type).getJavaClass(context).isArray()) {
            checkAndAppend(context, results, getSpecificScaffoldFactory_Array(context, type));
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success, using generic array factory"); }
        } else {

            Class targetclass = null;
            String typename = null;
            if (type instanceof JavaType) {
                JavaType jt = (JavaType) type;
                targetclass = jt.getJavaClass(context);
                typename = jt.getJavaClassName(context);
            } else if (type instanceof TypeParametrised) {
                TypeParametrised tp = (TypeParametrised) type;
                if (tp.getBaseType(context) instanceof JavaType) {
                    JavaType jt = (JavaType) tp.getBaseType(context);
                    targetclass = jt.getJavaClass(context);
                    typename = jt.getJavaClassName(context);
                } else {
                    cc.throwLimitation(context, "Type not an instance of 'JavaType', but of '%(class)'", "class", tp.getBaseType(context).getClass());
                    throw (ExceptionLimitation) null;
                }
            } else {
                cc.throwLimitation(context, "Type neither an instance of 'JavaType' nor of 'TypeParametrised', but of '%(class)'", "class", type.getClass());
                throw (ExceptionLimitation) null;
            }

            if (goals_enabled) { cc.createNextGoal(context, "Searching for and examining 'Factory_'"); }
            {
                boolean got_one = false;

                int pos = typename.lastIndexOf('.');
                SpecificScaffoldFactory result = null;
                Type factory_type = null;
                String name_to_try = null;
                if (pos == -1) {
                    try {
                        name_to_try = "Factory_" + typename;
                        factory_type = TypeManager.tryGet(context, name_to_try);
                        if (factory_type == null) {
                            name_to_try = "Class_Factory_" + typename;
                            factory_type = TypeManager.tryGet(context, name_to_try);
                        }
                        if (factory_type != null) {
                            result = getSpecificScaffoldFactory_Factory(context, type, factory_type, null, allow_dynamic_type_check, true);
                            checkAndAppend(context, results, result);
                            got_one = true;
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(factory)' via TypeManager", "factory", name_to_try); }
                        } else {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': class not found", "factory", name_to_try); }
                        }
                    } catch (InvalidFactory ifac) {
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': %(cause)", "factory", name_to_try, "cause", ifac); }
                    }
                } else {
                    String package_name = typename.substring(0, pos+1);
                    String class_name = typename.substring(pos+1);
                    try {
                        name_to_try = package_name + "Factory_" + class_name;
                        Class facclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, name_to_try);
                        factory_type = TypeManager.get(context, facclass);
                        result = getSpecificScaffoldFactory_Factory(context, type, factory_type, null, allow_dynamic_type_check, true);
                        checkAndAppend(context, results, result);
                        got_one = true;
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(factory)'", "factory", name_to_try); }
                    } catch (InvalidFactory ifac) {
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': %(cause)", "factory", name_to_try, "cause", ifac); }
                    } catch (ClassNotFoundException cnfe) {
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': %(cause)", "factory", name_to_try, "cause", cnfe); }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = package_name + "Class_Factory_" + class_name;
                            Class facclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, name_to_try);
                            factory_type = TypeManager.get(context, facclass);
                            result = getSpecificScaffoldFactory_Factory(context, type, factory_type, null, allow_dynamic_type_check, true);
                            checkAndAppend(context, results, result);
                            got_one = true;
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(factory)'", "factory", name_to_try); }
                        } catch (InvalidFactory ifac) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': %(cause)", "factory", name_to_try, "cause", ifac); }
                        } catch (ClassNotFoundException cnfe) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': %(cause)", "factory", name_to_try, "cause", cnfe); }
                        }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = package_name + "factories.Factory_" + class_name;
                            Class facclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, name_to_try);
                            factory_type = TypeManager.get(context, facclass);
                            result = getSpecificScaffoldFactory_Factory(context, type, factory_type, null, allow_dynamic_type_check, true);
                            checkAndAppend(context, results, result);
                            got_one = true;
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(factory)'", "factory", name_to_try); }
                        } catch (InvalidFactory ifac) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': %(cause)", "factory", name_to_try, "cause", ifac); }
                        } catch (ClassNotFoundException cnfe) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': %(cause)", "factory", name_to_try, "cause", cnfe); }
                        }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = package_name + "factories.Class_Factory_" + class_name;
                            Class facclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, name_to_try);
                            factory_type = TypeManager.get(context, facclass);
                            result = getSpecificScaffoldFactory_Factory(context, type, factory_type, null, allow_dynamic_type_check, true);
                            checkAndAppend(context, results, result);
                            got_one = true;
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(factory)'", "factory", name_to_try); }
                        } catch (InvalidFactory ifac) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': %(cause)", "factory", name_to_try, "cause", ifac); }
                        } catch (ClassNotFoundException cnfe) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': %(cause)", "factory", name_to_try, "cause", cnfe); }
                        }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = "Factory_" + class_name;
                            factory_type = TypeManager.tryGet(context, name_to_try);
                            if (factory_type != null) {
                                result = getSpecificScaffoldFactory_Factory(context, type, factory_type, null, allow_dynamic_type_check, true);
                                checkAndAppend(context, results, result);
                                got_one = true;
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(factory)' via TypeManager", "factory", name_to_try); }
                            } else {
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': class not found", "factory", name_to_try); }
                            }
                        } catch (InvalidFactory ifac) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': %(cause)", "factory", name_to_try, "cause", ifac); }
                        }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = "Class_Factory_" + class_name;
                            factory_type = TypeManager.tryGet(context, name_to_try);
                            if (factory_type != null) {
                                result = getSpecificScaffoldFactory_Factory(context, type, factory_type, null, allow_dynamic_type_check, true);
                                checkAndAppend(context, results, result);
                                got_one = true;
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(factory)' via TypeManager", "factory", name_to_try); }
                            } else {
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': class not found", "factory", name_to_try); }
                            }
                        } catch (InvalidFactory ifac) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(factory)': %(cause)", "factory", name_to_try, "cause", ifac); }
                        }
                    }
                }
                if (got_one) {
                    if (goals_enabled) { cc.done(context); }
                } else {
                    if (goals_enabled) { cc.missed(context); }
                }
            }

            if (goals_enabled) { cc.createNextGoal(context, "Searching for and examining 'Retriever_'"); }
            {
                boolean got_one = false;
                int pos = typename.lastIndexOf('.');
                SpecificScaffoldFactory result = null;
                Type retriever_type = null;
                String name_to_try = null;
                if (pos == -1) {
                    try {
                        name_to_try = "Retriever_" + typename;
                        retriever_type = TypeManager.tryGet(context, name_to_try);
                        if (retriever_type == null) {
                            name_to_try = "Class_Retriever_" + typename;
                            retriever_type = TypeManager.tryGet(context, name_to_try);
                        }
                        if (retriever_type != null) {
                            result = getSpecificScaffoldFactory_Retriever(context, type, retriever_type, null, allow_dynamic_type_check, true);
                            checkAndAppend(context, results, result);
                            got_one = true;
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(retriever)' via TypeManager", "retriever", name_to_try); }
                        } else {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': class not found", "retriever", name_to_try); }
                        }
                    } catch (InvalidRetriever iret) {
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': %(cause)", "retriever", name_to_try, "cause", iret); }
                    }
                } else {
                    String package_name = typename.substring(0, pos+1);
                    String class_name = typename.substring(pos+1);
                    try {
                        name_to_try = package_name + "Retriever_" + class_name;
                        Class facclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, name_to_try);
                        retriever_type = TypeManager.get(context, facclass);
                        result = getSpecificScaffoldFactory_Retriever(context, type, retriever_type, null, allow_dynamic_type_check, true);
                        checkAndAppend(context, results, result);
                        got_one = true;
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(retriever)'", "retriever", name_to_try); }
                    } catch (InvalidRetriever iret) {
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': %(cause)", "retriever", name_to_try, "cause", iret); }
                    } catch (ClassNotFoundException cnfe) {
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': %(cause)", "retriever", name_to_try, "cause", cnfe); }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = package_name + "Class_Retriever_" + class_name;
                            Class facclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, name_to_try);
                            retriever_type = TypeManager.get(context, facclass);
                            result = getSpecificScaffoldFactory_Retriever(context, type, retriever_type, null, allow_dynamic_type_check, true);
                            checkAndAppend(context, results, result);
                            got_one = true;
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(retriever)'", "retriever", name_to_try); }
                        } catch (InvalidRetriever iret) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': %(cause)", "retriever", name_to_try, "cause", iret); }
                        } catch (ClassNotFoundException cnfe) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': %(cause)", "retriever", name_to_try, "cause", cnfe); }
                        }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = package_name + "factories.Retriever_" + class_name;
                            Class facclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, name_to_try);
                            retriever_type = TypeManager.get(context, facclass);
                            result = getSpecificScaffoldFactory_Retriever(context, type, retriever_type, null, allow_dynamic_type_check, true);
                            checkAndAppend(context, results, result);
                            got_one = true;
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(retriever)'", "retriever", name_to_try); }
                        } catch (InvalidRetriever iret) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': %(cause)", "retriever", name_to_try, "cause", iret); }
                        } catch (ClassNotFoundException cnfe) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': %(cause)", "retriever", name_to_try, "cause", cnfe); }
                        }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = package_name + "factories.Class_Retriever_" + class_name;
                            Class facclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, name_to_try);
                            retriever_type = TypeManager.get(context, facclass);
                            result = getSpecificScaffoldFactory_Retriever(context, type, retriever_type, null, allow_dynamic_type_check, true);
                            checkAndAppend(context, results, result);
                            got_one = true;
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(retriever)'", "retriever", name_to_try); }
                        } catch (InvalidRetriever iret) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': %(cause)", "retriever", name_to_try, "cause", iret); }
                        } catch (ClassNotFoundException cnfe) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': %(cause)", "retriever", name_to_try, "cause", cnfe); }
                        }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = "Retriever_" + class_name;
                            retriever_type = TypeManager.tryGet(context, name_to_try);
                            if (retriever_type != null) {
                                result = getSpecificScaffoldFactory_Retriever(context, type, retriever_type, null, allow_dynamic_type_check, true);
                                checkAndAppend(context, results, result);
                                got_one = true;
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(retriever)' via TypeManager", "retriever", name_to_try); }
                            } else {
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': class not found", "retriever", name_to_try); }
                            }
                        } catch (InvalidRetriever iret) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': %(cause)", "retriever", name_to_try, "cause", iret); }
                        }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = "Class_Retriever_" + class_name;
                            retriever_type = TypeManager.tryGet(context, name_to_try);
                            if (retriever_type != null) {
                                result = getSpecificScaffoldFactory_Retriever(context, type, retriever_type, null, allow_dynamic_type_check, true);
                                checkAndAppend(context, results, result);
                                got_one = true;
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(retriever)' via TypeManager", "retriever", name_to_try); }
                            } else {
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': class not found", "retriever", name_to_try); }
                            }
                        } catch (InvalidRetriever iret) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(retriever)': %(cause)", "retriever", name_to_try, "cause", iret); }
                        }
                    }
                }
                if (got_one) {
                    if (goals_enabled) { cc.done(context); }
                } else {
                    if (goals_enabled) { cc.missed(context); }
                }
            }

            if (goals_enabled) { cc.createNextGoal(context, "Searching for and examining 'Class_'"); }
            {
                boolean got_one = false;
                int pos = typename.lastIndexOf('.');
                SpecificScaffoldFactory result = null;
                Type class_type = null;
                String name_to_try = null;
                if (    try_type_itself
                     && targetclass != null
                     && targetclass.isInterface() == false
                     && targetclass.isEnum() == false
                     && targetclass.isArray() == false
                     && targetclass.isPrimitive() == false
                   ) {
                    try {
                        result = getSpecificScaffoldFactory_Class(context, type, type, null, allow_dynamic_type_check, true);
                        checkAndAppend(context, results, result);
                        got_one = true;
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(class)' via TypeManager", "class", typename); }
                    } catch (InvalidClass iret) {
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(class)': %(cause)", "class", typename, "cause", iret); }
                    }
                }
                if (pos == -1) {
                    try {
                        name_to_try = "Class_" + typename;
                        class_type = TypeManager.tryGet(context, name_to_try);
                        if (class_type != null) {
                            result = getSpecificScaffoldFactory_Class(context, type, class_type, null, allow_dynamic_type_check, true);
                            checkAndAppend(context, results, result);
                            got_one = true;
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(class)' via TypeManager", "class", name_to_try); }
                        } else {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(class)': class not found", "class", name_to_try); }
                        }
                    } catch (InvalidClass iret) {
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(class)': %(cause)", "class", name_to_try, "cause", iret); }
                    }
                } else {
                    String package_name = typename.substring(0, pos+1);
                    String class_name = typename.substring(pos+1);
                    try {
                        name_to_try = package_name + "Class_" + class_name;
                        Class facclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, name_to_try);
                        class_type = TypeManager.get(context, facclass);
                        result = getSpecificScaffoldFactory_Class(context, type, class_type, null, allow_dynamic_type_check, true);
                        checkAndAppend(context, results, result);
                        got_one = true;
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(class)'", "class", name_to_try); }
                    } catch (InvalidClass iret) {
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(class)': %(cause)", "class", name_to_try, "cause", iret); }
                    } catch (ClassNotFoundException cnfe) {
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(class)': %(cause)", "class", name_to_try, "cause", cnfe); }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = package_name + "classes.Class_" + class_name;
                            Class facclass = com.sphenon.basics.cache.ClassCache.getClassForName(context, name_to_try);
                            class_type = TypeManager.get(context, facclass);
                            result = getSpecificScaffoldFactory_Class(context, type, class_type, null, allow_dynamic_type_check, true);
                            checkAndAppend(context, results, result);
                            got_one = true;
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(class)'", "class", name_to_try); }
                        } catch (InvalidClass iret) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(class)': %(cause)", "class", name_to_try, "cause", iret); }
                        } catch (ClassNotFoundException cnfe) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(class)': %(cause)", "class", name_to_try, "cause", cnfe); }
                        }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = "Class_" + class_name;
                            class_type = TypeManager.tryGet(context, name_to_try);
                            if (class_type != null) {
                                result = getSpecificScaffoldFactory_Class(context, type, class_type, null, allow_dynamic_type_check, true);
                                checkAndAppend(context, results, result);
                                got_one = true;
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(class)' via TypeManager", "class", name_to_try); }
                            } else {
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(class)': class not found", "class", name_to_try); }
                            }
                        } catch (InvalidClass iret) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(class)': %(cause)", "class", name_to_try, "cause", iret); }
                        }
                    }
                    if (got_one == false) {
                        try {
                            name_to_try = class_name;
                            class_type = type; // TypeManager.tryGet(context, name_to_try);
                            if (class_type != null) {
                                result = getSpecificScaffoldFactory_Class(context, type, class_type, null, allow_dynamic_type_check, true);
                                checkAndAppend(context, results, result);
                                got_one = true;
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Success while trying '%(class)' via TypeManager", "class", name_to_try); }
                            } else {
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(class)': class not found", "class", name_to_try); }
                            }
                        } catch (InvalidClass iret) {
                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No success while trying '%(class)': %(cause)", "class", name_to_try, "cause", iret); }
                        }
                    }
                }
                if (got_one) {
                    if (goals_enabled) { cc.done(context); }
                } else {
                    if (goals_enabled) { cc.missed(context); }
                }
            }

            if (goals_enabled) { cc.createNextGoal(context, "Examining constructors"); }
            {
                Constructor[] cons = targetclass.getConstructors();

                cons_test: for (int i=0; i<cons.length; i++) {
                    if ((cons[i].getAnnotation(OCPIgnore.class)) != null) { continue cons_test; }
                    if (Factory_Aggregate.debug_classloader) {
                        Factory_Aggregate.debugClassLoader("ScaffoldFactory targetclass", targetclass);
                        Factory_Aggregate.debugClassLoader("ScaffoldFactory ctor declaring of targetclass", cons[i].getDeclaringClass());
                        Factory_Aggregate.debugClassLoader("ScaffoldFactory ctor of targetclass", cons[i].getClass());
                    }
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Adding constructor..."); }
                    checkAndAppend(context, results, new SpecificScaffoldFactory_Constructor(context, type, cons[i]));
                }

            }
            if (goals_enabled) { cc.done(context); }

            if (targetclass.isEnum()) {
                if (goals_enabled) { cc.createNextGoal(context, "Enumeration, trying enum factory"); }
                {
                    Type factory_type = TypeManager.get(context, com.sphenon.engines.factorysite.factories.FactoryEnum.class);
                    checkAndAppend(context, results, new SpecificScaffoldFactory_FactoryEnum(context, type, factory_type, null, true));
                }
                if (goals_enabled) { cc.done(context); }
            }
        }

        cache_by_type.put(type, results);

        if (goals_enabled) { cc.doneSubGoals(context); }
        if (goals_enabled) { cc.done(context); }

        return results;
    }

    protected SpecificScaffoldFactory getSpecificScaffoldFactory_Factory (CallContext call_context, Type type, Type factory_type, String method_name, boolean allow_dynamic_type_check, boolean do_initialise) throws InvalidFactory {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        SpecificScaffoldFactory result = null;
        try {
            if (allow_dynamic_type_check == false) {
                result = (SpecificScaffoldFactory) cache_by_factory.get(factory_type);
                if (result == null) {
                    result = (SpecificScaffoldFactory) preload_cache_by_factory.get(factory_type);
                }
            }
        } catch (ClassCastException cce) { /* boo */ }
        if (result == null) {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Creating SpecificScaffoldFactory_Factory..."); }
            result = new SpecificScaffoldFactory_Factory(context, type, factory_type, method_name, allow_dynamic_type_check, do_initialise, null, null);
            if (allow_dynamic_type_check == false) {
                cache_by_factory.put(factory_type, result);
            }
        } else {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Using cached SpecificScaffoldFactory_Factory..."); }
        }
        return result;
    }

    protected SpecificScaffoldFactory getSpecificScaffoldFactory_Retriever (CallContext call_context, Type type, Type retriever_type, String method_name, boolean allow_dynamic_type_check, boolean do_initialise) throws InvalidRetriever {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        SpecificScaffoldFactory result = null;
        try {
            if (allow_dynamic_type_check == false) {
                result = (SpecificScaffoldFactory) cache_by_retriever.get(retriever_type);
                if (result == null) {
                    result = (SpecificScaffoldFactory) preload_cache_by_retriever.get(retriever_type);
                }
            }
        } catch (ClassCastException cce) { /* boo */ }
        if (result == null) {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Creating SpecificScaffoldFactory_Retriever..."); }
            result = new SpecificScaffoldFactory_Retriever(context, type, retriever_type, method_name, allow_dynamic_type_check, do_initialise, null, null);
            if (allow_dynamic_type_check == false) {
                cache_by_retriever.put(retriever_type, result);
            }
        } else {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Using cached SpecificScaffoldFactory_Retriever..."); }
        }
        return result;
    }

    protected SpecificScaffoldFactory getSpecificScaffoldFactory_Class (CallContext call_context, Type type, Type class_type, String method_name, boolean allow_dynamic_type_check, boolean do_initialise) throws InvalidClass {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        SpecificScaffoldFactory result = null;
        try {
            if (allow_dynamic_type_check == false) {
                result = (SpecificScaffoldFactory) cache_by_class.get(class_type);
                if (result == null) {
                    result = (SpecificScaffoldFactory) preload_cache_by_class.get(class_type);
                }
            }
        } catch (ClassCastException cce) { /* boo */ }
        if (result == null) {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Creating SpecificScaffoldFactory_Class..."); }
            result = new SpecificScaffoldFactory_Class(context, type, class_type, method_name, allow_dynamic_type_check, do_initialise, null, null);
            if (allow_dynamic_type_check == false) {
                cache_by_class.put(class_type, result);
            }
        } else {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Using cached SpecificScaffoldFactory_Class..."); }
        }
        return result;
    }

    protected SpecificScaffoldFactory getSpecificScaffoldFactory_AggregateFactory (CallContext call_context, Type type, TypeImpl_Aggregate aggregate_type, boolean allow_dynamic_type_check) throws InvalidFactory {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        SpecificScaffoldFactory result = null;
        try {
            if (allow_dynamic_type_check == false) {
                result = (SpecificScaffoldFactory) cache_by_factory_aggregate.get(aggregate_type);
                if (result == null) {
                    result = (SpecificScaffoldFactory) preload_cache_by_factory_aggregate.get(aggregate_type);
                }
            }
        } catch (ClassCastException cce) { /* boo */ }
        if (result == null) {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Creating SpecificScaffoldFactory_AggregateFactory..."); }
            result = new SpecificScaffoldFactory_AggregateFactory(context, type, aggregate_type, allow_dynamic_type_check);
            if (allow_dynamic_type_check == false) {
                cache_by_factory_aggregate.put(aggregate_type, result);
            }
        } else {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Using cached SpecificScaffoldFactory_AggregateFactory..."); }
        }
        return result;
    }

    protected SpecificScaffoldFactory getSpecificScaffoldFactory_Array (CallContext context, Type type) throws InvalidFactory {
        CustomaryContext cc = CustomaryContext.create((Context)context);

        SpecificScaffoldFactory result = null;
        try {
            result = (SpecificScaffoldFactory) cache_by_array.get(type);
            if (result == null) {
                result = (SpecificScaffoldFactory) preload_cache_by_array.get(type);
            }
        } catch (ClassCastException cce) { /* boo */ }
        if (result == null) {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Creating SpecificScaffoldFactory_Array..."); }
            result = new SpecificScaffoldFactory_Array(context, type);
            cache_by_array.put(type, result);
        } else {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Using cached SpecificScaffoldFactory_Array..."); }
        }
        return result;
    }

    public FactorySiteListener getListener (CallContext call_context, String listener) {
        if (listener == null || listener.length() == 0) return null;
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        String[] listeners = listener.split(":");
        if (listeners.length > 1) {
            FactorySiteListener[] fsls = new FactorySiteListener[listeners.length];
            for (int l=0; l<listeners.length; l++) {
                fsls[l] = getListener (context, listeners[l]);
            }
            return new FactorySiteMultiListener(context, fsls);
        }

        Class targetclass = null;
        Type type = null;
        try {
            type = TypeManager.get(context, listener);
        } catch (NoSuchClass nsc) {
            cc.throwPreConditionViolation(context, nsc, "FactorySiteListener '%(listener)' not found", "listener", listener);
            throw (ExceptionPreConditionViolation) null;
        }
        if (! type.isA(context, TypeManager.get(context, FactorySiteListener.class))) {
            cc.throwPreConditionViolation(context, "FactorySiteListener '%(listener)' is not a subclass of 'FactorySiteListener'", "listener", listener);
            throw (ExceptionPreConditionViolation) null;
        }
        if (! (type instanceof TypeImpl)) {
            cc.throwLimitation(context, "Type not an instance of 'TypeImpl', but of '%(class)'", "class", type.getClass());
            throw (ExceptionLimitation) null;
        }
        TypeImpl ti = (TypeImpl) type;
        targetclass = ti.getJavaClass(context);
        Constructor[] conss = targetclass.getConstructors();
        Constructor cons = null;
        boolean found = false;
        cons_test: for (int i=0; !found && i<conss.length; i++) {
            cons = conss[i];
            Class[] parameter_classes = cons.getParameterTypes();;
            if (    parameter_classes.length == 1
                 && parameter_classes[0].getName().equals("com.sphenon.basics.context.CallContext")
                )
            {   found = true;
            }
        }
        if (! found) {
            cc.throwPreConditionViolation(context, "FactorySiteListener '%(listener)' does not provide an adequate constructor '(CallContext, Scaffold)'", "listener", listener);
            throw (ExceptionPreConditionViolation) null;
        }

        Object[] actual_parameters = new Object[1];
        actual_parameters[0] = context;
        try {
            return (FactorySiteListener) cons.newInstance(actual_parameters);
        } catch (InstantiationException e) {
            cc.throwPreConditionViolation(context, e, "Cannot instantiate FactorySiteListener '%(listener)', class is abstract", "listener", listener);
            throw (ExceptionPreConditionViolation) null;
        } catch (IllegalAccessException e) {
            cc.throwPreConditionViolation(context, e, "Cannot instantiate FactorySiteListener '%(listener)', constructor is inaccessible", "listener", listener);
            throw (ExceptionPreConditionViolation) null;
        } catch (IllegalArgumentException e) {
            cc.throwPreConditionViolation(context, e, "Cannot instantiate FactorySiteListener '%(listener)', signature mismatch or unwrapping or method invocation", "listener", listener);
            throw (ExceptionPreConditionViolation) null;
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof java.lang.RuntimeException) {
                java.lang.RuntimeException re = null;
                try {
                    re = (java.lang.RuntimeException) e.getTargetException();
                } catch (ClassCastException ee) {
                    cc.throwImpossibleState(context, ee, "Java 'instanceof' operator reports castability to 'java.lang.RuntimeException', but cast failed");
                }
                throw re;
            }
            if (e.getTargetException() instanceof java.lang.Error) {
                try {
                    throw (java.lang.Error) e.getTargetException();
                } catch (ClassCastException ee) {
                    cc.throwImpossibleState(context, "Java 'instanceof' operator reports castability to 'java.lang.Error', but cast failed");
                }
            }
            cc.throwPreConditionViolation(context, e.getTargetException(), "Cannot instantiate FactorySiteListener '%(listener)', constructor throwed an exception", "listener", listener);
            throw (ExceptionPreConditionViolation) null;
        }
    }

    protected Scaffold getMatchingFactoryAndCreate (CallContext call_context, Vector_SpecificScaffoldFactory_long_ specific_scaffold_factories, Type type, Vector_ScaffoldParameter_long_ parameters, Map_TypeOrNull_String_ parameters_by_name, boolean allow_dynamic_type_check, boolean allow_missing_arguments, String listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]>  pre_build_scripts, Vector<String[]>  post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidFactory, InvalidRetriever, InvalidClass {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        if (goals_enabled) { cc.createGoal(context, "Looking for matching factory for '%(type)'", "type", type); }

        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Checking factories..."); }

        Vector<MatchResult> match_results = null;

        for (Iterator_SpecificScaffoldFactory_ iterssf = specific_scaffold_factories.getNavigator(context);
             iterssf.canGetCurrent(context);
             iterssf.next(context)) {
            SpecificScaffoldFactory ssf = iterssf.tryGetCurrent(context);

            MatchResult match_result;

            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Checking factory..."); }

            if ((match_result = ssf.isMatching(context, type, parameters, parameters_by_name, allow_missing_arguments)).successful == false) {
                if (match_results == null) { match_results = new Vector<MatchResult>(); }
                match_results.add(match_result);
            } else {

                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Found matching factory, creating..."); }

                Vector_ScaffoldParameter_long_ adopted_parameters = Factory_Vector_ScaffoldParameter_long_ .construct(context);

                HashMap<String,ScaffoldParameter> sphash = new HashMap<String,ScaffoldParameter>();
                for (ScaffoldParameter sp : parameters.getIterable_ScaffoldParameter_(context)) {
                    sphash.put(sp.getName(context), sp);
                }

                boolean empty_used = false;
                if (match_result.parameters_to_be_set != null) {
                    for (int i=0; i<match_result.parameters_to_be_set.getSize(context); i++) {
                        ParEntry pe = match_result.parameters_to_be_set.tryGet(context, i);
                        ScaffoldParameter sp = null;

                        sp = sphash.get(pe.name);
                        if (sp == null && empty_used == false) {
                            sp = sphash.get("");
                            empty_used = true;
                        }

                        if (sp == null && allow_missing_arguments == false) {
                            CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, "While matching parameters, no parameter named '%(name)' was found in internal collection", "name", pe.name);
                            throw (ExceptionAssertionProvedFalse) null; // compiler insists
                        }
                        sp.refineType(context, pe.type); 
                        adopted_parameters.append(context, sp);
                    }
                }

                try {
                    Scaffold scaffold = ssf.create(context, adopted_parameters, match_result, getListener(context, listener), is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
                    if (Factory_Aggregate.debug_classloader) {
                        Factory_Aggregate.debugClassLoader("ScaffoldFactory", ssf.getClass());
                        Factory_Aggregate.debugClassLoader("Scaffold", scaffold.getClass());
                    }
                    if (goals_enabled) { cc.done(context); }
                    return scaffold;
                } catch (InvalidFactory ifac) {
                    if (goals_enabled) { cc.missed(context); }
                    throw ifac;
                } catch (InvalidRetriever iret) {
                    if (goals_enabled) { cc.missed(context); }
                    throw iret;
                } catch (InvalidClass icla) {
                    if (goals_enabled) { cc.missed(context); }
                    throw icla;
                }
            }
        }
        if (goals_enabled) { cc.missed(context); }
        MessageText[] mts = new MessageText[(match_results == null ? 0 : match_results.size()) + 1];
        int midx = 0;
        mts[midx++] = MessageText.create(context, "Cannot create Scaffold, no matching factory (or constructor) for %(type) found", "type", type);
        if (match_results != null) {
            for (MatchResult match_result : match_results) {
                mts[midx++] = MessageText.create(context, "['%(scaffold)' match: %(message)] ", "scaffold", match_result.specific_scaffold_factory, "message", match_result.message_text);
            }
        }
        MessageText mt = MessageTextSequence.createSequence(context, mts);
        cc.throwConfigurationError(context, mt);
        throw (ExceptionConfigurationError) null;
    }

    static protected String cache_file = null;
    static protected String cache_type_include = null;
    static protected String cache_type_exclude = null;

    static public void saveCacheOnExit(CallContext context) {
        TypeManager.defer_save_cache = true;

        cache_file = config.get(context, "CacheFile", (String) null);
        cache_type_include = config.get(context, "Cache.Type.Include", (String) null);
        cache_type_exclude = config.get(context, "Cache.Type.Exclude", (String) null);

        java.lang.Runtime.getRuntime().addShutdownHook(new Thread() { public void run() { saveCache(RootContext.getDestructionContext()); } });
    }

    static public void saveCache(CallContext context) {
        File f = new File(cache_file);
        String type_include = cache_type_include;
        String type_exclude = cache_type_exclude;
        try {
            f.setWritable(true);
            FileOutputStream fos = new FileOutputStream(f);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(bw);

            printCache(context, pw, "PreloadCachePrimitives", preload_cache_by_type, type_exclude, type_include, true, false);
            printCache(context, pw, "PreloadCache", preload_cache_by_type, type_exclude, type_include, false, true);
            printCache(context, pw, "CachePrimitives", cache_by_type, type_exclude, type_include, true, false);
            printCache(context, pw, "Cache", cache_by_type, type_exclude, type_include, false, true);

            pw.close();
            bw.close();
            osw.close();
            fos.close();

        } catch (FileNotFoundException fnfe) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, fnfe, "Cannot write to file '%(filename)'", "filename", f.getPath());
            throw (ExceptionPreConditionViolation) null; // compiler insists
        } catch (UnsupportedEncodingException uee) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, uee, "Cannot write to file '%(filename)'", "filename", f.getPath());
            throw (ExceptionPreConditionViolation) null; // compiler insists
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ioe, "Cannot write to file '%(filename)'", "filename", f.getPath());
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        if (TypeManager.save_cache_deferred) {
            TypeManager.defer_save_cache = false;
            TypeManager.saveCache(context);
        }
    }

    static protected void printCache(CallContext context, PrintWriter pw, String cache_name, Map<Type,Object> cache, String type_exclude, String type_include, boolean primitives, boolean non_primitives) {
        pw.print("com.sphenon.engines.factorysite.ScaffoldFactory." + cache_name + "=");

        Map<String,Map<Type,Vector<SpecificScaffoldFactory>>> vssfbtbtc = new HashMap<String,Map<Type,Vector<SpecificScaffoldFactory>>>();

        for (Object ome : cache.entrySet()) {
            Map.Entry me = (Map.Entry) ome;
            Type type = (Type) (me.getKey());
            String tname = type.getId(context);

            if ((tname.matches("Java::java.lang.(Boolean|Byte|Character|Short|Integer|Long|Float|Double|String)") ? primitives : non_primitives) == false) { continue; }

            if (    (    type_include == null
                      || tname.matches(type_include) == true
                    )
                 && (    type_exclude == null
                      || tname.matches(type_exclude) == false
                    )
               ) {
                Vector_SpecificScaffoldFactory_long_ vssfl = (Vector_SpecificScaffoldFactory_long_) me.getValue();
                for (SpecificScaffoldFactory ssf : vssfl.getIterable_SpecificScaffoldFactory_(context)) {
                    String type_context = ssf.getTypeContext(context);
                    Map<Type,Vector<SpecificScaffoldFactory>> vssfbt = vssfbtbtc.get(type_context);
                    if (vssfbt == null) {
                        vssfbt = new HashMap<Type,Vector<SpecificScaffoldFactory>>();
                        vssfbtbtc.put(type_context, vssfbt);
                    }
                    Vector<SpecificScaffoldFactory> vssf = vssfbt.get(type);
                    if (vssf == null) {
                        vssf = new Vector<SpecificScaffoldFactory>();
                        vssfbt.put(type, vssf);
                    }
                    vssf.add(ssf);
                }
            }
        }

        boolean firsttc = true;
        for (String type_context : vssfbtbtc.keySet()) {

            pw.print((firsttc ? "" : "!") + Encoding.recode(context, type_context, Encoding.UTF8, Encoding.URI) + ":");
            firsttc = false;

            boolean firstentry = true;
            Map<Type,Vector<SpecificScaffoldFactory>> vssfbt = vssfbtbtc.get(type_context);
            for (Type type : vssfbt.keySet()) {
                String tname = type.getId(context);

                pw.print((firstentry ? "" : ";") + Encoding.recode(context, tname, Encoding.UTF8, Encoding.URI) + "=");
                firstentry = false;

                boolean first = true;
                Vector<SpecificScaffoldFactory> vssf = vssfbt.get(type);
                for (SpecificScaffoldFactory ssf : vssf) {

                    if ( ! first) { pw.print(","); }
                    first = false;
                    try {
                        String bs = ssf.getBuildString(context);
                        if (bs != null) {
                            pw.print(Encoding.recode(context, bs, Encoding.UTF8, Encoding.URI));
                        } else {
                            pw.print("NOINIT");
                        }
                    } catch (InvalidFactory ifac) {
                        pw.print("ERROR");
                        ifac.printStackTrace();
                    } catch (InvalidRetriever iret) {
                        pw.print("ERROR");
                        iret.printStackTrace();
                    } catch (InvalidClass icla) {
                        pw.print("ERROR");
                        icla.printStackTrace();
                    }
                }
            }
        }
        pw.println("");
    }

    static protected RegularExpression cache_entry_re1 = new RegularExpression("([^!:]+):([^!]+)(?:!?)");
    static protected RegularExpression cache_entry_re2 = new RegularExpression("([^!:;=]+)=([^!:;]+)(?:;?)");

    static public void loadCache(CallContext context, boolean primitives) {
        if (primitives) {
            // loadCache(context, "PreloadCachePrimitives", preload_cache_by_type);
            loadCache(context, "CachePrimitives", cache_by_type);
        } else {
            // loadCache(context, "PreloadCache", preload_cache_by_type);
            loadCache(context, "Cache", cache_by_type);
        }
    }

    static protected void loadCache(CallContext context, String cache_name, Map<Type,Object> cache) {
        String cache_property = config.get(context, cache_name, (String) null);
        if (cache_property == null) { return; }

        StopWatch stop_watch = StopWatch.optionallyCreate (context, "com.sphenon.engines.factorysite.ScaffoldFactory", "Cache", Notifier.SELF_DIAGNOSTICS);
        if (stop_watch != null) {
            stop_watch.start(context, "load cache begin");
        }

        Matcher m1 = cache_entry_re1.getMatcher(context, cache_property);
        while (m1.find()) {
            String tc = Encoding.recode(context, m1.group(1), Encoding.URI, Encoding.UTF8);
            String tc_entry = m1.group(2);

            if (TypeManager.isValidSearchPath(context, tc) == false) {
                if ((notification_level & Notifier.MONITORING) != 0) { NotificationContext.sendCaution(context, "Search path '%(searchpath)' not configured (scaffold cache entry skipped)", "searchpath", tc); }
            } else {

                Matcher m2 = cache_entry_re2.getMatcher(context, tc_entry);
                while (m2.find()) {
                    String type_id = Encoding.recode(context, m2.group(1), Encoding.URI, Encoding.UTF8);

                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Loading cache type '%(type)'", "type", type_id); }

                    Type type = TypeManager.tryGetById(context, type_id);
                    if (type == null) {
                        if ((notification_level & Notifier.OBSERVATION) != 0) { NotificationContext.sendNotice(context, "While loading scaffold cache, type '%(type)' was not found", "type", type); }
                        continue;
                    }

                    Vector_SpecificScaffoldFactory_long_ vssfl = null;
                    try {
                        vssfl = (Vector_SpecificScaffoldFactory_long_) cache.get(type);
                    } catch (ClassCastException cce) { /* boo */ }
                    if (vssfl == null) {
                        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "No entry for type '%(type)' yet, creating...", "type", type.getName(context)); }
                        vssfl = Factory_Vector_SpecificScaffoldFactory_long_.construct(context);
                        cache.put(type, vssfl);
                    }

                    String[] build_texts = m2.group(2).split(",");
                    for (String build_text_enc : build_texts) {

                        String build_text = Encoding.recode(context, build_text_enc, Encoding.URI, Encoding.UTF8);
                        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Loading '%(buildtext)'", "buildtext", build_text); }
                        int pos = build_text.indexOf('|');
                        String ssf_type = (pos == -1 ? build_text : build_text.substring(0, pos));
                        SpecificScaffoldFactory ssf = null;
                        if (ssf_type.equals("Factory")) {
                            ssf = SpecificScaffoldFactory_Factory.buildFromString(context, build_text);
                        } else if (ssf_type.equals("FactoryEnum")) {
                            ssf = SpecificScaffoldFactory_FactoryEnum.buildFromString(context, build_text);
                        } else if (ssf_type.equals("Retriever")) {
                            ssf = SpecificScaffoldFactory_Retriever.buildFromString(context, build_text);
                        } else if (ssf_type.equals("Class")) {
                            ssf = SpecificScaffoldFactory_Class.buildFromString(context, build_text);
                        } else if (ssf_type.equals("Constructor")) {
                            ssf = SpecificScaffoldFactory_Constructor.buildFromString(context, build_text);
                        } else if (ssf_type.equals("AggregateFactory")) {
                            ssf = SpecificScaffoldFactory_AggregateFactory.buildFromString(context, build_text);
                        } else if (ssf_type.equals("Array")) {
                            ssf = SpecificScaffoldFactory_Array.buildFromString(context, build_text);
                        } else if (ssf_type.equals("ERROR")) {
                            if ((notification_level & Notifier.MONITORING) != 0) { NotificationContext.sendCaution(context, "Erraneous entry in factory site cache"); }
                        } else if (ssf_type.equals("NOINIT")) {
                            if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Uninitialized entry in factory site cache"); }
                        } else {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, "While loading scaffold cache, an unrecognized entry was found '%(entry)' (was expecting Factory|Retriever|Class|Constructor|AggregateFactory, but not '%(got)')", "entry", build_text, "got", ssf_type);
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        if (ssf != null) {
                            vssfl.append(context, ssf);
                            if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Loading done."); }
                        }
                    }
                }
            }
        }
            
        if (stop_watch != null) {
            stop_watch.stop(context, "load cache end");
        }
    }
    
    /* ---------------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------------- */

    static public void dumpCache(CallContext context) {
        String type_exclude = config.get(context, "Cache.Type.Exclude", (String) null);
        String type_include = config.get(context, "Cache.Type.Include", (String) null);

        dumpCache(context, "PreloadCachePrimitives", preload_cache_by_type, type_exclude, type_include, true, false);
        dumpCache(context, "PreloadCache", preload_cache_by_type, type_exclude, type_include, false, true);
        dumpCache(context, "CachePrimitives", cache_by_type, type_exclude, type_include, true, false);
        dumpCache(context, "Cache", cache_by_type, type_exclude, type_include, false, true);
    }

    static protected void dumpCache(CallContext context, String cache_name, Map<Type,Object> cache, String type_exclude, String type_include, boolean primitives, boolean non_primitives) {
        System.err.println("Cache " + cache_name + ":");

        for (Object ome : cache.entrySet()) {
            Map.Entry me = (Map.Entry) ome;
            String tname = ((Type)(me.getKey())).getId(context);

            if ((tname.matches("Java::java.lang.(Boolean|Byte|Character|Short|Integer|Long|Float|Double|String)") ? primitives : non_primitives) == false) { continue; }

            if (    (    type_include == null
                      || tname.matches(type_include) == true
                    )
                 && (    type_exclude == null
                      || tname.matches(type_exclude) == false
                    )
               ) {
                System.err.println("    " + tname);
                Vector_SpecificScaffoldFactory_long_ vssfl = (Vector_SpecificScaffoldFactory_long_) me.getValue();
                for (SpecificScaffoldFactory ssf : vssfl.getIterable_SpecificScaffoldFactory_(context)) {
                    try {
                        String bs = ssf.getBuildString(context);
                        if (bs != null) {
                            System.err.println("        " + bs);
                            Vector<ParEntry> fsps = ssf.getFormalScaffoldParameters(context);
                            if (fsps != null) {
                                for (ParEntry fsp : fsps) {
                                    String n = fsp.name == null ? "??" : fsp.name;
                                    String t = fsp.type == null ? "??" : fsp.type.getName(context);
                                    System.err.println("            " + n + " - " + t + " - " + (fsp.is_optional ? "(opt)" : ""));
                                }
                            }
                            Type ctoc = ssf.getComponentTypeOfCollection(context);
                            String ctocs = ctoc == null ? null : ctoc.getName(context);
                            if (ctocs != null) {
                                System.err.println("            [ " + ctocs + "... ]");
                            }
                        } else {
                            // NOINIT
                        }
                    } catch (InvalidFactory ifac) {
                        // ERROR
                    } catch (InvalidRetriever iret) {
                        // ERROR
                    } catch (InvalidClass icla) {
                        // ERROR
                    } catch (Throwable t) {
                        // ERROR
                    }
                }
            }
        }
    }

    /* ---------------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------------- */
}
