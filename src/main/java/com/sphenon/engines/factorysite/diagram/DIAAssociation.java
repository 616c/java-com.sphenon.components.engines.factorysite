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

public class DIAAssociation extends DIAItem {

    protected DIAAssociation (CallContext context, XMLNode node, DIADiagram diagram, DIAItem parent) {
        super(context, node, null, ItemType.Association, diagram, parent);
    }

    protected Vector<DIAAssociationEnd> ends;

    public Vector<DIAAssociationEnd> getEnds(CallContext context) {
        if (this.ends == null) {
            this.ends = new Vector<DIAAssociationEnd>();
            if (this.node != null) {
                Vector<String> connection_ids = new Vector<String>();
                for (XMLNode connection : this.node.getChildElementsByFilters(context, connection_filter_1, connection_filter_2).getNodes(context)) {
                    connection_ids.add(connection.getAttribute(context, "to"));
                }
                Iterator<String> connection_id_iterator = connection_ids.iterator();

                String version = this.node.getAttribute(context, "version");
                if (version != null && version.equals("2")) {
                    DIAAssociationEnd dia_end_a = new DIAAssociationEnd(context, null, this.diagram, this, "a");
                    DIAAssociationEnd dia_end_b = new DIAAssociationEnd(context, null, this.diagram, this, "b");
                    this.addClassToEnd(context, dia_end_a, connection_id_iterator);
                    this.addClassToEnd(context, dia_end_b, connection_id_iterator);
                } else {
                    for (XMLNode end_node : this.node.getChildElementsByFilters(context, end_filter_1, end_filter_2).getNodes(context)) {
                        DIAAssociationEnd dia_end = new DIAAssociationEnd(context, end_node, this.diagram, this, null);
                        this.addClassToEnd(context, dia_end, connection_id_iterator);
                    }
                }
                for (DIAAssociationEnd dia_end : this.ends) {
                    for (DIAAssociationEnd dia_other_end : this.ends) {
                        if (dia_end != dia_other_end) {
                            dia_end.addOtherEnd(context, dia_other_end);
                        }
                    }
                }
            }
        }
        return this.ends;
    }

    protected void addClassToEnd(CallContext context, DIAAssociationEnd dia_end, Iterator<String> connection_id_iterator) {
        // parent wird in addAssociationEnd gesetzt (naja, naja - ggf. brauchen die ends 2 parents)
        DIAClass dia_target_class = connection_id_iterator.hasNext() ? this.diagram.getClassById(context, connection_id_iterator.next()) : null;
                   
        if (dia_target_class == null) {
            this.diagram.addProblemStatus(context, ProblemState.ERROR, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA diagram '%(diainfo)' contains association '%(iteminfo)' with loose association end", "diainfo", this.diagram.getSourceInfo(context), "iteminfo", this.getSourceInfo(context)));
        } else {
            this.ends.add(dia_end);
                        
            dia_end.setConnection(context, dia_target_class);
            dia_end.prepareEndName(context);
        }
    }

    protected BuildText createBuildText(CallContext context, DIAConfiguration pc, String name) {
        return null;
    }
}
