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
import com.sphenon.basics.data.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.xml.*;
import com.sphenon.basics.xml.returncodes.*;
import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.grocp.*;

import java.util.*;
import java.io.*;

public class DIAConfiguration implements Dumpable {

    protected DIAConfiguration (CallContext context, String name_space, String source_info) {
        this.name_space  = name_space;
        this.source_info = source_info;
        this.parent      = null;
    }

    protected DIAConfiguration (CallContext context) {
        this.name_space  = null;
        this.source_info = null;
        this.parent      = null;
    }

    protected DIAConfiguration (CallContext context, DIAConfiguration local, DIAConfiguration parent) {
        this.value_map   = local.value_map;
        this.parent      = parent;
    }

    protected String name_space;
    protected String source_info;

    protected DIAConfiguration parent;

    static public DIAConfiguration retrieveConfiguration(CallContext context, String name_space, String source_info) {
        DIAConfiguration dia_configuration = new DIAConfiguration (context, name_space, source_info);
        
        if (name_space != null) {
            String path = name_space.replaceAll("/([0-9.]+)(?=/|$)","-$1");
            OCPFinder ocp_finder = new OCPFinder(context, path, null, null, null);
            try {
                ocp_finder.findOCP(context, "grocp.cfg");
            } catch (ValidationFailure vf) {
                CustomaryContext.create((Context)context).throwVerificationFailure(context, vf, "While parsing GROCPConfiguration '%(info)', defining resource '%(path)' for namespace '%(namespace)' not found", "path", path, "namespace", name_space, "info", source_info);
                throw (ExceptionVerificationFailure) null; // compiler insists
            }
            if (ocp_finder.found == false) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, "While parsing GROCPConfiguration '%(info)', defining resource '%(path)' for namespace '%(namespace)' not found", "path", path, "namespace", name_space, "info", source_info);
                throw (ExceptionConfigurationError) null; // compiler insists
            }
            Data_MediaObject data = ((Data_MediaObject)(((NodeContent_Data)(((TreeLeaf) ocp_finder.result_node).getContent(context))).getData(context)));
            InputStream stream = data.getStream(context);
            try {
                GROCPConfigurationParser.retrieveGROCPConfiguration(context, name_space, dia_configuration, null);
            } catch (ParseException pe) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, pe, "Parsing GROCPConfiguration '%(info)' from resource '%(path)' for namespace '%(namespace)' failed", "path", path, "namespace", name_space, "info", source_info);
                throw (ExceptionConfigurationError) null; // compiler insists
            }
        }
        return dia_configuration;
    }

    static public DIAConfiguration processConfiguration(CallContext context, String source, DIAItem dia_item) {
        DIAConfiguration dia_configuration = new DIAConfiguration(context);
        
        if (source != null && ! source.isEmpty()) {
            try {
                GROCPConfigurationParser.processGROCPConfiguration(context, source, dia_configuration, dia_item);
            } catch (ParseException pe) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, pe, "Parsing GROCPConfiguration from comment failed");
                throw (ExceptionConfigurationError) null; // compiler insists
            }
        }
        return dia_configuration;
    }

    static public DIAConfiguration cloneLocalConfiguration(CallContext context, DIAConfiguration local, DIAConfiguration parent) {
        return new DIAConfiguration(context, local, parent);
    }

    // =======================================================================
    // Properties

    public String getProperty(CallContext context, OCPProperty ocpp, ItemType item_type, String default_value) {
        String value = getProperty(context, ocpp, item_type);
        return value == null ? default_value : value;
    }

    public boolean getProperty(CallContext context, OCPProperty ocpp, ItemType item_type, boolean default_value) {
        String value = getProperty(context, ocpp, item_type);
        return value == null ? default_value : new Boolean(value);
    }

    public String getProperty(CallContext context, OCPProperty ocpp, ItemType item_type, ItemProperty item_property, String default_value) {
        String value = getProperty(context, ocpp, item_type, item_property);
        return value == null ? default_value : value;
    }

    public boolean getProperty(CallContext context, OCPProperty ocpp, ItemType item_type, ItemProperty item_property, boolean default_value) {
        String value = getProperty(context, ocpp, item_type, item_property);
        return value == null ? default_value : new Boolean(value);
    }

    public String getProperty(CallContext context, ItemRole role, ItemType item_type, String default_value) {
        String value = getProperty(context, role, item_type);
        return value == null ? default_value : value;
    }

    public boolean getProperty(CallContext context, ItemRole role, ItemType item_type, boolean default_value) {
        String value = getProperty(context, role, item_type);
        return value == null ? default_value : new Boolean(value);
    }

    // -----------------------------------------------------------------------

    protected Map<Tuple,String> value_map;

    protected String getProperty(CallContext context, OCPProperty ocpp, ItemType item_type) {
        return getProperty(context, new Tuple(context, ocpp, item_type));
    }

    protected String getProperty(CallContext context, OCPProperty ocpp, ItemType item_type, ItemProperty item_property) {
        return getProperty(context, new Tuple(context, ocpp, item_type, item_property));
    }

    protected String getProperty(CallContext context, ItemRole role, ItemType item_type) {
        return getProperty(context, new Tuple(context, role, item_type));
    }

    // -----------------------------------------------------------------------

    public String getProperty(CallContext context, Tuple key) {
        String value = null;
        if (value_map != null) {
            value = value_map.get(key);
        }
        if (value == null && this.parent != null) {
            value = this.parent.getProperty(context, key);
        }
        return value;
    }

    // -----------------------------------------------------------------------

    public void setProperty(CallContext context, String name, List<String> keys, String value) {
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
            switch (ocpp) {
                case NameSpace :
                case NodeName  :
                case Class  :
                    key = new Tuple(context, ocpp, ItemType.valueOf(keys.get(0)));
                    break;
                case AttributeName :
                    key = new Tuple(context, ocpp, ItemType.valueOf(keys.get(0)), ItemProperty.valueOf(keys.get(1)));
                    break;
                default:
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "While parsing GROCPConfiguration '%(info)', an invalid configuration entry was encountered '%(name)'", "info", source_info, "name", name);
                    throw (ExceptionConfigurationError) null; // compiler insists
            }
        }

        if (role != null) {
            switch (role) {
                case Root :
                case Attribute :
                    key = new Tuple(context, role, ItemType.valueOf(keys.get(0)));
                    break;
                default:
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "While parsing GROCPConfiguration '%(info)', an invalid configuration entry was encountered '%(name)'", "info", source_info, "name", name);
                    throw (ExceptionConfigurationError) null; // compiler insists
            }
        }

        value_map.put(key, value);
    }

    // =======================================================================

    public void dump(CallContext context, DumpNode dump_node) {
        if (this.value_map != null && this.value_map.size() != 0) {
            for (Tuple key : this.value_map.keySet()) {
                String value = this.value_map.get(key);
                dump_node.dump(context, key.toString(context), value);
            }
        }
    }
}
