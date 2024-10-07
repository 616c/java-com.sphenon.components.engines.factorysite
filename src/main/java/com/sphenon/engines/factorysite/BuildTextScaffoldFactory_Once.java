package com.sphenon.engines.factorysite;

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
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.engines.factorysite.returncodes.*;

public class BuildTextScaffoldFactory_Once implements BuildTextScaffoldFactory {
    protected Scaffold scaffold;
    public BuildTextScaffoldFactory_Once(CallContext context, Scaffold scaffold) {
        this.scaffold = scaffold;
    }
    public Scaffold createScaffold(CallContext context, FactorySite factory_site) throws InvalidFactory {
        if (this.scaffold == null) {
            CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, "BuildTextScaffoldFactory_Once already used");
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
        }
        Scaffold s = this.scaffold;
        this.scaffold = null;
        ((ScaffoldGeneric_BaseImpl) s).setFactorySite(context, factory_site);
        return s;
    }
}
