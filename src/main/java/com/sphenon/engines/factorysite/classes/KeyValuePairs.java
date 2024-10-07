package com.sphenon.engines.factorysite.classes;

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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.encoding.*;

import com.sphenon.engines.factorysite.*;

import java.util.Vector;

public class KeyValuePairs {

    public KeyValuePairs (CallContext context) {
    }

    protected Vector<String[]> pairs;

    public Vector<String[]> getPairs (CallContext context) {
        return this.pairs;
    }

    public void setPairs (CallContext context, Vector<String[]> pairs) {
        this.pairs = pairs;
    }

    public String dumpToJavaStringList(CallContext context) {
        String result = "";
        String deli = "";
        if (pairs != null) {
            for (String[] pair : pairs) {
                result += deli + "\"" + Encoding.recode(context, pair[0], Encoding.UTF8, Encoding.JAVA) + "\", ";
                if (    pair[1].length() > 1
                     && pair[1].charAt(0) == '"'
                   ) {
                    result += pair[1];
                } else {
                    result += "\"" + Encoding.recode(context, pair[1], Encoding.UTF8, Encoding.JAVA) + "\"";
                }
                deli = ", ";
            }
        }
        return result;
    }
}
