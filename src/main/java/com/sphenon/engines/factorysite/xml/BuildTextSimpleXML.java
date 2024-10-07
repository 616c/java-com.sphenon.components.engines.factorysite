package com.sphenon.engines.factorysite.xml;

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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.io.StringWriter;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

public class BuildTextSimpleXML extends BuildTextXMLBaseImpl implements BuildTextSimple, Dumpable {

    private String text;

    public BuildTextSimpleXML (CallContext context, Element node, String source_location_info) {
        this(context, node, false, source_location_info);
    }

    public BuildTextSimpleXML (CallContext context, Element node, boolean serialise_node, String source_location_info) {
        super(context, node, source_location_info);

        if (node.hasChildNodes()) {
            if ( ! serialise_node) {
                try {
                    this.text = ((Text) node.getFirstChild()).getData();
                } catch (ClassCastException e) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, FactorySiteStringPool.get(context, "0.1.0" /* Node for SimpleText must be a text node */));
                }
            } else {
                this.text += com.sphenon.basics.xml.XMLUtil.serialiseContent(context, node);
            }
        } else {
            this.text = "";
        }
    }

    public BuildTextSimpleXML (CallContext context, Attr attr, String source_location_info) {
        super(context, null, source_location_info);

        this.node_name = attr.getName();
        this.oid = "";
        this.assign_to = "";
        this.type_name = "";
        this.factory_name = "";
        this.retriever_name = "";
        this.method_name = "";
        this.listener = "";
        this.source_location_info = source_location_info;

        this.text = attr.getValue();
    }

    public String getText (CallContext context) { return this.text; }

    public void setText (CallContext context, String text) { this.text = text; }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextSimple_String";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextSimple_String;
    }

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Text, this.getText(context), true);
    }

    public void dump(CallContext context, DumpNode dump_node) {
        super.dump(context, dump_node);
        if (this.getText(context) != null) {
            dump_node.dump(context, "Text", this.getText(context));
        }
    }
}
