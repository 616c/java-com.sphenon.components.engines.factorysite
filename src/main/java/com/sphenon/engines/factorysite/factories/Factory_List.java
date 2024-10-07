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

public class Factory_List {

    private String[] names;
    private Object[] values;

    protected java.util.List instance;

    public java.util.List precreate (CallContext context) {
        return this.instance = new java.util.ArrayList();
    }

    public java.util.List create (CallContext context) {
        java.util.List list = (this.instance != null ? this.instance : new java.util.ArrayList());
        this.instance = null;
        for (int i=0; i<values.length; i++) {
            list.add(values[i]);
        }
        return list;
    }

    public void set_ParametersAtOnce(CallContext call_context, String[] names, Object[] values) {
        if (names.length != values.length) {
            Context context = Context.create(call_context);
            CustomaryContext cc = CustomaryContext.create(context);
            cc.throwPreConditionViolation(context, FactorySiteStringPool.get(context, "1.1.0" /* Number of names differs from number of values */));
        }
        this.names = names;
        this.values = values;
    }

    static public Type get_GenericComponentTypeMethod(CallContext context, Type type) {
        if ((type instanceof TypeParametrised) == false) { return null; }
        Vector_Object_long_ ps = ((TypeParametrised) type).getParameters(context);
        if (ps == null || ps.getSize(context) != 1) { return null; }
        Object p = ps.tryGet(context, 0);
        if ((p instanceof JavaType) || (p instanceof TypeParametrised)) {
            return (Type) p;
        }
        return null;
    }
}
