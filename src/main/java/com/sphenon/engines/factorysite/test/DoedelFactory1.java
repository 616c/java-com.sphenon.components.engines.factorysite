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

public class DoedelFactory1 {
    public DoedelFactory1 (CallContext context) {
        NotificationContext.sendTrace(context, Notifier.CHECKPOINT, "DOEDEL FACTORY 1: new");
    }

    public Doedel create(CallContext context) {
        NotificationContext.sendTrace(context, Notifier.CHECKPOINT, "DOEDEL FACTORY 1: create");
        return new Doedel(context);
    }
}
