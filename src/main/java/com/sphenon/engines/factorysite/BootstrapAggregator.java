package com.sphenon.engines.factorysite;

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
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.engines.aggregator.*;

import com.sphenon.engines.factorysite.factories.*;

public class BootstrapAggregator extends Aggregator {

    protected BootstrapAggregator(CallContext context) {
        super(context);
    }

    static public void install(CallContext context) {
        if (singleton == null) {
            singleton = new BootstrapAggregator(context);
        }
    }

    protected Object doCreate(CallContext context, String aggregate_class) {
        return Factory_Aggregate.construct(context, aggregate_class);
    }
}
