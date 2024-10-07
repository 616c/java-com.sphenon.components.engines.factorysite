package com.sphenon.engines.factorysite.tocp;

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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.factory.returncodes.*;

import com.sphenon.engines.factorysite.*;

import java.util.Vector;
import java.util.Map;
import java.io.StringReader;

public class TOCPASTNode {

    static public TOCPASTNode parseTOCP(CallContext context, String tocp_code) {
        try {
            StringReader sr = new StringReader(tocp_code);
            TOCPParser tocpp = new TOCPParser(context, sr);
            return tocpp.TOCP(context);
        } catch (ParseException pe) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "Invalid TOCP '%(tocp)'", "tocp", tocp_code);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public TOCPASTNode(CallContext context) {
    }

    protected Map<String,String> argument_mapping;

    public Map<String,String> getArgumentMapping (CallContext context) {
        return this.argument_mapping;
    }

    public void setArgumentMapping (CallContext context, Map<String,String> argument_mapping) {
        this.argument_mapping = argument_mapping;
    }

    protected String value;

    public String getValue (CallContext context) {
        return this.value;
    }

    public void setValue (CallContext context, String value) {
        this.value = value;
    }

    protected String j_class;

    public String getClass (CallContext context) {
        return this.j_class;
    }

    public void setClass (CallContext context, String j_class) {
        this.j_class = j_class;
    }

    protected String oid;

    public String getOId (CallContext context) {
        return this.oid;
    }

    public void setOId (CallContext context, String oid) {
        this.oid = oid;
    }

    protected String factory;

    public String getFactory (CallContext context) {
        return this.factory;
    }

    public void setFactory (CallContext context, String factory) {
        this.factory = factory;
    }

    protected String retriever;

    public String getRetriever (CallContext context) {
        return this.retriever;
    }

    public void setRetriever (CallContext context, String retriever) {
        this.retriever = retriever;
    }

    protected String method;

    public String getMethod (CallContext context) {
        return this.method;
    }

    public void setMethod (CallContext context, String method) {
        this.method = method;
    }

    protected String component_type;

    public String getComponentType (CallContext context) {
        return this.component_type;
    }

    public void setComponentType (CallContext context, String component_type) {
        this.component_type = component_type;
    }

    protected String namespace;

    public String getNameSpace (CallContext context) {
        return this.namespace;
    }

    public void setNameSpace (CallContext context, String namespace) {
        this.namespace = namespace;
    }

    protected String ocp_id;

    public String getOCPId (CallContext context) {
        return this.ocp_id;
    }

    public void setOCPId (CallContext context, String ocp_id) {
        this.ocp_id = ocp_id;
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

    protected String name;

    public String getName (CallContext context) {
        return this.name;
    }

    public void setName (CallContext context, String name) {
        this.name = name;
    }

    protected String assign_to;

    public String getAssignTo (CallContext context) {
        return this.assign_to;
    }

    public void setAssignTo (CallContext context, String assign_to) {
        this.assign_to = assign_to;
    }

    protected String out;

    public String getOut (CallContext context) {
        return this.out;
    }

    public void setOut (CallContext context, String out) {
        this.out = out;
    }

    protected String parameter;

    public String getParameter (CallContext context) {
        return this.parameter;
    }

    public void setParameter (CallContext context, String parameter) {
        this.parameter = parameter;
    }

    protected String optional_parameter;

    public String getOptionalParameter (CallContext context) {
        return this.optional_parameter;
    }

    public void setOptionalParameter (CallContext context, String optional_parameter) {
        this.optional_parameter = optional_parameter;
    }

    protected String id_ref;

    public String getIdRef (CallContext context) {
        return this.id_ref;
    }

    public void setIdRef (CallContext context, String id_ref) {
        this.id_ref = id_ref;
    }

    protected String optional_id_ref;

    public String getOptionalIdRef (CallContext context) {
        return this.optional_id_ref;
    }

    public void setOptionalIdRef (CallContext context, String optional_id_ref) {
        this.optional_id_ref = optional_id_ref;
    }

    protected String oid_ref;

    public String getOIdRef (CallContext context) {
        return this.oid_ref;
    }

    public void setOIdRef (CallContext context, String oid_ref) {
        this.oid_ref = oid_ref;
    }

    protected String type_check;

    public String getTypeCheck (CallContext context) {
        return this.type_check;
    }

    public void setTypeCheck (CallContext context, String type_check) {
        this.type_check = type_check;
    }

    protected String argument_check;

    public String getArgumentCheck (CallContext context) {
        return this.argument_check;
    }

    public void setArgumentCheck (CallContext context, String argument_check) {
        this.argument_check = argument_check;
    }

    protected String singleton;

    public String getSingleton (CallContext context) {
        return this.singleton;
    }

    public void setSingleton (CallContext context, String singleton) {
        this.singleton = singleton;
    }

    protected String dynamic_parameters;

    public String getDynamicParameters (CallContext context) {
        return this.dynamic_parameters;
    }

    public void setDynamicParameters (CallContext context, String dynamic_parameters) {
        this.dynamic_parameters = dynamic_parameters;
    }

    protected String listener;

    public String getListener (CallContext context) {
        return this.listener;
    }

    public void setListener (CallContext context, String listener) {
        this.listener = listener;
    }

    protected String alias;

    public String getAlias (CallContext context) {
        return this.alias;
    }

    public void setAlias (CallContext context, String alias) {
        this.alias = alias;
    }

    protected String j_if;

    public String getIf (CallContext context) {
        return this.j_if;
    }

    public void setIf (CallContext context, String j_if) {
        this.j_if = j_if;
    }

    protected String for_each;

    public String getForEach (CallContext context) {
        return this.for_each;
    }

    public void setForEach (CallContext context, String for_each) {
        this.for_each = for_each;
    }

    protected String j_switch;

    public String getSwitch (CallContext context) {
        return this.j_switch;
    }

    public void setSwitch (CallContext context, String j_switch) {
        this.j_switch = j_switch;
    }

    protected String content;

    public String getContent (CallContext context) {
        return this.content;
    }

    public void setContent (CallContext context, String content) {
        this.content = content;
    }

    protected String complex;

    public String getComplex (CallContext context) {
        return this.complex;
    }

    public void setComplex (CallContext context, String complex) {
        this.complex = complex;
    }

    protected String expression;

    public String getExpression (CallContext context) {
        return this.expression;
    }

    public void setExpression (CallContext context, String expression) {
        this.expression = expression;
    }

    protected String expression_text;

    public String getExpressionText (CallContext context) {
        return this.expression_text;
    }

    public void setExpressionText (CallContext context, String expression_text) {
        this.expression_text = expression_text;
    }

    protected String expression_value;

    public String getExpressionValue (CallContext context) {
        return this.expression_value;
    }

    public void setExpressionValue (CallContext context, String expression_value) {
        this.expression_value = expression_value;
    }

    protected String locator;

    public String getLocator (CallContext context) {
        return this.locator;
    }

    public void setLocator (CallContext context, String locator) {
        this.locator = locator;
    }

    protected String locator_base;

    public String getLocatorBase (CallContext context) {
        return this.locator_base;
    }

    public void setLocatorBase (CallContext context, String locator_base) {
        this.locator_base = locator_base;
    }

    protected String locator_base_oid_ref;

    public String getLocatorBaseOIdRef (CallContext context) {
        return this.locator_base_oid_ref;
    }

    public void setLocatorBaseOIdRef (CallContext context, String locator_base_oid_ref) {
        this.locator_base_oid_ref = locator_base_oid_ref;
    }

    protected String locator_base_parameter;

    public String getLocatorBaseParameter (CallContext context) {
        return this.locator_base_parameter;
    }

    public void setLocatorBaseParameter (CallContext context, String locator_base_parameter) {
        this.locator_base_parameter = locator_base_parameter;
    }

    protected String j_null;

    public String getNull (CallContext context) {
        return this.j_null;
    }

    public void setNull (CallContext context, String j_null) {
        this.j_null = j_null;
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

    protected String override;

    public String getOverride (CallContext context) {
        return this.override;
    }

    public void setOverride (CallContext context, String override) {
        this.override = override;
    }

    protected String j_catch;

    public String getCatch (CallContext context) {
        return this.j_catch;
    }

    public void setCatch (CallContext context, String j_catch) {
        this.j_catch = j_catch;
    }

    protected String applies_to;

    public String getAppliesTo (CallContext context) {
        return this.applies_to;
    }

    public void setAppliesTo (CallContext context, String applies_to) {
        this.applies_to = applies_to;
    }

    protected String pass;

    public String getPass (CallContext context) {
        return this.pass;
    }

    public void setPass (CallContext context, String pass) {
        this.pass = pass;
    }

    protected String meta;

    public String getMeta (CallContext context) {
        return this.meta;
    }

    public void setMeta (CallContext context, String meta) {
        this.meta = meta;
    }

    protected String code;

    public String getCode (CallContext context) {
        return this.code;
    }

    public void setCode (CallContext context, String code) {
        this.code = code;
    }

    protected Vector<TOCPASTNode> parts;

    public Vector<TOCPASTNode> getParts (CallContext context) {
        return this.parts;
    }

    public void setParts (CallContext context, Vector<TOCPASTNode> parts) {
        this.parts = parts;
    }

    public void addPart (CallContext context, TOCPASTNode part) {
        if (this.parts == null) {
            this.parts = new Vector<TOCPASTNode>();
        }
        int pos;
        if (part.getName(context) != null && (pos = part.getName(context).indexOf('.')) != -1) {
            TOCPASTNode container = null;
            String first_name = part.getName(context).substring(0, pos);
            for (TOCPASTNode p : this.parts) {
                if (p.getName(context) != null && p.getName(context).equals(first_name)) {
                    container = p;
                    break;
                }
            }
            if (container == null) {
                container = new TOCPASTNode(context);
                container.setName(context, first_name);
                this.parts.add(container);
            }
            part.setName(context, part.getName(context).substring(pos + 1));
            container.addPart(context, part);            
        } else {
            this.parts.add(part);
        }
    }

    public void dumpToXML(CallContext context) {
        StringBuilder sb = dumpToXML(context, "", null);
        System.err.println(sb.toString());
    }

    public StringBuilder dumpToXML(CallContext context, String indent, StringBuilder sb) {
        if (this.name != null) {
            if (sb ==null) { sb = new StringBuilder(); }
            sb.append(indent);
            sb.append("<");
            sb.append(this.name);
            if (this.j_class != null)                           { sb.append(" CLASS=\"").append(this.j_class).append("\""); }
            if (this.oid != null)                               { sb.append(" OID=\"").append(this.oid).append("\""); }
            if (this.factory != null)                           { sb.append(" FACTORY=\"").append(this.factory).append("\""); }
            if (this.retriever != null)                         { sb.append(" RETRIEVER=\"").append(this.retriever).append("\""); }
            if (this.method != null)                            { sb.append(" METHOD=\"").append(this.method).append("\""); }
            if (this.component_type != null)                    { sb.append(" COMPONENTTYPE=\"").append(this.component_type).append("\""); }
            if (this.namespace != null)                         { sb.append(" xmlns=\"").append(this.namespace).append("\""); }
            if (this.ocp_id != null)                            { sb.append(" OCPID=\"").append(this.ocp_id).append("\""); }
            if (this.signature != null)                         { sb.append(" SIGNATURE=\"").append(this.signature).append("\""); }
            if (this.define != null)                            { sb.append(" DEFINE=\"").append(this.define).append("\""); }
            if (this.evaluator != null)                         { sb.append(" EVALUATOR=\"").append(this.evaluator).append("\""); }
            if (this.assign_to != null)                         { sb.append(" ASSIGNTO=\"").append(this.assign_to).append("\""); }
            if (this.out != null)                               { sb.append(" OUT=\"").append(this.out).append("\""); }
            if (this.parameter != null)                         { sb.append(" PARAMETER=\"").append(this.parameter).append("\""); }
            if (this.optional_parameter != null)                { sb.append(" OPTIONALPARAMETER=\"").append(this.optional_parameter).append("\""); }
            if (this.id_ref != null)                            { sb.append(" IDREF=\"").append(this.id_ref).append("\""); }
            if (this.optional_id_ref != null)                   { sb.append(" OPTIONALIDREF=\"").append(this.optional_id_ref).append("\""); }
            if (this.oid_ref != null)                           { sb.append(" OIDREF=\"").append(this.oid_ref).append("\""); }
            if (this.type_check != null)                        { sb.append(" TYPECHECK=\"").append(this.type_check).append("\""); }
            if (this.argument_check != null)                    { sb.append(" ARGUMENTCHECK=\"").append(this.argument_check).append("\""); }
            if (this.singleton != null)                         { sb.append(" SINGLETON=\"").append(this.singleton).append("\""); }
            if (this.dynamic_parameters != null)                { sb.append(" DYNAMICPARAMETERS=\"").append(this.dynamic_parameters).append("\""); }
            if (this.listener != null)                          { sb.append(" LISTENER=\"").append(this.listener).append("\""); }
            if (this.alias != null)                             { sb.append(" ALIAS=\"").append(this.alias).append("\""); }
            if (this.j_if != null)                              { sb.append(" IF=\"").append(this.j_if).append("\""); }
            if (this.for_each != null)                          { sb.append(" FOREACH=\"").append(this.for_each).append("\""); }
            if (this.j_switch != null)                          { sb.append(" SWITCH=\"").append(this.j_switch).append("\""); }
            if (this.content != null)                           { sb.append(" CONTENT=\"").append(this.content).append("\""); }
            if (this.complex != null)                           { sb.append(" COMPLEX=\"").append(this.complex).append("\""); }
            if (this.expression != null)                        { sb.append(" EXPRESSION=\"").append(this.expression).append("\""); }
            if (this.expression_text != null)                   { sb.append(" EXPRESSIONTEXT=\"").append(this.expression_text).append("\""); }
            if (this.expression_value != null)                  { sb.append(" EXPRESSIONVALUE=\"").append(this.expression_value).append("\""); }
            if (this.locator != null)                           { sb.append(" LOCATOR=\"").append(this.locator).append("\""); }
            if (this.locator_base != null)                      { sb.append(" LOCATORBASE=\"").append(this.locator_base).append("\""); }
            if (this.locator_base_oid_ref != null)              { sb.append(" LOCATORBASEOIDREF=\"").append(this.locator_base_oid_ref).append("\""); }
            if (this.locator_base_parameter != null)            { sb.append(" LOCATORBASEPARAMETER=\"").append(this.locator_base_parameter).append("\""); }
            if (this.j_null != null)                            { sb.append(" NULL=\"").append(this.j_null).append("\""); }
            if (this.base != null)                              { sb.append(" BASE=\"").append(this.base).append("\""); }
            if (this.polymorphic != null)                       { sb.append(" POLYMORPHIC=\"").append(this.polymorphic).append("\""); }
            if (this.override != null)                          { sb.append(" OVERRIDE=\"").append(this.override).append("\""); }
            if (this.j_catch != null)                           { sb.append(" CATCH=\"").append(this.j_catch).append("\""); }
            if (this.applies_to != null)                        { sb.append(" APPLIESTO=\"").append(this.applies_to).append("\""); }
            if (this.pass != null)                              { sb.append(" PASS=\"").append(this.pass).append("\""); }
            if (this.meta != null)                              { sb.append(" META=\"").append(this.meta).append("\""); }
            if (this.code != null)                              { sb.append(" CODE=\"").append(this.code).append("\""); }
            if ((this.parts == null || this.parts.size() == 0) && this.value == null) {
                sb.append("/");
            }
            sb.append(">");
        }
        if (this.value != null) {
            if (sb ==null) { sb = new StringBuilder(); }
            sb.append(this.value.replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;"));
        }
        boolean no_names = true;
        if (this.parts != null && this.parts.size() != 0) {
            for (TOCPASTNode tan : this.parts) {
                if (tan.getName(context) != null && tan.getName(context).length() != 0) {
                    no_names = false;
                }
            }
            if ( ! no_names) {
                if (sb ==null) { sb = new StringBuilder(); }
                sb.append("\n");
            }
            for (TOCPASTNode tan : this.parts) {
                sb = tan.dumpToXML(context, indent+"  ", sb);
            }
        }
        if ((this.parts != null && this.parts.size() != 0) || this.value != null) {
            if (this.name != null) {
                if (sb ==null) { sb = new StringBuilder(); }
                if (this.parts != null && this.parts.size() != 0 && ! no_names) {
                    sb.append(indent);
                }
                sb.append("</");
                sb.append(this.name);
                sb.append(">\n");
            }
        }
        return sb;
    }

    public void dumpToASCII(CallContext context) {
        StringBuilder sb = dumpToASCII(context, "", null);
        System.err.println(sb.toString());
    }

    public StringBuilder dumpToASCII(CallContext context, String indent, StringBuilder sb) {
        if (sb ==null) { sb = new StringBuilder(); }
        sb.append(indent);
        sb.append("*\n");
        if (this.name != null || this.j_class != null) {
            if (sb ==null) { sb = new StringBuilder(); }
            sb.append(indent);
            if (this.name != null)                              { sb.append(this.name).append(" "); }
            if (this.j_class != null)                           { sb.append("<").append(this.j_class).append(">"); }
            if (this.oid != null)                               { sb.append(" #").append(this.oid); }
            if (this.factory != null)                           { sb.append(" +").append(this.factory); }
            if (this.retriever != null)                         { sb.append(" ?").append(this.retriever); }
            if (this.method != null)                            { sb.append(" ????? ").append(this.method); }
            if (this.component_type != null)                    { sb.append(" ????? ").append(this.component_type); }
            if (this.namespace != null)                         { sb.append(" ~").append(this.namespace); }
            if (this.ocp_id != null)                            { sb.append(" ????? ").append(this.ocp_id); }
            if (this.signature != null)                         { sb.append(" ????? ").append(this.signature); }
            if (this.define != null)                            { sb.append(" ????? ").append(this.define); }
            if (this.evaluator != null)                         { sb.append(" ????? ").append(this.evaluator); }
            if (this.assign_to != null)                         { sb.append(" ????? ").append(this.assign_to); }
            if (this.out != null)                               { sb.append(" ????? ").append(this.out); }
            if (this.parameter != null)                         { sb.append(" ^").append(this.parameter); }
            if (this.optional_parameter != null)                { sb.append(" ????? ").append(this.optional_parameter); }
            if (this.id_ref != null)                            { sb.append(" %").append(this.id_ref); }
            if (this.optional_id_ref != null)                   { sb.append(" ????? ").append(this.optional_id_ref); }
            if (this.oid_ref != null)                           { sb.append(" ????? ").append(this.oid_ref); }
            if (this.type_check != null
                && this.type_check.equals("allow_dynamic"))     { sb.append(" *"); }
            if (this.argument_check != null)                    { sb.append(" ????? ").append(this.argument_check); }
            if (this.singleton != null)                         { sb.append(" ????? ").append(this.singleton); }
            if (this.dynamic_parameters != null)                { sb.append(" ????? ").append(this.dynamic_parameters); }
            if (this.listener != null)                          { sb.append(" ????? ").append(this.listener); }
            if (this.alias != null)                             { sb.append(" ????? ").append(this.alias); }
            if (this.j_if != null)                              { sb.append(" ????? ").append(this.j_if); }
            if (this.for_each != null)                          { sb.append(" ????? ").append(this.for_each); }
            if (this.j_switch != null)                          { sb.append(" ????? ").append(this.j_switch); }
            if (this.content != null)                           { sb.append(" ????? ").append(this.content); }
            if (this.complex != null)                           { sb.append(" ????? ").append(this.complex); }
            if (this.expression != null)                        { sb.append(" ????? ").append(this.expression); }
            if (this.expression_text != null)                   { sb.append(" ????? ").append(this.expression_text); }
            if (this.expression_value != null)                  { sb.append(" ????? ").append(this.expression_value); }
            if (this.locator != null)                           { sb.append(" !").append(this.locator); }
            if (this.locator != null)                           { sb.append(" ????? ").append(this.locator); }
            if (this.locator_base != null)                      { sb.append(" ????? ").append(this.locator_base); }
            if (this.locator_base_oid_ref != null)              { sb.append(" ????? ").append(this.locator_base_oid_ref); }
            if (this.locator_base_parameter != null)            { sb.append(" ????? ").append(this.locator_base_parameter); }
            if (this.j_null != null
                && this.j_null.equals("true"))                  { sb.append(" -"); }
            if (this.base != null)                              { sb.append(" ????? ").append(this.base); }
            if (this.polymorphic != null)                       { sb.append(" ????? ").append(this.polymorphic); }
            if (this.override != null)                          { sb.append(" ????? ").append(this.override); }
            if (this.j_catch != null)                           { sb.append(" ????? ").append(this.j_catch); }
            if (this.applies_to != null)                        { sb.append(" ????? ").append(this.applies_to); }
            if (this.pass != null)                              { sb.append(" ????? ").append(this.pass); }
            if (this.meta != null)                              { sb.append(" ????? ").append(this.meta); }
            if (this.code != null)                              { sb.append(" ????? ").append(this.code); }
            sb.append("\n");
        }
        if (this.value != null) {
            if (sb ==null) { sb = new StringBuilder(); }
            sb.append(indent);
            sb.append("=> ");
            sb.append(this.value);
        }
        if (this.parts != null && this.parts.size() != 0) {
            if (sb ==null) { sb = new StringBuilder(); }
            sb.append(indent);
            sb.append("=> {\n");
            for (TOCPASTNode tan : this.parts) {
                sb = tan.dumpToASCII(context, indent+"  ", sb);
            }
            sb.append(indent);
            sb.append("}\n");
        }
        return sb;
    }
}
