package com.sphenon.engines.factorysite.factories;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class Factory_Map {

    private String[] names;
    private Object[] values;

    public java.util.Map create (CallContext context) {
        java.util.Map table = new java.util.HashMap();
        for (int i=0; i<names.length; i++) {
            table.put(names[i], values[i]);
        }
        return table;
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
        if (ps == null || ps.getSize(context) != 2) { return null; }
        Object pk = ps.tryGet(context, 0);
        Object pv = ps.tryGet(context, 1);
        if ((pv instanceof JavaType) || (pv instanceof TypeParametrised)) {
            return (Type) pv;
        }
        return null;
    }
}
