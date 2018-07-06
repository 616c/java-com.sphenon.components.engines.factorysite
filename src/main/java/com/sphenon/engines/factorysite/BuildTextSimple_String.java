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

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.basics.encoding.*;

import java.io.StringWriter;

import java.util.HashMap;
import java.util.Vector;

public class BuildTextSimple_String extends BuildTextBaseImpl implements BuildTextSimple, Dumpable {

    private String text;

    public BuildTextSimple_String (CallContext context) {
        super(context);
    }

    public BuildTextSimple_String (CallContext context, String oid, String assign_to, String type_name, String factory_name, String retriever_name, String text, String source_location_info) {
        super(context);
        this.oid = oid;
        this.assign_to = assign_to;
        this.type_name = type_name;
        this.factory_name = factory_name;
        this.retriever_name = retriever_name;
        this.text = text;
        this.source_location_info = source_location_info;
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
