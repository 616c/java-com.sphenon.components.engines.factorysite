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
import com.sphenon.engines.factorysite.tplinst.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.io.StringWriter;

import java.util.HashMap;
import java.util.Vector;

public class BuildTextSwitch_String extends BuildTextBaseImpl implements BuildTextSwitch, Dumpable {

    private Vector_Pair_BuildText_String__long_ cases;

    public BuildTextSwitch_String (CallContext context, String oid, String assign_to, String type_name, String factory_name, String retriever_name, String source_location_info) {
        super(context);
        this.oid = oid;
        this.assign_to = assign_to;
        this.type_name = type_name;
        this.factory_name = factory_name;
        this.retriever_name = retriever_name;
        this.cases = Factory_Vector_Pair_BuildText_String__long_ .construct(context);
        this.source_location_info = source_location_info;
    }

    public BuildTextSwitch_String (CallContext context, String oid, String assign_to, String type_name, String factory_name, String retriever_name, String source_location_info, Pair_BuildText_String_... cases) {
        this(context, oid, assign_to, type_name, factory_name, retriever_name, source_location_info);
        for (Pair_BuildText_String_ a_case : cases) {
            this.cases.append(context, a_case);
        }
    }

    public BuildTextSwitch_String (CallContext context) {
        super(context);
        this.oid = EMPTY;
        this.assign_to = EMPTY;
        this.type_name = EMPTY;
        this.factory_name = EMPTY;
        this.retriever_name = EMPTY;
        this.method_name = EMPTY;
        this.cases = Factory_Vector_Pair_BuildText_String__long_ .construct(context);
        this.source_location_info = EMPTY;
    }

    public Vector_Pair_BuildText_String__long_ getCases (CallContext context) { return this.cases; }

    public void setCases (CallContext context, Vector_Pair_BuildText_String__long_ cases) { this.cases = cases; }

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
