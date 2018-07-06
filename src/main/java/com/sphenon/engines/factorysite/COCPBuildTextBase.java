package com.sphenon.engines.factorysite;

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
import com.sphenon.basics.system.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.xml.*;
import com.sphenon.basics.xml.returncodes.*;
import com.sphenon.formats.json.*;
import com.sphenon.formats.json.returncodes.*;
import com.sphenon.formats.yaml.*;
import com.sphenon.formats.yaml.returncodes.*;

import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.xml.*;
import com.sphenon.engines.factorysite.json.*;
import com.sphenon.engines.factorysite.yaml.*;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import org.w3c.dom.Node;

import com.fasterxml.jackson.databind.JsonNode;

abstract public class COCPBuildTextBase {
    static final public Class _class = COCPBuildTextBase.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    public COCPBuildTextBase(CallContext context, String xmlns, String ocpid, String polymorphic, String base_aggregate) {
        this.xmlns = xmlns;
        this.ocpid = ocpid;
        this.polymorphic = polymorphic;
        this.base_aggregate = base_aggregate;
    }

    abstract public BuildText create(CallContext context);

    protected String xmlns;

    public String getNameSpace (CallContext context) {
        return this.xmlns;
    }

    protected String ocpid;

    public String getOCPId (CallContext context) {
        return this.ocpid;
    }

    protected String polymorphic;

    public String getPolymorphic (CallContext context) {
        return this.polymorphic;
    }

    protected String base_aggregate;

    public String getBase (CallContext context) {
        return this.base_aggregate;
    }

    abstract protected int[][] getCOCPData(CallContext context);

    abstract protected Object evaluateCOCPCode(CallContext context, int index);

    abstract protected String getCOCPText(CallContext context, int index);

    protected StringCache string_cache;

    protected String getTextByIndex(CallContext context, int index) {
        if (this.string_cache == null) {
            this.string_cache = StringCache.getSingleton(context);
        }
        String sctext = string_cache.getText(context, index);
        return sctext;
    }

    static protected class DataComparator implements java.util.Comparator<int[]> {
        public int compare(int[] a1, int[] a2) {
            if (a1.length < 2) { return 1; }
            if (a2.length < 2) { return -1; }
            Integer a11 = a1[0];
            Integer a12 = a1[1];
            Integer a21 = a2[0];
            Integer a22 = a2[1];
            int c = a11.compareTo(a21);
            return c == 0 ? a12.compareTo(a22) : c;
        }
    }

    protected int[][] readCOCPData(CallContext context) {
        String cocp_resource = this.getClass().getName().replaceFirst(".*\\.","") + ".cocp";
        try {
            InputStream is = this.getClass().getResourceAsStream(cocp_resource);
            ObjectInputStream ois = new ObjectInputStream(is);
            int[][] data = (int[][]) ois.readObject();
            ois.close();
            is.close();

            if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: read data..."); }
            for (int i=0; i<data.length; i++) {
                StringBuilder diagnostics = null;
                if ((notification_level & Notifier.DIAGNOSTICS) != 0) { diagnostics = new StringBuilder(); diagnostics.append("row ").append(i).append(" ="); }
                for (int j=0; j<data[i].length; j++) {
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { diagnostics.append(" ").append(data[i][j]); }
                }                
                if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: read data, '%(row)'", "row", diagnostics.toString()); }
            }

            return data;
        } catch (ClassNotFoundException cnfe) {
            CustomaryContext.create((Context)context).throwInvalidState(context, cnfe, "Error while reading from serialized COCP data resource '%(resource)', class unexpectedly not found (resource should contain int[][])", "resource", cocp_resource);
            throw (ExceptionInvalidState) null; // compiler insists
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, ioe, "Error while reading from serialized COCP data resource '%(resource)'", "resource", cocp_resource);
            throw (ExceptionConfigurationError) null; // compiler insists
        } catch (Throwable t) {
            CustomaryContext.create((Context)context).throwVerificationFailure(context, t, "Error while reading from serialized COCP data resource '%(resource)'", "resource", cocp_resource);
            throw (ExceptionVerificationFailure) null; // compiler insists
        }
    }

    protected boolean getCOCPBoolean(CallContext context, int index) {
        return index == 0 ? false : true;
    }

    protected Map<String,int[]>[] cocp_data_maps;

    protected Object createItem(CallContext context, int item_index, String data_id) {

        if (this.cocp_data_maps == null) {
            synchronized (COCPBuildTextBase.class) {
                if (this.cocp_data_maps == null) {
                    this.cocp_data_maps = new Map[3];
                    this.cocp_data_maps[0] = new HashMap<String,int[]>();
                    this.cocp_data_maps[1] = new HashMap<String,int[]>();
                    this.cocp_data_maps[2] = new HashMap<String,int[]>();
                    for (int[] data : readCOCPData(context)) {
                        if (data.length == 0) { continue; }
                        int    ii = data[0];
                        String di = getTextByIndex(context, data[1]);
                        this.cocp_data_maps[ii].put(di, data);
                    }

                    this.readCOCPData(context);
                }
            }
        }

        switch (item_index) {
            case COCPIndices.COCPItem_BuildText: {
                int[] data = cocp_data_maps[item_index].get(data_id);
                if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Base: create BuildText '%(id)' : %(data)", "id", data_id, "data", data); }
                return createBT(context, data);
            }
            case COCPIndices.COCPItem_Scaffold: {
                int[] data = cocp_data_maps[item_index].get(data_id);
                if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Base: create Scaffold '%(ii)' / '%(id)' : %(data)", "ii", item_index, "id", data_id, "data", data); }
                return createS(context, data);
            }
            // case COCPIndices.COCPItem_Accessor:
            //     return createA(context, cocp_data_maps[2].get(data_id));
        }
        return null;
    }

    protected Vector_Pair_BuildText_String__long_ createBTS(CallContext context, COCPBuildTextComplex_String bt) {
        Vector_Pair_BuildText_String__long_ bts = Factory_Vector_Pair_BuildText_String__long_.construct(context);
        bt.setItems(context, bts);
        return bts;
    }

    // protected void createBTSChild(CallContext context, Vector_Pair_BuildText_String__long_ bts, BuildText bt, String name) {
    //     bts.append(context, new Pair_BuildText_String_(context, bt, name));
    // }

    protected BuildText createBT(CallContext context, int... arguments) {
        int    a = 2; // skip the first two, item_index and data_id

        int    code_class_index = (Integer) arguments[a++];
        String site_id          = getTextByIndex(context, (Integer) arguments[a++]);

        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: class '%(class)' ", "class", COCPIndices.classes[code_class_index]); }

        BuildText bt = null;
        switch (code_class_index) {
            case COCPIndices.COCPBuildTextNull_String:
                bt = new COCPBuildTextNull_String(context);
                break;
            case COCPIndices.COCPBuildTextSimple_String:
                bt = new COCPBuildTextSimple_String(context);
                break;
            case COCPIndices.COCPBuildTextDOM_Node:
                bt = new COCPBuildTextDOM_Node(context);
                break;
            case COCPIndices.COCPBuildTextComplex_String:
                bt = new COCPBuildTextComplex_String(context);
                break;
            case COCPIndices.COCPBuildTextRef_String:
                bt = new COCPBuildTextRef_String(context);
                break;
            case COCPIndices.COCPBuildTextParameter_String:
                bt = new COCPBuildTextParameter_String(context);
                break;
            case COCPIndices.COCPBuildTextOptionalParameter_String:
                bt = new COCPBuildTextOptionalParameter_String(context);
                break;
            case COCPIndices.COCPBuildTextRefById_String:
                bt = new COCPBuildTextRefById_String(context);
                break;
            case COCPIndices.COCPBuildTextOptionalRefById_String:
                bt = new COCPBuildTextOptionalRefById_String(context);
                break;
            case COCPIndices.COCPBuildTextSwitch_String:
                bt = new COCPBuildTextSwitch_String(context);
                break;
            default: {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Unhandled code class index '%(index)'", "index", code_class_index);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        }

        Map<String,Object> meta_data = null;

        while (a < arguments.length) {
            int method_index = (Integer) arguments[a++];
            switch (method_index) {
                case COCPIndices.BuildText_OCPId: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setOCPId(context, value);
                    break;
                }
                case COCPIndices.BuildText_NodeName: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setNodeName(context, value);
                    break;
                }
                case COCPIndices.BuildText_OID: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setOID(context, value);
                    break;
                }

                case COCPIndices.BuildText_Base: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setBase(context, value);
                    break;
                }
                case COCPIndices.BuildText_Polymorphic: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setPolymorphic(context, value);
                    break;
                }
                case COCPIndices.BuildText_AssignTo: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setAssignTo(context, value);
                    break;
                }
                case COCPIndices.BuildText_TypeName: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setTypeName(context, value);
                    break;
                }
                case COCPIndices.BuildText_FactoryName: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setFactoryName(context, value);
                    break;
                }
                case COCPIndices.BuildText_RetrieverName: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setRetrieverName(context, value);
                    break;
                }
                case COCPIndices.BuildText_MethodName: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setMethodName(context, value);
                    break;
                }
                case COCPIndices.BuildText_Alias: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setAlias(context, value);
                    break;
                }
                case COCPIndices.BuildText_AllowDynamicTypeCheck: {
                    Boolean value = getCOCPBoolean(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setAllowDynamicTypeCheck(context, value);
                    break;
                }
                case COCPIndices.BuildText_AllowMissingArguments: {
                    Boolean value = getCOCPBoolean(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setAllowMissingArguments(context, value);
                    break;
                }
                case COCPIndices.BuildText_IsSingleton: {
                    Boolean value = getCOCPBoolean(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setIsSingleton(context, value);
                    break;
                }
                case COCPIndices.BuildText_HaveDynamicParameters: {
                    Boolean value = getCOCPBoolean(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setHaveDynamicParameters(context, value);
                    break;
                }
                case COCPIndices.BuildText_NameAttribute: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setNameAttribute(context, value);
                    break;
                }
                case COCPIndices.BuildText_IsExpression: {
                    Boolean value = getCOCPBoolean(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setIsExpression(context, value);
                    break;
                }
                case COCPIndices.BuildText_IfExpression: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setIfExpression(context, value);
                    break;
                }
                case COCPIndices.BuildText_ForeachExpression: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setForeachExpression(context, value);
                    break;
                }
                case COCPIndices.BuildText_Signature: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setSignature(context, value);
                    break;
                }
                case COCPIndices.BuildText_Define: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setDefine(context, value);
                    break;
                }
                case COCPIndices.BuildText_Evaluator: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setEvaluator(context, value);
                    break;
                }
                case COCPIndices.BuildText_Catch: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setCatch(context, value);
                    break;
                }
                case COCPIndices.BuildText_AppliesTo: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setAppliesTo(context, value);
                    break;
                }
                case COCPIndices.BuildText_Listener: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setListener(context, value);
                    break;
                }
                case COCPIndices.BuildText_Pass: {
                    Integer value = (Integer) arguments[a++];
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setPass(context, value);
                    break;
                }
                case COCPIndices.BuildText_Override: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setOverride(context, value);
                    break;
                }
                case COCPIndices.BuildText_NameSpace: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setNameSpace(context, value);
                    break;
                }
                case COCPIndices.BuildText_SourceLocationInfo: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setSourceLocationInfo(context, value);
                    break;
                }
                case COCPIndices.BuildText_IsExpanded: {
                    Boolean value = getCOCPBoolean(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextBaseImpl) bt).setIsExpanded(context, value);
                    break;
                }
                case COCPIndices.BuildText_Name: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextParameter_String) bt).setName(context, value);
                    break;
                }
                case COCPIndices.BuildText_Parameter: {
                    String bt_id = getTextByIndex(context, (Integer) arguments[a++]);
                    BuildTextParameter_String value = (BuildTextParameter_String) createItem(context, COCPIndices.COCPItem_BuildText, bt_id);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", bt_id); }
                    ((BuildTextOptionalParameter_String) bt).setParameter(context, value);
                    break;
                }
                case COCPIndices.BuildText_Fallback_Parameter: {
                    String    bt_id = getTextByIndex(context, (Integer) arguments[a++]);
                    BuildText value = (BuildText) createItem(context, COCPIndices.COCPItem_BuildText, bt_id);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", bt_id); }
                    ((BuildTextOptionalParameter_String) bt).setFallback(context, value);
                    break;
                }
                case COCPIndices.BuildText_IdRef: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextRefById_String) bt).setIdRef(context, value);
                    break;
                }
                case COCPIndices.BuildText_RefById: {
                    String bt_id = getTextByIndex(context, (Integer) arguments[a++]);
                    BuildTextRefById_String value = (BuildTextRefById_String) createItem(context, COCPIndices.COCPItem_BuildText, bt_id);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", bt_id); }
                    ((BuildTextOptionalRefById_String) bt).setRefById(context, value);
                    break;
                }
                case COCPIndices.BuildText_Fallback_RefById: {
                    String    bt_id = getTextByIndex(context, (Integer) arguments[a++]);
                    BuildText value = (BuildText) createItem(context, COCPIndices.COCPItem_BuildText, bt_id);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", bt_id); }
                    ((BuildTextOptionalRefById_String) bt).setFallback(context, value);
                    break;
                }
                case COCPIndices.BuildText_OIDRef: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextRef_String) bt).setOIDRef(context, value);
                    break;
                }
                case COCPIndices.BuildText_Text: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextSimple_String) bt).setText(context, value);
                    break;
                }
                case COCPIndices.BuildText_XMLNode: {
                    String xml = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", xml); }
                    ((BuildTextDOM_Node) bt).setNode(context, (Node) parseMDXML(context, (String) xml, false, site_id));
                    break;
                }
                case COCPIndices.BuildText_JSONNode: {
                    String json = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", json); }
                    ((BuildTextJSONRawJSON) bt).setNode(context, (JsonNode) parseMDJSON(context, (String) json, false, site_id));
                    break;
                }
                case COCPIndices.BuildText_YAMLNode: {
                    String yaml = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", yaml); }
                    ((BuildTextYAMLRawYAML) bt).setNode(context, (Object) parseMDYAML(context, (String) yaml, false, site_id));
                    break;
                }
                case COCPIndices.BuildText_Items: {
                    int size = (Integer) arguments[a++];
                    Vector_Pair_BuildText_String__long_ bts = Factory_Vector_Pair_BuildText_String__long_.construct(context);
                    for (int c=0; c<size; c++) {
                        String    bt_id_2 = getTextByIndex(context, (Integer) arguments[a++]);
                        BuildText bt2     = (BuildText) createItem(context, COCPIndices.COCPItem_BuildText, bt_id_2);
                        String    name  = getTextByIndex(context, (Integer) arguments[a++]);
                        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)', add item: ('%(name)', '%(value)')", "method", COCPIndices.methods[method_index], "name", name, "value", bt_id_2); }
                        bts.append(context, new Pair_BuildText_String_(context, bt2, name));
                    }
                    ((BuildTextComplex_String) bt).setItems(context, bts);
                    break;
                }
                case COCPIndices.BuildText_ComponentType: {
                    String value = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", value); }
                    ((BuildTextComplex_String) bt).setComponentType(context, value);
                    break;
                }
                case COCPIndices.BuildText_Cases: {
                    int size = (Integer) arguments[a++];
                    Vector_Pair_BuildText_String__long_ bts = Factory_Vector_Pair_BuildText_String__long_.construct(context);
                    for (int c=0; c<size; c++) {
                        String    bt_id_2 = getTextByIndex(context, (Integer) arguments[a++]);
                        BuildText bt2     = (BuildText) createItem(context, COCPIndices.COCPItem_BuildText, bt_id_2);
                        String    name  = getTextByIndex(context, (Integer) arguments[a++]);
                        if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)', add case: ('%(name)', '%(value)')", "method", COCPIndices.methods[method_index], "name", name, "value", bt_id_2); }
                        bts.append(context, new Pair_BuildText_String_(context, bt2, name));
                    }
                    ((BuildTextSwitch_String) bt).setCases(context, bts);
                    break;
                }
                case COCPIndices.BuildText_ParameterToDeclare: {
                    String parameter_name = getTextByIndex(context, (Integer) arguments[a++]);
                    String parameter_type = getTextByIndex(context, (Integer) arguments[a++]);
                    Boolean optional      = getCOCPBoolean(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(name)', '%(type)', '%(optional)')", "method", COCPIndices.methods[method_index], "name", parameter_name, "type", parameter_type, "optional", optional); }
                    ((BuildTextBase) bt).addParameterToDeclare(context, parameter_name, parameter_type, optional);
                    break;
                }
                case COCPIndices.BuildText_ScaffoldFactory: {
                    String    sf_id = getTextByIndex(context, (Integer) arguments[a++]);
                    Scaffold  sf    = (Scaffold) createItem(context, COCPIndices.COCPItem_Scaffold, sf_id);
                    BuildTextScaffoldFactory scaffold_factory = new BuildTextScaffoldFactory_Once(context, sf);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' ('%(value)')", "method", COCPIndices.methods[method_index], "value", sf_id); }
                    ((BuildTextBase) bt).setScaffoldFactory(context, scaffold_factory);
                    break;
                }
                case COCPIndices.BuildText_MetaData: {
                    if (meta_data == null) {
                        meta_data = new HashMap<String,Object>();
                    }
                    int    type    = (Integer) arguments[a++];
                    String key     = getTextByIndex(context, (Integer) arguments[a++]);
                    String value   = getTextByIndex(context, (Integer) arguments[a++]);
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)', add meta data: ('%(type)', '%(key)', '%(value)')", "method", COCPIndices.methods[method_index], "type", type, "key", key, "value", value); }
                    switch (type) {
                        case 0:
                            meta_data.put(key, value);
                            break;
                        case 1:
                            meta_data.put(key, (Node) parseMDXML(context, (String) value, false, site_id));
                            break;
                        case 2:
                            meta_data.put(key, (BuildText) parseMDXML(context, (String) value, true, site_id));
                            break;
                        case 3:
                            meta_data.put(key, (JsonNode) parseMDJSON(context, (String) value, false, site_id));
                            break;
                        case 4:
                            meta_data.put(key, (BuildText) parseMDJSON(context, (String) value, true, site_id));
                            break;
                        case 5:
                            meta_data.put(key, (Object) parseMDYAML(context, (String) value, false, site_id));
                            break;
                        case 6:
                            meta_data.put(key, (BuildText) parseMDYAML(context, (String) value, true, site_id));
                            break;
                    }
                    break;
                }
                case COCPIndices.BuildText_PreConditions: {
                    Vector<String[]> pre_conditions = new Vector<String[]>();
                    int size = (Integer) arguments[a++];
                    for (int i=0; i<size; i++) {
                        int parts = (Integer) arguments[a++];
                        String[] pre_condition = new String[parts];
                        for (int p=0; p<parts; p++) {
                            pre_condition[p] = getTextByIndex(context, (Integer) arguments[a++]);
                        }
                        pre_conditions.add(pre_condition);
                    }
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' (...)", "method", COCPIndices.methods[method_index]); }
                    ((BuildTextBaseImpl) bt).setPreConditions(context, pre_conditions);
                    break;
                }
                case COCPIndices.BuildText_PostConditions: {
                    Vector<String[]> post_conditions = new Vector<String[]>();
                    int size = (Integer) arguments[a++];
                    for (int i=0; i<size; i++) {
                        int parts = (Integer) arguments[a++];
                        String[] post_condition = new String[parts];
                        for (int p=0; p<parts; p++) {
                            post_condition[p] = getTextByIndex(context, (Integer) arguments[a++]);
                        }
                        post_conditions.add(post_condition);
                    }
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' (...)", "method", COCPIndices.methods[method_index]); }
                    ((BuildTextBaseImpl) bt).setPostConditions(context, post_conditions);
                    break;
                }
                case COCPIndices.BuildText_PreBuildScripts: {
                    Vector<String[]> pre_build_scripts = new Vector<String[]>();
                    int size = (Integer) arguments[a++];
                    for (int i=0; i<size; i++) {
                        int parts = (Integer) arguments[a++];
                        String[] pre_build_script = new String[parts];
                        for (int p=0; p<parts; p++) {
                            pre_build_script[p] = getTextByIndex(context, (Integer) arguments[a++]);
                        }
                        pre_build_scripts.add(pre_build_script);
                    }
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' (...)", "method", COCPIndices.methods[method_index]); }
                    ((BuildTextBaseImpl) bt).setPreBuildScripts(context, pre_build_scripts);
                    break;
                }
                case COCPIndices.BuildText_PostBuildScripts: {
                    Vector<String[]> post_build_scripts = new Vector<String[]>();
                    int size = (Integer) arguments[a++];
                    for (int i=0; i<size; i++) {
                        int parts = (Integer) arguments[a++];
                        String[] post_build_script = new String[parts];
                        for (int p=0; p<parts; p++) {
                            post_build_script[p] = getTextByIndex(context, (Integer) arguments[a++]);
                        }
                        post_build_scripts.add(post_build_script);
                    }
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "COCP BT Factory: method '%(method)' (...)", "method", COCPIndices.methods[method_index]); }
                    ((BuildTextBaseImpl) bt).setPostBuildScripts(context, post_build_scripts);
                    break;
                }
                default: {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Unhandled method index '%(index)'", "index", method_index);
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
            }
        }

        if (meta_data != null) {
            ((BuildTextBaseImpl) bt).setMetaData(context, meta_data);
        }

        return bt;
    }

    protected Object createS(CallContext context, int... arguments) {
        int a = 2; // skip first two

        int          scaffold_index               = (Integer) arguments[a++];

        String       type_string                  = getTextByIndex(context, (Integer) arguments[a++]);
        Type         type                         = (type_string == null ? null : TypeManager.tryGetById(context, type_string));
        Class        factory_or_retriever_class   = (Class) evaluateCOCPCode(context, (Integer) arguments[a++]);
        Boolean      allow_dynamic_type_check     = getCOCPBoolean(context, (Integer) arguments[a++]);
        String       component_type_string        = getTextByIndex(context, (Integer) arguments[a++]);
        Type         component_type               = (component_type_string == null ? null : TypeManager.tryGetById(context, component_type_string));

        Vector_ScaffoldParameter_long_ parameters = Factory_Vector_ScaffoldParameter_long_.construct(context);
        Integer      parameter_size               = (Integer) arguments[a++];
        for (int p=0; p<parameter_size; p++) {

            int parameter_index = (Integer) arguments[a++];
            boolean dynamic = (parameter_index == COCPIndices.COCPDynamicScaffoldParameter ? true : false);

            String  name                           = getTextByIndex(context, (Integer) arguments[a++]);
            String  sp_type_string                 = getTextByIndex(context, (Integer) arguments[a++]);
            Type    sp_type                        = (sp_type_string == null ? null : TypeManager.tryGetById(context, sp_type_string));
            String  name_template                  = dynamic ? getTextByIndex(context, (Integer) arguments[a++]) : null;
            Boolean is_expression                  = dynamic ? getCOCPBoolean(context, (Integer) arguments[a++]) : null;
            String  if_expression                  = dynamic ? getTextByIndex(context, (Integer) arguments[a++]) : null;
            String  foreach_expression             = dynamic ? getTextByIndex(context, (Integer) arguments[a++]) : null;
            String  variable_definition_expression = dynamic ? getTextByIndex(context, (Integer) arguments[a++]) : null;
            String  source_location_info           = getTextByIndex(context, (Integer) arguments[a++]);
            Integer build_text_index               = (Integer) arguments[a++];
            Integer applies_to_size                = (Integer) arguments[a++];
            Type[]  applies_to                     = new Type[applies_to_size];
            for (int at=0; at<applies_to_size; at++) {
                applies_to[at] = TypeManager.tryGetById(context, getTextByIndex(context, (Integer) arguments[a++]));
            }

            ScaffoldParameter sp = dynamic ?
                  new DynamicScaffoldParameter(context, name, sp_type, name_template, is_expression, if_expression, foreach_expression, variable_definition_expression, source_location_info, build_text_index, applies_to)
                : new ScaffoldParameter(context, name, sp_type, source_location_info, build_text_index, applies_to);

            parameters.append(context, sp);
        }

        FactorySiteListener listener              = (FactorySiteListener) evaluateCOCPCode(context, (Integer) arguments[a++]);
        Boolean      is_singleton                 = getCOCPBoolean(context, (Integer) arguments[a++]);
        Boolean      have_dynamic_parameters      = getCOCPBoolean(context, (Integer) arguments[a++]);
        FactorySite  factory_site                 = (FactorySite) evaluateCOCPCode(context, (Integer) arguments[a++]);
        String       oid                          = getTextByIndex(context, (Integer) arguments[a++]);
        Integer      pass                         = (Integer) arguments[a++];

        String[][][] expressions = new String[4][][];
        Integer      prec_size                    = (Integer) arguments[a++];
        expressions[0] = new String[prec_size][];
        for (int i=0; i<prec_size; i++) {
            expressions[0][i] = new String[3];
            expressions[0][i][0] = getTextByIndex(context, (Integer) arguments[a++]);
            expressions[0][i][1] = getTextByIndex(context, (Integer) arguments[a++]);
            expressions[0][i][2] = getTextByIndex(context, (Integer) arguments[a++]);
        }
        Integer      postc_size                   = (Integer) arguments[a++];
        expressions[1] = new String[postc_size][];
        for (int i=0; i<postc_size; i++) {
            expressions[1][i] = new String[3];
            expressions[1][i][0] = getTextByIndex(context, (Integer) arguments[a++]);
            expressions[1][i][1] = getTextByIndex(context, (Integer) arguments[a++]);
            expressions[1][i][2] = getTextByIndex(context, (Integer) arguments[a++]);
        }
        Integer      prebs_size                   = (Integer) arguments[a++];
        expressions[2] = new String[prebs_size][];
        for (int i=0; i<prebs_size; i++) {
            expressions[2][i] = new String[3];
            expressions[2][i][0] = getTextByIndex(context, (Integer) arguments[a++]);
            expressions[2][i][1] = getTextByIndex(context, (Integer) arguments[a++]);
            expressions[2][i][2] = getTextByIndex(context, (Integer) arguments[a++]);
        }
        Integer      postbs_size                  = (Integer) arguments[a++];
        expressions[3] = new String[postbs_size][];
        for (int i=0; i<postbs_size; i++) {
            expressions[3][i] = new String[3];
            expressions[3][i][0] = getTextByIndex(context, (Integer) arguments[a++]);
            expressions[3][i][1] = getTextByIndex(context, (Integer) arguments[a++]);
            expressions[3][i][2] = getTextByIndex(context, (Integer) arguments[a++]);
        }

        String       source_location_info             = getTextByIndex(context, (Integer) arguments[a++]);
        String       problem_monitor_oid              = getTextByIndex(context, (Integer) arguments[a++]);
        Boolean      has_variable_signature           = getCOCPBoolean(context, (Integer) arguments[a++]);
        Boolean      can_pre_create_or_retrieve       = getCOCPBoolean(context, (Integer) arguments[a++]);
        String       scaffold_id                      = getTextByIndex(context, (Integer) arguments[a++]);
        Boolean      static_create_or_retrieve_method = getCOCPBoolean(context, (Integer) arguments[a++]);
        Integer      cocpa_index                      = (Integer) arguments[a++];
        Object       cocpa                            = evaluateCOCPCode(context, cocpa_index);

        int[][]      index_map = new int[2][];
        Integer      ptbs_size                    = (Integer) arguments[a++];
        index_map[0] = new int[ptbs_size];
        for (int i=0; i<ptbs_size; i++) {
            index_map[0][i] = (Integer) arguments[a++];
        }
        Integer      ptbd_size                    = (Integer) arguments[a++];
        index_map[1] = new int[ptbd_size];
        for (int i=0; i<ptbd_size; i++) {
            index_map[1][i] = (Integer) arguments[a++];
        }

        Boolean      set_parameters_at_once       = getCOCPBoolean(context, (Integer) arguments[a++]);
        Integer      parameters_to_be_set         = (Integer) arguments[a++];
        Integer      parameters_to_be_defaulted   = (Integer) arguments[a++];
        
        switch (scaffold_index) {
            case COCPIndices.COCPScaffold_Factory:
                return new ScaffoldGenericFactoryCOCP(context, type, factory_or_retriever_class, allow_dynamic_type_check, component_type, parameters, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, expressions, source_location_info, problem_monitor_oid, has_variable_signature, can_pre_create_or_retrieve, scaffold_id, static_create_or_retrieve_method, (COCPAccessorFactory) cocpa, index_map, set_parameters_at_once, parameters_to_be_set, parameters_to_be_defaulted);
            case COCPIndices.COCPScaffold_Retriever:
                return new ScaffoldGenericRetrieverCOCP(context, type, factory_or_retriever_class, allow_dynamic_type_check, component_type, parameters, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, expressions, source_location_info, problem_monitor_oid, has_variable_signature, can_pre_create_or_retrieve, scaffold_id, static_create_or_retrieve_method, (COCPAccessorRetriever) cocpa, index_map, set_parameters_at_once, parameters_to_be_set, parameters_to_be_defaulted);
            default:
                return null;
        }
    }

    protected Object parseMDXML(CallContext context, String xml, boolean is_btcx, String site_id) {
        XMLNode xn = null;
        try {
            xn = XMLNode.createXMLNode(context, xml, "COCP:" + site_id);
        } catch (InvalidXML ix) {
            CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, ix, "Previously serialized XML in COCP could not be reparsed");
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
        }
        if (is_btcx) {
            try {
                return (BuildText) ((new BuildTextXMLFactory(context, xn, null, null)).getBuildText(context));
            } catch (InvalidDocument id) {
                CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, id, "Previously serialized XML in COCP could not be reconverted to BuildText");
                throw (ExceptionAssertionProvedFalse) null; // compiler insists
            }
        } else {
            return (Node) (xn.getDOMNodes(context).get(0));
        }
    }

    protected Object parseMDJSON(CallContext context, String json, boolean is_btcj, String site_id) {
        JSONNode jn = null;
        try {
            jn = JSONNode.createJSONNode(context, json /* , "COCP:" + site_id */);
        } catch (InvalidJSON ij) {
            CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, ij, "Previously serialized JSON in COCP could not be reparsed");
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
        }
        if (is_btcj) {
            try {
                return (BuildText) ((new BuildTextJSONFactory(context, jn, null, null)).getBuildText(context));
            } catch (InvalidDocument id) {
                CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, id, "Previously serialized JSON in COCP could not be reconverted to BuildText");
                throw (ExceptionAssertionProvedFalse) null; // compiler insists
            }
        } else {
            return jn.getFirstNode(context);
        }
    }

    protected Object parseMDYAML(CallContext context, String yaml, boolean is_btcy, String site_id) {
        YAMLNode yn = null;
        try {
            yn = YAMLNode.createYAMLNode(context, yaml /* , "COCP:" + site_id */);
        } catch (InvalidYAML iy) {
            CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, iy, "Previously serialized YAML in COCP could not be reparsed");
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
        }
        if (is_btcy) {
            try {
                return (BuildText) ((new BuildTextYAMLFactory(context, yn, null, null)).getBuildText(context));
            } catch (InvalidDocument id) {
                CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, id, "Previously serialized YAML in COCP could not be reconverted to BuildText");
                throw (ExceptionAssertionProvedFalse) null; // compiler insists
            }
        } else {
            return yn.getFirstNode(context);
        }
    }
}
