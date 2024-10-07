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
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.expression.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.io.StringWriter;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.text.*;

public class BuildTextSwitchYAML extends BuildTextYAMLBaseImpl implements BuildTextSwitch, Dumpable {

    protected Vector_Pair_BuildText_String__long_ cases;

    public BuildTextSwitchYAML (CallContext context, Object node, BuildTextYAMLMetainfo meta, String source_location_info, BuildTextYAMLFactory bt_factory) throws InvalidDocument {
        super(context, node, meta, source_location_info);

        cases = Factory_Vector_Pair_BuildText_String__long_.construct(context);

        if (node instanceof Map) {
            Map map = (Map) node;
            java.util.Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                Object child = map.get(key);

                BuildTextYAMLMetainfo child_meta = null;
                if (key instanceof BuildTextYAMLMetainfo) {
                    if (child instanceof BuildTextYAMLMetainfo) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, "Meta information in a map can only be provided either at key or at value, but not on both sides (OCP/YAML file '%(info)')", "info", source_location_info);
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                    child_meta = (BuildTextYAMLMetainfo) key;
                } else if (child instanceof BuildTextYAMLMetainfo) {
                    child_meta = (BuildTextYAMLMetainfo) child;
                    if (child_meta.getName() != null) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, "Meta information in a map provided at value must not specify a name (OCP/YAML file '%(info)')", "info", source_location_info);
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                    if ((key instanceof String) == false) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, "A key in a map of OCP/YAML file '%(info)' must be either a String or of type 'BuildTextYAMLMetainfo'", "info", source_location_info);
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                    child_meta.setName((String) key);
                    child = child_meta.getValue();
                } else if (key instanceof String) {
                    child_meta = new BuildTextYAMLMetainfo((String) key);
                } else {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "A key in a map of OCP/YAML file '%(info)' must be either a String or of type 'BuildTextYAMLMetainfo'", "info", source_location_info);
                    throw (ExceptionConfigurationError) null; // compiler insists
                }

                BuildText btchild = bt_factory.create(context, child, child_meta, source_location_info);
                if (btchild.getTypeName(context) == null || btchild.getTypeName(context).length() == 0) {
                    btchild.setTypeName(context, this.getTypeName(context));
                }
                if (btchild.getFactoryName(context) == null || btchild.getFactoryName(context).length() == 0) {
                    btchild.setFactoryName(context, this.getFactoryName(context));
                }
                if (btchild.getRetrieverName(context) == null || btchild.getRetrieverName(context).length() == 0) {
                    btchild.setRetrieverName(context, this.getRetrieverName(context));
                }
                if (btchild.getMethodName(context) == null || btchild.getMethodName(context).length() == 0) {
                    btchild.setMethodName(context, this.getMethodName(context));
                }

                cases.append(context, new Pair_BuildText_String_(context, btchild, btchild.getNodeName(context)));
            }
        }

        if (node instanceof List) {
            List list = (List) node;
            int i=0;
            java.util.Iterator<Object> elements = list.iterator();
            while (elements.hasNext()) {
                String id = "" + i;
                Object child = elements.next();

                String child_name = id;
                BuildTextYAMLMetainfo child_meta = null;
                if (child instanceof BuildTextYAMLMetainfo) {
                    child_meta = (BuildTextYAMLMetainfo) child;
                    child = child_meta.getValue();
                    if (this.isNonEmpty(context, child_meta.getName())) {
                        child_name = child_meta.getName();
                    }
                } else {
                    child_meta = new BuildTextYAMLMetainfo(id);
                }

                BuildText btchild = bt_factory.create(context, child, child_meta, source_location_info);

                if (btchild.getTypeName(context) == null || btchild.getTypeName(context).length() == 0) {
                    btchild.setTypeName(context, this.getTypeName(context));
                }
                if (btchild.getFactoryName(context) == null || btchild.getFactoryName(context).length() == 0) {
                    btchild.setFactoryName(context, this.getFactoryName(context));
                }
                if (btchild.getRetrieverName(context) == null || btchild.getRetrieverName(context).length() == 0) {
                    btchild.setRetrieverName(context, this.getRetrieverName(context));
                }
                if (btchild.getMethodName(context) == null || btchild.getMethodName(context).length() == 0) {
                    btchild.setMethodName(context, this.getMethodName(context));
                }

                cases.append(context, new Pair_BuildText_String_(context, btchild, btchild.getNodeName(context)));
            }
        }
    }

    public Vector_Pair_BuildText_String__long_ getCases (CallContext context) {
        return this.cases;
    }

    public String getCOCPCodeClass(CallContext context) {
        return "COCPBuildTextSwitch_String";
    }

    public int getCOCPCodeClassIndex(CallContext context) {
        return COCPIndices.COCPBuildTextSwitch_String;
    }

    protected void printSpecificCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder, String indent, String site_id, String dotid) {
    }

    public void dump(CallContext context, DumpNode dump_node) {
        super.dump(context, dump_node);
    }
}
