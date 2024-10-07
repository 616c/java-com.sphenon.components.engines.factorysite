package com.sphenon.engines.factorysite.yaml;

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
import com.sphenon.formats.yaml.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.io.StringWriter;

import java.util.HashMap;
import java.util.Vector;

public class BuildTextYAMLRawYAML extends BuildTextYAMLBaseImpl implements BuildTextYAMLRaw, Dumpable {

    public BuildTextYAMLRawYAML (CallContext context, Object node, BuildTextYAMLMetainfo meta, String source_location_info) {
        super(context, node, meta, source_location_info);
    }

    public BuildTextYAMLRawYAML (CallContext context, String oid, String assign_to, String type_name, String factory_name, String retriever_name, Object node, BuildTextYAMLMetainfo meta, String source_location_info) {
        super(context, node, meta, source_location_info);
        this.oid = oid;
        this.assign_to = assign_to;
        this.type_name = type_name;
        this.factory_name = factory_name;
        this.retriever_name = retriever_name;
        this.source_location_info = source_location_info;
    }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextYAMLRaw_Node";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextYAMLRaw_Node;
    }

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
        if (this.getNode(context) != null) {
            String yaml = BuildTextYAMLFactory.getText(context, this.getNode(context));
            this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_YAMLNode, yaml, true);
        }
    }

    public void dump(CallContext context, DumpNode dump_node) {
        super.dump(context, dump_node);
        if (this.getNode(context) != null) {
            dump_node.dump(context, "Node", "<YAML Tree>" /* this.getNode(context) */);
        }
    }
}
