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

public class DIAAssociationEnd extends DIAFeature {

    protected String dia_end_index;

    protected DIAAssociationEnd (CallContext context, XMLNode node, DIADiagram diagram, DIAItem parent, String dia_end_index) {
        super(context, node, null, ItemType.AssociationEnd, diagram, parent);
        this.dia_end_index = dia_end_index;
    }

    public String getId(CallContext context) {
        if (this.id == null && this.node == null && this.dia_end_index != null) {
            this.id = this.parent.getId(context) + "_" + dia_end_index;
        }
        return super.getId(context);
    }

    public void prepareEndName(CallContext context) {
        this.name = this.getRole(context);
        if (this.name == null || this.name.length() == 0) {
            this.name = this.connection.getName(context);
        }
    }

    protected DIAClass dia_class;

    public DIAClass getClass (CallContext context) {
        return this.dia_class;
    }

    public void setClass (CallContext context, DIAClass dia_class) {
        this.dia_class = dia_class;
    }

    protected String role;

    public String getRole(CallContext context) {
        if (this.role == null) {
            if (this.dia_end_index == null) {
                if (this.node != null) {
                    this.role = this.getAttributeString(context, "role");
                }
            } else {
                this.role = this.parent.getAttributeString(context, "role_" + this.dia_end_index);
            }

            // well; this shall be handled by the GROCP Parser via properties/configuration
            // to be checked if it really does, after more stuff is implemented
            // [see also: DIAAssociation]
            // 
            // String[] matches = sttpre.tryGetMatches(context, this.role);
            // if (matches[1] != null && matches[1].length() != 0 && this.stereotype_string == null) {
            //     this.role = matches[0] + matches[2];
            //     this.stereotype_string = matches[1];
            // }
            // matches = idxre.tryGetMatches(context, this.role);
            // if (matches[1] != null && matches[1].length() != 0) {
            //     this.role = (matches[0] == null ? "" : matches[0]) + (matches[2] == null ? "" : matches[2]);
            //     this.index = matches[1];
            // }
            // matches = rore.tryGetMatches(context, this.role);
            // if (matches != null && matches[1] != null && matches[1].length() != 0) {
            //     this.role = ((matches[0] == null ? "" : matches[0]) + (matches[2] == null ? "" : matches[2])).trim();
            //     this.read_only = new Boolean(true);
            // }
        }
        return this.role;
    }

    public void setRole(CallContext context, String role) {
        this.role = role;
    }

    protected String multiplicity;

    public String getMultiplicity(CallContext context) {
        if (this.multiplicity == null) {
            if (this.dia_end_index == null) {
                if (this.node != null) {
                    this.multiplicity = this.getAttributeString(context, "multiplicity");
                }
            } else {
                this.multiplicity = this.parent.getAttributeString(context, "multiplicity_" + this.dia_end_index);
            }
        }
        return this.multiplicity;
    }

    protected Boolean is_navigable;

    public Boolean getIsNavigable(CallContext context) {
        if (this.is_navigable == null) {
            if (this.node != null) {
            }
            if (this.dia_end_index == null) {
                if (this.node != null) {
                    this.is_navigable = new Boolean(this.getAttributeBoolean(context, "arrow"));
                }
            } else {
                this.is_navigable = new Boolean(this.parent.getAttributeBoolean(context, "show_arrow_" + this.dia_end_index));
            }
        }
        return this.is_navigable;
    }

    protected DIAClass connection;

    public DIAClass getConnection(CallContext context) {
        return this.connection;
    }

    public void setConnection(CallContext context, DIAClass connection) {
        this.connection = connection;
    }

    protected Vector<DIAAssociationEnd> other_ends;

    protected void addOtherEnd(CallContext context, DIAAssociationEnd dia_other_end) {
        if (this.other_ends == null) {
            this.other_ends = new Vector<DIAAssociationEnd>();
        }
        this.other_ends.add(dia_other_end);
    }

    protected void setOtherEnds(CallContext context, Vector<DIAAssociationEnd> other_ends) {
        this.other_ends = other_ends;
    }

    public Vector<DIAAssociationEnd> getOtherEnds(CallContext context) {
        return this.other_ends;
    }

    public DIAAssociationEnd getOtherEnd(CallContext context) {
        return this.other_ends == null || this.other_ends.size() == 0 ? null : this.other_ends.get(0);
    }

    public String getOtherEndsName(CallContext context) {
        String oen = "";
        for (DIAAssociationEnd oe : this.getOtherEnds(context)) {
            oen += (oen.length() == 0 ? "" : ",") + oe.getName(context);
        }
        return oen;
    }

    protected BuildText createBuildText(CallContext context, DIAConfiguration pc, String name) {
        return null;
    }
}
