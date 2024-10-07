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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

abstract public class BuildTextJSONBaseImpl extends BuildTextBaseImpl implements BuildTextJSON {

    public BuildTextJSONBaseImpl(CallContext context, JsonNode node, String name, String source_location_info) {
        super(context);
        this.node = node;
        this.meta_node = BuildTextJSONFactory.getMetaNode(context, this.node);
        if (this.meta_node != null) {
            this.node_name = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Name);
            if (this.node_name == null || this.node_name.length() == 0) {
                this.node_name = name;
            }

            this.oid = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.OId, BuildTextKeywords.OID_UC);
            this.oid_ref = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.OIdRef);
            this.assign_to = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.AssignTo);
            this.type_name = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Class, BuildTextKeywords.CLASS_UC);
            this.factory_name = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Factory, BuildTextKeywords.FACTORY_UC);
            this.retriever_name = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Retriever, BuildTextKeywords.RETRIEVER_UC);
            this.method_name = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Method);
            this.alias = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Alias);
            String typecheck = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.TypeCheck);
            this.allow_dynamic_type_check = (typecheck != null && typecheck.equals("allow_dynamic") ? true : false);
            String argumentcheck = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.ArgumentCheck);
            this.allow_missing_arguments = (argumentcheck != null && argumentcheck.equals("allow_missing") ? true : false);
            String singleton = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Singleton);
            this.is_singleton = (singleton != null && singleton.equals("true"));
            String dynamicparameters = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.DynamicParameters);
            this.have_dynamic_parameters = (dynamicparameters != null && dynamicparameters.equals("true"));
            this.name_attribute = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Name);
            this.is_expression = (    BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Expression, BuildTextKeywords.EXPRESSION_UC) != null
                                      || BuildTextJSONFactory.getAttribute(context, this.meta_node, "@Expression-Text") != null
                                      || BuildTextJSONFactory.getAttribute(context, this.meta_node, "@Expression-Value") != null
                                 );
            this.if_expression = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.If, BuildTextKeywords.IF_UC);
            this.foreach_expression = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.ForEach, BuildTextKeywords.FOREACH_UC);
            this.signature = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Signature, BuildTextKeywords.SIGNATURE_UC);
            this.define = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Define, BuildTextKeywords.DEFINE_UC);
            this.evaluator = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Evaluator);
            this.j_catch = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Catch);
            String pass_string = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Pass);
            this.pass = (pass_string == null || pass_string.length() == 0 ? 1 : Integer.parseInt(pass_string));
            this.override = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Override);
            this.name_space = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.NameSpace);
            this.applies_to = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.AppliesTo);
            this.listener = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Listener);
        }
        this.source_location_info = source_location_info;
    }

    protected JsonNode meta_node;

    protected JsonNode node;

    public JsonNode getNode (CallContext context) {
        return this.node;
    }

    public void setNode (CallContext context, JsonNode node) {
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
