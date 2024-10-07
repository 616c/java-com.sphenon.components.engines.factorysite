package com.sphenon.engines.factorysite.test;

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
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.engines.factorysite.*;

public class Willy
{
    public Willy (CallContext call_context, String s1, String s2, String s3) {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        cc.sendTrace(context, Notifier.CHECKPOINT, FactorySiteStringPool.get(context, "2.4.0" /* Hey, a new Willy(context, %(s1), %(s2), %(s3)) is born! %(willy) */), "s1", s1, "s2", s2, "s3", s3, "willy", this);
        hallo = s3;
    }

    protected String hallo;

    public String getHallo (CallContext context) {
        return this.hallo;
    }
}
