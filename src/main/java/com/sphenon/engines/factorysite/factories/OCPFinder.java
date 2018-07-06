package com.sphenon.engines.factorysite.factories;

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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.message.classes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.factory.returncodes.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.tplinst.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.graph.files.factories.*;
import com.sphenon.basics.graph.javaresources.factories.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.util.regex.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.Hashtable;
import java.util.List;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

public class OCPFinder {
    static protected Configuration config;
    static public Configuration getConfiguration(CallContext context) { return config; }
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.factories.Factory_Aggregate"); };

    private String aggregate_class;
    private TreeNode aggregate_tree_node;
    public BuildText aggregate_build_text;

    static private String path_delimiter;

    static protected String[][] ocp_variants;

    static protected RegularExpression tree_re = new RegularExpression("Tree<(.*)>");
    static protected RegularExpression fac_re = new RegularExpression("Factory<(.*)>");
    static protected RegularExpression ctn_re = new RegularExpression("^(?:(?:ctn|oorl):)?(//.*)$");

    static {
        CallContext context = RootContext.getInitialisationContext();
        config = Configuration.create(context, "com.sphenon.engines.factorysite.factories.Factory_Aggregate");
        path_delimiter = config.get(context, "PathDelimiter", ":");

        String ovs = config.get(context, "OCPVariants", (String) null);
        List<String> ova = null;
        if (ovs != null) {
            if (ovs.equals("UseConfigurationVariants")) {
                ConfigurationContext cc = ConfigurationContext.getOrCreate((Context) context);
                ova = cc.getConfigurationVariants(context);
            } else {
                ova = ovs.length() == 0 ? null : java.util.Arrays.asList(ovs.split(":"));
            }
        }
        int i=0;
        if (ova != null) {
            ocp_variants = new String[ova.size() + 1][];
            for (String ov : ova) {
                if (ov == null || ov.length() == 0) {
                    ocp_variants[i++] = null;
                } else {
                    ocp_variants[i] = new String[2];
                    ocp_variants[i][0] = "-" + ov;
                    ocp_variants[i][1] = ov;
                    i++;
                }
            }
        } else {
            ocp_variants = new String[1][];
        }
        ocp_variants[i] = new String[2];
        ocp_variants[i][0] = "";
        ocp_variants[i][1] = "";
        i++;
    }

    public OCPFinder(CallContext call_context, String aggregate_class, TreeNode aggregate_tree_node, BuildText aggregate_build_text, String aggregate_target_class) {
        this.aggregate_class = aggregate_class;
        this.aggregate_tree_node = aggregate_tree_node;
        this.aggregate_build_text = aggregate_build_text;
        this.aggregate_target_class = aggregate_target_class;
    }

    public ReadOnlyVector_String_long_ tree_folders = null;
    public ReadOnlyVector_TreeNode_long_ tree_folder_nodes = null;
    public String aggregate_root_class = "Object";
    public String tree_aggregate_root_class = "Object";
    public String folderaggregateclass = "";
    public String tree_folder_aggregate_part = "";
    public String wrapperaggregateclass;
    public String wrapperaggregateclasses;
    public boolean factory_tree = false;

    protected String getSearchPath(CallContext context, String aggregate_root_class) {
        return Factory_Aggregate.getSearchPath(context, aggregate_root_class);
    }

    protected String concatPathes(CallContext context, String s1, String s2) {
        StringBuilder sb = new StringBuilder();
        if (s1 != null) { sb.append(s1); }
        if (   s1 != null && s1.isEmpty() == false
            && s2 != null && s2.isEmpty() == false && s2.charAt(0) != '/') { sb.append("/"); }
        if (s2 != null) { sb.append(s2); }
        return sb.toString();
    }

    public boolean found;
    public boolean is_tree;
    public boolean no_config;
    public boolean tree_folder_found;
    public TreeNode result_node;
    public String fullpath;
    public String tree_extension;
    public ReadOnlyVector_TreeNode_long_ ocp_tnodes_to_check;
    public String aggregate_target_class;

    static protected Map<String,String> cache_1;
    static protected Map<String,String[]> cache_2;
    static protected Map<String,String> cache_3;

    static public boolean ocp_finder_cache_debug = false;

    public void findOCP(CallContext context) throws ValidationFailure {
        findOCP(context, "");
    }

    public void findOCP(CallContext call_context, String main_extension) throws ValidationFailure {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        InvalidLocator invalid_locator = null;

        found = false;
        is_tree = false;
        no_config = false;
        tree_folder_found = true;
        result_node = null;
        fullpath = "";
        tree_extension = "";
        ocp_tnodes_to_check = null;

        aggregate_root_class = aggregate_target_class != null ? aggregate_target_class : "Object";
        tree_aggregate_root_class = "Object";
        tree_folders = null;
        String[] matches;

        boolean got_real_aggregate_class = false;
        Vector_TreeNode_long_ modifyable_ocp_tnodes_to_check = null;
        String my_aggregate_class = this.aggregate_class;
        String my_cache_key_3 = this.aggregate_class;
        String my_cache_key_2 = null;

        String variant_include = null;
        String variant_exclude = null;

        if (ocp_finder_cache_debug) { System.err.println("OCPFinder: " + this.aggregate_class); }
        if (ocp_finder_cache_debug) { System.err.println("         : " + this.aggregate_tree_node); }
        if (this.aggregate_class != null) {
            String[] parts = this.aggregate_class.split(";");
            if (parts.length > 1) {
                my_aggregate_class = parts[0];
                if (    parts[0].isEmpty()
                     && (    this.aggregate_tree_node != null
                          || this.aggregate_build_text != null
                        )
                   ) {
                    this.aggregate_class = null;
                    my_aggregate_class = null;
                    my_cache_key_3 = null;
                }
                for (int p=1; p<parts.length; p++) {
                    String[] kv = parts[p].split("=");
                    if (kv.length != 2) {
                        my_aggregate_class += ";" + parts[p];
                    } else {
                        if (kv[0].equals("class")) {
                            aggregate_root_class = kv[1];
                        } else if (kv[0].equals("variants")) {
                            String[] vf = kv[1].split(",");
                            if (vf.length != 2) {
                                my_aggregate_class += ";" + parts[p];
                            } else {
                                variant_include = Encoding.recode(context, vf[0], Encoding.URI, Encoding.UTF8);
                                variant_exclude = Encoding.recode(context, vf[1], Encoding.URI, Encoding.UTF8);
                            }
                        } else {
                            my_aggregate_class += ";" + parts[p];
                        }
                    }
                }
            }
        }
        if (    this.aggregate_class == null
             && this.aggregate_tree_node != null) {
            Location ln;
            List<Locator> vlr;
            Locator lr;
            String extension;
            int pos = -1;
            try {
                if (    (ln = this.aggregate_tree_node.getLocation(context)) != null
                     && (vlr = ln.getLocators(context)) != null
                     && vlr.size() != 0
                     && (lr = vlr.get(0)) != null
                     && (extension = lr.getResolvedTextLocatorValue(context)) != null
                     && (pos = extension.lastIndexOf('.')) != -1
                     && (extension = extension.substring(pos+1)).matches("[A-Za-z0-9_-]+")
                   ) {
                    aggregate_root_class = Factory_Aggregate.getProperty(context, "ClassForExtension." + extension, aggregate_root_class);
                }
            } catch (InvalidLocator il) {
                // ok
            }
        }

        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): aggregate class '%(aggregateclass)', aggregate root class '%(aggregaterootclass)'", "oid", this, "aggregaterootclass", aggregate_root_class, "aggregateclass", my_aggregate_class); }

        if (this.aggregate_tree_node != null) {
            found = true;
            result_node = this.aggregate_tree_node;
            TreeNode ptn = result_node.tryGetParent(context);

            String node_path = result_node.getPath(context); 
            String cache_entry = (cache_1 == null ? null : cache_1.get(node_path));
            if (cache_entry != null) {
                TreeNode ctn = ptn.tryGetChild(context, cache_entry);
                if (ctn != null && ctn.exists(context)) {
                    result_node = ctn;
                    if (ocp_finder_cache_debug) { System.err.println("OCP1:OK: " + cache_entry); }
                } else {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "OCPFinder cache entry '%(key)', '%(value)' is invalid", "key", node_path, "value", cache_entry);
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
            } else {
                if (ptn != null) {
                    String tnid = result_node.getId(context);
                    String ext = "";
                    int dot = tnid.lastIndexOf('.');
                    if (dot != -1) {
                        ext = tnid.substring(dot);
                        tnid = tnid.substring(0, dot);
                    }
                    LOOP: for (String[] ocp_variant : ocp_variants) {
                        if (    ocp_variant != null
                             && (variant_include == null || ocp_variant[1].matches(variant_include) == true)
                             && (variant_exclude == null || ocp_variant[1].matches(variant_exclude) == false)
                           ) {
                            String locator_to_try = tnid + ocp_variant[0] + ext;
                            TreeNode ctn = ptn.tryGetChild(context, locator_to_try);
                            if (ctn != null && ctn.exists(context)) {
                                result_node = ctn;
                                if (cache_1 == null) {
                                    cache_1 = new HashMap<String,String>();
                                }
                                cache_1.put(node_path, locator_to_try);
                                if (ocp_finder_cache_debug) { System.err.println("OCP1:OK: " + tnid + " - " + ocp_variant[0] + " - " + ext); }
                                break LOOP;
                            }
                            if (ocp_finder_cache_debug) { System.err.println("OCP1:--: " + tnid + " - " + ocp_variant[0] + " - " + ext); }
                        }
                    }
                }
            }

            ocp_tnodes_to_check = new ReadOnlyVectorImplSingle_TreeNode_long_(context, result_node);
        } else if (this.aggregate_build_text != null) {
            found = true;
        } else if ((matches = ctn_re.tryGetMatches(context, my_aggregate_class)) != null) {
            String ctn = matches[0];

            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): direct given locator: '%(ctn)'", "oid", this, "ctn", ctn); }

            Locator locator = null;
            Object o = null;
            try {
                locator = Locator.createLocator(context, ctn);
                o = locator.retrieveTarget(context);
            } catch (InvalidLocator il) {
                o = null;
                invalid_locator = il;
                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): direct given locator is invalid: '%(ctn)' - '%(reason)'", "oid", this, "ctn", ctn, "reason", il); }
            }
            if (o == null) {
                try {
                    result_node = Factory_TreeNode.construct(context, locator);
                    found = true;
                    ocp_tnodes_to_check = new ReadOnlyVectorImplSingle_TreeNode_long_(context, result_node);
                } catch (ValidationFailure vf) {
                    result_node = null;
                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): direct given locator '%(ctn)' is invalid, reason '%(reason)'", "oid", this, "ctn", ctn, "reason", vf); }
                }
            } else if (o instanceof TreeNode) {
                found = true;
                result_node = (TreeNode) o;
                ocp_tnodes_to_check = new ReadOnlyVectorImplSingle_TreeNode_long_(context, result_node);
            } else if (o instanceof File) {
                try {
                    result_node = Factory_TreeNode_File.construct(context, (File) o);
                    found = true;
                    ocp_tnodes_to_check = new ReadOnlyVectorImplSingle_TreeNode_long_(context, result_node);
                } catch (ValidationFailure vf) {
                    result_node = null;
                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): direct given locator '%(ctn)' is invalid, reason '%(reason)'", "oid", this, "ctn", ctn, "reason", vf); }
                }
            } else if (o instanceof JavaResource) {
                try {
                    result_node = Factory_TreeNode_JavaResource.construct(context, (JavaResource) o);
                    found = true;
                    ocp_tnodes_to_check = new ReadOnlyVectorImplSingle_TreeNode_long_(context, result_node);
                } catch (ValidationFailure vf) {
                    result_node = null;
                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): direct given locator '%(ctn)' is invalid, reason '%(reason)'", "oid", this, "ctn", ctn, "reason", vf); }
                }
            } else if (o instanceof BuildText) {
                this.aggregate_build_text = (BuildText) o;
                found = true;
            } else {
                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): direct given locator '%(ctn)' denotes invalid target '%(target)'", "oid", this, "ctn", ctn, "target", o); }
            }
            
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): direct given locator: '%(ctn)', %({'does NOT exist','exists'}[found])", "oid", this, "ctn", ctn, "found", t.o(found ? 1 : 0)); }

            if ( ! found) { return; }
        } else if (my_aggregate_class.matches("^(?:(?:file)|(?:java)):.*")) {
            fullpath = my_aggregate_class;
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): direct given file: '%(fullpath)'", "oid", this, "fullpath", fullpath); }
            
            if (found = ((result_node = Factory_TreeNode.tryConstruct(context, fullpath)) != null)) {
                ocp_tnodes_to_check = new ReadOnlyVectorImplSingle_TreeNode_long_(context, result_node);
            }
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): direct given file: '%(fullpath)', %({'does NOT exist','exists'}[found])", "oid", this, "fullpath", fullpath, "found", t.o(found ? 1 : 0)); }

            if ( ! found) { return; }
        } else {

            got_real_aggregate_class = true;

        }

        if ((matches = tree_re.tryGetMatches(context, aggregate_root_class)) != null) {
            aggregate_root_class = matches[0];
            factory_tree = false;
            if ((matches = fac_re.tryGetMatches(context, aggregate_root_class)) != null) {
                aggregate_root_class = matches[0];
                factory_tree = true;
            }
            is_tree = true;

            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): aggregate root class is tree, tree component class '%(treecomponentclass)'", "oid", this, "treecomponentclass", aggregate_root_class); }

            folderaggregateclass = Factory_Aggregate.getProperty(context, (factory_tree ? "Factory" : "") + "FolderAggregateClassForClass." + aggregate_root_class, "");
            if (folderaggregateclass == null || folderaggregateclass.length() == 0) {
                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): no folder aggregate class configured", "oid", this); }
                no_config = true;
                return;
            }
            
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): folder aggregate class (complete) is '%(folderaggregateclass)'", "oid", this, "folderaggregateclass", folderaggregateclass); }
            tree_folder_aggregate_part = my_aggregate_class;
            my_cache_key_2 = my_cache_key_3;
            {
                int pos = -1;
                if ((pos = folderaggregateclass.lastIndexOf(';')) != -1 && folderaggregateclass.length() > pos+6 && folderaggregateclass.substring(pos+1,pos+7).equals("class=")) {
                    tree_aggregate_root_class = folderaggregateclass.substring(pos+7);
                    my_aggregate_class = folderaggregateclass.substring(0,pos);
                    my_cache_key_3 = folderaggregateclass;
                } else {
                    my_aggregate_class = folderaggregateclass;
                    my_cache_key_3 = folderaggregateclass;
                }
            }
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): folder aggregate class '%(aggregateclass)', tree aggregate root class '%(treeaggregaterootclass)'", "oid", this, "aggregateclass", my_aggregate_class, "treeaggregaterootclass", tree_aggregate_root_class); }
            
            if (main_extension.isEmpty()) {
                main_extension = Factory_Aggregate.getProperty(context, "ExtensionForClass." + tree_aggregate_root_class, "ocp");
            }
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): tree aggregate extension '%(extension)'", "oid", this, "extension", main_extension); }
            
            if (got_real_aggregate_class) {
                tree_folder_found = false;
                got_real_aggregate_class = false;

                Vector_String_long_ modifyable_tree_folders = Factory_Vector_String_long_.construct(context);
                tree_folders = modifyable_tree_folders;

                {
                    String sp = getSearchPath(context, aggregate_root_class);
                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): search path for root class '%(rootclass)': '%(path)'", "oid", this, "rootclass", aggregate_root_class, "path", sp); }
                
                    modifyable_ocp_tnodes_to_check = Factory_Vector_TreeNode_long_.construct(context);
                    ocp_tnodes_to_check = modifyable_ocp_tnodes_to_check;
                    TreeNode tn = null;
                
                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): searching for '%(treefolderaggregatepart)' in '%(path)'", "oid", this, "treefolderaggregatepart", tree_folder_aggregate_part, "path", sp); }
                
                    String[] cache_entry = (cache_2 == null ? null : cache_2.get(my_cache_key_2));
                    if (cache_entry != null) {
                        for (String ce : cache_entry) {
                            if ((tn = Factory_TreeNode.tryConstruct(context, ce)) != null) {
                                if (ocp_finder_cache_debug) { System.err.println("OCP2:OK: " + ce); }
                                tree_folder_found = true;
                                modifyable_tree_folders.append(context, ce);
                                modifyable_ocp_tnodes_to_check.append(context, tn);
                            } else {
                                CustomaryContext.create((Context)context).throwConfigurationError(context, "OCPFinder cache entry '%(key)', '%(value)' is invalid", "key", tree_folder_aggregate_part, "value", ce);
                                throw (ExceptionConfigurationError) null; // compiler insists
                            }
                        }
                    } else {
                        for (String spe : sp.split(path_delimiter)) {
                            String tree_folder = concatPathes(context, Encoding.recode(context, spe, Encoding.URI, Encoding.UTF8), tree_folder_aggregate_part);
                            if ((tn = Factory_TreeNode.tryConstruct(context, tree_folder)) != null) {
                                if (ocp_finder_cache_debug) { System.err.println("OCP2:OK: " + tree_folder); }
                        
                                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): '%(treefolder)' exists", "oid", this, "treefolder", tree_folder); }
                                tree_folder_found = true;
                                modifyable_tree_folders.append(context, tree_folder);
                                modifyable_ocp_tnodes_to_check.append(context, tn);
                            } else {
                                if (ocp_finder_cache_debug) { System.err.println("OCP2:--: " + tree_folder); }
                                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): '%(treefolder)' does not exist", "oid", this, "treefolder", tree_folder); }
                            }
                        }

                        if (cache_2 == null) {
                            cache_2 = new HashMap<String,String[]>();
                        }
                        cache_entry = new String[(int) modifyable_tree_folders.getSize(context)];
                        int cei = 0;
                        for (String ce : modifyable_tree_folders.getIterable_String_(context)) {
                            cache_entry[cei++] = ce;
                        }
                        cache_2.put(my_cache_key_2, cache_entry);
                    }
                }
            } else {
                TreeNode tree_folder_node = result_node;

                result_node = null;
                found = false;

                modifyable_ocp_tnodes_to_check = Factory_Vector_TreeNode_long_.construct(context);
                ocp_tnodes_to_check = modifyable_ocp_tnodes_to_check;
                modifyable_ocp_tnodes_to_check.append(context, tree_folder_node);

                // ok, besser wärs ja vermutlich den ocp-tree-mechanismus auch
                // auf "Locators" aufzusetzen und nicht auf TextLocators --
                // weil das hier geht zwar aber so richtig sauber isses ja
                // nich... (siehe auch doku zur tryGetTextLocator method an
                // der Klasse Locator)
                String tree_folder = tree_folder_node.getLocation(context).tryGetTextLocator(context, null, null);

                Vector_String_long_ modifyable_tree_folders = Factory_Vector_String_long_.construct(context);
                tree_folders = modifyable_tree_folders;
                modifyable_tree_folders.append(context, tree_folder);

                Vector_TreeNode_long_ modifyable_tree_folder_nodes = Factory_Vector_TreeNode_long_.construct(context);
                tree_folder_nodes = modifyable_tree_folder_nodes;
                modifyable_tree_folder_nodes.append(context, tree_folder_node);
            }

            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) {
                if (tree_folder_found) {
                    for (long i=0; i<tree_folders.getSize(context); i++) {
                        cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): tree folder '%(treefolder)', %({'does NOT exist','exists'}[found])", "oid", this, "treefolder", tree_folders.tryGet(context, i), "found", t.o(1));
                    }
                } else {
                    cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): tree folder '%(treefolder)', %({'does NOT exist','exists'}[found])", "oid", this, "treefolder", tree_folder_aggregate_part, "found", t.o(0));
                }
            }
        }

        if (got_real_aggregate_class || (is_tree && tree_folder_found)) {

            String sp = null;
            if (! is_tree) {
                if (main_extension.isEmpty()) {
                    main_extension = Factory_Aggregate.getProperty(context, "ExtensionForClass." + aggregate_root_class, "ocp");
                }
                sp = getSearchPath(context, aggregate_root_class);
                wrapperaggregateclass = Factory_Aggregate.getProperty(context, "WrapperAggregateClassForClass." + aggregate_root_class, (String) null);
                wrapperaggregateclasses = Factory_Aggregate.getProperty(context, "WrapperAggregateClassesForClass." + aggregate_root_class, (String) null);
            } else {
                tree_extension = Factory_Aggregate.getProperty(context, "ExtensionForClass." + aggregate_root_class, "ocp");
                sp = getSearchPath(context, tree_aggregate_root_class);
                wrapperaggregateclass = Factory_Aggregate.getProperty(context, "WrapperAggregateClassForClass." + tree_aggregate_root_class, (String) null);
                wrapperaggregateclasses = Factory_Aggregate.getProperty(context, "WrapperAggregateClassesForClass." + tree_aggregate_root_class, (String) null);
            }
            
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): root aggregate class: '%(aggregaterootclass)', extension '%(mainextension)', %({\"tree of class occurences, tree extension '\",\"plain class\"}[tree])%(treeextension)%({\"'\",\"\"}[tree]), wrapper '%(wrapperaggregateclass)'", "oid", this, "aggregaterootclass", aggregate_root_class, "mainextension", main_extension, "tree", t.o(is_tree ? 0 : 1), "treeextension", tree_extension, "wrapperaggregateclass", wrapperaggregateclass); }
            
            {
                String cache_entry = (cache_3 == null ? null : cache_3.get(my_cache_key_3));
                if (cache_entry != null) {
                    found = ((result_node = Factory_TreeNode.tryConstruct(context, cache_entry)) != null);
                    if (found) {
                        fullpath = cache_entry;
                        if (ocp_finder_cache_debug) { System.err.println("OCP3:OK: " + cache_entry); }
                    } else {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, "OCPFinder cache entry '%(key)', '%(value)' is invalid", "key", my_aggregate_class, "value", cache_entry);
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                } else {
                    String[] spa = sp == null ? null : sp.split(path_delimiter);
                    String[] mea = main_extension == null ? null : main_extension.split(",");

                    LOOP: for (String[] ocp_variant : ocp_variants) {
                        if (    ocp_variant != null
                             && (variant_include == null || ocp_variant[1].matches(variant_include) == true)
                             && (variant_exclude == null || ocp_variant[1].matches(variant_exclude) == false)
                           ) {
                            for (String spe : spa) {
                                for (String ext : mea) {
                                    fullpath = concatPathes(context, Encoding.recode(context, spe, Encoding.URI, Encoding.UTF8), my_aggregate_class + ocp_variant[0] + "." + ext);
                                    found = ((result_node = Factory_TreeNode.tryConstruct(context, fullpath)) != null);
                                    if (found) {
                                        if (ocp_finder_cache_debug) { System.err.println("OCP3:OK: " + spe + " - " + my_aggregate_class + " - " + ocp_variant[0] + " - . - " + ext); }
                                        if (cache_3 == null) {
                                            cache_3 = new HashMap<String,String>();
                                        }
                                        cache_3.put(my_cache_key_3, fullpath);
                                        break LOOP;
                                    }
                                    if (ocp_finder_cache_debug) { System.err.println("OCP3:--: " + spe + " - " + my_aggregate_class + " - " + ocp_variant[0] + " - . - " + ext + " -- " + fullpath); }
                                }
                            }
                        }
                    }
                }
                if (modifyable_ocp_tnodes_to_check != null) {
                    modifyable_ocp_tnodes_to_check.append(context, result_node);
                } else {
                    ocp_tnodes_to_check = new ReadOnlyVectorImplSingle_TreeNode_long_(context, result_node);
                }
                
                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Factory_Aggregate[%(oid)].create(): full path '%(fullpath)', %({'does NOT exist','exists'}[found])", "oid", this, "fullpath", fullpath, "found", t.o(found ? 1 : 0)); }
            }
        }
    }

    static protected String ocp_cache_file;
    static protected String ocp_cache_exclude;
    static protected String ocp_cache_include;

    static public void saveCacheOnExit(CallContext context) {
        ocp_cache_file = config.get(context, "Cache.File", (String) null);
        ocp_cache_exclude = config.get(context, "Cache.Exclude", (String) null);
        ocp_cache_include = config.get(context, "Cache.Include", (String) null);
        java.lang.Runtime.getRuntime().addShutdownHook(new Thread() { public void run() { saveCache(RootContext.getDestructionContext()); } });
    }

    static public void saveCache(CallContext context) {
        File f = new File(ocp_cache_file);
        try {
            f.setWritable(true);
            FileOutputStream fos = new FileOutputStream(f);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(bw);

            printCache(context, pw, "Cache1", cache_1, false, ocp_cache_exclude, ocp_cache_include);
            printCache(context, pw, "Cache2", cache_2, true, ocp_cache_exclude, ocp_cache_include);
            printCache(context, pw, "Cache3", cache_3, false, ocp_cache_exclude, ocp_cache_include);

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
    }

    static protected void printCache(CallContext context, PrintWriter pw, String cache_name, Map cache, boolean is_array, String exclude, String include) {
        if (cache == null) { return; }

        pw.print("com.sphenon.engines.factorysite.factories.Factory_Aggregate." + cache_name + "=");

        boolean firstentry = true;
        for (Object ome : cache.entrySet()) {
            Map.Entry me = (Map.Entry) ome;
            String key = (String) me.getKey();

            if (    (    include == null
                      || key.matches(include) == true
                    )
                 && (    exclude == null
                      || key.matches(exclude) == false
                    )
               ) {
                pw.print((firstentry ? "" : ";") + Encoding.recode(context, key, Encoding.UTF8, Encoding.URI) + "=");
                firstentry = false;
                if (is_array) {
                    String[] values = (String[]) me.getValue();
                    boolean firstvalue = true;
                    for (String value : values) {
                        if (firstvalue) { firstvalue = false; } else { pw.print(","); }
                        pw.print(Encoding.recode(context, value, Encoding.UTF8, Encoding.URI));
                    }
                } else {
                    String value = (String) me.getValue();
                    pw.print(Encoding.recode(context, value, Encoding.UTF8, Encoding.URI));
                }
            }
        }
        pw.println("");
    }

    static protected RegularExpression cache_entry_re = new RegularExpression("([^;=]+)=([^;]+)(?:;?)");

    static public void loadCaches(CallContext context) {
        cache_1 = new HashMap<String,String>();
        cache_2 = new HashMap<String,String[]>();
        cache_3 = new HashMap<String,String>();
        loadCache(context, "Cache1", cache_1, false);
        loadCache(context, "Cache2", cache_2, true);
        loadCache(context, "Cache3", cache_3, false);
    }

    static protected void loadCache(CallContext context, String cache_name, Map cache, boolean is_array) {
        String cache_property = config.get(context, cache_name, (String) null);
        if (cache_property == null) { return; }

        Matcher m = cache_entry_re.getMatcher(context, cache_property);
        while (m.find()) {
            String key = Encoding.recode(context, m.group(1), Encoding.URI, Encoding.UTF8);

            if (is_array) {
                String[] values = m.group(2).split(",");
                for (int i=0; i<values.length; i++) {
                    values[i] = Encoding.recode(context, values[i], Encoding.URI, Encoding.UTF8);
                }
                cache.put(key, values);
            } else {
                String value = m.group(2);
                value = Encoding.recode(context, value, Encoding.URI, Encoding.UTF8);
                cache.put(key, value);
            }
        }
    }
}

