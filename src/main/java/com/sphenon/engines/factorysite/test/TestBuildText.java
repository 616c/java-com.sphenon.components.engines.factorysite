package com.sphenon.engines.factorysite.test;

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
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.tplinst.*;

public class TestBuildText {

    static public BuildText get (CallContext context) {
        Vector_Pair_BuildText_String__long_ named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);

        named_items.append(context, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "", "", "", "wauwau", "<Testkram>"), "Hund1"));
        named_items.append(context, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "", "", "", "wuffwuff", "<Testkram>"), "Hund2"));
        
        return new BuildTextComplex_String(context, "", "", "java.util.Hashtable", "", "", "<Testkram>", named_items);
    }
}
