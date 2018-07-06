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

import java.util.HashMap;
import java.util.Vector;

import java.io.StringWriter;

public class BuildTextComplex_String extends BuildTextBaseImpl implements BuildTextComplex, Dumpable {

    private String component_type;
    private Vector_Pair_BuildText_String__long_ named_items;

    public BuildTextComplex_String (CallContext context, String node_name, String oid, String assign_to, String type_name, String factory_name, String retriever_name, String source_location_info, Vector_Pair_BuildText_String__long_ named_items) {
        super(context);
        this.node_name = node_name;
        this.oid = oid;
        this.assign_to = assign_to;
        this.type_name = type_name;
        this.factory_name = factory_name;
        this.retriever_name = retriever_name;
        this.component_type = null;
        this.named_items = named_items;
        this.source_location_info = source_location_info;
    }

    public BuildTextComplex_String (CallContext context, String oid, String assign_to, String type_name, String factory_name, String retriever_name, String source_location_info, Vector_Pair_BuildText_String__long_ named_items) {
        this(context, null, oid, assign_to, type_name, factory_name, retriever_name, source_location_info, named_items);
    }

    public BuildTextComplex_String (CallContext context, String node_name, String oid, String assign_to, String type_name, String factory_name, String retriever_name, String source_location_info) {
        this(context, node_name, oid, assign_to, type_name, factory_name, retriever_name, source_location_info, Factory_Vector_Pair_BuildText_String__long_.construct(context));
    }

    public BuildTextComplex_String (CallContext context, String oid, String assign_to, String type_name, String factory_name, String retriever_name, String source_location_info) {
        this(context, null, oid, assign_to, type_name, factory_name, retriever_name, source_location_info);
    }

    public BuildTextComplex_String (CallContext context, String oid, String assign_to, String type_name, String factory_name, String retriever_name, String source_location_info, Pair_BuildText_String_... childs) {
        this(context, oid, assign_to, type_name, factory_name, retriever_name, source_location_info);
        for (Pair_BuildText_String_ child : childs) {
            this.named_items.append(context, child);
        }
    }

    public BuildTextComplex_String (CallContext context, String node_name, String oid, String assign_to, String type_name, String factory_name, String retriever_name, String source_location_info, Pair_BuildText_String_... childs) {
        this(context, oid, assign_to, type_name, factory_name, retriever_name, source_location_info, childs);
        this.node_name = node_name;
    }

    public BuildTextComplex_String (CallContext context) {
        super(context);
        this.oid = EMPTY;
        this.assign_to = EMPTY;
        this.type_name = EMPTY;
        this.factory_name = EMPTY;
        this.retriever_name = EMPTY;
        this.method_name = EMPTY;
        this.named_items = Factory_Vector_Pair_BuildText_String__long_ .construct(context);
        this.source_location_info = EMPTY;
    }

    public String getComponentType(CallContext context) { return this.component_type; }

    public void setComponentType(CallContext context, String component_type) { this.component_type = component_type; }

    public Vector_Pair_BuildText_String__long_ getItems (CallContext context) { return this.named_items; }

    public void setItems (CallContext context, Vector_Pair_BuildText_String__long_ items) { this.named_items = items; }

    public void addItem (CallContext context, String name, BuildText item) {
        if (this.named_items == null) {
            this.named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);
        }
        int pos = name.indexOf('/');
        if (pos == -1) {
            this.named_items.append(context, new Pair_BuildText_String_(context, item, name));
        } else {
            String first = name.substring(0, pos);
            String rest  = name.substring(pos+1);
            BuildTextComplex_String btc = null;
            if (first.startsWith("!")) {
                first = first.substring(1);
                for (Pair_BuildText_String_ pbts : this.named_items.getIterable(context)) {
                    if (first.equals(pbts.getItem2(context))) {
                        btc = (BuildTextComplex_String) pbts.getItem1(context);
                    }
                }
            }
            if (btc == null) {
                btc = new BuildTextComplex_String(context);
                btc.setNodeName(context, first);
                this.named_items.append(context, new Pair_BuildText_String_(context, btc, first));
            }
            btc.addItem(context, rest, item);
        }
    }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextComplex_String";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextComplex_String;
    }

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_ComponentType, this.getComponentType(context), true);
    }

    public void dump(CallContext context, DumpNode dump_node) {
        super.dump(context, dump_node);
        if (this.getComponentType(context) != null) {
            dump_node.dump(context, "ComponentType", this.getComponentType(context));
        }
        DumpNode dn = dump_node.openDump(context, "Items");
        for (Pair_BuildText_String_ item : this.getItems(context).getIterable_Pair_BuildText_String__(context)) {
            dn.dump(context, item.getItem2(context), item.getItem1(context));
        }
    }
}
