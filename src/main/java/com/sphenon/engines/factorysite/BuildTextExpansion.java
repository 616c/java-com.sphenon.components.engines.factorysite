package com.sphenon.engines.factorysite;

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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class BuildTextExpansion {

    private String name;
    private String type_name;
    private String factory_name;
    private String retriever_name;
    private String method_name;
    private Boolean allow_dynamic_type_check;
    private boolean allow_missing_arguments;
    protected boolean is_container;
    protected Integer pass;
    private String oid;

    public BuildTextExpansion(CallContext context) {
    }

    public String getName (CallContext context) {
        return this.name;
    }

    public void setName (CallContext context, String name) {
        this.name = name;
    }

    public String getTypeName (CallContext context) {
        return this.type_name;
    }

    public void setTypeName (CallContext context, String type_name) {
        this.type_name = type_name;
    }

    public String getFactoryName (CallContext context) {
        return this.factory_name;
    }

    public void setFactoryName (CallContext context, String factory_name) {
        this.factory_name = factory_name;
    }

    public String getRetrieverName (CallContext context) {
        return this.retriever_name;
    }

    public void setRetrieverName (CallContext context, String retriever_name) {
        this.retriever_name = retriever_name;
    }

    public String getMethodName (CallContext context) {
        return this.method_name;
    }

    public void setMethodName (CallContext context, String method_name) {
        this.method_name = method_name;
    }

    public boolean getIsContainer (CallContext context) {
        return this.is_container;
    }

    public void setIsContainer (CallContext context, boolean is_container) {
        this.is_container = is_container;
    }

    public Integer getPass (CallContext context) {
        return this.pass;
    }

    public void setPass (CallContext context, int pass) {
        this.pass = pass;
    }

    public Boolean allowDynamicTypeCheck (CallContext context) {
        return this.allow_dynamic_type_check;
    }

    public void setAllowDynamicTypeCheck (CallContext context, boolean allow_dynamic_type_check) {
        this.allow_dynamic_type_check = allow_dynamic_type_check;
    }

    public Boolean allowMissingArguments (CallContext context) {
        return this.allow_missing_arguments;
    }

    public void setAllowMissingArguments (CallContext context, boolean allow_missing_arguments) {
        this.allow_missing_arguments = allow_missing_arguments;
    }

    public String getOID (CallContext context) {
        return this.oid;
    }

    public void setOID (CallContext context, String oid) {
        this.oid = oid;
    }

    public String toString() {
        return name
            + "|" + type_name
            + "|" + factory_name
            + "|" + retriever_name
            + "|" + method_name
            + "|" + (is_container ? "CONTAINER" : "")
            + "|" + pass
            + "|" + (allow_dynamic_type_check != null && allow_dynamic_type_check ? "DYNAMIC" : "")
            + "|" + (allow_missing_arguments ? "MISSING" : "")
            + "|" + oid;
    }
}
