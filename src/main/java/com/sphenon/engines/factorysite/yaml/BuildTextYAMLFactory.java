package com.sphenon.engines.factorysite.yaml;

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
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.performance.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.data.*;
import com.sphenon.formats.yaml.*;
import com.sphenon.formats.yaml.returncodes.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

import java.io.InputStream;

public class BuildTextYAMLFactory implements BuildTextFactory {

    static final public Class _class = BuildTextYAMLFactory.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    protected BuildText              result;
    protected Map<String, BuildText> bt_by_oid;
    protected String                 base;
    protected String                 polymorphic;
    protected BuildTextYAMLMetainfo  meta;

    static protected StopWatch stop_watch;
    static protected boolean   initialised;

    // static protected Vector_BuildTextYAMLPreprocessor_long_ preprocessors;

    // static public void registerPreprocessor(CallContext context, BuildTextYAMLPreprocessor preprocessor) {
    //     if (preprocessors == null) {
    //         preprocessors = Factory_Vector_BuildTextYAMLPreprocessor_long_.construct(context);
    //     }
    //     preprocessors.append(context, preprocessor);
    // }

    public BuildTextYAMLFactory (CallContext context, YAMLNode node, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, node.getFirstNode(context), bt_by_oid_override, signature_override);
    }

    public BuildTextYAMLFactory (CallContext context, Data_MediaObject data, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, data.getStream(context), bt_by_oid_override, signature_override);
    }

    static protected Object createYamlNode(CallContext context, InputStream input_stream) throws InvalidDocument {
        try {
            return YAMLNode.createYAMLNode(context, input_stream).getFirstNode(context);
        } catch (InvalidYAML ij) {
            InvalidDocument.createAndThrow(context, ij, "Could not parse YAML for factory site");
            throw (InvalidDocument) null; // compiler insists
        }
    }

    public BuildTextYAMLFactory (CallContext context, InputStream input_stream, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, createYamlNode(context, input_stream), bt_by_oid_override, signature_override);
    }

    static protected Object createYamlNode(CallContext context, File file) throws InvalidDocument {
        try {
            return YAMLNode.createYAMLNode(context, file).getFirstNode(context);
        } catch (InvalidYAML ij) {
            InvalidDocument.createAndThrow(context, ij, "Could not parse YAML for factory site");
            throw (InvalidDocument) null; // compiler insists
        }
    }

    public BuildTextYAMLFactory (CallContext context, File file, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, createYamlNode(context, file), bt_by_oid_override, signature_override);
    }

    static protected Object createYamlNode(CallContext context, String yaml_string) throws InvalidDocument {
        try {
            return YAMLNode.createYAMLNode(context, yaml_string).getFirstNode(context);
        } catch (InvalidYAML ij) {
            InvalidDocument.createAndThrow(context, ij, "Could not parse YAML for factory site");
            throw (InvalidDocument) null; // compiler insists
        }
    }

    public BuildTextYAMLFactory (CallContext context, String yaml_string, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, createYamlNode(context, yaml_string), bt_by_oid_override, signature_override);
    }

    static public boolean isEmpty(CallContext context, String value) {
        return (value == null || value.isEmpty() ? true : false);
    }

    static public boolean isNonEmpty(CallContext context, String value) {
        return (value == null || value.isEmpty() ? false : true);
    }

    static public String getText(CallContext context, Object node) {
        // possibly handling of primitive types as values more directly in OCP?
        // otherwise they get parsed, stringified, and reparsed again...
        // see also below: isPrimitiveType(context, node)
        return node.toString();
    }

    protected BuildTextYAMLFactory (CallContext call_context, Object node, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

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

        Object value;
        {   Map map = null;
            if ((node instanceof Map) == false || (map = (Map) node).size() != 1) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, "Main element of OCP/YAML file '%(id)' must be a map with a single key/value entry", "id", this.getOCPId(context));
                throw (ExceptionConfigurationError) null; // compiler insists
            }
            Map.Entry entry = (Map.Entry) map.entrySet().iterator().next();
            Object key   = entry.getKey();
            value = entry.getValue();
            if (key instanceof String) {
                this.meta = new BuildTextYAMLMetainfo((String) key);
            } else if (key instanceof BuildTextYAMLMetainfo) {
                this.meta = (BuildTextYAMLMetainfo) key;
            } else {
                CustomaryContext.create((Context)context).throwConfigurationError(context, "The key in main element map of OCP/YAML file '%(id)' must be either a String or of type 'BuildTextYAMLMetainfo'", "id", this.getOCPId(context));
                throw (ExceptionConfigurationError) null; // compiler insists
            }
        }

        this.result = this.create(context, value, this.meta, this.getOCPId(context));

        if (this.stop_watch != null) { this.stop_watch.show(context, null, "postcreate"); }

        this.base = this.meta.getBase();
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

        this.polymorphic = this.meta.getPolymorphic();
        if (this.polymorphic != null && this.polymorphic.length() == 0) {
            this.polymorphic = null;
        }

        if (this.stop_watch != null) { this.stop_watch.stop(context, null, "create_end"); }
    }

    public BuildText getBuildText(CallContext context) {
        return this.result;
    }

    public String getNameSpace(CallContext context) {
        return this.meta.getNameSpace();
    }

    public String getOCPId(CallContext context) {
        return this.meta.getOCPId();
    }

    public String getSignature(CallContext context) {
        String signature = this.meta.getSignature();
        return signature != null && signature.length() != 0 ? signature : null;
    }

    public String getBase(CallContext context) {
        return this.base;
    }

    public String getPolymorphic(CallContext context) {
        return this.polymorphic;
    }

    protected BuildText create (CallContext context, Object node, BuildTextYAMLMetainfo meta, String source_location_info) throws InvalidDocument {
        return create(context, node, meta, false, source_location_info);
    }

    // Standard YAML tags 	
    // !!null 	null
    // !!set 	Set
    // !!omap, !!pairs 	List of Object[]
    // !!seq 	List     : ArrayList
    // !!map 	Map      : LinkedHashMap (the order is implicitly defined)
    protected boolean isPrimitiveType(CallContext context, Object node) {
        return (    node instanceof Boolean              // !!bool
                 || node instanceof Integer              // !!int
                 || node instanceof Long                 // !!int
                 || node instanceof java.math.BigInteger // !!int
                 || node instanceof Double               // !!float
                 || node instanceof byte[]               // !!binary
                 || node instanceof java.util.Date       // !!timestamp
                 || node instanceof String               // !!str
               ) ? true : false;
    }

    protected BuildText create (CallContext context, Object node, BuildTextYAMLMetainfo meta, boolean ignore_parameter, String source_location_info) throws InvalidDocument {
        BuildText result = null;        

        String info = null;
        // to be implemented, but YamlLocation is difficult to get
        // file possibly to be passed as argument
        // ((notification_level & Notifier.DIAGNOSTICS) != 0 ? ((String) node.getOwnerDocument().getUserData("file")) + ":" + ((String) node.getUserData("line")) : null);

        String oid = meta.getOId();
        BuildTextMerged merged_result = null;        
        boolean override_append = false;

        if (bt_by_oid != null && oid != null && oid.length() != 0 && (result = bt_by_oid.get(oid)) != null) {
            if (result.getOverride(context) != null && result.getOverride(context).matches("merge|MERGE|append|APPEND|prepend|PREPEND")) {
                if ((result instanceof BuildTextComplex) == false) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "OCP element merge is only applicable to BuildTextComplex items, not to '%(class)' (overridinig element - '%(info)')", "class", result.getClass().getName(), "info", source_location_info);
                    throw (ExceptionConfigurationError) null; // compiler insists
                }
                merged_result = new BuildTextMerged(context);
                if (result.getOverride(context).matches("append|APPEND")) {
                    merged_result.setSecond(context, (BuildTextComplex) result);
                    override_append = true;
                } else {
                    merged_result.setFirst(context, (BuildTextComplex) result);
                    override_append = false;
                }
                result = null;
            } else {
                return result;
            }
        }

        BuildTextYAMLDatainfo data = (node instanceof BuildTextYAMLDatainfo ? ((BuildTextYAMLDatainfo) node) : null);

        if (isPrimitiveType(context, node)) {
            result = new BuildTextSimpleYAML(context, node, meta, info);
        } else if (isNonEmpty(context, meta.getSwitch())) {
            result = new BuildTextSwitchYAML(context, node, meta, info, this);
        } else if (! ignore_parameter && isNonEmpty(context, meta.getOptionalParameter())) {
            result = new BuildTextOptionalParameterYAML(context, node, meta, new BuildTextParameterYAML(context, node, meta, info), (BuildTextYAML) create(context, node, meta, true, source_location_info), info);
        } else if (! ignore_parameter && isNonEmpty(context, meta.getOptionalIdRef())) {
            result = new BuildTextOptionalRefByIdYAML(context, node, meta, new BuildTextRefByIdYAML(context, node, meta, info), (BuildTextYAML) create(context, node, meta, true, source_location_info), info);
//         } else if (meta.getContent() != null && meta.getContent().equals("YAML/DOM")) {
//             result = new BuildTextDOMYAML(context, node, info);
//         } else if (meta.getContent() != null && meta.getContent().equals("YAML/Text")) {
//             result = new BuildTextSimpleYAML(context, node, true, info);
        } else if (    data != null
                    && isNonEmpty(context, data.getExpression())
                    && data.getExpressionType().equals("value")) {
            result = new BuildTextSimpleYAML(context, node, meta, info);
        } else if (    data != null
                    && isNonEmpty(context, data.getOIdRef())) {
            result = new BuildTextRefYAML(context, node, meta, info);
        } else if (    data != null
                    && isNonEmpty(context, data.getIdRef())) {
            result = new BuildTextRefByIdYAML(context, node, meta, info);
        } else if (    data != null
                    && isNonEmpty(context, data.getNull())) {
            result = new BuildTextNullYAML(context, node, meta, info);
        } else if (    data != null
                    && ! ignore_parameter
                    && isNonEmpty(context, data.getParameter())) {
            result = new BuildTextParameterYAML(context, node, meta, info);
        } else {
            result = new BuildTextComplexYAML(context, node, meta, info, this);
        }

        if (meta.getMetaData(context) != null) {
            for (Map.Entry<String,Object> entry : meta.getMetaData(context).entrySet()) {
                ((BuildTextBaseImpl) result).addMetaData(context, entry.getKey(), entry.getValue());
            }
        }

        String pass = meta.getPass();
        if (pass == null || pass.length() == 0) { pass = "1"; }

        if (meta.getPreConditions(context) != null) {
            for (String code : meta.getPreConditions(context)) {
                ((BuildTextBaseImpl) result).addPreCondition(context, code, pass, source_location_info);
            }
        }
        if (meta.getPostConditions(context) != null) {
            for (String code : meta.getPostConditions(context)) {
                ((BuildTextBaseImpl) result).addPostCondition(context, code, pass, source_location_info);
            }
        }
        if (meta.getPreBuildScripts(context) != null) {
            for (String code : meta.getPreBuildScripts(context)) {
                ((BuildTextBaseImpl) result).addPreBuildScript(context, code, pass, source_location_info);
            }
        }
        if (meta.getPostBuildScripts(context) != null) {
            for (String code : meta.getPostBuildScripts(context)) {
                ((BuildTextBaseImpl) result).addPostBuildScript(context, code, pass, source_location_info);
            }
        }
        if (meta.getPreBuildMessages(context) != null) {
            for (String code : meta.getPreBuildMessages(context)) {
                ((BuildTextBaseImpl) result).addPreBuildMessage(context, code, pass, source_location_info);
            }
        }
        if (meta.getPostBuildMessages(context) != null) {
            for (String code : meta.getPostBuildMessages(context)) {
                ((BuildTextBaseImpl) result).addPostBuildMessage(context, code, pass, source_location_info);
            }
        }
        if (meta.getPreBuildDumps(context) != null) {
            for (String code : meta.getPreBuildDumps(context)) {
                ((BuildTextBaseImpl) result).addPreBuildDump(context, code, pass, source_location_info);
            }
        }
        if (meta.getPostBuildDumps(context) != null) {
            for (String code : meta.getPostBuildDumps(context)) {
                ((BuildTextBaseImpl) result).addPostBuildDump(context, code, pass, source_location_info);
            }
        }

        if (bt_by_oid != null && oid != null && oid.length() != 0) {
            bt_by_oid.put(oid, result);
        }

        if (merged_result != null) {
            if ((result instanceof BuildTextComplex) == false) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, "OCP element merge is only applicable to BuildTextComplex items, not to '%(class)' (overridden element - '%(info)')", "class", result.getClass().getName(), "info", source_location_info);
                throw (ExceptionConfigurationError) null; // compiler insists
            }
            if (override_append) {
                merged_result.setFirst(context, (BuildTextComplex) result);
            } else {
                merged_result.setSecond(context, (BuildTextComplex) result);
            }
            result = merged_result;
        }

        return result;
    }

    public HashMap<String,String> getMetaData (CallContext context) { return null; }
}
