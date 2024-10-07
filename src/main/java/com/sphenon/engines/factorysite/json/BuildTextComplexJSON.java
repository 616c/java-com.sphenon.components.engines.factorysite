package com.sphenon.engines.factorysite.json;

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

import java.util.HashMap;
import java.util.Vector;
import java.text.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

public class BuildTextComplexJSON extends BuildTextJSONBaseImpl implements BuildTextComplex, Dumpable {
    private String text_locator;
    private String text_locator_base;
    private String component_type;
    private Vector_Pair_BuildText_String__long_ named_items;
    private String out;

    protected JsonNode meta_node;
    protected JsonNode node;

    static protected RegularExpression meta_attribute_re = new RegularExpression("^Meta:(.*)$");
    static protected RegularExpression code_attribute_re = new RegularExpression("^Code:(.*)$");
    static protected RegularExpression number_remove_re = new RegularExpression("-[0-9]$","");

    static protected boolean xerces_message_already_printed;

    protected String getMetaAttribute(CallContext context, String name) {
        return BuildTextJSONFactory.getAttribute(context, this.meta_node, name);
    }

    protected String getMetaAttribute(CallContext context, String name1, String name2) {
        return BuildTextJSONFactory.getAttribute(context, this.meta_node, name1, name2);
    }

    protected String getAttributeFrom(CallContext context, JsonNode node, String name) {
        if (    node != null
             && node.isObject()
             && node.get(name) != null
           ) {
            return node.get(name).asText();
        }
        return "";
    }

    public BuildTextComplexJSON (CallContext context, JsonNode node, String name, String source_location_info, BuildTextJSONFactory bt_factory) throws InvalidDocument {
        super(context, node, name, source_location_info);

        this.node = node;
        this.meta_node = BuildTextJSONFactory.getMetaNode(context, this.node);
        if (this.meta_node != null) {
            this.out = getMetaAttribute(context, "@" + BuildTextKeywords.Out);
            this.component_type = getMetaAttribute(context, "@" + BuildTextKeywords.ComponentType);
            this.text_locator = getMetaAttribute(context, "@" + BuildTextKeywords.Locator, BuildTextKeywords.LOCATOR_UC);
            this.text_locator_base = getMetaAttribute(context, "@" + BuildTextKeywords.LocatorBase);
        }

        boolean unnamed_value_field = false;
        int named_value_fields = 0;
        if (node.isObject()) {
            java.util.Iterator<String> iterator = node.fieldNames();
            while (iterator.hasNext()) {
                String field_name = iterator.next();
                if (field_name.startsWith("#")) {
                    /* skip, comment */
                } else if (field_name.equals("@")) {
                    unnamed_value_field = true;
                } else if (field_name.isEmpty() || field_name.charAt(0) != '@') {
                    named_value_fields++;
                }
            }
        }

        named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);

        boolean got_text_locator = false;
        if (this.text_locator != null && this.text_locator.length() != 0) {
            this.retriever_name = "com.sphenon.basics.locating.retrievers.RetrieverByTextLocator";
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", source_location_info, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "String", "", "", this.text_locator, source_location_info), "")), "TextLocator"));
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", source_location_info, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "String", "", "", "Property", source_location_info), "")), "DefaultType"));
            this.allow_dynamic_type_check = true;
            got_text_locator = true;
        }

        this.text_locator_base = getMetaAttribute(context, "@" + BuildTextKeywords.LocatorBase);
        if (this.text_locator_base != null && this.text_locator_base.length() != 0) {
            if (this.text_locator_base.charAt(0) == '#') {
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRef_String (context, "", "", "", this.text_locator_base.substring(1), source_location_info), "Base"));
            } else if (this.text_locator_base.charAt(0) == '$') {
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextParameter_String (context, "", "", "", this.text_locator_base.substring(1), source_location_info), "Base"));
            } else {
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRefById_String (context, "", "", "", this.text_locator_base, source_location_info), "Base"));
            }
        } else if ((this.text_locator_base = getMetaAttribute(context, "@" + BuildTextKeywords.LocatorBaseOIdRef)) != null && this.text_locator_base.length() != 0) {
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRef_String (context, "", "", "", this.text_locator_base, source_location_info), "Base"));
        } else if ((this.text_locator_base = getMetaAttribute(context, "@" + BuildTextKeywords.LocatorBaseParameter)) != null && this.text_locator_base.length() != 0) {
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextParameter_String (context, "", "", "", this.text_locator_base, source_location_info), "Base"));
        } else if (got_text_locator) {
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRefById_String (context, "", "", "", "*", source_location_info), "Base"));
        }

        String expression_text = getMetaAttribute(context, "@Expression-Text");
        if (expression_text != null && expression_text.isEmpty() == false) {
            String btsstype = "java.lang.String";
            BuildTextSimple_String btss = new BuildTextSimple_String(context, "", "", btsstype, "", "", expression_text, source_location_info);
            named_items.append(context, new Pair_BuildText_String_(context, btss, ""));
            btss.setIsExpression(context, this.isExpression(context));
        }

        String expression_value = getMetaAttribute(context, "@Expression-Value");
        if (expression_value != null && expression_value.isEmpty() == false) {
            String btsstype = this.type_name;
            BuildTextSimple_String btss = new BuildTextSimple_String(context, "", "", btsstype, "", "", expression_value, source_location_info);
            named_items.append(context, new Pair_BuildText_String_(context, btss, ""));
            btss.setIsExpression(context, this.isExpression(context));
        }

        HashMap<String, BuildText> item_hash = new HashMap<String, BuildText>();

        String content = getMetaAttribute(context, "@" + BuildTextKeywords.Content);
        if (content != null && content.equals("JSON/Node")) {
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextJSONRawJSON(context, "", "", "com.fasterxml.jackson.databind.JsonNode", "", "", node, "", source_location_info), ""));
        } else if (content != null && content.equals("JSON/Text")) {

            JsonNode content_node = null;
            if (node.isObject()) {
                content_node = node.get("@");
            } else if (node.isArray()) {
                int i=0;
                do {
                    content_node = node.get(i++);
                } while (    content_node != null
                          && (    content_node.isObject() == false
                               || content_node.get("@" + BuildTextKeywords.Meta) == null
                               || content_node.get("@" + BuildTextKeywords.Meta).asText().equals("true") == false
                             )
                        );
            }

            String text = "";
            if (content_node != null) {
                text = BuildTextJSONFactory.getText(context, content_node);
            }
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextSimple_String(context, "", "", "java.lang.String", "", "", text, source_location_info), ""));
        } else if (unnamed_value_field == true && named_value_fields == 0) {
            String text = node.get("@").asText();
            if (content != null && content.equals("Text/Indented")) {
                text = text.replaceFirst("^([ \\n]*\\n)", "").replaceFirst("([ \\n]*)$", "");
                CharacterIterator iter = new StringCharacterIterator(text);
                StringBuffer sb = new StringBuffer();
                for (char c = iter.first(); c != CharacterIterator.DONE && c == ' '; c = iter.next()) {
                    sb.append(c);
                }
                text = text.replaceAll("(^|\\n)" + sb.toString(), "$1");
            }

            String btsstype = "java.lang.String";
            if (getMetaAttribute(context, "@" + BuildTextKeywords.Expression, BuildTextKeywords.EXPRESSION_UC) != null && getMetaAttribute(context, "@" + BuildTextKeywords.Expression, BuildTextKeywords.EXPRESSION_UC).equals("value")) {
                btsstype = this.type_name;
            }
            BuildTextSimple_String btss = new BuildTextSimple_String(context, "", "", btsstype, "", "", text, source_location_info);
            named_items.append(context, new Pair_BuildText_String_(context, btss, ""));
            btss.setIsExpression(context, this.isExpression(context));
        } else {

            boolean is_out = false;
            if (out != null && out.length() != 0) {
                is_out = true;
                this.type_name = "FactorySiteOutParameter";
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, (String) null, (String) null, (String) null, (String) null, (String) null, getMetaAttribute(context, "@" + BuildTextKeywords.Class, BuildTextKeywords.CLASS_UC), source_location_info), "Type"));
            }

            if (node.isObject()) {
                java.util.Iterator<String> iterator = node.fieldNames();
                while (iterator.hasNext()) {
                    String field_name = iterator.next();
                    if (field_name.startsWith("#")) {
                        /* comment, skip */
                    } else if (field_name.equals("@")) {
                        if (is_out == true) {
                            NotificationContext.sendWarning(context, FactorySiteStringPool.get(context, "0.0.4" /* Tag with OUT attribute must not have childs (they are ignored) */));
                            continue;
                        }
                    } else if (field_name.isEmpty() || field_name.charAt(0) != '@' || field_name.startsWith("@Meta:")) {
                        if (is_out == true) {
                            NotificationContext.sendWarning(context, FactorySiteStringPool.get(context, "0.0.4" /* Tag with OUT attribute must not have childs (they are ignored) */));
                            continue;
                        }
                        JsonNode child = node.get(field_name);
                        String child_name = BuildTextJSONFactory.getNodeName(context, child);
                        if (child_name == null || child_name.length() == 0) {
                            child_name = field_name;
                        }

                        BuildText btchild = null;
                        if (child.isValueNode()) {
                            String text = child.asText();
                            btchild = new BuildTextComplex_String(context, "", "", "", "", "", source_location_info,
                                          new Pair_BuildText_String_(context,
                                              new BuildTextSimple_String(context, "", "", "java.lang.String", "", "", text, source_location_info), ""));
                        } else {
                            btchild = bt_factory.create(context, child, child_name);
                        }

                        if (field_name.startsWith("@Meta:")) {
                            BuildText btsubchild = null;
                            if (btchild instanceof BuildTextComplex) {
                                Vector_Pair_BuildText_String__long_ items = ((BuildTextComplex)btchild).getItems(context);
                                if (items != null && items.getSize(context) == 1) {
                                    Pair_BuildText_String_ pair = items.tryGet(context, 0);
                                    if (pair != null && pair.getItem2(context) != null && pair.getItem2(context).length() == 0) {
                                        btsubchild = pair.getItem1(context);
                                    }
                                }
                            }
                            if (meta_data == null) {
                                meta_data = new HashMap<String,Object>();
                            }
                            Object value = (   btsubchild != null ?
                                               (   btsubchild instanceof BuildTextSimple ?
                                                   ((BuildTextSimple) btsubchild).getText(context)
                                                   : btsubchild instanceof BuildTextJSONRaw ?
                                                   ((BuildTextJSONRaw) btsubchild).getNode(context)
                                                   : btchild
                                                   )
                                               : btchild
                                           );
                            meta_data.put(field_name.substring(6), value);
                        } else {
                            addChild(context, child_name, btchild, this, item_hash, "", source_location_info);
                        }
                    } else {
                        if (field_name.startsWith("@Code:")) {
                            String code = BuildTextJSONFactory.getText(context, node.get(field_name));
                            String codetype = field_name.substring(6);
                            setCode(context, codetype, code, getMetaAttribute(context, "@" + BuildTextKeywords.Pass), source_location_info);
                        }
                    }
                }
            }          

            if (node.isArray()) {
                int i=0;
                java.util.Iterator<JsonNode> elements = node.elements();
                while (elements.hasNext()) {
                    String id = "" + i;
                    JsonNode child = elements.next();
                    String code_name = null;
                    if (getAttributeFrom(context, child, "@" + BuildTextKeywords.Meta).equals("true")) {
                        /* skip */
                    } else if ((code_name = getAttributeFrom(context, child, "@" + BuildTextKeywords.Code)).isEmpty() == false) {
                        String code = BuildTextJSONFactory.getText(context, child);
                        String codetype = code_name;
                        setCode(context, codetype, code, getMetaAttribute(context, "@" + BuildTextKeywords.Pass), source_location_info);
                    } else {
                        if (is_out == true) {
                            NotificationContext.sendWarning(context, FactorySiteStringPool.get(context, "0.0.4" /* Tag with OUT attribute must not have childs (they are ignored) */));
                            continue;
                        }
                        String child_name = BuildTextJSONFactory.getNodeName(context, child);
                        if (child_name == null || child_name.length() == 0) {
                            child_name = id;
                        }
                        BuildText btchild = bt_factory.create(context, child, child_name);
                        String meta_name = getAttributeFrom(context, child, "@" + BuildTextKeywords.Meta);
                        if (meta_name != null && meta_name.isEmpty() == false && meta_name.equals("true") == false) {
                            BuildText btsubchild = null;
                            if (btchild instanceof BuildTextComplex) {
                                Vector_Pair_BuildText_String__long_ items = ((BuildTextComplex)btchild).getItems(context);
                                if (items != null && items.getSize(context) == 1) {
                                    Pair_BuildText_String_ pair = items.tryGet(context, 0);
                                    if (pair != null && pair.getItem2(context) != null && pair.getItem2(context).length() == 0) {
                                        btsubchild = pair.getItem1(context);
                                    }
                                }
                            }
                            if (meta_data == null) {
                                meta_data = new HashMap<String,Object>();
                            }
                            Object value = (   btsubchild != null ?
                                               (   btsubchild instanceof BuildTextSimple ?
                                                   ((BuildTextSimple) btsubchild).getText(context)
                                                   : btsubchild instanceof BuildTextJSONRaw ?
                                                   ((BuildTextJSONRaw) btsubchild).getNode(context)
                                                   : btchild
                                                   )
                                               : btchild
                                           );
                            meta_data.put(meta_name, value);
                        } else {
                            addChild(context, child_name, btchild, this, item_hash, "", source_location_info);
                            i++;
                        }
                    }
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

    protected void setCode(CallContext context, String codetype, String code, String pass, String source_location_info) {
        if (pass == null || pass.length() == 0) { pass = "1"; }
        if (codetype.equals("PreCondition")) {
            if (this.pre_conditions == null) {
                this.pre_conditions = new Vector<String[]>();
            }
            String[] codepass = { code, pass, source_location_info };
            this.pre_conditions.add(codepass);
        } else if (codetype.equals("PostCondition")) {
            if (this.post_conditions == null) {
                this.post_conditions = new Vector<String[]>();
            }
            String[] codepass = { code, pass, source_location_info };
            this.post_conditions.add(codepass);
        } else if (codetype.equals("PreBuildScript")) {
            if (this.pre_build_scripts == null) {
                this.pre_build_scripts = new Vector<String[]>();
            }
            String[] codepass = { code, pass, source_location_info };
            this.pre_build_scripts.add(codepass);
        } else if (codetype.equals("PostBuildScript")) {
            if (this.post_build_scripts == null) {
                this.post_build_scripts = new Vector<String[]>();
            }
            String[] codepass = { code, pass, source_location_info };
            this.post_build_scripts.add(codepass);
        } else if (codetype.equals("PreBuildMessage")) {
            if (this.pre_build_scripts == null) {
                this.pre_build_scripts = new Vector<String[]>();
            }
            if (code != null && code.length() != 0 && code.charAt(0) != '"') {
                code = '"' + code + '"';
            }
            String[] codepass = { "java.lang.System.err.println(" + code + ")", pass, source_location_info };
            this.pre_build_scripts.add(codepass);
        } else if (codetype.equals("PostBuildMessage")) {
            if (this.post_build_scripts == null) {
                this.post_build_scripts = new Vector<String[]>();
            }
            if (code != null && code.length() != 0 && code.charAt(0) != '"') {
                code = '"' + code + '"';
            }
            String[] codepass = { "java.lang.System.err.println(" + code + ")", pass, source_location_info };
            this.post_build_scripts.add(codepass);
        } else if (codetype.equals("PreBuildDump")) {
            if (this.pre_build_scripts == null) {
                this.pre_build_scripts = new Vector<String[]>();
            }
            String[] codepass = { "java.lang.System.err.println(\"=========================================================\\n" + code + "\\n=========================================================\\n\"+Packages.com.sphenon.basics.debug.RuntimeStepContext.get(context).getStackDump(context)+\"\\n=========================================================\")", pass, source_location_info };
            this.pre_build_scripts.add(codepass);
        } else if (codetype.equals("PostBuildDump")) {
            if (this.post_build_scripts == null) {
                this.post_build_scripts = new Vector<String[]>();
            }
            String[] codepass = { "java.lang.System.err.println(\"=========================================================\\n" + code + "\\n=========================================================\\n\"+Packages.com.sphenon.basics.debug.RuntimeStepContext.get(context).getStackDump(context)+\"\\n=========================================================\")", pass, source_location_info };
            this.post_build_scripts.add(codepass);
        } else {
            NotificationContext.sendCaution(context, "Unrecognized code fragment of type '%(type)' in OCP", "type", codetype);
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
