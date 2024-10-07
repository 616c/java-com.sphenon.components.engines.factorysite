package com.sphenon.engines.factorysite;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.Element;

public class BuildTextOptionalParameter_String extends BuildTextBaseImpl implements BuildText, BuildTextOptionalParameter {
    private BuildTextParameter_String p;
    private BuildText f;

    public BuildTextParameter_String getParameter (CallContext context) {
        return p;
    }

    public void setParameter (CallContext context, BuildTextParameter_String p) {
        this.p = p;

        p.setOID(context, EMPTY);
    }

    public BuildText getFallback (CallContext context) {
        return f;
    }

    public void setFallback (CallContext context, BuildText f) {
        this.f = f;

        f.setOID(context, EMPTY);
    }

    public BuildTextOptionalParameter_String (CallContext context) {
        super(context);

        this.assign_to = EMPTY;
        this.factory_name = EMPTY;
        this.retriever_name = EMPTY;
        this.method_name = EMPTY;
        this.alias = EMPTY;
        this.listener = EMPTY;
    }

    public BuildTextOptionalParameter_String (CallContext context, String oid, String type_name, BuildTextParameter_String p, BuildText f, String source_location_info) {
        super(context);

        this.assign_to = EMPTY;
        this.factory_name = EMPTY;
        this.retriever_name = EMPTY;
        this.method_name = EMPTY;
        this.alias = EMPTY;
        this.listener = EMPTY;

        this.p = p;
        this.f = f;
        this.oid = oid;
        p.setOID(context, EMPTY);
        f.setOID(context, EMPTY);
        this.type_name = type_name;
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
