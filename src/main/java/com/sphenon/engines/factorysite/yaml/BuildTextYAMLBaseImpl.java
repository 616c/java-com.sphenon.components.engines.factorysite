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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;

abstract public class BuildTextYAMLBaseImpl extends BuildTextBaseImpl implements BuildTextYAML {

    public BuildTextYAMLBaseImpl(CallContext context, Object node, BuildTextYAMLMetainfo meta, String source_location_info) {
        super(context);

        this.node = node;
        this.meta = meta;

        if (this.meta != null) {
            this.node_name = meta.getName();
            this.oid = meta.getOId();
            this.assign_to = meta.getAssignTo();
            this.type_name = meta.getJClass();
            this.factory_name = meta.getFactory();
            this.retriever_name = meta.getRetriever();
            this.method_name = meta.getMethod();
            this.alias = meta.getAlias();
            String typecheck = meta.getTypeCheck();
            this.allow_dynamic_type_check = (typecheck != null && typecheck.equals("allow_dynamic") ? true : false);
            String argumentcheck = meta.getArgumentCheck();
            this.allow_missing_arguments = (argumentcheck != null && argumentcheck.equals("allow_missing") ? true : false);
            String singleton = meta.getSingleton();
            this.is_singleton = (singleton != null && singleton.equals("true"));
            String dynamicparameters = meta.getDynamicParameters();
            this.have_dynamic_parameters = (dynamicparameters != null && dynamicparameters.equals("true"));
            this.name_attribute = meta.getNameExpression();
            this.if_expression = meta.getIf();
            this.foreach_expression = meta.getForEach();
            this.signature = meta.getSignature();
            this.define = meta.getDefine();
            this.evaluator = meta.getEvaluator();
            this.j_catch = meta.getCatch();
            String pass_string = meta.getPass();
            this.pass = (pass_string == null || pass_string.length() == 0 ? 1 : Integer.parseInt(pass_string));
            this.override = meta.getOverride();
            this.name_space = meta.getNameSpace();
            this.applies_to = meta.getAppliesTo();
            this.listener = meta.getListener();
        }
        if (this.node instanceof BuildTextYAMLDatainfo) {
            BuildTextYAMLDatainfo data = (BuildTextYAMLDatainfo) this.node;
            this.oid_ref = data.getOIdRef();
            this.is_expression = (data.getExpression() != null ? true : false);
        }

        this.source_location_info = source_location_info;
    }

    protected BuildTextYAMLMetainfo meta;

    protected Object node;

    public Object getNode (CallContext context) {
        return this.node;
    }

    public void setNode (CallContext context, Object node) {
        this.node = node;
    }
    protected String oid_ref;

    public String getOIDRef (CallContext context) {
        return this.oid_ref;
    }

    public void setOIDRef (CallContext context, String oid_ref) {
        this.oid_ref = oid_ref;
    }

    public boolean isEmpty(CallContext context, String value) {
        return (value == null || value.isEmpty() ? true : false);
    }

    public boolean isNonEmpty(CallContext context, String value) {
        return (value == null || value.isEmpty() ? false : true);
    }
}
