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

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.Vector;

public class Factory_Vector_ByVectorString {

    public java.util.Vector create (CallContext context) {
        if (vector instanceof VectorImpl_String_long_) {
            return ((VectorImpl_String_long_)vector).getImplementationVector(context);
        }
        Vector result = new Vector();
        for (String string : vector.getIterable_String_(context)) {
            result.add(string);
        }
        return result;
    }

    protected Vector_String_long_ vector;

    public Vector_String_long_ getVector (CallContext context) {
        return this.vector;
    }

    public void setVector (CallContext context, Vector_String_long_ vector) {
        this.vector = vector;
    }
}
