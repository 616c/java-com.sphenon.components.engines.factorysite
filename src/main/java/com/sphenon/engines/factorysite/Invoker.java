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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.debug.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.factory.returncodes.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.lang.reflect.*;

public class Invoker {

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }


    public static Object executeCommand(String command) {
        return main(command.split(" "), false);
    }

    public static void main(String[] args) {
        Object result = main(args, true);
        if (result instanceof Throwable) {
            ((Throwable)result).printStackTrace();
            System.out.println("Invocation failed.\n");
        } else {
            Dumper.dump(RootContext.getRootContext (), "Result: ", result);
        }
    }

    public static Object main(String[] args, boolean initialise) {

        Context context = RootContext.getRootContext ();

        if (initialise) {
            Configuration.checkCommandLineArgs(args);
            Configuration.initialise(context);

            // do it here, AFTER config variant setting
            notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.Invoker");
        }

        CustomaryContext cc = CustomaryContext.create(context);

        String class_name  = null;
        String method_name = "execute";

        RegularExpression class_re  = new RegularExpression("(?:(?:--class)|(?:-c))=(.*)");
        RegularExpression method_re = new RegularExpression("(?:(?:--method)|(?:-m))=(.*)");
        RegularExpression argpfx_re = new RegularExpression("(?:(?:--argument)|(?:-a)).*");
        RegularExpression arg_re    = new RegularExpression("(?:(?:(?:--argument)|(?:-a))[=:])?([A-Za-z0-9_]*)(?:\\(([^\\)]*)\\))?(?:([=:])(.*))?");

        String[] matches = null;
        for (String arg : args) {
            if ((matches = class_re.tryGetMatches(context, arg)) != null) {
                class_name = matches[0];
            } else if ((matches = method_re.tryGetMatches(context, arg)) != null) {
                method_name = matches[0];
            }
        }

        if (class_name == null || method_name == null) {
            System.err.println("invoke --class=<classname> --method=<methodname> [ --argument=<argname>|[<argclass>][|<argvalue>] ... ] [--configuration-name=<cfgname>]");
            return null;
        }
        
        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Class   : %(class)", "class", class_name); }
        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Method  : %(method)", "method", method_name); }

        BuildTextComplex bt = new BuildTextComplex_String (context, "", "", class_name, "", "", "[command line]");

        for (String arg : args) {
            if (    (arg.charAt(0) != '-' || argpfx_re.matches(context, arg))
                 && (matches = arg_re.tryGetMatches(context, arg)) != null
               ) {
                String arg_name    = matches[0];
                String arg_class   = matches[1];
                if (arg_class == null) { arg_class = ""; }
                String arg_paren   = matches[2];
                String arg_value   = (arg_paren == null || arg_paren.length() == 0 ? null : matches[3]);
                String source_location_info = "[command line argument '" + arg_name + "']";
                bt.getItems(context).append(context,
                             new Pair_BuildText_String_(
                                    context, 
                                     (arg_value == null) ?
                                       new BuildTextComplex_String(context, "", "", arg_class, "", "", source_location_info)
                                     : new BuildTextComplex_String(context, "", "", "", "", "", source_location_info, new Pair_BuildText_String_(context, new BuildTextSimple_String(context, "", "", arg_class, "", "", arg_value, source_location_info), "")),
                                    arg_name));
                if ((notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Argument: Name : %(name)", "name", arg_name); }
                if ((notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "          Class: %(name)", "name", arg_class); }
                if ((notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "          Value: %(name)", "name", arg_value); }
            }
        }

        FactorySiteTextBased fs = null;
        try {
            fs = new FactorySiteTextBased(context, bt, "[command line]");
        } catch (PutUpFailure puf) {
            return puf;
        }
        
        Object object = null;
        try {
            object = fs.build(context, new java.util.Hashtable());
        } catch (BuildFailure bf) {
            return bf;
        }

        Method method = null;
        boolean have_context = true;
        try {
            method = object.getClass().getMethod(method_name, CallContext.class);
        } catch (NoSuchMethodException nsme) {
            try {
                have_context = false;
                method = object.getClass().getMethod(method_name);
            } catch (NoSuchMethodException nsme2) {
                return nsme2;
            }
        }

        Object result;
        try {
            if (have_context) {
                result = method.invoke(object, context);
            } else {
                result = method.invoke(object);
            }
        } catch (IllegalAccessException iae) {
            return iae;
        } catch (InvocationTargetException ite) {
            return ite.getTargetException();
        }

        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Invocation succeeded."); }

        return result;
    }
}


