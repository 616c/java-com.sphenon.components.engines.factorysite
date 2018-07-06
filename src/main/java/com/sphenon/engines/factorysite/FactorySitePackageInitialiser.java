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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.expression.*;

import com.sphenon.engines.factorysite.factories.*;

import java.util.Hashtable;

public class FactorySitePackageInitialiser {
    static protected boolean initialised = false;

    static public synchronized void initialise (CallContext context) {
        
        if (initialised == false) {
            initialised = true;
            Configuration.loadDefaultProperties(context, com.sphenon.engines.factorysite.FactorySitePackageInitialiser.class);

            com.sphenon.basics.system.SystemPackageInitialiser.initialise(context);
            com.sphenon.basics.metadata.MetaDataPackageInitialiser.initialise(context);
            com.sphenon.basics.javacode.JavaCodePackageInitialiser.initialise(context);

            BootstrapAggregator.install(context);

            TypeManager.registerSpecificTypeFactory(context, new SpecificTypeFactory_Aggregate(context));

            FactorySitePreloader.initialise(context);

            if (getConfiguration(context).get(context, "SaveCacheOnExit", false)) {
                ScaffoldFactory.saveCacheOnExit(context);
            }
            if (getConfiguration(context).get(context, "SaveOCPFinderCacheOnExit", false)) {
                OCPFinder.saveCacheOnExit(context);
            }
            if (getConfiguration(context).get(context, "SaveAggregateFactoryCacheOnExit", false)) {
                Factory_Aggregate.saveAggregateFactoryCacheOnExit(context);
            }

            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_TOCP(context));

            AggregateRegistry.register(context, "oorl://Class<Java>/com/sphenon/basics/interaction/GenericChoiceSetRetriever", Object.class, com.sphenon.basics.interaction.aggregateroles.BLCRetriever.class);
        }
    }

    static protected boolean initialised_cache_rebuild = false;
    static protected boolean initialised_cache_primitives_rebuild = false;
    static protected boolean initialised_ocpfinder = false;

    static public synchronized void initialise (CallContext context, String phase) {
        
        if (initialised == false) {
            // in that case, no initialisation in the primary phase took place
            return;
        }

        if (initialised_ocpfinder == false) {
            initialised_ocpfinder = true;

            if (getConfiguration(context).get(context, "LoadOCPFinderCache", false)) {
                OCPFinder.loadCaches(context);
            }
        }

        if (initialised_cache_primitives_rebuild == false && phase.equals("ScaffoldCacheRebuildPrimitives")) {
            initialised_cache_primitives_rebuild = true;

            if (getConfiguration(context).get(context, "LoadCache", false)) {
                ScaffoldFactory.loadCache(context, true);
            }
        }

        if (initialised_cache_rebuild == false && phase.equals("ScaffoldCacheRebuild")) {
            initialised_cache_rebuild = true;

            if (getConfiguration(context).get(context, "LoadCache", false)) {
                ScaffoldFactory.loadCache(context, false);
            }
        }
    }

    static public void defineAliases (CallContext context, Configuration config) {
        String entry = config.get(context, "FactorySiteAliases", (String) null);
        if (entry == null) { return; }
        String entries[] = entry.split("=",2);
        defineAliases(context, entries[0], entries[1]);
    }

    static public void defineAliases (CallContext context, String type_context_id, String named_aliases) {
        if (named_aliases == null || named_aliases.length() == 0) { return; }
        int last_pos = -1;
        int pos = -1;
        Hashtable abbreviations = new Hashtable();
        while ((pos = named_aliases.indexOf(",", last_pos + 1)) != -1) {
            defineAlias(context, type_context_id, named_aliases.substring(last_pos + 1, pos), abbreviations);
            last_pos = pos;
        }
        defineAlias(context, type_context_id, named_aliases.substring(last_pos + 1), abbreviations);
    }

    static protected void defineAlias(CallContext context, String type_context_id, String named_alias, Hashtable abbreviations) {
        if (named_alias == null || named_alias.length() == 0) { return; }

        String[] keyval = named_alias.split("=");
        if (    keyval.length != 2
             || keyval[0] == null || keyval[0].length() == 0
             || keyval[1] == null || keyval[1].length() == 0
           ) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, "Invalid alias definition entry '%(entry)'", "entry", named_alias);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        if (keyval[0].charAt(0) == '*') {
            abbreviations.put(keyval[0].substring(1), keyval[1]);
            return;
        }

        boolean  at_sign    = (keyval[0].indexOf("@") != -1);
        String[] keys       = keyval[0].split(at_sign ? "@" : "#");
        String[] expansions = keyval[1].split("\\|");

        if (    keys.length != 2
             || keys[0] == null || keys[0].length() == 0
             || keys[1] == null || keys[1].length() == 0
           ) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, "Invalid alias definition entry '%(entry)'", "entry", named_alias);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        BuildTextExpansion expansion = new BuildTextExpansion(context);

        if (expansions.length >=  1 && expansions[0] != null && expansions[0].length() != 0) { expansion.setName (context, expansions[0]); }
        if (expansions.length >=  2 && expansions[1] != null && expansions[1].length() != 0) { expansion.setTypeName (context, expansions[1]); }
        if (expansions.length >=  3 && expansions[2] != null && expansions[2].length() != 0) { expansion.setFactoryName (context, expansions[2]); }
        if (expansions.length >=  4 && expansions[3] != null && expansions[3].length() != 0) { expansion.setRetrieverName (context, expansions[3]); }
        if (expansions.length >=  5 && expansions[4] != null && expansions[4].length() != 0) { expansion.setMethodName (context, expansions[4]); }
        if (expansions.length >=  6 && expansions[5] != null && expansions[5].length() != 0) { expansion.setIsContainer (context, expansions[5].equals("CONTAINER")); }
        if (expansions.length >=  7 && expansions[6] != null && expansions[6].length() != 0) { expansion.setPass(context, Integer.parseInt(expansions[6])); }
        if (expansions.length >=  8 && expansions[7] != null && expansions[7].length() != 0) { expansion.setAllowDynamicTypeCheck(context, expansions[7].equals("DYNAMIC")); }
        if (expansions.length >=  9 && expansions[8] != null && expansions[8].length() != 0) { expansion.setAllowMissingArguments(context, expansions[8].equals("MISSING")); }
        if (expansions.length >= 10 && expansions[9] != null && expansions[9].length() != 0) { expansion.setOID(context, expansions[9]); }

        String abbrev_expand;

        String type_names  = keys[1];
        abbrev_expand = (String) abbreviations.get(type_names);
        if (abbrev_expand != null) { type_names = abbrev_expand; }

        String alias_names = keys[0];
        abbrev_expand = (String) abbreviations.get(alias_names);
        if (abbrev_expand != null) { alias_names = abbrev_expand; }

        try {
            for (String type_name : type_names.split("\\+")) {
                for (String alias_name : alias_names.split("\\+")) {
                    if (at_sign) {
                        FactorySiteTextBased.registerAliasForScopeType(context, type_context_id, TypeManager.get(context, type_name), alias_name, expansion);
                    } else {
                        FactorySiteTextBased.registerAliasForItemType(context, type_context_id, TypeManager.get(context, type_name), alias_name, expansion);
                    }
                }
            }
        } catch (NoSuchClass nsc) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, nsc, "Invalid alias definition entry '%(entry)'", "entry", named_alias);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    static public void defineDefaults (CallContext context, Configuration config) {
        String entry = config.get(context, "FactorySiteDefaults", (String) null);
        if (entry == null) { return; }
        String entries[] = entry.split("=",2);
        defineDefaults(context, entries[0], entries[1]);
    }

    static public void defineDefaults (CallContext context, String type_context_id, String defaults) {
        if (defaults == null || defaults.length() == 0) { return; }
        int last_pos = -1;
        int pos = -1;
        Hashtable abbreviations = new Hashtable();
        while ((pos = defaults.indexOf(",", last_pos + 1)) != -1) {
            defineDefault(context, type_context_id, defaults.substring(last_pos + 1, pos), abbreviations);
            last_pos = pos;
        }
        defineDefault(context, type_context_id, defaults.substring(last_pos + 1), abbreviations);
    }

    static protected void defineDefault(CallContext context, String type_context_id, String default_def, Hashtable abbreviations) {
        if (default_def == null || default_def.length() == 0) { return; }

        String[] keyval = default_def.split("=",2);
        if (    keyval.length != 2
             || keyval[0] == null || keyval[0].length() == 0
             || keyval[1] == null || keyval[1].length() == 0
           ) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, "Invalid default definition entry '%(entry)'", "entry", default_def);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        if (keyval[0].charAt(0) == '*') {
            abbreviations.put(keyval[0].substring(1), keyval[1]);
            return;
        }

        boolean  at_sign    = (keyval[0].indexOf("@") != -1);
        String[] keys       = keyval[0].split("@");

        if (    keys.length != 2
             || keys[0] == null || keys[0].length() == 0
             || keys[1] == null || keys[1].length() == 0
           ) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, "Invalid default definition entry '%(entry)'", "entry", default_def);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        BuildText bt = null;
        try {
            bt = (BuildText) Locator.resolve(context, keyval[1], null, "BuildText");
        } catch (InvalidLocator il) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, il, "Invalid default definition entry '%(entry)'", "entry", default_def);
            throw (ExceptionConfigurationError) null; // compiler insists
        } catch (ClassCastException cce) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, cce, "Invalid default definition entry '%(entry)'", "entry", default_def);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        String abbrev_expand;

        String type_names  = keys[1];
        abbrev_expand = (String) abbreviations.get(type_names);
        if (abbrev_expand != null) { type_names = abbrev_expand; }

        String default_names = keys[0];
        abbrev_expand = (String) abbreviations.get(default_names);
        if (abbrev_expand != null) { default_names = abbrev_expand; }

        try {
            for (String type_name : type_names.split("\\+")) {
                for (String default_name : default_names.split("\\+")) {
                    FactorySiteTextBased.registerDefaultForScopeType(context, type_context_id, TypeManager.get(context, type_name), default_name, bt);
                }
            }
        } catch (NoSuchClass nsc) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, nsc, "Invalid default definition entry '%(entry)'", "entry", default_def);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    static protected Configuration config;
    static public Configuration getConfiguration (CallContext context) {
        if (config == null) {
            config = Configuration.create(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite");
        }
        return config;
    }
}

