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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.factory.returncodes.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.locating.factories.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.monitoring.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tocp.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Map;

public class Test {

    public Test(CallContext context) {
    }

    public static void main(String[] args) {

        Configuration.checkCommandLineArgs(args);
        Context context = com.sphenon.basics.context.classes.RootContext.getRootContext ();
        Configuration.initialise(context);
        NotificationContext nc = NotificationContext.create(context);
        CustomaryContext cc = CustomaryContext.create(context);

        int reload_times = 1;
        for (String arg : args) {
            if (arg.matches("reload:[0-9]+")) {
                reload_times = Integer.parseInt(arg.substring(7));
            }
        }

        ScaffoldFactory sf = FactorySiteContext.getScaffoldFactory(context);
        try {
            sf.preloadScaffoldFactory (context,
                                       TypeManager.get(context, com.sphenon.engines.factorysite.test.Erni.class),
                                       TypeManager.get(context, com.sphenon.engines.factorysite.test.Factory_Erni.class),
                                       false);
            sf.preloadScaffoldFactory (context,
                                       TypeManager.get(context, com.sphenon.engines.factorysite.test.PreloadTest.class),
                                       TypeManager.get(context, com.sphenon.engines.factorysite.test.Factory_PreloadTest1.class),
                                       false);
            sf.preloadScaffoldFactory (context,
                                       TypeManager.get(context, com.sphenon.engines.factorysite.test.PreloadTest.class),
                                       TypeManager.get(context, com.sphenon.engines.factorysite.test.Factory_PreloadTest2.class),
                                       false);
            sf.preloadScaffoldFactory_Aggregate (context,
                                       TypeManager.get(context, com.sphenon.engines.factorysite.test.PreloadTest.class),
                                       "my/cool/PreloadTest3",
                                       false);
        } catch (InvalidFactory ifac) {
            cc.sendTrace(context, Notifier.CHECKPOINT, "Could not preload factory: %(reason)", "reason", ifac);
            return;
        }

        Kunigunde kunigunde = new Kunigunde(context);
        FactorySiteOutParameter hansi = new FactorySiteOutParameter(context);
        Hashtable parameters = new Hashtable();
        parameters.put("Kunigunde", kunigunde);
        parameters.put("Hansi", hansi);
        parameters.put("ParaEins", "Uebergebener Parameter Eins");
        parameters.put("ParaDrei", "Uebergebener Parameter Drei");
        parameters.put("zumpara", "Zumpa Zumpa welche ein...");

        // Factory_Aggregate.configSearchPath(context, args[0]);

        FactorySitePackageInitialiser.defineAliases(context, "com.sphenon.engines.factorysite.Test", "MyAlias@java.util.Hashtable=item20|Aggregate::my/cool/module|||,MyAlias2@java.util.Hashtable=|Aggregate::my/cool/module2|||,MapAlias#java.lang.Object=|java.util.Hashtable|||,ContainerItem@java.util.Hashtable=container|java.util.Vector||||CONTAINER,MagicOID@java.util.Hashtable=|String||||||||Magic4711");

        FactorySitePackageInitialiser.defineDefaults(context, "com.sphenon.engines.factorysite.Test", "Zumbel@Wumbel=String/TutenGag,Zumbel@Gumbel=String/IDREF=zumpara");

        {
            Factory_Aggregate cf = new Factory_Aggregate(context);
            cf.setAggregateClass(context, "my/cool/test");
            cf.setParameters(context, parameters);
            
            Object o1;
            Object o2;
            Object o1B = null;
            Object o2B = null;
            try {
                o1 = cf.create(context);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.0" /* Could not build object aggregate: %(reason) */), "reason", e);
                return;
            }

            {   Vector<Pair<String,Object>> vmd = cf.getMetaData(context).get("Eins");
                for (Pair<String,Object> md : vmd) {
                    cc.sendTrace(context, Notifier.CHECKPOINT, "Meta data 'Eins': '%(path)' = '%(data)'", "path", md.getItem1(context), "data", md.getItem2(context));
                }
            }
            {   Vector<Pair<String,Object>> vmd = cf.getMetaData(context).get("Zwei");
                for (Pair<String,Object> md : vmd) {
                    cc.sendTrace(context, Notifier.CHECKPOINT, "Meta data 'Zwei': '%(path)' = '%(data)'", "path", md.getItem1(context), "data", md.getItem2(context));
                }
            }
            {   Vector<Pair<String,Object>> vmd = cf.getMetaData(context).get("Doclet");
                for (Pair<String,Object> md : vmd) {
                    cc.sendTrace(context, Notifier.CHECKPOINT, "Meta data 'Doclet': '%(path)' = '%(data)'", "path", md.getItem1(context), "data", md.getItem2(context));
                }
            }
            {   String md = (String) cf.getMetaData(context, "Doclet", ".*item86");
                cc.sendTrace(context, Notifier.CHECKPOINT, "Meta data 'Doclet', by filter 'item86': '%(data)'", "data", md);
            }
            {   Vector<Pair<String,Object>> vmd = cf.getMetaData(context).get("Susi");
                for (Pair<String,Object> md : vmd) {
                    cc.sendTrace(context, Notifier.CHECKPOINT, "Meta data 'Susi': '%(path)' = '%(data)'", "path", md.getItem1(context), "data", md.getItem2(context));
                }
            }
            {   Object md = cf.getMetaData(context, "Susi", "object");
                cc.sendTrace(context, Notifier.CHECKPOINT, "Meta data 'Susi', by filter 'object': '%(data)'", "data", md);
            }

            if (! hansi.isValid(context)) {
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.31" /* Out parameter not retrievable: %(reason) */), "reason", "data source is null");
                return;
            }
            try {
                o1B = hansi.getValueAsObject(context);
            } catch (DataSourceUnavailable dsu) {
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.31" /* Out parameter not retrievable: %(reason) */), "reason", dsu);
                return;
            } catch (IgnoreErraneousDataSource ieds) {
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.31" /* Out parameter not retrievable: %(reason) */), "reason", ieds);
                return;
            }

            try {
                o2 = cf.create(context);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.0" /* Could not build object aggregate: %(reason) */), "reason", e);
                return;
            }

            if (! hansi.isValid(context)) {
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.31" /* Out parameter not retrievable: %(reason) */), "reason", "data source is null");
                return;
            }
            try {
                o2B = hansi.getValueAsObject(context);
            } catch (DataSourceUnavailable dsu) {
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.31" /* Out parameter not retrievable: %(reason) */), "reason", dsu);
                return;
            } catch (IgnoreErraneousDataSource ieds) {
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.31" /* Out parameter not retrievable: %(reason) */), "reason", ieds);
                return;
            }

            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.1" /* Factory site and object aggregate successfully put up and built */));

            Hashtable h1 = (Hashtable) o1;
            Hashtable h2 = (Hashtable) o2;

            Object o11 = h1.get("item" + new Integer(1).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.2" /* Table 1, object 1: %(class) - %(object) */), "class", o11.getClass().toString(), "object", o11);
            Object o12 = h1.get("item" + new Integer(2).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.3" /* Table 1, object 2: %(class) - %(object) */), "class", o12.getClass().toString(), "object", o12);
            Object o13 = h1.get("item" + new Integer(3).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.4" /* Table 1, object 3: %(class) - %(object) */), "class", o13.getClass().toString(), "object", o13);
            Object o14 = h1.get("item" + new Integer(4).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 1, object 4: %(class) - %(object) */), "class", o14.getClass().toString(), "object", o14);
            Object o15 = h1.get("item" + new Integer(5).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.6" /* Table 1, object 5: %(class) - %(object) */), "class", o15.getClass().toString(), "object", o15);
            Object o16 = h1.get("item" + new Integer(6).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.7" /* Table 1, object 6: %(class) - %(object) */), "class", o16.getClass().toString(), "object", o16);
            Object o17 = h1.get("item" + new Integer(7).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.8" /* Table 1, object 7: %(class) - %(object) */), "class", o17.getClass().toString(), "object", o17);
            try {
                Erni e17 = (Erni) o17;
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.9" /* Table 1, object 7, Erni's Bert: %(bert) */), "bert", e17.getBert(context));
            } catch (ClassCastException e) {
                cc.sendError(context, FactorySiteStringPool.get(context, "2.1.10" /* o17 is not an Erni, as expected */));
                return;
            }
            Object o18 = h1.get("item" + new Integer(8).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.11" /* Table 1, object 8: %(class) - %(object) */), "class", o18.getClass().toString(), "object", o18);
            try {
                Bert b18 = (Bert) o18;
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.12" /* Table 1, object 8, Bert's Erni: %(erni) */), "erni", b18.getErni(context));
            } catch (ClassCastException e) {
                cc.sendError(context, FactorySiteStringPool.get(context, "2.1.13" /* o18 is not a Bert, as expected */));
                return;
            }
            Object o19 = h1.get("item" + new Integer(9).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.27" /* Table 1, object 9: %(class) - %(object) */), "class", o19.getClass().toString(), "object", o19);
            Object o1A = h1.get("item" + new Integer(10).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.29" /* Table 1, object A: %(class) - %(object) */), "class", o1A.getClass().toString(), "object", o1A);

            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.32" /* Table 1, object B (out parameter): %(class) - %(object) */), "class", o1B.getClass().toString(), "object", o1B);

            Object o1C = h1.get("item" + new Integer(11).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object C: %(class) - %(object)", "class", o1C.getClass().toString(), "object", o1C);
            Vector_String_long_ v1C = (Vector_String_long_) o1C;
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object C, element 0: '%(value)'", "value", v1C.tryGet(context, 0));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object C, element 1: '%(value)'", "value", v1C.tryGet(context, 1));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object C, element 2: '%(value)'", "value", v1C.tryGet(context, 2));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object C, element 3: '%(value)'", "value", v1C.tryGet(context, 3));

            Object o1Ca = h1.get("item" + new Integer(11).toString() + "a");
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11a: %(class) - %(object)", "class", o1Ca.getClass().toString(), "object", o1Ca);
            Vector v1Ca = (Vector) o1Ca;
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11a, element 0: '%(value)'", "value", v1Ca.get(0));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11a, element 1: '%(value)'", "value", v1Ca.get(1));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11a, element 2: '%(value)'", "value", v1Ca.get(2));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11a, element 3: '%(value)'", "value", v1Ca.get(3));

            Object o1Cb = h1.get("item" + new Integer(11).toString() + "b");
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11b: %(class) - %(object)", "class", o1Cb.getClass().toString(), "object", o1Cb);
            Vector v1Cb = (Vector) o1Cb;
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11b, element 0: '%(value)'", "value", v1Cb.get(0));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11b, element 1: '%(value)'", "value", v1Cb.get(1));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11b, element 2: '%(value)'", "value", v1Cb.get(2));

            Object o1Cc = h1.get("item" + new Integer(11).toString() + "c");
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11c: %(class) - %(object)", "class", o1Cc.getClass().toString(), "object", o1Cc);

            Object o1Cd = h1.get("item" + new Integer(11).toString() + "d");
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11d: %(class) - %(object)", "class", o1Cd.getClass().toString(), "object", o1Cd);
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11d: VS: %(object)", "object", ((GenericsTest)o1Cd).getVS(context));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object 11d: VVS: %(object)", "object", ((GenericsTest)o1Cd).getVVS(context));

            Object o1D = h1.get("item" + new Integer(12).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 1, object 4: %(class) - %(object) */), "class", o1D.getClass().toString(), "object", o1D);
            Object o1E = h1.get("item" + new Integer(13).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 1, object 4: %(class) - %(object) */), "class", o1E.getClass().toString(), "object", o1E);
            Object o1F = h1.get("item" + new Integer(14).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 1, object 4: %(class) - %(object) */), "class", o1F.getClass().toString(), "object", o1F);

            Object o1G = h1.get("item" + new Integer(15).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 1, object 4: %(class) - %(object) */), "class", o1G.getClass().toString(), "object", o1G);
            Object o1H = h1.get("item" + new Integer(16).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 1, object 4: %(class) - %(object) */), "class", o1H.getClass().toString(), "object", o1H);
            Object o1I = h1.get("item" + new Integer(17).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 1, object 4: %(class) - %(object) */), "class", o1I.getClass().toString(), "object", o1I);
            Object o1J = h1.get("item" + new Integer(18).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 1, object 4: %(class) - %(object) */), "class", o1J.getClass().toString(), "object", o1J);

            Object o1K = h1.get("item" + new Integer(19).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object K: %(class) - %(object)", "class", o1K.getClass().toString(), "object", o1K);
            Hashtable h1K = (Hashtable) o1K;
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object K, element 'franz': '%(value)'", "value", h1K.get("franz"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object K, element 'angela': '%(value)'", "value", h1K.get("angela"));
        
            Object o1L = h1.get("item" + new Integer(20).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object L: %(class) - %(object)", "class", o1L.getClass().toString(), "object", o1L);
            Hashtable h1L = (Hashtable) o1L;
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object L, element 'franz': '%(value)'", "value", h1L.get("franz"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object L, element 'angela': '%(value)'", "value", h1L.get("angela"));
        
            Object o1M = h1.get("item" + new Integer(27).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object M: %(class) - %(object)", "class", o1M.getClass().toString(), "object", o1M);
            Object o1Ma = h1.get("item" + new Integer(27).toString() + "a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object Ma: %(class) - %(object)", "class", o1Ma.getClass().toString(), "object", o1Ma);

            Object o1N = h1.get("container");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object N: %(class) - %(object)", "class", o1N.getClass().toString(), "object", o1N);
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object N, entry 0: %(entry)", "entry", ((java.util.Vector) o1N).elementAt(0));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object N, entry 1: %(entry)", "entry", ((java.util.Vector) o1N).elementAt(1));

            /*
              Object o1D = h1.get("item" + new Integer(12).toString());
              cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object D: %(class) - %(object)", "class", o1D.getClass().toString(), "object", o1D);
              Vector_String_long_ v1D = (Vector_String_long_) o1D;
              cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object D, element 0: '%(value)'", "value", v1D.tryGet(context, 0));
              cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object D, element 1: '%(value)'", "value", v1D.tryGet(context, 1));
              cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object D, element 2: '%(value)'", "value", v1D.tryGet(context, 2));
              cc.sendTrace(context, Notifier.CHECKPOINT,"Table 1, object D, element 3: '%(value)'", "value", v1D.tryGet(context, 3));
            */

            Object o128 = h1.get("item" + new Integer(28).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 28: %(class) - %(object)", "class", o128.getClass().toString(), "object", o128);

            Object o129 = h1.get("item" + new Integer(29).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 29: %(class) - %(object)", "class", o129.getClass().toString(), "object", o129);
            Object o130 = h1.get("item" + new Integer(30).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 30: %(class) - %(object)", "class", o130.getClass().toString(), "object", o130);
            Object o130a = h1.get("item" + new Integer(30).toString() + "a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 30a: %(class) - %(object)", "class", o130a.getClass().toString(), "object", o130a);
            Object o130b = h1.get("item" + new Integer(30).toString() + "b");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 30b: %(class) - %(object)", "class", o130b.getClass().toString(), "object", o130b);
            Object o130c = h1.get("item" + new Integer(30).toString() + "c");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 30c: %(class) - %(object)", "class", o130c.getClass().toString(), "object", o130c);
            Object o131 = h1.get("item" + new Integer(31).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 31: %(class) - %(object)", "class", o131.getClass().toString(), "object", o131);
            Object o132 = h1.get("item" + new Integer(32).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 32: %(class) - %(object)", "class", o132.getClass().toString(), "object", o132);
            Object o132a = h1.get("item" + new Integer(32).toString() + "a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 32a: %(class) - %(object)", "class", o132a.getClass().toString(), "object", o132a);
            Object o132b = h1.get("item" + new Integer(32).toString() + "b");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 32b: %(class) - %(object)", "class", o132b.getClass().toString(), "object", o132b);
            Object o132c = h1.get("item" + new Integer(32).toString() + "c");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 32c: %(class) - %(object)", "class", o132c.getClass().toString(), "object", o132c);

            Object o133 = h1.get("item" + new Integer(33).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 33: %(class) - entry 1: %(object)", "class", o133.getClass().toString(), "object", ((Vector_String_long_) o133).tryGet(context, 0));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 33: %(class) - entry 2: %(object)", "class", o133.getClass().toString(), "object", ((Vector_String_long_) o133).tryGet(context, 1));

            Object o133a = h1.get("item" + new Integer(33).toString() +"a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 33a: %(class) - entry 0: %(object)", "class", o133a.getClass().toString(), "object", ((Container) o133a).getArray(context)[0]);
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 33a: %(class) - entry 1: %(object)", "class", o133a.getClass().toString(), "object", ((Container) o133a).getArray(context)[1]);
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 33a: %(class) - entry 2: %(object)", "class", o133a.getClass().toString(), "object", ((Container) o133a).getArrayHolder(context).getArray(context)[0]);
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 33a: %(class) - entry 3: %(object)", "class", o133a.getClass().toString(), "object", ((Container) o133a).getArrayHolder(context).getArray(context)[1]);
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 33a: %(class) - entry 4: %(object)", "class", o133a.getClass().toString(), "object", ((Container) o133a).getArrayHolder(context).getArray(context)[2]);
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 33a: %(class) - entry 5: %(object)", "class", o133a.getClass().toString(), "object", ((Container) o133a).getArrayHolder(context).getArray(context)[3]);

            Object o134 = h1.get("item" + new Integer(34).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 34: %(class) - entry 1: %(object)", "class", o134.getClass().toString(), "object", ((Hashtable) o134).get("item1"));

            Object o135 = h1.get("item" + new Integer(35).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 35: %(class) - entry 1: %(object)", "class", o135.getClass().toString(), "object", ((Hashtable) o135).get("item1"));
            Object o135a = h1.get("item" + new Integer(35).toString() + "a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 35a: %(class) - entry 1: %(object)", "class", o135a.getClass().toString(), "object", ((Hashtable) o135a).get("item1"));

            Object o136 = h1.get("item" + new Integer(36).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 36: %(class) - %(object)", "class", o136 == null ? null : o136.getClass().toString(), "object", o136);
            Object o136a = h1.get("item" + new Integer(36).toString() + "a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 36a: %(class) - %(object)", "class", o136a == null ? null : o136a.getClass().toString(), "object", o136a);
            Object o136b = h1.get("item" + new Integer(36).toString() + "b");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 36b: %(class) - %(object)", "class", o136b == null ? null : o136b.getClass().toString(), "object", o136b);
            Object o137 = h1.get("item" + new Integer(37).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 37: %(class) - %(object)", "class", o137 == null ? null : o137.getClass().toString(), "object", o137);

            Object o138 = h1.get("item" + new Integer(38).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 38: %(class) - %(object)", "class", o138 == null ? null : o138.getClass().toString(), "object", o138);
            Object o139 = h1.get("item" + new Integer(39).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 39: %(class) - %(object)", "class", o139 == null ? null : o139.getClass().toString(), "object", o139);
            Object o140 = h1.get("item" + new Integer(40).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 40: %(class) - %(object)", "class", o140 == null ? null : o140.getClass().toString(), "object", o140);

            Object o138a = h1.get("item" + new Integer(38).toString()+"a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 38a: %(class) - %(object)", "class", o138a == null ? null : o138a.getClass().toString(), "object", o138a);
            Object o139a = h1.get("item" + new Integer(39).toString()+"a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 39a: %(class) - %(object)", "class", o139a == null ? null : o139a.getClass().toString(), "object", o139a);
            Object o140a = h1.get("item" + new Integer(40).toString()+"a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 40a: %(class) - %(object)", "class", o140a == null ? null : o140a.getClass().toString(), "object", o140a);

            Object o138b = h1.get("item" + new Integer(38).toString()+"b");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 38b: %(class) - %(object)", "class", o138b == null ? null : o138b.getClass().toString(), "object", o138b);
            Object o139b = h1.get("item" + new Integer(39).toString()+"b");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 39b: %(class) - %(object)", "class", o139b == null ? null : o139b.getClass().toString(), "object", o139b);
            Object o140b = h1.get("item" + new Integer(40).toString()+"b");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 40b: %(class) - %(object)", "class", o140b == null ? null : o140b.getClass().toString(), "object", o140b);

            Object o141 = h1.get("item" + new Integer(41).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 41: %(class) - %(object)", "class", o141 == null ? null : o141.getClass().toString(), "object", o141);
            Object o141a = h1.get("item41a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 41a: %(class) - %(object)", "class", o141a == null ? null : o141a.getClass().toString(), "object", o141a);
            Object o142 = h1.get("item" + new Integer(42).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 42: %(class) - %(object)", "class", o142 == null ? null : o142.getClass().toString(), "object", o142);

            Object o143 = h1.get("item" + new Integer(43).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 43: %(class) - %(object)", "class", o143 == null ? null : o143.getClass().toString(), "object", ((Willy)o143).getHallo(context));

            Object o144 = h1.get("item" + new Integer(44).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 44: %(class) - %(object)", "class", o144 == null ? null : o144.getClass().toString(), "object", o144);
            Object o145 = h1.get("item" + new Integer(45).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 45: %(class) - %(object)", "class", o145 == null ? null : o145.getClass().toString(), "object", o145);
            Object o146 = h1.get("item" + new Integer(46).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 46: %(class) - %(object)", "class", o146 == null ? null : o146.getClass().toString(), "object", o146);

            Object o147 = h1.get("item" + new Integer(47).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 47: %(class) - %(object)", "class", o147 == null ? null : o147.getClass().toString(), "object", ((Wumbel)o147).getZumbel(context));
            Object o148 = h1.get("item" + new Integer(48).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 48: %(class) - %(object)", "class", o148 == null ? null : o148.getClass().toString(), "object", ((Gumbel)o148).getZumbel(context));

            Object o149 = h1.get("item" + new Integer(49).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 49: %(class) - %(object)", "class", o149 == null ? null : o149.getClass().toString(), "object", o149);
            Object o150 = h1.get("item" + new Integer(50).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 50: %(class) - %(object)", "class", o150 == null ? null : o150.getClass().toString(), "object", o150);
            Object o151 = h1.get("item" + new Integer(51).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 51: %(class) - %(object)", "class", o151 == null ? null : o151.getClass().toString(), "object", o151);
            Object o152 = h1.get("item" + new Integer(52).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 52: %(class) - %(object)", "class", o152 == null ? null : o152.getClass().toString(), "object", o152);
            Object o153 = h1.get("item" + new Integer(53).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 53: %(class) - %(object)", "class", o153 == null ? null : o153.getClass().toString(), "object", o153);
            Object o154 = h1.get("item" + new Integer(54).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 54: %(class) - %(object)", "class", o154 == null ? null : o154.getClass().toString(), "object", o154);
            Object o155 = h1.get("item" + new Integer(55).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 55: %(class) - %(object)", "class", o155 == null ? null : o155.getClass().toString(), "object", ((Willy)o155).getHallo(context));

            Object o162 = h1.get("item" + new Integer(62).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 62: %(class) - entry 1: %(object)", "class", o162.getClass().toString(), "object", ((Hashtable) o162).get("item1"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 62: %(class) - entry 2: %(object)", "class", o162.getClass().toString(), "object", ((Hashtable) o162).get("item2"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 62: %(class) - entry 3: %(object)", "class", o162.getClass().toString(), "object", ((Hashtable) o162).get("item3"));
            Object o163 = h1.get("item" + new Integer(63).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 63: %(class) - entry 1: %(object)", "class", o163.getClass().toString(), "object", ((Hashtable) o163).get("item1"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 63: %(class) - entry 2: %(object)", "class", o163.getClass().toString(), "object", ((Hashtable) o163).get("item2"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 63: %(class) - entry 3: %(object)", "class", o163.getClass().toString(), "object", ((Hashtable) o163).get("item3"));
            Object o164 = h1.get("item" + new Integer(64).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 64: %(class) - entry 1: %(object)", "class", o164.getClass().toString(), "object", ((Hashtable) o164).get("item1"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 64: %(class) - entry 2: %(object)", "class", o164.getClass().toString(), "object", ((Hashtable) o164).get("item2"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 64: %(class) - entry 3: %(object)", "class", o164.getClass().toString(), "object", ((Hashtable) o164).get("item3"));

            Object o169 = h1.get("item" + new Integer(69).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 69: %(class) - %(object)", "class", o169.getClass().toString(), "object", o169);
            Object o169a = h1.get("item" + new Integer(69).toString() + "a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 69a: %(class) - %(object)", "class", o169a.getClass().toString(), "object", o169a);
            Object o169b = h1.get("item" + new Integer(69).toString() + "b");
            if (o169b != null) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 69b: %(class) - %(object)", "class", o169b.getClass().toString(), "object", o169b);
            }
            Object o169c = h1.get("item" + new Integer(69).toString() + "c");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 69c: %(class) - %(object)", "class", o169c.getClass().toString(), "object", o169c);

            Object o172 = h1.get("item" + new Integer(72).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 72: %(class) - %(object)", "class", o172.getClass().toString(), "object", o172);
            if (((MonitorableObject) o172).getProblemStatusDetails(context) != null) {
                for (ProblemStatus ps : ((MonitorableObject) o172).getProblemStatusDetails(context)) {
                    cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 72: problem: %(problem)", "problem", ps.toString());
                }
            }

            Object o173 = h1.get("item" + new Integer(73).toString());
            if (o173 != null) {
                cc.sendError(context, "erraneous instance 73 should not exist");
                return;
            }
            Object o174 = h1.get("item" + new Integer(74).toString());
            if (o174 != null) {
                cc.sendError(context, "erraneous instance 74 should not exist");
                return;
            }

            Object o184 = h1.get("item" + new Integer(84).toString());
            if (o184 != null) {
                cc.sendError(context, "instance 84 should not exist");
            }

            Object o185 = h1.get("item" + new Integer(85).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 85: %(class) - entry 1: %(object)", "class", o185.getClass().toString(), "object", ((Hashtable) o185).get("Hund1"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 85: %(class) - entry 2: %(object)", "class", o185.getClass().toString(), "object", ((Hashtable) o185).get("Hund2"));

            Object oMagic1 = h1.get("MagicOID");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object MagicOID: %(class) - %(object)", "class", oMagic1 == null ? null : oMagic1.getClass().toString(), "object", oMagic1);
            Object oMagic2 = h1.get("MagicReference");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object MagicReference: %(class) - %(object)", "class", oMagic2 == null ? null : oMagic2.getClass().toString(), "object", oMagic2);

            Object o189 = h1.get("item" + new Integer(89).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 89: %(class) - name: %(name)", "class", o189.getClass().toString(), "name", ((Person) o189).getName(context));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 89: %(class) - street: %(street)", "class", o189.getClass().toString(), "street", ((Person) o189).getAddress(context).getStreet(context));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 89: %(class) - city: %(city)", "class", o189.getClass().toString(), "city", ((Person) o189).getAddress(context).getCity(context));

            Object o191 = h1.get("item" + new Integer(91).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 91: %(class) - name: %(name)", "class", o191.getClass().toString(), "name", ((Person) o191).getName(context));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 91: %(class) - street: %(street)", "class", o191.getClass().toString(), "street", ((Person) o191).getAddress(context).getStreet(context));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 91: %(class) - city: %(city)", "class", o191.getClass().toString(), "city", ((Person) o191).getAddress(context).getCity(context));

            Object o192 = h1.get("item" + new Integer(92).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 92: %(class) - name: %(name)", "class", o192.getClass().toString(), "name", ((Person) o192).getName(context));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 92: %(class) - street: %(street)", "class", o192.getClass().toString(), "street", ((Person) o192).getAddress(context).getStreet(context));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 92: %(class) - city: %(city)", "class", o192.getClass().toString(), "city", ((Person) o192).getAddress(context).getCity(context));

            Object o193 = h1.get("item" + new Integer(93).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 93: %(class) - c1/c2/c3/text: %(text)", "class", o193.getClass().toString(), "text", ((C1) o193).getC2(context).getC3(context).getText(context));

            Object o194 = h1.get("item" + new Integer(94).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 94: %(class) - text: %(text)", "class", o194.getClass().toString(), "text", ((String) o194));

            Object o195 = h1.get("item" + new Integer(95).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 95: %(class) - text: %(text)", "class", o195.getClass().toString(), "text", ((String) o195));

            Object o197 = h1.get("item" + new Integer(97).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 97: %(class) - text: %(text)", "class", o197.getClass().toString(), "text", ((String) o197));
            Object o198 = h1.get("item" + new Integer(98).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 1, object 98: %(class) - text: %(text)", "class", o198.getClass().toString(), "text", ((String) o198));

            Object o21 = h2.get("item" + new Integer(1).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.14" /* Table 2, object 1: %(class) - %(object) */), "class", o21.getClass().toString(), "object", o21);
            Object o22 = h2.get("item" + new Integer(2).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.15" /* Table 2, object 2: %(class) - %(object) */), "class", o22.getClass().toString(), "object", o22);
            Object o23 = h2.get("item" + new Integer(3).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.16" /* Table 2, object 3: %(class) - %(object) */), "class", o23.getClass().toString(), "object", o23);
            Object o24 = h2.get("item" + new Integer(4).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.17" /* Table 2, object 4: %(class) - %(object) */), "class", o24.getClass().toString(), "object", o24);
            Object o25 = h2.get("item" + new Integer(5).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.18" /* Table 2, object 5: %(class) - %(object) */), "class", o25.getClass().toString(), "object", o25);
            Object o26 = h2.get("item" + new Integer(6).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.19" /* Table 2, object 6: %(class) - %(object) */), "class", o26.getClass().toString(), "object", o26);
            Object o27 = h2.get("item" + new Integer(7).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.20" /* Table 2, object 7: %(class) - %(object) */), "class", o27.getClass().toString(), "object", o27);
            try {
                Erni e27 = (Erni) o27;
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.21" /* Table 2, object 7, Erni's Bert: %(bert) */), "bert", e27.getBert(context));
            } catch (ClassCastException e) {
                cc.sendError(context, FactorySiteStringPool.get(context, "2.1.22" /* o27 is not an Erni, as expected */));
                return;
            }
            Object o28 = h2.get("item" + new Integer(8).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.23" /* Table 2, object 8: %(class) - %(object) */), "class", o28.getClass().toString(), "object", o28);
            try {
                Bert b28 = (Bert) o28;
                cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.24" /* Table 2, object 8, Bert's Erni: %(erni) */), "erni", b28.getErni(context));
            } catch (ClassCastException e) {
                cc.sendError(context, FactorySiteStringPool.get(context, "2.1.25" /* o28 is not a Bert, as expected */));
                return;
            }
            Object o29 = h2.get("item" + new Integer(9).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.28" /* Table 2, object 9: %(class) - %(object) */), "class", o29.getClass().toString(), "object", o29);
            Object o2A = h2.get("item" + new Integer(10).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.30" /* Table 2, object A: %(class) - %(object) */), "class", o2A.getClass().toString(), "object", o2A);

            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.33" /* Table 2, object B (out parameter): %(class) - %(object) */), "class", o2B.getClass().toString(), "object", o2B);

            Object o2C = h2.get("item" + new Integer(11).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 2, object C: %(class) - %(object)", "class", o2C.getClass().toString(), "object", o2C);
            Vector_String_long_ v2C = (Vector_String_long_) o2C;
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 2, object C, element 0: '%(value)'", "value", v2C.tryGet(context, 0));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 2, object C, element 1: '%(value)'", "value", v2C.tryGet(context, 1));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 2, object C, element 2: '%(value)'", "value", v2C.tryGet(context, 2));
            cc.sendTrace(context, Notifier.CHECKPOINT,"Table 2, object C, element 3: '%(value)'", "value", v2C.tryGet(context, 3));

            Object o2D = h2.get("item" + new Integer(12).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 2, object 4: %(class) - %(object) */), "class", o2D.getClass().toString(), "object", o2D);
            Object o2E = h2.get("item" + new Integer(13).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 2, object 4: %(class) - %(object) */), "class", o2E.getClass().toString(), "object", o2E);
            Object o2F = h2.get("item" + new Integer(14).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 2, object 4: %(class) - %(object) */), "class", o2F.getClass().toString(), "object", o2F);

            Object o2G = h2.get("item" + new Integer(15).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 2, object 4: %(class) - %(object) */), "class", o2G.getClass().toString(), "object", o2G);
            Object o2H = h2.get("item" + new Integer(16).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 2, object 4: %(class) - %(object) */), "class", o2H.getClass().toString(), "object", o2H);
            Object o2I = h2.get("item" + new Integer(17).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 2, object 4: %(class) - %(object) */), "class", o2I.getClass().toString(), "object", o2I);
            Object o2J = h2.get("item" + new Integer(18).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.5" /* Table 2, object 4: %(class) - %(object) */), "class", o2J.getClass().toString(), "object", o2J);

            Object o2K = h2.get("item" + new Integer(19).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object K: %(class) - %(object)", "class", o2K.getClass().toString(), "object", o2K);
            Hashtable h2K = (Hashtable) o2K;
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object K, element 'franz': '%(value)'", "value", h2K.get("franz"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object K, element 'angela': '%(value)'", "value", h2K.get("angela"));

            Object o2L = h2.get("item" + new Integer(20).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object L: %(class) - %(object)", "class", o2L.getClass().toString(), "object", o2L);
            Hashtable h2L = (Hashtable) o2L;
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object L, element 'franz': '%(value)'", "value", h2L.get("franz"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object L, element 'angela': '%(value)'", "value", h2L.get("angela"));

            Object o2M = h2.get("item" + new Integer(27).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object M: %(class) - %(object)", "class", o2M.getClass().toString(), "object", o2M);
            Object o2Ma = h2.get("item" + new Integer(27).toString() + "a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object Ma: %(class) - %(object)", "class", o2Ma.getClass().toString(), "object", o2Ma);

            Object o2N = h2.get("container");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object N: %(class) - %(object)", "class", o2N.getClass().toString(), "object", o2N);
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object N, entry 0: %(entry)", "entry", ((java.util.Vector) o2N).elementAt(0));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object N, entry 1: %(entry)", "entry", ((java.util.Vector) o2N).elementAt(1));

            /*
              Object o2D = h2.get("item" + new Integer(12).toString());
              cc.sendTrace(context, Notifier.CHECKPOINT,"Table 2, object D: %(class) - %(object)", "class", o2D.getClass().toString(), "object", o2D);
              Vector_String_long_ v2D = (Vector_String_long_) o2D;
              cc.sendTrace(context, Notifier.CHECKPOINT,"Table 2, object D, element 0: '%(value)'", "value", v2D.tryGet(context, 0));
              cc.sendTrace(context, Notifier.CHECKPOINT,"Table 2, object D, element 1: '%(value)'", "value", v2D.tryGet(context, 1));
              cc.sendTrace(context, Notifier.CHECKPOINT,"Table 2, object D, element 2: '%(value)'", "value", v2D.tryGet(context, 2));
              cc.sendTrace(context, Notifier.CHECKPOINT,"Table 2, object D, element 3: '%(value)'", "value", v2D.tryGet(context, 3));
            */

            Object o228 = h2.get("item" + new Integer(28).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 28: %(class) - %(object)", "class", o228.getClass().toString(), "object", o228);

            Object o229 = h2.get("item" + new Integer(29).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 29: %(class) - %(object)", "class", o229.getClass().toString(), "object", o229);
            Object o230 = h2.get("item" + new Integer(30).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 30: %(class) - %(object)", "class", o230.getClass().toString(), "object", o230);
            Object o230a = h2.get("item" + new Integer(30).toString() + "a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 30a: %(class) - %(object)", "class", o230a.getClass().toString(), "object", o230a);
            Object o230b = h2.get("item" + new Integer(30).toString() + "b");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 30b: %(class) - %(object)", "class", o230b.getClass().toString(), "object", o230b);
            Object o230c = h2.get("item" + new Integer(30).toString() + "c");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 30c: %(class) - %(object)", "class", o230c.getClass().toString(), "object", o230c);

            Object o231 = h2.get("item" + new Integer(31).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 31: %(class) - %(object)", "class", o231.getClass().toString(), "object", o231);
            Object o232 = h2.get("item" + new Integer(32).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 32: %(class) - %(object)", "class", o232.getClass().toString(), "object", o232);
            Object o232a = h2.get("item" + new Integer(32).toString() + "a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 32a: %(class) - %(object)", "class", o232a.getClass().toString(), "object", o232a);
            Object o232b = h2.get("item" + new Integer(32).toString() + "b");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 32b: %(class) - %(object)", "class", o232b.getClass().toString(), "object", o232b);
            Object o232c = h2.get("item" + new Integer(32).toString() + "c");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 32c: %(class) - %(object)", "class", o232c.getClass().toString(), "object", o232c);

            Object o233 = h2.get("item" + new Integer(33).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 33: %(class) - entry 1: %(object)", "class", o233.getClass().toString(), "object", ((Vector_String_long_) o233).tryGet(context, 0));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 33: %(class) - entry 2: %(object)", "class", o233.getClass().toString(), "object", ((Vector_String_long_) o233).tryGet(context, 1));

            Object o234 = h2.get("item" + new Integer(34).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 34: %(class) - entry 1: %(object)", "class", o234.getClass().toString(), "object", ((Hashtable) o234).get("item1"));

            Object o235 = h2.get("item" + new Integer(35).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 35: %(class) - entry 1: %(object)", "class", o235.getClass().toString(), "object", ((Hashtable) o235).get("item1"));
            Object o235a = h2.get("item" + new Integer(35).toString() + "a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 35a: %(class) - entry 2: %(object)", "class", o235a.getClass().toString(), "object", ((Hashtable) o235a).get("item1"));

            Object o236 = h2.get("item" + new Integer(36).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 36: %(class) - %(object)", "class", o236 == null ? null : o236.getClass().toString(), "object", o236);
            Object o237 = h2.get("item" + new Integer(37).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 37: %(class) - %(object)", "class", o237 == null ? null : o237.getClass().toString(), "object", o237);
            Object o238 = h2.get("item" + new Integer(38).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 38: %(class) - %(object)", "class", o238 == null ? null : o238.getClass().toString(), "object", o238);
            Object o239 = h2.get("item" + new Integer(39).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 39: %(class) - %(object)", "class", o239 == null ? null : o239.getClass().toString(), "object", o239);
            Object o240 = h2.get("item" + new Integer(40).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 40: %(class) - %(object)", "class", o240 == null ? null : o240.getClass().toString(), "object", o240);
            Object o241 = h2.get("item" + new Integer(41).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 41: %(class) - %(object)", "class", o241 == null ? null : o241.getClass().toString(), "object", o241);
            Object o241a = h2.get("item41a");
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 41a: %(class) - %(object)", "class", o241a == null ? null : o241a.getClass().toString(), "object", o241a);
            Object o242 = h2.get("item" + new Integer(42).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 42: %(class) - %(object)", "class", o242 == null ? null : o242.getClass().toString(), "object", o242);

            Object o243 = h2.get("item" + new Integer(43).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 43: %(class) - %(object)", "class", o243 == null ? null : o243.getClass().toString(), "object", ((Willy)o243).getHallo(context));

            Object o244 = h2.get("item" + new Integer(44).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 44: %(class) - %(object)", "class", o244 == null ? null : o244.getClass().toString(), "object", o244);
            Object o245 = h2.get("item" + new Integer(45).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 45: %(class) - %(object)", "class", o245 == null ? null : o245.getClass().toString(), "object", o245);
            Object o246 = h2.get("item" + new Integer(46).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 46: %(class) - %(object)", "class", o246 == null ? null : o246.getClass().toString(), "object", o246);

            Object o247 = h2.get("item" + new Integer(47).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 47: %(class) - %(object)", "class", o247 == null ? null : o247.getClass().toString(), "object", ((Wumbel)o247).getZumbel(context));
            Object o248 = h2.get("item" + new Integer(48).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 48: %(class) - %(object)", "class", o248 == null ? null : o248.getClass().toString(), "object", ((Gumbel)o248).getZumbel(context));

            Object o249 = h2.get("item" + new Integer(49).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 49: %(class) - %(object)", "class", o249 == null ? null : o249.getClass().toString(), "object", o249);
            Object o250 = h2.get("item" + new Integer(50).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 50: %(class) - %(object)", "class", o250 == null ? null : o250.getClass().toString(), "object", o250);
            Object o251 = h2.get("item" + new Integer(51).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 51: %(class) - %(object)", "class", o251 == null ? null : o251.getClass().toString(), "object", o251);
            Object o252 = h2.get("item" + new Integer(52).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 52: %(class) - %(object)", "class", o252 == null ? null : o252.getClass().toString(), "object", o252);
            Object o253 = h2.get("item" + new Integer(53).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 53: %(class) - %(object)", "class", o253 == null ? null : o253.getClass().toString(), "object", o253);
            Object o254 = h2.get("item" + new Integer(54).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 54: %(class) - %(object)", "class", o254 == null ? null : o254.getClass().toString(), "object", o254);
            Object o255 = h2.get("item" + new Integer(55).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 55: %(class) - %(object)", "class", o255 == null ? null : o255.getClass().toString(), "object", ((Willy)o255).getHallo(context));

            Object o262 = h2.get("item" + new Integer(62).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 62: %(class) - entry 1: %(object)", "class", o262.getClass().toString(), "object", ((Hashtable) o262).get("item1"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 62: %(class) - entry 2: %(object)", "class", o262.getClass().toString(), "object", ((Hashtable) o262).get("item2"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 62: %(class) - entry 3: %(object)", "class", o262.getClass().toString(), "object", ((Hashtable) o262).get("item3"));
            Object o263 = h2.get("item" + new Integer(63).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 63: %(class) - entry 1: %(object)", "class", o263.getClass().toString(), "object", ((Hashtable) o263).get("item1"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 63: %(class) - entry 2: %(object)", "class", o263.getClass().toString(), "object", ((Hashtable) o263).get("item2"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 63: %(class) - entry 3: %(object)", "class", o263.getClass().toString(), "object", ((Hashtable) o263).get("item3"));
            Object o264 = h2.get("item" + new Integer(64).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 64: %(class) - entry 1: %(object)", "class", o264.getClass().toString(), "object", ((Hashtable) o264).get("item1"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 64: %(class) - entry 2: %(object)", "class", o264.getClass().toString(), "object", ((Hashtable) o264).get("item2"));
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 64: %(class) - entry 3: %(object)", "class", o264.getClass().toString(), "object", ((Hashtable) o264).get("item3"));

            Object o269 = h2.get("item" + new Integer(69).toString());
            cc.sendTrace(context, Notifier.CHECKPOINT, "Table 2, object 69: %(class) - %(object)", "class", o269.getClass().toString(), "object", o269);

            if (o11 == o21) {
                cc.sendError(context, FactorySiteStringPool.get(context, "2.1.26" /* Objects are identical, contrary to expectation */));
                return;
            }
        }

        {
            Factory_Aggregate cf2 = new Factory_Aggregate(context);
            cf2.setAggregateClass(context, "my/cool/test;class=Willy");
            cf2.setParameters(context, new Hashtable());

            Object willyo1;
            Object willyo2;
            try {
                willyo1 = cf2.create(context);
                willyo2 = cf2.create(context);
                if (willyo1 != willyo2) {
                    cc.sendError(context, "Objects are not identical, contrary to expectation");
                    return;
                }
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }

        {
            Factory_Aggregate cf3 = new Factory_Aggregate(context);
            cf3.setAggregateClass(context, "my;class=Tree<Willy>");
            cf3.setParameters(context, new Hashtable());

            Object two1;
            try {
                two1 = cf3.create(context);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }

            cc.sendTrace(context, Notifier.CHECKPOINT, "Treewilly     : %(class) - %(object)", "class", two1.getClass().toString(), "object", two1);
            Hashtable twh1 = (Hashtable) two1;

//             for (java.util.Enumeration e = ((Hashtable) two1).keys() ; e.hasMoreElements() ;) {
//                 System.out.println("GUMP: «" + e.nextElement() + "»");
//             }

            Object two2 = twh1.get("cool");
            cc.sendTrace(context, Notifier.CHECKPOINT, "        cool -> %(class) - %(object)", "class", two2 == null ? null : two2.getClass().toString(), "object", two2);
            Hashtable twh2 = (Hashtable) two2;

            Object two3 = twh2.get("test");
            cc.sendTrace(context, Notifier.CHECKPOINT, "  test.willy -> %(class) - %(object)", "class", two3 == null ? null : two3.getClass().toString(), "object", two3);

            Object two4 = twh2.get("supercool");
            cc.sendTrace(context, Notifier.CHECKPOINT, "        cool -> %(class) - %(object)", "class", two4 == null ? null : two4.getClass().toString(), "object", two4);
            Hashtable twh4 = (Hashtable) two4;

//             try {
//                 Erni e17 = (Erni) o17;
//                 cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.1.9" /* Table 1, object 7, Erni's Bert: %(bert) */), "bert", e17.getBert(context));
//             } catch (ClassCastException e) {
//                 cc.sendError(context, FactorySiteStringPool.get(context, "2.1.10" /* o17 is not an Erni, as expected */));
//                 return;
//             }

        }

        {
            Factory_Aggregate cf4 = new Factory_Aggregate(context);
            cf4.setAggregateClass(context, "my/cool/test2");
            cf4.setParameters(context, new Hashtable());

            Object hashy;
            try {
                hashy = cf4.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Hashy ist ein: '%(hashyclass)'", "hashyclass", hashy.getClass().getName());
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }
        {
            Factory_Aggregate cf5 = new Factory_Aggregate(context);
            cf5.setAggregateClass(context, "my/cool/test3");
            cf5.setParameters(context, new Hashtable());

            Object hashy;
            try {
                hashy = cf5.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Hashy ist ein: '%(hashyclass)'", "hashyclass", hashy.getClass().getName());
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }
        {
            Factory_Aggregate cf6 = new Factory_Aggregate(context);
            cf6.setAggregateClass(context, "my/cool/test99");
            cf6.setParameters(context, new Hashtable());

            Object smashy;
            try {
                smashy = cf6.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Smashy ist ein: '%(smashyclass)'", "smashyclass", smashy.getClass().getName());
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }
        {
            Factory_Aggregate cf7 = new Factory_Aggregate(context);
            cf7.setAggregateClass(context, "my/cool/test99a");
            cf7.setParameters(context, new Hashtable());

            Object smashy;
            try {
                smashy = cf7.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Smashy-a ist ein: '%(smashyclass)'", "smashyclass", smashy.getClass().getName());
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }
        {
            Factory_Aggregate cf8 = new Factory_Aggregate(context);
            cf8.setAggregateClass(context, "my/jool/test");
            cf8.setParameters(context, new Hashtable());

            Object jashy;
            try {
                jashy = cf8.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Jashy ist ein: '%(jashyclass)'", "jashyclass", jashy.getClass().getName());
                cc.sendTrace(context, Notifier.CHECKPOINT, "Jashy looks like: '%(jashy)'", "jashy", jashy);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }

        {
            Factory_Aggregate cf9 = new Factory_Aggregate(context);
            cf9.setAggregateClass(context, "my/yool/test");
            cf9.setParameters(context, new Hashtable());

            Object yashy;
            try {
                yashy = cf9.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Yashy ist ein: '%(yashyclass)'", "yashyclass", yashy.getClass().getName());
                cc.sendTrace(context, Notifier.CHECKPOINT, "Yashy looks like: '%(yashy)'", "yashy", yashy);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }

        {
            Factory_Aggregate cf10 = new Factory_Aggregate(context);
            cf10.setAggregateClass(context, "my/mool/test");
            cf10.setParameters(context, new Hashtable());

            Object mashy;
            try {
                mashy = cf10.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Mashy ist ein: '%(mashyclass)'", "mashyclass", mashy.getClass().getName());
                cc.sendTrace(context, Notifier.CHECKPOINT, "Mashy looks like: '%(mashy)'", "mashy", mashy);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }

        {
            Factory_Aggregate cf11a = new Factory_Aggregate(context);
            cf11a.setAggregateClass(context, "my/sool/test1");
            cf11a.setParameters(context, new Hashtable());

            try {
                Object sashy = cf11a.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Sashy(a) ist ein: '%(sashyclass)'", "sashyclass", sashy.getClass().getName());
                cc.sendTrace(context, Notifier.CHECKPOINT, "Sashy(a) looks like: '%(sashy)'", "sashy", sashy);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }

            Factory_Aggregate cf11b = new Factory_Aggregate(context);
            cf11b.setAggregateClass(context, "my/sool/test2");
            cf11b.setParameters(context, new Hashtable());

            try {
                Object sashy = cf11b.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Sashy(b) ist ein: '%(sashyclass)'", "sashyclass", sashy.getClass().getName());
                cc.sendTrace(context, Notifier.CHECKPOINT, "Sashy(b) looks like: '%(sashy)'", "sashy", sashy);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }

            Factory_Aggregate cf11c = new Factory_Aggregate(context);
            cf11c.setAggregateClass(context, "my/sool/test3");
            cf11c.setParameters(context, new Hashtable());

            try {
                Object sashy = cf11c.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Sashy(c) ist ein: '%(sashyclass)'", "sashyclass", sashy.getClass().getName());
                cc.sendTrace(context, Notifier.CHECKPOINT, "Sashy(c) looks like: '%(sashy)'", "sashy", sashy);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }

        {
            Factory_Aggregate cf12a = new Factory_Aggregate(context);
            cf12a.setAggregateClass(context, "my/gool/test1");
            cf12a.setParameters(context, new Hashtable());

            Object gashy;
            try {
                gashy = cf12a.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Gashy(a) ist ein: '%(gashyclass)'", "gashyclass", gashy.getClass().getName());
                cc.sendTrace(context, Notifier.CHECKPOINT, "Gashy(a) looks like: '%(gashy)'", "gashy", gashy);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }

        {
            Factory_Aggregate cf12b = new Factory_Aggregate(context);
            cf12b.setAggregateClass(context, "my/gool/test2");
            cf12b.setParameters(context, new Hashtable());

            Object gashy;
            try {
                gashy = cf12b.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Gashy(b) ist ein: '%(gashyclass)'", "gashyclass", gashy.getClass().getName());
                cc.sendTrace(context, Notifier.CHECKPOINT, "Gashy(b) looks like: '%(gashy)'", "gashy", gashy);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }

        {
            Factory_Aggregate cf12c = new Factory_Aggregate(context);
            cf12c.setAggregateClass(context, "my/gool/test3");
            cf12c.setParameters(context, new Hashtable());

            Object gashy;
            try {
                gashy = cf12c.create(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Gashy(c) ist ein: '%(gashyclass)'", "gashyclass", gashy.getClass().getName());
                cc.sendTrace(context, Notifier.CHECKPOINT, "Gashy(c) looks like: '%(gashy)'", "gashy", gashy);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }

        try {
            cc.sendTrace(context, Notifier.CHECKPOINT, "And now, ladies and gentlemen...");
            Locator l = Factory_Locator.tryConstruct(context, "ctn://OCP/com.sphenon.engines.factorysite.test.Willy/Werner=Blub/Hans=ber");
            Willy lw = (Willy) l.retrieveTarget(context);
            cc.sendTrace(context, Notifier.CHECKPOINT, "...ein Willy via locator mit einem member '%(Hallo)'", "Hallo", lw.getHallo(context));
        } catch (InvalidLocator il) {
            cc.sendError(context, "Mist: '%(error)'", "error", il);
            return;
        }

        try {
            cc.sendTrace(context, Notifier.CHECKPOINT, "Again...");
            Locator l = Factory_Locator.tryConstruct(context, "ctn://OCP/com.sphenon.engines.factorysite.test.Willy/Werner=Blub/Hans=(String/IDREF=HansPara)");
            Hashtable paras = new Hashtable();
            paras.put("HansPara", "bel");
            Willy lw = (Willy) l.retrieveTarget(context, paras);
            cc.sendTrace(context, Notifier.CHECKPOINT, "...ein Willy via locator mit einem member '%(Hallo)'", "Hallo", lw.getHallo(context));
        } catch (InvalidLocator il) {
            cc.sendError(context, "Mist: '%(error)'", "error", il);
            return;
        }

        try {
            cc.sendTrace(context, Notifier.CHECKPOINT, "And again...");
            Locator l = Factory_Locator.tryConstruct(context, "ctn://OCP/com.sphenon.engines.factorysite.test.Willy/Werner=Blub/Hans=(String/OPTIONALIDREF=HansPara/bin_nicht_das_ergebnis)");
            Hashtable paras = new Hashtable();
            paras.put("HansPara", "bel");
            Willy lw = (Willy) l.retrieveTarget(context, paras);
            cc.sendTrace(context, Notifier.CHECKPOINT, "...ein Willy via locator mit einem member '%(Hallo)'", "Hallo", lw.getHallo(context));
        } catch (InvalidLocator il) {
            cc.sendError(context, "Mist: '%(error)'", "error", il);
            return;
        }

        try {
            cc.sendTrace(context, Notifier.CHECKPOINT, "And again...");
            Locator l = Factory_Locator.tryConstruct(context, "ctn://OCP/com.sphenon.engines.factorysite.test.Willy/Werner=Blub/Hans=(String/OPTIONALIDREF=HansPara/ich_bin_das_ergebnis)");
            Hashtable paras = new Hashtable();
            Willy lw = (Willy) l.retrieveTarget(context, paras);
            cc.sendTrace(context, Notifier.CHECKPOINT, "...ein Willy via locator mit einem member '%(Hallo)'", "Hallo", lw.getHallo(context));
        } catch (InvalidLocator il) {
            cc.sendError(context, "Mist: '%(error)'", "error", il);
            return;
        }

        {
            Factory_Aggregate cf12 = new Factory_Aggregate(context);
            cf12.setAggregateClass(context, "my;class=Tree<Factory<Object>>");
            cf12.setParameters(context, new Hashtable());

            Vector_Pair_String_Object__long_ ocp_tree;
            try {
                ocp_tree = (Vector_Pair_String_Object__long_) cf12.create(context);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }

            dump(context, ocp_tree, "  ");
        }

        {
            Factory_Aggregate cf13 = new Factory_Aggregate(context);
//             try {
//                 cf13.setAggregateTreeNode(context, Factory_TreeNode.construct(context, "ctn://File//workspace/sphenon/projects/components/engines/factorysite/v0001/origin/examples3"));
//             } catch (ValidationFailure vf) {
//                 cc.sendError(context, "Mist: '%(error)'", "error", vf);
//                 return;
//             }
            cf13.setAggregateClass(context, "//File//workspace/sphenon/projects/components/engines/factorysite/v0001/origin/examples3");
            cf13.setAggregateTargetClass(context, "Tree<Willy>");
            cf13.setParameters(context, new Hashtable());

            Object o131;
            try {
                o131 = cf13.create(context);
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }

            cc.sendTrace(context, Notifier.CHECKPOINT, "Treewilly in explicit tree: %(class) - %(object)", "class", o131.getClass().toString(), "object", o131);
            Hashtable h131 = (Hashtable) o131;

            Object o132 = h131.get("my");
            cc.sendTrace(context, Notifier.CHECKPOINT, "          my -> %(class) - %(object)", "class", o132 == null ? null : o132.getClass().toString(), "object", o132);
            Hashtable h132 = (Hashtable) o132;
            Object o133 = h132.get("tuut");
            cc.sendTrace(context, Notifier.CHECKPOINT, "        tuut -> %(class) - %(object)", "class", o133 == null ? null : o133.getClass().toString(), "object", o133);
            Hashtable h133 = (Hashtable) o133;
            Object o134 = h133.get("piep");
            cc.sendTrace(context, Notifier.CHECKPOINT, "        piep -> %(class) - %(object)", "class", o134 == null ? null : o134.getClass().toString(), "object", o134);
        }

        {
            Factory_Aggregate cf14 = new Factory_Aggregate(context);
            cf14.setAggregateClass(context, "my/cool/test6");
            cf14.setParameters(context, new Hashtable());

            Object hashy;
            try {
                hashy = cf14.create(context);
                if (hashy != null) {
                    cc.sendError(context, "test5 should be NULL");
                    return;
                }
            } catch (ExceptionError e) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", e);
                return;
            }
        }

        TOCPASTNode tan = TOCPASTNode.parseTOCP(context, "Willy<Hashtable~factorysite_test> = { Hans<String> = \"hallo\", Fritz<String> = \"holla\" }");
        Pair_BuildText_String_ pbts = TOCPBuildText.create(context, tan);
        Dumper.dump(context, null, pbts.getItem1(context));

        Object otocp = Factory_Aggregate.construct(context, pbts.getItem1(context), tan.getNameSpace(context));
        Hashtable htocp = (Hashtable) otocp;
        cc.sendTrace(context, Notifier.CHECKPOINT, "TOCP object 1, element 'Hans': '%(value)'", "value", htocp.get("Hans"));
        cc.sendTrace(context, Notifier.CHECKPOINT, "TOCP object 1, element 'Fritz': '%(value)'", "value", htocp.get("Fritz"));

        {   Factory_Aggregate cfreload = new Factory_Aggregate(context);
            cfreload.setAggregateClass(context, "my/cool/reloadable");
            cfreload.setParameters(context, new Hashtable());

            Interface reloadable;
            try {
                reloadable = (Interface) cfreload.createReloadable(context);
                cc.sendTrace(context, Notifier.CHECKPOINT, "Reloadable ist ein: '%(class)'", "class", reloadable.getClass().getName());
            } catch (Throwable t) {
                cc.sendTrace(context, Notifier.CHECKPOINT, "Could not build object aggregate: %(reason)", "reason", t);
                return;
            }

            for (int i=0; i<reload_times; i++) {
                if (i != 0) {
                    try {
                        Thread.currentThread().sleep(3000);
                    } catch (Throwable t) {
                    }
                }
                cc.sendTrace(context, Notifier.CHECKPOINT, "Reloadable 'name' is: '%(name)'", "name", reloadable.getName(context));
            }
        }
    }

    static protected void dump(CallContext context, Vector_Pair_String_Object__long_ vector, String indent) {
        for (Pair_String_Object_ pair : vector.getIterable_Pair_String_Object__(context)) {
            String name = pair.getItem1(context);
            Object value = pair.getItem2(context);
            if (value instanceof Vector_Pair_String_Object__long_) {
                NotificationContext.sendCheckpoint(context, indent + name);
                dump(context, (Vector_Pair_String_Object__long_) value, indent + "    ");
            } else {
                NotificationContext.sendCheckpoint(context, indent + name + " = " + value.toString());
            }
        }
    }
}
