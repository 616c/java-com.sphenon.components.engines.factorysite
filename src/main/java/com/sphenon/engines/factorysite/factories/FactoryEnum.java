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

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import com.sphenon.basics.validation.returncodes.*;

public class FactoryEnum {

    protected Class enumclass;
    protected String value;

    public FactoryEnum (CallContext context) {
    }

    public Object create (CallContext context) throws ValidationFailure {
        Object[] ecs = enumclass.getEnumConstants();
        for (Object ec : ecs) {
            Enum e = (Enum) ec;
            if (e.name().equals(value)) {
                return e;
            }
        }

        ValidationFailure.createAndThrow(context, "The name '%(value)' is not a valid enumeration constant of '%(enum)'", "value", this.value, "enum", enumclass.getName());
        throw (ValidationFailure) null;
    }

    public void setValue(CallContext call_context, String value) {
        this.value = value;
    }

    public void attachEnumClass(CallContext context, Class enumclass) {
        this.enumclass = enumclass;
    }
}
