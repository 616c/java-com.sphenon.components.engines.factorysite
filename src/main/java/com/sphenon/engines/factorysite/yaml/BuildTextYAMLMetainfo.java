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

import java.util.Map;
import java.util.List;

public class BuildTextYAMLMetainfo {

    public BuildTextYAMLMetainfo () {
    }

    public BuildTextYAMLMetainfo (String name) {
        this.name = name;
    }

    public BuildTextYAMLMetainfo (String name, String oid) {
        this.name = name;
        this.oid = oid;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // possible additional subclasses:
    //
    // NameSpace
    // 
    // OCPId
    // 
    // OptionalParameter
    // OptionalIdRef
    // AssignTo
    // Out
    // 
    // TypeCheck
    // ArgumentCheck
    // 
    // Singleton
    // DynamicParameters
    // Listener
    // Content
    // ComponentType
    // Alias
    // Catch
    // 
    // Signature
    // Base
    // Polymorphic
    // Override
    // Evaluator
    // Define
    // If
    // ForEach
    // 
    // AppliesTo
    // Pass
    // Switch
    // 
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // not used with yaml:
    // 
    // Meta
    // Complex
    // Code
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /* In lists, if there is metainfo needed, the value is wrapped in a
       BuildTextYAMLMetainfo, while the actual value is wrapped within
       this metainfo in this field named 'Value' */
    protected String value;

    public String getValue () {
        return this.value;
    }

    public void setValue (String value) {
        this.value = value;
    }

    protected String name;

    public String getName () {
        return this.name;
    }

    public void setName (String name) {
        this.name = name;
    }

    protected String name_expression;

    public String getNameExpression () {
        return this.name_expression;
    }

    public void setNameExpression (String name_expression) {
        this.name_expression = name_expression;
    }

    protected String oid;

    public String getOId () {
        return this.oid;
    }

    public void setOId (String oid) {
        this.oid = oid;
    }

    protected String j_class;

    public String getJClass () {
        return this.j_class;
    }

    public void setJClass (String j_class) {
        this.j_class = j_class;
    }

    protected String factory;

    public String getFactory () {
        return this.factory;
    }

    public void setFactory (String factory) {
        this.factory = factory;
    }

    protected String retriever;

    public String getRetriever () {
        return this.retriever;
    }

    public void setRetriever (String retriever) {
        this.retriever = retriever;
    }

    protected String method;

    public String getMethod () {
        return this.method;
    }

    public void setMethod (String method) {
        this.method = method;
    }

    protected String name_space;

    public String getNameSpace () {
        return this.name_space;
    }

    public void setNameSpace (String name_space) {
        this.name_space = name_space;
    }

    protected String ocp_id;

    public String getOCPId () {
        return this.ocp_id;
    }

    public void setOCPId (String ocp_id) {
        this.ocp_id = ocp_id;
    }

    protected String optional_parameter;

    public String getOptionalParameter () {
        return this.optional_parameter;
    }

    public void setOptionalParameter (String optional_parameter) {
        this.optional_parameter = optional_parameter;
    }

    protected String optional_id_ref;

    public String getOptionalIdRef () {
        return this.optional_id_ref;
    }

    public void setOptionalIdRef (String optional_id_ref) {
        this.optional_id_ref = optional_id_ref;
    }

    protected String assign_to;

    public String getAssignTo () {
        return this.assign_to;
    }

    public void setAssignTo (String assign_to) {
        this.assign_to = assign_to;
    }

    protected String out;

    public String getOut () {
        return this.out;
    }

    public void setOut (String out) {
        this.out = out;
    }

    protected String type_check;

    public String getTypeCheck () {
        return this.type_check;
    }

    public void setTypeCheck (String type_check) {
        this.type_check = type_check;
    }

    protected String argument_check;

    public String getArgumentCheck () {
        return this.argument_check;
    }

    public void setArgumentCheck (String argument_check) {
        this.argument_check = argument_check;
    }

    protected String singleton;

    public String getSingleton () {
        return this.singleton;
    }

    public void setSingleton (String singleton) {
        this.singleton = singleton;
    }

    protected String dynamic_parameters;

    public String getDynamicParameters () {
        return this.dynamic_parameters;
    }

    public void setDynamicParameters (String dynamic_parameters) {
        this.dynamic_parameters = dynamic_parameters;
    }

    protected String listener;

    public String getListener () {
        return this.listener;
    }

    public void setListener (String listener) {
        this.listener = listener;
    }

    protected String content;

    public String getContent () {
        return this.content;
    }

    public void setContent (String content) {
        this.content = content;
    }

    protected String component_type;

    public String getComponentType () {
        return this.component_type;
    }

    public void setComponentType (String component_type) {
        this.component_type = component_type;
    }

    protected String alias;

    public String getAlias () {
        return this.alias;
    }

    public void setAlias (String alias) {
        this.alias = alias;
    }

    protected String j_catch;

    public String getCatch () {
        return this.j_catch;
    }

    public void setCatch (String j_catch) {
        this.j_catch = j_catch;
    }

    protected String signature;

    public String getSignature () {
        return this.signature;
    }

    public void setSignature (String signature) {
        this.signature = signature;
    }

    protected String base;

    public String getBase () {
        return this.base;
    }

    public void setBase (String base) {
        this.base = base;
    }

    protected String polymorphic;

    public String getPolymorphic () {
        return this.polymorphic;
    }

    public void setPolymorphic (String polymorphic) {
        this.polymorphic = polymorphic;
    }

    protected String override;

    public String getOverride () {
        return this.override;
    }

    public void setOverride (String override) {
        this.override = override;
    }

    protected String evaluator;

    public String getEvaluator () {
        return this.evaluator;
    }

    public void setEvaluator (String evaluator) {
        this.evaluator = evaluator;
    }

    protected String define;

    public String getDefine () {
        return this.define;
    }

    public void setDefine (String define) {
        this.define = define;
    }

    protected String j_if;

    public String getIf () {
        return this.j_if;
    }

    public void setIf (String j_if) {
        this.j_if = j_if;
    }

    protected String for_each;

    public String getForEach () {
        return this.for_each;
    }

    public void setForEach (String for_each) {
        this.for_each = for_each;
    }

    protected String applies_to;

    public String getAppliesTo () {
        return this.applies_to;
    }

    public void setAppliesTo (String applies_to) {
        this.applies_to = applies_to;
    }

    protected String pass;

    public String getPass () {
        return this.pass;
    }

    public void setPass (String pass) {
        this.pass = pass;
    }

    protected String j_switch;

    public String getSwitch () {
        return this.j_switch;
    }

    public void setSwitch (String j_switch) {
        this.j_switch = j_switch;
    }

    protected Map<String,Object> meta_data;

    public Map<String,Object> getMetaData (CallContext context) {
        return this.meta_data;
    }

    public void setMetaData (CallContext context, Map<String,Object> meta_data) {
        this.meta_data = meta_data;
    }

    protected List<String> pre_conditions;

    public List<String> getPreConditions (CallContext context) {
        return this.pre_conditions;
    }

    public void setPreConditions (CallContext context, List<String> pre_conditions) {
        this.pre_conditions = pre_conditions;
    }

    protected List<String> post_conditions;

    public List<String> getPostConditions (CallContext context) {
        return this.post_conditions;
    }

    public void setPostConditions (CallContext context, List<String> post_conditions) {
        this.post_conditions = post_conditions;
    }

    protected List<String> pre_build_scripts;

    public List<String> getPreBuildScripts (CallContext context) {
        return this.pre_build_scripts;
    }

    public void setPreBuildScripts (CallContext context, List<String> pre_build_scripts) {
        this.pre_build_scripts = pre_build_scripts;
    }

    protected List<String> post_build_scripts;

    public List<String> getPostBuildScripts (CallContext context) {
        return this.post_build_scripts;
    }

    public void setPostBuildScripts (CallContext context, List<String> post_build_scripts) {
        this.post_build_scripts = post_build_scripts;
    }

    protected List<String> pre_build_messages;

    public List<String> getPreBuildMessages (CallContext context) {
        return this.pre_build_messages;
    }

    public void setPreBuildMessages (CallContext context, List<String> pre_build_messages) {
        this.pre_build_messages = pre_build_messages;
    }

    protected List<String> post_build_messages;

    public List<String> getPostBuildMessages (CallContext context) {
        return this.post_build_messages;
    }

    public void setPostBuildMessages (CallContext context, List<String> post_build_messages) {
        this.post_build_messages = post_build_messages;
    }

    protected List<String> pre_build_dumps;

    public List<String> getPreBuildDumps (CallContext context) {
        return this.pre_build_dumps;
    }

    public void setPreBuildDumps (CallContext context, List<String> pre_build_dumps) {
        this.pre_build_dumps = pre_build_dumps;
    }

    protected List<String> post_build_dumps;

    public List<String> getPostBuildDumps (CallContext context) {
        return this.post_build_dumps;
    }

    public void setPostBuildDumps (CallContext context, List<String> post_build_dumps) {
        this.post_build_dumps = post_build_dumps;
    }
}
