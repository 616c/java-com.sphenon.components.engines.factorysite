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

import static com.sphenon.basics.system.StringUtilities.isEmpty;
import static com.sphenon.basics.system.StringUtilities.isNotEmpty;

import java.util.*;

public class DIAClass extends DIAItem {

    protected DIAClass (CallContext context, XMLNode node, DIADiagram diagram, DIAItem parent) {
        super(context, node, null, ItemType.Class, diagram, parent);
    }

    protected Vector<DIAAttribute> attributes;

    public Vector<DIAAttribute> getAttributes(CallContext context) {
        if (this.attributes == null) {
            this.attributes = new Vector<DIAAttribute>();
            if (this.node != null) {

                for (XMLNode attribute_node : this.node.getChildElementsByFilters(context, attribute_filter_1, attribute_filter_2).getNodes(context)) {
                    DIAAttribute dia_attribute = new DIAAttribute(context, attribute_node, this.diagram, this);
                    this.attributes.add(dia_attribute);

                    String attribute_name = dia_attribute.getName(context);
                    this.addFeature(context, dia_attribute, attribute_name);

                    // if (this.feature_extensions_map != null) {
                    //     Vector<DIAAttribute> feature_extensions = this.feature_extensions_map.get(attribute_name);
                    //     if (feature_extensions != null) {
                    //         dia_attribute.setExtensions(context, feature_extensions);
                    //     }
                    // }
                }
            }
        }

        return this.attributes;
    }

    protected Vector<DIAAssociationEnd> association_ends;

    protected void addAssociationEnd(CallContext context, DIAAssociationEnd dia_association_end) {
        if (this.association_ends == null) {
            this.association_ends = new Vector<DIAAssociationEnd>();
        }
        this.association_ends.add(dia_association_end);
        this.addFeature(context, dia_association_end, ">" + dia_association_end.getName(context));

        // dia_association_end.parent = this; // naja, naja - der hat 2 parents
        // machen wir hier jetzt so nicht mehr, parent bleibt associaction
        // stattdessen:
        dia_association_end.setClass(context, this);
    }

    protected void setAssociationEnds(CallContext context, Vector<DIAAssociationEnd> association_ends) {
        this.association_ends = association_ends;
    }

    public Vector<DIAAssociationEnd> getAssociationEnds(CallContext context) {
        return this.association_ends;
    }

    protected Vector<DIAOperation> operations;

    public Vector<DIAOperation> getOperations(CallContext context) {
        if (this.operations == null) {
            this.operations = new Vector<DIAOperation>();

            if (this.node != null) {
                for (XMLNode operation_node : this.node.getChildElementsByFilters(context, operation_filter_1, operation_filter_2).getNodes(context)) {
                    DIAOperation dia_operation = new DIAOperation(context, operation_node, this.diagram, this);
                    this.operations.add(dia_operation);

                    String operation_name = dia_operation.getName(context);
                    this.addFeature(context, dia_operation, operation_name + "()");
                    
                    // if (this.feature_extensions_map != null) {
                    //     Vector<DIAOperation> feature_extensions = this.feature_extensions_map.get(operation_name);
                    //     if (feature_extensions != null) {
                    //         operation_utility.setExtensions(context, feature_extensions);
                    //     }
                    // }
                }
            }
        }
        return this.operations;
    }

    protected HashMap<String,DIAFeature> features_by_id;

    protected void addFeature(CallContext context, DIAFeature feature, String alternate_id) {
        if (this.features_by_id == null) {
            this.features_by_id = new HashMap<String,DIAFeature>();
        }
        this.features_by_id.put(feature.getId(context), feature);
        if (alternate_id != null) {
            this.features_by_id.put(alternate_id, feature);
        }
    }

    protected DIAFeature getFeature(CallContext context, String id) {
        this.getAttributes(context);
        this.getOperations(context);
        if (this.features_by_id == null) { return null; }
        return this.features_by_id.get(id);
    }

    protected BuildText createBuildText(CallContext context, DIAConfiguration pc, String name) {
        DIAConfiguration dc = DIAConfiguration.cloneLocalConfiguration(context, this.getLocalConfiguration(context), pc);

        BuildTextComplex_String result = new BuildTextComplex_String(context);

        this.applyOCPProperties(context, dc, result, name);

        String class_name = this.getName(context);
        String class_name_attribute = this.getProperty(context, dc, null, OCPProperty.AttributeName, ItemProperty.ClassName, (String) null);
        if (isNotEmpty(class_name) && isNotEmpty(class_name_attribute)) {
            BuildTextSimple_String bts = new BuildTextSimple_String(context, /* oid */ null, /* assign_to */ null, /* type_name */ "java.lang.String", /* factory_name */ null, /* retriever_name */ null, /* text */ class_name, this.getSourceInfo(context));
            result.addItem(context, class_name_attribute, bts);
        }

        String attribute_attribute  = this.getProperty(context, dc, null, OCPProperty.AttributeName, ItemProperty.Attribute, (String) null);
        if (isNotEmpty(attribute_attribute)) {
            for (DIAAttribute dia_attribute : this.getAttributes(context)) {
                BuildText attribute_bt = dia_attribute.createBuildText(context, dc, null);
                String an = dia_attribute.getName(context);
                String a = attribute_attribute.replaceAll("\\{attribute_name\\}", an);
                result.addItem(context, a, attribute_bt);
            }
        }

        if (this.getAssociationEnds(context) != null) {
            for (DIAAssociationEnd dia_association_end : this.getAssociationEnds(context)) {
                DIAAssociation    dia_association           = (DIAAssociation) dia_association_end.getParent(context);
                DIAAssociationEnd dia_other_association_end = dia_association_end.getOtherEnd(context);
                DIAClass          dia_other_class           = dia_other_association_end.getClass(context);

                if (dia_other_association_end.getIsNavigable(context)) {
                    String attribute_name = dia_other_class.getName(context);

                    BuildText class_bt = dia_other_class.createBuildText(context, dc, null);
                    result.addItem(context, attribute_name, class_bt);
                }
            }
        }

        return result;
    }
}
