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

import java.util.Vector;

public class GenericsTest {

    public GenericsTest (CallContext call_context) {
    }

    protected Vector<String> vs;

    public Vector<String> getVS (CallContext context) {
        return this.vs;
    }

    public void setVS (CallContext context, Vector<String> vs) {
        this.vs = vs;
    }

    protected Vector<Vector<String>> vvs;

    public Vector<Vector<String>> getVVS (CallContext context) {
        return this.vvs;
    }

    public void setVVS (CallContext context, Vector<Vector<String>> vvs) {
        this.vvs = vvs;
    }
}