package com.sphenon.engines.factorysite.factories;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class Factory_Hashtable {

    private String[] names;
    private Object[] values;

    protected java.util.Hashtable instance;

    public java.util.Hashtable precreate (CallContext context) {
        return this.instance = new java.util.Hashtable();
    }

    public java.util.Hashtable create (CallContext context) {
        java.util.Hashtable table = (this.instance != null ? this.instance : new java.util.Hashtable());
        this.instance = null;
        for (int i=0; i<names.length; i++) {
            if (values[i] == null) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Value of '%(name)' in Factory_Hashtable is null", "name", names[i]);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            } else {
                table.put(names[i], values[i]);
            }
        }
        return table;
    }

    public void set_ParametersAtOnce(CallContext context, String[] names, Object[] values) {
        if (names.length != values.length) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, FactorySiteStringPool.get(context, "1.1.0" /* Number of names differs from number of values */));
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        this.names = names;
        this.values = values;
    }

    static public Type get_GenericComponentTypeMethod(CallContext context, Type type) {
        if ((type instanceof TypeParametrised) == false) { return null; }
        Vector_Object_long_ ps = ((TypeParametrised) type).getParameters(context);
        if (ps == null || ps.getSize(context) != 2) { return null; }
        Object pk = ps.tryGet(context, 0);
        Object pv = ps.tryGet(context, 1);
        if ((pv instanceof JavaType) || (pv instanceof TypeParametrised)) {
            return (Type) pv;
        }
        return null;
    }
}
