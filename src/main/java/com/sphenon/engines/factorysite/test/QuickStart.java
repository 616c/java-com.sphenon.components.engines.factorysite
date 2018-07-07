package com.sphenon.engines.factorysite.test;

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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;

import java.util.Hashtable;

public class QuickStart {

    public static void main(String[] args) {

        System.err.println("ObjectAssembler - QuickStart");

        // optional: in case you want to provide configuration via command line
        Configuration.checkCommandLineArgs(args);

        System.err.println("   initialising...");

        // required: sphenon library needs a context
        Context context = com.sphenon.basics.context.classes.RootContext.getRootContext();
        // required: and needs to be initialised
        Configuration.initialise(context);

        Object result = null;

        try {

            System.err.println("   setting up factory...");

            // our test: setting up factory for aggregates
            Factory_Aggregate fa = new Factory_Aggregate(context);

            // the aggregate we want to create
            // (it is found in the jar at the given location, named QuickStart.ocp
            fa.setAggregateClass(context, "com/sphenon/engines/objectassembler/QuickStart");

            // in case we want to provide parameters
            Hashtable parameters = new Hashtable();
            fa.setParameters(context, parameters);

            System.err.println("   processing quick start OCP...");

            // and go...
            result = fa.create(context);
        } catch (ExceptionError e) {
            NotificationContext.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
            return;
        }
    }

}

