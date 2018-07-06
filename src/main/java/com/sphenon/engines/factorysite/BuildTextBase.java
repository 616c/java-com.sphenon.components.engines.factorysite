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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.xml.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.xml.*;
import com.sphenon.engines.factorysite.json.*;
import com.sphenon.engines.factorysite.yaml.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

import java.io.StringWriter;

abstract public class BuildTextBase implements BuildText, Dumpable {

    static public final String EMPTY = "";

    public BuildTextBase(CallContext context) {
    }

    abstract public String getOCPId (CallContext context);
    abstract public String getNodeName (CallContext context);
    abstract public String getOID (CallContext context);
    abstract public String getBase (CallContext context);
    abstract public String getPolymorphic (CallContext context);
    abstract public String getAssignTo (CallContext context);
    abstract public String getTypeName (CallContext context);
    abstract public String getFactoryName (CallContext context);
    abstract public String getRetrieverName (CallContext context);
    abstract public String getMethodName (CallContext context);
    abstract public String getAlias (CallContext context);
    abstract public boolean allowDynamicTypeCheck (CallContext context);
    abstract public boolean allowMissingArguments (CallContext context);
    abstract public boolean isSingleton (CallContext context);
    abstract public boolean haveDynamicParameters (CallContext context);
    abstract public String getNameAttribute (CallContext context);
    abstract public boolean isExpression (CallContext context);
    abstract public String getIfExpression (CallContext context);
    abstract public String getForeachExpression (CallContext context);
    abstract public String getSignature (CallContext context);
    abstract public String getDefine (CallContext context);
    abstract public String getEvaluator (CallContext context);
    abstract public String getAppliesTo (CallContext context);
    abstract public String getListener (CallContext context);
    abstract public int getPass (CallContext context);
    abstract public String getOverride (CallContext context);
    abstract public String getNameSpace (CallContext context);
    abstract public String getSourceLocationInfo (CallContext context);
    abstract public Map<String,Object> getMetaData (CallContext context);
    abstract public Vector<String[]> getPreConditions (CallContext context);
    abstract public Vector<String[]> getPostConditions (CallContext context);
    abstract public Vector<String[]> getPreBuildScripts (CallContext context);
    abstract public Vector<String[]> getPostBuildScripts (CallContext context);

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
    }

    protected void printCOCPString(CallContext context, StringWriter sw, String indent, String name, String value, boolean encode) {
        // vermutlich muss das encode immer true sein,
        // aber erst mal refactoring gemaess "never change a running system"
        if (value != null && value.isEmpty() == false) {
            sw.write(indent + "bt.set" + name + "(context, " + (value.length() == 0 ? "EMPTY" : ("\"" + (encode ? Encoding.recode(context, value, Encoding.UTF8, Encoding.JAVA) : value) + "\"")) + ");\n");
        }
    }

    protected void printCOCPString(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, int index, String value, boolean encode) {
        if (value != null && value.isEmpty() == false) {
            dr.add(index);
            dr.add((value.length() == 0 ? ((Integer) 1) : coder.writeText(context, value)));
        }
    }

    protected void printCOCPString(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, int index, boolean value) {
        if (value == true) {
            dr.add(index);
            dr.add(coder.writeBoolean(context, value));
        }
    }

    protected void printCOCPString(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, int index, int value, int default_value) {
        if (value != default_value) {
            dr.add(index);
            dr.add(value);
        }
    }

    public void printCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid, String cocp_file_name) {
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_OCPId, this.getOCPId(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_NodeName, this.getNodeName(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_OID, this.getOID(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Base, this.getBase(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Polymorphic, this.getPolymorphic(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_AssignTo, this.getAssignTo(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_TypeName, this.getTypeName(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_FactoryName, this.getFactoryName(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_RetrieverName, this.getRetrieverName(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_MethodName, this.getMethodName(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Alias, this.getAlias(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_AllowDynamicTypeCheck, this.allowDynamicTypeCheck(context));
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_AllowMissingArguments, this.allowMissingArguments(context));
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_IsSingleton, this.isSingleton(context));
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_HaveDynamicParameters, this.haveDynamicParameters(context));
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_NameAttribute, this.getNameAttribute(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_IsExpression, this.isExpression(context));
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_IfExpression, this.getIfExpression(context), true);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_ForeachExpression, this.getForeachExpression(context), true);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Signature, this.getSignature(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Define, this.getDefine(context), true);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Evaluator, this.getEvaluator(context), true);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Catch, this.getCatch(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_AppliesTo, this.getAppliesTo(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Listener, this.getListener(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Pass, this.getPass(context), 1);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Override, this.getOverride(context), false);
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_NameSpace, this.getNameSpace(context), false);
        if (this.getSourceLocationInfo(context) != null) {
            dr.add(COCPIndices.BuildText_SourceLocationInfo);
            dr.add(coder.writeText(context, cocp_file_name + ":" + this.getSourceLocationInfo(context)));
        }
        Map<String,Object> meta_data = this.getMetaData(context);
        if (meta_data != null && meta_data.size() != 0) {
            org.w3c.dom.Node xml_node = null;
            com.fasterxml.jackson.databind.JsonNode json_node = null;;
            Object yaml_node = null;
            boolean is_btcx = false;
            boolean is_btcj = false;
            boolean is_btcy = false;
            for (String key : meta_data.keySet()) {
                Object mdo = meta_data.get(key);
                if (mdo instanceof String) {
                    dr.add(COCPIndices.BuildText_MetaData);
                    dr.add(0);
                    dr.add(coder.writeText(context, key));
                    dr.add(coder.writeText(context, (String) mdo));
                } else if (    (xml_node = (   (is_btcx = (mdo instanceof BuildTextComplexXML))
                                             ? ((BuildTextComplexXML) mdo).getNode(context)
                                             : (   mdo instanceof org.w3c.dom.Node
                                                 ? ((org.w3c.dom.Node) mdo)
                                                 : null
                                               )
                                           )
                               ) != null
                          ) {
                    String xml = XMLUtil.serialiseContent(context, xml_node);
                    String mdxml = ((is_btcx ? ("<MetaData" + ((BuildTextXML) mdo).getStartTagAttributes(context) + ">") : "") + xml + (is_btcx ? ("</MetaData>") : ""));

                    dr.add(COCPIndices.BuildText_MetaData);
                    dr.add((is_btcx ? 2 : 1));
                    dr.add(coder.writeText(context, key));
                    dr.add(coder.writeText(context, mdxml));
                } else if (    (json_node = (   (is_btcj = (mdo instanceof BuildTextComplexJSON))
                                              ? ((BuildTextComplexJSON) mdo).getNode(context)
                                              : (   mdo instanceof com.fasterxml.jackson.databind.JsonNode
                                                  ? ((com.fasterxml.jackson.databind.JsonNode) mdo)
                                                  : null
                                                )
                                            )
                               ) != null
                          ) {
                    String json = BuildTextJSONFactory.getText(context, json_node);
                    String mdjson = /* ((is_btcj ? ("<MetaData" + ((BuildTextJSON) mdo).getStartTagAttributes(context) + ">") : "") + */ json /* + (is_btcj ? ("</MetaData>") : "")) */;

                    dr.add(COCPIndices.BuildText_MetaData);
                    dr.add((is_btcj ? 4 : 3));
                    dr.add(coder.writeText(context, key));
                    dr.add(coder.writeText(context, mdjson));
                } else if (    (yaml_node = (   (is_btcy = (mdo instanceof BuildTextComplexYAML))
                                              ? ((BuildTextComplexYAML) mdo).getNode(context)
                                              // [ToDo:YAML] !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                              // : (   mdo instanceof Object
                                              //     ? ((Object) mdo)
                                                  : null
                                              //   )
                                            )
                               ) != null
                          ) {
                    String yaml = BuildTextYAMLFactory.getText(context, yaml_node);
                    String mdyaml = /* ((is_btcy ? ("<MetaData" + ((BuildTextYAML) mdo).getStartTagAttributes(context) + ">") : "") + */ yaml /* + (is_btcy ? ("</MetaData>") : "")) */;

                    dr.add(COCPIndices.BuildText_MetaData);
                    dr.add((is_btcy ? 6 : 5));
                    dr.add(coder.writeText(context, key));
                    dr.add(coder.writeText(context, mdyaml));
                } else {
                    CustomaryContext.create((Context)context).throwLimitation(context, "Meta data class '%(mdclass)' not supported yet for COCP creation", "mdclass", mdo.getClass().getName());
                    throw (ExceptionLimitation) null; // compiler insists
                }
            }
        }
        if (this.getPreConditions(context) != null && this.getPreConditions(context).size() != 0) {
            dr.add(COCPIndices.BuildText_PreConditions);
            dr.add(this.getPreConditions(context).size());
            for (String[] pre_condition : this.getPreConditions(context)) {
                dr.add(pre_condition.length);
                int i=0;
                for (String pre_condition_part : pre_condition) {
                    dr.add(coder.writeText(context, (pre_condition_part == null ? null : pre_condition_part)));
                }
            }
        }
        if (this.getPostConditions(context) != null && this.getPostConditions(context).size() != 0) {
            dr.add(COCPIndices.BuildText_PostConditions);
            dr.add(this.getPostConditions(context).size());
            for (String[] post_condition : this.getPostConditions(context)) {
                dr.add(post_condition.length);
                int i=0;
                for (String post_condition_part : post_condition) {
                    dr.add(coder.writeText(context, (post_condition_part == null ? null : post_condition_part)));
                }
            }
        }
        if (this.getPreBuildScripts(context) != null && this.getPreBuildScripts(context).size() != 0) {
            dr.add(COCPIndices.BuildText_PreBuildScripts);
            dr.add(this.getPreBuildScripts(context).size());
            for (String[] pre_build_script : this.getPreBuildScripts(context)) {
                dr.add(pre_build_script.length);
                int i=0;
                for (String pre_build_script_part : pre_build_script) {
                    dr.add(coder.writeText(context, (pre_build_script_part == null ? null : pre_build_script_part)));
                }
            }
        }
        if (this.getPostBuildScripts(context) != null && this.getPostBuildScripts(context).size() != 0) {
            dr.add(COCPIndices.BuildText_PostBuildScripts);
            dr.add(this.getPostBuildScripts(context).size());
            for (String[] post_build_script : this.getPostBuildScripts(context)) {
                dr.add(post_build_script.length);
                int i=0;
                for (String post_build_script_part : post_build_script) {
                    dr.add(coder.writeText(context, (post_build_script_part == null ? null : post_build_script_part)));
                }
            }
        }
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_IsExpanded, true);
        this.printSpecificCOCPCode(context, sw, dr, coder, indent, site_id, dotid);
    }

    public void dump(CallContext context, DumpNode dump_node) {
        dump_node.dump(context, "BuildText", this.getClass().getName().replaceFirst(".*\\.BuildText", ""));
        if (this.getOCPId(context) != null) {
            dump_node.dump(context, "OCPId", this.getOCPId(context));
        }
        if (this.getNodeName(context) != null) {
            dump_node.dump(context, "NodeName", this.getNodeName(context));
        }
        if (this.getOID(context) != null) {
            dump_node.dump(context, "OID", this.getOID(context));
        }
        if (this.getBase(context) != null) {
            dump_node.dump(context, "Base", this.getBase(context));
        }
        if (this.getPolymorphic(context) != null) {
            dump_node.dump(context, "Polymorphic", this.getPolymorphic(context));
        }
        if (this.getAssignTo(context) != null) {
            dump_node.dump(context, "AssignTo", this.getAssignTo(context));
        }
        if (this.getTypeName(context) != null) {
            dump_node.dump(context, "TypeName", this.getTypeName(context));
        }
        if (this.getFactoryName(context) != null) {
            dump_node.dump(context, "FactoryName", this.getFactoryName(context));
        }
        if (this.getRetrieverName(context) != null) {
            dump_node.dump(context, "RetrieverName", this.getRetrieverName(context));
        }
        if (this.getMethodName(context) != null) {
            dump_node.dump(context, "MethodName", this.getMethodName(context));
        }
        if (this.getAlias(context) != null) {
            dump_node.dump(context, "Alias", this.getAlias(context));
        }
        if (this.allowDynamicTypeCheck(context) == true) {
            dump_node.dump(context, "AllowDynamicTypeCheck", this.allowDynamicTypeCheck(context));
        }
        if (this.allowMissingArguments(context) == true) {
            dump_node.dump(context, "AllowMissingArguments", this.allowMissingArguments(context));
        }
        if (this.isSingleton(context) == true) {
            dump_node.dump(context, "IsSingleton", this.isSingleton(context));
        }
        if (this.haveDynamicParameters(context) == true) {
            dump_node.dump(context, "HaveDynamicParameters", this.haveDynamicParameters(context));
        }
        if (this.getNameAttribute(context) != null) {
            dump_node.dump(context, "NameAttribute", this.getNameAttribute(context));
        }
        if (this.isExpression(context) == true) {
            dump_node.dump(context, "IsExpression", this.isExpression(context));
        }
        if (this.getIfExpression(context) != null) {
            dump_node.dump(context, "IfExpression", this.getIfExpression(context));
        }
        if (this.getForeachExpression(context) != null) {
            dump_node.dump(context, "ForeachExpression", this.getForeachExpression(context));
        }
        if (this.getSignature(context) != null) {
            dump_node.dump(context, "Signature", this.getSignature(context));
        }
        if (this.getDefine(context) != null) {
            dump_node.dump(context, "Define", this.getDefine(context));
        }
        if (this.getEvaluator(context) != null) {
            dump_node.dump(context, "Evaluator", this.getEvaluator(context));
        }
        if (this.getCatch(context) != null) {
            dump_node.dump(context, "Catch", this.getCatch(context));
        }
        if (this.getAppliesTo(context) != null) {
            dump_node.dump(context, "AppliesTo", this.getAppliesTo(context));
        }
        if (this.getListener(context) != null) {
            dump_node.dump(context, "Listener", this.getListener(context));
        }
        if (this.getPass(context) != 1) {
            dump_node.dump(context, "Pass", this.getPass(context));
        }
        if (this.getOverride(context) != null) {
            dump_node.dump(context, "Override", this.getOverride(context));
        }
        if (this.getNameSpace(context) != null) {
            dump_node.dump(context, "NameSpace", this.getNameSpace(context));
        }
        if (this.getSourceLocationInfo(context) != null) {
            dump_node.dump(context, "SourceLocationInfo", this.getSourceLocationInfo(context));
        }
        Map<String,Object> meta_data = this.getMetaData(context);
        if (meta_data != null && meta_data.size() != 0) {
            for (String key : meta_data.keySet()) {
                Object mdo = meta_data.get(key);
                if (mdo instanceof String) {
                    dump_node.dump(context, "MetaData", key + " = " + mdo);
                } else if (mdo instanceof org.w3c.dom.Node) {
                    dump_node.dump(context, "MetaData", key + " = <DOM tree>");
                } else {
                    dump_node.dump(context, "MetaData", key + " = <unknown type>");
                }
            }
        }
        if (this.getPreConditions(context) != null && this.getPreConditions(context).size() != 0) {
            for (String[] pre_condition : this.getPreConditions(context)) {
                for (String pre_condition_part : pre_condition) {
                    dump_node.dump(context, "PreCondition", pre_condition_part);
                }
            }
        }
        if (this.getPostConditions(context) != null && this.getPostConditions(context).size() != 0) {
            for (String[] post_condition : this.getPostConditions(context)) {
                for (String post_condition_part : post_condition) {
                    dump_node.dump(context, "PostCondition", post_condition_part);
                }
            }
        }
        if (this.getPreBuildScripts(context) != null && this.getPreBuildScripts(context).size() != 0) {
            for (String[] pre_build_script : this.getPreBuildScripts(context)) {
                for (String pre_build_script_part : pre_build_script) {
                    dump_node.dump(context, "PreBuildScript", pre_build_script_part);
                }
            }
        }
        if (this.getPostBuildScripts(context) != null && this.getPostBuildScripts(context).size() != 0) {
            for (String[] post_build_script : this.getPostBuildScripts(context)) {
                for (String post_build_script_part : post_build_script) {
                    dump_node.dump(context, "PostBuildScript", post_build_script_part);
                }
            }
        }
    }


    // --------------------------------------------------------------------
    // hier implementiert aus convenience
    // *eigentlich* gehörte das in COCPBuildText, aber das ist ein
    // interface und Java erlaubt keine multiple inheritance,
    // ceterum censeo...

    protected Vector<COCPBuildText.Parameter> parameters_to_declare;

    public Vector<COCPBuildText.Parameter> getParametersToDeclare (CallContext context) {
        return this.parameters_to_declare;
    }

    public void setParametersToDeclare (CallContext context, Vector<COCPBuildText.Parameter> parameters_to_declare) {
        this.parameters_to_declare = parameters_to_declare;
    }

    public void addParameterToDeclare (CallContext context, String name, String type_id, boolean optional) {
        if (this.parameters_to_declare == null) {
            this.parameters_to_declare = new Vector<COCPBuildText.Parameter>();
        }
        parameters_to_declare.add(new COCPBuildText.Parameter(context, name, type_id, optional));
    }

    protected boolean is_expanded;

    public boolean isExpanded (CallContext context) {
        return this.is_expanded;
    }

    public void setIsExpanded (CallContext context, boolean is_expanded) {
        this.is_expanded = is_expanded;
    }

    protected BuildTextScaffoldFactory scaffold_factory;

    public BuildTextScaffoldFactory getScaffoldFactory (CallContext context) {
        return this.scaffold_factory;
    }

    public void setScaffoldFactory (CallContext context, BuildTextScaffoldFactory scaffold_factory) {
        this.scaffold_factory = scaffold_factory;
    }

    public String getStartTag(CallContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        if (this.getNodeName(context) != null) {
            sb.append(this.getNodeName(context));
        } else {
            sb.append("anonymous");
        }
        this.addStartTagAttributes(context, sb);
        sb.append(">");
        return sb.toString();
    }

    public String getEndTag(CallContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("</");
        if (this.getNodeName(context) != null) {
            sb.append(this.getNodeName(context));
        } else {
            sb.append("anonymous");
        }
        sb.append(">");
        return sb.toString();
    }

    public String getStartTagAttributes(CallContext context) {
        StringBuilder sb = new StringBuilder();
        this.addStartTagAttributes(context, sb);
        return sb.toString();
    }

    protected void addStartTagAttributes(CallContext context, StringBuilder sb) {
        if (this.getOCPId(context) != null && this.getOCPId(context).isEmpty() == false) {
            sb.append(" OCPID=\"");
            sb.append(this.getOCPId(context));
            sb.append("\"");
        }
        if (this.getOID(context) != null && this.getOID(context).isEmpty() == false) {
            sb.append(" OID=\"");
            sb.append(this.getOID(context));
            sb.append("\"");
        }
        if (this.getBase(context) != null && this.getBase(context).isEmpty() == false) {
            sb.append(" BASE=\"");
            sb.append(this.getBase(context));
            sb.append("\"");
        }
        if (this.getPolymorphic(context) != null && this.getPolymorphic(context).isEmpty() == false) {
            sb.append(" POLYMORPHIC=\"");
            sb.append(this.getPolymorphic(context));
            sb.append("\"");
        }
        if (this.getAssignTo(context) != null && this.getAssignTo(context).isEmpty() == false) {
            sb.append(" ASSIGNTO=\"");
            sb.append(this.getAssignTo(context));
            sb.append("\"");
        }
        if (this.getTypeName(context) != null && this.getTypeName(context).isEmpty() == false) {
            sb.append(" CLASS=\"");
            sb.append(this.getTypeName(context));
            sb.append("\"");
        }
        if (this.getFactoryName(context) != null && this.getFactoryName(context).isEmpty() == false) {
            sb.append(" FACTORY=\"");
            sb.append(this.getFactoryName(context));
            sb.append("\"");
        }
        if (this.getRetrieverName(context) != null && this.getRetrieverName(context).isEmpty() == false) {
            sb.append(" RETRIEVER=\"");
            sb.append(this.getRetrieverName(context));
            sb.append("\"");
        }
        if (this.getMethodName(context) != null && this.getMethodName(context).isEmpty() == false) {
            sb.append(" METHOD=\"");
            sb.append(this.getMethodName(context));
            sb.append("\"");
        }
        if (this.getAlias(context) != null && this.getAlias(context).isEmpty() == false) {
            sb.append(" ALIAS=\"");
            sb.append(this.getAlias(context));
            sb.append("\"");
        }
        if (this.allowDynamicTypeCheck(context) == true) {
            sb.append(" TYPECHECK=\"allow_dynamic\"");
        }
        if (this.allowMissingArguments(context) == true) {
            sb.append(" ARGUMENTCHECK=\"allow_missing\"");
        }
        if (this.isSingleton(context) == true) {
            sb.append(" SINGLETON=\"");
            sb.append(this.isSingleton(context));
            sb.append("\"");
        }
        if (this.haveDynamicParameters(context) == true) {
            sb.append(" DYNAMICPARAMETERS=\"");
            sb.append(this.haveDynamicParameters(context));
            sb.append("\"");
        }
        if (this.getNameAttribute(context) != null && this.getNameAttribute(context).isEmpty() == false) {
            sb.append(" NAME=\"");
            sb.append(this.getNameAttribute(context));
            sb.append("\"");
        }
        if (this.isExpression(context) == true) {
            sb.append(" EXPRESSION=\"");
            sb.append(this.isExpression(context));
            sb.append("\"");
        }
        if (this.getIfExpression(context) != null && this.getIfExpression(context).isEmpty() == false) {
            sb.append(" IF=\"");
            sb.append(this.getIfExpression(context));
            sb.append("\"");
        }
        if (this.getForeachExpression(context) != null && this.getForeachExpression(context).isEmpty() == false) {
            sb.append(" FOREACH=\"");
            sb.append(this.getForeachExpression(context));
            sb.append("\"");
        }
        if (this.getSignature(context) != null && this.getSignature(context).isEmpty() == false) {
            sb.append(" SIGNATURE=\"");
            sb.append(this.getSignature(context));
            sb.append("\"");
        }
        if (this.getDefine(context) != null && this.getDefine(context).isEmpty() == false) {
            sb.append(" DEFINE=\"");
            sb.append(this.getDefine(context));
            sb.append("\"");
        }
        if (this.getEvaluator(context) != null && this.getEvaluator(context).isEmpty() == false) {
            sb.append(" EVALUATOR=\"");
            sb.append(this.getEvaluator(context));
            sb.append("\"");
        }
        if (this.getCatch(context) != null && this.getCatch(context).isEmpty() == false) {
            sb.append(" CATCH=\"");
            sb.append(this.getCatch(context));
            sb.append("\"");
        }
        if (this.getAppliesTo(context) != null && this.getAppliesTo(context).isEmpty() == false) {
            sb.append(" APPLIESTO=\"");
            sb.append(this.getAppliesTo(context));
            sb.append("\"");
        }
        if (this.getListener(context) != null && this.getListener(context).isEmpty() == false) {
            sb.append(" LISTENER=\"");
            sb.append(this.getListener(context));
            sb.append("\"");
        }
        if (this.getPass(context) != 1) {
            sb.append(" PASS=\"");
            sb.append(this.getPass(context));
            sb.append("\"");
        }
        if (this.getOverride(context) != null && this.getOverride(context).isEmpty() == false) {
            sb.append(" OVERRIDE=\"");
            sb.append(this.getOverride(context));
            sb.append("\"");
        }
        if (this.getNameSpace(context) != null && this.getNameSpace(context).isEmpty() == false) {
            sb.append(" xmlns=\"");
            sb.append(this.getNameSpace(context));
            sb.append("\"");
        }
    }
}
