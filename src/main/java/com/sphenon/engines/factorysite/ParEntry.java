package com.sphenon.engines.factorysite;

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
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.returncodes.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.lang.reflect.*;

public class ParEntry {
    public Method set_method;
    public Method default_method;
    public Type   type;
    public String name;
    public boolean is_optional;
    public boolean set_method_has_context;
    public boolean default_method_has_context;
    public int     index;

    public ParEntry() {
        this.set_method = null;
        this.default_method = null;
        this.type = null;
        this.name = null;
        this.is_optional = false;
        this.set_method_has_context = false;
        this.default_method_has_context = false;
    }

    public ParEntry(Type type, String name, boolean is_optional) {
        this.type = type;
        this.name = name;
        this.is_optional = is_optional;
    }
}

