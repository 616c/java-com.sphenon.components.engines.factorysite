package com.sphenon.engines.factorysite.xml;

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

import java.util.HashMap;
import java.util.Vector;
import java.text.*;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

public class BuildTextComplexXML extends BuildTextXMLBaseImpl implements BuildTextComplex, Dumpable {
    private String text_locator;
    private String text_locator_base;
    private String component_type;
    private Vector_Pair_BuildText_String__long_ named_items;
    private String out;

    static public    RegularExpression fs_attribute_re = new RegularExpression("^((xml.*)|" + BuildTextKeywords.ATTRIBUTE_RE + "|(xmlns))$"); // public: braucht auch der LocatorBuildText
    static protected RegularExpression fs_attribute_postfix_re = new RegularExpression("^(.*)-(" + BuildTextKeywords.ATTRIBUTE_POSTFIX_RE + ")$");
    static protected RegularExpression meta_attribute_re = new RegularExpression("^meta:(.*)$");
    static protected RegularExpression code_attribute_re = new RegularExpression("^code:(.*)$");
    static protected RegularExpression number_remove_re = new RegularExpression("-[0-9]$","");

    static protected boolean xerces_message_already_printed = true; // naja...

    public BuildTextComplexXML (CallContext context, Element node, String source_location_info, BuildTextXMLFactory bt_factory) throws InvalidDocument {
        super(context, node, source_location_info);
        this.out = node.getAttribute(BuildTextKeywords.OUT);
        this.component_type = node.getAttribute(BuildTextKeywords.COMPONENTTYPE);

        named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);

        this.text_locator = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.LOCATOR, BuildTextKeywords.LOCATOR_UC);
        boolean got_text_locator = false;
        if (this.text_locator != null && this.text_locator.length() != 0) {
            this.retriever_name = "com.sphenon.basics.locating.retrievers.RetrieverByTextLocator";
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", source_location_info, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "String", "", "", this.text_locator, source_location_info), "")), "TextLocator"));
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", source_location_info, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "String", "", "", "Property", source_location_info), "")), "DefaultType"));
            this.allow_dynamic_type_check = true;
            got_text_locator = true;
        }
        this.text_locator_base = node.getAttribute(BuildTextKeywords.LOCATORBASE);
        if (this.text_locator_base != null && this.text_locator_base.length() != 0) {
            if (this.text_locator_base.charAt(0) == '#') {
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRef_String (context, "", "", "", this.text_locator_base.substring(1), source_location_info), "Base"));
            } else if (this.text_locator_base.charAt(0) == '$') {
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextParameter_String (context, "", "", "", this.text_locator_base.substring(1), source_location_info), "Base"));
            } else {
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRefById_String (context, "", "", "", this.text_locator_base, source_location_info), "Base"));
            }
        } else if ((this.text_locator_base = node.getAttribute(BuildTextKeywords.LOCATORBASEOIDREF)) != null && this.text_locator_base.length() != 0) {
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRef_String (context, "", "", "", this.text_locator_base, source_location_info), "Base"));
        } else if ((this.text_locator_base = node.getAttribute(BuildTextKeywords.LOCATORBASEPARAMETER)) != null && this.text_locator_base.length() != 0) {
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextParameter_String (context, "", "", "", this.text_locator_base, source_location_info), "Base"));
        } else if (got_text_locator) {
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRefById_String (context, "", "", "", "*", source_location_info), "Base"));
        }

        String expression_text = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.EXPRESSION_TEXT, BuildTextKeywords.EXPRESSION_TEXT_UC);
        if (expression_text != null && expression_text.isEmpty() == false) {
            String btsstype = "java.lang.String";
            BuildTextSimple_String btss = new BuildTextSimple_String(context, "", "", btsstype, "", "", expression_text, source_location_info);
            named_items.append(context, new Pair_BuildText_String_(context, btss, ""));
            btss.setIsExpression(context, this.isExpression(context));
        }

        String expression_value = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.EXPRESSION_VALUE, BuildTextKeywords.EXPRESSION_VALUE_UC);
        if (expression_value != null && expression_value.isEmpty() == false) {
            String btsstype = this.type_name;
            BuildTextSimple_String btss = new BuildTextSimple_String(context, "", "", btsstype, "", "", expression_value, source_location_info);
            named_items.append(context, new Pair_BuildText_String_(context, btss, ""));
            btss.setIsExpression(context, this.isExpression(context));
        }

        NamedNodeMap nnm = node.getAttributes();
        HashMap<String, BuildText> item_hash = new HashMap<String, BuildText>();
        for (int i=0; i<nnm.getLength(); i++) {
            Attr attr = (Attr) nnm.item(i);
            String aname = attr.getName();
            String[] m;
            if ((m = meta_attribute_re.tryGetMatches(context, aname)) != null) {
                if (meta_data == null) {
                    meta_data = new HashMap<String,Object>(4);
                }
                meta_data.put(m[0], attr.getValue());
            } else if ((m = code_attribute_re.tryGetMatches(context, aname)) != null) {
                String codetype = m[0];
                String code = attr.getValue();
                setCode(context, codetype, code, null, source_location_info);
            } else if (! fs_attribute_re.matches(context, aname)) {
                String text = attr.getValue();
                addAttributeItem(context, aname, text, this, item_hash, "", source_location_info);
            }
        }

        if (node.getAttribute(BuildTextKeywords.CONTENT) != null && node.getAttribute(BuildTextKeywords.CONTENT).equals("XML/DOM")) {
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextDOM_Node(context, "", "", "org.w3c.dom.Node", "", "", node, source_location_info), ""));
        } else if (node.getAttribute(BuildTextKeywords.CONTENT) != null && node.getAttribute(BuildTextKeywords.CONTENT).equals("XML/Text")) {
            String text1 = com.sphenon.basics.xml.XMLUtil.serialiseContent(context, node);
            String text2 = text1.replaceAll("xmlns=\"[^\"]*\"","");
            if (xerces_message_already_printed == false && text1.equals(text2) == false) {
                // name spaces are included in serialised xml even if
                // respective flags should disable this behaviour
                System.err.println("Exceptionally ugly bug in xerces! (siehe BuildTextComplexXML.java)");
                System.err.println("(following is the example,");
                System.err.println("further detecion of this behaviour will not be printed,");
                System.err.println("but output will be silently adjusted)");
                System.err.println("T1: " + text1);
                System.err.println("T2: " + text2);
                
                xerces_message_already_printed = true;
            }
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextSimple_String(context, "", "", "java.lang.String", "", "", text2, source_location_info), ""));
        } else if (node.getChildNodes().getLength() == 1 && (node.getFirstChild().getNodeType() == Node.TEXT_NODE || node.getFirstChild().getNodeType() == Node.CDATA_SECTION_NODE) && (node.getAttribute(BuildTextKeywords.COMPLEX) == null || node.getAttribute(BuildTextKeywords.COMPLEX).length() == 0)) {
            String text = null;
            try {
                text = ((Text) node.getFirstChild()).getData();
            } catch (ClassCastException e) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, FactorySiteStringPool.get(context, "0.1.0" /* Node for SimpleText must be a text node */));
            }
            if (node.getAttribute(BuildTextKeywords.CONTENT) != null && node.getAttribute(BuildTextKeywords.CONTENT).equals("Text/Indented")) {
                text = text.replaceFirst("^([ \\n]*\\n)", "").replaceFirst("([ \\n]*)$", "");
                CharacterIterator iter = new StringCharacterIterator(text);
                StringBuffer sb = new StringBuffer();
                for (char c = iter.first(); c != CharacterIterator.DONE && c == ' '; c = iter.next()) {
                    sb.append(c);
                }
                text = text.replaceAll("(^|\\n)" + sb.toString(), "$1");
            }

            String btsstype = "java.lang.String";
            if (BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.EXPRESSION, BuildTextKeywords.EXPRESSION_UC) != null && BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.EXPRESSION, BuildTextKeywords.EXPRESSION_UC).equals("value")) {
                btsstype = this.type_name;
            }
            BuildTextSimple_String btss = new BuildTextSimple_String(context, "", "", btsstype, "", "", text, source_location_info);
            named_items.append(context, new Pair_BuildText_String_(context, btss, ""));
            btss.setIsExpression(context, this.isExpression(context));
        } else {
            Node child = node.getFirstChild();

            if (out != null && out.length() != 0) {
                // what is this code and the 'OUT' attribute doing, does it work, did it work?
                // why, e.g., no childs?
                if (child != null) {
                    NotificationContext.sendWarning(context, FactorySiteStringPool.get(context, "0.0.4" /* Tag with OUT attribute must not have childs (they are ignored) */));
                }
                this.type_name = "FactorySiteOutParameter";
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, (String) null, (String) null, (String) null, (String) null, (String) null, BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.CLASS, BuildTextKeywords.CLASS_UC), source_location_info), "Type"));
            } else {
                while (child != null) {
                    int type = child.getNodeType();
                    if (type == Node.ELEMENT_NODE) {
                        Element element = null;
                        try {
                            element = (Element) child;
                        } catch (ClassCastException e) {
                            CustomaryContext.create((Context)context).throwInvalidState(context, FactorySiteStringPool.get(context, "0.0.0" /* Node is of DOM type 'ELEMENT_NODE' but cannot be down-cast to Element */));
                        }
                        String tag_name = element.getTagName();
                        if (    tag_name.startsWith("code:")
                             && element.getChildNodes().getLength() == 1
                             && (    element.getFirstChild().getNodeType() == Node.TEXT_NODE
                                  || element.getFirstChild().getNodeType() == Node.CDATA_SECTION_NODE
                                )
                           ) {
                            String code = ((Text) element.getFirstChild()).getData();
                            String codetype = tag_name.substring(5);
                            setCode(context, codetype, code, element.getAttribute(BuildTextKeywords.PASS), source_location_info);
                        } else {
                            String element_name = element.getAttribute(BuildTextKeywords.NAME);
                            if (element_name == null || element_name.length() == 0) {
                                element_name = tag_name;
                            }
                            BuildText btchild = bt_factory.create(context, element);
                            String btcnn = btchild.getNodeName(context);
                            if (btcnn != null && btcnn.startsWith("meta:")) {
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
                                Object value = (     btsubchild != null ?
                                                     (   btsubchild instanceof BuildTextSimple ?
                                                         ((BuildTextSimple) btsubchild).getText(context)
                                                         : btsubchild instanceof BuildTextDOM ?
                                                         ((BuildTextDOM) btsubchild).getNode(context)
                                                         : btchild
                                                         )
                                                     : btchild
                                    );
                                meta_data.put(btcnn.substring(5), value);
                            } else {
                                addChild(context, element_name, btchild, this, item_hash, "", source_location_info);
                            }
                        }
                    } else if (type == Node.COMMENT_NODE) {
                        /* kindly ignore */
                    } else {
                        if (type == Node.TEXT_NODE || type == Node.CDATA_SECTION_NODE) {
                            Text text = (Text) child;
                            
                            if (text.getData().matches("^\\s*$") == false) {
                                // NotificationContext.sendWarning(context, FactorySiteStringPool.get(context, "0.0.1" /* Text node in complex BuildText ist not plain whitespace, as expected: '%(data)' */), "data", text.getData());
                                InvalidDocument.createAndThrow(context, FactorySiteStringPool.get(context, "0.0.1" /* Text node in complex BuildText ist not plain whitespace, as expected: '%(data)' */), "data", text.getData());
                                throw (InvalidDocument) null; // compiler insists

                            }
                        } else {
                            NotificationContext.sendWarning(context, FactorySiteStringPool.get(context, "0.0.3" /* Node type '%(nodetype)' not expected */), "nodetype", t.s(type) + (type == Node.ENTITY_REFERENCE_NODE ? " - ENTITY_REFERENCE_NODE" : ""));
                        }
                    }
                    child = child.getNextSibling();
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

    protected void addAttributeItem(CallContext context, String aname, String text, BuildTextComplex parent, HashMap<String, BuildText> item_hash, String prefixpath, String source_location_info) throws InvalidDocument {
        int dotpos = aname.indexOf(".");
        if (dotpos == -1) {
            String[] matches = fs_attribute_postfix_re.tryGetMatches(context, aname);
            String n = matches == null ? aname : matches[0];
            String finalatt = matches == null ? null : matches[1];

            if (item_hash.get(prefixpath + n) != null) {
                InvalidDocument.createAndThrow(context, "Attribute '%(name)'='%(value)' is defined twice (via different subattribute postfixes)", "name", aname, "value", text);
                throw (InvalidDocument) null; // compiler insists
            }

            String node_name = number_remove_re.replaceAll(context, n);

            BuildText item = null;
            BuildTextComplex complex = null;
            if (finalatt != null) {
                if (finalatt.equals(BuildTextKeywords.EXPRESSION_VALUE) || finalatt.equals(BuildTextKeywords.EXPRESSION_VALUE_UC)) {
                    BuildTextSimple_String btss = new BuildTextSimple_String(context, "", "", "", "", "", text, source_location_info);
                    btss.setIsExpression(context, true);
                    item = btss;
                } else if (finalatt.equals(BuildTextKeywords.EXPRESSION_TEXT) || finalatt.equals(BuildTextKeywords.EXPRESSION_TEXT_UC)) {
                    item = complex = new BuildTextComplex_String(context, node_name, "", "", "", "", "", source_location_info);
                    ((BuildTextComplex_String) complex).setIsExpression(context, true);
                    BuildTextSimple_String btss = new BuildTextSimple_String(context, "", "", "java.lang.String", "", "", text, source_location_info);
                    btss.setIsExpression(context, true);
                    complex.getItems(context).append(context, new Pair_BuildText_String_(context, btss, ""));
                } else if (finalatt.equals(BuildTextKeywords.OIDREF)) {
                    item = new BuildTextRef_String(context, "", "", "", text, source_location_info);
                } else if (finalatt.equals(BuildTextKeywords.IDREF)) {
                    item = new BuildTextRefById_String(context, "", "", "", text, source_location_info);
                } else if (finalatt.equals(BuildTextKeywords.PARAMETER) || finalatt.equals(BuildTextKeywords.PARAMETER_UC)) {
                    item = new BuildTextParameter_String(context, "", "", "", text, source_location_info);
                } else if (finalatt.equals(BuildTextKeywords.NULL)) {
                    item = new BuildTextNull_String(context);
                } else if (finalatt.equals(BuildTextKeywords.LOCATOR) || finalatt.equals(BuildTextKeywords.LOCATOR_UC)) {
                    item = complex = new BuildTextComplex_String(context, node_name, "", "", "", "", "com.sphenon.basics.locating.retrievers.RetrieverByTextLocator", source_location_info);
                    complex.getItems(context).append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", source_location_info, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "String", "", "", text, source_location_info), "")), "TextLocator"));
                    complex.getItems(context).append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", source_location_info, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "String", "", "", "Property", source_location_info), "")), "DefaultType"));
                    complex.setAllowDynamicTypeCheck (context, true);
                }
            } else {
                item = complex = new BuildTextComplex_String(context, node_name, "", "", "", "", "", source_location_info);
                complex.getItems(context).append(context, new Pair_BuildText_String_(context, new BuildTextSimple_String(context, "", "", "java.lang.String", "", "", text, source_location_info), ""));
            }

            parent.getItems(context).append(context, new Pair_BuildText_String_(context, item, node_name));
            item_hash.put(prefixpath + n, item);
        } else {
            String prefix = aname.substring(0, dotpos);
            BuildTextComplex item = (BuildTextComplex) item_hash.get(prefixpath + prefix);
            if (item == null) {
                String node_name = number_remove_re.replaceAll(context, prefix);
                item = new BuildTextComplex_String(context, node_name, "", "", "", "", "", source_location_info);
                parent.getItems(context).append(context, new Pair_BuildText_String_(context, item, node_name));
                item_hash.put(prefixpath + prefix, item);
            }
            addAttributeItem(context, aname.substring(dotpos+1), text, item, item_hash, prefixpath + prefix + ".", source_location_info);
        }
    }

    protected void setCode(CallContext context, String codetype, String code, String pass, String source_location_info) {
        if (pass == null || pass.length() == 0) { pass = "1"; }
        if (codetype.equals("PreCondition")) {
            this.addPreCondition(context, code, pass, source_location_info);
        } else if (codetype.equals("PostCondition")) {
            this.addPostCondition(context, code, pass, source_location_info);
        } else if (codetype.equals("PreBuildScript")) {
            this.addPreBuildScript(context, code, pass, source_location_info);
        } else if (codetype.equals("PostBuildScript")) {
            this.addPostBuildScript(context, code, pass, source_location_info);
        } else if (codetype.equals("PreBuildMessage")) {
            this.addPreBuildMessage(context, code, pass, source_location_info);
        } else if (codetype.equals("PostBuildMessage")) {
            this.addPostBuildMessage(context, code, pass, source_location_info);
        } else if (codetype.equals("PreBuildDump")) {
            this.addPreBuildDump(context, code, pass, source_location_info);
        } else if (codetype.equals("PostBuildDump")) {
            this.addPostBuildDump(context, code, pass, source_location_info);
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
