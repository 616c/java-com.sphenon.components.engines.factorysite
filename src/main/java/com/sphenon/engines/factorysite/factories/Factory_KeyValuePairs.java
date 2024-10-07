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

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.classes.*;

import java.util.Vector;

public class Factory_KeyValuePairs {

    private String[] names;
    private String[] values;

    protected KeyValuePairs instance;

    public KeyValuePairs precreate (CallContext context) {
        return this.instance = new KeyValuePairs(context);
    }

    public KeyValuePairs create (CallContext context) {
        KeyValuePairs result = (this.instance != null ? this.instance : new KeyValuePairs(context));
        this.instance = null;
        Vector<String[]> pairs = new Vector<String[]>();
        for (int i=0; i<names.length; i++) {
            String[] pair = new String[2];
            pair[0] = names[i];
            pair[1] = values[i];
            pairs.add(pair);
        }
        result.setPairs(context, pairs);
        return result;
    }

    public void set_ParametersAtOnce(CallContext call_context, String[] names, String[] values) {
        if (names.length != values.length) {
            Context context = Context.create(call_context);
            CustomaryContext cc = CustomaryContext.create(context);
            cc.throwPreConditionViolation(context, FactorySiteStringPool.get(context, "1.1.0" /* Number of names differs from number of values */));
        }
        this.names = names;
        this.values = values;
    }
}
