package com.sphenon.engines.factorysite.xml;

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
import com.sphenon.basics.xml.*;
import com.sphenon.basics.xml.returncodes.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.xerces.xni.parser.XMLInputSource;

import java.io.InputStream;

public class BuildTextXMLFactory implements BuildTextFactory {

    static final public Class _class = BuildTextXMLFactory.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    protected BuildText           result;
    protected Document            document;
    protected Map<String, BuildText> bt_by_oid;
    protected String                 base;
    protected String                 polymorphic;

    static protected StopWatch stop_watch;
    static protected boolean   initialised;

    static protected Vector_BuildTextXMLPreprocessor_long_ preprocessors;

    static public void registerPreprocessor(CallContext context, BuildTextXMLPreprocessor preprocessor) {
        if (preprocessors == null) {
            preprocessors = Factory_Vector_BuildTextXMLPreprocessor_long_.construct(context);
        }
        preprocessors.append(context, preprocessor);
    }

    public BuildTextXMLFactory (CallContext context, String uri, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, uri, new XMLInputSource(null, uri, null), null, bt_by_oid_override, signature_override);
    }

    public BuildTextXMLFactory (CallContext context, String resource_id, InputStream input_stream, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, resource_id, new XMLInputSource(null, null, null, input_stream, null), null, bt_by_oid_override, signature_override);
    }

    public BuildTextXMLFactory (CallContext context, Data_MediaObject data, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, data.getDispositionFilename(context), data instanceof Data_MediaObject_File ? new XMLInputSource(null, ((Data_MediaObject_File)(data)).getCurrentFile(context).getPath(), null) : new XMLInputSource(null, null, null, data.getStream(context), null), null, bt_by_oid_override, signature_override);
    }

    public BuildTextXMLFactory (CallContext context, XMLNode xml_node, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, "<xml-node>", null, xml_node, bt_by_oid_override, signature_override);
    }

    protected BuildTextXMLFactory (CallContext call_context, String resource_id, XMLInputSource input_source, XMLNode xml_node, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        if (initialised == false) {
            initialised = true;
            this.stop_watch = StopWatch.optionallyCreate(context, _class, "message", Notifier.SELF_DIAGNOSTICS);
        }

        if (this.stop_watch != null) { this.stop_watch.start(context, null, "create_begin"); }

        this.document = null;
        this.bt_by_oid = bt_by_oid_override;

        Element node = null;

        if (xml_node == null) {
            if (preprocessors != null) {
                for (int i=0; this.document == null && i<preprocessors.getSize(context); i++) {
                    this.document = preprocessors.tryGet(context, i).getDocument(context, input_source);
                }
            }

            if (this.stop_watch != null) { this.stop_watch.show(context, null, "xmlparse"); }

            if (this.document == null) {
                try {
                    this.document = XMLUtil.parse(context, input_source, notification_level, resource_id);
                } catch (InvalidXML ix) {
                    InvalidDocument.createAndThrow(context, ix, "XML '%(document)' is invalid", "document", resource_id);
                    throw (InvalidDocument) null; // compiler insists
                }
            }
            node = this.document.getDocumentElement();

        } else {
            Node n = xml_node.getDOMNodes(context).get(0);
            node = xml_node.isDocument(context) ? ((Document) n).getDocumentElement() : ((Element) n);
        }

        if (this.stop_watch != null) { this.stop_watch.show(context, null, "create"); }

        this.result = this.create(context, node);

        if (this.stop_watch != null) { this.stop_watch.show(context, null, "postcreate"); }

        this.base = node.getAttribute(BuildTextKeywords.BASE);
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

        this.polymorphic = node.getAttribute(BuildTextKeywords.POLYMORPHIC);
        if (this.polymorphic != null && this.polymorphic.length() == 0) {
            this.polymorphic = null;
        }

        if (this.stop_watch != null) { this.stop_watch.stop(context, null, "create_end"); }
    }

    public BuildText getBuildText(CallContext context) {
        return this.result;
    }

    public String getNameSpace(CallContext context) {
        return this.document.getDocumentElement().getAttribute("xmlns") ;
    }

    public String getOCPId(CallContext context) {
        return this.document.getDocumentElement().getAttribute(BuildTextKeywords.OCPID) ;
    }

    public String getSignature(CallContext context) {
        String signature = this.document.getDocumentElement().getAttribute(BuildTextKeywords.SIGNATURE);
        return signature != null && signature.length() != 0 ? signature : null;
    }

    public String getBase(CallContext context) {
        return this.base;
    }

    public String getPolymorphic(CallContext context) {
        return this.polymorphic;
    }

    protected BuildText create (CallContext context, Element node) throws InvalidDocument {
        return create(context, node, false);
    }

    protected BuildText create (CallContext context, Element node, boolean ignore_parameter) throws InvalidDocument {
        BuildText result = null;        

        String info = ((notification_level & Notifier.DIAGNOSTICS) != 0 ? ((String) node.getOwnerDocument().getUserData("file")) + ":" + ((String) node.getUserData("line")) : null);

        String oid = BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.OID, BuildTextKeywords.OID_UC);
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

        if (node.getAttribute(BuildTextKeywords.SWITCH) != null && node.getAttribute(BuildTextKeywords.SWITCH).length() != 0) {
            result = new BuildTextSwitchXML(context, node, info, this);
        } else if (! ignore_parameter && node.getAttribute(BuildTextKeywords.OPTIONALPARAMETER) != null && node.getAttribute(BuildTextKeywords.OPTIONALPARAMETER).length() != 0) {
            result = new BuildTextOptionalParameterXML(context, node, new BuildTextParameterXML(context, node, info), (BuildTextXML) create(context, node, true), info);
        } else if (! ignore_parameter && node.getAttribute(BuildTextKeywords.OPTIONALIDREF) != null && node.getAttribute(BuildTextKeywords.OPTIONALIDREF).length() != 0) {
            result = new BuildTextOptionalRefByIdXML(context, node, new BuildTextRefByIdXML(context, node, info), (BuildTextXML) create(context, node, true), info);
//         } else if (node.getAttribute(BuildTextKeywords.CONTENT) != null && node.getAttribute(BuildTextKeywords.CONTENT).equals("XML/DOM")) {
//             result = new BuildTextDOMXML(context, node, info);
//         } else if (node.getAttribute(BuildTextKeywords.CONTENT) != null && node.getAttribute(BuildTextKeywords.CONTENT).equals("XML/Text")) {
//             result = new BuildTextSimpleXML(context, node, true, info);
        } else if (BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.EXPRESSION, BuildTextKeywords.EXPRESSION_UC) != null && BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.EXPRESSION, BuildTextKeywords.EXPRESSION_UC).equals("value") && node.getChildNodes().getLength() == 1 && (node.getFirstChild().getNodeType() == Node.TEXT_NODE || node.getFirstChild().getNodeType() == Node.CDATA_SECTION_NODE)) {
            result = new BuildTextSimpleXML(context, node, info);
        } else if (node.getChildNodes().getLength() == 0 && node.getAttribute(BuildTextKeywords.OIDREF) != null && node.getAttribute(BuildTextKeywords.OIDREF).length() != 0) {
            result = new BuildTextRefXML(context, node, info);
        } else if (node.getChildNodes().getLength() == 0 && node.getAttribute(BuildTextKeywords.IDREF) != null && node.getAttribute(BuildTextKeywords.IDREF).length() != 0) {
            result = new BuildTextRefByIdXML(context, node, info);
        } else if (node.getChildNodes().getLength() == 0 && node.getAttribute(BuildTextKeywords.NULL) != null && node.getAttribute(BuildTextKeywords.NULL).length() != 0) {
            result = new BuildTextNullXML(context, node, info);
        } else if (! ignore_parameter && node.getChildNodes().getLength() == 0 && BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.PARAMETER, BuildTextKeywords.PARAMETER_UC) != null && BuildTextXMLFactory.getAttribute(node, BuildTextKeywords.PARAMETER, BuildTextKeywords.PARAMETER_UC).length() != 0) {
            result = new BuildTextParameterXML(context, node, info);
        } else {
            result = new BuildTextComplexXML(context, node, info, this);
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

    static public String getAttribute(Element node, String name1, String name2) {
        String value;
        value = node.getAttribute(name1);
        if (value != null && value.isEmpty() == false) { return value; }
        value = node.getAttribute(name2);
        if (value != null && value.isEmpty() == false) { return value; }
        return "";
    }
}
