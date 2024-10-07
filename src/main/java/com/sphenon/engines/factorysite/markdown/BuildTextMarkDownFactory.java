package com.sphenon.engines.factorysite.markdown;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.performance.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.data.*;
import com.sphenon.formats.yaml.*;
import com.sphenon.formats.yaml.returncodes.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.mdocp.*;
import com.sphenon.engines.factorysite.tocp.*;
import com.sphenon.engines.factorysite.mdocp.ParseException;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

import java.io.InputStream;

public class BuildTextMarkDownFactory implements BuildTextFactory {

    static final public Class _class = BuildTextMarkDownFactory.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    protected BuildText              result;
    protected Map<String, BuildText> bt_by_oid;
    protected String                 base;

    static protected StopWatch stop_watch;
    static protected boolean   initialised;

    public BuildTextMarkDownFactory (CallContext context, Data_MediaObject data, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, parse(context, data.getStream(context)), bt_by_oid_override, signature_override);
    }

    public BuildTextMarkDownFactory (CallContext context, InputStream input_stream, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, parse(context, input_stream), bt_by_oid_override, signature_override);
    }

    public BuildTextMarkDownFactory (CallContext context, File file, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, parse(context, file), bt_by_oid_override, signature_override);
    }

    public BuildTextMarkDownFactory (CallContext context, String md_string, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, parse(context, md_string), bt_by_oid_override, signature_override);
    }

    static protected BuildText parse(CallContext context, InputStream input_stream) throws InvalidDocument {
        try {
            return build(context, MDOCPParser.parseMDOCP(context, input_stream, "<stream>"));
        } catch (ParseException pe) {
            InvalidDocument.createAndThrow(context, pe, "Could not parse MarkDown for factory site");
            throw (InvalidDocument) null; // compiler insists
        } catch (IOException ioe) {
            InvalidDocument.createAndThrow(context, ioe, "Could not read MarkDown for factory site");
            throw (InvalidDocument) null; // compiler insists
        }
    }

    static protected BuildText parse(CallContext context, File file) throws InvalidDocument {
        try {
            return build(context, MDOCPParser.parseMDOCP(context, file));
        } catch (ParseException pe) {
            InvalidDocument.createAndThrow(context, pe, "Could not parse MarkDown for factory site");
            throw (InvalidDocument) null; // compiler insists
        } catch (IOException ioe) {
            InvalidDocument.createAndThrow(context, ioe, "Could not read MarkDown for factory site");
            throw (InvalidDocument) null; // compiler insists
        }
    }

    static protected BuildText parse(CallContext context, String md_string) throws InvalidDocument {
        try {
            return build(context, MDOCPParser.parseMDOCP(context, md_string, "<string>"));
        } catch (ParseException pe) {
            InvalidDocument.createAndThrow(context, pe, "Could not parse MarkDown for factory site");
            throw (InvalidDocument) null; // compiler insists
        }
    }

    static protected BuildText build(CallContext context, TOCPASTNode node) {
        node.dumpToXML(context);
        Pair_BuildText_String_ pbts = TOCPBuildText.create(context, node);
        BuildText bt = pbts.getItem1(context);
        return bt;
    }

    protected BuildTextMarkDownFactory (CallContext context, BuildText mdocp, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {

        if (initialised == false) {
            initialised = true;
            this.stop_watch = StopWatch.optionallyCreate(context, _class, "message", Notifier.SELF_DIAGNOSTICS);
        }

        if (this.stop_watch != null) { this.stop_watch.start(context, null, "create_begin"); }

        this.bt_by_oid = bt_by_oid_override;

        // if (preprocessors != null) {
        //     for (int i=0; this.document == null && i<preprocessors.getSize(context); i++) {
        //         this.document = preprocessors.tryGet(context, i).getDocument(context, input_source);
        //     }
        // }

        if (this.stop_watch != null) { this.stop_watch.show(context, null, "create"); }

        this.result = mdocp;

        if (this.stop_watch != null) { this.stop_watch.show(context, null, "postcreate"); }

        this.base = this.result.getBase(context);

        if (this.base != null && this.base.length() == 0) {
            this.base = null;
        }
        if (this.base != null) {
            if (this.bt_by_oid == null) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "If build text with base attribute is parser, a BuildText-By-OID Map MUST be provided");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            if ( ! (this.result instanceof BuildTextComplex)) {
                InvalidDocument.createAndThrow(context, "An OCP with BASE Attribute MUST contain a complex node at top level.");
                throw (InvalidDocument) null; // compiler insists
            }
            for (Pair_BuildText_String_ item : ((BuildTextComplex) this.result).getItems(context).getIterable_Pair_BuildText_String__(context)) {
                String bt_oid = item.getItem2(context);
                BuildText more_derived_bt = this.bt_by_oid.get(bt_oid);
                BuildText more_base_bt    = item.getItem1(context);
                if (more_derived_bt == null) {
                    this.bt_by_oid.put(bt_oid, more_base_bt);
                } else if (   more_derived_bt.getOverride(context) != null
                           && more_derived_bt.getOverride(context).matches("merge|MERGE|append|APPEND|prepend|PREPEND")
                          ) {
                    if ((more_derived_bt instanceof BuildTextComplex) == false) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, "OCP element merge is only applicable to BuildTextComplex items, not to '%(class)' (more derived element - '%(id)')", "class", more_derived_bt.getClass().getName(), "id", this.getOCPId(context));
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                    if ((more_base_bt instanceof BuildTextComplex) == false) {
                        CustomaryContext.create((Context)context).throwConfigurationError(context, "OCP element merge is only applicable to BuildTextComplex items, not to '%(class)' (more base element - '%(id)')", "class", more_base_bt.getClass().getName(), "id", this.getOCPId(context));
                        throw (ExceptionConfigurationError) null; // compiler insists
                    }
                    BuildTextMerged btm = new BuildTextMerged(context);
                    if (more_derived_bt.getOverride(context).matches("append|APPEND")) {
                        btm.setFirst(context, (BuildTextComplex) more_base_bt);
                        btm.setSecond(context, (BuildTextComplex) more_derived_bt);
                    } else {
                        btm.setFirst(context, (BuildTextComplex) more_derived_bt);
                        btm.setSecond(context, (BuildTextComplex) more_base_bt);
                    }
                    this.bt_by_oid.put(bt_oid, btm);
                }
            }
        }
        if (signature_override != null && signature_override.length() != 0) {
            if ( ! (this.result instanceof BuildTextComplex)) {
                InvalidDocument.createAndThrow(context, "An OCP with signature override MUST contain a complex node at top level.");
                throw (InvalidDocument) null; // compiler insists
            }
            ((BuildTextComplex) this.result).setSignature(context, signature_override);
        }

        if (this.stop_watch != null) { this.stop_watch.stop(context, null, "create_end"); }
    }

    public BuildText getBuildText(CallContext context) {
        return this.result;
    }

    public String getNameSpace(CallContext context) {
        return this.result.getNameSpace(context);
    }

    public String getOCPId(CallContext context) {
        return this.result.getOCPId(context);
    }

    public String getSignature(CallContext context) {
        String signature = this.result.getSignature(context);
        return signature != null && signature.length() != 0 ? signature : null;
    }

    public String getBase(CallContext context) {
        return this.base;
    }

    public String getPolymorphic(CallContext context) {
        String polymorphic = this.result.getPolymorphic(context);
        return polymorphic != null && polymorphic.length() != 0 ? polymorphic : null;
    }

    public HashMap<String,String> getMetaData (CallContext context) { return null; }
}
