package com.sphenon.engines.factorysite.json;

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
import com.sphenon.formats.json.*;
import com.sphenon.formats.json.returncodes.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.InputStream;

public class BuildTextJSONFactory implements BuildTextFactory {

    static final public Class _class = BuildTextJSONFactory.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    protected BuildText              result;
    protected Map<String, BuildText> bt_by_oid;
    protected String                 base;
    protected String                 polymorphic;
    protected JsonNode               meta_node;

    static protected StopWatch stop_watch;
    static protected boolean   initialised;

    // static protected Vector_BuildTextJSONPreprocessor_long_ preprocessors;

    // static public void registerPreprocessor(CallContext context, BuildTextJSONPreprocessor preprocessor) {
    //     if (preprocessors == null) {
    //         preprocessors = Factory_Vector_BuildTextJSONPreprocessor_long_.construct(context);
    //     }
    //     preprocessors.append(context, preprocessor);
    // }

    public BuildTextJSONFactory (CallContext context, JSONNode node, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, node.getFirstNode(context), bt_by_oid_override, signature_override);
    }

    public BuildTextJSONFactory (CallContext context, Data_MediaObject data, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, data.getStream(context), bt_by_oid_override, signature_override);
    }

    static protected JsonNode createJsonNode(CallContext context, InputStream input_stream) throws InvalidDocument {
        try {
            return JSONNode.createJSONNode(context, input_stream).getFirstNode(context);
        } catch (InvalidJSON ij) {
            InvalidDocument.createAndThrow(context, ij, "Could not parse JSON for factory site");
            throw (InvalidDocument) null; // compiler insists
        }
    }

    public BuildTextJSONFactory (CallContext context, InputStream input_stream, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, createJsonNode(context, input_stream), bt_by_oid_override, signature_override);
    }

    static protected JsonNode createJsonNode(CallContext context, File file) throws InvalidDocument {
        try {
            return JSONNode.createJSONNode(context, file).getFirstNode(context);
        } catch (InvalidJSON ij) {
            InvalidDocument.createAndThrow(context, ij, "Could not parse JSON for factory site");
            throw (InvalidDocument) null; // compiler insists
        }
    }

    public BuildTextJSONFactory (CallContext context, File file, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, createJsonNode(context, file), bt_by_oid_override, signature_override);
    }

    static protected JsonNode createJsonNode(CallContext context, String json_string) throws InvalidDocument {
        try {
            return JSONNode.createJSONNode(context, json_string).getFirstNode(context);
        } catch (InvalidJSON ij) {
            InvalidDocument.createAndThrow(context, ij, "Could not parse JSON for factory site");
            throw (InvalidDocument) null; // compiler insists
        }
    }

    public BuildTextJSONFactory (CallContext context, String json_string, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, createJsonNode(context, json_string), bt_by_oid_override, signature_override);
    }

    static public JsonNode getMetaNode(CallContext context, JsonNode node) {
        JsonNode meta_node = null;
        if (node != null) {
            if (node.isObject()) {
                meta_node = node;
            } else if (    node.isArray()
                        && node.get(0) != null
                        && node.get(0).get("@" + BuildTextKeywords.Meta) != null
                        && node.get(0).get("@" + BuildTextKeywords.Meta).asText().equals("true")
                      ) {
                meta_node = node.get(0);
            }
        }
        return meta_node;
    }

    static public String getAttribute(CallContext context, JsonNode node, String name) {
        if (node == null) { return null; }
        JsonNode an = node.get(name);
        return an == null ? null : an.asText();
    }

    static public String getAttribute(CallContext context, JsonNode node, String name1, String name2) {
        if (node == null) { return null; }
        JsonNode an;
        an = node.get(name1);
        if (an != null) { return an.asText(); }
        an = node.get(name2);
        if (an != null) { return an.asText(); }
        return null;
    }

    static public boolean isAttributeEmpty(CallContext context, JsonNode node, String name) {
        String attribute = getAttribute(context, node, name);
        return (attribute == null || attribute.isEmpty() ? true : false);
    }

    static public boolean isAttributeEmpty(CallContext context, JsonNode node, String name1, String name2) {
        String attribute;
        attribute = getAttribute(context, node, name1);
        if (attribute != null && attribute.isEmpty() == false) { return false; }
        attribute = getAttribute(context, node, name2);
        if (attribute != null && attribute.isEmpty() == false) { return false; }
        return true;
    }

    static public String getText(CallContext context, JsonNode node) {
        if (node.isValueNode()) {
            return node.asText();
        } else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(node);
            } catch (JsonProcessingException jpe) {
                CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, jpe, "Cannot create String from JSON '%(node)'", "node", node);
                throw (ExceptionAssertionProvedFalse) null; // compiler insists
            }
        }
    }

    static public String getNodeName(CallContext context, JsonNode node) {
        JsonNode meta_node = BuildTextJSONFactory.getMetaNode(context, node);
        return meta_node == null ? null : BuildTextJSONFactory.getAttribute(context, meta_node, "@" + BuildTextKeywords.Name);
    }

    protected BuildTextJSONFactory (CallContext call_context, JsonNode node, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
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

        this.result = this.create(context, node, "");

        if (this.stop_watch != null) { this.stop_watch.show(context, null, "postcreate"); }

        this.meta_node = getMetaNode(context, node);

        this.base = getAttribute(context, meta_node, "@" + BuildTextKeywords.Base);
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

        this.polymorphic = getAttribute(context, meta_node, "@" + BuildTextKeywords.Polymorphic);
        if (this.polymorphic != null && this.polymorphic.length() == 0) {
            this.polymorphic = null;
        }

        if (this.stop_watch != null) { this.stop_watch.stop(context, null, "create_end"); }
    }

    public BuildText getBuildText(CallContext context) {
        return this.result;
    }

    public String getNameSpace(CallContext context) {
        return getAttribute(context, this.meta_node, "@" + BuildTextKeywords.NameSpace);
    }

    public String getOCPId(CallContext context) {
        return getAttribute(context, this.meta_node, "@" + BuildTextKeywords.OCPId);
    }

    public String getSignature(CallContext context) {
        String signature = getAttribute(context, this.meta_node, "@" + BuildTextKeywords.Signature, BuildTextKeywords.SIGNATURE_UC);
        return signature != null && signature.length() != 0 ? signature : null;
    }

    public String getBase(CallContext context) {
        return this.base;
    }

    public String getPolymorphic(CallContext context) {
        return this.polymorphic;
    }

    protected BuildText create (CallContext context, JsonNode node, String name) throws InvalidDocument {
        return create(context, node, name, false);
    }

    protected BuildText create (CallContext context, JsonNode node, String name, boolean ignore_parameter) throws InvalidDocument {
        BuildText result = null;        

        String info = null;
        // to be implemented, but JsonLocation is difficult to get
        // file possibly to be passed as argument
        // ((notification_level & Notifier.DIAGNOSTICS) != 0 ? ((String) node.getOwnerDocument().getUserData("file")) + ":" + ((String) node.getUserData("line")) : null);

        JsonNode meta_node = getMetaNode(context, node);

        boolean unnamed_value_field = false;
        int named_value_fields = 0;
        if (node.isObject()) {
            java.util.Iterator<String> iterator = node.fieldNames();
            while (iterator.hasNext()) {
                String field_name = iterator.next();
                if (field_name.startsWith("#")) {
                    /* skip, comment */
                } else if (field_name.equals("@")) {
                    unnamed_value_field = true;
                } else if (field_name.isEmpty() || field_name.charAt(0) != '@') {
                    named_value_fields++;
                }
            }
        }

        String oid = getAttribute(context, meta_node, "@" + BuildTextKeywords.OId, BuildTextKeywords.OID_UC);
        BuildTextMerged merged_result = null;        
        boolean override_append = false;

        if (bt_by_oid != null && oid != null && oid.length() != 0 && (result = bt_by_oid.get(oid)) != null) {
            if (result.getOverride(context) != null && result.getOverride(context).matches("merge|MERGE|append|APPEND|prepend|PREPEND")) {
                if ((result instanceof BuildTextComplex) == false) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, "OCP element merge is only applicable to BuildTextComplex items, not to '%(class)' (overridinig element - '%(id)')", "class", result.getClass().getName(), "id", this.getOCPId(context));
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

        int meta     = 0;
        int non_meta = 0;
        int value    = 0;

        if (node.isValueNode()) {
            result = new BuildTextSimpleJSON(context, node, name, info);
        } else if (isAttributeEmpty(context, meta_node, "@" + BuildTextKeywords.Switch) == false) {
            result = new BuildTextSwitchJSON(context, node, name, info, this);
        } else if (! ignore_parameter && isAttributeEmpty(context, meta_node, "@" + BuildTextKeywords.OptionalParameter) == false) {
            result = new BuildTextOptionalParameterJSON(context, node, new BuildTextParameterJSON(context, node, name, info), (BuildTextJSON) create(context, node, name, true), info);
        } else if (! ignore_parameter && isAttributeEmpty(context, meta_node, "@" + BuildTextKeywords.OptionalIdRef) == false) {
            result = new BuildTextOptionalRefByIdJSON(context, node, new BuildTextRefByIdJSON(context, node, name, info), (BuildTextJSON) create(context, node, name, true), info);
//         } else if (node.getAttribute("CONTENT") != null && node.getAttribute("CONTENT").equals("JSON/DOM")) {
//             result = new BuildTextDOMJSON(context, node, info);
//         } else if (node.getAttribute("CONTENT") != null && node.getAttribute("CONTENT").equals("JSON/Text")) {
//             result = new BuildTextSimpleJSON(context, node, true, info);
        } else if (    isAttributeEmpty(context, meta_node, "@" + BuildTextKeywords.Expression, BuildTextKeywords.EXPRESSION_UC) == false
                    && getAttribute(context, meta_node, "@" + BuildTextKeywords.Expression, BuildTextKeywords.EXPRESSION_UC).equals("value")
                    && unnamed_value_field == true
                    && named_value_fields == 0) {
            result = new BuildTextSimpleJSON(context, node, name, info);
        } else if (    isAttributeEmpty(context, meta_node, "@" + BuildTextKeywords.OIdRef) == false
                    && unnamed_value_field == false
                    && named_value_fields == 0) {
            result = new BuildTextRefJSON(context, node, name, info);
        } else if (    isAttributeEmpty(context, meta_node, "@" + BuildTextKeywords.IdRef) == false
                    && unnamed_value_field == false
                    && named_value_fields == 0) {
            result = new BuildTextRefByIdJSON(context, node, name, info);
        } else if (    isAttributeEmpty(context, meta_node, "@" + BuildTextKeywords.Null) == false
                    && unnamed_value_field == false
                    && named_value_fields == 0) {
            result = new BuildTextNullJSON(context, node, name, info);
        } else if (    ! ignore_parameter
                    && isAttributeEmpty(context, meta_node, "@" + BuildTextKeywords.Parameter, BuildTextKeywords.PARAMETER_UC) == false
                    && unnamed_value_field == false
                    && named_value_fields == 0) {
            result = new BuildTextParameterJSON(context, node, name, info);
        } else {
            result = new BuildTextComplexJSON(context, node, name, info, this);
        }

        if (bt_by_oid != null && oid != null && oid.length() != 0) {
            bt_by_oid.put(oid, result);
        }

        if (merged_result != null) {
            if ((result instanceof BuildTextComplex) == false) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, "OCP element merge is only applicable to BuildTextComplex items, not to '%(class)' (overridden element - '%(id)')", "class", result.getClass().getName(), "id", this.getOCPId(context));
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
