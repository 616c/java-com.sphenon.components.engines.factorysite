package com.sphenon.engines.factorysite.factories;

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
import com.sphenon.basics.tracking.*;
import com.sphenon.basics.debug.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.message.classes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.metadata.tplinst.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.system.*;
import static com.sphenon.basics.system.StringUtilities.join;
import com.sphenon.basics.accessory.*;
import com.sphenon.basics.factory.returncodes.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.javacode.*;
import com.sphenon.basics.javacode.classes.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.tplinst.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.graph.files.factories.*;
import com.sphenon.formats.json.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.xml.*;
import com.sphenon.engines.factorysite.json.*;
import com.sphenon.engines.factorysite.yaml.*;

import com.sphenon.engines.aggregator.annotations.*;

import java.io.*;

import java.util.regex.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

public class Factory_Aggregate
{
    static public boolean crash_debug = false;


    static final public Class _class = Factory_Aggregate.class;

    static protected Configuration config;
    static public Configuration getConfiguration(CallContext context) { return config; }

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.factories.Factory_Aggregate"); };

    static protected long runtimestep_level;
    static public    long adjustRuntimeStepLevel(long new_level) { long old_level = runtimestep_level; runtimestep_level = new_level; return old_level; }
    static public    long getRuntimeStepLevel() { return runtimestep_level; }
    static { runtimestep_level = RuntimeStepLocationContext.getLevel(_class); };

    private String aggregate_class;
    private TreeNode aggregate_tree_node;
    private BuildText aggregate_build_text;
    private java.util.Map parameters; 

    static private String search_path;
    static private Hashtable search_pathes;
    static private Hashtable<String,CacheEntry> factory_sites;
    static private String compile_destination;
    static private boolean ocp_compiler_enabled;
    static private boolean ocp_compiler_immediate_compilation;
    static private boolean cocp_loader_enabled;
    static private boolean cocp_ignore_missing_classes;
    static private RegularExpression cocp_aggregate_include_regexp;
    static private RegularExpression cocp_aggregate_exclude_regexp;
    static private RegularExpression cocp_treenode_include_regexp;
    static private RegularExpression cocp_treenode_exclude_regexp;
    protected boolean cocp_ignore;

    static protected java.util.Hashtable alias_libraries_hash;
    static protected java.util.Hashtable alias_library_hash;

    static {
        CallContext context = RootContext.getInitialisationContext();
        config = Configuration.create(context, "com.sphenon.engines.factorysite.factories.Factory_Aggregate");
        factory_sites = new Hashtable<String,CacheEntry>();
        search_path = getProperty(context, "SearchPath", (String) null);
        search_pathes = new java.util.Hashtable();
        compile_destination = getProperty(context, "CompileDestination", (String) null);
        ocp_compiler_enabled = getProperty(context, "OCPCompilerEnabled", false);
        ocp_compiler_immediate_compilation = getProperty(context, "OCPCompilerImmediateCompilation", false);
        cocp_loader_enabled = getProperty(context, "COCPLoaderEnabled", false);
        cocp_ignore_missing_classes = getProperty(context, "COCPIgnoreMissingClasses", false);
        String cocpair = getProperty(context, "COCPAggregateIncludeRegExp", null);
        String cocpaer = getProperty(context, "COCPAggregateExcludeRegExp", null);
        String cocptir = getProperty(context, "COCPTreeNodeIncludeRegExp", null);
        String cocpter = getProperty(context, "COCPTreeNodeExcludeRegExp", null);
        cocp_aggregate_include_regexp = (cocpair == null ? null : new RegularExpression(cocpair));
        cocp_aggregate_exclude_regexp = (cocpaer == null ? null : new RegularExpression(cocpaer));
        cocp_treenode_include_regexp = (cocptir == null ? null : new RegularExpression(cocptir));
        cocp_treenode_exclude_regexp = (cocpter == null ? null : new RegularExpression(cocpter));
    }

    static public void clearFactorySiteCaches(CallContext context) {
        factory_sites = new Hashtable<String,CacheEntry>();
        ScaffoldFactory.resetCaches(context);
    }

    static public String getProperty(CallContext context, String property, String default_value) {
        String result = FactorySitePackageInitialiser.getConfiguration(context).get(context, property, (String) null);
        if (result != null) { return result; }
        return config.get(context, property, default_value);
    }

    static public boolean getProperty(CallContext context, String property, boolean default_value) {
        boolean result = FactorySitePackageInitialiser.getConfiguration(context).get(context, property, false);
        if (result != default_value) { return result; }
        return config.get(context, property, default_value);
    }

    static public void configSearchPath(CallContext context, String new_search_path) {
        search_path = new_search_path;
    }

    public Factory_Aggregate(CallContext context) {
        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
            CustomaryContext.create((Context)context).sendTrace(context, Notifier.DIAGNOSTICS, FactorySiteStringPool.get(context, "1.2.8" /* Factory_Aggregate[%(oid)] created */), "oid", this);
        }
        this.use_cache = true;
    }

    private int aggregate_class_validation_state = -1;
    private int parameters_validation_state = -1;
    private ValidationFailure parameters_validation_failure = null;
    private FactorySite fs = null;
    private String search_path_context = null;
    private String key = null;
    private Throwable pvs_cause = null;
    private OCPFinder ocp_finder;
    private BuildText bt;
    private String xmlns;
    private String ocpid;
    private String polymorphic;
    private String original_base;
    private String base_aggregate;
    private TreeNode base_tree_node;
    private Type root_type;
    
    static public Object construct(String aggregate_class, Object... arguments) {
        return construct(RootContext.getRootContext(), aggregate_class, arguments);
    }

    static public Object construct(CallContext context, String aggregate_class, Object... arguments) {
        return doConstruct(context, aggregate_class, null, null, null, null, true, arguments);
    }
    
    static public Object construct(CallContext context, String aggregate_class, Type expected_type ) {
        return doConstruct(context, aggregate_class, null, null, expected_type, null, true);
    }

    static public Object constructFromString(CallContext context, String aggregate_string, Object... arguments) {
        return constructFromString(context, aggregate_string, null, null, true, arguments);
    }

    static public Object constructFromString(CallContext context, String aggregate_string, Type expected_type, String name_space, boolean use_cache, Object... arguments) {
        TreeNode aggregate_tree_node = com.sphenon.basics.graph.io.TreeLeaf_MediaObject.create(context, aggregate_string, null);
        return doConstruct(context, null, aggregate_tree_node, null, expected_type, name_space, use_cache, arguments);
    }
    
    static public Object construct(CallContext context, String aggregate_class, Hashtable arguments) {
        if (arguments == null) { arguments = new Hashtable(); }
        return doConstruct2(context, aggregate_class, null, null, null, null, true, arguments);
    }

    static public Object construct(CallContext context, String aggregate_class, Map arguments) {
        if (arguments == null) { arguments = new HashMap(); }
        return doConstruct2(context, aggregate_class, null, null, null, null, true, arguments);
    }

    static public Object construct(CallContext context, TreeNode aggregate_tree_node, Object... arguments) {
        return doConstruct(context, null, aggregate_tree_node, null, null, null, true, arguments);
    }
    
    static public Object construct(CallContext context, TreeNode aggregate_tree_node, Type expected_type) {
        return doConstruct(context, null, aggregate_tree_node, null, expected_type, null, true);
    }
    
    static public Object construct(CallContext context, TreeNode aggregate_tree_node) {
        return doConstruct(context, null, aggregate_tree_node, null, null, null, true);
    }

    static public Object construct(CallContext context, TreeNode aggregate_tree_node, Hashtable arguments) {
        if (arguments == null) { arguments = new Hashtable(); }
        return doConstruct2(context, null, aggregate_tree_node, null, null, null, true, arguments);
    }

    static public<ET> ET construct(CallContext context, Class<ET> expected_type, String aggregate_class, Object... arguments) {
        return (ET) doConstruct(context, aggregate_class, null, null, TypeManager.get(context, expected_type), null, true, arguments);
    }
    
    static public<ET> ET construct(CallContext context, Class<ET> expected_type, String aggregate_class) {
        return (ET) doConstruct(context, aggregate_class, null, null, TypeManager.get(context, expected_type), null, true);
    }

    static public<ET> ET construct(CallContext context, Class<ET> expected_type, String aggregate_class, Map arguments) {
        return (ET) doConstruct2(context, aggregate_class, null, null, TypeManager.get(context, expected_type), null, true, arguments);
    }
    
    static public Object construct(CallContext context, BuildText aggregate_build_text, String name_space, Object... arguments) {
        return doConstruct(context, null, null, aggregate_build_text, null, name_space, false, arguments);
    }

    static public Object construct(CallContext context, JSONNode node, String name_space, Object... arguments) {
        try {
            return doConstruct(context, null, null, new BuildTextJSONFactory(context, node, null, null).getBuildText(context), null, name_space, false, arguments);
        } catch (InvalidDocument id) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, id, "Provided JSON is invalid");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }
    
    static public Map makeMap(CallContext context, boolean ignore_null, Object... arguments) {
        Map parameters = new HashMap(1);
        
        if (arguments != null) {
            if (arguments.length % 2 != 0) {
                CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Invocation of Factory_Aggregate with variable arguments failed, number of arguments is uneven");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            for (int a = 0; a < arguments.length; a += 2) {
                if (!(arguments[a] instanceof String)) {
                    CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Invocation of Factory_Aggregate with variable arguments failed, argument '%(index)' is not a string", "index", a);
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
                String name = (String) (arguments[a]);
                Object value = arguments[a + 1];
                if (value == null) {
                    if (ignore_null == false) {
                        CustomaryContext.create((Context) context).throwPreConditionViolation(context, "Invocation of Factory_Aggregate failed, argument '%(name)' is null", "name", name);
                        throw (ExceptionPreConditionViolation) null; // compiler insists
                    }
                } else {
                    parameters.put(name, value);
                }
            }
        }

        return parameters;
    }

    static protected Object doConstruct(CallContext context, String aggregate_class, TreeNode aggregate_tree_node, BuildText aggregate_build_text, Type expected_type, String name_space, boolean use_cache, Object... arguments) {
        return doConstruct2(context, aggregate_class, aggregate_tree_node, aggregate_build_text, expected_type, name_space, use_cache, makeMap(context, false, arguments));
    }

    static protected Object doConstruct2(CallContext context, String aggregate_class, TreeNode aggregate_tree_node, BuildText aggregate_build_text, Type expected_type, String name_space, boolean use_cache, java.util.Map parameters) {

        Factory_Aggregate cf = new Factory_Aggregate(context);
        cf.setAggregateClass(context, aggregate_class);
        cf.setAggregateTreeNode(context, aggregate_tree_node);
        cf.setAggregateBuildText(context, aggregate_build_text);
        cf.setParameters(context, parameters);
        cf.setExpectedType(context, expected_type);
        cf.setNameSpace(context, name_space);
        cf.setUseCache(context, use_cache);

        return cf.create(context);
    }

    public String getAggregateClass(CallContext context) {
        return this.aggregate_class;
    }

    static public boolean slash_notification;

    public void setAggregateClass(CallContext context, String aggregate_class) {
        this.aggregate_class = aggregate_class;
        if (slash_notification == false && aggregate_class != null && aggregate_class.length() != 0 && aggregate_class.charAt(0) == '/' && (aggregate_class.length() <= 1 || aggregate_class.charAt(1) != '/')) {
            System.err.println("NOTICE: aggregate starts with slash: " + aggregate_class);
            System.err.println("(this is only the first notification, following occurences are silently processed)");
            slash_notification = true;
        }
        this.aggregate_class_validation_state = -1;
        this.parameters_validation_state = -1;
        this.pvs_cause = null;
        this.fs = null;
        this.search_path_context = null;
        this.ocp_finder = null;
        this.bt = null;
        this.ocpid = null;
        this.polymorphic = null;
        this.root_type = null;
        this.cocp_class = null;
        this.cocp_path = null;
        this.cocp_name = null;
        this.dcocpc = null;
        if (this.aggregate_class != null) {
            this.cocp_ignore =    (this.ocp_compiler_enabled == false && this.cocp_loader_enabled == false)
                               || (    cocp_aggregate_include_regexp != null 
                                    && cocp_aggregate_include_regexp.matches(context, this.aggregate_class) == false
                                  )
                               || (    cocp_aggregate_exclude_regexp != null 
                                    && cocp_aggregate_exclude_regexp.matches(context, this.aggregate_class) == true
                                  )
                               ;
        }
    }

    public void setAggregateString(CallContext context, String aggregate_string) {
        if (aggregate_string != null) {
            TreeNode aggregate_tree_node = com.sphenon.basics.graph.io.TreeLeaf_MediaObject.create(context, aggregate_string, null);
            setAggregateTreeNode(context, aggregate_tree_node);
        }
    }

    public String defaultAggregateString(CallContext context) {
        return null;
    }

    public void setAggregateTreeNode(CallContext context, TreeNode aggregate_tree_node) {
        this.aggregate_tree_node = aggregate_tree_node;
        this.aggregate_class_validation_state = -1;
        this.parameters_validation_state = -1;
        this.pvs_cause = null;
        this.fs = null;
        this.search_path_context = null;
        this.ocp_finder = null;
        this.bt = null;
        this.ocpid = null;
        this.polymorphic = null;
        this.root_type = null;
        this.cocp_class = null;
        this.cocp_path = null;
        this.cocp_name = null;
        this.dcocpc = null;
        if (this.aggregate_tree_node != null) {
            this.cocp_ignore =    (this.ocp_compiler_enabled == false && this.cocp_loader_enabled == false)
                               || (    cocp_treenode_include_regexp != null 
                                    && cocp_treenode_include_regexp.matches(context, this.aggregate_tree_node.getPath(context)) == false
                                  )
                               || (    cocp_treenode_exclude_regexp != null 
                                    && cocp_treenode_exclude_regexp.matches(context, this.aggregate_tree_node.getPath(context)) == true
                                  )
                               ;
        }
    }

    public TreeNode defaultAggregateTreeNode(CallContext context) {
        return null;
    }

    public void setAggregateBuildText(CallContext context, BuildText aggregate_build_text) {
        this.aggregate_build_text = aggregate_build_text;
        this.aggregate_class_validation_state = -1;
        this.parameters_validation_state = -1;
        this.pvs_cause = null;
        this.fs = null;
        this.search_path_context = null;
        this.ocp_finder = null;
        this.bt = null;
        this.ocpid = null;
        this.polymorphic = null;
        this.root_type = null;
        this.cocp_class = null;
        this.cocp_path = null;
        this.cocp_name = null;
        this.dcocpc = null;
        if (this.aggregate_class == null && this.aggregate_tree_node == null) {
            this.cocp_ignore = true;
        }
    }

    public BuildText defaultAggregateBuildText(CallContext context) {
        return null;
    }

    public String getNameSpace (CallContext context) {
        return this.xmlns;
    }

    public String defaultNameSpace (CallContext context) {
        return null;
    }

    public void setNameSpace (CallContext context, String xmlns) {
        this.xmlns = xmlns;
    }

    public boolean isValidNameSpace(CallContext context) {
        return this.xmlns == null ? true : findSearchPathContext(context) != null ? true : false;
    }

    protected String findSearchPathContext(CallContext context) {
        if (this.xmlns != null) {
            if (this.xmlns.matches("^[A-Za-z0-9_]+$")) {
                this.search_path_context = getProperty(context, "TypeContextForNamedNS." + this.xmlns, (String) null);
            } else {
                this.search_path_context = getProperty(context, "TypeContext4XMLNS." + xmlns, (String) null);
                if (this.search_path_context == null) {
                    this.search_path_context = getProperty(context, "TypeContextForXMLNS." + xmlns, (String) null);
                }
            }
        }
        return this.search_path_context;
    }

   // ---------------------------------------------------------
    // cache for the factories

    protected boolean use_cache;

    public boolean getUseCache (CallContext context) {
        return this.use_cache;
    }

    public boolean defaultUseCache (CallContext context) {
        return true;
    }

    public void setUseCache (CallContext context, boolean use_cache) {
        this.use_cache = use_cache;
    }

   // ---------------------------------------------------------
    // cache for the factories

    protected Map aggregate_cache;

    public Map getAggregateCache (CallContext context) {
        return this.aggregate_cache;
    }

    public Map defaultAggregateCache (CallContext context) {
        return null;
    }

    public void setAggregateCache (CallContext context, Map aggregate_cache) {
        this.aggregate_cache = aggregate_cache;
    }

    protected String aggregate_cache_parameter;

    public String getAggregateCacheParameter (CallContext context) {
        return this.aggregate_cache_parameter;
    }

    public String defaultAggregateCacheParameter (CallContext context) {
        return null;
    }

    public void setAggregateCacheParameter (CallContext context, String aggregate_cache_parameter) {
        this.aggregate_cache_parameter = aggregate_cache_parameter;
    }

   // ---------------------------------------------------------

    protected Type expected_type;

    public Type getExpectedType (CallContext context) {
        return this.expected_type;
    }

    public Type defaultExpectedType (CallContext context) {
        return null;
    }

    public void setExpectedType (CallContext context, Type expected_type) {
        this.expected_type = expected_type;
    }

    protected String aggregate_target_class;

    public String getAggregateTargetClass (CallContext context) {
        return this.aggregate_target_class;
    }

    public String defaultAggregateTargetClass (CallContext context) {
        return null;
    }

    public void setAggregateTargetClass (CallContext context, String aggregate_target_class) {
        this.aggregate_target_class = aggregate_target_class;
    }

    public Type getRootType (CallContext context) {
        try {
            validateAggregateClass(context/* DOES NOT WORK!!  , true*/);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, FactorySiteStringPool.get(context, "1.2.15" /* Parameter 'AggregateClass' ('%(aggregateclass)') is invalid */), "aggregateclass", this.aggregate_class);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        return this.root_type;
    }

    public String getId(CallContext context) {
        return (   this.aggregate_class != null && this.aggregate_class.length() != 0
                 ? this.aggregate_class
                 : this.aggregate_tree_node != null
                 ? this.aggregate_tree_node.getId(context)
                 : this.aggregate_build_text != null
                 ? "<BuildText>"
                 : "<invalid>"
               );
    }
    
    protected class CacheEntry {
        public  BuildText                     bt;
        public  FactorySite                   fs;
        private String                        search_path_context;
        public  long                          created;
        public  ReadOnlyVector_TreeNode_long_ ocp_tnodes_to_check;
        public  OCPFinder                     ocp_finder;
        public  String                        xmlns;
        public  String                        ocpid;
        public  String                        polymorphic;
        public  AggregateRegistry             aggregate_registry;
        public  Type                          root_type;
        public  String                        original_base;
        public  String                        base_aggregate;
        public  TreeNode                      base_tree_node;

        public  String                        aggregate_class;
        public  String                        aggregate_tree_node;
        public  boolean                       aggregate_build_text;
        public  String                        aggregate_target_class;
        public  String                        expected_type;
        public  String                        name_space;
    }

    protected CacheEntry cache_entry;

    static protected synchronized String getSearchPath(CallContext context, String aggregate_root_class) {
        Object o = search_pathes.get(aggregate_root_class);
        String sp;
        if (o == null) {
            sp = getProperty(context, "SearchPathForClass." + aggregate_root_class, (String) null);
            if (sp != null) {
                search_pathes.put(aggregate_root_class, sp);
            } else {
                if (search_path == null) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, FactorySiteStringPool.get(context, "1.2.0" /* Factory_Aggregate: search path not configured (use '.configSearchPath(context, search_path);' ) */));
                }
                sp = search_path;
            }
        } else {
            sp = (String) o;
        }
        return sp;
    }

    protected LocationContext location_context;

    public LocationContext getLocationContext(CallContext context) {
        return this.location_context;
    }

    public LocationContext defaultLocationContext(CallContext context) {
        return null;
    }

    public void setLocationContext(CallContext context, LocationContext location_context) {
        this.location_context = location_context;
    }

    public boolean exists(CallContext context) {
        if (this.ocp_finder == null) {
            this.ocp_finder = new OCPFinder(context, this.aggregate_class, this.aggregate_tree_node, this.aggregate_build_text, this.aggregate_target_class);
            try {
                this.ocp_finder.findOCP(context);
            } catch (ValidationFailure vf) {
                this.ocp_finder = null;
                return false;
            }
        }
        return this.ocp_finder.found;
    }

    public void validateAggregateClass(CallContext context) throws ValidationFailure {
        validateAggregateClass(context, false);
    }

    public Object[] prepareBaseInfo(CallContext context, String bag) {
        Object[] bat = new Object[2];
        bat[0] = bag;
        if (bag != null) {
            if (bag.indexOf('/') == -1) {
                System.err.println("Preparing relative:" + bag);
                String[] bagparts = bag.split(";",2);
                int slash;
                if (this.aggregate_class != null && (slash = this.aggregate_class.lastIndexOf('/')) != -1) {
                    bat[0] = this.aggregate_class.substring(0, slash+1) + bag;
                    System.err.println("BAT0: " + ((String) bat[0]));
                } else if (bagparts.length == 2 && bagparts[1].isEmpty() == false) {
                    bat[0] = ";" + bagparts[1];
                    System.err.println("BAT0: " + ((String) bat[0]));
                }
                if (this.aggregate_tree_node != null) {
                    String curid = this.aggregate_tree_node.getId(context);
                    int ep = curid.lastIndexOf('.');
                    String curext = (ep == -1 ? "" : curid.substring(ep));
                    bat[1] = this.aggregate_tree_node.tryGetParent(context).tryGetChild(context, bagparts[0] + curext);
                    System.err.println("BAT1: " + (bat[1] == null ? "null" : ((TreeNode) bat[1]).getPath(context)));
                }
            }
        }
        return bat;
    }

    public boolean checkChanged(CallContext context) {
        return this.checkChanged(context, null);
    }

    public boolean checkChanged(CallContext context, Object[] ref) {
        String cache_key = (this.use_cache ? (this.aggregate_class != null ? this.aggregate_class.replaceFirst("^/","").replaceFirst(";class=Object$","") : (this.aggregate_tree_node == null ? null : this.aggregate_tree_node.getPath(context))) : null);
        CacheEntry ce = (this.use_cache ? factory_sites.get(cache_key) : null);
        long actual_last_modification = 0;
        if (ce != null) {
            actual_last_modification = ce.fs.getLastModification(context);
            for (long i=0; i<ce.ocp_tnodes_to_check.getSize(context); i++) {
                long lm = ce.ocp_tnodes_to_check.tryGet(context, i).getLastModification(context);
                if (lm > actual_last_modification) { actual_last_modification = lm; }
            }
        }
        if ((this.notification_level & Notifier.VERBOSE) != 0) {
            if (ce == null) {
                NotificationContext.sendTrace(context, Notifier.VERBOSE, FactorySiteStringPool.get(context, "1.2.18" /* Factory_Aggregate[%(oid)].create(): no cache entry */), "oid", this);
            } else {
                NotificationContext.sendTrace(context, Notifier.VERBOSE, FactorySiteStringPool.get(context, "1.2.19" /* Factory_Aggregate[%(oid)].create(): cache entry, created at %(ctime), last modification of file is %(mtime) */), "oid", this, "ctime", t.s(ce.created), "mtime", t.s(actual_last_modification));
            }
        }
        boolean has_changed = ce != null && ce.created < actual_last_modification;
        if (ref != null) {
            ref[0] = cache_key;
            ref[1] = ce;
        }

        if (has_changed) {
            this.aggregate_class_validation_state = -1;
            this.parameters_validation_state = -1;
            this.pvs_cause = null;
            this.fs = null;
            this.search_path_context = null;
            this.ocp_finder = null;
            this.bt = null;
            this.ocpid = null;
            this.polymorphic = null;
            this.root_type = null;
            this.cocp_class = null;
            this.cocp_path = null;
            this.cocp_name = null;
            this.dcocpc = null;
        }

        return has_changed;
    }

    // do not use with "true" - does not work (the mechanism in general)
    protected void validateAggregateClass(CallContext call_context, boolean prevalidate_only) throws ValidationFailure {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        String type_mismatch_parameter = null;
        TypeMismatch type_mismatch = null;
        InvalidLocator invalid_locator = null;

        RuntimeStep runtime_step = null;
        if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { runtime_step = RuntimeStep.create(context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Validating aggregate class '%(id)'", "id", this.getId(context)); }

        // this is to debug occasional exceptions fairyloom/board
        // Factory_Aggregate.java:1160 --- "Parameter 'aggregate class' wurde nicht übergeben"
        if (crash_debug) { SystemContext.err.println(context, "FA " + SystemUtilities.getObjectIdHex(context, this) + " " + this.aggregate_class + " " + this.aggregate_class_validation_state + " (entry)"); }

        if (    this.aggregate_class_validation_state == -1
             || (this.aggregate_class_validation_state == -2 && prevalidate_only == false)
           ) {
            if (this.aggregate_class == null && this.aggregate_tree_node == null && this.aggregate_build_text == null) {
                if (crash_debug) { SystemContext.err.println(context, "FA " + SystemUtilities.getObjectIdHex(context, this) + " " + this.aggregate_class + " " + this.aggregate_class_validation_state + " (=> 1)"); }
                this.aggregate_class_validation_state = 1;
            } else {
                if (this.aggregate_class != null && this.aggregate_class.matches("role=.*")) {
                    String role = this.aggregate_class.substring(5);
                    Type context_type = null;
                    try {
                        context_type = TypeManager.get(context, role);
                    } catch (NoSuchClass nsc) {
                        this.aggregate_class_validation_state = 6;
                    }
                    if (this.aggregate_class_validation_state <= 0) {
                        if (this.parameters == null || this.parameters.size() != 1) {
                            this.aggregate_class_validation_state = 7;
                        }
                        if (this.aggregate_class_validation_state <= 0) {
                            Object parameter = this.parameters.values().iterator().next();
                            Type handled_type = null;
                            if (parameter instanceof DataSink_WithTypeInfo) {
                                handled_type = ((DataSink_WithTypeInfo) parameter).getItemType(context);
                            }
                            if (handled_type == null) {
                                handled_type = TypeManager.get(context, parameter);
                            }
                            String registered_aggregate = null;
                            try {
                                registered_aggregate = com.sphenon.engines.factorysite.AggregateRegistry.getAggregate(context, handled_type, context_type);
                                registered_aggregate = registered_aggregate.replaceFirst("(?:ctn:|oorl:)?//Class<Aggregate>/","");
                            } catch (DoesNotExist dne) {
                                this.aggregate_class_validation_state = 8;
                            }
                            if (this.aggregate_class_validation_state <= 0) {
                                this.aggregate_class = registered_aggregate;
                            }
                        }
                    }
                }

                if (this.aggregate_class_validation_state <= 0) {
                    Object[] ref = new Object[2];
                    boolean has_changed = this.checkChanged(context, ref);
                    String cache_key = (String) ref[0];
                    CacheEntry ce = (CacheEntry) ref[1];

                    boolean something_to_do;
                    if (ce != null && ! has_changed) {
                        this.bt = ce.bt;
                        this.fs = ce.fs;
                        this.search_path_context = ce.search_path_context;
                        this.aggregate_class_validation_state = 0;
                        this.ocp_finder = ce.ocp_finder;
                        this.xmlns = ce.xmlns;
                        this.ocpid = ce.ocpid;
                        this.polymorphic = ce.polymorphic;
                        this.root_type = ce.root_type;
                        this.original_base  = ce.original_base;
                        this.base_aggregate = ce.base_aggregate;
                        this.base_tree_node = ce.base_tree_node;
                        this.cache_entry = ce;
                        something_to_do = (this.fs == null);
                    } else {
                        something_to_do = true;
                    }

                    if (something_to_do) {
                        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) {
                            if (has_changed) {
                              cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, FactorySiteStringPool.get(context, "1.2.17" /* Factory_Aggregate[%(oid)].create(): OCP for factory site has changed */), "oid", this);
                            }
                            cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, FactorySiteStringPool.get(context, "1.2.10" /* Factory_Aggregate[%(oid)].create(): creating new factory site */), "oid", this);
                        }

                        if (this.ocp_finder == null) {
                            this.ocp_finder = new OCPFinder(context, this.aggregate_class, this.aggregate_tree_node, this.aggregate_build_text, this.aggregate_target_class);
                            this.ocp_finder.findOCP(context);
                        }

                        if (this.ocp_finder.no_config) {
                            this.aggregate_class_validation_state = 3;
                        } else if (! this.ocp_finder.found) {
                            this.aggregate_class_validation_state = 2;
                        } else if (! this.ocp_finder.tree_folder_found) {
                            this.aggregate_class_validation_state = 4;
                        } else {

                            this.bt = tryLoadCompiledOCP(context);

                            if (this.bt == null) {
                                if (this.ocp_finder.aggregate_build_text != null) {
                                    this.bt = this.ocp_finder.aggregate_build_text;
                                    if (ocpid == null || ocpid.length() == 0) { ocpid = this.aggregate_class; }
                                    if (ocpid == null || ocpid.length() == 0) { ocpid = "build-text"; }
                                    this.use_cache = false;
                                } else {
                                    try {
                                        Map<String,BuildText> bt_by_oid = new HashMap<String,BuildText>();
                                        Data_MediaObject data = ((Data_MediaObject)(((NodeContent_Data)(((TreeLeaf) this.ocp_finder.result_node).getContent(context))).getData(context)));
                                        BuildTextFactory btf =  BuildTextFactoryFactory.create(context, data, bt_by_oid, null);
                                        String signature_override = btf.getSignature(context);
                                        if (xmlns == null) { xmlns = btf.getNameSpace(context); }
                                        ocpid = btf.getOCPId(context);
                                        polymorphic = btf.getPolymorphic(context);
                                        if (polymorphic != null && polymorphic.length() == 0) { polymorphic = null; }

                                        original_base = btf.getBase(context);
                                        Object[] bat = prepareBaseInfo(context, original_base);
                                        String   bag = (String) bat[0];
                                        TreeNode btn = (TreeNode) bat[1];
                                        base_aggregate = bag;
                                        base_tree_node = btn;

                                        while (bag != null) {
                                            OCPFinder base_ocp_finder = new OCPFinder(context, bag, btn, null, null);
                                            base_ocp_finder.findOCP(context);
                                            if (base_ocp_finder.found == false) {
                                                cc.throwConfigurationError(context, "Base ocp '%(base)' of '%(ocp)' does not exist", "base", bag, "ocp", this.aggregate_class);
                                                throw (ExceptionConfigurationError) null; // compiler insists
                                            }
                                            // hier gibts noch 2 Themen:
                                            // a) es fehlt noch ein super.OID, d.h. wenn
                                            //    man im abgeleiteten doch wieder auf das
                                            //    Ding von oben zugreifen will
                                            // b) (ich glaube...) derzeit gibt es noch keinen
                                            //    check, ob ein ueberladendes OID zu dem ueber-
                                            //    ladenen OID passt - ggf. waere so ein constraint
                                            //    gut ggf. aber auch nicht (zu restriktiv)
                                            //    speziell beim root-element muss man aufpassen,
                                            //    dass das nicht "falsch" ueberladen wird --
                                            //    andererseits: gegen was soll man eigentlich
                                            //    pruefen? nur der "expected type"? gegen den
                                            //    wird aber ja geprueft, automatisch
                                            // beide Themen können mit einem verbesserten
                                            // Design vermutlich gemeinsam erschlagen
                                            // werden (statt simpler hashtable)
                                            Data_MediaObject base_data = ((Data_MediaObject)(((NodeContent_Data)(((TreeLeaf) base_ocp_finder.result_node).getContent(context))).getData(context)));
                                            btf = BuildTextFactoryFactory.create(context, base_data, bt_by_oid, signature_override);
                                            if (xmlns != null) { xmlns = btf.getNameSpace(context); }
                                            if (ocpid != null) { ocpid = btf.getOCPId(context); }
                                            bag = btf.getBase(context);
                                            bat = prepareBaseInfo(context, bag);
                                            bag = (String) bat[0];
                                            btn = (TreeNode) bat[1];
                                        }
                                        this.bt = btf.getBuildText(context);
                                    } catch (ClassCastException cce) {
                                        cc.throwConfigurationError(context, cce, "Final node in '%(ocp)' is not a leaf or does not contain data content", "ocp", this.ocp_finder.fullpath);
                                        throw (ExceptionConfigurationError) null; // compiler insists
                                    } catch (InvalidDocument e) {
                                        cc.throwConfigurationError(context, e, FactorySiteStringPool.get(context, "1.2.3" /* Could not read object construction plan (ocp/xml): '%(ocp)' */), "ocp", this.ocp_finder.fullpath);
                                        throw (ExceptionConfigurationError) null; // compiler insists
                                    }
                                    if (ocpid == null || ocpid.length() == 0) { ocpid = this.aggregate_class; }
                                    if (ocpid == null || ocpid.length() == 0) { ocpid = "tree-node"; }
                                }
                            }

                            boolean have_xmlns = (xmlns != null && xmlns.length() != 0 ? true : false);
                            if (have_xmlns) {
                                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): xmlns '%(xmlns)'", "oid", this, "xmlns", xmlns); }
                                String package_to_initialise = getProperty(context, "Package2Initialise4XMLNS." + xmlns, (String) null);
                                if (package_to_initialise == null) {
                                    package_to_initialise = getProperty(context, "Package2InitialiseForXMLNS." + xmlns, (String) null);
                                }
                                if (package_to_initialise != null) {
                                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): package to initialise '%(package_to_initialise)'", "oid", this, "package_to_initialise", package_to_initialise); }

                                    CoreInitialiser.initialisePackage(context, package_to_initialise);
                                }

                                this.findSearchPathContext(context);

                                if (this.search_path_context == null) {
                                    if ((this.notification_level & Notifier.OBSERVATION) != 0) { cc.sendInfo(context, FactorySiteStringPool.get(context, "1.2.23" /* No type context configured for xml namespace '%(xmlns)' */), "xmlns", xmlns); }
                                }
                            }

                            {    String root_class =   this.ocp_finder.is_tree
                                                     ? (ocp_finder.tree_aggregate_root_class == null ? null : ocp_finder.tree_aggregate_root_class)
                                                     : (ocp_finder.aggregate_root_class == null ? null : ocp_finder.aggregate_root_class);

                                {   if (this.search_path_context == null && root_class != null) {
                                        this.search_path_context = getProperty(context, "TypeContext4Class." + root_class, (String) null);
                                        if (this.search_path_context == null) {
                                            this.search_path_context = getProperty(context, "TypeContextForClass." + root_class, (String) null);
                                        }

                                        if (search_path_context == null) {
                                            if (root_class.equals("Object")) {
                                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "No type context configured for class '%(class)'", "class", root_class); }
                                            } else {
                                                if ((this.notification_level & Notifier.OBSERVATION) != 0) { cc.sendInfo(context, "No type context configured for class '%(class)'", "class", root_class); }
                                            }
                                        }
                                    }
                                }

                                if (search_path_context != null) {
                                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): search path context '%(search_path_context)'", "oid", this, "search_path_context", search_path_context); }

                                    TypeContext tc = TypeContext.create(context);
                                    tc.setSearchPathContext(context, this.search_path_context);
                                }

                                try {
                                    root_type = (root_class == null ? null : TypeManager.get(context, root_class));
                                } catch (NoSuchClass nsc) {
                                    cc.throwConfigurationError(context, nsc, "Target type '%(type)' of OCP, as derived from ctn, was not found", "type", this.ocp_finder.is_tree ? ocp_finder.tree_aggregate_root_class : ocp_finder.aggregate_root_class);
                                    throw (ExceptionConfigurationError) null; // compiler insists
                                }
                            }

                            root_type = refineTypes(context, root_type, this.expected_type, true, "explicitly expected type and root type from ctn");

                            if (have_xmlns) {
                                String root_class = getProperty(context, "RootClass4XMLNS." + xmlns, (String) null);
                                if (root_class == null) {
                                    root_class = getProperty(context, "RootClassForXMLNS." + xmlns, (String) null);
                                }
                                if (root_class != null) {

                                    /* HM, that's complete nonsense, isn't it?
                                       if no xmlns, or no root_class
                                       why not just use bt.typename then?
                                    */

                                    String rc = this.bt.getTypeName(context);
                                    if (rc != null && rc.length() != 0) {
                                        root_class = rc;
                                    }

                                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): root class '%(root_class)'", "oid", this, "root_class", root_class); }

                                    Type rt = null;
                                    try {
                                        rt = TypeManager.get(context, root_class);
                                    } catch (NoSuchClass nsc) {
                                        cc.throwConfigurationError(context, nsc, "Target type '%(type)' of OCP, as derived from xmlns (or root element), was not found", "type", root_class);
                                        throw (ExceptionConfigurationError) null; // compiler insists
                                    }
                                    root_type = refineTypes(context, root_type, rt, true, "explicite and/or ctn root type and type from name space");
                                }
                            }

                            // AUSKOMMENTIERT, WEIL EH NICHT FUNKTIONIERT
                            // muss einkommentiert werden, wenn prevalidate_only
                            // jemals wieder mit true aufgerufen wird
                            // (anderfalls kann auch prevalidate komplett wieder
                            // ausgebaut werden)
    //                         String bttn = this.bt.getTypeName(context);
    //                         if (bttn != null && bttn.length() != 0) {
    //                             Type btt = null;
    //                             try {
    //                                 btt = TypeManager.get(context, bttn);
    //                             } catch (NoSuchClass nsc) {
    //                                 cc.throwConfigurationError(context, nsc, "Type '%(type)' of root element in OCP was not found", "type", bttn);
    //                                 throw (ExceptionConfigurationError) null; // compiler insists
    //                             }
    //                             root_type = refineTypes(context, root_type, btt, false);
    //                         }

    //                         if (root_type == null) {
    //                             root_type = TypeManager.get(context, Object.class);
    //                         }

                            if (prevalidate_only == false) {
                                if (this.search_path_context != null) {
                                    String alias_libraries = getProperty(context, "AliasLibraries4TypeContext." + this.search_path_context, (String) null);
                                    if (alias_libraries == null) {
                                        alias_libraries = getProperty(context, "AliasLibrariesForTypeContext." + this.search_path_context, (String) null);
                                    }
                                    if (alias_libraries != null) {
                                        if (alias_libraries_hash == null) {
                                            alias_libraries_hash = new java.util.Hashtable();
                                        }
                                        if (alias_libraries_hash.get(this.search_path_context) != null) {
                                            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): alias libraries '%(aliaslibraries)' (already loaded)", "oid", this, "aliaslibraries", alias_libraries); }
                                        } else {
                                            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): alias libraries '%(aliaslibraries)'", "oid", this, "aliaslibraries", alias_libraries); }
                                            alias_libraries_hash.put(this.search_path_context, this.search_path_context);

                                            for (String alias_library : alias_libraries.split(":")) {
                                                if (alias_library != null) {
                                                    if (alias_library_hash == null) {
                                                        alias_library_hash = new java.util.Hashtable();
                                                    }
                                                    Hashtable spchash = (Hashtable) alias_library_hash.get(alias_library);
                                                    if (spchash == null) {
                                                        spchash =  new java.util.Hashtable();
                                                        alias_library_hash.put(alias_library, spchash);
                                                    }
                                                    if (spchash.get(this.search_path_context) != null) {
                                                        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): alias library '%(aliaslibrary)' (already loaded for type context '%(tc)')", "oid", this, "aliaslibrary", alias_library, "tc", this.search_path_context); }
                                                    } else {
                                                        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): alias library '%(aliaslibrary)'", "oid", this, "aliaslibrary", alias_library); }
                                                        spchash.put(this.search_path_context, this.search_path_context);

                                                        String aliases = getProperty(context, "AliasLibrary." + alias_library, (String) null);
                                                        if (aliases != null) {
                                                            FactorySitePackageInitialiser.defineAliases (context, this.search_path_context, aliases);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                try {
                                    JavaCodeManager jcm = optionallyOpenJavaCodeManager(context);
                                    this.fs = new FactorySiteTextBased(context, this.bt, ocpid, root_type, this.location_context, jcm);
                                    optionallyProcessGeneratedJavaCode(context, jcm);

                                    com.sphenon.engines.factorysite.DataSource ds = this.fs.getMainDataSource (context);
                                    this.root_type = refineTypes(context, this.root_type, ds.getType(context), false, "expected root type and factory site root type");

                                } catch (PutUpFailure puf) {
                                    cc.throwConfigurationError(context, puf, FactorySiteStringPool.get(context, "1.2.4" /* Could not put up factory site */));
                                    throw (ExceptionConfigurationError) null; // compiler insists
                                }

                                if (this.ocp_finder.is_tree) {
                                    try {
                                        fs.setOptionalStaticParameter(context, "FOLDERS", this.ocp_finder.tree_folders);
                                    } catch (TypeMismatch tm) {
                                        type_mismatch = tm;
                                        type_mismatch_parameter = "FOLDERS";
                                    }
                                    if (this.ocp_finder.tree_folder_nodes != null) {
                                        try {
                                            fs.setOptionalStaticParameter(context, "TREENODES", this.ocp_finder.tree_folder_nodes);
                                        } catch (TypeMismatch tm) {
                                            type_mismatch = tm;
                                            type_mismatch_parameter = "FOLDERS";
                                        }
                                    }
                                    try {
                                        fs.setOptionalStaticParameter(context, "EXTENSION", this.ocp_finder.tree_extension);
                                    } catch (TypeMismatch tm) {
                                        type_mismatch = tm;
                                        type_mismatch_parameter = "EXTENSION";
                                    }
                                    try {
                                        fs.setOptionalStaticParameter(context, "AGGREGATEPREFIX", this.ocp_finder.tree_folder_aggregate_part);
                                    } catch (TypeMismatch tm) {
                                        type_mismatch = tm;
                                        type_mismatch_parameter = "AGGREGATEPREFIX";
                                    }
                                    try {
                                        fs.setOptionalStaticParameter(context, "COMPONENTCLASS", ocp_finder.aggregate_root_class);
                                    } catch (TypeMismatch tm) {
                                        type_mismatch = tm;
                                        type_mismatch_parameter = "COMPONENTCLASS";
                                    }
                                } else {
                                    try {
                                        fs.setStaticParameter(context, "__OCP_ID__", this.aggregate_class);
                                    } catch (DoesNotExist dne) {
                                    } catch (TypeMismatch tm) {
                                        type_mismatch = tm;
                                        type_mismatch_parameter = "__OCP_ID__";
                                    }
                                    try {
                                        fs.setStaticParameter(context, "__OCP_LOCATION__", this.ocp_finder.result_node != null ? this.ocp_finder.result_node.getLocation(context) : this.ocp_finder.aggregate_build_text != null ? "//Invalid/<build text no location>" : "//Invalid/<no location>");
                                    } catch (DoesNotExist dne) {
                                    } catch (TypeMismatch tm) {
                                        type_mismatch = tm;
                                        type_mismatch_parameter = "__OCP_LOCATION__";
                                    }
                                }

                                if (type_mismatch != null) {
                                    this.aggregate_class_validation_state = 5;
                                } else {
                                    this.aggregate_class_validation_state = 0;
                                }
                            } else {
                                this.aggregate_class_validation_state = -2;
                            }

                            if (    this.aggregate_class_validation_state == 0
                                 || this.aggregate_class_validation_state == -2
                               ) {
                                if (this.use_cache) {
                                    ce = new CacheEntry();
                                    ce.bt = this.bt;
                                    ce.fs = this.fs;
                                    ce.search_path_context = this.search_path_context;
                                    ce.ocp_tnodes_to_check = this.ocp_finder.ocp_tnodes_to_check;
                                    ce.ocp_finder = this.ocp_finder;
                                    ce.xmlns = this.xmlns;
                                    ce.polymorphic = this.polymorphic;
                                    ce.ocpid = this.ocpid;
                                    ce.root_type = this.root_type;
                                    ce.original_base  = this.original_base;
                                    ce.base_aggregate = this.base_aggregate;
                                    ce.base_tree_node = this.base_tree_node;

                                    ce.aggregate_class = this.aggregate_class;

                                    Location location = (this.aggregate_tree_node == null ? null : this.aggregate_tree_node.getLocation(context));
                                    String   locator  = (location == null ? null : location.tryGetTextLocator(context, null, null));

                                    ce.aggregate_tree_node = locator;
                                    ce.aggregate_build_text = (this.aggregate_build_text != null);
                                    ce.aggregate_target_class = this.aggregate_target_class;
                                    ce.expected_type = this.expected_type == null ? null : this.expected_type.getId(context);
                                    ce.name_space = this.xmlns;

                                    ce.created = this.fs.getLastModification(context);
                                    if (ce.ocp_tnodes_to_check != null) {
                                        for (long i=0; i<ce.ocp_tnodes_to_check.getSize(context); i++) {
                                            long lm = ce.ocp_tnodes_to_check.tryGet(context, i).getLastModification(context);
                                            if (lm > ce.created) { ce.created = lm; }
                                        }
                                    }

                                    factory_sites.put(cache_key, ce);
                                    this.cache_entry = ce;
                                }
                            }

                            if (base_aggregate != null) {
                                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Factory_Aggregate[%(oid)]: got base aggregate '%(base_aggregate)', registering myself ('%(aggregateclass)')...", "oid", this, "base_aggregate", base_aggregate, "aggregateclass", this.aggregate_class); }
                                Factory_Aggregate base_fa = new Factory_Aggregate(context);
                                base_fa.setAggregateClass(context, base_aggregate);
                                base_fa.setAggregateTreeNode(context, base_tree_node);
                                base_fa.registerDerived(context, this);
                            }

                            if (polymorphic != null) {
                                if (this.aggregate_class == null || this.aggregate_class.length() == 0) {
                                    // autoload only possible if explicit aggregate class is given
                                    // (but derivation is also only possible in that case, so...)
                                } else {
                                    String package_name = this.aggregate_class.replaceFirst("/[^/]*$", "");

                                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Factory_Aggregate[%(oid)]: scanning '%(package)'...", "oid", this, "package", package_name); }

                                    package_name += ";class=Tree<Factory<Object>>";
                                    Vector_Pair_String_Object__long_ factories = (Vector_Pair_String_Object__long_) Factory_Aggregate.construct(context, package_name);
                                    for (Pair_String_Object_ pair : factories.getIterable_Pair_String_Object__(context)) {
                                        String name = pair.getItem1(context);
                                        Object value = pair.getItem2(context);
                                        if (value instanceof Vector_Pair_String_Object__long_) {
                                            // skip subfolder
                                        } else {
                                            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Factory_Aggregate[%(oid)]: autoloading '%(name)'...", "oid", this, "name", name); }
                                            ((Factory_Aggregate) value).validateAggregateClass(context);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (crash_debug) { SystemContext.err.println(context, "FA " + SystemUtilities.getObjectIdHex(context, this) + " " + this.aggregate_class + " " + this.aggregate_class_validation_state + " (switch)"); }
        ValidationFailure vf = null;
        switch (this.aggregate_class_validation_state) {
            case -2:
                break;
            case 0:
                break;
            case 1:
                if (crash_debug) { SystemContext.err.println(context, "FA " + SystemUtilities.getObjectIdHex(context, this) + " " + this.aggregate_class + " " + this.aggregate_class_validation_state + " (ValidationFailure)"); }
                vf = ValidationFailure.createValidationFailure(context, FactorySiteStringPool.get(context, "1.2.1" /* Factory_Aggregate: parameter not set: aggregate class */));
                break;
            case 2:
                if (invalid_locator != null) {
                    vf = ValidationFailure.createValidationFailure(context, invalid_locator, FactorySiteStringPool.get(context, "1.2.14" /* Could not find object construction plan (ocp/xml): '%(ocp)' */), "ocp", this.aggregate_class);
                } else {
                    vf = ValidationFailure.createValidationFailure(context, FactorySiteStringPool.get(context, "1.2.14" /* Could not find object construction plan (ocp/xml): '%(ocp)' */), "ocp", this.aggregate_class);
                }
                break;
            case 3:
                vf = ValidationFailure.createValidationFailure(context, "No folder AggregateClass defined for %({'','Factories of '}[factory_tree])'%(aggregate_root_class)' (config entry ...%({'','Factory'}[factory_tree])FolderAggregateClassForClass.%(aggregate_root_class))", "aggregate_root_class", ocp_finder.aggregate_root_class, "factory_tree", ocp_finder.factory_tree);
                break;
            case 4:
                vf = ValidationFailure.createValidationFailure(context, "Could not find folder '%(tree_folder)'", "tree_folder", ocp_finder.tree_folder_aggregate_part);
                break;
            case 5:
                vf = ValidationFailure.createValidationFailure(context, type_mismatch, "The type of parameter '%(parameter)' of factory site does not match", "parameter", type_mismatch_parameter);
                break;
            case 6:
                vf = ValidationFailure.createValidationFailure(context, type_mismatch, "The role type of aggregate '%(aggregate)' does not exist", "aggregate", this.aggregate_class);
                break;
            case 7:
                vf = ValidationFailure.createValidationFailure(context, type_mismatch, "A role aggregate '%(aggregate)' was specified, but not exactly one parameter given", "aggregate", this.aggregate_class);
                break;
            case 8:
                vf = ValidationFailure.createValidationFailure(context, type_mismatch, "No aggregate is registered for '%(aggregate)'", "aggregate", this.aggregate_class);
                break;
        }
        if (vf != null) {
            if (runtime_step != null) { runtime_step.setFailed(context, vf, "Aggregate class validation failed"); runtime_step = null; }
            throw vf;
        }
        if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate class successfully validated"); runtime_step = null; }
    }

    protected Type refineTypes(CallContext context, Type type1, Type type2, boolean both_ways, String explanation) {
        if (type1 == null) {
            return type2;
        } else if (type2 == null) {
            return type1;
        } else if (type1.equals(type2)) {
            return type1;
        } else if (type2.isA(context, type1)) {
            return type2;
        } else if (both_ways == false) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "OCP parent types (ctn, xmlns, etc.) '%(type1)' and '%(type2)' are incompatible, the latter is not a refinement of the former (%(explanation))", "type1", type1, "type2", type2, "explanation", explanation);
            throw (ExceptionConfigurationError) null; // compiler insists
        } else if (type1.isA(context, type2)) {
            return type1;
        } else {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "OCP parent types (ctn, xmlns, etc.) '%(type1)' and '%(type2)' are incompatible, neither is derived from the other one (%(explanation))", "type1", type1, "type2", type2, "explanation", explanation);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    public FactorySiteOptionalParameter optional(CallContext context, Object value) {
        return new FactorySiteOptionalParameter(context, value);
    }

    @OCPIgnore
    public void setParameters(CallContext context, Object... arguments) {
        setParameters(context, makeMap(context, false, arguments));
    }

    @OCPIgnore
    public void trySetParameters(CallContext context, Object... arguments) {
        setParameters(context, makeMap(context, true, arguments));
    }

    public void setParameters(CallContext context, java.util.Map parameters) {
        this.parameters = parameters;
        this.parameters_validation_state = -1;
        this.pvs_cause = null;
    }

    public void validateParameters(CallContext call_context) throws ValidationFailure {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        if (this.parameters_validation_state == -1) {
            if (this.parameters == null) {
                this.parameters_validation_state = 1;
            } else {
                    this.parameters_validation_failure = fs.validateParameters(context, this.parameters);
                    if (this.parameters_validation_failure != null) {
                        this.parameters_validation_state = 4;
                    } else {
                        this.parameters_validation_state = 0;
                    }
            }
        }
        switch (this.parameters_validation_state) {
            case 0:
                return;
            case 1:
                ValidationFailure.createAndThrow(context, FactorySiteStringPool.get(context, "1.2.2" /* Factory_Aggregate: parameter not set: parameters */));
                throw (ValidationFailure) null; // compiler insists
            case 4:
                throw this.parameters_validation_failure;
        }
    }

    public FactorySite getFactorySite (CallContext context) {
        try {
            validateAggregateClass(context);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, FactorySiteStringPool.get(context, "1.2.15" /* Parameter 'AggregateClass' ('%(aggregateclass)') is invalid */), "aggregateclass", this.aggregate_class);
        }

        return this.fs;
    }

    public Vector<Named<Type>> getSignature (CallContext context) {
        FactorySite fs = this.getFactorySite(context);
        Map_DataSourceConnector_String_ map = fs.getParameters(context);

        Vector<Named<Type>> result = new Vector<Named<Type>>();

        for (IteratorItemIndex_DataSourceConnector_String_ iiidscs = map.getNavigator(context);
             iiidscs.canGetCurrent(context);
             iiidscs.next(context)) {
            String              name = iiidscs.tryGetCurrentIndex(context);
            DataSourceConnector dsc  = iiidscs.tryGetCurrent(context);

            result.add(new Named<Type>(context, name, dsc.getType(context)));
        }

        return result;
    }


    public HashMap<String,Vector<Pair<String,Object>>> getMetaData (CallContext context) {
        try {
            validateAggregateClass(context);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, FactorySiteStringPool.get(context, "1.2.15" /* Parameter 'AggregateClass' ('%(aggregateclass)') is invalid */), "aggregateclass", this.aggregate_class);
        }

        return this.fs.getMetaData(context);
    }

    public Vector<Pair<String,Object>> getMetaData (CallContext context, String key) {
        try {
            validateAggregateClass(context);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, FactorySiteStringPool.get(context, "1.2.15" /* Parameter 'AggregateClass' ('%(aggregateclass)') is invalid */), "aggregateclass", this.aggregate_class);
        }

        return this.fs.getMetaData(context, key);
    }

    public Object getMetaData (CallContext context, String key, String path_filter) {
        Vector<Pair<String,Object>> vpso = this.getMetaData(context, key);
        if (vpso == null) { return null; }
        for (Pair<String,Object> pso : vpso) {
            if (pso.getItem1(context).matches(path_filter)) {
                return pso.getItem2(context);
            }
        }
        return null;
    }

    public void registerDerived (CallContext context, Factory_Aggregate factory_aggregate) {
        try {
            validateAggregateClass(context);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, FactorySiteStringPool.get(context, "1.2.15" /* Parameter 'AggregateClass' ('%(aggregateclass)') is invalid */), "aggregateclass", this.aggregate_class);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        if (this.polymorphic != null && polymorphic.length() != 0) {
            if (this.cache_entry.aggregate_registry == null) {
                this.cache_entry.aggregate_registry = (AggregateRegistry) ReflectionUtilities.newInstance(context, ReflectionUtilities.getConstructor(context, "com.sphenon.engines.factorysite.factories.AggregateRegistry_" + this.polymorphic, CallContext.class), context);
            }

            String derived_aggregate_class = factory_aggregate.getAggregateClass(context);
            String my_aggregate_class = this.getAggregateClass(context);
            if (   derived_aggregate_class != null
                && derived_aggregate_class.equals(my_aggregate_class) == false) {
                this.cache_entry.aggregate_registry.registerFactoryAggregate(context, factory_aggregate);
            }
        }

        if (base_aggregate != null) {
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Factory_Aggregate[%(oid)]: base aggregate got base aggregate '%(base_aggregate)', registering ('%(aggregateclass)')...", "oid", this, "base_aggregate", base_aggregate, "aggregateclass", this.aggregate_class); }
            Factory_Aggregate base_fa = new Factory_Aggregate(context);
            base_fa.setAggregateClass(context, base_aggregate);
            base_fa.setAggregateTreeNode(context, base_tree_node);
            base_fa.registerDerived(context, factory_aggregate);
        }
    }

    protected Object tryPolymorphicCreation (CallContext context, int ip) {
        if (this.polymorphic != null && polymorphic.length() != 0) {
            if (this.cache_entry.aggregate_registry != null) {
                Factory_Aggregate factory_aggregate = this.cache_entry.aggregate_registry.tryGet(context, this.getFactorySite(context), parameters);
                if (factory_aggregate != null) {
                    
                    factory_aggregate.setParameters(context, parameters);
                    factory_aggregate.setExpectedType(context, expected_type);
                    
                    return factory_aggregate.create(context, ip);
                }
            }
        }
        return null;
    }

    @com.sphenon.engines.aggregator.annotations.OCPIgnore
    public Object createReloadable (CallContext context) {
        Object result = this.create(context);
        return Reloadable.wrap(context, result, this);
    }

    public Object create(CallContext context) {
        return create(context, -1);
    }

    public Object create(CallContext context, int ip) {
        return create(context, ip, true);
    }

    public Object createUnwrapped(CallContext context) {
        return create(context, -1, false);
    }

    public Object create(CallContext call_context, int ip, boolean optionally_wrapped) {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        RuntimeStep runtime_step = null;
        if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { runtime_step = RuntimeStep.create(context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Creating '%(id)'", "id", this.getId(context)); }

        try {
            validateAggregateClass(context);
        } catch (ValidationFailure vf) {
            if (runtime_step != null) { runtime_step.setFailed(context, vf, "Aggregate class validation failed"); runtime_step = null; }
            cc.throwPreConditionViolation(context, vf, FactorySiteStringPool.get(context, "1.2.15" /* Parameter 'AggregateClass' ('%(aggregateclass)') is invalid */), "aggregateclass", this.aggregate_class);
        }

        try {
            validateParameters(context);
        } catch (ValidationFailure vf) {
            if (runtime_step != null) { runtime_step.setFailed(context, vf, "Parameter validation failed"); runtime_step = null; }
            cc.throwPreConditionViolation(context, vf, "Parameters for aggregate ('%(aggregateclass)') are invalid", "aggregateclass", this.aggregate_class);
        }

        if (this.aggregate_cache != null && this.aggregate_cache_parameter != null) {
            Object o = this.aggregate_cache.get(this.parameters.get(this.aggregate_cache_parameter));
            if (o != null) {
                if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully created (reused from cache)"); runtime_step = null; }
                return o;
            }
        }

        if (this.search_path_context != null) {
            TypeContext tc = TypeContext.create(context);
            tc.setSearchPathContext(context, this.search_path_context);
        }

        Object o = tryPolymorphicCreation(context, ip);

        if (o == null) {

            try {
                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) {
                    cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, FactorySiteStringPool.get(context, "1.2.12" /* Factory_Aggregate[%(oid)].create(): building aggregate... */), "oid", this);
                }
                o = fs.build(context, this.parameters, ip);
            } catch (BuildFailure e) {
                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.dump(context, "BuildText", this.bt, Notifier.SELF_DIAGNOSTICS); }
                
                if (runtime_step != null) { runtime_step.setFailed(context, e, "Build failed"); runtime_step = null; }
                cc.throwConfigurationError(context, e, "Could not build object aggregate ('%(aggregateclass)')", "aggregateclass", this.aggregate_class);
                throw (ExceptionConfigurationError) null; // compiler insists
            }

            // here we set the _origin_, i.e. a dynamically updated instance
            // which reflects the current last modification of the origin
            if (o != null && o instanceof OriginSlot) {
                ((OriginSlot) o).setOrigin(context, this.getOrigin(context));
            }

            // here we set the hypothetical last modification of the created
            // aggregate, which is the last modification of the origin at
            // creation time - of course the aggregate might have been
            // instantiated later than that, but it reflects a "version"
            // of the instance of that point in time
            if (o != null && o instanceof TimestampSlot) {
                long origin_lm = this.getLastModification(context);
                
                // we modify it here in any case, the method might internally decide
                // to check whether the aggregate is considered to be even newer,
                // so, maybe after calling get afterwards the value returned is not
                // identical to the value set here
                ((TimestampSlot) o).setLastModification(context, origin_lm);
                // the reason is, that the following check caused some ondemand
                // aggregates to load the whole universe, which is not so good
                // if (origin_lm > ((TimestampSlot) o).getLastModification(context)) {
                // }
            }

            // better not, sonst wird grundsätzlich an jedes coreobject das angehängt
            // bei bedarf: wenn ein objekt das haben will, dieses Objekt von OriginSlot
            // ableiten, und es kann ja intern den folgenden code verwenden und das an
            // den accessories speichern
            //
            //    static public Type origin_type;
            //             if (o != null && o instanceof CoreObject) {
            //                 if (origin_type == null) {
            //                     origin_type = TypeManager.get(context, com.sphenon.basics.tracking.Origin.class);
            //                 }
            //                 OSet_Object_Type_ accessories = ((CoreObject) o).getAccessorys(context);
            //                 if (accessories.tryGet(context, origin_type) == null) {
            //                     accessories.set(context, this.getOrigin(context));
            //                 }                
            //             }
        }

        if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully created"); runtime_step = null; }

        if (optionally_wrapped) {
            String wac = this.ocp_finder.wrapperaggregateclass;

            // performance: maybe cache for this property, but presently only used by TSX ocps
            if (this.ocp_finder.wrapperaggregateclasses != null) {
                String[] wacs = this.ocp_finder.wrapperaggregateclasses.split(";");
                for (String wac4c : wacs) {
                    String[] kv = wac4c.split("=");
                    Type t;
                    try {
                        t = TypeManager.get(context, kv[0]);
                    } catch (NoSuchClass nsc) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, nsc, "Class '%(class)' in property 'WrapperAggregateClasses4Class' not found", "class", kv[0]);
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                    Type ot = TypeManager.get(context, o);
                    if (ot.isA(context, t)) {
                        wac = Encoding.recode(context, kv[1], Encoding.URI, Encoding.UTF8);
                    }
                }
            }
            
            if (wac != null) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                    cc.sendTrace(context, Notifier.DIAGNOSTICS, "Wrapping instance in a '%(wrapper)'", "wrapper", ocp_finder.wrapperaggregateclass);
                }
                Factory_Aggregate fa = new Factory_Aggregate(context);
                fa.setAggregateClass(context, ocp_finder.wrapperaggregateclass);
                java.util.Hashtable h = new java.util.Hashtable();
                h.put("AGGREGATE", new FactorySiteOptionalParameter(context, o));
                h.put("AGGREGATECLASS", new FactorySiteOptionalParameter(context, "ctn://Class<Aggregate>/" + this.aggregate_class));
                fa.setParameters(context, h);
                Object wrapper = fa.create(context);
                if (wrapper != null && wrapper instanceof OriginSlot) {
                    ((OriginSlot) wrapper).setOrigin(context, this.getOrigin(context));
                }
                o = wrapper;
            }
        }

        if (this.aggregate_cache != null && this.aggregate_cache_parameter != null) {
            this.aggregate_cache.put(this.parameters.get(this.aggregate_cache_parameter), o);
        }

        return o;
    }

    public void performPasses(CallContext context, int ip) {
        try {
            Factory_Aggregate factory_aggregate;
            if (    this.polymorphic != null && polymorphic.length() != 0
                 && this.cache_entry.aggregate_registry != null
                 && (factory_aggregate = this.cache_entry.aggregate_registry.tryGet(context, this.getFactorySite(context), parameters)) != null) {
                factory_aggregate.performPasses(context, ip);
                return;
            }
            fs.performPasses(context, ip);
        } catch (BuildFailure e) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, e, "Could not complete build of object aggregate ('%(aggregateclass)'), failed in deferred pass processing", "aggregateclass", this.aggregate_class);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    protected Origin origin;

    public Origin getOrigin (CallContext context) {
        if (this.origin == null) {
            this.origin = new Origin() {
                    public String[] getTrack (CallContext context) {
                        return Factory_Aggregate.this.getTrack(context);
                    }
                    public long getLastModification(CallContext context) {
                        return Factory_Aggregate.this.getLastModification(context);
                    }   
                };
        }
        return this.origin;
    }

    protected String[] track;

    public String[] getTrack (CallContext context) {
        if (this.track == null) {
            this.track = new String[1];
            this.track[0] = this.getFactorySite(context).getSiteId(context);
        }
        return this.track;
    }

    public long getLastModification (CallContext context) {

        try {
            if (crash_debug) { SystemContext.err.println(context, "FA " + SystemUtilities.getObjectIdHex(context, this) + " " + this.aggregate_class + " " + this.aggregate_class_validation_state + " (last modification, validating...)"); }
            validateAggregateClass(context);
            if (crash_debug) { SystemContext.err.println(context, "FA " + SystemUtilities.getObjectIdHex(context, this) + " " + this.aggregate_class + " " + this.aggregate_class_validation_state + " (last modification, validated)"); }
        } catch (ValidationFailure vf) {
            if (crash_debug) { SystemContext.err.println(context, "FA " + SystemUtilities.getObjectIdHex(context, this) + " " + this.aggregate_class + " " + this.aggregate_class_validation_state + " (catch failure)"); }
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, FactorySiteStringPool.get(context, "1.2.15" /* Parameter 'AggregateClass' ('%(aggregateclass)') is invalid */), "aggregateclass", this.aggregate_class);
        }

        long actual_last_modification = this.fs.getLastModification(context);

        for (long i=0; i<this.ocp_finder.ocp_tnodes_to_check.getSize(context); i++) {
            long lm = this.ocp_finder.ocp_tnodes_to_check.tryGet(context, i).getLastModification(context);
            if (lm > actual_last_modification) { actual_last_modification = lm; }
        }
    
        return actual_last_modification;
    }

    protected String           cocp_class;
    protected String           cocp_path;
    protected String           cocp_name;
    protected DynamicCOCPClass dcocpc;

    protected void prepareDynamicCOCPClass(CallContext context) {
        if (this.cocp_class == null && this.cocp_ignore == false && (this.ocp_compiler_enabled || this.cocp_loader_enabled)) {
            this.cocp_class = (this.aggregate_class == null ? null : this.aggregate_class.replaceFirst(";class=Object$", ""));
            if (this.cocp_class == null) {
                String tn_path    = this.aggregate_tree_node.getPath(context);
                String sha_code   = Encoding.recode_UTF8_SHA1(context, tn_path);
                String final_part = tn_path.replaceFirst("^.*/", "").replaceFirst("\\.[^\\.]$", "");
                String safe_hint  = Encoding.recode(context, final_part, Encoding.UTF8, Encoding.VSA);
                this.cocp_path = "cache.cocp.treenodes";
                this.cocp_name = "COCP_" + safe_hint + "_SHA1_" + sha_code;
                this.cocp_class = this.cocp_path + "." + this.cocp_name;
            } else {
                int pos = this.cocp_class.lastIndexOf("/");
                this.cocp_path = "cache.cocp.classes" + (pos == -1 ? "" : ("." + this.cocp_class.substring(0, pos).replaceFirst("^(?:(?:ctn|oorl):)?/+([A-Za-z0-9_]+)/","__$1_").replaceAll("/",".").replaceAll("[^A-Za-z0-9_]","_").replaceAll("\\.(class|interface|package)\\b","\\._$1")));
                this.cocp_name = pos == -1 ? this.cocp_class : this.cocp_class.substring(pos+1);
                this.cocp_name = "COCP_" + Encoding.recode(context, this.cocp_name, Encoding.UTF8, Encoding.VSA);
                this.cocp_class = this.cocp_path + "." + this.cocp_name;
            }
            this.dcocpc = new DynamicCOCPClass(context, this.cocp_class, null, this.ocp_finder.ocp_tnodes_to_check);
        }
    }

    protected boolean is_compiled_ocp;
    protected boolean needs_generation;

    protected BuildText tryLoadCompiledOCP(CallContext context) {
        this.is_compiled_ocp = false;
        this.needs_generation = false;

        if (this.cocp_loader_enabled == false || this.cocp_ignore) { return null; }

        this.prepareDynamicCOCPClass(context);

        if (this.dcocpc.needsGeneration(context)) {
            this.needs_generation = true;
            return null;
        }

        COCPBuildTextFactory cocpbtf;
        try {
            cocpbtf = this.dcocpc.createInstance(context, false, true, true);
        } catch (Throwable t) {
            if (this.cocp_ignore_missing_classes) {
                return null;
            } else {
                throw t;
            }
        }

        BuildText bt        = cocpbtf.create(context);

        this.is_compiled_ocp = true;

        this.xmlns          = cocpbtf.getNameSpace(context);
        this.ocpid          = cocpbtf.getOCPId(context);
        this.polymorphic    = cocpbtf.getPolymorphic(context);
        this.original_base  = cocpbtf.getBase(context);

        Object[] bat = prepareBaseInfo(context, original_base);
        String   bag = (String) bat[0];
        TreeNode btn = (TreeNode) bat[1];
        this.base_aggregate = bag;
        this.base_tree_node = btn;

        return bt;
    }

    protected boolean code_already_generated = false;

    protected JavaCodeManager optionallyOpenJavaCodeManager(CallContext context) {

        if (ocp_compiler_enabled == false || code_already_generated || this.is_compiled_ocp || this.cocp_ignore) { return null; }

        this.prepareDynamicCOCPClass(context);

        if (this.needs_generation == false) {
            return null;
        }

        JavaCodeManager jcm = this.dcocpc.getJavaCodeManager(context);
        BufferedWriter bw = jcm.getDefaultResource(context).getWriter(context);

        try {
            bw.write("package " + this.cocp_path + ";\n");
            bw.write("\n");
            bw.write("import com.sphenon.basics.context.*;\n");
            bw.write("import com.sphenon.basics.configuration.*;\n");
            bw.write("import com.sphenon.basics.exception.*;\n");
            bw.write("import com.sphenon.basics.notification.*;\n");
            bw.write("import com.sphenon.basics.customary.*;\n");
            bw.write("import com.sphenon.basics.metadata.TypeManager;\n");
            bw.write("\n");
            bw.write("import com.sphenon.engines.factorysite.*;\n");
            bw.write("import com.sphenon.engines.factorysite.BuildText;\n");
            bw.write("import com.sphenon.engines.factorysite.returncodes.*;\n");
            bw.write("import com.sphenon.engines.factorysite.exceptions.*;\n");
            bw.write("import com.sphenon.engines.factorysite.tplinst.*;\n");
            bw.write("\n");
            bw.write("import java.util.Vector;\n");
            bw.write("import java.util.HashMap;\n");
            bw.write("\n");

            {
                Vector_String_long_ sp = TypeManager.getSearchPath(context);
                if (sp != null) {
                    for (String spe : sp.getIterable_String_(context)) {
                        if (spe != null && spe.length() != 0) {
                            bw.write("import " + spe + ".*;\n");
                        }
                    }
                    bw.write("\n");
                }
            }

            bw.write("public class " + this.cocp_name + " extends COCPBuildTextFactory {\n");
            bw.write("\n");
            bw.write("    public " + this.cocp_name + "(CallContext context) {\n");
            bw.write("        super(context, " +
                     (this.xmlns == null ? "null" : ("\"" + Encoding.recode(context, this.xmlns, Encoding.UTF8, Encoding.JAVA) + "\"")) +
                     ", " +
                     (this.ocpid == null ? "null" : ("\"" + Encoding.recode(context, this.ocpid, Encoding.UTF8, Encoding.JAVA) + "\"")) +
                     ", " +
                     (this.polymorphic == null ? "null" : ("\"" + Encoding.recode(context, this.polymorphic, Encoding.UTF8, Encoding.JAVA) + "\"")) +
                     ", " +
                     (this.original_base == null ? "null" : ("\"" + Encoding.recode(context, this.original_base, Encoding.UTF8, Encoding.JAVA) + "\"")) +
                     ");\n");
            bw.write("    }\n");
            bw.write("\n");
            bw.write("    static protected BuildText bt;\n");
            bw.write("\n");
            bw.write("    public BuildText create(CallContext context) {\n");
            bw.write("        if (bt == null) {\n");
            bw.write("            bt = (BuildText) createItem(context, COCPIndices.COCPItem_BuildText, \"\");\n");
            bw.write("        }\n");
            bw.write("        return bt;\n");
            bw.write("    }\n");
            bw.write("\n");
            bw.write("    protected int[][] getCOCPData(CallContext context) {\n");
            bw.write("        return cocp_data;\n");
            bw.write("    }\n");
            bw.write("\n");
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Cannot write to '%(file)'", "file", jcm.getDefaultResource(context).getJavaFilePath(context));
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }

        return jcm;
    }

    protected void optionallyProcessGeneratedJavaCode(CallContext context, JavaCodeManager jcm) {
        if (ocp_compiler_enabled && jcm != null) {           

            BufferedWriter bw = jcm.getDefaultResource(context).getWriter(context);

            try {
                bw.write("}\n");
            } catch (IOException ioe) {
                CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Cannot write to '%(file)'", "file", jcm.getDefaultResource(context).getJavaFilePath(context));
                throw (ExceptionEnvironmentFailure) null; // compiler insists
            }

            jcm.closeResources(context);
            this.dcocpc.notifyCodeGenerationCompleted(context);
            this.code_already_generated = true;
            this.needs_generation = false;

            if (this.ocp_compiler_immediate_compilation) {
                COCPBuildTextFactory cocpbtf = this.dcocpc.createInstance(context, false, true, true);
            }
        }
    }
    
    public static boolean debug_classloader = false;
    
    public static void debugClassLoader(Object... objects) {
        if (objects != null) {
            String message = "";
            for (Object obj : objects) {
                if (obj instanceof String) {
                    String msg = (String) obj;
                    message += msg;
                }
                if (obj instanceof Class) {
                    Class clazz = (Class)obj;
                    String cls_name = clazz.getName();
                    String cls_loader = clazz.getClassLoader() != null ? clazz.getClassLoader().toString() : "no classloader";
                    message += " | " + "CLASS:" + cls_name;
                    message += " | " + "CLASSLOADER:" + cls_loader;
                }
            }
            System.out.println(message);
        }
    }

    static protected String aggregate_factory_cache_file;
    static protected RegularExpression aggregate_factory_cache_class_include;
    static protected RegularExpression aggregate_factory_cache_class_exclude;
    static protected RegularExpression aggregate_factory_cache_treenode_include;
    static protected RegularExpression aggregate_factory_cache_treenode_exclude;

    static public void saveAggregateFactoryCacheOnExit(CallContext context) {
        aggregate_factory_cache_file = config.get(context, "AggregateFactoryCache.File", (String) null);
        aggregate_factory_cache_class_include    = RegularExpression.optinallyCreate(context, config.get(context, "AggregateFactoryCache.Class.Include", (String) null));
        aggregate_factory_cache_class_exclude    = RegularExpression.optinallyCreate(context, config.get(context, "AggregateFactoryCache.Class.Exclude", (String) null));
        aggregate_factory_cache_treenode_include = RegularExpression.optinallyCreate(context, config.get(context, "AggregateFactoryCache.TreeNode.Include", (String) null));
        aggregate_factory_cache_treenode_exclude = RegularExpression.optinallyCreate(context, config.get(context, "AggregateFactoryCache.TreeNode.Exclude", (String) null));
        java.lang.Runtime.getRuntime().addShutdownHook(new Thread() { public void run() { saveAggregateFactoryCache(RootContext.getDestructionContext()); } });
    }

    static protected int afc_verbose = 1; // 0: none, 1: summary, 2: detailed

    static protected Map<String,String[]> getAggregateFactoryCache(CallContext context) {
        int afc_read = 0;
        Map<String,String[]> cache = new HashMap<String,String[]>();
        String cache_content = config.get(context, "AggregateFactoryCache", (String) null);
        if (cache_content != null) {
            for (String entry : cache_content.split(",")) {
                String[] ep = entry.split("\\+",-1);
                cache.put(Encoding.recode(context, ep[0], Encoding.URI, Encoding.UTF8),
                          new String[] {
                              Encoding.recode(context, ep[1], Encoding.URI, Encoding.UTF8),
                              Encoding.recode(context, ep[2], Encoding.URI, Encoding.UTF8),
                              Encoding.recode(context, ep[3], Encoding.URI, Encoding.UTF8),
                              Encoding.recode(context, ep[4], Encoding.URI, Encoding.UTF8),
                              Encoding.recode(context, ep[5], Encoding.URI, Encoding.UTF8)
                          });
                afc_read++;
            }
        }
        if (afc_verbose >= 1) {
            System.err.println("Aggregate Factory Cache: read " + afc_read);
        }
        return cache;
    }

    static protected String[] environment_variables_to_substitute = { "WORKSPACE" };
    static protected RegularExpression file_locator = new RegularExpression("^((?:(?:ctn|oorl):)?//File/)(.*)$");

    static protected String substituteEnvironment(CallContext context, String path) {
        String[] flm = file_locator.tryGetMatches(context, path);
        if (flm != null) {
            for (String evn : environment_variables_to_substitute) {
                String evv = java.lang.System.getenv(evn);
                if (flm[1].startsWith(evv)) {
                    return flm[0] + "${" + evn + "}" + flm[1].substring(evv.length());
                }
            }
        }
        return path;
    }

    static public void saveAggregateFactoryCache(CallContext context) {
        int afc_written = 0;
        int afc_skipped = 0;
        try {
            if (aggregate_factory_cache_file != null) {
                File f = new File(aggregate_factory_cache_file);
                f.setWritable(true);
                FileOutputStream fos = new FileOutputStream(f);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                BufferedWriter bw = new BufferedWriter(osw);
                PrintWriter pw = new PrintWriter(bw);

                pw.print("com.sphenon.engines.factorysite.factories.Factory_Aggregate.AggregateFactoryCache=");

                Map<String,String[]> cache = getAggregateFactoryCache(context);

                for (String cache_key : factory_sites.keySet()) {
                    CacheEntry ce = factory_sites.get(cache_key);

                    if (    (    (ce.aggregate_class != null && ce.aggregate_class.isEmpty() == false)
                              || (ce.aggregate_tree_node != null && ce.aggregate_tree_node.isEmpty() == false)
                            )
                         && ce.aggregate_build_text == false
                       ) {

                        boolean cinc = (    aggregate_factory_cache_class_include == null
                                         || ce.aggregate_class == null
                                         || aggregate_factory_cache_class_include.matches(context, ce.aggregate_class) == true);
                        boolean cexc = (    aggregate_factory_cache_class_exclude == null
                                         || ce.aggregate_class == null
                                         || aggregate_factory_cache_class_exclude.matches(context, ce.aggregate_class) == false);
                        boolean tinc = (    aggregate_factory_cache_treenode_include == null
                                         || ce.aggregate_tree_node == null
                                         || aggregate_factory_cache_treenode_include.matches(context, ce.aggregate_tree_node) == true);
                        boolean texc = (    aggregate_factory_cache_treenode_exclude == null
                                         || ce.aggregate_tree_node == null
                                         || aggregate_factory_cache_treenode_exclude.matches(context, ce.aggregate_tree_node) == false);
                        if (cinc && cexc && tinc && texc) {
                            String ac  = (ce.aggregate_class == null ? "" : substituteEnvironment(context, ce.aggregate_class));
                            String atn = (ce.aggregate_tree_node == null ? "" : substituteEnvironment(context, ce.aggregate_tree_node));
                            if (afc_verbose >= 2) {
                                System.err.println("AFC WRITTEN: " + ac + " - " + atn);
                            }
                            afc_written++;
                            cache.put(cache_key, new String[] {
                                                     ac,
                                                     atn,
                                                     (ce.aggregate_target_class == null ? "" : ce.aggregate_target_class),
                                                     (ce.expected_type == null ? "" : ce.expected_type),
                                                     (ce.name_space == null ? "" : ce.name_space)
                                                 });
                        } else {
                            if (afc_verbose >= 2) {
                                System.err.println("AFC SKIPPED: " + (ce.aggregate_class == null ? "" : ce.aggregate_class) + " - " + (ce.aggregate_tree_node == null ? "" : ce.aggregate_tree_node));
                            }
                            afc_skipped++;
                        }
                    }
                }
 
                boolean first = true;

                for (String cache_key : cache.keySet()) {
                    if (first) { first = false; } else { pw.print(","); }
                    String[] entry = cache.get(cache_key);
                    pw.print(Encoding.recode(context, cache_key, Encoding.UTF8, Encoding.URI));
                    pw.print("+");
                    pw.print(Encoding.recode(context, entry[0], Encoding.UTF8, Encoding.URI));
                    pw.print("+");
                    pw.print(Encoding.recode(context, entry[1], Encoding.UTF8, Encoding.URI));
                    pw.print("+");
                    pw.print(Encoding.recode(context, entry[2], Encoding.UTF8, Encoding.URI));
                    pw.print("+");
                    pw.print(Encoding.recode(context, entry[3], Encoding.UTF8, Encoding.URI));
                    pw.print("+");
                    pw.print(Encoding.recode(context, entry[4], Encoding.UTF8, Encoding.URI));
                }

                pw.println("");

                pw.close();
                bw.close();
                osw.close();
                fos.close();
            }
        } catch (FileNotFoundException fnfe) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, fnfe, "Cannot write to file '%(filename)'", "filename", aggregate_factory_cache_file);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        } catch (UnsupportedEncodingException uee) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, uee, "Cannot write to file '%(filename)'", "filename", aggregate_factory_cache_file);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ioe, "Cannot write to file '%(filename)'", "filename", aggregate_factory_cache_file);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        if (afc_verbose >= 1) {
            System.err.println("Aggregate Factory Cache: written " + afc_written + ", skipped " + afc_skipped);
        }
    }

    static public void loadCachedAggregates(CallContext context) {
        Map<String,String[]> cache = getAggregateFactoryCache(context);

        for (String cache_key : cache.keySet()) {
            String[] entry = cache.get(cache_key);
            String aggregate_class = Encoding.recode(context, entry[0], Encoding.URI, Encoding.UTF8);
            String aggregate_tree_node = Encoding.recode(context, entry[1], Encoding.URI, Encoding.UTF8);
            String aggregate_target_class = Encoding.recode(context, entry[2], Encoding.URI, Encoding.UTF8);
            String expected_type = Encoding.recode(context, entry[3], Encoding.URI, Encoding.UTF8);
            String name_space = Encoding.recode(context, entry[4], Encoding.URI, Encoding.UTF8);

            Factory_Aggregate cf = new Factory_Aggregate(context);

            cf.setAggregateClass(context, /* DELETEME - aggregate_class.isEmpty() ? null : */ aggregate_class);
            cf.setAggregateTreeNode(context, aggregate_tree_node.isEmpty() ? null : Factory_TreeNode.tryConstruct(context, aggregate_tree_node));
            cf.setAggregateTargetClass(context, aggregate_target_class.isEmpty() ? null : aggregate_target_class);
            cf.setExpectedType(context, expected_type.isEmpty() ? null : TypeManager.tryGet(context, expected_type));
            cf.setNameSpace(context, name_space.isEmpty() ? null : name_space);
            cf.setUseCache(context, true);

            // System.err.println("Loading Aggregate: " + aggregate_class + ", " + aggregate_tree_node + ", " + aggregate_target_class + ", " + expected_type + ", " + name_space);

            if (cf.isValidNameSpace(context)) {
                cf.getFactorySite(context);
            } else {
                if ((notification_level & Notifier.OBSERVATION) != 0) { NotificationContext.sendNotice(context, "Loading of aggregate '%(aggregate)' skipped, namespace not configured", "aggregate", join(context, entry, ",")); }
            }
        }
    }
}
