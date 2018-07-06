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

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

import java.io.StringWriter;

public interface BuildText {
    public String getOCPId (CallContext context);
    public String getNodeName (CallContext context);
    public String getOID (CallContext context);
    public String getBase (CallContext context);
    public String getPolymorphic (CallContext context);
    public String getAssignTo (CallContext context);
    public String getTypeName (CallContext context);
    public String getFactoryName (CallContext context);
    public String getRetrieverName (CallContext context);
    public String getMethodName (CallContext context);
    public String getAlias (CallContext context);
    public boolean allowDynamicTypeCheck (CallContext context);
    public boolean allowMissingArguments (CallContext context);
    public boolean isSingleton (CallContext context);
    public boolean haveDynamicParameters (CallContext context);
    public String getListener (CallContext context);
    public boolean isExpression (CallContext context);
    public String getCatch (CallContext context);
    public String getNameAttribute (CallContext context);
    public String getIfExpression (CallContext context);
    public String getForeachExpression (CallContext context);
    public String getSignature (CallContext context);
    public String getDefine (CallContext context);
    public String getEvaluator (CallContext context);
    public String getAppliesTo (CallContext context);
    public int    getPass (CallContext context);
    public String getOverride (CallContext context);
    public String getNameSpace (CallContext context);

    public Vector<String[]> getPreConditions (CallContext context);
    public Vector<String[]> getPostConditions (CallContext context);
    public Vector<String[]> getPreBuildScripts (CallContext context);
    public Vector<String[]> getPostBuildScripts (CallContext context);

    public void setOCPId (CallContext context, String ocp_id);
    public void setOID (CallContext context, String oid);
    public void setBase (CallContext context, String base);
    public void setPolymorphic (CallContext context, String polymorphic);
    public void setTypeName (CallContext context, String type_name);
    public void setFactoryName (CallContext context, String factory_name);
    public void setRetrieverName (CallContext context, String retriever_name);
    public void setMethodName (CallContext context, String method_name);
    public void setAppliesTo (CallContext context, String applies_to);
    public void setPass (CallContext context, int pass);
    public void setNameSpace (CallContext context, String name_space);
    public void setAllowDynamicTypeCheck (CallContext context, boolean allow_dynamic_type_check);

    public void setNodeName (CallContext context, String node_name);
    public void setNameAttribute (CallContext context, String name_attribute);

    public void setSignature (CallContext context, String signature);

    public Map<String,Object> getMetaData (CallContext context);

    /**
       Where the build text was originally defined, for debugging purposes.
       E.g., if the source of the OCP is a XML file, the file name and
       line number are returned.
       @return the location info as a string
    */
    public String getSourceLocationInfo(CallContext context);

    public void printCOCPCode(CallContext context, StringWriter bw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid, String cocp_file_name);
    public String getCOCPCodeClass(CallContext context);
    public int getCOCPCodeClassIndex(CallContext context);
}
