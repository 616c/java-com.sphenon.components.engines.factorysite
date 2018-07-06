package com.sphenon.engines.factorysite.diagram;

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

import java.util.*;

public class DIATransition extends DIAItem {

    protected DIATransition (CallContext context, XMLNode node, DIADiagram diagram, DIAItem parent) {
        super(context, node, null, ItemType.Transition, diagram, parent);
    }

    protected String retrieveName(CallContext context) {
        String n = this.getAttributeString(context, "trigger", this.node);
        String[] matches = sttpre.tryGetMatches(context, n);
        if (matches[1] != null && matches[1].length() != 0 && this.stereotype_string == null) {
            n = matches[0] + matches[2];
            this.stereotype_string = matches[1];
        }
        return n;
    }

    public String[] getStereotypes(CallContext context) {
        if (this.stereotypes == null) {
            this.getName(context);
        }
        return super.getStereotypes(context);
    }

    protected BuildText createBuildText(CallContext context, DIAConfiguration pc, String name) {
        return null;
    }
}
