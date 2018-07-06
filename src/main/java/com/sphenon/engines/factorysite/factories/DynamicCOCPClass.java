package com.sphenon.engines.factorysite.factories;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.javacode.*;
import com.sphenon.basics.javacode.classes.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.graph.tplinst.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import com.sphenon.basics.configuration.annotations.*;

public class DynamicCOCPClass extends Class_DynamicClass<COCPBuildTextFactory> {

    // Configuration ------------------------------------------------------------------

    @Configuration public interface Config {
        @DefaultValue("true")
        boolean getDoGeneration(CallContext context);
        @DefaultValue("true")
        boolean getDoCompilation(CallContext context);
        @DefaultValue("false")
        boolean getTryToLoadAsResource(CallContext context);
        @DefaultValue("false")
        boolean getUseExistingResourceUnconditionally(CallContext context);
    }
    static public Config config = Configuration_DynamicCOCPClass.get(RootContext.getInitialisationContext());

    // Construction -------------------------------------------------------------------

    public DynamicCOCPClass(CallContext context, String full_class_name, FactorySite factory_site, ReadOnlyVector_TreeNode_long_ ocp_tnodes_to_check) {
        super(context, full_class_name, COCPBuildTextFactory.class);

        this.factory_site = factory_site;
        this.ocp_tnodes_to_check = ocp_tnodes_to_check;
    }

    // Overloads ------------------------------------------------------------------

    protected FactorySite                   factory_site;
    protected ReadOnlyVector_TreeNode_long_ ocp_tnodes_to_check;

    protected boolean doAutoGeneration(CallContext context) {
        return true;
    }

    protected boolean doGeneration(CallContext context) {
        return DynamicCOCPClass.config.getDoGeneration(context);
    }

    protected boolean doCompilation(CallContext context) {
        return DynamicCOCPClass.config.getDoCompilation(context);
    }

    protected boolean tryToLoadAsResource(CallContext context) {
        return DynamicCOCPClass.config.getTryToLoadAsResource(context);
    }

    protected boolean useExistingResourceUnconditionally(CallContext context) {
        return DynamicCOCPClass.config.getUseExistingResourceUnconditionally(context);
    }

    protected String getAdditionalMetaData(CallContext context) {
        return "";
    }

    protected String getCodeGeneratorPackage(CallContext context) {
        return "com.sphenon.engines.factorysite";
    }

    protected long getLastModificationOfCodeGeneratorSource(CallContext context) {
        long actual_last_modification = (this.factory_site == null ? -1 : this.factory_site.getLastModification(context));
        for (long i=0; i<this.ocp_tnodes_to_check.getSize(context); i++) {
            long lm = this.ocp_tnodes_to_check.tryGet(context, i).getLastModification(context);
            if (lm > actual_last_modification) { actual_last_modification = lm; }
        }
        return actual_last_modification;
    }

    protected long getLastModificationOfCodeGeneratorConfiguration(CallContext context) {
        return -1;
    }

    protected void generateCode(CallContext context) {
        // noop
    }

    protected String[] additional_relative_data_resources;

    protected String[] getAdditionalRelativeDataResources(CallContext context) {
        if (this.additional_relative_data_resources == null) {
            String cocp_resource = full_class_name.replaceFirst(".*\\.", "") + ".cocp";
            this.additional_relative_data_resources = new String[] { cocp_resource };
        }
        return this.additional_relative_data_resources;
    }
}
