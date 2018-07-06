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
import com.sphenon.basics.many.*;
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

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

abstract public class DIAItem {
    static final public Class _class = DIAItem.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    protected XMLNode node;

    static boolean debug = true; 

    protected DIADiagram diagram;
    protected DIAItem parent;

    public DIAItem getParent(CallContext context) {
        return this.parent;
    }

    protected XMLNode layer_node;
    protected String[] layers;

    protected ItemType item_type;

    public ItemType getItemType(CallContext context) {
        return this.item_type;
    }

    public String getSourceInfo(CallContext context) {
        String dsi = this.diagram.getSourceInfo(context);
        String id = this.node == null ? null : this.node.getAttribute(context, "id");
        String name = this.getAttributeString(context, "name");
        String type = this.node == null ? null : this.node.getAttribute(context, "type");

        return dsi + (id == null ? "" : ("/#" + id)) + (name == null ? "" : ("/" + name)) + (type == null ? "" : ("[" + type + "]"));
    }

    protected DIAConfiguration local_configuration;

    protected DIAConfiguration getLocalConfiguration(CallContext context) {
        if (this.local_configuration == null) {
            String configuration_source = getLocalConfigurationSource(context);
            this.local_configuration = DIAConfiguration.processConfiguration(context, configuration_source, this);
        }

        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) {
            NotificationContext.sendSelfDiagnostics(context, "Local configuration for '%(iteminfo)' is:", "iteminfo", this.getSourceInfo(context));
            Dumper.dump(context, "Local configuration", this.local_configuration);
            NotificationContext.sendSelfDiagnostics(context, "Local properties for '%(iteminfo)' is:", "iteminfo", this.getSourceInfo(context));
            Dumper.dump(context, "Local properties", this.value_map);
        }

        return this.local_configuration;
    }

    protected String getLocalConfigurationSource(CallContext context) {
        return getComment(context);
    }

    protected DIAItem (CallContext context, XMLNode node, String[] layers, ItemType item_type, DIADiagram diagram, DIAItem parent) {
        this.node              = node;
        this.layers            = layers;
        this.item_type         = item_type;
        this.diagram           = diagram;
        this.parent            = parent;
    }

    // =======================================================================
    // Properties

    public String getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, OCPProperty ocpp, String default_value) {
        return getProperty(context, lc, pc, ocpp, default_value, true, true);
    }

    public String getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, OCPProperty ocpp, String default_value, boolean use_settings, boolean use_rules) {
        DIAConfiguration dc = lc != null ? lc : DIAConfiguration.cloneLocalConfiguration(context, this.getLocalConfiguration(context), pc);
        String value = getProperty(context, dc, use_settings, use_rules, ocpp);
        String result = value == null ? default_value : value;

        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendSelfDiagnostics(context, "Property '%(property)' (%({'do not use settings','use settings'}[use_settings]), %({'do not use rules','use rules'}[use_rules])) for '%(iteminfo)' is: '%(value)'", "iteminfo", this.getSourceInfo(context), "property", ocpp.toString(), "use_settings", use_settings, "use_rules", use_rules, "value", result); }
        return result;
    }

    // .............

    public boolean getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, OCPProperty ocpp, boolean default_value) {
        return getProperty(context, lc, pc, ocpp, default_value, true, true);
    }

    public boolean getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, OCPProperty ocpp, boolean default_value, boolean use_settings, boolean use_rules) {
        DIAConfiguration dc = lc != null ? lc : DIAConfiguration.cloneLocalConfiguration(context, this.getLocalConfiguration(context), pc);
        String value = getProperty(context, dc, use_settings, use_rules, ocpp);
        boolean result = value == null ? default_value : new Boolean(value);

        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendSelfDiagnostics(context, "Property '%(property)' (%({'do not use settings','use settings'}[use_settings]), %({'do not use rules','use rules'}[use_rules])) for '%(iteminfo)' is: '%(value)'", "iteminfo", this.getSourceInfo(context), "property", ocpp.toString(), "use_settings", use_settings, "use_rules", use_rules, "value", result); }
        return result;
    }

    // .............

    public String getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, OCPProperty ocpp, ItemProperty item_property, String default_value) {
        return getProperty(context, lc, pc, ocpp, item_property, default_value, true, true);
    }

    public String getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, OCPProperty ocpp, ItemProperty item_property, String default_value, boolean use_settings, boolean use_rules) {
        DIAConfiguration dc = lc != null ? lc : DIAConfiguration.cloneLocalConfiguration(context, this.getLocalConfiguration(context), pc);
        String value = getProperty(context, dc, use_settings, use_rules, ocpp, item_property);
        String result = value == null ? default_value : value;

        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendSelfDiagnostics(context, "Property '%(property)' (%({'do not use settings','use settings'}[use_settings]), %({'do not use rules','use rules'}[use_rules])) for '%(iteminfo)' is: '%(value)'", "iteminfo", this.getSourceInfo(context), "property", ocpp.toString() + "/" + item_property.toString(), "use_settings", use_settings, "use_rules", use_rules, "value", result); }
        return result;
    }

    // .............

    public boolean getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, OCPProperty ocpp, ItemProperty item_property, boolean default_value) {
        return getProperty(context, lc, pc, ocpp, item_property, default_value, true, true);
    }

    public boolean getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, OCPProperty ocpp, ItemProperty item_property, boolean default_value, boolean use_settings, boolean use_rules) {
        DIAConfiguration dc = lc != null ? lc : DIAConfiguration.cloneLocalConfiguration(context, this.getLocalConfiguration(context), pc);
        String value = getProperty(context, dc, use_settings, use_rules, ocpp, item_property);
        boolean result = value == null ? default_value : new Boolean(value);

        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendSelfDiagnostics(context, "Property '%(property)' (%({'do not use settings','use settings'}[use_settings]), %({'do not use rules','use rules'}[use_rules])) for '%(iteminfo)' is: '%(value)'", "iteminfo", this.getSourceInfo(context), "property", ocpp.toString() + "/" + item_property.toString(), "use_settings", use_settings, "use_rules", use_rules, "value", result); }
        return result;
    }

    // .............

    public String getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, ItemRole role, String default_value) {
        return getProperty(context, lc, pc, role, default_value, true, true);
    }

    public String getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, ItemRole role, String default_value, boolean use_settings, boolean use_rules) {
        DIAConfiguration dc = lc != null ? lc : DIAConfiguration.cloneLocalConfiguration(context, this.getLocalConfiguration(context), pc);
        String value = getProperty(context, dc, use_settings, use_rules, role);
        String result = value == null ? default_value : value;

        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendSelfDiagnostics(context, "Property '%(property)' (%({'do not use settings','use settings'}[use_settings]), %({'do not use rules','use rules'}[use_rules])) for '%(iteminfo)' is: '%(value)'", "iteminfo", this.getSourceInfo(context), "property", role.toString(), "use_settings", use_settings, "use_rules", use_rules, "value", result); }
        return result;
    }

    // .............

    public boolean getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, ItemRole role, boolean default_value) {
        return getProperty(context, lc, pc, role, default_value, true, true);
    }

    public boolean getProperty(CallContext context, DIAConfiguration lc, DIAConfiguration pc, ItemRole role, boolean default_value, boolean use_settings, boolean use_rules) {
        DIAConfiguration dc = lc != null ? lc : DIAConfiguration.cloneLocalConfiguration(context, this.getLocalConfiguration(context), pc);
        String value = getProperty(context, dc, use_settings, use_rules, role);
        boolean result = value == null ? default_value : new Boolean(value);

        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendSelfDiagnostics(context, "Property '%(property)' (%({'do not use settings','use settings'}[use_settings]), %({'do not use rules','use rules'}[use_rules])) for '%(iteminfo)' is: '%(value)'", "iteminfo", this.getSourceInfo(context), "property", role.toString(), "use_settings", use_settings, "use_rules", use_rules, "value", result); }
        return result;
    }

    // -----------------------------------------------------------------------

    protected Map<Tuple,String> value_map;

    protected String getProperty(CallContext context, DIAConfiguration dc, boolean use_settings, boolean use_rules, OCPProperty ocpp) {
        return getProperty(context, dc, use_settings, use_rules, new Tuple(context, ocpp), new Tuple(context, ocpp, this.getItemType(context)));
    }

    protected String getProperty(CallContext context, DIAConfiguration dc, boolean use_settings, boolean use_rules, OCPProperty ocpp, ItemProperty item_property) {
        return getProperty(context, dc, use_settings, use_rules, new Tuple(context, ocpp, item_property), new Tuple(context, ocpp, this.getItemType(context), item_property));
    }

    protected String getProperty(CallContext context, DIAConfiguration dc, boolean use_settings, boolean use_rules, ItemRole role) {
        return getProperty(context, dc, use_settings, use_rules, new Tuple(context, role), new Tuple(context, role, this.getItemType(context)));
    }

    // -----------------------------------------------------------------------

    protected String getProperty(CallContext context, DIAConfiguration dc, boolean use_settings, boolean use_rules, Tuple key, Tuple ckey) {
        String value = null;
        if (value_map != null && use_settings) {
            value = value_map.get(key);
        }
        if (value == null && use_rules) {
            value = dc.getProperty(context, ckey);
        }
        return value;
    }

    // -----------------------------------------------------------------------

    public void setProperty(CallContext context, String name, String value) {
        ItemRole role = null;
        OCPProperty ocpp = null;
        if (name != null && name.startsWith("@")) {
            role = ItemRole.valueOf(name.substring(1));
        } else {
            ocpp = OCPProperty.valueOf(name);
        }

        if (value_map == null) {
            value_map = new HashMap<Tuple,String>();
        }

        Tuple key = null;

        if (ocpp != null) {
            key = new Tuple(context, ocpp);
        }

        if (role != null) {
            key = new Tuple(context, role);
        }

        value_map.put(key, value);
    }

    // =======================================================================

    protected void applyOCPProperties(CallContext context, DIAConfiguration dc, BuildText bt, String name_from_context) {
        if (bt == null) { return; }

        String namespace_bt = this.getProperty(context, dc, null, OCPProperty.NameSpace, (String) null);
        if (namespace_bt != null && ! namespace_bt.isEmpty()) {
            bt.setNameSpace(context, this.evaluate(context, namespace_bt));
        }

        String node_name;
        if (    isNotEmpty(node_name = this.getProperty(context, dc, null, OCPProperty.NodeName, (String) null, true, false))
             || isNotEmpty(node_name = name_from_context)
             || isNotEmpty(node_name = this.getProperty(context, dc, null, OCPProperty.NodeName, (String) null, false, true))
           ) {
            bt.setNodeName(context, this.evaluate(context, node_name));
        }

        String type_name;
        if (isNotEmpty(type_name = this.getProperty(context, dc, null, OCPProperty.Class, (String) null))) {
            bt.setTypeName(context, this.evaluate(context, type_name));
        }
    }

    // =======================================================================

    static final protected String dia = "http://www.lysator.liu.se/~alla/dia/";

    static protected NamedRegularExpressionFilter[] diagram_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:diagram", null
        // "xml:nsuri", dia, null
        );

    static protected NamedRegularExpressionFilter[] layer_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:layer", null,
        // "xml:nsuri", dia, null,
        "name", null, null
        );

    protected NamedRegularExpressionFilter[] getLayerFilter(CallContext context) {
        NamedRegularExpressionFilter[] lf = new NamedRegularExpressionFilter[layer_filter.length];
        for (int i=0; i<layer_filter.length; i++) {
            lf[i] = layer_filter[i];
        }
        String lnre = StringUtilities.join(context, this.layers, "^(?:", ")|(?:", ")$", true);
        lf[1] = new NamedRegularExpressionFilter(context, "name", lnre, null);
        return lf;
    }

    static protected NamedRegularExpressionFilter[] class_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:object", null,
        // "xml:nsuri", dia, null,
        "type", "^UML - Class$", null
        );

    static protected NamedRegularExpressionFilter[] small_package_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:object", null,
        // "xml:nsuri", dia, null,
        "type", "^UML - SmallPackage$", null
        );

    static protected NamedRegularExpressionFilter[] state_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:object", null,
        // "xml:nsuri", dia, null,
        "type", "^UML - (?:State|State Term|Branch)$", null
        );

    static protected NamedRegularExpressionFilter[] association_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:object", null,
        // "xml:nsuri", dia, null,
        "type", "^UML - Association$", null
        );

    static protected NamedRegularExpressionFilter[] generalization_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:object", null,
        // "xml:nsuri", dia, null,
        "type", "^UML - Generalization$", null
        );

    static protected NamedRegularExpressionFilter[] transition_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:object", null,
        // "xml:nsuri", dia, null,
        "type", "^UML - Transition$", null
        );

    static protected NamedRegularExpressionFilter[] extension_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:object", null,
        // "xml:nsuri", dia, null,
        "type", "^XM.*$", null
        );

    static protected NamedRegularExpressionFilter[] view_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:object", null,
        // "xml:nsuri", dia, null,
        "type", "^ObjectView$", null
        );

    static protected NamedRegularExpressionFilter[] import_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:object", null,
        // "xml:nsuri", dia, null,
        "type", "^Imports$", null
        );

    static protected NamedRegularExpressionFilter[] constraint_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:object", null,
        // "xml:nsuri", dia, null,
        "type", "^UML - Constraint$", null
        );

    static protected NamedRegularExpressionFilter[] attribute_filter_1 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:attribute", null,
        // "xml:nsuri", dia, null,
        "name", "^attributes$", null
        );

    static protected NamedRegularExpressionFilter[] attribute_filter_2 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:composite", null,
        // "xml:nsuri", dia, null,
        "type", "^umlattribute$", null
        );

    static protected NamedRegularExpressionFilter[] property_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:attribute", null,
        // "xml:nsuri", dia, null,
        "name", "custom:.*", null
        );

    static protected NamedRegularExpressionFilter[] string_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:string", null
        // "xml:nsuri", dia, null,
        );

    static protected NamedRegularExpressionFilter[] operation_filter_1 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:attribute", null,
        // "xml:nsuri", dia, null,
        "name", "^operations$", null
        );

    static protected NamedRegularExpressionFilter[] operation_filter_2 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:composite", null,
        // "xml:nsuri", dia, null,
        "type", "^umloperation$", null
        );

    static protected NamedRegularExpressionFilter[] parameter_filter_1 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:attribute", null,
        // "xml:nsuri", dia, null,
        "name", "^parameters$", null
        );

    static protected NamedRegularExpressionFilter[] parameter_filter_2 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:composite", null,
        // "xml:nsuri", dia, null,
        "type", "^umlparameter$", null
        );

    static protected NamedRegularExpressionFilter[] connection_filter_1 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:connections", null
        // "xml:nsuri", dia, null,
        );

    static protected NamedRegularExpressionFilter[] connection_filter_2 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:connection", null
        // "xml:nsuri", dia, null,
        );

    static protected NamedRegularExpressionFilter[] connection_h0_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:connection", null,
        // "xml:nsuri", dia, null,
        "handle", "^0$", null
        );

    static protected NamedRegularExpressionFilter[] connection_h1_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:connection", null,
        // "xml:nsuri", dia, null,
        "handle", "^1$", null
        );

    static protected NamedRegularExpressionFilter[] end_filter_1 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:attribute", null,
        // "xml:nsuri", dia, null,
        "name", "^ends$", null
        );

    static protected NamedRegularExpressionFilter[] end_filter_2 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:composite", null
        // "xml:nsuri", dia, null,
        );

    static protected NamedRegularExpressionFilter[] viewname_filter_1 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:attribute", null,
        // "xml:nsuri", dia, null,
        "name", "^text$", null
        );

    static protected NamedRegularExpressionFilter[] viewname_filter_2 = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:composite", null,
        // "xml:nsuri", dia, null,
        "type", "^text$", null
        );

    static protected NamedRegularExpressionFilter[] attribute_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:attribute", null,
        // "xml:nsuri", dia, null,
        "name", null, null
        );

    static protected NamedRegularExpressionFilter[] composite_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:composite", null,
        // "xml:nsuri", dia, null,
        "type", null, null
        );

    static protected NamedRegularExpressionFilter[] attribute_string_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:string", null
        // "xml:nsuri", dia, null,
        );

    static protected NamedRegularExpressionFilter[] attribute_enum_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:enum", null
        // "xml:nsuri", dia, null,
        );

    static protected NamedRegularExpressionFilter[] attribute_boolean_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:boolean", null
        // "xml:nsuri", dia, null,
        );

    static protected NamedRegularExpressionFilter[] attribute_font_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:font", null
        // "xml:nsuri", dia, null,
        );

    static protected NamedRegularExpressionFilter[] note_filter = NamedRegularExpressionFilter.createArray(
        "xml:name", "dia:object", null,
        // "xml:nsuri", dia, null,
        "type", "^UML - Note$", null
        );

    protected NamedRegularExpressionFilter[] getAttributeFilter(CallContext context, String name) {
        NamedRegularExpressionFilter[] af = new NamedRegularExpressionFilter[attribute_filter.length];
        for (int i=0; i<attribute_filter.length; i++) {
            af[i] = attribute_filter[i];
        }
        af[1] = new NamedRegularExpressionFilter(context, "name", "^" + name + "$", null);
        return af;
    }

    protected NamedRegularExpressionFilter[] getCompositeFilter(CallContext context, String type) {
        NamedRegularExpressionFilter[] cf = new NamedRegularExpressionFilter[composite_filter.length];
        for (int i=0; i<composite_filter.length; i++) {
            cf[i] = composite_filter[i];
        }
        cf[1] = new NamedRegularExpressionFilter(context, "type", "^" + type + "$", null);
        return cf;
    }

    public String getAttributeString(CallContext context, String name) {
        return getAttributeString(context, name, this.node);
    }

    public String getAttributeEnum(CallContext context, String name) {
        return getAttributeEnum(context, name, this.node);
    }

    public String getAttributeBoolean(CallContext context, String name) {
        return getAttributeBoolean(context, name, this.node);
    }

    public String getAttributeFontStyle(CallContext context, String name) {
        return getAttributeFontStyle(context, name, this.node);
    }

    protected String getAttributeString(CallContext context, String name, XMLNode my_node) {
        return Encoding.recode(context, my_node.getChildElementsByFilters(context, this.getAttributeFilter(context, name), attribute_string_filter).toString(context).replaceFirst("(?s)^#(.*)#$","$1"), Encoding.XML, Encoding.UTF8);
    }

    protected String getAttributeEnum(CallContext context, String name, XMLNode my_node) {
        return Encoding.recode(context, my_node.getChildElementsByFilters(context, this.getAttributeFilter(context, name), attribute_enum_filter).getAttribute(context, "val").replaceFirst("(?s)^#(.*)#$","$1"), Encoding.XML, Encoding.UTF8);
    }

    protected String getAttributeBoolean(CallContext context, String name, XMLNode my_node) {
        return Encoding.recode(context, my_node.getChildElementsByFilters(context, this.getAttributeFilter(context, name), attribute_boolean_filter).getAttribute(context, "val").replaceFirst("(?s)^#(.*)#$","$1"), Encoding.XML, Encoding.UTF8);
    }

    protected String getAttributeFontStyle(CallContext context, String name, XMLNode my_node) {
        return Encoding.recode(context, my_node.getChildElementsByFilters(context, this.getAttributeFilter(context, name), attribute_font_filter).getAttribute(context, "style"), Encoding.XML, Encoding.UTF8);
    }

    // State name incl. state type
    static protected RegularExpression stnare = new RegularExpression("\\s*([A-Za-z0-9_]+)\\s*(?:\\[\\s*(None|Ready|Active|CompletedReadyAgain|Finished)\\s*\\]\\s*)?");

    // Stereotype name
    static protected RegularExpression sttpre = new RegularExpression("([^«]*)(?:«([^»]+)»(.*))?");

    protected String id;

    public String getId(CallContext context) {
        if (this.id == null) {
            if (this.node != null) {
                this.id = this.node.getAttribute(context, "id");
            } else {
                this.diagram.addProblemStatus(context, ProblemState.ERROR, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA diagram '%(diainfo)' contains item '%(iteminfo)' of type '%(itemtype)' with no node", "diainfo", this.diagram.getSourceInfo(context), "iteminfo", this.getSourceInfo(context), "itemtype", this.item_type));
                this.id = "[? no node ?]";
            }
        }
        return this.id;
    }

    protected String name;

    public String getName(CallContext context) {
        if (this.name == null) {
            if (this.node != null) {
                this.name = retrieveName(context);
            } else {
                this.diagram.addProblemStatus(context, ProblemState.ERROR, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA diagram '%(diainfo)' contains item '%(iteminfo)' of type '%(itemtype)' with no node", "diainfo", this.diagram.getSourceInfo(context), "iteminfo", this.getSourceInfo(context), "itemtype", this.item_type));
                this.name = "[? no node ?]";
            }
        }
        return this.name;
    }

    protected String retrieveName(CallContext context) {
        return this.getAttributeString(context, "name");
    }

    protected String stereotype_string;
    protected String[] stereotypes;

    public String[] getStereotypes(CallContext context) {
        if (this.stereotypes == null) {
            if (this.node != null) {
                String stps = this.getAttributeString(context, "stereotype");

                // *** reactivate this ***
                // *** reactivate this ***
                // *** reactivate this ***
                // if (stps == null || stps.length() == 0) {
                //     this.getRole(context);
                //     stps = this.stereotype_string;
                // }
                // if (stps == null || stps.length() == 0) {
                //     this.getComment(context);
                //     stps = this.stereotype_string;
                // }
                // *** reactivate this ***
                // *** reactivate this ***
                // *** reactivate this ***

                if (stps == null || stps.length() == 0) {
                    this.getName(context);
                    stps = this.stereotype_string;
                }
                this.stereotypes = (stps != null && stps.length() != 0 ? stps.split(",") : new String[0]);
            }
        }
        return this.stereotypes;
    }

    public String getText(CallContext context) {
        return Encoding.recode(context, this.node.getChildElementsByFilters(context, this.getAttributeFilter(context, "text"), this.getCompositeFilter(context, "text"), this.getAttributeFilter(context, "string"), attribute_string_filter).toString(context).replaceFirst("(?s)^#(.*)#$","$1"), Encoding.XML, Encoding.UTF8);
    }

    protected String type;
    public String getType(CallContext context) {
        if (this.type == null) {
            if (this.node != null) {
                this.type = this.getAttributeString(context, "type");
            }
        }
        return this.type;
    }

    protected String value;
    public String getValue(CallContext context) {
        if (this.value == null) {
            if (this.node != null) {
                this.value = this.getAttributeString(context, "value");
            }
        }
        return this.value;
    }

    protected String comment;
    public String getComment(CallContext context) {
        if (this.comment == null) {
            if (this.node != null) {
                this.comment = this.getAttributeString(context, "comment");
 
                // well; this shall be handled by the GROCP Parser via properties/configuration
                // to be checked if it really does, after more stuff is implemented
                // [see also: DIAAssociation]
                // 
                // this.getRole(context);
                //  String[] matches = sttpre.tryGetMatches(context, this.comment);
                //  if (matches != null && matches[1] != null && matches[1].length() != 0 && this.stereotype_string == null) {
                //      this.comment = matches[0] + matches[2];
                //      this.stereotype_string = matches[1];
                //  }
                //  matches = idxre.tryGetMatches(context, this.comment);
                //  if (matches != null && matches[1] != null && matches[1].length() != 0) {
                //      this.comment = (matches[0] == null ? "" : matches[0]) + (matches[2] == null ? "" : matches[2]);
                //      this.index = matches[1];
                //  }
                //  matches = rore.tryGetMatches(context, this.comment);
                //  if (matches != null && matches[1] != null && matches[1].length() != 0) {
                //      this.comment = (matches[0] == null ? "" : matches[0]) + (matches[2] == null ? "" : matches[2]);
                //      this.read_only = new Boolean(true);
                //  }
            }
        }

        return this.comment;
    }

    abstract protected BuildText createBuildText(CallContext context, DIAConfiguration pc, String name);

    public String evaluate(CallContext context, String string) {
        return string;
    }
}
