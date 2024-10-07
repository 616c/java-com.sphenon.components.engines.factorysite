package com.sphenon.engines.factorysite.xml;

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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

abstract public class BuildTextXMLBaseImpl extends BuildTextBaseImpl implements BuildTextXML {

    public BuildTextXMLBaseImpl(CallContext context, Element node, String source_location_info) {
        super(context);
        this.node = node;
        if (node != null) {
            this.node_name = node.getAttribute(BuildTextKeywords.NAME);
            if (this.node_name == null || this.node_name.length() == 0) {
                this.node_name = (   node.getNodeType() == Node.ELEMENT_NODE
                                   ? ((Element)node).getTagName().replaceFirst(".*\\.","")
                                     // replaceFirst: possible dotted prefixes are handled in
                                     // BuildTextComplexXML / getChild
                                     // here only the final part should be processed
                                   : null
                                 );
            }
            this.oid = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.OID, BuildTextKeywords.OID_UC);
            this.oid_ref = node.getAttribute(BuildTextKeywords.OIDREF);
            this.assign_to = node.getAttribute(BuildTextKeywords.ASSIGNTO);
            this.type_name = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.CLASS, BuildTextKeywords.CLASS_UC);
            this.factory_name = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.FACTORY, BuildTextKeywords.FACTORY_UC);
            this.retriever_name = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.RETRIEVER, BuildTextKeywords.RETRIEVER_UC);
            this.method_name = node.getAttribute(BuildTextKeywords.METHOD);
            this.alias = node.getAttribute(BuildTextKeywords.ALIAS);
            String typecheck = node.getAttribute(BuildTextKeywords.TYPECHECK);
            this.allow_dynamic_type_check = (typecheck != null && typecheck.equals("allow_dynamic") ? true : false);
            String argumentcheck = node.getAttribute(BuildTextKeywords.ARGUMENTCHECK);
            this.allow_missing_arguments = (argumentcheck != null && argumentcheck.equals("allow_missing") ? true : false);
            this.is_singleton = (node.getAttributeNode(BuildTextKeywords.SINGLETON) != null);
            this.have_dynamic_parameters = (node.getAttributeNode(BuildTextKeywords.DYNAMICPARAMETERS) != null);
            this.name_attribute = node.getAttribute(BuildTextKeywords.NAME);
            this.is_expression = (    node.getAttributeNode(BuildTextKeywords.EXPRESSION) != null
                                   || node.getAttributeNode(BuildTextKeywords.EXPRESSION_TEXT) != null
                                   || node.getAttributeNode(BuildTextKeywords.EXPRESSION_TEXT_UC) != null
                                   || node.getAttributeNode(BuildTextKeywords.EXPRESSION_VALUE) != null
                                   || node.getAttributeNode(BuildTextKeywords.EXPRESSION_VALUE_UC) != null
                                 );
            this.if_expression = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.IF, BuildTextKeywords.IF_UC);
            this.foreach_expression = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.FOREACH, BuildTextKeywords.FOREACH_UC);
            this.signature = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.SIGNATURE, BuildTextKeywords.SIGNATURE_UC);
            this.define = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.DEFINE, BuildTextKeywords.DEFINE_UC);
            this.evaluator = node.getAttribute(BuildTextKeywords.EVALUATOR);
            this.j_catch = node.getAttribute(BuildTextKeywords.CATCH);
            String pass_string = node.getAttribute(BuildTextKeywords.PASS);
            this.pass = (pass_string == null || pass_string.length() == 0 ? 1 : Integer.parseInt(pass_string));
            this.override = node.getAttribute(BuildTextKeywords.OVERRIDE);
            this.name_space = node.getAttribute("xmlns");
            this.applies_to = node.getAttribute(BuildTextKeywords.APPLIESTO);
            this.listener = node.getAttribute(BuildTextKeywords.LISTENER);
        }
        this.source_location_info = source_location_info;
    }

    protected Node node;

    public Node getNode (CallContext context) {
        return this.node;
    }

    public void setNode (CallContext context, Node node) {
        this.node = node;
    }
    protected String oid_ref;

    public String getOIDRef (CallContext context) {
        return this.oid_ref;
    }

    public void setOIDRef (CallContext context, String oid_ref) {
        this.oid_ref = oid_ref;
    }
}
