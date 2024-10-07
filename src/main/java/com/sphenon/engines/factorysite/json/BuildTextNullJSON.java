package com.sphenon.engines.factorysite.json;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

public class BuildTextNullJSON extends BuildTextJSONBaseImpl implements BuildTextNull {

    public BuildTextNullJSON (CallContext context, JsonNode node, String name, String source_location_info) {
        super(context, node, name, source_location_info);

        if (this.factory_name != null && this.factory_name.length() != 0) {
            NotificationContext.sendNotice(context, FactorySiteStringPool.get(context, "0.9.2" /* NULL node has FACTORY attribute ('%(factory)'), which is ignored */), "factory", this.factory_name);
        }
        if (this.retriever_name != null && this.retriever_name.length() != 0) {
            NotificationContext.sendNotice(context, FactorySiteStringPool.get(context, "0.9.3" /* NULL node has RETRIEVER attribute ('%(retriever)'), which is ignored */), "retriever", this.retriever_name);
        }
        if (this.oid_ref != null && this.oid_ref.length() != 0) {
            NotificationContext.sendNotice(context, FactorySiteStringPool.get(context, "0.9.4" /* NULL node has OIDREF attribute ('%(oidref)'), which is ignored */), "oidref", this.oid_ref);
        }
    }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextNull_String";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextNull_String;
    }
}
