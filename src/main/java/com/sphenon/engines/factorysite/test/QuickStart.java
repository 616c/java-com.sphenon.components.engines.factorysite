package com.sphenon.engines.factorysite.test;

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
        System.err.println("(you find this in com.sphenon.engines.factorysite.test.QuickStart.java)");
        System.err.println("");

        // optional: in case you want to provide configuration via command line
        Configuration.checkCommandLineArgs(args);

        System.err.println("   initialising services...");
        System.err.println("");

        // required: sphenon library needs a context
        Context context = com.sphenon.basics.context.classes.RootContext.getRootContext();
        // required: and needs to be initialised
        Configuration.initialise(context);

        Object result = null;

        try {

            System.err.println("   setting up factory to load OCP...");

            // our test: setting up factory for aggregates
            Factory_Aggregate fa = new Factory_Aggregate(context);

            System.err.println("   specifying QuickStart OCP...");
            System.err.println("   (you find this in com.sphenon.engines.objectassembler.QuickStart.ocp)");

            // the aggregate we want to create
            // (it is found in the jar at the given location, named QuickStart.ocp
            fa.setAggregateClass(context, "com/sphenon/engines/objectassembler/QuickStart");

            // in case we want to provide parameters
            Hashtable parameters = new Hashtable();
            fa.setParameters(context, parameters);

            System.err.println("   actually processing OCP now...");

            // and go...
            result = fa.create(context);
        } catch (ExceptionError e) {
            NotificationContext.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
            return;
        }

        if ( ! (result instanceof Hashtable)) {
            System.err.println("   Assertion failed: result is not a Hashtable");
        } else if ( ! "hello".equals(((Hashtable) result).get("Message"))) {
            System.err.println("   Assertion failed: entry 'Message' in result is not 'hello'");
        } else if ( ! "world".equals(((Hashtable) result).get("Planet"))) {
            System.err.println("   Assertion failed: entry 'Planet' in result is not 'world'");
        } else {
            System.err.println("   (result looks good, all tests passed successfully)");
        }
    }
}
