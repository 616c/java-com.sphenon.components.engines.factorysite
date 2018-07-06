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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.util.Vector;

public interface COCPAccessorRetriever {
    public Object createRetrieverInstance(CallContext context, ScaffoldGenericRetrieverCOCP sgrcocp);
    public Object preretrieveInstance(CallContext context, ScaffoldGenericRetrieverCOCP sgrcocp);
    public Object retrieveInstance(CallContext context, ScaffoldGenericRetrieverCOCP sgrcocp);
    public void setParametersAtOnce(CallContext context, ScaffoldGenericRetrieverCOCP sgrcocp, String[] names, Object values);
    public Object createParametersAtOnceArray(CallContext context, ScaffoldGenericRetrieverCOCP sgrcocp, Vector values_vector);
    public boolean hasRetrieverDefaultValue(CallContext context, ScaffoldGenericRetrieverCOCP sgrcocp, int member);
    public Object getRetrieverDefaultValue(CallContext context, ScaffoldGenericRetrieverCOCP sgrcocp, int member);   
    public void setRetrieverValue(CallContext context, ScaffoldGenericRetrieverCOCP sgrcocp, int member, Object value);
    public void setComponentType(CallContext context, ScaffoldGenericRetrieverCOCP sgrcocp, Type component_type);
}
