// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/Factory_Vector.javatpl

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
// please do not modify this file directly
package com.sphenon.engines.factorysite.tplinst;

import com.sphenon.basics.many.*;
import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.xml.*;
import com.sphenon.engines.factorysite.json.*;
import java.lang.reflect.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.tplinst.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;

public class Factory_Vector_Pair_BuildText_String__long_
{
    private String[] names;
    private Pair_BuildText_String_[] values;

    static public Vector_Pair_BuildText_String__long_ construct (CallContext context) {
        return (new Factory_Vector_Pair_BuildText_String__long_(context)).create(context);
    }

    public Factory_Vector_Pair_BuildText_String__long_ (CallContext context) {
    }

    public Factory_Vector_Pair_BuildText_String__long_ () {
    }

    protected Vector_Pair_BuildText_String__long_ instance;

    public Vector_Pair_BuildText_String__long_ precreate (CallContext context) {
        return this.instance = VectorImpl_Pair_BuildText_String__long_.create(context);
    }

    public Vector_Pair_BuildText_String__long_ create (CallContext context) {
        Vector_Pair_BuildText_String__long_ vector = (this.instance != null ? this.instance : VectorImpl_Pair_BuildText_String__long_.create(context));
        this.instance = null;
        if (names != null && values != null) {
            for (int i=0; i<names.length; i++) {
                vector.set(context, i, values[i]); // naja, eigentlich "add" statt "set"
            }
        }
        return vector;
    }

    public void set_ParametersAtOnce(CallContext call_context, String[] names, Pair_BuildText_String_[] values) {
        if (names.length != values.length) {
            Context context = Context.create(call_context);
            CustomaryContext cc = CustomaryContext.create(context);
            cc.throwPreConditionViolation(context, ManyStringPool.get(context, "0.5.0" /* Number of names differs from number of values */));
        }
        this.names = names;
        this.values = values;
    }
}
