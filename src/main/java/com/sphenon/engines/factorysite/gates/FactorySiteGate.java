package com.sphenon.engines.factorysite.gates;

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
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.factory.returncodes.*;
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.returncodes.*;
import java.lang.reflect.*;

public class FactorySiteGate {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.gates.FactorySiteGate"); };

    public static Object createObject (CallContext context, String target_class) throws InvalidCTN {
        return createObject(context, target_class, new java.util.Hashtable());
    }

    static protected RegularExpression ctn_re = new RegularExpression("^(?:ctn|oorl)://Class<([A-Za-z0-9]+)>/([A-Za-z0-9_/;=<>.-]+)$");
    static protected RegularExpression method_re = new RegularExpression("^(.*)/method=([A-Za-z0-9_.]+)$");

    public static Object createObject (CallContext context, String target_class, java.util.Map parameters) throws InvalidCTN {
        return createObject(context, target_class, parameters, false);
    }

    public static Object createObject (CallContext context, String target_class, boolean try_factory_site, Object... parameters) throws InvalidCTN {
        return createObject(context, target_class, Factory_Aggregate.makeMap(context, false, parameters), try_factory_site);
    }

    public static Object createObject (CallContext call_context, String target_class, java.util.Map parameters, boolean try_factory_site) throws InvalidCTN {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        String class_type;
        String class_id;
        String[] generic_arguments = null;

        String[] matches = ctn_re.tryGetMatches(context, target_class);
        if (matches == null) {
            if (try_factory_site) {
                class_type  = "Aggregate";
                class_id    = target_class;
            } else {
                InvalidCTN.createAndThrow(context, "Invalid ctn (no match): %(ctn) (expected: 'ctn://Class<Java|Aggregate>/...')", "ctn", target_class);
                throw (InvalidCTN) null; // compiler insists
            }
        } else {
            class_type  = matches[0];
            class_id    = matches[1];
        }
            
        boolean deprecated_warning = false;
        Object result = null;
        if (class_type.equals("Aggregate") || (deprecated_warning = class_type.equals("Chunk"))) {
            if (deprecated_warning) {
                if ((notification_level & Notifier.OBSERVATION) != 0) { cc.sendNotice(context, "Use of 'Chunk' is deprecated '%(class)'", "class", target_class); }
            }
            Factory_Aggregate cf = new Factory_Aggregate(context);
            cf.setAggregateClass(context, class_id);
            cf.setParameters(context, parameters);
            try {
                cf.validateAggregateClass(context);
                cf.validateParameters(context);
            } catch (ValidationFailure vf) {
                InvalidCTN.createAndThrow(context, vf, "Aggregate creation of '%(class)' failed", "class", target_class);
                throw (InvalidCTN) null; // compiler insists
            }
            result = cf.create(context);
        } else if (class_type.equals("Java")) {
            class_id = class_id.replace('/','.');
            int pos = -1;
            if (    class_id.isEmpty() == false
                 && (pos = class_id.indexOf('<')) != -1
                 && class_id.charAt(class_id.length() - 1) == '>') {
                String genargs = class_id.substring(pos+1, class_id.length() - 1);
                generic_arguments = genargs.split(",");
                class_id = class_id.substring(0, pos);
                System.err.println("CHECK ME (FactorySiteGate.java):");
                System.err.println("         - " + class_type);
                System.err.println("         - " + class_id);
                System.err.println("         - " + genargs);
                System.err.println("AND REMOVE print IF OK");
            }

            String[] mm = method_re.tryGetMatches(context, class_id);
            String method = null;
            if (mm != null) {
                class_id = mm[0];
                method = mm[1];
            }

            try {
                if (method != null) {
                    result = ReflectionUtilities.invoke(context, class_id, method, null, context);
                } else {
                    result = ReflectionUtilities.newInstance(context, true, class_id);
                }
            } catch (Throwable t) {
                InvalidCTN.createAndThrow(context, t, "Invalid oorl: %(oorl)", "oorl", target_class);
                throw (InvalidCTN) null; // compiler insists
            }

        } else {
            InvalidCTN.createAndThrow(context, "Invalid ctn: %(ctn) (expected: 'ctn://Class<Java|Aggregate>/...')", "ctn", target_class);
            throw (InvalidCTN) null; // compiler insists
        }
        if (    generic_arguments != null
             && generic_arguments.length != 0
             && result instanceof GenericWithRunTimeTypes) {
            com.sphenon.basics.metadata.Type[] rtts = new com.sphenon.basics.metadata.Type[generic_arguments.length];
            int t=0;
            for (String generic_argument : generic_arguments) {
                try {
                    rtts[t++] = TypeManager.get(context, generic_argument);
                } catch (com.sphenon.basics.metadata.returncodes.NoSuchClass nsc) {
                    InvalidCTN.createAndThrow(context, nsc, "object of type '%(type)' cannot be delivered, the generic runtime type '%(runtimetype)' does not exist", "type", target_class, "runtimetype", generic_argument);
                    throw (InvalidCTN) null; // compiler insists
                }
            }
            ((GenericWithRunTimeTypes) result).setRuntimeTypes(context, rtts);
        }
        return result;
    }
}
