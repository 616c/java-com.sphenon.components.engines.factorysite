package com.sphenon.engines.factorysite.diagram;

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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.debug.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.monitoring.*;
import com.sphenon.basics.accessory.classes.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.xml.*;
import com.sphenon.basics.xml.returncodes.*;
import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.tplinst.*;

import static com.sphenon.basics.system.StringUtilities.isEmpty;
import static com.sphenon.basics.system.StringUtilities.isNotEmpty;

import java.util.*;

public class DIAOperation extends DIAFeature {

    protected DIAOperation (CallContext context, XMLNode node, DIADiagram diagram, DIAItem parent) {
        super(context, node, null, ItemType.Operation, diagram, parent);
    }

    protected BuildText createBuildText(CallContext context, DIAConfiguration pc, String name) {
        DIAConfiguration dc = DIAConfiguration.cloneLocalConfiguration(context, this.getLocalConfiguration(context), pc);

        BuildTextComplex_String result = new BuildTextComplex_String(context);

        this.applyOCPProperties(context, dc, result, name);

        // String operation_name = this.getName(context);
        // String operation_name_attribute = this.getProperty(context, dc, null, OCPProperty.AttributeName, ItemProperty.OperationName, (String) null);
        // if (isNotEmpty(operation_name) && isNotEmpty(operation_name_attribute)) {
        //     BuildTextSimple_String bts = new BuildTextSimple_String(context, /* oid */ null, /* assign_to */ null, /* type_name */ "java.lang.String", /* factory_name */ null, /* retriever_name */ null, /* text */ operation_name, this.getSourceInfo(context));
        //     result.addItem(context, operation_name_attribute, bts);
        // }

        // String operation_type = this.getType(context);
        // String operation_type_attribute = this.getProperty(context, dc, null, OCPProperty.AttributeName, ItemProperty.OperationType, (String) null);
        // if (isNotEmpty(operation_type) && isNotEmpty(operation_type_attribute)) {
        //     BuildTextSimple_String bts = new BuildTextSimple_String(context, /* oid */ null, /* assign_to */ null, /* type_name */ "java.lang.String", /* factory_name */ null, /* retriever_name */ null, /* text */ operation_type, this.getSourceInfo(context));
        //     result.addItem(context, operation_type_attribute, bts);
        // }

        return result;
    }
}
