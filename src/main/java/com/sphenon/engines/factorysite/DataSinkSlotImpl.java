package com.sphenon.engines.factorysite;

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
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class DataSinkSlotImpl
    implements DataSinkSlot
{
  private DataSink data_sink;
  private Type type;

  public DataSinkSlotImpl(CallContext context, Type type) {
    this.type = type;
    this.data_sink = null;
  }

  public boolean isValid(CallContext context) {
    return (this.data_sink != null);
  }

  public void setDataSink(CallContext context, DataSink data_sink) throws TypeMismatch {
    this.data_sink = data_sink;
    if (this.data_sink == null) return;

    if (! this.type.isA(context, data_sink.getType(context))) {
      TypeMismatch.createAndThrow(context, FactorySiteStringPool.get(context, "0.3.1" /* DataSinkSlot of type '%(slottype)' rejects DataSink of type '%(type)' */), "slottype", this.type.getName(context), "type", data_sink.getType(context).getName(context));
    }
  }

  public DataSink getDataSink(CallContext call_context) {
    if (this.data_sink == null) {
      Context context = Context.create(call_context);
      CustomaryContext cc = CustomaryContext.create(context);
      cc.throwProtocolViolation(context, FactorySiteStringPool.get(context, "0.3.0" /* DataSinkSlot contains no valid DataSink yet (null pointer) */));
    }
    return this.data_sink;
  }
}
