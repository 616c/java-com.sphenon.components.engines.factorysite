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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

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

public class BuildTextParameterXML extends BuildTextXMLBaseImpl implements BuildTextParameter, Dumpable {

    private String name;

    public BuildTextParameterXML (CallContext context, Element node, String source_location_info) {
        super(context, node, source_location_info);
        this.name = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.PARAMETER, BuildTextKeywords.PARAMETER_UC);
        String optname = node.getAttribute(BuildTextKeywords.OPTIONALPARAMETER);
        if ((this.name == null || this.name.length() == 0) && optname != null && optname.length() != 0) {
            this.name = optname;
        }
    }

    public String getName (CallContext context) { return this.name; }

    public void setName (CallContext context, String name) { this.name = name; }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextParameter_String";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextParameter_String;
    }

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_Name, this.getName(context), true);
    }

    public void dump(CallContext context, DumpNode dump_node) {
        super.dump(context, dump_node);
        if (this.getName(context) != null) {
            dump_node.dump(context, "Name", this.getName(context));
        }
    }
}
