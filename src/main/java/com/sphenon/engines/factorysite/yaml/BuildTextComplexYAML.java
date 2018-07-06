package com.sphenon.engines.factorysite.yaml;

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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.expression.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.io.StringWriter;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.text.*;

public class BuildTextComplexYAML extends BuildTextYAMLBaseImpl implements BuildTextComplex, Dumpable {
    private String text_locator;
    private String text_locator_base;
    private String component_type;
    private Vector_Pair_BuildText_String__long_ named_items;
    private String out;

    protected Object node;

    static protected RegularExpression number_remove_re = new RegularExpression("-[0-9]$","");

    public BuildTextComplexYAML (CallContext context, Object node, BuildTextYAMLMetainfo meta, String source_location_info, BuildTextYAMLFactory bt_factory) throws InvalidDocument {
        super(context, node, meta, source_location_info);

        this.node = node;
        BuildTextYAMLDatainfo data = (this.node instanceof BuildTextYAMLDatainfo ? ((BuildTextYAMLDatainfo) this.node) : null);

        this.out               = meta.getOut();
        this.component_type    = meta.getComponentType();

        this.text_locator      = (data == null ? null : data.getLocator());
        this.text_locator_base = (data == null ? null : data.getLocatorBase());

        named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);

        boolean got_text_locator = false;
        if (this.isNonEmpty(context, this.text_locator)) {
            this.retriever_name = "com.sphenon.basics.locating.retrievers.RetrieverByTextLocator";
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", source_location_info, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "String", "", "", this.text_locator, source_location_info), "")), "TextLocator"));
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", source_location_info, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "String", "", "", "Property", source_location_info), "")), "DefaultType"));
            this.allow_dynamic_type_check = true;
            got_text_locator = true;
        }

        if (this.isNonEmpty(context, this.text_locator_base = (data == null ? null : data.getLocatorBase()))) {
            if (this.text_locator_base.charAt(0) == '#') {
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRef_String (context, "", "", "", this.text_locator_base.substring(1), source_location_info), "Base"));
            } else if (this.text_locator_base.charAt(0) == '$') {
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextParameter_String (context, "", "", "", this.text_locator_base.substring(1), source_location_info), "Base"));
            } else {
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRefById_String (context, "", "", "", this.text_locator_base, source_location_info), "Base"));
            }
        } else if (this.isNonEmpty(context, this.text_locator_base = (data == null ? null : data.getLocatorBaseOIdRef()))) {
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRef_String (context, "", "", "", this.text_locator_base, source_location_info), "Base"));
        } else if (this.isNonEmpty(context, this.text_locator_base = (data == null ? null : data.getLocatorBaseParameter()))) {
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextParameter_String (context, "", "", "", this.text_locator_base, source_location_info), "Base"));
        } else if (got_text_locator) {
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRefById_String (context, "", "", "", "*", source_location_info), "Base"));
        }

        HashMap<String, BuildText> item_hash = new HashMap<String, BuildText>();

        String content = meta.getContent();
        if (content != null && content.equals("YAML/Node")) {
            // untested, maybe better something like "YAML/Map", "YAML/List", or testing
            // what node is and setting type ("...Object/Map/List") appropriately?
            // whatever...

            // named_items.append(context, new Pair_BuildText_String_(context, new BuildTextYAMLRawYAML(context, "", "", "java.lang.Object", "", "", node, "", source_location_info), ""));
        } else if (content != null && content.equals("YAML/Text")) {

            // serialise YAML? (which is named "dump" in snakeyaml, right?)

        } else if (data != null && data.getExpression() != null) {
            String text = data.getExpression();
            String btsstype = "java.lang.String";
            if (data.getExpressionType() != null && data.getExpressionType().equals("value")) {
                btsstype = this.type_name;
            }
            BuildTextSimple_String btss = new BuildTextSimple_String(context, "", "", btsstype, "", "", text, source_location_info);
            named_items.append(context, new Pair_BuildText_String_(context, btss, ""));
            btss.setIsExpression(context, true);
        } else if (node instanceof String) {
            String text = (String) node;
            BuildTextSimple_String btss = new BuildTextSimple_String(context, "", "", "java.lang.String", "", "", text, source_location_info);
            named_items.append(context, new Pair_BuildText_String_(context, btss, ""));
        } else {

            boolean is_out = this.isNonEmpty(context, out);
            if (is_out) {
                this.type_name = "FactorySiteOutParameter";
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, (String) null, (String) null, (String) null, (String) null, (String) null, meta.getJClass(), source_location_info), "Type"));
            }

            if (node instanceof Map) {
                Map map = (Map) node;
                java.util.Iterator iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    Object key = iterator.next();
                    Object child = map.get(key);

                    if (is_out == true) {
                        NotificationContext.sendWarning(context, FactorySiteStringPool.get(context, "0.0.4" /* Tag with OUT attribute must not have childs (they are ignored) */));
                        continue;
                    }

                    BuildTextYAMLMetainfo child_meta = null;
                    if (key instanceof BuildTextYAMLMetainfo) {
                        if (child instanceof BuildTextYAMLMetainfo) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, "Meta information in a map can only be provided either at key or at value, but not on both sides (OCP/YAML file '%(info)')", "info", source_location_info);
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        child_meta = (BuildTextYAMLMetainfo) key;
                    } else if (child instanceof BuildTextYAMLMetainfo) {
                        child_meta = (BuildTextYAMLMetainfo) child;
                        if (child_meta.getName() != null) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, "Meta information in a map provided at value must not specify a name (OCP/YAML file '%(info)')", "info", source_location_info);
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        if ((key instanceof String) == false) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, "A key in a map of OCP/YAML file '%(info)' must be either a String or of type 'BuildTextYAMLMetainfo'", "info", source_location_info);
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        child_meta.setName((String) key);
                        child = child_meta.getValue();
                    } else if (key instanceof String) {
                        child_meta = new BuildTextYAMLMetainfo((String) key);
                    } else {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, "A key in a map of OCP/YAML file '%(info)' must be either a String or of type 'BuildTextYAMLMetainfo'", "info", source_location_info);
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }

                    BuildText btchild = bt_factory.create(context, child, child_meta, source_location_info);

                    addChild(context, child_meta.getName(), btchild, this, item_hash, "", source_location_info);
                }
            }          

            if (node instanceof List) {
                List list = (List) node;
                int i=0;
                java.util.Iterator<Object> elements = list.iterator();
                while (elements.hasNext()) {
                    String id = "" + i;
                    Object child = elements.next();
                    String code_name = null;

                    if (is_out == true) {
                        NotificationContext.sendWarning(context, FactorySiteStringPool.get(context, "0.0.4" /* Tag with OUT attribute must not have childs (they are ignored) */));
                        continue;
                    }

                    String child_name = id;
                    BuildTextYAMLMetainfo child_meta = null;
                    if (child instanceof BuildTextYAMLMetainfo) {
                        child_meta = (BuildTextYAMLMetainfo) child;
                        child = child_meta.getValue();
                        if (this.isNonEmpty(context, child_meta.getName())) {
                            child_name = child_meta.getName();
                        }
                    } else {
                        child_meta = new BuildTextYAMLMetainfo(id);
                    }

                    BuildText btchild = bt_factory.create(context, child, child_meta, source_location_info);

                    addChild(context, child_name, btchild, this, item_hash, "", source_location_info);
                }
            }          
        }
    }

    protected void addChild(CallContext context, String cname, BuildText btchild, BuildTextComplex parent, HashMap<String, BuildText> item_hash, String prefixpath, String source_location_info) throws InvalidDocument {
        int dotpos = cname.indexOf(".");
        if (dotpos == -1) {
            parent.getItems(context).append(context, new Pair_BuildText_String_(context, btchild, cname));
        } else {
            String prefix = cname.substring(0, dotpos);
            BuildTextComplex item = (BuildTextComplex) item_hash.get(prefixpath + prefix);
            if (item == null) {
                String node_name = prefix;
                item = new BuildTextComplex_String(context, node_name, "", "", "", "", "", source_location_info);
                parent.getItems(context).append(context, new Pair_BuildText_String_(context, item, node_name));
                item_hash.put(prefixpath + prefix, item);
            }
            addChild(context, cname.substring(dotpos+1), btchild, item, item_hash, prefixpath + prefix + ".", source_location_info);
        }
    }

    public String getComponentType(CallContext context) { return this.component_type; }

    public void setComponentType(CallContext context, String component_type) { this.component_type = component_type; }

    public Vector_Pair_BuildText_String__long_ getItems (CallContext context) {
        return this.named_items;
    }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextComplex_String";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextComplex_String;
    }

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_ComponentType, this.getComponentType(context), true);
    }

    public void dump(CallContext context, DumpNode dump_node) {
        super.dump(context, dump_node);
        if (this.getComponentType(context) != null) {
            dump_node.dump(context, "ComponentType", this.getComponentType(context));
        }
        DumpNode dn = dump_node.openDump(context, "Items");
        for (Pair_BuildText_String_ item : this.getItems(context).getIterable_Pair_BuildText_String__(context)) {
            dn.dump(context, item.getItem2(context), item.getItem1(context));
        }
    }
}
