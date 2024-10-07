package com.sphenon.basics.locating.locators;

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
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.javaresources.factories.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.variatives.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;

import java.util.Vector;
import java.util.regex.*;

public class LocatorOCP extends LocatorBuildText {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.locating.locators.LocatorOCP"); };

    static protected Configuration config;
    static { config = Configuration.create(RootContext.getInitialisationContext(), "com.sphenon.basics.locating.locators.LocatorOCP"); };

    public LocatorOCP (CallContext context, String text_locator_value, Locator sub_locator, String locator_class_parameter_string) {
        super(context, text_locator_value, sub_locator, locator_class_parameter_string);
    }

    /* Base Acceptors ------------------------------------------------------------------- */

    static protected Vector<LocatorBaseAcceptor> locator_base_acceptors;

    static protected Vector<LocatorBaseAcceptor> initBaseAcceptors(CallContext context) {
        if (locator_base_acceptors == null) {
            locator_base_acceptors = new Vector<LocatorBaseAcceptor>();
            locator_base_acceptors.add(new LocatorBaseAcceptor(context, java.util.Map.class));
        }
        return locator_base_acceptors;
    }

    protected Vector<LocatorBaseAcceptor> getBaseAcceptors(CallContext context) {
        return initBaseAcceptors(context);
    }

    static public void addBaseAcceptor(CallContext context, LocatorBaseAcceptor base_acceptor) {
        initBaseAcceptors(context).add(base_acceptor);
    }
    
    /* Parser States -------------------------------------------------------------------- */
    
    protected Object retrieveLocalTarget(CallContext context) throws InvalidLocator {
        String ocpid = "[Locator: " + this.text_locator_value + "]";

        Object base = lookupBaseObject(context, false);

        BuildText bt = (BuildText) super.retrieveLocalTarget(context);

        try {
            FactorySite fs = new FactorySiteTextBased(context, bt, ocpid);
            return fs.build(context, (java.util.Map) base);
        } catch (PutUpFailure puf) {
            InvalidLocator.createAndThrow(context, puf, "Invalid OCP Locator '%(locator)'", "locator", this.text_locator_value);
            throw (InvalidLocator) null;
        } catch (BuildFailure bf) {
            InvalidLocator.createAndThrow(context, bf, "Invalid OCP Locator '%(locator)'", "locator", this.text_locator_value);
            throw (InvalidLocator) null;
        }
    }
}
