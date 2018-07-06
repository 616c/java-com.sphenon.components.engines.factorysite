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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.xml.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.io.StringWriter;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

public class BuildTextDOMXML extends BuildTextXMLBaseImpl implements BuildTextDOM, Dumpable {

    public BuildTextDOMXML (CallContext context, Element node, String source_location_info) {
        super(context, node, source_location_info);
    }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextDOM_Node";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextDOM_Node;
    }

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
        if (this.getNode(context) != null) {
            String xml = XMLUtil.serialiseContent(context, this.getNode(context));
            this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_XMLNode, xml, true);
        }
    }

    public void dump(CallContext context, DumpNode dump_node) {
        super.dump(context, dump_node);
        if (this.getNode(context) != null) {
            dump_node.dump(context, "Node", "<DOM Tree>" /* this.getNode(context) */);
        }
    }
}
