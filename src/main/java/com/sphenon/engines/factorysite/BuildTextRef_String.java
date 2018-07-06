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

public class BuildTextRef_String extends BuildTextBaseImpl implements BuildTextRef, Dumpable {

//     private String text;

    public BuildTextRef_String (CallContext context) {
        super(context);
    }

    public BuildTextRef_String (CallContext context, String oid, String assign_to, String type_name, String oid_ref, String source_location_info) {
        super(context);
        this.oid = oid;
        this.assign_to = assign_to;
        this.type_name = type_name;
        this.factory_name = EMPTY;
        this.retriever_name = EMPTY;
//         this.text = text;
        this.source_location_info = source_location_info;
        this.oid_ref = oid_ref;
    }

    protected String oid_ref;

    public String getOIDRef (CallContext context) {
        return this.oid_ref;
    }

    public void setOIDRef (CallContext context, String oid_ref) {
        this.oid_ref = oid_ref;
    }

//     public String getText (CallContext context) { return this.text; }

//     public void setText (CallContext context, String text) { this.text = text; }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextRef_String";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextRef_String;
    }

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_OIDRef, this.getOIDRef(context), false);
    }

    public void dump(CallContext context, DumpNode dump_node) {
        super.dump(context, dump_node);
        if (this.getOIDRef(context) != null) {
            dump_node.dump(context, "OIDRef", this.getOIDRef(context));
        }
    }
}
