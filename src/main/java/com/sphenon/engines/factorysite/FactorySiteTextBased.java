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
import com.sphenon.basics.tracking.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.performance.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.factory.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.javacode.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.factories.Factory_Aggregate;
import com.sphenon.engines.factorysite.tplinst.*;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

import java.util.regex.Matcher;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class FactorySiteTextBased
  implements FactorySite
{
    static final public Class _class = FactorySiteTextBased.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    static protected long runtimestep_level;
    static public    long adjustRuntimeStepLevel(long new_level) { long old_level = runtimestep_level; runtimestep_level = new_level; return old_level; }
    static public    long getRuntimeStepLevel() { return runtimestep_level; }
    static { runtimestep_level = RuntimeStepLocationContext.getLevel(_class); };

    static protected Configuration config;
    static { config = Configuration.create(RootContext.getInitialisationContext(), _class); };

    static protected enum DEBUG { BREAK_AT };
    static protected boolean[] debug_features;
    static public    boolean[] adjustDebugFeatures(boolean[] new_features) { boolean[] old_features = debug_features; debug_features = new_features; return old_features; }
    static public    boolean[] getDebugFeatures() { return debug_features; }
    static { debug_features = DebugFeatures.getFeatures(_class, DEBUG.class); };

    private DataSource main_data_source;
    private DataSource main_data_source_internal;
    private Map_DataSource_String_ oidmap;
    private Vector_Scaffold_long_ all_scaffolds;
    private Vector_Scaffold_long_ origin_aware_scaffolds;
    protected Map_DataSourceConnector_String_ parametermap;
    protected Map_DataSourceConnector_String_ referencemap;

    // folgendes erwies sich als ernsthaftes Problem:
    // ----------------------------------------------------------------------------
    // ACHTUNG! Bei diesen Einträgen ist die Reihenfolge wichtig! Wenn ein OCP ein
    // anderes benötigt, für diese Konstruktion aber schon die Defaults notwendig
    // sind, diese dafür aber noch nicht registriert sind, funktionert das nicht
    // mehr. Leider wird für das Registrieren schon auf die Supertypes
    // zurückgrgriffen, und diese erfordern in der TypeImpl_Aggregate wiederum die
    // Factorysite (bzw. den Hauptdatentyp des OCPs).
    // Sollten hierdurch eines Tages Zyklen entstehen, wird's haarig. Dann muß die
    // Haupttypermittlung vom Durchbauen des OCP entkoppelt werden.
    // Beispiel für Effekt: hier Reiehnfolge umkehren (also PlainJavaLibrary
    // vorziehen)
    // ----------------------------------------------------------------------------
    // das Phänomen tauchte noch mehrfach auf, auch im Zusammenhang mit dem
    // ScaffoldCache: es gibt sehr häßliche Zyklen, da:
    // - für den Aufbau von OCPs die Aliases und Defaults benötigt werden
    // - für die OMap Einträge der Aliases und Defaults die Aggregate-Supertypen
    //   benötigt werden
    // - für die Aggregate-Supertypen-Ermittlung aber leider die OCPs aufgebaut
    //   sein müssen - selbst für den root-Typ kann sogar schon wiederum ein
    //   Alias erforderlich sein
    // ----------------------------------------------------------------------------
    // daher nun erstmal eine einfache HashMap statt einer OMap -- möglicherweise
    // ist das sowieso richtiger
    // --
    // bei genauer Betrachtung ist es in der Tat sogar (partiell) falsch, wenn
    // hier OMaps verwendet werden: ein Alias im Scope A kann im Scope einer
    // abgeleiteten Klasse B sogar völlig falsch sein -- er könnte passen, bspw.
    // wenn er wiederum eine von B abgeleitete Klasse C repräsentiert, aber er
    // könnte auch nicht passen. Hier ist sowieso noch eine elegantere Logik
    // zur halb-automatischen (?), in jedem Fall aber bequemeren Definition
    // der Aliase vonnöten

    static protected HashMap<TypeInTypeContext,Map_BuildTextExpansion_String_>      aliases_by_scope_type;
    static protected HashMap<TypeInTypeContext,Map_BuildTextExpansion_String_>      aliases_by_item_type;

    static protected HashMap<TypeInTypeContext,Vector<Pair<String,BuildTextExpansion>>>   re_aliases_by_scope_type;
    static protected HashMap<TypeInTypeContext,Vector<Pair<String,BuildTextExpansion>>>   re_aliases_by_item_type;

    static protected HashMap<TypeInTypeContext,Vector_Pair_BuildText_String__long_> defaults_by_scope_type;

    static protected RegularExpression definere = new RegularExpression("([^: ]+)(?: *):(?: *)(.*)");

    static protected class TypeInTypeContext {
        public TypeInTypeContext(Type type, String type_context) {
            this.type = type;
            this.type_context = (type_context == null ? "DEFAULT" : type_context);
        }
        public Type   type;
        public String type_context;
        public boolean equals(Object o) {
            return (o != null && ((TypeInTypeContext)o).type.equals(type) && ((TypeInTypeContext)o).type_context.equals(type_context));
        }
        public int hashCode() {
            return (type == null ? 0 : type.hashCode()) ^ (type_context == null ? 0 : type_context.hashCode());
        }
    }

    static public void registerAliasForScopeType (CallContext context, String type_context, Type scope_type, String alias_name, BuildTextExpansion expansion) {
        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Registering alias '%(alias)' for scope type '%(scopetype)' in type context '%(typecontext)' : '%(expansion)'", "alias", alias_name, "scopetype", scope_type, "typecontext", type_context, "expansion", expansion); }
        
        TypeInTypeContext titc = new TypeInTypeContext(scope_type, type_context);

        if (alias_name != null && alias_name.length() > 0 && alias_name.charAt(0) == '~') {
            if (re_aliases_by_scope_type == null) {
                re_aliases_by_scope_type = new HashMap<TypeInTypeContext,Vector<Pair<String,BuildTextExpansion>>>();
            }
            Vector<Pair<String,BuildTextExpansion>> vpsbte = re_aliases_by_scope_type.get(titc);
            if (vpsbte == null) {
                vpsbte = new Vector<Pair<String,BuildTextExpansion>>();
                re_aliases_by_scope_type.put(titc, vpsbte);
            }
            vpsbte.add(new Pair<String,BuildTextExpansion>(context, alias_name.substring(1), expansion));
        } else {
            if (aliases_by_scope_type == null) {
                aliases_by_scope_type = new HashMap<TypeInTypeContext,Map_BuildTextExpansion_String_>(); // OMapImpl_Map_BuildTextExpansion_String__Type_(context);
            }
            Map_BuildTextExpansion_String_ mbtes = aliases_by_scope_type.get(titc);
            if (mbtes == null) {
                mbtes = new MapImpl_BuildTextExpansion_String_(context);
                aliases_by_scope_type.put(titc, mbtes);
            }
            mbtes.set(context, alias_name, expansion);
        }
    } 

    static public void registerAliasForItemType (CallContext context, String type_context, Type item_type, String alias_name, BuildTextExpansion expansion) {
        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Registering alias '%(alias)' for item type '%(itemtype)' in type context '%(typecontext)' : '%(expansion)'", "alias", alias_name, "itemtype", item_type, "typecontext", type_context, "expansion", expansion); }

        TypeInTypeContext titc = new TypeInTypeContext(item_type, type_context);

        if (alias_name != null && alias_name.length() > 0 && alias_name.charAt(0) == '~') {
            if (re_aliases_by_item_type == null) {
                re_aliases_by_item_type = new HashMap<TypeInTypeContext,Vector<Pair<String,BuildTextExpansion>>>();
            }
            Vector<Pair<String,BuildTextExpansion>> vpsbte = re_aliases_by_item_type.get(titc);
            if (vpsbte == null) {
                vpsbte = new Vector<Pair<String,BuildTextExpansion>>();
                re_aliases_by_item_type.put(titc, vpsbte);
            }
            vpsbte.add(new Pair<String,BuildTextExpansion>(context, alias_name.substring(1), expansion));
        } else {
            if (aliases_by_item_type == null) {
                aliases_by_item_type = new HashMap<TypeInTypeContext,Map_BuildTextExpansion_String_>(); //OMapImpl_Map_BuildTextExpansion_String__Type_(context);
            }
            Map_BuildTextExpansion_String_ mbtes = aliases_by_item_type.get(titc);
            if (mbtes == null) {
                mbtes = new MapImpl_BuildTextExpansion_String_(context);
                aliases_by_item_type.put(titc, mbtes);
            }
            mbtes.set(context, alias_name, expansion);
        }
    } 

    static public void registerDefaultForScopeType (CallContext context, String type_context, Type scope_type, String name, BuildText build_text) {
        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Registering default '%(name)' for scope type '%(scopetype)' in type context '%(typecontext)' : '%(buildtext)'", "name", name, "scopetype", scope_type, "typecontext", type_context, "buildtext", build_text); }
        if (defaults_by_scope_type == null) {
            defaults_by_scope_type  = new HashMap<TypeInTypeContext,Vector_Pair_BuildText_String__long_>();
        }
        TypeInTypeContext titc = new TypeInTypeContext(scope_type, type_context);
        Vector_Pair_BuildText_String__long_ vpbtsl = defaults_by_scope_type.get(titc);
        if (vpbtsl == null) {
            vpbtsl = Factory_Vector_Pair_BuildText_String__long_.construct(context);
            defaults_by_scope_type.put(titc, vpbtsl);
        }
        Pair_BuildText_String_ pbts = new Pair_BuildText_String_(context, build_text, name);
        vpbtsl.append(context, pbts);
    } 

    static public BuildTextExpansion getAliasForScopeType (CallContext context, String type_context, Type scope_type, String alias_name) {
        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Retrieving alias '%(alias)' for scope type '%(scopetype)' in type context '%(typecontext)'", "alias", alias_name, "scopetype", scope_type, "typecontext", type_context); }
        if (scope_type == null || alias_name == null) { return null; }

        if (aliases_by_scope_type != null) {
            TypeInTypeContext titc = new TypeInTypeContext(scope_type, type_context);
            Map_BuildTextExpansion_String_ mbtes = aliases_by_scope_type.get(titc);
            if (mbtes == null) { return null; }
            BuildTextExpansion bte = mbtes.tryGet(context, alias_name);
            if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Got '%(expansion)'", "expansion", bte); }
            return bte;
        }

        if (re_aliases_by_scope_type != null) {
            TypeInTypeContext titc = new TypeInTypeContext(scope_type, type_context);
            Vector<Pair<String,BuildTextExpansion>> vpsbte = re_aliases_by_scope_type.get(titc);
            if (vpsbte == null) { return null; }
            for (Pair<String,BuildTextExpansion> psbts : vpsbte) {
                if (alias_name.matches(psbts.getItem1(context))) {
                    BuildTextExpansion bte = psbts.getItem2(context);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Got '%(expansion)'", "expansion", bte); }
                    return bte;
                }
            }
        }

        return null;
    }

    static public BuildTextExpansion getAliasForItemType (CallContext context, String type_context, Type item_type, String alias_name) {
        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Retrieving alias '%(alias)' for item type '%(itemtype)' in type context '%(typecontext)'", "alias", alias_name, "itemtype", item_type, "typecontext", type_context); }
        if (item_type == null || alias_name == null) { return null; }

        if (aliases_by_item_type != null) {
            TypeInTypeContext titc = new TypeInTypeContext(item_type, type_context);
            Map_BuildTextExpansion_String_ mbtes = aliases_by_item_type.get(titc);
            if (mbtes == null) { return null; }
            BuildTextExpansion bte = mbtes.tryGet(context, alias_name);
            if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Got '%(expansion)'", "expansion", bte); }
            return bte;
        }

        if (re_aliases_by_item_type != null) {
            TypeInTypeContext titc = new TypeInTypeContext(item_type, type_context);
            Vector<Pair<String,BuildTextExpansion>> vpsbte = re_aliases_by_scope_type.get(titc);
            if (vpsbte == null) { return null; }
            for (Pair<String,BuildTextExpansion> psbts : vpsbte) {
                if (alias_name.matches(psbts.getItem1(context))) {
                    BuildTextExpansion bte = psbts.getItem2(context);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Got '%(expansion)'", "expansion", bte); }
                    return bte;
                }
            }
        }

        return null;
    }

    static public Vector_Pair_BuildText_String__long_ getDefaultsForScopeType (CallContext context, String type_context, Type scope_type) {
        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Retrieving defaults for scope type '%(scopetype)' in type context '%(typecontext)'", "scopetype", scope_type, "typecontext", type_context); }
        if (scope_type == null) { return null; }
        if (defaults_by_scope_type == null) { return null; }
        TypeInTypeContext titc = new TypeInTypeContext(scope_type, type_context);
        Vector_Pair_BuildText_String__long_ vpbtsl = defaults_by_scope_type.get(titc);
        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Got '%(defaults)'", "defaults", vpbtsl); }
        return vpbtsl;
    }

    private HashMap<String,Vector<Pair<String,Object>>> meta_data = null;

    public HashMap<String,Vector<Pair<String,Object>>> getMetaData (CallContext context) {
        return this.meta_data;
    }

    public Vector<Pair<String,Object>> getMetaData (CallContext context, String key) {
        return this.meta_data == null ? null : this.meta_data.get(key);
    }

    protected void addMetaData (CallContext context, String key, Object meta_data, String path) {
        if (this.meta_data == null) {
            this.meta_data = new HashMap<String,Vector<Pair<String,Object>>>();
        }
        
        Vector<Pair<String,Object>> mdv = this.meta_data.get(key);
        if (mdv == null) {
            mdv = new Vector<Pair<String,Object>>();
            this.meta_data.put(key, mdv);
        }
        mdv.add(new Pair<String,Object>(context, path, meta_data));
    }

    static protected Type dynamic_parameter_type = null;

    protected String site_id;

    protected String type_context_id;

    protected int passes;

    protected LocationContext location_context;

    protected String default_evaluator;

    public String getDefaultEvaluator (CallContext context) {
        return this.default_evaluator;
    }

    public String getSiteId(CallContext context) {
        return this.site_id;
    }

    public FactorySiteTextBased (CallContext context, BuildText build_text, String site_id) throws PutUpFailure {
        init(context, build_text, site_id, null, null, null);
    }

    public FactorySiteTextBased (CallContext context, BuildText build_text, String site_id, Type expected_type) throws PutUpFailure {
        init(context, build_text, site_id, expected_type, null, null);
    }

    public FactorySiteTextBased (CallContext context, BuildText build_text, String site_id, Type expected_type, LocationContext location_context) throws PutUpFailure {
        init(context, build_text, site_id, expected_type, location_context, null);
    }

    public FactorySiteTextBased (CallContext context, BuildText build_text, String site_id, Type expected_type, LocationContext location_context, JavaCodeManager jcm) throws PutUpFailure {
        init(context, build_text, site_id, expected_type, location_context, jcm);
    }

    static protected StopWatch stop_watch;
    static protected boolean   initialised;

    protected void init (CallContext context, BuildText build_text, String site_id, Type expected_type, LocationContext location_context, JavaCodeManager jcm) throws PutUpFailure {

        if (initialised == false) {
            initialised = true;
            stop_watch = StopWatch.optionallyCreate(context, _class, "factorysite", Notifier.SELF_DIAGNOSTICS);
        }
        if (this.stop_watch != null) { this.stop_watch.start(context, null, "initbegin"); }

        if (dynamic_parameter_type == null) {
            dynamic_parameter_type = TypeManager.get(context, Vector_ScaffoldParameter_long_.class);
        }

        this.site_id = site_id;
        this.location_context = location_context;

        context = Context.create(context, this.location_context);

        TypeContext tc = TypeContext.get((Context)context);
        this.type_context_id = (tc == null ? "DEFAULT" : tc.getTypeContextId(context));

        this.oidmap = new MapImpl_DataSource_String_(context);
        this.all_scaffolds = Factory_Vector_Scaffold_long_.construct(context);
        this.origin_aware_scaffolds = Factory_Vector_Scaffold_long_.construct(context);
        this.parametermap = new MapImpl_DataSourceConnector_String_(context);
        this.referencemap = new MapImpl_DataSourceConnector_String_(context);

        this.passes = 1;

        this.default_evaluator = "js";

        if (this.stop_watch != null) { this.stop_watch.show(context, null, "putup"); }

        this.main_data_source_internal = putUp(context, expected_type, build_text, jcm);
        this.main_data_source = new DataSourceFactorySiteMain(context, this.main_data_source_internal);

        if (this.stop_watch != null) { this.stop_watch.show(context, null, "initend"); }
        this.current_pass = 1;
        this.interruption_pass = -1;

        this.in_use = false;
    }

    public long getLastModification (CallContext context) {
        long last_modification = 0;
        for (Iterator_Scaffold_ is = this.origin_aware_scaffolds.getNavigator(context); is.canGetCurrent(context); is.next(context)) {
            long lm = ((OriginAware) (is.tryGetCurrent(context))).getOrigin(context).getLastModification(context);
            if (lm > last_modification) { last_modification = lm; }
        }
        return last_modification;
    }

    public DataSource getDataSourceById(CallContext context, String oid) {
        return this.oidmap == null ? null : this.oidmap.tryGet(context, oid);
    }

    protected class DataSourceFactorySiteMain implements DataSource {
        protected DataSource data_source;
        public DataSourceFactorySiteMain(CallContext context, DataSource data_source) {
            this.data_source = data_source;
        }
        public Type getType(CallContext context) {
            return this.data_source.getType(context);
        }
        public Object getValueAsObject(CallContext context) throws DataSourceUnavailable {
            if (FactorySiteTextBased.this.in_use) {
                CustomaryContext.create((Context)context).throwLimitation(context, "FactorySite '%(siteid)' invoked recursively", "siteid", FactorySiteTextBased.this.site_id);
                throw (ExceptionLimitation) null; // compilernsists
            }

            FactorySiteTextBased.this.in_use = true;

            boolean pending_passes = false;
            try {
                FactorySiteTextBased.this.current_pass = 1;

                if ((FactorySiteTextBased.this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Building '%(siteid)', main, pass 1...", "siteid", FactorySiteTextBased.this.site_id); }

                Object result = this.data_source.getValueAsObject(context);
                
                if( Factory_Aggregate.debug_classloader ) {
                    Factory_Aggregate.debugClassLoader("FactorySiteTextBase.DataSourceFactorySiteMain", this.getClass());
                    if (result != null) {
                        Factory_Aggregate.debugClassLoader("FactorySiteTextBase.DataSourceFactorySiteMain.result_of_getValueAsObject", result.getClass());
                    }
                }
                
                if ((FactorySiteTextBased.this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Building '%(siteid)', main, pass 1 - done, result: '%(result)'", "siteid", FactorySiteTextBased.this.site_id, "result", result); }

                if (performPasses(context)) {
                    pending_passes = false;
                    if ((FactorySiteTextBased.this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Building '%(siteid)', main, all passes done", "siteid", FactorySiteTextBased.this.site_id); }
                } else {
                    pending_passes = true;
                    if ((FactorySiteTextBased.this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Building '%(siteid)', main, returning, but only partially initialised due to by request outstanding passes", "siteid", FactorySiteTextBased.this.site_id); }
                }

                return result;

            } catch (IgnoreErraneousDataSource ieds) {
                DataSourceUnavailable.createAndThrow(context, ieds, "Cannot ignore top level erraneous data source (where did the problems went to?)");
                throw (DataSourceUnavailable) null; // compiler insists
            } finally {
                if (pending_passes == false) {
                    cleanupAfterBuild(context);
                }
            }
        }
//         public void compile(CallContext context, java.io.PrintStream code, String var_prefix, String indent, Vector_String_long_ pars, boolean do_not_append_par) throws DataSourceUnavailable {
//             this.data_source.compile(context, code, var_prefix, indent, pars, do_not_append_par);
//         }
        public String getSourceLocationInfo(CallContext context) {
            return FactorySiteTextBased.this.site_id + "<main>";
        }
    }

    protected void cleanupAfterBuild(CallContext context) {
        this.is_prebuilded = false;
        this.resetParameters(context);
        this.in_use = false;
        this.root_scope = null;
        this.current_scope = null;
        for (Iterator_Scaffold_ is = all_scaffolds.getNavigator(context); is.canGetCurrent(context); is.next(context)) {
            is.tryGetCurrent(context).reset(context);
        }
    }

    public boolean performPasses(CallContext context, int ip) throws BuildFailure {
        try {
            this.setInterruptionPass (context, ip);
            return performPasses(context);
        } catch (DataSourceUnavailable dsu) {
            BuildFailure.createAndThrow (context, dsu, FactorySiteStringPool.get(context, "0.5.16" /* Could not create main object */));
            throw (BuildFailure) null; // compiler insists
        } catch (IgnoreErraneousDataSource ieds) {
            BuildFailure.createAndThrow (context, ieds, "Cannot ignore top level erraneous data source (where did the problems went to?)");
            throw (BuildFailure) null; // compiler insists
        }
    }

    public boolean performPasses(CallContext context) throws DataSourceUnavailable, IgnoreErraneousDataSource {
        boolean pending_passes = false;
        try {
            for (this.current_pass = 2; this.current_pass <= passes && (this.interruption_pass == -1 || this.current_pass < this.interruption_pass); this.current_pass++) {
                Class_Scope local_scope = new Class_Scope(context, null, this.getCurrentScope(context));

                if (this.postponed_pre_conditions != null) {
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Building '%(siteid)', main, pass %(pass), verifying PreConditions...", "siteid", this.site_id, "pass", this.current_pass); }
                    for (BuildAssertion ba : this.postponed_pre_conditions) {
                        if (ba.getScaffold(context).isSkipped(context) == false) {
                            ba.setScope(context, local_scope);
                            ba.check(context, this.current_pass);
                        }
                    }
                }

                if (this.postponed_pre_build_scripts != null) {
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Building '%(siteid)', main, pass %(pass), evaluating PreBuildScripts...", "siteid", this.site_id, "pass", this.current_pass); }
                    for (BuildScript bs : this.postponed_pre_build_scripts) {
                        if (bs.getScaffold(context).isSkipped(context) == false) {
                            bs.setScope(context, local_scope);
                            bs.evaluate(context, this.current_pass);
                        }
                    }
                }

                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Building '%(siteid)', main, pass %(pass), building...", "siteid", this.site_id, "pass", this.current_pass); }
                for (Scaffold scaffold : this.all_scaffolds.getIterable_Scaffold_(context)) {
                    if (scaffold.getPass(context) == this.current_pass) {
                        scaffold.getValueAsObject(context);
                    }
                }

                // Update:
                // a) das folgende ist letztlich nicht nötig, weil die Results
                // immer auch über OIDs erreichbar sind, was auch den Vorteil
                // hat, daß auch auf Ergebnisse anderer Scaffolds zurückgegriffen
                // werden kann, insofern kann man sich das setzen und entfernen
                // von results sparen (bzw. neue local scopes, weil die results
                // ja auch wieder sauber raus sollten
                // b) die scaffolds werden aber nun auch mitregistriert an den
                // BuildScript/BuildAssertion Instanzen weil sie wegen "skip"
                // abgefragt werden müssen
                //
                // Old:
                // hier müßte noch sowas hin, ist aber knifflig, weil die
                // passes von pre, create und post durchaus abweichen
                // können, d.h. hier würde eine hashtable oder so
                // erfordert, die zu jedem scaffold das ergebnis
                // verzeichnet und zudem an den scripts müßte dieses
                // scaffold noch angeklebt werden
                // local_scope.set(context, "result", result);
                
                if (this.postponed_post_build_scripts != null) {
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Building '%(siteid)', main, pass %(pass), evaluating PostBuildScripts...", "siteid", this.site_id, "pass", this.current_pass); }
                    for (BuildScript bs : this.postponed_post_build_scripts) {
                        if (bs.getScaffold(context).isSkipped(context) == false) {
                            bs.setScope(context, local_scope);
                            bs.evaluate(context, this.current_pass);
                        }
                    }
                }
                
                if (this.postponed_post_conditions != null) {
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Building '%(siteid)', main, pass %(pass), verifying PostConditions...", "siteid", this.site_id, "pass", this.current_pass); }
                    for (BuildAssertion ba : this.postponed_post_conditions) {
                        if (ba.getScaffold(context).isSkipped(context) == false) {
                            ba.setScope(context, local_scope);
                            ba.check(context, this.current_pass);
                        }
                    }
                }
            }
            pending_passes = (this.current_pass <= passes ? true : false);
        } finally {
            if (pending_passes == false) {
                cleanupAfterBuild(context);
            }
        }

        return (pending_passes ? false : true);
    }

    public DataSource getMainDataSource (CallContext context) {
        return this.main_data_source;
    }

    public DataSource getInternalMainDataSource (CallContext context) {
        return this.main_data_source_internal;
    }

    protected class DataSource_FSScope implements DataSource {
        public DataSource_FSScope(CallContext context) { }
        public Type getType(CallContext context) { return TypeManager.get(context, Object.class); }
        public Object getValueAsObject(CallContext context) throws DataSourceUnavailable {
            return FactorySiteTextBased.this.getCurrentScope(context);
        }
        public void compile(CallContext context, java.io.PrintStream code, String var_prefix, String indent, Vector_String_long_ pars, boolean do_not_append_par) throws DataSourceUnavailable {
            // [ToBeImplemented]
        }
        public String getSourceLocationInfo(CallContext context) {
            return FactorySiteTextBased.this.site_id + "<fsscope>";
        }
    }

    static public class Coder {
        protected Vector<Vector<Integer>> data;
        protected StringWriter codesw;
        protected StringWriter textsw1;
        protected StringWriter textsw2;
        protected Map<String,Integer> codes;
        protected Map<String,Integer> texts;
        int code_index;
        int text_index;
        public Coder(CallContext context) {
            this.data = new Vector<Vector<Integer>>();
            this.codesw = new StringWriter();
            this.textsw1 = new StringWriter();
            this.textsw2 = new StringWriter();
            this.codes = new HashMap<String,Integer>();
            this.texts = new HashMap<String,Integer>();
            this.code_index = 1;
            this.text_index = 2;
            this.codesw.write("            case 0 : return null;\n");
            this.textsw1.write("    static protected final String NULL = null;\n");
            this.textsw1.write("    static protected final String EMPTY = \"\";\n");
            this.textsw2.write("            case 0 : return NULL;\n");
            this.textsw2.write("            case 1 : return EMPTY;\n");
            this.texts.put("", 1);
        }
        public Vector<Vector<Integer>> getData(CallContext context) {
            return this.data;
        }
        public StringWriter getCodeWriter(CallContext context) {
            return this.codesw;
        }
        public StringWriter getTextWriter1(CallContext context) {
            return this.textsw1;
        }
        public StringWriter getTextWriter2(CallContext context) {
            return this.textsw2;
        }
        public Vector<Integer> createDataRow(CallContext context) {
            Vector<Integer> v = new Vector<Integer>();
            this.data.add(v);
            return v;
        }
        public int writeBoolean(CallContext context, boolean b) {
            return (b ? 1 : 0);
        }
        public int writeCode(CallContext context, String code) {
            Integer try_index = (code == null ? ((Integer) 0) : this.codes.get(code));
            if (try_index != null) { return try_index; }
            int index = this.code_index++;
            this.codesw.write("            case " + index + " : return " + code + ";\n");
            this.codes.put(code, index);
            return index;
        }
        public int writeText(CallContext context, String text) {
            int scindex = StringCache.getSingleton(context).putText(context, text);
            return scindex;
        }
    }

    protected DataSource putUp (CallContext context, Type expected_type, BuildText build_text, JavaCodeManager jcm) throws PutUpFailure {
        Vector<String> scaffolds_to_register = jcm != null ? new Vector<String>() : null;

        Coder coder = null;
        if (jcm != null) {
            StringWriter sw = new StringWriter();
            sw.write("    static protected final int[][] cocp_data = {\n");
            writeToJCM(context, jcm, sw.toString());
            coder = new Coder(context);
        }

        DataSource result = this._putUp(context, expected_type, build_text, "", true, null, false, true, jcm, coder, "", null, scaffolds_to_register);

        if (jcm != null) {
            StringWriter sw = new StringWriter();
            sw.write("        { }\n"); // dummy entry
            sw.write("    };\n");
            sw.write("\n");
            sw.write("    protected Object evaluateCOCPCode(CallContext context, int index) {\n");
            sw.write("        switch(index) {\n");
            sw.write(coder.getCodeWriter(context).toString());
            sw.write("        }\n");
            sw.write("        return null;\n");
            sw.write("    }\n");
            sw.write("\n");
            sw.write(coder.getTextWriter1(context).toString());
            sw.write("\n");
            sw.write("    protected String getCOCPText(CallContext context, int index) {\n");
            sw.write("        switch(index) {\n");
            sw.write(coder.getTextWriter2(context).toString());
            sw.write("        }\n");
            sw.write("        return null;\n");
            sw.write("    }\n");
            writeToJCM(context, jcm, sw.toString());
            writeToJCM(context, jcm, coder.getData(context));
        }

        for (IteratorItemIndex_DataSourceConnector_String_ iiids = referencemap.getNavigator(context); iiids.canGetCurrent(context); iiids.next(context)) {
            DataSourceConnector dsc = iiids.tryGetCurrent(context);
            String oidref = iiids.tryGetCurrentIndex(context);
            DataSource ds = null;
            try {
                ds = this.oidmap.get(context, oidref);
                dsc.setDataSource(context, ds);
            } catch (DoesNotExist dne) {
                DataSourceConnector_Id dsci = null;
                if (dsc instanceof DataSourceConnector_Id && (dsci = ((DataSourceConnector_Id) dsc)).getParameterEnabled(context)) {
                    String dscid = ((DataSourceConnector_Id) dsc).getId(context); // ist wohl das gleiche wie oidref, aber dennoch...
                    if (dscid.equals("*")) {
                        DataSource dsfsc = new DataSource_FSScope(context);
                        try {
                            dsc.setDataSource(context, dsfsc);
                        } catch (TypeMismatch e) {
                            PutUpFailure.createAndThrow (context, "Reference to Id #%(idref) expects type '%(expected)', but refered object is of type '%(got)' ", "idref", dscid, "expected", dsc.getType(context).getName(context), "got", dsfsc.getType(context).getName(context));
                        }
                    } else {
                        DataSourceConnector pdsc = parametermap.tryGet(context, dscid);
                        if (pdsc == null) {
                            pdsc = new DataSourceConnector_Parameter(context, dsc.getType(context), dscid, dsci.getHigherRanking(context), dsci.isOptional(context), false, this.site_id + "<Parameter:" + dscid + ">");
                            parametermap.set(context, dscid, pdsc);
                        } else {
                            Type pdsctype = pdsc.getType(context);
                            if (pdsctype == null) {
                                pdsc.setType(context, dsc.getType(context));
                            } else {
                                if (dsc.getType(context) == null || pdsctype.equals(dsc.getType(context)) || pdsctype.isA(context, dsc.getType(context))) {
                                    // ok
                                } else {
                                    if (dsc.getType(context).isA(context, pdsctype)) {
                                        pdsc.setType(context, dsc.getType(context));
                                    } else {
                                        PutUpFailure.createAndThrow (context, "Parameter '%(parname)' is used inconsistent: one occurence expects a type '%(expected1)', while the other (a general id ref) occurence expects '%(expected2)'", "parname", dscid, "expected1", pdsctype.getName(context), "expected2", dsc.getType(context).getName(context));
                                    }
                                }
                            }
                        }
                        try {
                            dsc.setDataSource(context, pdsc);
                        } catch (TypeMismatch e) {
                            PutUpFailure.createAndThrow (context, "Reference to Id #%(idref) expects type '%(expected)', but refered object is of type '%(got)' ", "idref", dscid, "expected", dsc.getType(context).getName(context), "got", pdsc.getType(context).getName(context));
                        }
                    }
                } else {
                    PutUpFailure.createAndThrow (context, FactorySiteStringPool.get(context, "0.5.3" /* No such OID #%(oid) */), "oid", oidref);
                }
            } catch (TypeMismatch e) {
                PutUpFailure.createAndThrow (context, e, FactorySiteStringPool.get(context, "0.5.4" /* Reference to OID #%(oidref) expects type '%(expected)', but refered object is of type '%(got)' */), "oidref", oidref, "expected", dsc.getType(context).getName(context), "got", ds.getType(context).getName(context));
            }
        }

        return result;
    }

    static protected class Vars {
        public Vector_Pair_BuildText_String__long_                 btcis;
        public Vector_ScaffoldParameter_long_                      params;
        public Map_TypeOrNull_String_                              parameters_by_name;
        public java.util.Hashtable<String, Pair_BuildText_String_> container_hash;
    }

    private DataSource _putUp (CallContext call_context, Type expected_type, BuildText build_text, String indent, boolean inside_collection_or_toplevel, DataSourceConnector higher_ranking, boolean is_optional, boolean toplevel, JavaCodeManager jcm, Coder coder, String dotid, String btpath, Vector<String> scaffolds_to_register) throws PutUpFailure {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        String source_location_info = build_text.getSourceLocationInfo(context);
        String node_name = build_text.getNodeName(context);
        String problem_monitor_oid = build_text.getCatch(context);
        if (problem_monitor_oid != null && problem_monitor_oid.length() == 0) { problem_monitor_oid = null; }

        String mybtpath = (btpath == null ? node_name : (btpath + "/" + node_name));

        if (DebugFeatures.isEnabled(context, DEBUG.BREAK_AT, debug_features)) {
            String break_at = config.get(context, "BreakAt", (String) null);
            if (break_at != null) {
                String[] break_at_regexps = break_at.split(":", 2);
                if (    break_at_regexps != null
                     && break_at_regexps.length == 2
                     && (    break_at_regexps[0] == null
                          || break_at_regexps[0].isEmpty()
                          || this.site_id.matches(break_at_regexps[0]))
                     && (    break_at_regexps[1] == null
                          || break_at_regexps[1].isEmpty()
                          || mybtpath.matches(break_at_regexps[1]))
                   ) {
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Reached BuildText path '%(btpath)' in '%(siteid)'", "btpath", mybtpath, "siteid", this.site_id); }
                    int BREAK_HERE = 42;
                }
            }
        }

        Map<String,Object> meta_data = build_text.getMetaData(context);
        if (meta_data != null) {
            for (String key : meta_data.keySet()) {
                Object md = meta_data.get(key);
                if (md instanceof BuildText) {
                    md = Factory_Aggregate.construct(context, (BuildText) md, ((BuildText) md).getNameSpace(context));
                }
                this.addMetaData(context, key, md, mybtpath);
            }
        }

        RuntimeStep runtime_step = null;
        try {
            if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { runtime_step = RuntimeStep.create(context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "OCP putting up element '%(name)' in '%(info)'", "name", node_name, "info", source_location_info); }

            StringWriter sw = null;
            Vector<Integer> dr = null;
            String cocp_file_name = null;
            if (jcm != null) {
                sw = new StringWriter();
                dr = coder.createDataRow(context);
                cocp_file_name = jcm.getDefaultResource(context).getJavaFilePath(context).replaceFirst(".*/", "");
            }

            COCPBuildText cocpbt = (build_text instanceof COCPBuildText ? ((COCPBuildText) build_text) : null);
            
            if (cocpbt == null || cocpbt.isExpanded(context) == false) {
                
                BuildTextExpansion btexp = (inside_collection_or_toplevel && expected_type != null && build_text.getNodeName(context) == null ? null : getAliasForItemType(context, this.type_context_id, expected_type, build_text.getNodeName(context)));
                if (btexp != null) {
                    if (btexp.getTypeName(context) != null && btexp.getTypeName(context).length() != 0) {
                        if (    build_text.getTypeName(context) != null
                             && build_text.getTypeName(context).length() != 0
                             && build_text.getTypeName(context).equals(btexp.getTypeName(context)) == false
                             && TypeManager.tryGet(context, btexp.getTypeName(context)).isA(context, TypeManager.tryGet(context, build_text.getTypeName(context))) == false
                           ) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), type property has already a value (%(current)) and it is not a derived type", "name", build_text.getNodeName(context), "value", btexp.getTypeName(context), "current", build_text.getTypeName(context));
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        build_text.setTypeName(context, btexp.getTypeName(context));
                    }
                    if (btexp.getOID(context) != null && btexp.getOID(context).length() != 0) {
                        if (build_text.getOID(context) != null && build_text.getOID(context).length() != 0 && build_text.getOID(context).equals(btexp.getOID(context)) == false) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), OID property has already a value (%(current)) and it is not a derived type", "name", build_text.getNodeName(context), "value", btexp.getOID(context), "current", build_text.getOID(context));
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        build_text.setOID(context, btexp.getOID(context));
                    }
                    if (btexp.getFactoryName(context) != null && btexp.getFactoryName(context).length() != 0) {
                        if (build_text.getFactoryName(context) != null && build_text.getFactoryName(context).length() != 0 && build_text.getFactoryName(context).equals(btexp.getFactoryName(context)) == false) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), factory property has already a value (%(current))", "name", build_text.getNodeName(context), "value", btexp.getFactoryName(context), "current", build_text.getFactoryName(context));
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        build_text.setFactoryName(context, btexp.getFactoryName(context));
                    }
                    if (btexp.getRetrieverName(context) != null && btexp.getRetrieverName(context).length() != 0) {
                        if (build_text.getRetrieverName(context) != null && build_text.getRetrieverName(context).length() != 0 && build_text.getRetrieverName(context).equals(btexp.getRetrieverName(context)) == false) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), retriever property has already a value (%(current))", "name", build_text.getNodeName(context), "value", btexp.getRetrieverName(context), "current", build_text.getRetrieverName(context));
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        build_text.setRetrieverName(context, btexp.getRetrieverName(context));
                    }
                    if (btexp.getMethodName(context) != null && btexp.getMethodName(context).length() != 0) {
                        if (build_text.getMethodName(context) != null && build_text.getMethodName(context).length() != 0 && build_text.getMethodName(context).equals(btexp.getMethodName(context)) == false) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), method property has already a value (%(current))", "name", build_text.getNodeName(context), "value", btexp.getMethodName(context), "current", build_text.getMethodName(context));
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        build_text.setMethodName(context, btexp.getMethodName(context));
                    }
                    if (btexp.getPass(context) != null) {
                        if (build_text.getPass(context) != 1 && build_text.getPass(context) != btexp.getPass(context)) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), pass property has already a non default value (%(current))", "name", build_text.getNodeName(context), "value", btexp.getPass(context), "current", build_text.getPass(context));
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        build_text.setPass(context, btexp.getPass(context));
                    }
                    if (btexp.allowDynamicTypeCheck(context) != null) {
                        if (build_text.allowDynamicTypeCheck(context) && build_text.allowDynamicTypeCheck(context) != btexp.allowDynamicTypeCheck(context)) {
                            CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), allowDynamicTypeCheck property has already a non default value (%(current))", "name", build_text.getNodeName(context), "value", btexp.allowDynamicTypeCheck(context), "current", build_text.allowDynamicTypeCheck(context));
                            throw (ExceptionConfigurationError) null; // compiler insists
                        }
                        build_text.setAllowDynamicTypeCheck(context, btexp.allowDynamicTypeCheck(context));
                    }
                }
            }
            
            if (sw != null) {
                String cocp_code_class = build_text.getCOCPCodeClass(context);
                int cocp_code_class_index = build_text.getCOCPCodeClassIndex(context);
                dr.add(COCPIndices.COCPItem_BuildText);
                dr.add(coder.writeText(context, dotid));
                dr.add(cocp_code_class_index);
                dr.add(coder.writeText(context, site_id));
            }
            
            String oid = build_text.getOID(context);
            String assign_to = build_text.getAssignTo(context);
            String typename = build_text.getTypeName(context);

            Type type = (typename == null || typename.length() == 0) ? expected_type : TypeManager.tryGet(context, typename);
            if (type != null) { typename = type.getName(context); }
            String factory = build_text.getFactoryName(context);
            String retriever = build_text.getRetrieverName(context);
            String methodname = build_text.getMethodName(context);
            boolean allow_dynamic_type_check = build_text.allowDynamicTypeCheck(context);
            boolean allow_missing_arguments = build_text.allowMissingArguments(context);
            boolean is_singleton = build_text.isSingleton(context);
            boolean have_dynamic_parameters = build_text.haveDynamicParameters(context);
            String listener = build_text.getListener(context);
            
            Vector<String[]> pre_conditions    = build_text.getPreConditions(context);
            if (pre_conditions != null) {
                for (String[] pre_condition : pre_conditions) {
                    checkExpression(context, pre_condition[0], higher_ranking, cocpbt, sw, dr, coder);
                }
            }
            Vector<String[]> post_conditions   = build_text.getPostConditions(context);
            if (post_conditions != null) {
                for (String[] post_condition : post_conditions) {
                    checkExpression(context, post_condition[0], higher_ranking, cocpbt, sw, dr, coder);
                }
            }
            Vector<String[]> pre_build_scripts   = build_text.getPreBuildScripts(context);
            if (pre_build_scripts != null) {
                for (String[] pre_build_script : pre_build_scripts) {
                    checkExpression(context, pre_build_script[0], higher_ranking, cocpbt, sw, dr, coder);
                }
            }
            Vector<String[]> post_build_scripts   = build_text.getPostBuildScripts(context);
            if (post_build_scripts != null) {
                for (String[] post_build_script : post_build_scripts) {
                    checkExpression(context, post_build_script[0], higher_ranking, cocpbt, sw, dr, coder);
                }
            }

            if (toplevel) {
                String variable_definition_expression = checkExpression(context, build_text.getDefine(context), higher_ranking, cocpbt, sw, dr, coder);
                if (    variable_definition_expression != null
                     && variable_definition_expression.length() != 0
                   ) {
                    String vdes[] = variable_definition_expression.replaceAll("(^ *)|( *$)","").split("(?: *);(?: *)");
                    this.define_variable_name = new String[vdes.length];
                    this.define_variable_expression = new Expression[vdes.length];
                    int index = 0;
                    for (String vde : vdes) {
                        String fea[] = definere.tryGetMatches(context, vde);
                        this.define_variable_name[index] = fea[0];
                        this.define_variable_expression[index] = new Expression(context, fea[1], "jspp");
                        index++;
                    }
                }
            }

            int pass = build_text.getPass(context);
            if (pass > passes) { passes = pass; }
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + FactorySiteStringPool.get(context, "0.5.0" /* Putting up '%(type)' Scaffold%(factory)%(oid)%(dynamic) (%(info))... */), "type", typename, "factory", ((factory == null || factory.length() == 0 ? "" : (" [F|" + factory + "]")) + (retriever == null || retriever.length() == 0 ? "" : (" [R|" + retriever + "]"))), "oid", (oid == null || oid.length() == 0 ? "" : (" #" + oid)), "dynamic", (allow_dynamic_type_check ? " (dynamic)" : ""), "missing", (allow_missing_arguments ? " (relaxed)" : ""), "info", build_text.getSourceLocationInfo(context)); }
            DataSource result;
            Scaffold scaffold;
            
            String signature = build_text.getSignature(context);
            if (signature != null && signature.length() != 0) {
                declareParameters(context, signature, higher_ranking, toplevel, cocpbt, sw, dr, coder);
            }

            String evaluator = build_text.getEvaluator(context);
            if (evaluator != null && evaluator.length() != 0) {
                this.default_evaluator = evaluator;
            }

            if (build_text instanceof BuildTextOptionalParameter) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + "... optional parameter ..."); }
                BuildTextOptionalParameter btop = (BuildTextOptionalParameter) build_text;
                DataSourceConnector dso = (DataSourceConnector) _putUp(context, expected_type, btop.getParameter(context), indent + "  ", false, higher_ranking, true, false, jcm, coder, dotid+"_0", mybtpath, scaffolds_to_register);
                DataSource          dsf = _putUp(context, expected_type, btop.getFallback(context), indent + "  ", false, dso, false, false, jcm, coder, dotid+"_1", mybtpath, scaffolds_to_register);
                
                result = new DataSource_DSCopt_or_DS (context, dso, dsf, source_location_info);
                scaffold = null;
                
                if (sw != null) {
                    dr.add(COCPIndices.BuildText_Parameter);
                    dr.add(coder.writeText(context, dotid + "_0"));
                    dr.add(COCPIndices.BuildText_Fallback_Parameter);
                    dr.add(coder.writeText(context, dotid + "_1"));
                }

            } else if (build_text instanceof BuildTextOptionalRefById) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + "... optional parameter ..."); }
                BuildTextOptionalRefById btop = (BuildTextOptionalRefById) build_text;
                DataSourceConnector dso = (DataSourceConnector) _putUp(context, expected_type, btop.getRefById(context), indent + "  ", false, higher_ranking, true, false, jcm, coder, dotid+"_0", mybtpath, scaffolds_to_register);
                DataSource          dsf = _putUp(context, expected_type, btop.getFallback(context), indent + "  ", false, dso, false, false, jcm, coder, dotid+"_1", mybtpath, scaffolds_to_register);
                
                result = new DataSource_DSCopt_or_DS (context, dso, dsf, source_location_info);
                scaffold = null;
                
                if (sw != null) {
                    dr.add(COCPIndices.BuildText_RefById);
                    dr.add(coder.writeText(context, dotid + "_0"));
                    dr.add(COCPIndices.BuildText_Fallback_RefById);
                    dr.add(coder.writeText(context, dotid + "_1"));
                }

            } else if (build_text instanceof BuildTextSwitch) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + "... switch ..."); }
                BuildTextSwitch bts = (BuildTextSwitch) build_text;
                Vector_Pair_BuildText_String__long_ cases = bts.getCases(context);

                DataSource_Switch dsw = new DataSource_Switch(context, type, source_location_info, this);

                StringWriter ptdsw = null; // for parameters to declare
                Vector<Integer> ptddr = null; // for parameters to declare
                if (sw != null) {
                    dr.add(COCPIndices.BuildText_Cases);
                    dr.add((int) cases.getSize(context));
                    ptdsw = new StringWriter();
                    ptddr = new Vector<Integer>();
                }

                for (int i=0; i<cases.getSize(context); i++) {
                    Pair_BuildText_String_ a_case = cases.tryGet(context, i);

                    DataSource ds = null;

                    BuildText bt_case = a_case.getItem1(context);
                    boolean cocp_is_handled = false;

                    if (bt_case instanceof BuildTextSimple) {
                        BuildTextSimple btsi = (BuildTextSimple) bt_case;
                        if (bt_case.isExpression(context)) {
                            ds = new DataSourceGeneric_ExpressionMember(context, checkExpression(context, btsi.getText(context), higher_ranking, cocpbt, ptdsw, ptddr, coder), this, expected_type, source_location_info);
                        } else {
                            ds = new DataSourceGeneric_Member(context, btsi.getText(context), source_location_info);
                        }
                    } else if (bt_case instanceof BuildTextDOM) {
                        BuildTextDOM btd = (BuildTextDOM) bt_case;
                        ds = new DataSourceGeneric_Member(context, btd.getNode(context), source_location_info);
                    } else {
                        ds = _putUp(context, expected_type, bt_case, indent + "  ", false, higher_ranking, false, false, jcm, coder, dotid+"_"+i, mybtpath, scaffolds_to_register);
                        cocp_is_handled = true;
                    }
                   
                    if (sw != null && ! cocp_is_handled) {
                        StringWriter btcsw = new StringWriter();
                        Vector<Integer> btcdr = coder.createDataRow(context);

                        String btc_cocp_code_class = bt_case.getCOCPCodeClass(context);
                        int btc_cocp_code_class_index = bt_case.getCOCPCodeClassIndex(context);

                        btcdr.add(COCPIndices.COCPItem_BuildText);
                        btcdr.add(coder.writeText(context, dotid + "_" + i));
                        btcdr.add(btc_cocp_code_class_index);
                        btcdr.add(coder.writeText(context, site_id));

                        bt_case.printCOCPCode(context, btcsw, btcdr, coder, "        ", site_id, dotid, cocp_file_name);

                        writeToJCM(context, jcm, btcsw.toString());
                    }

                    String if_expression = checkExpression(context, bt_case.getIfExpression(context), higher_ranking, cocpbt, ptdsw, ptddr, coder);
                    dsw.addCase(context, ds, if_expression);

                    if (sw != null) {
                        dr.add(coder.writeText(context, dotid + "_" + i));
                        dr.add(coder.writeText(context, a_case.getItem2(context)));
                    }
                }

                result = dsw;
                scaffold = null;
                
                if (sw != null) {
                    for (Integer i : ptddr) {
                        dr.add(i);
                    }
                }

            } else if (build_text instanceof BuildTextRef) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + "... Reference ..."); }
                BuildTextRef btr = (BuildTextRef) build_text;
                String oidref = btr.getOIDRef(context);
                
                DataSourceConnector dsc = referencemap.tryGet(context, oidref);
                if (dsc == null) {
                    dsc = new DataSourceConnector(context, type, source_location_info);
                    referencemap.set(context, oidref, dsc);
                } else {
                    if (dsc instanceof DataSourceConnector_Id) {
                        ((DataSourceConnector_Id) dsc).setParameterEnabled(context, false);
                    }
                    Type dsctype = dsc.getType(context);
                    if (dsctype == null) {
                        dsc.setType(context, type);
                    } else {
                        if (type == null || dsctype.equals(type) || dsctype.isA(context, type)) {
                            // ok
                        } else {
                            if (type.isA(context, dsctype)) {
                                dsc.setType(context, type);
                            } else {
                                PutUpFailure.createAndThrow (context, FactorySiteStringPool.get(context, "0.5.19" /* Reference to OID #%(oidref) is used inconsistent: one occurence expects a type '%(expected1)', while the other occurence expects '%(expected2)' */), "oidref", oidref, "expected1", dsctype.getName(context), "expected2", typename);
                            }
                        }
                    }
                }
                result = dsc;
                scaffold = null;
            } else if (build_text instanceof BuildTextRefById) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + "... Reference by Id ..."); }
                BuildTextRefById btrbi = (BuildTextRefById) build_text;
                String idref = btrbi.getIdRef(context);
                
                DataSourceConnector dsc = referencemap.tryGet(context, idref);
                if (dsc == null) {
                    dsc = new DataSourceConnector_Id(context, type, idref, higher_ranking, is_optional, source_location_info);
                    referencemap.set(context, idref, dsc);
                } else {
                    Type dsctype = dsc.getType(context);
                    if (dsctype == null) {
                        dsc.setType(context, type);
                    } else {
                        if (type == null || dsctype.equals(type) || dsctype.isA(context, type)) {
                            // ok
                        } else {
                            if (type.isA(context, dsctype)) {
                                dsc.setType(context, type);
                            } else {
                                PutUpFailure.createAndThrow (context, "Reference to Id #%(idref) is used inconsistent: one occurence expects a type '%(expected1)', while the other occurence expects '%(expected2)'", "idref", idref, "expected1", dsctype.getName(context), "expected2", typename);
                            }
                        }
                    }
                }
                result = dsc;
                scaffold = null;
            } else if (build_text instanceof BuildTextNull) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + "... Null ..."); }
                
                DataSourceNull dsn = new DataSourceNull(context, type, source_location_info);
                result = dsn;
                scaffold = null;
            } else if (build_text instanceof BuildTextParameter) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + "... parameter ..."); }
                BuildTextParameter btp = (BuildTextParameter) build_text;
                String name = btp.getName(context);
                
                DataSourceConnector dsc = parametermap.tryGet(context, name);
                if (dsc == null) {
                    dsc = new DataSourceConnector_Parameter(context, type, name, higher_ranking, is_optional, false, source_location_info);
                    parametermap.set(context, name, dsc);
                } else {
                    Type dsctype = dsc.getType(context);
                    if (dsctype == null) {
                        dsc.setType(context, type);
                    } else {
                        if (type == null || dsctype.equals(type) || dsctype.isA(context, type)) {
                            // ok
                        } else {
                            if (type.isA(context, dsctype)) {
                                dsc.setType(context, type);
                            } else {
                                PutUpFailure.createAndThrow (context, FactorySiteStringPool.get(context, "0.5.21" /* Parameter '%(parname)' is used inconsistent: one occurence expects a type '%(expected1)', while the other occurence expects '%(expected2)' */), "parname", name, "expected1", dsctype.getName(context), "expected2", typename);
                            }
                        }
                    }
                }
                result = dsc;
                scaffold = null;
            } else if (build_text instanceof BuildTextComplex) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + "... complex ..."); }
                BuildTextComplex btc = (BuildTextComplex) build_text;
                Vector_Pair_BuildText_String__long_ btcis_orig = btc.getItems(context);
                
                Vars vars = null;
                
                if (cocpbt != null && cocpbt.getParametersToDeclare(context) != null) {
                    for (COCPBuildText.Parameter p : cocpbt.getParametersToDeclare(context)) {
                        this.declareParameter(context, p.name, p.type, p.optional, p.default_value, higher_ranking, toplevel, sw, dr, coder);
                    }
                }
                
                if (cocpbt != null && cocpbt.getScaffoldFactory(context) != null) {
                    
                    try {
                        result = scaffold = cocpbt.getScaffoldFactory(context).createScaffold(context, this);
                    } catch (InvalidFactory ifac) {
                        PutUpFailure.createAndThrow (context, ifac, FactorySiteStringPool.get(context, "0.5.18" /* Cannot put up, scaffold creation for '%(type)[F|%(factory)/R|%(retriever)]' failed */), "type", typename, "factory", factory, "retriever", retriever);
                        throw (PutUpFailure) null; // compiler insists
                    }
                    
                } else {
                    vars = new Vars();
                    vars.btcis = Factory_Vector_Pair_BuildText_String__long_.construct(context);
                    vars.params = Factory_Vector_ScaffoldParameter_long_.construct(context);
                    vars.parameters_by_name = new MapImpl_TypeOrNull_String_(context);
                    vars.container_hash = null; 
                    
                    BTCLOOP: for (int i=0; i<btcis_orig.getSize(context); i++) {
                        prepareBuildText(context, btcis_orig.tryGet(context, i), type, vars, higher_ranking, source_location_info, cocpbt, sw, dr, coder);
                    }
                    Vector_Pair_BuildText_String__long_ btcis_default = getDefaultsForScopeType(context, this.type_context_id, type);
                    if (btcis_default != null) {
                        BTCLOOP: for (int i=0; i<btcis_default.getSize(context); i++) {
                            Pair_BuildText_String_ def_btp = btcis_default.tryGet(context, i);
                            String def_par_name = def_btp.getItem2(context);
                            if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Checking parameter '%(name)'", "name", def_par_name); }
                            if (vars.parameters_by_name != null && vars.parameters_by_name.tryGet(context, def_par_name) == null) {
                                if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "Applying build text '%(buildtext)'", "buildtext", def_btp.getItem1(context)); }
                                prepareBuildText(context, def_btp, type, vars, higher_ranking, source_location_info, cocpbt, sw, dr, coder);
                            }
                        }
                    }
                    
                    if (type == null) {
                        PutUpFailure.createAndThrow (context, FactorySiteStringPool.get(context, "0.5.10" /* Cannot put up due to unavailability of required class '%(type)' */), "type", typename);
                        throw (PutUpFailure) null; // compiler insists
                    }

                    try {

                        result = scaffold = FactorySiteContext.getScaffoldFactory(context).get(context, type, factory, retriever, methodname, vars.params, vars.parameters_by_name, allow_dynamic_type_check, allow_missing_arguments, listener, is_singleton, have_dynamic_parameters, this, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
                    } catch (InvalidFactory ifac) {
                        PutUpFailure.createAndThrow (context, ifac, FactorySiteStringPool.get(context, "0.5.18" /* Cannot put up, scaffold creation for '%(type)[F|%(factory)/R|%(retriever)]' failed */), "type", typename, "factory", factory, "retriever", retriever);
                        throw (PutUpFailure) null; // compiler insists
                    } catch (InvalidRetriever iret) {
                        PutUpFailure.createAndThrow (context, iret, FactorySiteStringPool.get(context, "0.5.18" /* Cannot put up, scaffold creation for '%(type)[F|%(factory)/R|%(retriever)]' failed */), "type", typename, "factory", factory, "retriever", retriever);
                        throw (PutUpFailure) null; // compiler insists
                    } catch (InvalidClass icls) {
                        PutUpFailure.createAndThrow (context, icls, FactorySiteStringPool.get(context, "0.5.18" /* Cannot put up, scaffold creation for '%(type)[F|%(factory)/R|%(retriever)]' failed */), "type", typename, "factory", factory, "retriever", retriever);
                        throw (PutUpFailure) null; // compiler insists
                    } catch (InvalidConfiguration icfg) {
                        PutUpFailure.createAndThrow (context, icfg, FactorySiteStringPool.get(context, "0.5.18" /* Cannot put up, scaffold creation for '%(type)[F|%(factory)/R|%(retriever)]' failed */), "type", typename, "factory", factory, "retriever", retriever);
                        throw (PutUpFailure) null; // compiler insists
                    }
                    
                }
                
                // retrieve scaffold parameters from scaffold, they are
                // adopted (types refined) and may be some are skipped
                // due to "AppliesTo" restrictions
                // note: for variable signature scaffolds (collections),
                // sps is *not* filled, yet
                Vector_ScaffoldParameter_long_ sps = scaffold.getParameters(context);
                Map_ScaffoldParameter_String_ sps_by_name = new MapImpl_ScaffoldParameter_String_(context);
                
                for (int spi=0; spi<sps.getSize(context); spi++) {
                    ScaffoldParameter sp = sps.tryGet(context, spi);
                    sps_by_name.set(context, sp.getName(context), sp);
                }
                
                Type comptype = scaffold.hasVariableSignature(context) ? 
                                   (   have_dynamic_parameters
                                     ? dynamic_parameter_type
                                     : scaffold.getComponentType(context)
                                   )
                                 : null;
                Type component_type;
                try {
                    component_type = (btc.getComponentType(context) == null || btc.getComponentType(context).length() == 0 ? null : TypeManager.get(context, btc.getComponentType(context)));
                } catch (NoSuchClass nsc) {
                    PutUpFailure.createAndThrow (context, nsc, "Explicitly given component type does not exist");
                    throw (PutUpFailure) null; // compiler insists
                }
                if (component_type != null) {
                    if (comptype != null && component_type.isA(context, comptype) == false) {
                        PutUpFailure.createAndThrow(context, "Explicitly given component type '%(componenttype)' does not fit to mandatory component type determined by collection '%(collectiontype)'", "componenttype", component_type, "collectiontype", comptype);
                        throw (PutUpFailure) null; // compiler insists
                    }
                    comptype = component_type;
                }
                
                // after this loop, sps contains the final set of scaffolds,
                // for normal as well as variable signature scaffolds
                // since we got sps from the scaffold, the scaffold's collection
                // is up to date thereby, too
                if (cocpbt == null || cocpbt.getScaffoldFactory(context) == null) {
                    for (int i=0; i<vars.params.getSize(context); i++) {
                        ScaffoldParameter sp = null;
                        Pair_BuildText_String_ pbts = vars.btcis.tryGet(context, i);
                        if (scaffold.hasVariableSignature(context)) {
                            sp = vars.params.tryGet(context, i);
                            Type partype = sp.getType(context);
                            if (partype == null) {
                                partype = comptype;
                            } else {
                                if (! partype.isA(context, comptype)) {
                                    PutUpFailure.createAndThrow (context, FactorySiteStringPool.get(context, "0.5.20" /* Component in collection is not of type '%(comptype)', as expected, but of type '%(partype)' */), "comptype", comptype == null ? null : comptype.getName(context), "partype", partype.getName(context));
                                    throw (PutUpFailure) null; // compiler insists
                                }
                            }
                            
                            sp.setType(context, partype);
                            sps.append(context, sp);
                        } else {
                            sp = sps_by_name.tryGet(context, pbts.getItem2(context));
                            if (sp == null) {
                                continue;
                            }
                        }
                    }
                }

                StringWriter ptdsw = null; // for parameters to declare
                Vector<Integer> ptddr = null; // for parameters to declare
                if (sw != null) {
                    dr.add(COCPIndices.BuildText_Items);
                    dr.add((int) sps.getSize(context));
                    ptdsw = new StringWriter();
                    ptddr = new Vector<Integer>();
                }

                for (int i=0; i<sps.getSize(context); i++) {
                    
                    ScaffoldParameter      sp = sps.tryGet(context, i);
                    Pair_BuildText_String_ pbts = (   cocpbt == null || cocpbt.getScaffoldFactory(context) == null
                                                    ? vars.btcis.tryGet(context, sp.getBuildTextIndex(context))
                                                    : btcis_orig.tryGet(context, i)
                                                  );

                    Type et = sp.getType(context);
                    DataSource ds = null;
            
                    BuildText bt_child = pbts.getItem1(context);
                    boolean cocp_is_handled = false;
                    
                    if (bt_child instanceof BuildTextSimple) {
                        BuildTextSimple bts = (BuildTextSimple) bt_child;
                        if (bt_child.isExpression(context)) {
                            ds = new DataSourceGeneric_ExpressionMember(context, checkExpression(context, bts.getText(context), higher_ranking, cocpbt, ptdsw, ptddr, coder), this, et, source_location_info);
                        } else {
                            ds = new DataSourceGeneric_Member(context, bts.getText(context), source_location_info);
                        }
                        this.optionallyRegisterOID(context, ds, bt_child.getOID(context), indent, source_location_info);
                    } else if (bt_child instanceof BuildTextDOM) {
                        BuildTextDOM btd = (BuildTextDOM) bt_child;
                        ds = new DataSourceGeneric_Member(context, btd.getNode(context), source_location_info);
                        this.optionallyRegisterOID(context, ds, bt_child.getOID(context), indent, source_location_info);
                    } else {
                        ds = _putUp(context, et, bt_child, indent + "  ", scaffold.hasVariableSignature(context) ? true : false, higher_ranking, false, false, jcm, coder, dotid+"_"+i, mybtpath, scaffolds_to_register);
                        cocp_is_handled = true;
                    }
                    
                    if (sw != null && ! cocp_is_handled) {
                        StringWriter btcsw = new StringWriter();
                        Vector<Integer> btcdr = coder.createDataRow(context);
                        String btc_cocp_code_class = bt_child.getCOCPCodeClass(context);
                        int btc_cocp_code_class_index = bt_child.getCOCPCodeClassIndex(context);

                        btcdr.add(COCPIndices.COCPItem_BuildText);
                        btcdr.add(coder.writeText(context, dotid + "_" + i));
                        btcdr.add(btc_cocp_code_class_index);
                        btcdr.add(coder.writeText(context, site_id));

                        bt_child.printCOCPCode(context, btcsw, btcdr, coder, "        ", site_id, dotid, cocp_file_name);

                        writeToJCM(context, jcm, btcsw.toString());
                    }
                    
                    if (sw != null) {
                        dr.add(coder.writeText(context, dotid + "_" + i));
                        dr.add(coder.writeText(context, pbts.getItem2(context)));
                    }
                    
                    if (scaffold.hasVariableSignature(context) && sp.getType(context) == null && bt_child instanceof BuildTextRef) {
                        if (! ds.getType(context).isA(context, comptype)) {
                            PutUpFailure.createAndThrow (context, FactorySiteStringPool.get(context, "0.5.20" /* Component in collection is not of type '%(comptype)', as expected, but of type '%(partype)' */), "comptype", comptype.getName(context), "partype", ds.getType(context).getName(context));
                            throw (PutUpFailure) null; // compiler insists
                        }
                        sp.setType(context, ds.getType(context));
                    }
                    try {
                        sp.setValue(context, ds);
                    } catch (TypeMismatch e) {
                        PutUpFailure.createAndThrow(context, e, FactorySiteStringPool.get(context, "0.5.12" /* In FactorySite '%(siteid)', Scaffold parameter '%(spname)' of type '%(sptype)' does not accept DataSource of type '%(type)' */), "siteid", getSiteId(context), "spname", sp.getName(context), "sptype", sp.getType(context), "type", ds.getType(context).getName(context));
                        throw (PutUpFailure) null; // compiler insists
                    }
                }
                
                if (sw != null) {
                    for (Integer i : ptddr) {
                        dr.add(i);
                    }
                }
            } else if (build_text instanceof BuildTextSimple) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + "... simple ..."); }
                BuildTextSimple bts = (BuildTextSimple) build_text;
                DataSource ds = null;
                if (build_text.isExpression(context)) {
                    ds = new DataSourceGeneric_ExpressionMember(context, checkExpression(context, bts.getText(context), higher_ranking, cocpbt, sw, dr, coder), this, expected_type, source_location_info);
                } else {
                    ds = new DataSourceGeneric_Member(context, bts.getText(context), source_location_info);
                }
                result = ds;
                scaffold = null;
            } else if (build_text instanceof BuildTextDOM) {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + "... DOM ..."); }
                BuildTextDOM btd = (BuildTextDOM) build_text;
                DataSource ds = null;
                ds = new DataSourceGeneric_Member(context, btd.getNode(context), source_location_info);
                result = ds;
                scaffold = null;
            } else {
                PutUpFailure.createAndThrow (context, FactorySiteStringPool.get(context, "0.5.13" /* build_text is neither, as expected, of type BuildTextSimple, BuildTextRef nor of type BuildTextComplex, but of type '%(type)' */), "type", build_text.getClass().getName());
                throw (PutUpFailure) null; // compiler insists
            }
            
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, indent + "... DataSource is created ..."); }
            
            if (scaffold != null) {
                this.all_scaffolds.append(context, scaffold);
                if (scaffold instanceof OriginAware) {
                    this.origin_aware_scaffolds.append(context, scaffold);
                }
            }
            
            this.optionallyRegisterOID(context, result, oid, indent, source_location_info);
            
            if (assign_to != null && assign_to.length() != 0) {
                DataSourceConnector dsc = parametermap.tryGet(context, assign_to);
                if (dsc != null) {
                    PutUpFailure.createAndThrow (context, "Out parameter '%(name)' is assigned a value twice", "name", assign_to);
                }
                dsc = new DataSourceConnector_Parameter(context, type, assign_to, source_location_info);
                ((DataSourceConnector_Parameter) dsc).setIsStatic(context, true);
                parametermap.set(context, assign_to, dsc);
                try {
                    dsc.setDataSource(context, result);
                } catch (TypeMismatch tm) {
                    PutUpFailure.createAndThrow (context, tm, "Type of out parameter '%(name)' does not match to assigned value", "name", assign_to);
                }
            }

            ScaffoldGenericCOCPEnabled sgcocpe = null;
            if (jcm != null && result != null && result instanceof ScaffoldGenericCOCPEnabled) {
                sgcocpe = (ScaffoldGenericCOCPEnabled) result;
                dr.add(COCPIndices.BuildText_ScaffoldFactory);
                dr.add(coder.writeText(context, dotid));
            }
            
            if (sw != null) {
                build_text.printCOCPCode(context, sw, dr, coder, "        ", site_id, dotid, cocp_file_name);
                
                if (sgcocpe != null) {
                    sgcocpe.printCOCPCode2(context, sw, coder.createDataRow(context), coder, dotid);
                    scaffolds_to_register.add(dotid);
                }

                writeToJCM(context, jcm, sw.toString());
            }
            
            if (runtime_step != null) { runtime_step.setCompleted(context, ""); runtime_step = null; }
            return result;

        } catch(ExceptionError t) {
            if (runtime_step != null) { runtime_step.setFailed(context, t, ""); runtime_step = null; }
            throw t;
        }
    }

    protected void optionallyRegisterOID(CallContext context, DataSource result, String oid, String indent, String source_location_info) throws PutUpFailure {
        if (result != null && oid != null && oid.length() != 0) {
            try {
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                    Type st = result.getType(context); 
                    NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, indent + FactorySiteStringPool.get(context, "0.5.2" /* (registering Scaffold with OID #%(oid), type is '%(type)') */), "oid", oid, "type",  st == null ? "not defined yet" : st.getName(context));
                }
                this.oidmap.add(context, oid, result);

                // we add this here since otherwise for dynamic oidref
                // resolution no entries would be found if there's no direct
                // access to that oid also
                DataSourceConnector dsc = referencemap.tryGet(context, oid);
                if (dsc == null) {
                    dsc = new DataSourceConnector(context, result.getType(context), source_location_info);
                    referencemap.set(context, oid, dsc);
                }
            } catch (AlreadyExists e) {
                PutUpFailure.createAndThrow (context, FactorySiteStringPool.get(context, "0.5.15" /* Duplicate OID #%(oid) */), "oid", oid);
            }
        }
    }

    protected void writeToJCM(CallContext context, JavaCodeManager jcm, String string) {
        BufferedWriter bw = jcm.getDefaultResource(context).getWriter(context);
        try {
            bw.write(string);
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Cannot write to '%(file)'", "file", jcm.getDefaultResource(context).getJavaFilePath(context));
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
    }

    protected void writeToJCM(CallContext context, JavaCodeManager jcm, Vector<Vector<Integer>> data) {
        String name= jcm.getDefaultResource(context).getJavaClassName(context).replaceFirst(".*\\.", "") + ".cocp";
        OutputStream os = jcm.getRelativeDataResource(context, name).getStream(context);

        int[][] data_array = new int[data.size()][];
        int i = 0;
        for (Vector<Integer> v : data) {
            data_array[i] = new int[v.size()];
            int j = 0;
            for (Integer I : v) {
                data_array[i][j] = I;
                j++;
            }
            i++;
        }

        try {
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(data_array);
            oos.close();
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Cannot write to '%(file)'", "file", jcm.tryGetRelativeDataResource(context, name).getJavaFilePath(context));
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
    }

    protected void prepareBuildText(CallContext context, Pair_BuildText_String_ bti, Type type, Vars vars, DataSourceConnector higher_ranking, String source_location_info, COCPBuildText cocpbt, StringWriter sw, Vector<Integer> dr, Coder coder) throws PutUpFailure {
        BuildText bt = bti.getItem1(context);
        String alias = bt.getAlias(context);
        if (alias == null || alias.length() == 0) { alias = bti.getItem2(context); }

        BuildTextExpansion btexp;
        if (   (cocpbt != null && cocpbt.isExpanded(context))
            || (btexp = getAliasForScopeType(context, this.type_context_id, type, alias)) == null) {
            vars.btcis.append(context, bti);
        } else {
            if (btexp.getIsContainer(context)) {
                String container_name = btexp.getName(context);
                if (container_name == null || container_name.length() == 0) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)' is a container alias but does not specify a new name, as required", "name", alias);
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
                Pair_BuildText_String_ bti_orig = bti;
                BuildText              bt_orig  = bt;
                boolean new_container = false;
                if (vars.container_hash == null || vars.container_hash.get(container_name) == null) {
                    new_container = true;
                    if (vars.container_hash == null) {
                        vars.container_hash = new java.util.Hashtable<String, Pair_BuildText_String_>();
                    }
                    bt  = new BuildTextComplex_String (context);
                    bti = new Pair_BuildText_String_(context, bt, container_name);
                    vars.container_hash.put(container_name, bti);
                    vars.btcis.append(context, bti);
                } else {
                    bti = vars.container_hash.get(container_name);
                    bt  = bti.getItem1(context);
                }
                ((BuildTextComplex_String) bt).getItems(context).append(context, bti_orig);
                if (new_container == false) {
                    return;
                }
            } else {
                vars.btcis.append(context, bti);
            }
            if (btexp.getName(context) != null && btexp.getName(context).length() != 0) {
                bti.setItem2(context, btexp.getName(context));
            }
            if (btexp.getOID(context) != null && btexp.getOID(context).length() != 0) {
                if (bt.getOID(context) != null && bt.getOID(context).length() != 0 && bt.getOID(context).equals(btexp.getOID(context)) == false) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), OID property has already a value (%(current))", "name", alias, "value", btexp.getOID(context), "current", bt.getOID(context));
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
                bt.setOID(context, btexp.getOID(context));
            }
            if (btexp.getTypeName(context) != null && btexp.getTypeName(context).length() != 0) {
                if (bt.getTypeName(context) != null && bt.getTypeName(context).length() != 0 && bt.getTypeName(context).equals(btexp.getTypeName(context)) == false) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), type property has already a value (%(current))", "name", alias, "value", btexp.getTypeName(context), "current", bt.getTypeName(context));
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
                bt.setTypeName(context, btexp.getTypeName(context));
            }
            if (btexp.getFactoryName(context) != null && btexp.getFactoryName(context).length() != 0) {
                if (bt.getFactoryName(context) != null && bt.getFactoryName(context).length() != 0 && bt.getFactoryName(context).equals(btexp.getFactoryName(context)) == false) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), factory property has already a value (%(current))", "name", alias, "value", btexp.getFactoryName(context), "current", bt.getFactoryName(context));
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
                bt.setFactoryName(context, btexp.getFactoryName(context));
            }
            if (btexp.getRetrieverName(context) != null && btexp.getRetrieverName(context).length() != 0) {
                if (bt.getRetrieverName(context) != null && bt.getRetrieverName(context).length() != 0 && bt.getRetrieverName(context).equals(btexp.getRetrieverName(context)) == false) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), retriever property has already a value (%(current))", "name", alias, "value", btexp.getRetrieverName(context), "current", bt.getRetrieverName(context));
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
                bt.setRetrieverName(context, btexp.getRetrieverName(context));
            }
            if (btexp.getMethodName(context) != null && btexp.getMethodName(context).length() != 0) {
                if (bt.getMethodName(context) != null && bt.getMethodName(context).length() != 0 && bt.getMethodName(context).equals(btexp.getMethodName(context)) == false) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), method property has already a value (%(current))", "name", alias, "value", btexp.getMethodName(context), "current", bt.getMethodName(context));
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
                bt.setMethodName(context, btexp.getMethodName(context));
            }
            if (btexp.getPass(context) != null) {
                if (bt.getPass(context) != 1 && bt.getPass(context) != btexp.getPass(context)) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), pass property has already a non default value (%(current))", "name", alias, "value", btexp.getPass(context), "current", bt.getPass(context));
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
                bt.setPass(context, btexp.getPass(context));
            }
            if (btexp.allowDynamicTypeCheck(context) != null) {
                if (bt.allowDynamicTypeCheck(context) && bt.allowDynamicTypeCheck(context) != btexp.allowDynamicTypeCheck(context)) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "Alias '%(name)', cannot override (%(value)), allowDynamicTypeCheck property has already a non default value (%(current))", "name", alias, "value", btexp.allowDynamicTypeCheck(context), "current", bt.allowDynamicTypeCheck(context));
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
                bt.setAllowDynamicTypeCheck(context, btexp.allowDynamicTypeCheck(context));
            }
        }

        String typename1 = bt.getTypeName(context);
        Type type_or_null;
        try {
            type_or_null = (typename1 == null || typename1.length() == 0 ? null : TypeManager.get(context, typename1));
        } catch (NoSuchClass e) {
            PutUpFailure.createAndThrow (context, e, FactorySiteStringPool.get(context, "0.5.8" /* Class '%(class)' does not exist (for parameter '%(parameter)') */), "class", typename1, "parameter", bti.getItem2(context));
            throw (PutUpFailure) null; // compiler insists
        }
        ScaffoldParameter sp;
        String  name_attribute                 = checkExpression(context, bt.getNameAttribute(context)    , higher_ranking, cocpbt, sw, dr, coder);
        String  if_expression                  = checkExpression(context, bt.getIfExpression(context)     , higher_ranking, cocpbt, sw, dr, coder);
        String  foreach_expression             = checkExpression(context, bt.getForeachExpression(context), higher_ranking, cocpbt, sw, dr, coder);
        boolean is_expression                  = bt.isExpression(context);
        String  variable_definition_expression = checkExpression(context, bt.getDefine(context), higher_ranking, cocpbt, sw, dr, coder);

        if (    (name_attribute                 != null)
             || (if_expression                  != null)
             || (foreach_expression             != null)
             || (variable_definition_expression != null)
             || is_expression
           ) {
            sp = new DynamicScaffoldParameter(context, bti.getItem2(context), type_or_null, name_attribute, is_expression, if_expression, foreach_expression, variable_definition_expression, source_location_info, vars.btcis.getSize(context)-1);
        } else {
            sp = new ScaffoldParameter(context, bti.getItem2(context), type_or_null, source_location_info, vars.btcis.getSize(context)-1);
        }
        if (bt.getAppliesTo(context) != null && bt.getAppliesTo(context).length() != 0) {
            try {
                String[] attpss = bt.getAppliesTo(context).split(",");
                Type[] attpst = new Type[attpss.length];
                int t=0;
                for (String attps : attpss) {
                    attpst[t++] = TypeManager.get(context, attps);
                }
                sp.setAppliesTo(context, attpst);
            } catch (NoSuchClass nsc) {
                PutUpFailure.createAndThrow (context, nsc, "AppliesTo-constraint '%(appliesto)' refers to an invalid type", "appliesto", bt.getAppliesTo(context));
                throw (PutUpFailure) null; // compiler insists
            }
        }

        vars.params.append(context, sp);
        if (vars.parameters_by_name != null) {
            try {
                vars.parameters_by_name.add(context, bti.getItem2(context), new TypeOrNull(type_or_null));
            } catch (AlreadyExists ae) {
                vars.parameters_by_name = null;
            }
        }
    }

    static protected RegularExpression parameter_in_expression_re = new RegularExpression("\\$\\(([^\\)]+)\\)");

    protected String checkExpression(CallContext context, String expression, DataSourceConnector higher_ranking, COCPBuildText cocpbt, StringWriter sw, Vector<Integer> dr, Coder coder) throws PutUpFailure {
        if (expression == null || expression.length() == 0) {
            return null;
        }
        Matcher m = parameter_in_expression_re.getMatcher(context, expression);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String declaration = m.group(1);
            String name = declareParameters(context, declaration, higher_ranking, false, cocpbt, sw, dr, coder);
            m.appendReplacement(sb, name);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    protected Vector_String_long_ root_arguments;

    public Vector_String_long_ getRootArguments (CallContext context) {
        return this.root_arguments;
    }

    public void setRootArguments (CallContext context, Vector_String_long_ root_arguments) {
        this.root_arguments = root_arguments;
    }

    static protected RegularExpression parameter_in_signature_re = new RegularExpression("\\s*([A-Za-z0-9_<>\\.]+(?:\\[\\])*)\\s+([^,\\)\\?\\t\\r\\n ]+)\\s*(?:=\\s*([^,\\?\\r\\n]+))?\\s*(\\??)\\s*(,?)\\s*");

    protected String declareParameters(CallContext context, String declaration, DataSourceConnector higher_ranking, boolean toplevel, COCPBuildText cocpbt, StringWriter sw, Vector<Integer> dr, Coder coder) throws PutUpFailure {
        if (declaration == null || declaration.length() == 0) {
            return null;
        }
        Matcher m = parameter_in_signature_re.getMatcher(context, declaration);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String  parameter_type = m.group(1);
            String  parameter_name = m.group(2);
            String  default_value  = m.group(3);
            boolean optional       = (m.group(4) != null && m.group(4).equals("?"));

            Type type = null;
            try {
                type = TypeManager.get(context, parameter_type);
            } catch (NoSuchClass nsc) {
                PutUpFailure.createAndThrow (context, nsc, "Type of declared parameter '%(declaration)' does not exist", "declaration", declaration);
                throw (PutUpFailure) null; // compiler insists
            }

            if (cocpbt == null) {
                declareParameter(context, parameter_name, type, optional, default_value, higher_ranking, toplevel, sw, dr, coder);
            }

            m.appendReplacement(sb, "$2$5");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    protected void declareParameter(CallContext context, String parameter_name, Type parameter_type, boolean optional, String default_value, DataSourceConnector higher_ranking, boolean toplevel, StringWriter sw, Vector<Integer> dr, Coder coder) throws PutUpFailure {
        if (sw != null) {
            dr.add(COCPIndices.BuildText_ParameterToDeclare);
            dr.add(coder.writeText(context, parameter_name));
            dr.add(coder.writeText(context, parameter_type.getId(context)));
            dr.add(coder.writeBoolean(context, optional));
            dr.add(coder.writeText(context, default_value));
        }

        DataSourceConnector dsc = parametermap.tryGet(context, parameter_name);
        DataSource default_value_ds = (default_value == null ? null : new DataSourceGeneric_ExpressionMember(context, default_value, this, parameter_type, this.site_id + "<DefaultValueOfParameter:" + parameter_name + ">"));

        if (dsc == null) {
            dsc = new DataSourceConnector_Parameter(context, parameter_type, parameter_name, higher_ranking, optional, false, this.site_id + "<Parameter:" + parameter_name + ">");
            if (default_value_ds != null) {
                try {
                    dsc.setDefaultValueDataSource(context, default_value_ds);
                } catch (TypeMismatch tm) {
                    PutUpFailure.createAndThrow (context, "Unexpected (impossible): provided default value for parameter '%(parname)' does not match type of data source", "parname", parameter_name);
                }
            }
            parametermap.set(context, parameter_name, dsc);
        } else {
            Type dsctype = dsc.getType(context);
            if (dsctype == null) {
                dsc.setType(context, parameter_type);
            } else {
                if (parameter_type == null || dsctype.equals(parameter_type) || dsctype.isA(context, parameter_type)) {
                    // ok
                } else {
                    if (parameter_type.isA(context, dsctype)) {
                        dsc.setType(context, parameter_type);
                    } else {
                        PutUpFailure.createAndThrow (context, FactorySiteStringPool.get(context, "0.5.21" /* Parameter '%(parname)' is used inconsistent: one occurence expects a type '%(expected1)', while the other occurence expects '%(expected2)' */), "parname", parameter_name, "expected1", dsctype.getName(context), "expected2", parameter_type.getName(context));
                    }
                }
            }
            if (default_value_ds != null) {
                if (dsc.getDefaultValueDataSource(context) != null) {
                    PutUpFailure.createAndThrow (context, "Default value for parameter '%(parname)' is defined twice, only one definition is possible", "parname", parameter_name);
                }
            }
        }

        if (toplevel) {
            if (this.root_arguments == null) {
                this.root_arguments = Factory_Vector_String_long_.construct(context);
            }
            this.root_arguments.append(context, parameter_name);
        }
    }

    protected void resetParameters(CallContext context) {
        this.current_pass = 1;
        this.interruption_pass = -1;
        for (IteratorItemIndex_DataSourceConnector_String_ iiidscs = this.parametermap.getNavigator(context);
             iiidscs.canGetCurrent(context);
             iiidscs.next(context)) {
            DataSourceConnector dsc = iiidscs.tryGetCurrent(context);
            try {
                if (! (dsc instanceof DataSourceConnector_Parameter)) {
                    CustomaryContext.create(Context.create(context)).throwAssertionProvedFalse(context, "FactorySite parameter not of type DataSourceConnector_Parameter, but of type '%(got)'", "got", dsc.getClass());
                    throw (ExceptionAssertionProvedFalse) null; // compiler insists
                }

                if (! ((DataSourceConnector_Parameter) dsc).isStatic(context) || ! dsc.isValid(context)) {
                    dsc.setDataSource(context, null);
                }
            } catch (TypeMismatch tm) {
            }
        }
    }

    protected void setParameter(CallContext context, String name, DataSource ds) throws DoesNotExist, TypeMismatch {
        DataSourceConnector dsc = parametermap.get(context, name);
        dsc.setDataSource(context, ds);
    }

    protected void setOptionalParameter(CallContext context, String name, DataSource ds) throws TypeMismatch {
        DataSourceConnector dsc = parametermap.tryGet(context, name);
        if (dsc != null) {
            dsc.setDataSource(context, ds);
        }
    }

    public void setStaticParameter(CallContext context, String name, Object object) throws DoesNotExist, TypeMismatch {
        setStaticParameter(context, name, object, false);
    }

    public void setOptionalStaticParameter(CallContext context, String name, Object object) throws TypeMismatch {
        try {
            setStaticParameter(context, name, object, true);
        } catch (DoesNotExist dne) {
            // should be impossible
        }
    }

    protected synchronized void setStaticParameter(CallContext context, String name, Object object, boolean optional) throws DoesNotExist, TypeMismatch {
        DataSourceConnector dsc = optional ? parametermap.tryGet(context, name) : parametermap.get(context, name);
        if (optional && dsc == null) { return; }
        
        dsc.setDataSource(context, new DataSourceGeneric_Member(context, object, this.site_id + "<StaticParameter:" + name + ">"));

        if (! (dsc instanceof DataSourceConnector_Parameter)) {
            CustomaryContext.create(Context.create(context)).throwAssertionProvedFalse(context, "FactorySite parameter not of type DataSourceConnector_Parameter, but of type '%(got)'", "got", dsc.getClass());
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
        }

        ((DataSourceConnector_Parameter) dsc).setIsStatic(context, true);
    }

    protected void setOutParameter(CallContext context, String name, DataSourceSlot dss) throws DoesNotExist, TypeMismatch {
        DataSourceConnector dsc = parametermap.get(context, name);
        dss.setDataSource(context, dsc);
    }

    protected void setParameters(CallContext context, java.util.Map parameters)  throws ValidationFailure {
        this.resetParameters(context);

        if (parameters != null) {
            java.util.Set entry_set = parameters.entrySet();
            java.util.Iterator iterator = entry_set.iterator();
            while (iterator.hasNext()) {
                java.util.Map.Entry me = (java.util.Map.Entry) iterator.next();
                String key = (String) me.getKey();
                Object o = me.getValue();
                try {
                    if (o instanceof FactorySiteOutParameter) {
                        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, FactorySiteStringPool.get(context, "1.2.20" /* Factory_Aggregate[%(oid)].create(): setting out parameter '%(key)' */), "oid", this, "key", key); }
                        FactorySiteOutParameter fsop = (FactorySiteOutParameter) o;
                        this.setOutParameter(context, key, fsop);
                        if (fsop.getSourceLocationInfo(context) == null) {
                            fsop.setSourceLocationInfo(context, /* this.site_id + "<OutParameter:" + */ key /* + ">" */);
                        }
                    } else if (o instanceof FactorySiteOptionalParameter) {
                        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, FactorySiteStringPool.get(context, "1.2.24" /* Factory_Aggregate[%(oid)].create(): setting optional parameter '%(key)' */), "oid", this, "key", key); }
                        DataSource ds = new DataSourceGeneric_Member(context, ((FactorySiteOptionalParameter) o).getParameter(context), this.site_id + "<OptionalParameter:" + key + ">");
                        this.setOptionalParameter(context, key, ds);
                    } else if (o instanceof FactorySiteNullParameter) {
                        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, FactorySiteStringPool.get(context, "0.5.22" /* Factory_Aggregate[%(oid)].create(): setting null parameter '%(key)' */), "oid", this, "key", key); }
                        DataSource ds = new DataSourceNull(context, ((FactorySiteNullParameter) o).getType(context), this.site_id + "<NullParameter:" + key + ">");
                        this.setOptionalParameter(context, key, ds);
                    } else {
                        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, FactorySiteStringPool.get(context, "1.2.11" /* Factory_Aggregate[%(oid)].create(): setting parameter '%(key)' */), "oid", this, "key", key); }
                        DataSource ds = new DataSourceGeneric_Member(context, o, /* this.site_id + "<Parameter:" + */ key /* + ">"*/);
                        this.setParameter(context, key, ds);
                    }
                } catch (DoesNotExist dne) {
                    ValidationFailure.createAndThrow(context, dne, "Invalid parameter '%(parameter)'", "parameter", key);
                } catch (TypeMismatch tm) {
                    ValidationFailure.createAndThrow(context, tm, "Invalid parameter '%(parameter)'", "parameter", key);
                }
            }
        }
    }

    public synchronized ValidationFailure validateParameters (CallContext context, java.util.Map parameters) {
        String invalid_ones = null;
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Validating parameters..."); }

        try {
            this.setParameters(context, parameters);
        } catch (ValidationFailure vf) {
            return vf;
        }

        for (IteratorItemIndex_DataSourceConnector_String_ iiidscs = this.parametermap.getNavigator(context);
             iiidscs.canGetCurrent(context);
             iiidscs.next(context)) {
            DataSourceConnector dsc = iiidscs.tryGetCurrent(context);
            if (! dsc.isValid(context)) {
                if (    dsc instanceof DataSourceConnector_Parameter
                     && (    ((DataSourceConnector_Parameter) dsc).isOptional(context)
                          || ((DataSourceConnector_Parameter) dsc).isCovered(context)
                        )
                   ) {
                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Parameter '%(parameter)' is invalid, but optional or already covered", "parameter", iiidscs.tryGetCurrentIndex(context)); }
                } else {
                    String name = iiidscs.tryGetCurrentIndex(context);
                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Parameter '%(parameter)' is invalid", "parameter", name); }
                    if (invalid_ones == null) {
                        invalid_ones = new String();
                    } else {
                        invalid_ones += ", ";
                    }
                    invalid_ones += name;
                }
            } else {
                if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Parameter '%(parameter)' is valid", "parameter", iiidscs.tryGetCurrentIndex(context)); }
            }
        }
        if (invalid_ones != null) {
            return ValidationFailure.createValidationFailure(context, "Invalid parameters: %(invalidones)", "invalidones", invalid_ones);
        }

        this.resetParameters(context);

        return null;
    }

    public synchronized Map_DataSourceConnector_String_ getParameters (CallContext context) {
        return this.parametermap;
    }

    protected Expression[] define_variable_expression;
    protected String[]     define_variable_name;

    protected class RootScope implements Scope {
        public RootScope(CallContext context) {
        }
        public Object get (CallContext context, String name) throws NoSuchVariable {
            return doGet(context, name, true).value;
        }
        public Result doGet (CallContext context, String name, boolean throw_exception) throws NoSuchVariable {
            try {
                DataSource ds;

                ds = FactorySiteTextBased.this.referencemap.tryGet(context, (String) name);
                if (ds != null) {
                    try {
                        return new Result(ds.getValueAsObject(context));
                    } catch (IgnoreErraneousDataSource ieds) {
                        return null;
                    }
                }

                ds = FactorySiteTextBased.this.parametermap.tryGet(context, (String) name);
                if (ds != null) {
                    if (    ds instanceof DataSourceConnector_Parameter
                         && ((DataSourceConnector_Parameter)ds).isValid(context) == false
                         && ((DataSourceConnector_Parameter)ds).isOptional(context)
                       ) {
                        return new Result(null);
                    } else {
                        try {
                            return new Result(ds.getValueAsObject(context));
                        } catch (IgnoreErraneousDataSource ieds) {
                            return null;
                        }
                    }
                }

                if (name.equals("current_scope")) {
                    return new Result(FactorySiteTextBased.this.getCurrentScope(context));
                }
                
                if (throw_exception) {
                    NoSuchVariable.createAndThrow(context, "Variable '%(name)' is not defined within OCP scope", "name", name);
                    throw (NoSuchVariable) null;
                } else {
                    return null;
                }
            } catch (DataSourceUnavailable dsu) {
                NoSuchVariable.createAndThrow(context, dsu, "Variable '%(name)' is defined, but it's value cannot be retrieved", "name", name);
                throw (NoSuchVariable) null;
            }
        }
        public Object get (CallContext context, String name, String search_name_space) throws NoSuchVariable {
            if (search_name_space != null && search_name_space.length() != 0) {
                NoSuchVariable.createAndThrow(context, "Variable '%(name)' is not defined in name space '%(namespace)'", "name", name, "namespace", search_name_space);
                throw (NoSuchVariable) null;
            }
            return get(context, name);
        }
        public Result doGet (CallContext context, String name, String search_name_space, boolean throw_exception) throws NoSuchVariable {
            if (search_name_space != null && search_name_space.length() != 0) {
                if (throw_exception) {
                    NoSuchVariable.createAndThrow(context, "Variable '%(name)' is not defined in name space '%(namespace)'", "name", name, "namespace", search_name_space);
                    throw (NoSuchVariable) null;
                } else {
                    return null;
                }
            }
            return doGet(context, name, throw_exception);
        }
        public boolean getIsSealed (CallContext context) {
            return true;
        }
        public void set (CallContext context, String name, Object value) {
            CustomaryContext.create((Context)context).throwLimitation(context, "Root scope in FactorySite is not modifyable (when trying to set variable '%(name)')", "name", name);
            throw (ExceptionLimitation) null; // compiler insists
        }
        public boolean trySet (CallContext context, String name, Object value) {
            CustomaryContext.create((Context)context).throwLimitation(context, "Root scope in FactorySite is not modifyable (when trying to set variable '%(name)')", "name", name);
            throw (ExceptionLimitation) null; // compiler insists
        }
        public void set (CallContext context, String name, String name_space, Object value) {
            CustomaryContext.create((Context)context).throwLimitation(context, "Root scope in FactorySite is not modifyable (when trying to set variable '%(name)')", "name", name);
            throw (ExceptionLimitation) null; // compiler insists
        }
        public boolean trySet (CallContext context, String name, String name_space, Object value) {
            CustomaryContext.create((Context)context).throwLimitation(context, "Root scope in FactorySite is not modifyable (when trying to set variable '%(name)')", "name", name);
            throw (ExceptionLimitation) null; // compiler insists
        }
        public void setOnDemand (CallContext context, String name, com.sphenon.basics.data.DataSource ds) {
            CustomaryContext.create((Context)context).throwLimitation(context, "Root scope in FactorySite is not modifyable (when trying to set variable '%(name)')", "name", name);
            throw (ExceptionLimitation) null; // compiler insists
        }
        public boolean containsNameSpace (CallContext context, String name_space) {
            if (name_space == null || name_space.isEmpty()) {
                return true;
            }
            return false;
        }
        public Scope getNameSpace (CallContext context, String name_space) {
            if (name_space == null || name_space.isEmpty()) {
                return this;
            }
            return null;
        }
        public Object get (String name) throws NoSuchVariable {
            return get(RootContext.getFallbackCallContext(), name);
        }
        public Result tryGetWithNull (String name) {
            try {
                return doGet(RootContext.getFallbackCallContext(), name, false);
            } catch (NoSuchVariable nsv) {
                // can't happen
                return null;
            }
        }
        public Result tryGetWithNull (CallContext context, String name) {
            try {
                return doGet(context, name, false);
            } catch (NoSuchVariable nsv) {
                // can't happen
                return null;
            }
        }
        public Result tryGetWithNull (CallContext context, String name, String search_name_space) {
            try {
                return doGet(context, name, search_name_space, false);
            } catch (NoSuchVariable nsv) {
                // can't happen
                return null;
            }
        }
        public Object tryGet (String name) {
            try {
                Result result = doGet(RootContext.getFallbackCallContext(), name, false);
                return (result == null ? null : result.value);
            } catch (NoSuchVariable nsv) {
                // can't happen
                return null;
            }
        }
        public Object tryGet (CallContext context, String name) {
            try {
                Result result = doGet(context, name, false);
                return (result == null ? null : result.value);
            } catch (NoSuchVariable nsv) {
                // can't happen
                return null;
            }
        }
        public Object tryGet (CallContext context, String name, String search_name_space) {
            try {
                Result result = doGet(context, name, search_name_space, false);
                return (result == null ? null : result.value);
            } catch (NoSuchVariable nsv) {
                // can't happen
                return null;
            }
        }
        public Vector<Variable> getAllVariables(CallContext context) {
            return getAllVariables(context, null);
        }

        public Vector<Variable> getAllVariables(CallContext context, String pattern) {
            Vector<Variable> result = new Vector<Variable>();
            HashMap<String,String> check = new HashMap<String,String>();
            if (FactorySiteTextBased.this.referencemap != null) {
                for (IteratorItemIndex_DataSourceConnector_String_ iiidscs = FactorySiteTextBased.this.referencemap.getNavigator(context);
                     iiidscs.canGetCurrent(context);
                     iiidscs.next(context)
                    ) {
                    String name = iiidscs.tryGetCurrentIndex(context);
                    if (pattern == null || name.matches(pattern)) {
                        if (check.containsKey(name) == false) {
                            result.add(new Class_Variable(context, name, null, iiidscs.tryGetCurrent(context)));
                            check.put(name, name);
                        }
                    }
                }
            }
            if (FactorySiteTextBased.this.parametermap != null) {
                for (IteratorItemIndex_DataSourceConnector_String_ iiidscs = FactorySiteTextBased.this.parametermap.getNavigator(context);
                     iiidscs.canGetCurrent(context);
                     iiidscs.next(context)
                    ) {
                    String name = iiidscs.tryGetCurrentIndex(context);
                    if (pattern == null || name.matches(pattern)) {
                        if (check.containsKey(name) == false) {
                            result.add(new Class_Variable(context, name, null, iiidscs.tryGetCurrent(context)));
                            check.put(name, name);
                        }
                    }
                }
            }
            return result;
        }
    }

    protected Scope root_scope;
    protected Scope current_scope;

    protected Scope buildAndGetRootScope(CallContext context) throws DataSourceUnavailable {
        if (this.root_scope == null) {
            this.root_scope = new RootScope(context);
            if (this.define_variable_expression != null) {
                this.root_scope = new Class_Scope(context, null, this.root_scope);
                for (int index=0; index<this.define_variable_name.length; index++) {
                    Object object = null;
                    try {
                        object = this.define_variable_expression[index].evaluate(context, this.root_scope);
                    } catch (EvaluationFailure ef) {
                         DataSourceUnavailable.createAndThrow(context, ef, "While preparing root scope, evaluation of expression '%(expression)' failed", "expression", this.define_variable_expression[index].getExpression(context));
                         throw (DataSourceUnavailable) null; // compiler insists
                    }
                    this.root_scope.set(context, this.define_variable_name[index], object);
                }
            }
        }
        return this.root_scope;
    }

    public Scope getCurrentScope(CallContext context) throws DataSourceUnavailable {
        return (current_scope != null ? current_scope : (root_scope != null ? root_scope : (root_scope = buildAndGetRootScope(context))));
    }

    public Scope getCurrentScopeOverride(CallContext context) {
        return this.current_scope;
    }

    public void setCurrentScopeOverride(CallContext context, Scope scope) {
        this.current_scope = scope;
    }

    static public void resetSubTree(CallContext context, DataSource ds) {
        if (ds instanceof Scaffold) {
            ((Scaffold)ds).reset(context);
            for (ScaffoldParameter sp : ((Scaffold)ds).getParameters(context).getIterable_ScaffoldParameter_(context)) {
                resetSubTree(context, sp.getValue(context));
            }
        }
    }

    protected boolean is_prebuilded;

    public synchronized Object prebuild (CallContext context) throws BuildFailure {
        context = Context.create(context, this.location_context);

        this.root_scope = null;
        this.current_scope = null;
        for (Iterator_Scaffold_ is = all_scaffolds.getNavigator(context); is.canGetCurrent(context); is.next(context)) {
            is.tryGetCurrent(context).reset(context);
        }
        this.is_prebuilded = true;

        if (this.in_use) {
            CustomaryContext.create((Context)context).throwLimitation(context, "FactorySite '%(siteid)' invoked recursively", "siteid", FactorySiteTextBased.this.site_id);
            throw (ExceptionLimitation) null; // compilernsists
        }

        this.in_use = true;

        try {
            this.current_pass = 0;
            Object result = main_data_source_internal.getValueAsObject(context);
            return result;
        } catch (DataSourceUnavailable dsu) {
            BuildFailure.createAndThrow (context, dsu, FactorySiteStringPool.get(context, "0.5.16" /* Could not create main object */));
            throw (BuildFailure) null; // compiler insists
        } catch (IgnoreErraneousDataSource ieds) {
            BuildFailure.createAndThrow (context, ieds, "Cannot ignore top level erraneous data source (where did the problems went to?)");
            throw (BuildFailure) null; // compiler insists
        } finally {
            this.in_use = false;
        }
    }

    protected boolean in_use;
    protected int current_pass;
    protected int interruption_pass;

    public int getCurrentPass(CallContext context) {
        return this.current_pass;
    }

    public int getInterruptionPass (CallContext context) {
        return this.interruption_pass;
    }

    public void setInterruptionPass (CallContext context, int interruption_pass) {
        this.interruption_pass = interruption_pass;
    }
        
    public Object build (CallContext context, java.util.Map parameters) throws BuildFailure {
        return build(context, parameters, -1);
    }

    public synchronized Object build (CallContext context, java.util.Map parameters, int ip) throws BuildFailure {
        if (this.stop_watch != null) { this.stop_watch.start(context, null, "buildbegin"); }

        context = Context.create(context, this.location_context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Building '%(siteid)'...", "siteid", this.site_id); }

        if (this.is_prebuilded == false) {
            this.root_scope = null;
            this.current_scope = null;
            for (Iterator_Scaffold_ is = all_scaffolds.getNavigator(context); is.canGetCurrent(context); is.next(context)) {
                is.tryGetCurrent(context).reset(context);
            }
        }

        try {
            this.setParameters(context, parameters);
        } catch (ValidationFailure vf) {
            CustomaryContext.create(Context.create(context)).throwPreConditionViolation(context, vf, "Invalid parameter for FactorySite (should be prevalidated before calling)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        this.setInterruptionPass(context, ip);

        try {
            Object result = main_data_source.getValueAsObject(context);

            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Building '%(siteid)' - done, result '%(result)", "siteid", this.site_id, "result", result); }

            if (this.stop_watch != null) { this.stop_watch.start(context, null, "buildend"); }
            return result;
        } catch (DataSourceUnavailable dsu) {
            if (this.stop_watch != null) { this.stop_watch.start(context, null, "buildfailed"); }
            BuildFailure.createAndThrow (context, dsu, FactorySiteStringPool.get(context, "0.5.16" /* Could not create main object */));
            throw (BuildFailure) null; // compiler insists
        } catch (IgnoreErraneousDataSource ieds) {
            if (this.stop_watch != null) { this.stop_watch.start(context, null, "buildfailed"); }
            BuildFailure.createAndThrow (context, ieds, "Cannot ignore top level erraneous data source (where did the problems went to?)");
            throw (BuildFailure) null; // compiler insists
        }
    }

    protected Vector<BuildAssertion> postponed_pre_conditions;

    public void addPostponedPreCondition(CallContext context, BuildAssertion build_assertion) {
        if (this.postponed_pre_conditions == null) {
            this.postponed_pre_conditions = new Vector<BuildAssertion>();
        }
        this.postponed_pre_conditions.add(build_assertion);
    }

    protected Vector<BuildAssertion> postponed_post_conditions;

    public void addPostponedPostCondition(CallContext context, BuildAssertion build_assertion) {
        if (this.postponed_post_conditions == null) {
            this.postponed_post_conditions = new Vector<BuildAssertion>();
        }
        this.postponed_post_conditions.add(build_assertion);
    }

    protected Vector<BuildScript> postponed_pre_build_scripts;

    public void addPostponedPreBuildScript(CallContext context, BuildScript build_script) {
        if (this.postponed_pre_build_scripts == null) {
            this.postponed_pre_build_scripts = new Vector<BuildScript>();
        }
        this.postponed_pre_build_scripts.add(build_script);
    }

    protected Vector<BuildScript> postponed_post_build_scripts;

    public void addPostponedPostBuildScript(CallContext context, BuildScript build_script) {
        if (this.postponed_post_build_scripts == null) {
            this.postponed_post_build_scripts = new Vector<BuildScript>();
        }
        this.postponed_post_build_scripts.add(build_script);
    }
}
