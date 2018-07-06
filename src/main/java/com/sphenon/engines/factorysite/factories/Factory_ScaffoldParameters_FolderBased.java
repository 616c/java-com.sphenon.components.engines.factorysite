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
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.factory.returncodes.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.tplinst.*;
import com.sphenon.basics.graph.factories.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.io.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/*
   How it works:

   Factory_Aggregate ( "....;class=Tree<...>" )
     aggregate_class: ...FolderAggregateClassForClass
     passes: FOLDERS, EXTENSION, AGGREGATEPREFIX, COMPONENTCLASS
     e.g. .factorysite/umlpackage.model oder /package.ooem
     creates: Factory_UMLPackage_ByUMLElementList oder Factory_OOEMPackage_ByOOEMElementList
       passes: u.a. ElementFactories, assigned by FactorySite internal dynamic parameter mechanism,
       i.e. a list of Vector_ScaffoldParameter_long_, created by Factory_ScaffoldParameters_FolderBased 
         uses: FOLDERS, EXTENSION, AGGREGATEPREFIX, COMPONENTCLASS
         these factories are again:
           Factory_Aggregate ( "....;class=Tree<...>" )
         es well as
           Factory_Aggregate ( "....;class=..." )
             passes automatically __OCP_ID__, __OCP_LOCATION__
 */

public class Factory_ScaffoldParameters_FolderBased
{
    static protected Configuration config;
    static { config = Configuration.create(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.factories.Factory_ScaffoldParameters_FolderBased"); };

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.factories.Factory_ScaffoldParameters_FolderBased"); };

    protected ReadOnlyVector_String_long_ folders;
    private int folder_validation_state = -1;
    protected ValidationFailure folder_validation_failure = null;

    protected Vector_TreeNode_long_ tree_nodes;
    private int tree_nodes_validation_state = -1;
    protected ValidationFailure tree_nodes_validation_failure = null;

    protected String extension;
    protected String extension_plural;
    private int extension_validation_state = -1;

    protected String aggregate_prefix;
    private int aggregate_prefix_validation_state = -1;

    protected String component_class;
    protected String component_class_plural;
    private int component_class_validation_state = -1;

    protected String wrapper_class;
    private int wrapper_class_validation_state = -1;

    protected boolean pass_file_factories;
    protected boolean pass_folder_factories;
    protected boolean is_factory_tree;

    public ReadOnlyVector_String_long_ getFolders (CallContext context) {
        return this.folders;
    }

    public void setFolders (CallContext context, ReadOnlyVector_String_long_ folders) {
        this.folders = folders;
        this.folder_validation_state = -1;
    }

    public void validateFolders(CallContext context) throws ValidationFailure {
        if (this.folder_validation_state == -1) {
            this.folder_validation_state = 0;
            this.folder_validation_failure = null;
            if (this.folders == null && this.tree_nodes == null) {
                this.folder_validation_state = 1;
                this.folder_validation_failure = ValidationFailure.createValidationFailure(context, "Parameter not set: 'folders' (or, alternatively: 'tree_nodes')");
            } else {
                for (long i=0; i<folders.getSize(context); i++) {
                    String folder = folders.tryGet(context, i);
                    TreeNode tn = Factory_TreeNode.tryConstruct(context, folder);
                    if (tn == null) {
                        this.folder_validation_state = 2;
                        this.folder_validation_failure = ValidationFailure.createValidationFailure(context, "Path does not exist: '%(folder)'", "folder", folder);
                    } else if (tn instanceof TreeLeaf) {
                        this.folder_validation_state = 3;
                        this.folder_validation_failure = ValidationFailure.createValidationFailure(context, "Path is not a folder: '%(folder)'", "folder", folder);
                    }
                }
            }
            if (this.folder_validation_state == -1) {
                this.folder_validation_state = 0;
            }
        }
        if (this.folder_validation_state != 0) {
            throw folder_validation_failure;
        }
    }

    public Vector_TreeNode_long_ getTreeNodes (CallContext context) {
        return this.tree_nodes;
    }

    public Vector_TreeNode_long_ defaultTreeNodes (CallContext context) {
        return null;
    }

    public void validateTreeNodes(CallContext context) throws ValidationFailure {
        if (this.tree_nodes_validation_state == -1) {
            this.tree_nodes_validation_state = 0;
            if (this.tree_nodes != null) {
                for (long i=0; i<tree_nodes.getSize(context); i++) {
                    TreeNode tn = tree_nodes.tryGet(context, i);
                    if (tn instanceof TreeLeaf) {
                        this.tree_nodes_validation_state = 2;
                        this.tree_nodes_validation_failure = ValidationFailure.createValidationFailure(context, "TreeNode is not a node, but a leaf: '%(tree_node)'", "tree_node", tn);
                    }
                }
            }
        }
        if (this.tree_nodes_validation_state != 0) {
            throw tree_nodes_validation_failure;
        }
    }

    public void setTreeNodes (CallContext context, Vector_TreeNode_long_ tree_nodes) {
        this.tree_nodes = tree_nodes;
        this.tree_nodes_validation_state = -1;
    }

    public String getExtension (CallContext context) {
        return this.extension;
    }

    public String defaultExtension (CallContext context) {
        return "ocp";
    }

    public void setExtension (CallContext context, String extension) {
        this.extension = extension;
        this.extension_validation_state = -1;
    }

    public void validateExtension(CallContext context) throws ValidationFailure {
        if (this.extension_validation_state == -1) {
            if (this.extension == null) {
                this.extension_validation_state = 1;
            } else {
                this.extension_validation_state = 0;
                this.extension_plural = Factory_Aggregate.getProperty(context, "PluralOfExtension." + this.extension, (String) null);
            }
        }
        switch (this.extension_validation_state) {
            case 0:
                return;
            case 1:
                ValidationFailure.createAndThrow(context, "Parameter not set: 'extension'");
                throw (ValidationFailure) null; // compiler insists
        }
    }

    public String getAggregatePrefix (CallContext context) {
        return this.aggregate_prefix;
    }

    public void setAggregatePrefix (CallContext context, String aggregate_prefix) {
        this.aggregate_prefix = aggregate_prefix;
        this.aggregate_prefix_validation_state = -1;
    }

    public void validateAggregatePrefix(CallContext context) throws ValidationFailure {
        if (this.aggregate_prefix_validation_state == -1) {
            if (this.aggregate_prefix == null && this.tree_nodes == null) {
                this.aggregate_prefix_validation_state = 1;
            } else {
                this.aggregate_prefix_validation_state = 0;
            }
        }
        switch (this.aggregate_prefix_validation_state) {
            case 0:
                return;
            case 1:
                ValidationFailure.createAndThrow(context, "Parameter not set: 'aggregate_prefix'");
                throw (ValidationFailure) null; // compiler insists
        }
    }

    public String getComponentClass (CallContext context) {
        return this.component_class;
    }

    public void setComponentClass (CallContext context, String component_class) {
        this.component_class = component_class;
        this.component_class_validation_state = -1;
    }

    public void validateComponentClass(CallContext context) throws ValidationFailure {
        if (this.component_class_validation_state == -1) {
            if (this.component_class == null) {
                this.component_class_validation_state = 1;
            } else {
                this.component_class_validation_state = 0;
                this.component_class_plural = Factory_Aggregate.getProperty(context, "PluralOfComponentClass." + this.component_class, (String) null);
            }
        }
        switch (this.component_class_validation_state) {
            case 0:
                return;
            case 1:
                ValidationFailure.createAndThrow(context, "Parameter not set: 'component_class'");
                throw (ValidationFailure) null; // compiler insists
        }
    }

    public String getWrapperClass (CallContext context) {
        return this.wrapper_class;
    }

    public String defaultWrapperClass (CallContext context) {
        return null;
    }

    public void setWrapperClass (CallContext context, String wrapper_class) {
        this.wrapper_class = wrapper_class;
        this.wrapper_class_validation_state = -1;
    }

    public void validateWrapperClass(CallContext context) throws ValidationFailure {
        if (this.wrapper_class_validation_state == -1) {
            this.wrapper_class_validation_state = 0;
        }
        switch (this.wrapper_class_validation_state) {
            case 0:
                return;
        }
    }

    public boolean getPassFileFactories (CallContext context) {
        return this.pass_file_factories;
    }

    public boolean defaultPassFileFactories (CallContext context) {
        return false;
    }

    public void setPassFileFactories (CallContext context, boolean pass_file_factories) {
        this.pass_file_factories = pass_file_factories;
    }

    public void validatePassFileFactories(CallContext call_context) throws ValidationFailure {
    }

    public boolean getPassFolderFactories (CallContext context) {
        return this.pass_folder_factories;
    }

    public boolean defaultPassFolderFactories (CallContext context) {
        return false;
    }

    public void setPassFolderFactories (CallContext context, boolean pass_folder_factories) {
        this.pass_folder_factories = pass_folder_factories;
    }

    public void validatePassFolderFactories(CallContext call_context) throws ValidationFailure {
    }

    public boolean getIsFactoryTree (CallContext context) {
        return this.is_factory_tree;
    }

    public boolean defaultIsFactoryTree (CallContext context) {
        return false;
    }

    public void setIsFactoryTree (CallContext context, boolean is_factory_tree) {
        this.is_factory_tree = is_factory_tree;
    }

    public void validateIsFactoryTree(CallContext call_context) throws ValidationFailure {
    }

    protected Map<String,com.sphenon.basics.data.TypedValue> parameters;

    public Map<String,com.sphenon.basics.data.TypedValue> getParameters (CallContext context) {
        return this.parameters;
    }

    public Map<String,com.sphenon.basics.data.TypedValue> defaultParameters (CallContext context) {
        return null;
    }

    public void setParameters (CallContext context, Map<String,com.sphenon.basics.data.TypedValue> parameters) {
        this.parameters = parameters;
    }

    public Factory_ScaffoldParameters_FolderBased(CallContext call_context) {
    }

    protected class NodeComparator implements java.util.Comparator {
        protected CallContext context;
        public NodeComparator(CallContext context) { this.context = context; }
        public int compare(Object o1, Object o2) { return ((TreeNode) o1).getId(context).compareTo(((TreeNode) o2).getId(context)); }
    }

    public Vector_ScaffoldParameter_long_ createVector_ScaffoldParameter_long_ (CallContext context) {

        try {
            validateFolders(context);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, "Parameter 'Folders' is invalid");
        }

        try {
            validateTreeNodes(context);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, "Parameter 'TreeNodes' is invalid");
        }

        try {
            validateExtension(context);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, "Parameter 'Extension' ('%(extension)') is invalid", "extension", this.extension);
        }

        try {
            validateAggregatePrefix(context);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, "Parameter 'AggregatePrefix' ('%(aggregate_prefix)') is invalid", "aggregate_prefix", this.aggregate_prefix);
        }

        try {
            validateComponentClass(context);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, "Parameter 'ComponentClass' ('%(component_class)') is invalid", "component_class", this.component_class);
        }

        try {
            validateWrapperClass(context);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, "Parameter 'WrapperClass' ('%(wrapper_class)') is invalid", "wrapper_class", this.wrapper_class);
        }

        BuildTextComplex_String btc = new BuildTextComplex_String (context, "", "", "java.util.Hashtable", "", "", "<dynamic>");
        Vector_Pair_BuildText_String__long_ items = btc.getItems (context);

        String file_include_regexp   = Factory_Aggregate.getProperty(context, "FileIncludeRegexp4." + this.component_class, (String) null);
        String file_exclude_regexp   = Factory_Aggregate.getProperty(context, "FileExcludeRegexp4." + this.component_class, (String) null);
        String folder_include_regexp = Factory_Aggregate.getProperty(context, "FolderIncludeRegexp4." + this.component_class, (String) null);
        String folder_exclude_regexp = Factory_Aggregate.getProperty(context, "FolderExcludeRegexp4." + this.component_class, (String) null);

        String fs_id = "";
        java.util.Hashtable unique = new java.util.Hashtable();

        boolean using_tree_nodes = true;

        if (this.tree_nodes == null) {
            using_tree_nodes = false;
            Vector_TreeNode_long_ tns = Factory_Vector_TreeNode_long_.construct(context);          
            for (long f=0; f<folders.getSize(context); f++) {
                String folder = folders.tryGet(context, f);
                if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Dynamic scaffold builder: loading '%(folder)'...", "folder", folder); }
                fs_id += (fs_id.length() == 0 ? "" : ":") + folder;

                TreeNode tn = Factory_TreeNode.tryConstruct(context, folder);
                if (tn == null) {
                    CustomaryContext.create((Context)context).throwInvalidState(context, "Invalid state, folder vanished or is not folder anymore: '%(folder)'", "folder", folder);
                    throw (ExceptionInvalidState) null; // compiler insists
                }
                tns.append(context, tn);
            }
            this.tree_nodes = tns;
        }

        List<TreeNode> tree_node_parameters = (using_tree_nodes ? new ArrayList<TreeNode>() : null);

        for (long tni=0; tni<this.tree_nodes.getSize(context); tni++) {
            TreeNode tn = this.tree_nodes.tryGet(context, tni);
            String tnid = tn.getLocation(context).getUniqueIdentifier(context);

            if (using_tree_nodes) {
                fs_id += (fs_id.length() == 0 ? "" : ":") + tnid;
            }

            if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Dynamic scaffold builder: loading '%(treenode)'...", "treenode", tn); }

            Vector_TreeNode_long_ childs = tn.getChilds(context);
            TreeNode[] entries = new TreeNode[childs == null ? 0 : ((int) childs.getSize(context))];
            int i=0;
            if (childs != null) {
                for (TreeNode child : new VectorIterable_TreeNode_long_(context, childs)) {
                    entries[i++] = child;
                }
            }
            java.util.Arrays.sort(entries, new NodeComparator(context));
            for (i=0; i<entries.length; i++) {
                TreeNode entry = entries[i];
                String name = entry.getId(context);
                if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Dynamic scaffold builder: checking entry '%(entry)'...", "entry", name); }

                if (unique.get(name) != null) {
                    if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "...already included"); }
                    continue;
                }

                BuildTextComplex_String btcp = new BuildTextComplex_String (context, "", "", "", "", "", "<dynamic>");
                Vector_Pair_BuildText_String__long_ itemsp = btcp.getItems (context);

                if (this.getParameters(context) != null) {
                    for (String namep : this.getParameters(context).keySet()) {

                        BuildTextParameter_String btp = new BuildTextParameter_String(context, "", "", this.getParameters(context).get(namep).getTypeName(context), namep, "<dynamic>");

                        BuildTextComplex_String btc_fsop = new BuildTextComplex_String (context, "", "", "com.sphenon.engines.factorysite.FactorySiteOptionalParameter", "", "", "<dynamic>");
                        Vector_Pair_BuildText_String__long_ items_fsop = btc_fsop.getItems (context);
                        items_fsop.append(context, new Pair_BuildText_String_(context, btp, "Parameter"));

                        itemsp.append(context, new Pair_BuildText_String_(context, btc_fsop, namep));
                    }
                }

                String tnpn = null;
                if (using_tree_nodes) {
                    tnpn = "TREENODE" + tree_node_parameters.size();
                    tree_node_parameters.add(entry);
                }

                if (entry instanceof TreeLeaf) {
                    if (file_include_regexp != null && ! name.matches(file_include_regexp)) {
                        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "...not included by regexp filter '%(includere)'", "includere", file_include_regexp); }
                        continue;
                    }
                    if (file_exclude_regexp != null &&   name.matches(file_exclude_regexp)) {
                        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "...excluded by regexp filter '%(excludere)'", "excludere", file_exclude_regexp); }
                        continue;
                    }
                
                    boolean does_match = false;
                    boolean is_plural = false;
                    String aggregate_id = null;

                    boolean is_locator = (this.aggregate_prefix != null && this.aggregate_prefix.matches("^(?:(?:file:)|(?:java:)|(?:(?:(?:ctn|oorl):)?//)).*"));

                    for (String ext : this.extension.split(",")) {
                        int extlen = ext.length() + 1;
                        if (name.length() >= extlen && name.substring(name.length()-extlen,name.length()).equals("." + ext)) {
                            does_match = true;
                            aggregate_id = name.substring(0,name.length()-extlen);
                            break;
                        }
                    }
                    if (    does_match == false
                         && this.extension_plural != null
                         && this.extension_plural.isEmpty() == false
                       ) {
                        for (String ext : this.extension_plural.split(",")) {
                            int extlen = ext.length() + 1;
                            if (name.length() >= extlen && name.substring(name.length()-extlen,name.length()).equals("." + ext)) {
                                does_match = true;
                                is_plural = true;
                                aggregate_id = name.substring(0,name.length()-extlen);
                                break;
                            }
                        }
                    }
                    if (does_match == false) {
                        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "...does not have extension '%(extension)'", "extension", this.extension); }
                        continue;
                    }
                    String component_class = is_plural ? this.component_class_plural : this.component_class;
                    {
                        String aggregate_class = (is_locator ? name : aggregate_id) + ";class=" + component_class;
                        BuildTextComplex_String btc2 = new BuildTextComplex_String (context, "", "", this.pass_file_factories ? "Factory_Aggregate" : "Object", this.pass_file_factories ? "" : "Factory_Aggregate", "", "<dynamic>");
                        btc2.setAllowDynamicTypeCheck(context, true);
                        Vector_Pair_BuildText_String__long_ items2 = btc2.getItems (context);
                        if (this.wrapper_class == null) {
                            if (using_tree_nodes) {
                                items2.append(context, new Pair_BuildText_String_(context, new BuildTextParameter_String(context, "", "", "com.sphenon.basics.graph.TreeNode", tnpn, "<dynamic>"), "AggregateTreeNode"));
                            }
                            items2.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", "<dynamic>", new Pair_BuildText_String_(context, new BuildTextSimple_String(context, "", "", "String", "", "", (this.aggregate_prefix == null ? "" : (this.aggregate_prefix + "/" + aggregate_class)), "<dynamic>"), "")), "AggregateClass"));
                            items2.append(context, new Pair_BuildText_String_(context, btcp, "Parameters"));
                        } else {
                            items2.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", "<dynamic>", new Pair_BuildText_String_(context, new BuildTextSimple_String(context, "", "", "String", "", "", this.wrapper_class, "<dynamic>"), "")), "AggregateClass"));
                            itemsp.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "String", "", "", "<dynamic>", new Pair_BuildText_String_(context, new BuildTextSimple_String(context, "", "", "String", "", "", (this.aggregate_prefix == null ? "" : (this.aggregate_prefix + "/" + aggregate_class)) + aggregate_class, "<dynamic>"), "")), "AGGREGATECLASS"));
                            items2.append(context, new Pair_BuildText_String_(context, btcp, "Parameters"));
                        }
                        items.append(context, new Pair_BuildText_String_(context, btc2, aggregate_id));
                        unique.put(name, name);
                    }
                } else {
                    if (folder_include_regexp != null && ! name.matches(folder_include_regexp)) {
                        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "...not included by regexp filter '%(includere)'", "includere", folder_include_regexp); }
                        continue;
                    }
                    if (folder_exclude_regexp != null &&   name.matches(folder_exclude_regexp)) {
                        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "...excluded by regexp filter '%(excludere)'", "excludere", folder_exclude_regexp); }
                        continue;
                    }

                    {
                        String aggregate_id = name;
                        String aggregate_class = aggregate_id;
                        if (aggregate_class.matches(".*;class=") == false) {
                            aggregate_class += ";class=Tree<" + (this.is_factory_tree ? "Factory<" : "") + this.component_class + (this.is_factory_tree ? ">" : "") + ">";
                        }
                        BuildTextComplex_String btc2 = new BuildTextComplex_String (context, "", "", this.pass_folder_factories ? "Factory_Aggregate" : "Object", this.pass_folder_factories ? "" : "Factory_Aggregate", "", "<dynamic>");
                        btc2.setAllowDynamicTypeCheck(context, true);
                        Vector_Pair_BuildText_String__long_ items2 = btc2.getItems (context);
                        if (using_tree_nodes) {
                            items2.append(context, new Pair_BuildText_String_(context, new BuildTextParameter_String(context, "", "", "com.sphenon.basics.graph.TreeNode", tnpn, "<dynamic>"), "AggregateTreeNode"));
                        }
                        items2.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", "<dynamic>", new Pair_BuildText_String_(context, new BuildTextSimple_String(context, "", "", "String", "", "", (this.aggregate_prefix == null ? "" : (this.aggregate_prefix + "/" + aggregate_class)) + aggregate_class, "<dynamic>"), "")), "AggregateClass"));
                        items2.append(context, new Pair_BuildText_String_(context, btcp, "Parameters"));
                        items.append(context, new Pair_BuildText_String_(context, btc2, aggregate_id));
                        unique.put(name, name);
                    }
                }
            }
        }

        String xmlns = "http://xmlns.sphenon.com/engines/factorysite";
        String search_path_context = Factory_Aggregate.getProperty(context, "TypeContext4XMLNS." + xmlns, (String) null);
        if (search_path_context != null) {
            context = Context.create(context);
            TypeContext tc = TypeContext.create((Context) context);
            tc.setSearchPathContext(context, search_path_context);
        } else {
            if ((this.notification_level & Notifier.OBSERVATION) != 0) { NotificationContext.sendInfo(context, FactorySiteStringPool.get(context, "1.2.23" /* No type context configured for xml namespace '%(xmlns)' */), "xmlns", xmlns); }
        }

        FactorySiteTextBased fs = null;
        try {
            fs = new FactorySiteTextBased (context, btc, fs_id);
        } catch (PutUpFailure puf) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, puf, "Dynamic creation of folder based scaffolds failed");
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        if (this.getParameters(context) != null) {
            for (String namep : this.getParameters(context).keySet()) {
                try {
                    fs.setOptionalStaticParameter(context, namep, this.getParameters(context).get(namep).getValue(context));
                } catch (TypeMismatch tm) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "While building a file based OCP tree, the internal OCP defined for folders declared a parameter of type '%(declared)', but the passed value is of type '%(given)'", "declared", this.getParameters(context).get(namep).getTypeName(context), "given", this.getParameters(context).get(namep).getValue(context).getClass().getName());
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
            }
        }

        if (using_tree_nodes) {
            int tnpi = 0;
            for (TreeNode tn : tree_node_parameters) {
                String tnpn = "TREENODE" + tnpi++;
                try {
                    fs.setOptionalStaticParameter(context, tnpn, tn);
                } catch (TypeMismatch tm) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "While building a file based OCP tree, the internal OCP defined for folders declared a parameter of type 'TreeNode', but the passed value is of type '%(given)'", "given", tn.getClass().getName());
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
            }
        }

        DataSource ds = fs.getInternalMainDataSource (context);
        Scaffold s = null;
        try {
            s = (Scaffold) ds;
        } catch (ClassCastException cce) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, cce, "Dynamic creation of folder based scaffolds failed; main datasource is not a 'Scaffold', as expected, but a '%(got)'", "got", ds.getClass().getName());
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        Vector_ScaffoldParameter_long_ sps = s.getParameters(context);

        return sps;
    }

    public void compile (CallContext context) {
    }
}

