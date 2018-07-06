package com.sphenon.engines.factorysite.json;

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
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.expression.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.io.StringWriter;

import java.util.HashMap;
import java.util.Vector;
import java.text.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

public class BuildTextSwitchJSON extends BuildTextJSONBaseImpl implements BuildTextSwitch, Dumpable {

    protected Vector_Pair_BuildText_String__long_ cases;

    public BuildTextSwitchJSON (CallContext context, JsonNode node, String name, String source_location_info, BuildTextJSONFactory bt_factory) throws InvalidDocument {
        super(context, node, name, source_location_info);

        cases = Factory_Vector_Pair_BuildText_String__long_.construct(context);

        if (node.isObject()) {
            java.util.Iterator<String> iterator = node.fieldNames();
            while (iterator.hasNext()) {
                String field_name = iterator.next();
                BuildText btchild = bt_factory.create(context, node.get(field_name), field_name);

                String element_name = btchild.getNodeName(context);
                if (element_name == null || element_name.length() == 0) {
                    element_name = field_name;
                }

                if (btchild.getTypeName(context) == null || btchild.getTypeName(context).length() == 0) {
                    btchild.setTypeName(context, this.getTypeName(context));
                }
                if (btchild.getFactoryName(context) == null || btchild.getFactoryName(context).length() == 0) {
                    btchild.setFactoryName(context, this.getFactoryName(context));
                }
                if (btchild.getRetrieverName(context) == null || btchild.getRetrieverName(context).length() == 0) {
                    btchild.setRetrieverName(context, this.getRetrieverName(context));
                }
                if (btchild.getMethodName(context) == null || btchild.getMethodName(context).length() == 0) {
                    btchild.setMethodName(context, this.getMethodName(context));
                }

                cases.append(context, new Pair_BuildText_String_(context, btchild, element_name));
            }
        }

        if (node.isArray()) {
            int i=0;
            java.util.Iterator<JsonNode> elements = node.elements();
            while (elements.hasNext()) {
                String id = "" + i;
                JsonNode child = elements.next();
                BuildText btchild = bt_factory.create(context, child, id);

                String element_name = btchild.getNodeName(context);
                if (element_name == null || element_name.length() == 0) {
                    element_name = id;
                }

                if (btchild.getTypeName(context) == null || btchild.getTypeName(context).length() == 0) {
                    btchild.setTypeName(context, this.getTypeName(context));
                }
                if (btchild.getFactoryName(context) == null || btchild.getFactoryName(context).length() == 0) {
                    btchild.setFactoryName(context, this.getFactoryName(context));
                }
                if (btchild.getRetrieverName(context) == null || btchild.getRetrieverName(context).length() == 0) {
                    btchild.setRetrieverName(context, this.getRetrieverName(context));
                }
                if (btchild.getMethodName(context) == null || btchild.getMethodName(context).length() == 0) {
                    btchild.setMethodName(context, this.getMethodName(context));
                }

                cases.append(context, new Pair_BuildText_String_(context, btchild, element_name));
            }
        }
    }

    public Vector_Pair_BuildText_String__long_ getCases (CallContext context) {
        return this.cases;
    }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextSwitch_String";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextSwitch_String;
    }

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
    }

    public void dump(CallContext context, DumpNode dump_node) {
        super.dump(context, dump_node);
    }
}
