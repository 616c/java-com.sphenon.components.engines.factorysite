package com.sphenon.engines.factorysite.tocp;

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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.tocp.*;

import java.util.Vector;

public class TOCPBuildText {

    static public Pair_BuildText_String_ create(CallContext context, TOCPASTNode tan) {
        return create(context, tan, 0, false);
    }

    static public Pair_BuildText_String_ create(CallContext context, TOCPASTNode tan, int depth) {
        return create(context, tan, depth, false);
    }

    static public Pair_BuildText_String_ create(CallContext context, TOCPASTNode tan, int depth, boolean already_within_optional) {
        BuildText bt = null;
        String  name                 = tan.getName(context);
        String  j_class              = tan.getClass(context);
        String  oid                  = tan.getOId(context);
        String  factory              = tan.getFactory(context);
        String  retriever            = tan.getRetriever(context);
        String  locator              = tan.getLocator(context);
        String  id_ref               = tan.getIdRef(context);
        String  oid_ref              = tan.getOIdRef(context);
        String  parameter            = tan.getParameter(context);
        boolean is_null              = (tan.getNull(context) != null && tan.getNull(context).equals("true") ? true : false);
        boolean is_switch            = (tan.getSwitch(context) != null && tan.getSwitch(context).equals("true") ? true : false);
        String  optional_parameter   = tan.getOptionalParameter(context);
        String  optional_idref       = tan.getOptionalIdRef(context);

        if (j_class == null)   { j_class = ""; }
        if (oid == null)       { oid = ""; }
        if (factory == null)   { factory = ""; }
        if (retriever == null) { retriever = ""; }
        if (locator == null)   { locator = ""; }
        if (id_ref == null)    { id_ref = ""; }
        if (oid_ref == null)   { oid_ref = ""; }
        if (parameter == null) { parameter = ""; }

        String  value                = tan.getValue(context);

        String  expression_text      = tan.getExpressionText(context);
        String  expression_value     = tan.getExpressionValue(context);
        if (expression_text  != null) { value = expression_text;  tan.setExpression(context, "text"); }
        if (expression_value != null) { value = expression_value; tan.setExpression(context, "value");}

        Vector<TOCPASTNode> parts = tan.getParts(context);

        boolean has_name    = name != null;
        boolean has_childs  = (parts != null && parts.size() != 0);
        int     nbrofchilds = (has_childs ? parts.size() : 0);
        boolean has_value   = value != null;

        boolean has_locator            = (locator.isEmpty() == false);
        boolean has_idref              = (id_ref.isEmpty() == false);
        boolean has_optional_idref     = (optional_idref != null && optional_idref.isEmpty() == false);
        boolean has_oidref             = (oid_ref.isEmpty() == false);
        boolean has_parameter          = (parameter.isEmpty() == false);
        boolean has_optional_parameter = (optional_parameter != null && optional_parameter.isEmpty() == false);

        if (has_name) {
            if ( ! already_within_optional && has_optional_parameter) {
                return new Pair_BuildText_String_
                           (context,
                            new BuildTextOptionalParameter_String
                                (context, oid, j_class,
                                 (BuildTextParameter_String) createSimple(context, name, j_class, oid, factory, retriever, locator, id_ref, oid_ref, optional_parameter, value, depth, tan).getItem1(context),
                                 create(context, tan, depth, true).getItem1(context),
                                 "<TOCPASTNode>"),
                            name);
            } else if ( ! already_within_optional && has_optional_idref) {
                return new Pair_BuildText_String_
                           (context,
                            new BuildTextOptionalRefById_String
                                (context, oid, j_class,
                                 (BuildTextRefById_String) createSimple(context, name, j_class, oid, factory, retriever, locator, optional_idref, oid_ref, parameter, value, depth, tan).getItem1(context),
                                 create(context, tan, depth, true).getItem1(context),
                                 "<TOCPASTNode>"),
                            name);
            } else if (is_switch) {
                return createSwitch(context, name, j_class, oid, factory, retriever, locator, id_ref, oid_ref, parameter, parts, depth, tan);
            } else if (is_null) {
                return createNull(context, name, tan);
            } else if (! has_value && ! has_locator && ! has_idref && ! has_oidref && ! has_parameter && ! has_childs) {
                return createComplex(context, name, j_class, oid, factory, retriever, locator, id_ref, oid_ref, parameter, parts, depth, tan);
            } else if (has_name && (has_value || has_locator || has_idref || has_oidref || has_parameter) && ! has_childs) {
                return createSimple(context, name, j_class, oid, factory, retriever, locator, id_ref, oid_ref, parameter, value, depth, tan);
            } else if (has_name && ! has_value && ! has_locator && ! has_idref && ! has_oidref && ! has_parameter && has_childs) {
                TOCPASTNode part0  = nbrofchilds == 1 ? parts.get(0) : null;
                String               part0_name  = null;
                String               part0_value = null;
                Vector<TOCPASTNode> part0_parts = null;
                if (part0 != null) {
                    part0_name  = part0.getName(context);
                    part0_value = part0.getValue(context);
                    part0_parts = part0.getParts(context);
                }
                if (    part0 != null
                     && part0_name == null
                     && (    (part0_value == null)
                          != (part0_parts == null || part0_parts.size() == 0)
                        )
                   ) {
                    if (part0_value != null) {
                        return createSimple(context, name, j_class, oid, factory, retriever, locator, id_ref, oid_ref, parameter, part0_value, depth, tan);
                    } else {
                        return createComplex(context, name, j_class, oid, factory, retriever, locator, id_ref, oid_ref, parameter, part0_parts, depth, tan);
                    }
                } else {
                    return createComplex(context, name, j_class, oid, factory, retriever, locator, id_ref, oid_ref, parameter, parts, depth, tan);
                }
            } else {
                CustomaryContext.create((Context)context).throwConfigurationError(context, "Cannot create BuildText from TOCPASTNode with value and childs");
                throw (ExceptionConfigurationError) null; // compiler insists
            }
        } else {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Cannot create BuildText from TOCPASTNode with no name");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    static public Pair_BuildText_String_ createSimple(CallContext context, String name, String j_class, String oid, String factory, String retriever, String locator, String id_ref, String oid_ref, String parameter, String value, int depth, TOCPASTNode tan) {
        BuildTextBaseImpl btbi;
        if (id_ref != null && id_ref.isEmpty() == false) {
            btbi = new BuildTextRefById_String(context, oid, "", j_class, id_ref, "<TOCPASTNode>");
        } else if (oid_ref != null && oid_ref.isEmpty() == false) {
            btbi = new BuildTextRef_String(context, oid, "", j_class, oid_ref, "<TOCPASTNode>");
        } else if (parameter != null && parameter.isEmpty() == false) {
            btbi = new BuildTextParameter_String(context, oid, "", j_class, parameter, "<TOCPASTNode>");
        } else if (locator != null && locator.isEmpty() == false) {
            retriever = "com.sphenon.basics.locating.retrievers.RetrieverByTextLocator";
            Vector_Pair_BuildText_String__long_ named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);

            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", "<TOCPASTNode>", new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "String", "", "", locator, "<TOCPASTNode>"), "")), "TextLocator"));
            named_items.append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", "<TOCPASTNode>", new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "String", "", "", "Property", "<TOCPASTNode>"), "")), "DefaultType"));

            String locator_base = null;
            if ((locator_base = tan.getLocatorBase(context)) != null && locator_base.length() != 0) {
                if (locator_base.charAt(0) == '#') {
                    named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRef_String (context, "", "", "", locator_base.substring(1), "<TOCPASTNode>"), "Base"));
                } else if (locator_base.charAt(0) == '$') {
                    named_items.append(context, new Pair_BuildText_String_(context, new BuildTextParameter_String (context, "", "", "", locator_base.substring(1), "<TOCPASTNode>"), "Base"));
                } else {
                    named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRefById_String (context, "", "", "", locator_base, "<TOCPASTNode>"), "Base"));
                }
            } else if ((locator_base = tan.getLocatorBaseOIdRef(context)) != null && locator_base.length() != 0) {
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextRef_String (context, "", "", "", locator_base, "<TOCPASTNode>"), "Base"));
            } else if ((locator_base = tan.getLocatorBaseParameter(context)) != null && locator_base.length() != 0) {
                named_items.append(context, new Pair_BuildText_String_(context, new BuildTextParameter_String (context, "", "", "", locator_base, "<TOCPASTNode>"), "Base"));
            }

            btbi = new BuildTextComplex_String(context, name, oid, "", j_class, factory, retriever, "<TOCPASTNode>", named_items);
        } else {
            String btsstype = "java.lang.String";
            String  expression = tan.getExpression(context);
            boolean has_value_expression = (expression != null && expression.equals("value"));
            if (has_value_expression) { btsstype = j_class; }
            BuildTextSimple_String btss = new BuildTextSimple_String(context, "", "", btsstype, "", "", value, "<TOCPASTNode>");
            if (expression != null) { btss.setIsExpression(context, true); }
            btbi = new BuildTextComplex_String(context, name, oid, "", j_class, factory, retriever, "<TOCPASTNode>", new Pair_BuildText_String_(context, btss, ""));
        }

        transferAttribtues(context, btbi, tan);

        return new Pair_BuildText_String_(context, btbi, name);
    }

    static protected void processParts(CallContext context, Vector<TOCPASTNode> parts, Vector_Pair_BuildText_String__long_ named_items, BuildTextBaseImpl container, String pass, int depth) {
        if (parts != null) {
            for (TOCPASTNode part : parts) {
                String code;
                String meta;
                String value;
                if (    (code  = part.getCode(context))  != null && code.isEmpty()  == false
                     && (value = part.getValue(context)) != null && value.isEmpty() == false) {
                    if (code.equals("PreCondition")) {
                        container.addPreCondition(context, value, pass, "<TOCPASTNode>");
                    } else if (code.equals("PostCondition")) {
                        container.addPostCondition(context, value, pass, "<TOCPASTNode>");
                    } else if (code.equals("PreBuildScript")) {
                        container.addPreBuildScript(context, value, pass, "<TOCPASTNode>");
                    } else if (code.equals("PostBuildScript")) {
                        container.addPostBuildScript(context, value, pass, "<TOCPASTNode>");
                    } else if (code.equals("PreBuildMessage")) {
                        container.addPreBuildMessage(context, value, pass, "<TOCPASTNode>");
                    } else if (code.equals("PostBuildMessage")) {
                        container.addPostBuildMessage(context, value, pass, "<TOCPASTNode>");
                    } else if (code.equals("PreBuildDump")) {
                        container.addPreBuildDump(context, value, pass, "<TOCPASTNode>");
                    } else if (code.equals("PostBuildDump")) {
                        container.addPostBuildDump(context, value, pass, "<TOCPASTNode>");
                    } else {
                        NotificationContext.sendCaution(context, "Unrecognized code fragment of type '%(type)' in OCP", "type", code);
                    }
                } else if (    (meta  = part.getMeta(context))  != null && meta.isEmpty()  == false
                            && (value = part.getValue(context)) != null && value.isEmpty() == false) {
                    container.addMetaData(context, meta, value);
                } else {
                    named_items.append(context, create(context, part, depth+1));
                }
            }
        }
    }

    static public Pair_BuildText_String_ createComplex(CallContext context, String name, String j_class, String oid, String factory, String retriever, String locator, String id_ref, String oid_ref, String parameter, Vector<TOCPASTNode> parts, int depth, TOCPASTNode tan) {
        Vector_Pair_BuildText_String__long_ named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);
        BuildTextComplex_String btcs = new BuildTextComplex_String(context, name, oid, "", j_class, factory, retriever, "<TOCPASTNode>", named_items);

        String pass = tan.getPass(context);
        if (pass == null || pass.length() == 0) { pass = "1"; }

        processParts(context, parts, named_items, btcs, pass, depth);

        String component_type = tan.getComponentType(context);
        if (component_type != null) { btcs.setComponentType(context, component_type); }

        transferAttribtues(context, btcs, tan);

        return new Pair_BuildText_String_(context, btcs, name);
    }

    static public Pair_BuildText_String_ createNull(CallContext context, String name, TOCPASTNode tan) {
        BuildTextBaseImpl btbi = new BuildTextNull_String(context);

        transferAttribtues(context, btbi, tan);

        return new Pair_BuildText_String_(context, btbi, name);
    }

    static public Pair_BuildText_String_ createSwitch(CallContext context, String name, String j_class, String oid, String factory, String retriever, String locator, String id_ref, String oid_ref, String parameter, Vector<TOCPASTNode> parts, int depth, TOCPASTNode tan) {
        BuildTextSwitch_String btss = new BuildTextSwitch_String(context, oid, "", j_class, factory, retriever, "<TOCPASTNode>");

        String pass = tan.getPass(context);
        if (pass == null || pass.length() == 0) { pass = "1"; }

        processParts(context, parts, btss.getCases(context), btss, pass, depth);
        String method = tan.getMethod(context);
        for (Pair_BuildText_String_ pbts : btss.getCases(context).getIterable(context)) {
            BuildText bt = pbts.getItem1(context);
            if (bt.getTypeName(context) == null || bt.getTypeName(context).length() == 0) {
                bt.setTypeName(context, j_class);
            }
            if (bt.getFactoryName(context) == null || bt.getFactoryName(context).length() == 0) {
                bt.setFactoryName(context, factory);
            }
            if (bt.getRetrieverName(context) == null || bt.getRetrieverName(context).length() == 0) {
                bt.setRetrieverName(context, retriever);
            }
            if (bt.getMethodName(context) == null || bt.getMethodName(context).length() == 0) {
                bt.setMethodName(context, method);
            }
        }

        transferAttribtues(context, btss, tan);

        return new Pair_BuildText_String_(context, btss, name);
    }

    static public void transferAttribtues(CallContext context, BuildTextBaseImpl btbi, TOCPASTNode tan) {
        // getValue
        // getClass
        // getOId
        // getFactory
        // getRetriever
        String method = tan.getMethod(context);
        if (method != null) { btbi.setMethodName(context, method); }
        String name_space = tan.getNameSpace(context);
        if (name_space != null) { btbi.setNameSpace(context, name_space); }
        String ocp_id = tan.getOCPId(context);
        if (ocp_id != null) { btbi.setOCPId(context, ocp_id); }
        String signature = tan.getSignature(context);
        if (signature != null) { btbi.setSignature(context, signature); }
        String define = tan.getDefine(context);
        if (define != null) { btbi.setDefine(context, define); }
        String evaluator = tan.getEvaluator(context);
        if (evaluator != null) { btbi.setEvaluator(context, evaluator); }
        // getName
        String assign_to = tan.getAssignTo(context);
        if (assign_to != null) { btbi.setAssignTo(context, assign_to); }
        // getOut - implementation looks strange in BuildTextComplexXML.java
        // getParameter
        // getIdRef
        // getOptionalParameter
        // getOptionalIdRef
        // getSwitch
        // getOIdRef
        String type_check = tan.getTypeCheck(context);
        if (type_check != null && type_check.equals("allow_dynamic")) { btbi.setAllowDynamicTypeCheck(context, true); }
        String argument_check = tan.getArgumentCheck(context);
        if (argument_check != null && argument_check.equals("allow_missing")) { btbi.setAllowMissingArguments(context, true); }
        String singleton = tan.getSingleton(context);
        if (singleton != null && singleton.equals("true")) { btbi.setIsSingleton(context, true); }
        String dynamic_parameters = tan.getDynamicParameters(context);
        if (dynamic_parameters != null && dynamic_parameters.equals("true")) { btbi.setHaveDynamicParameters(context, true); }
        String listener = tan.getListener(context);
        if (listener != null) { btbi.setListener(context, listener); }
        String alias = tan.getAlias(context);
        if (alias != null) { btbi.setAlias(context, alias); }
        String j_if = tan.getIf(context);
        if (j_if != null) { btbi.setIfExpression(context, j_if); }
        String for_each = tan.getForEach(context);
        if (for_each != null) { btbi.setForeachExpression(context, for_each); }
        // getContent - to be processed earlier during parsing and content provision
        // getComplex - more or less xml specific and likely meaningless here
        // getExpression
        // getExpressionText
        // getExpressionValue
        // getLocator
        // getLocatorBase
        // getLocatorBaseOIdRef
        // getLocatorBaseParameter
        // getNull
        String base = tan.getBase(context);
        if (base != null) { btbi.setBase(context, base); }
        String polymorphic = tan.getPolymorphic(context);
        if (polymorphic != null) { btbi.setPolymorphic(context, polymorphic); }
        String override = tan.getOverride(context);
        if (override != null) { btbi.setOverride(context, override); }
        String j_catch = tan.getCatch(context);
        if (j_catch != null) { btbi.setCatch(context, j_catch); }
        String applies_to = tan.getAppliesTo(context);
        if (applies_to != null) { btbi.setAppliesTo(context, applies_to); }
        String pass = tan.getPass(context);
        if (pass != null) { btbi.setPass(context, Integer.parseInt(pass)); }
        // getMeta
        // getCode
    }
}
