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

import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import org.w3c.dom.Node;

abstract public class COCPBuildTextFactory extends COCPBuildTextBase {
    static final public Class _class = COCPBuildTextFactory.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    public COCPBuildTextFactory(CallContext context, String xmlns, String ocpid, String polymorphic, String base_aggregate) {
        super(context, xmlns, ocpid, polymorphic, base_aggregate);
    }

    protected Object createFactoryInstance(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp) {
        return null;
    }

    protected Object precreateInstance(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp) {
        return null;
    }

    protected Object createInstance(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp) {
        return null;
    }

    protected void setParametersAtOnce(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp, String[] names, Object values) {
    }

    protected Object createParametersAtOnceArray(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp, Vector values_vector) {
        return null;
    }

    protected boolean hasFactoryDefaultValue(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp, int index, boolean to_be_defaulted) {
        return false;
    }

    protected Object getFactoryDefaultValue(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp, int index, boolean to_be_defaulted) {
        return null;
    }

    protected void setFactoryValue(CallContext context, ScaffoldGenericFactoryCOCP sgfcocp, int index, Object value, boolean to_be_defaulted) {
    }
}
