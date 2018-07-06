package com.sphenon.engines.factorysite.xml;

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

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

public class BuildTextSwitchXML extends BuildTextXMLBaseImpl implements BuildTextSwitch, Dumpable {

    protected Vector_Pair_BuildText_String__long_ cases;

    public BuildTextSwitchXML (CallContext context, Element node, String source_location_info, BuildTextXMLFactory bt_factory) throws InvalidDocument {
        super(context, node, source_location_info);

        cases = Factory_Vector_Pair_BuildText_String__long_.construct(context);

        Node child = node.getFirstChild();
        while (child != null) {
            int type = child.getNodeType();
            if (type == Node.ELEMENT_NODE) {
                Element element = null;
                try {
                    element = (Element) child;
                } catch (ClassCastException e) {
                    CustomaryContext.create((Context)context).throwInvalidState(context, FactorySiteStringPool.get(context, "0.0.0" /* Node is of DOM type 'ELEMENT_NODE' but cannot be down-cast to Element */));
                }
                String tag_name = element.getTagName();
                String element_name = element.getAttribute(BuildTextKeywords.NAME);
                if (element_name == null || element_name.length() == 0) {
                    element_name = tag_name;
                }
                BuildText btchild = bt_factory.create(context, element);

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
            } else if (type == Node.COMMENT_NODE) {
                /* kindly ignore */
            } else {
                if (type == Node.TEXT_NODE || type == Node.CDATA_SECTION_NODE) {
                    Text text = (Text) child;
                            
                    if (text.getData().matches("^\\s*$") == false) {
                        NotificationContext.sendWarning(context, FactorySiteStringPool.get(context, "0.0.1" /* Text node in complex BuildText ist not plain whitespace, as expected: '%(data)' */), "data", text.getData());
                    }
                } else {
                    NotificationContext.sendWarning(context, FactorySiteStringPool.get(context, "0.0.3" /* Node type '%(nodetype)' not expected */), "nodetype", t.s(type) + (type == Node.ENTITY_REFERENCE_NODE ? " - ENTITY_REFERENCE_NODE" : ""));
                }
            }
            child = child.getNextSibling();
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
