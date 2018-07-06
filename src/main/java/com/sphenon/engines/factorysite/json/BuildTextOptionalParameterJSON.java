package com.sphenon.engines.factorysite.json;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

public class BuildTextOptionalParameterJSON extends BuildTextBaseImpl implements BuildTextJSON, BuildTextOptionalParameter {
    private BuildTextParameterJSON p;
    private BuildText              f;

    protected JsonNode meta_node;
    protected JsonNode node;

    public BuildTextParameter getParameter (CallContext context) {
        return p;
    }

    public BuildText          getFallback  (CallContext context) {
        return f;
    }

    public BuildTextOptionalParameterJSON (CallContext context, JsonNode node, BuildTextParameterJSON p, BuildText f, String source_location_info) {
        super(context);

        this.assign_to = "";
        this.factory_name = "";
        this.retriever_name = "";
        this.method_name = "";
        this.alias = "";
        this.listener = "";

        this.p = p;
        this.f = f;

        this.node = node;
        this.meta_node = BuildTextJSONFactory.getMetaNode(context, this.node);
        if (this.meta_node != null) {
            this.oid = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.OId);
            this.type_name = BuildTextJSONFactory.getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Class);
        }

        p.setOID(context, "");
        f.setOID(context, "");
        this.source_location_info = source_location_info;
    }

    public String getTypeName (CallContext context) { return this.type_name; }

    public void setTypeName (CallContext context, String type_name) {
        this.type_name = type_name;
        this.f.setTypeName(context, type_name);
        this.p.setTypeName(context, type_name);
    }
    public void setFactoryName (CallContext context, String factory_name) {
        this.f.setFactoryName (context, factory_name);
        this.p.setFactoryName (context, factory_name);
    }
    public void setRetrieverName (CallContext context, String retriever_name) {
        this.f.setRetrieverName (context, retriever_name);
        this.p.setRetrieverName (context, retriever_name);
    }
    public void setMethodName (CallContext context, String method_name) {
        this.f.setMethodName (context, method_name);
        this.p.setMethodName (context, method_name);
    }

    public void setNodeName (CallContext context, String node_name) {
        this.f.setNodeName (context, node_name);
        this.p.setNodeName (context, node_name);
    }
    public void setNameAttribute (CallContext context, String name_attribute) {
        this.f.setNameAttribute (context, name_attribute);
        this.p.setNameAttribute (context, name_attribute);
    }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextOptionalParameter_String";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextOptionalParameter_String;
    }
}
