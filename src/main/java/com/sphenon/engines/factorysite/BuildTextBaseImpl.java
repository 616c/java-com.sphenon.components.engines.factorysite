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
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.xml.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

import java.io.StringWriter;

abstract public class BuildTextBaseImpl extends BuildTextBase {

    public BuildTextBaseImpl(CallContext context) {
        super(context);
        this.ocp_id                   = null;
        this.node_name                = null;
        this.oid                      = null;
        this.assign_to                = null;
        this.type_name                = null;
        this.factory_name             = null;
        this.retriever_name           = null;
        this.method_name              = null;
        this.alias                    = null;
        this.allow_dynamic_type_check = false;
        this.allow_missing_arguments  = false;
        this.is_singleton             = false;
        this.have_dynamic_parameters  = false;
        this.name_attribute           = null;
        this.is_expression            = false;
        this.if_expression            = null;
        this.foreach_expression       = null;
        this.define                   = null;
        this.evaluator                = null;
        this.applies_to               = null;
        this.pass                     = 1;
        this.listener                 = null;
        this.source_location_info     = null;
    }

    protected String ocp_id;

    public String getOCPId (CallContext context) {
        return this.ocp_id;
    }

    public void setOCPId (CallContext context, String ocp_id) {
        this.ocp_id = ocp_id;
    }

    protected String node_name;

    public String getNodeName (CallContext context) {
        return this.node_name;
    }

    public void setNodeName (CallContext context, String node_name) {
        this.node_name = node_name;
    }

    protected String oid;

    public String getOID (CallContext context) {
        return this.oid;
    }

    public void setOID (CallContext context, String oid) {
        this.oid = oid;
    }

    protected String base;

    public String getBase (CallContext context) {
        return this.base;
    }

    public void setBase (CallContext context, String base) {
        this.base = base;
    }

    protected String polymorphic;

    public String getPolymorphic (CallContext context) {
        return this.polymorphic;
    }

    public void setPolymorphic (CallContext context, String polymorphic) {
        this.polymorphic = polymorphic;
    }

    protected String assign_to;

    public String getAssignTo (CallContext context) {
        return this.assign_to;
    }

    public void setAssignTo (CallContext context, String assign_to) {
        this.assign_to = assign_to;
    }

    protected String type_name;

    public String getTypeName (CallContext context) {
        return this.type_name;
    }

    public void setTypeName (CallContext context, String type_name) {
        this.type_name = type_name;
    }

    protected String factory_name;

    public String getFactoryName (CallContext context) {
        return this.factory_name;
    }

    public void setFactoryName (CallContext context, String factory_name) {
        this.factory_name = factory_name;
    }

    protected String retriever_name;

    public String getRetrieverName (CallContext context) {
        return this.retriever_name;
    }

    public void setRetrieverName (CallContext context, String retriever_name) {
        this.retriever_name = retriever_name;
    }

    protected String method_name;

    public String getMethodName (CallContext context) {
        return this.method_name;
    }

    public void setMethodName (CallContext context, String method_name) {
        this.method_name = method_name;
    }

    protected String alias;

    public String getAlias (CallContext context) {
        return this.alias;
    }

    public void setAlias (CallContext context, String alias) {
        this.alias = alias;
    }

    protected boolean allow_dynamic_type_check;

    public boolean allowDynamicTypeCheck (CallContext context) {
        return this.allow_dynamic_type_check;
    }

    public void setAllowDynamicTypeCheck (CallContext context, boolean allow_dynamic_type_check) {
        this.allow_dynamic_type_check = allow_dynamic_type_check;
    }

    protected boolean allow_missing_arguments;

    public boolean allowMissingArguments (CallContext context) {
        return this.allow_missing_arguments;
    }

    public void setAllowMissingArguments (CallContext context, boolean allow_missing_arguments) {
        this.allow_missing_arguments = allow_missing_arguments;
    }

    protected boolean is_singleton;

    public boolean isSingleton (CallContext context) {
        return this.is_singleton;
    }

    public void setIsSingleton (CallContext context, boolean is_singleton) {
        this.is_singleton = is_singleton;
    }

    protected boolean have_dynamic_parameters;

    public boolean haveDynamicParameters (CallContext context) {
        return this.have_dynamic_parameters;
    }

    public void setHaveDynamicParameters (CallContext context, boolean have_dynamic_parameters) {
        this.have_dynamic_parameters = have_dynamic_parameters;
    }

    protected String name_attribute;

    public String getNameAttribute (CallContext context) {
        return this.name_attribute;
    }

    public void setNameAttribute (CallContext context, String name_attribute) {
        this.name_attribute = name_attribute;
    }

    protected boolean is_expression;

    public boolean isExpression (CallContext context) {
        return this.is_expression;
    }

    public void setIsExpression (CallContext context, boolean is_expression) {
        this.is_expression = is_expression;
    }

    protected String if_expression;

    public String getIfExpression (CallContext context) {
        return this.if_expression;
    }

    public void setIfExpression (CallContext context, String if_expression) {
        this.if_expression = if_expression;
    }

    protected String foreach_expression;

    public String getForeachExpression (CallContext context) {
        return this.foreach_expression;
    }

    public void setForeachExpression (CallContext context, String foreach_expression) {
        this.foreach_expression = foreach_expression;
    }

    protected String signature;

    public String getSignature (CallContext context) {
        return this.signature;
    }

    public void setSignature (CallContext context, String signature) {
        this.signature = signature;
    }

    protected String define;

    public String getDefine (CallContext context) {
        return this.define;
    }

    public void setDefine (CallContext context, String define) {
        this.define = define;
    }

    protected String evaluator;

    public String getEvaluator (CallContext context) {
        return this.evaluator;
    }

    public void setEvaluator (CallContext context, String evaluator) {
        this.evaluator = evaluator;
    }

    protected String applies_to;

    public String getAppliesTo (CallContext context) {
        return this.applies_to;
    }

    public void setAppliesTo (CallContext context, String applies_to) {
        this.applies_to = applies_to;
    }

    protected String listener;

    public String getListener (CallContext context) {
        return this.listener;
    }

    public void setListener (CallContext context, String listener) {
        this.listener = listener;
    }

    protected String j_catch;

    public String getCatch (CallContext context) {
        return this.j_catch;
    }

    public void setCatch (CallContext context, String j_catch) {
        this.j_catch = j_catch;
    }

    protected int pass;

    public int getPass (CallContext context) {
        return this.pass;
    }

    public void setPass (CallContext context, int pass) {
        this.pass = pass;
    }

    protected String override;

    public String getOverride (CallContext context) {
        return this.override;
    }

    public void setOverride (CallContext context, String override) {
        this.override = override;
    }

    protected String name_space;

    public String getNameSpace (CallContext context) {
        return this.name_space;
    }

    public void setNameSpace (CallContext context, String name_space) {
        this.name_space = name_space;
    }

    protected String source_location_info;

    public String getSourceLocationInfo (CallContext context) {
        return this.source_location_info;
    }

    public void setSourceLocationInfo (CallContext context, String source_location_info) {
        this.source_location_info = source_location_info;
    }

    protected Map<String,Object> meta_data;

    public Map<String,Object> getMetaData (CallContext context) {
        return this.meta_data;
    }

    public void setMetaData (CallContext context, Map<String,Object> meta_data) {
        this.meta_data = meta_data;
    }

    public void addMetaData (CallContext context, String name, Object data) {
        if (this.meta_data == null) {
            this.meta_data = new HashMap<String,Object>();
        }
        this.meta_data.put(name, data);
    }

    protected Vector<String[]> pre_conditions;

    public Vector<String[]> getPreConditions (CallContext context) {
        return this.pre_conditions;
    }

    public void setPreConditions (CallContext context, Vector<String[]> pre_conditions) {
        this.pre_conditions = pre_conditions;
    }

    public void addPreCondition (CallContext context, String code, String pass, String source_location_info) {
        if (this.pre_conditions == null) {
            this.pre_conditions = new Vector<String[]>();
        }
        String[] codepass = { code, pass, source_location_info };
        this.pre_conditions.add(codepass);
    }

    protected Vector<String[]> post_conditions;

    public Vector<String[]> getPostConditions (CallContext context) {
        return this.post_conditions;
    }

    public void setPostConditions (CallContext context, Vector<String[]> post_conditions) {
        this.post_conditions = post_conditions;
    }

    public void addPostCondition (CallContext context, String code, String pass, String source_location_info) {
        if (this.post_conditions == null) {
            this.post_conditions = new Vector<String[]>();
        }
        String[] codepass = { code, pass, source_location_info };
        this.post_conditions.add(codepass);
    }

    protected Vector<String[]> pre_build_scripts;

    public Vector<String[]> getPreBuildScripts (CallContext context) {
        return this.pre_build_scripts;
    }

    public void setPreBuildScripts (CallContext context, Vector<String[]> pre_build_scripts) {
        this.pre_build_scripts = pre_build_scripts;
    }

    public void addPreBuildScript (CallContext context, String code, String pass, String source_location_info) {
        if (this.pre_build_scripts == null) {
            this.pre_build_scripts = new Vector<String[]>();
        }
        String[] codepass = { code, pass, source_location_info };
        this.pre_build_scripts.add(codepass);
    }

    public void addPreBuildMessage (CallContext context, String code, String pass, String source_location_info) {
        String quote = (code != null && code.startsWith("\"") ? "" : "\"");
        String message_code = "java.lang.System.err.println(" + quote + code + quote + ")";
        addPreBuildScript(context, message_code, pass, source_location_info);
    }

    public void addPreBuildDump (CallContext context, String code, String pass, String source_location_info) {
        String dump_code = "java.lang.System.err.println(\"=========================================================\\n" + code + "\\n=========================================================\\n\"+Packages.com.sphenon.basics.debug.RuntimeStepContext.get(context).getStackDump(context)+\"\\n=========================================================\")";
        addPreBuildScript(context, dump_code, pass, source_location_info);
    }

    protected Vector<String[]> post_build_scripts;

    public Vector<String[]> getPostBuildScripts (CallContext context) {
        return this.post_build_scripts;
    }

    public void setPostBuildScripts (CallContext context, Vector<String[]> post_build_scripts) {
        this.post_build_scripts = post_build_scripts;
    }

    public void addPostBuildScript (CallContext context, String code, String pass, String source_location_info) {
        if (this.post_build_scripts == null) {
            this.post_build_scripts = new Vector<String[]>();
        }
        String[] codepass = { code, pass, source_location_info };
        this.post_build_scripts.add(codepass);
    }

    public void addPostBuildMessage (CallContext context, String code, String pass, String source_location_info) {
        String quote = (code != null && code.startsWith("\"") ? "" : "\"");
        String message_code = "java.lang.System.err.println(" + quote + code + quote + ")";
        addPostBuildScript(context, message_code, pass, source_location_info);
    }

    public void addPostBuildDump (CallContext context, String code, String pass, String source_location_info) {
        String dump_code = "java.lang.System.err.println(\"=========================================================\\n" + code + "\\n=========================================================\\n\"+Packages.com.sphenon.basics.debug.RuntimeStepContext.get(context).getStackDump(context)+\"\\n=========================================================\")";
        addPostBuildScript(context, dump_code, pass, source_location_info);
    }
}
