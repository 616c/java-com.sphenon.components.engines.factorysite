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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.encoding.*;

import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

import java.io.StringWriter;

public class BuildTextMerged extends BuildTextBase implements BuildTextComplex, Dumpable {

    protected BuildTextComplex first;

    public BuildTextComplex getFirst (CallContext context) {
        return this.first;
    }

    public void setFirst (CallContext context, BuildTextComplex first) {
        this.first = first;
    }

    protected BuildTextComplex second;

    public BuildTextComplex getSecond (CallContext context) {
        return this.second;
    }

    public void setSecond (CallContext context, BuildTextComplex second) {
        this.second = second;
    }

    public BuildTextMerged(CallContext context) {
        super(context);
        this.first  = null;
        this.second = null;
    }

    protected boolean items_merged = false;
    protected Vector_Pair_BuildText_String__long_ merged_items;

    public Vector_Pair_BuildText_String__long_ getItems (CallContext context) {
        if (this.items_merged == false) {
            if (this.first.getItems(context) != null && this.second.getItems(context) != null) {
                this.merged_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);
                Vector_Pair_BuildText_String__long_ first_items = this.first.getItems(context);
                for (Pair_BuildText_String_ item : first_items.getIterable_Pair_BuildText_String__(context)) {
                    this.merged_items.append(context, item);
                }
                Vector_Pair_BuildText_String__long_ second_items = this.second.getItems(context);
                for (Pair_BuildText_String_ item : second_items.getIterable_Pair_BuildText_String__(context)) {
                    this.merged_items.append(context, item);
                }
            } else if (this.first.getItems(context) != null) {
                this.merged_items = this.first.getItems(context);
            } else if (this.second.getItems(context) != null) {
                this.merged_items = this.second.getItems(context);
            } else {
                this.merged_items = null;
            }
            items_merged = true;
        }
        return this.merged_items;
    }

    public String getOCPId (CallContext context) {
        return (this.first.getOCPId(context) != null && this.first.getOCPId(context).length() != 0 ?
                this.first.getOCPId(context) : this.second.getOCPId(context));
    }

    public void setOCPId (CallContext context, String ocp_id) {
        this.first.setOCPId(context, ocp_id);
    }

    public String getNodeName (CallContext context) {
        return (this.first.getNodeName(context) != null && this.first.getNodeName(context).length() != 0 ?
                this.first.getNodeName(context) : this.second.getNodeName(context));
    }

    public void setNodeName (CallContext context, String node_name) {
        this.first.setNodeName(context, node_name);
    }

    public String getOID (CallContext context) {
        return (this.first.getOID(context) != null && this.first.getOID(context).length() != 0 ?
                this.first.getOID(context) : this.second.getOID(context));
    }

    public void setOID (CallContext context, String oid) {
        this.first.setOID(context, oid);
    }

    public String getBase (CallContext context) {
        return (this.first.getBase(context) != null && this.first.getBase(context).length() != 0 ?
                this.first.getBase(context) : this.second.getBase(context));
    }

    public void setBase (CallContext context, String base) {
        this.first.setBase(context, base);
    }

    public String getPolymorphic (CallContext context) {
        return (this.first.getPolymorphic(context) != null && this.first.getPolymorphic(context).length() != 0 ?
                this.first.getPolymorphic(context) : this.second.getPolymorphic(context));
    }

    public void setPolymorphic (CallContext context, String polymorphic) {
        this.first.setPolymorphic(context, polymorphic);
    }

    public String getAssignTo (CallContext context) {
        return (this.first.getAssignTo(context) != null && this.first.getAssignTo(context).length() != 0 ?
                this.first.getAssignTo(context) : this.second.getAssignTo(context));
    }

    public String getTypeName (CallContext context) {
        return (this.first.getTypeName(context) != null && this.first.getTypeName(context).length() != 0 ?
                this.first.getTypeName(context) : this.second.getTypeName(context));
    }

    public void setTypeName (CallContext context, String type_name) {
        this.first.setTypeName(context, type_name);
    }

    public String getFactoryName (CallContext context) {
        return (this.first.getFactoryName(context) != null && this.first.getFactoryName(context).length() != 0 ?
                this.first.getFactoryName(context) : this.second.getFactoryName(context));
    }

    public void setFactoryName (CallContext context, String factory_name) {
        this.first.setFactoryName(context, factory_name);
    }

    public String getRetrieverName (CallContext context) {
        return (this.first.getRetrieverName(context) != null && this.first.getRetrieverName(context).length() != 0 ?
                this.first.getRetrieverName(context) : this.second.getRetrieverName(context));
    }

    public void setRetrieverName (CallContext context, String retriever_name) {
        this.first.setRetrieverName(context, retriever_name);
    }

    public String getMethodName (CallContext context) {
        return (this.first.getMethodName(context) != null && this.first.getMethodName(context).length() != 0 ?
                this.first.getMethodName(context) : this.second.getMethodName(context));
    }

    public void setMethodName (CallContext context, String method_name) {
        this.first.setMethodName(context, method_name);
    }

    public String getAlias (CallContext context) {
        return (this.first.getAlias(context) != null && this.first.getAlias(context).length() != 0 ?
                this.first.getAlias(context) : this.second.getAlias(context));
    }

    public boolean allowDynamicTypeCheck (CallContext context) {
        return this.first.allowDynamicTypeCheck(context) || this.second.allowDynamicTypeCheck(context);
    }

    public void setAllowDynamicTypeCheck (CallContext context, boolean allow_dynamic_type_check) {
        this.first.setAllowDynamicTypeCheck(context, allow_dynamic_type_check);
    }

    public boolean allowMissingArguments (CallContext context) {
        return this.first.allowMissingArguments(context) || this.second.allowMissingArguments(context);
    }

    public boolean isSingleton (CallContext context) {
        return this.first.isSingleton(context) || this.second.isSingleton(context);
    }

    public boolean haveDynamicParameters (CallContext context) {
        return this.first.haveDynamicParameters(context) || this.second.haveDynamicParameters(context);
    }

    public String getNameAttribute (CallContext context) {
        return (this.first.getNameAttribute(context) != null && this.first.getNameAttribute(context).length() != 0 ?
                this.first.getNameAttribute(context) : this.second.getNameAttribute(context));
    }

    public void setNameAttribute (CallContext context, String name_attribute) {
        this.first.setNameAttribute(context, name_attribute);
    }

    public boolean isExpression (CallContext context) {
        return this.first.isExpression(context) || this.second.isExpression(context);
    }

    public String getIfExpression (CallContext context) {
        return (this.first.getIfExpression(context) != null && this.first.getIfExpression(context).length() != 0 ?
                this.first.getIfExpression(context) : this.second.getIfExpression(context));
    }

    public String getForeachExpression (CallContext context) {
        return (this.first.getForeachExpression(context) != null && this.first.getForeachExpression(context).length() != 0 ?
                this.first.getForeachExpression(context) : this.second.getForeachExpression(context));
    }

    public String getSignature (CallContext context) {
        return (this.first.getSignature(context) != null && this.first.getSignature(context).length() != 0 ?
                this.first.getSignature(context) : this.second.getSignature(context));
    }

    public void setSignature (CallContext context, String signature) {
        this.first.setSignature(context, signature);
    }

    public String getComponentType (CallContext context) {
        return (this.first.getComponentType(context) != null && this.first.getComponentType(context).length() != 0 ?
                this.first.getComponentType(context) : this.second.getComponentType(context));
    }

    public String getDefine (CallContext context) {
        return (this.first.getDefine(context) == null || this.first.getDefine(context).length() == 0 ?
                  this.second.getDefine(context)
                : this.second.getDefine(context) == null || this.second.getDefine(context).length() == 0 ?
                    this.first.getDefine(context)
                  : (this.second.getDefine(context) + ";" + this.first.getDefine(context))
               );
    }

    public String getEvaluator (CallContext context) {
        return (this.first.getEvaluator(context) == null || this.first.getEvaluator(context).length() == 0 ?
                  this.second.getEvaluator(context)
                : this.second.getEvaluator(context) == null || this.second.getEvaluator(context).length() == 0 ?
                    this.first.getEvaluator(context)
                  : (this.second.getEvaluator(context) + ";" + this.first.getEvaluator(context))
               );
    }

    public String getCatch (CallContext context) {
        return (this.first.getCatch(context) != null && this.first.getCatch(context).length() != 0 ?
                this.first.getCatch(context) : this.second.getCatch(context));
    }

    public String getAppliesTo (CallContext context) {
        return (this.first.getAppliesTo(context) != null && this.first.getAppliesTo(context).length() != 0 ?
                this.first.getAppliesTo(context) : this.second.getAppliesTo(context));
    }

    public void setAppliesTo (CallContext context, String applies_to) {
        this.first.setAppliesTo(context, applies_to);
    }

    public String getListener (CallContext context) {
        return (this.first.getListener(context) != null && this.first.getListener(context).length() != 0 ?
                this.first.getListener(context) : this.second.getListener(context));
    }

    public int getPass (CallContext context) {
        return (this.first.getPass(context) != 0 ?
                this.first.getPass(context) : this.second.getPass(context));
    }

    public void setPass (CallContext context, int pass) {
        this.first.setPass(context, pass);
    }

    public String getOverride (CallContext context) {
        return (this.first.getOverride(context) != null && this.first.getOverride(context).length() != 0 ?
                this.first.getOverride(context) : this.second.getOverride(context));
    }

    public String getNameSpace (CallContext context) {
        return (this.first.getNameSpace(context) != null && this.first.getNameSpace(context).length() != 0 ?
                this.first.getNameSpace(context) : this.second.getNameSpace(context));
    }

    public void setNameSpace (CallContext context, String name_space) {
        this.first.setNameSpace(context, name_space);
    }

    public String getSourceLocationInfo (CallContext context) {
        return (this.first.getSourceLocationInfo(context) != null && this.first.getSourceLocationInfo(context).length() != 0 ?
                this.first.getSourceLocationInfo(context) : this.second.getSourceLocationInfo(context));
    }

    protected boolean map_merged = false;
    protected Map<String,Object> merged_map;

    public Map<String,Object> getMetaData (CallContext context) {
        if (this.map_merged == false) {
            if (this.first.getMetaData(context) != null && this.second.getMetaData(context) != null) {
                this.merged_map = new HashMap<String,Object>();
                Map<String,Object> second_map = this.second.getMetaData(context);
                for (String key : second_map.keySet()) {
                    this.merged_map.put(key, second_map.get(key));
                }
                Map<String,Object> first_map = this.first.getMetaData(context);
                for (String key : first_map.keySet()) {
                    this.merged_map.put(key, first_map.get(key));
                }
            } else if (this.first.getMetaData(context) != null) {
                this.merged_map = this.first.getMetaData(context);
            } else if (this.second.getMetaData(context) != null) {
                this.merged_map = this.second.getMetaData(context);
            } else {
                this.merged_map = null;
            }
            map_merged = true;
        }
        return this.merged_map;
    }

    protected boolean preconditions_merged = false;
    protected Vector<String[]> merged_preconditions;

    public Vector<String[]> getPreConditions (CallContext context) {
        if (this.preconditions_merged == false) {
            if (this.first.getPreConditions(context) != null && this.second.getPreConditions(context) != null) {
                this.merged_preconditions = new Vector<String[]>();
                Vector<String[]> second_preconditions = this.second.getPreConditions(context);
                for (String[] precondition : second_preconditions) {
                    this.merged_preconditions.add(precondition);
                }
                Vector<String[]> first_preconditions = this.first.getPreConditions(context);
                for (String[] precondition : first_preconditions) {
                    this.merged_preconditions.add(precondition);
                }
            } else if (this.first.getPreConditions(context) != null) {
                this.merged_preconditions = this.first.getPreConditions(context);
            } else if (this.second.getPreConditions(context) != null) {
                this.merged_preconditions = this.second.getPreConditions(context);
            } else {
                this.merged_preconditions = null;
            }
            preconditions_merged = true;
        }
        return this.merged_preconditions;
    }

    protected boolean postconditions_merged = false;
    protected Vector<String[]> merged_postconditions;

    public Vector<String[]> getPostConditions (CallContext context) {
        if (this.postconditions_merged == false) {
            if (this.first.getPostConditions(context) != null && this.second.getPostConditions(context) != null) {
                this.merged_postconditions = new Vector<String[]>();
                Vector<String[]> second_postconditions = this.second.getPostConditions(context);
                for (String[] postcondition : second_postconditions) {
                    this.merged_postconditions.add(postcondition);
                }
                Vector<String[]> first_postconditions = this.first.getPostConditions(context);
                for (String[] postcondition : first_postconditions) {
                    this.merged_postconditions.add(postcondition);
                }
            } else if (this.first.getPostConditions(context) != null) {
                this.merged_postconditions = this.first.getPostConditions(context);
            } else if (this.second.getPostConditions(context) != null) {
                this.merged_postconditions = this.second.getPostConditions(context);
            } else {
                this.merged_postconditions = null;
            }
            postconditions_merged = true;
        }
        return this.merged_postconditions;
    }

    protected boolean prebuildscripts_merged = false;
    protected Vector<String[]> merged_prebuildscripts;

    public Vector<String[]> getPreBuildScripts (CallContext context) {
        if (this.prebuildscripts_merged == false) {
            if (this.first.getPreBuildScripts(context) != null && this.second.getPreBuildScripts(context) != null) {
                this.merged_prebuildscripts = new Vector<String[]>();
                Vector<String[]> second_prebuildscripts = this.second.getPreBuildScripts(context);
                for (String[] prebuildscript : second_prebuildscripts) {
                    this.merged_prebuildscripts.add(prebuildscript);
                }
                Vector<String[]> first_prebuildscripts = this.first.getPreBuildScripts(context);
                for (String[] prebuildscript : first_prebuildscripts) {
                    this.merged_prebuildscripts.add(prebuildscript);
                }
            } else if (this.first.getPreBuildScripts(context) != null) {
                this.merged_prebuildscripts = this.first.getPreBuildScripts(context);
            } else if (this.second.getPreBuildScripts(context) != null) {
                this.merged_prebuildscripts = this.second.getPreBuildScripts(context);
            } else {
                this.merged_prebuildscripts = null;
            }
            prebuildscripts_merged = true;
        }
        return this.merged_prebuildscripts;
    }

    protected boolean postbuildscripts_merged = false;
    protected Vector<String[]> merged_postbuildscripts;

    public Vector<String[]> getPostBuildScripts (CallContext context) {
        if (this.postbuildscripts_merged == false) {
            if (this.first.getPostBuildScripts(context) != null && this.second.getPostBuildScripts(context) != null) {
                this.merged_postbuildscripts = new Vector<String[]>();
                Vector<String[]> second_postbuildscripts = this.second.getPostBuildScripts(context);
                for (String[] postbuildscript : second_postbuildscripts) {
                    this.merged_postbuildscripts.add(postbuildscript);
                }
                Vector<String[]> first_postbuildscripts = this.first.getPostBuildScripts(context);
                for (String[] postbuildscript : first_postbuildscripts) {
                    this.merged_postbuildscripts.add(postbuildscript);
                }
            } else if (this.first.getPostBuildScripts(context) != null) {
                this.merged_postbuildscripts = this.first.getPostBuildScripts(context);
            } else if (this.second.getPostBuildScripts(context) != null) {
                this.merged_postbuildscripts = this.second.getPostBuildScripts(context);
            } else {
                this.merged_postbuildscripts = null;
            }
            postbuildscripts_merged = true;
        }
        return this.merged_postbuildscripts;
    }

    public boolean isExpanded (CallContext context) {
        return false;
    }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextComplex_String";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextComplex_String;
    }

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_ComponentType, this.getComponentType(context), true);
    }

    public void dump(CallContext context, DumpNode dump_node) {
        super.dump(context, dump_node);
        if (this.getComponentType(context) != null) {
            dump_node.dump(context, "ComponentType", this.getComponentType(context));
        }
    }
}
