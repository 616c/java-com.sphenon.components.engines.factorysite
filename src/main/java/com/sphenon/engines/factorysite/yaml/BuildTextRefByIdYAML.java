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

public class BuildTextRefByIdYAML extends BuildTextYAMLBaseImpl implements BuildTextRefById, Dumpable {

    protected String id_ref;

    protected Object node;

    public BuildTextRefByIdYAML (CallContext context, Object node, BuildTextYAMLMetainfo meta, String source_location_info) {
        super(context, node, meta, source_location_info);

        this.node = node;

        this.id_ref = (node instanceof BuildTextYAMLDatainfo ? ((BuildTextYAMLDatainfo) node).getIdRef() : null);
        String optidref = meta.getOptionalIdRef();
        if ((this.id_ref == null || this.id_ref.length() == 0) && optidref != null && optidref.length() != 0) {
            this.id_ref = optidref;
        }

        if (this.factory_name != null && this.factory_name.length() != 0) {
            NotificationContext.sendNotice(context, "IDREF node has FACTORY attribute ('%(factory)'), which is ignored", "factory", this.factory_name);
        }
        if (this.retriever_name != null && this.retriever_name.length() != 0) {
            NotificationContext.sendNotice(context, "IDREF node has RETRIEVER attribute ('%(retriever)'), which is ignored", "retriever", this.retriever_name);
        }
    }

    public String getIdRef (CallContext context) { return this.id_ref; }

    public void setIdRef (CallContext context, String oid_ref) { this.id_ref = id_ref; }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextRefById_String";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextRefById_String;
    }

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
        this.printCOCPString(context, sw, dr, coder, COCPIndices.BuildText_IdRef, this.getIdRef(context), false);
    }

    public void dump(CallContext context, DumpNode dump_node) {
        super.dump(context, dump_node);
        if (this.getIdRef(context) != null) {
            dump_node.dump(context, "IdRef", this.getIdRef(context));
        }
    }
}
