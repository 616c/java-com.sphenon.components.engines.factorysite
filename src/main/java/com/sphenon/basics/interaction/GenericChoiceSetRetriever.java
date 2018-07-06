package com.sphenon.basics.interaction;

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
import com.sphenon.basics.variatives.*;
import com.sphenon.basics.variatives.classes.*;
import com.sphenon.basics.retriever.*;
import com.sphenon.basics.retriever.returncodes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.factories.*;
import com.sphenon.basics.metadata.*;

public class GenericChoiceSetRetriever implements Retriever {

    public GenericChoiceSetRetriever (CallContext context) {
    }

    public Object retrieveObject (CallContext context) throws RetrievalFailure {
        CustomaryContext.create((Context)context).throwPreConditionViolation(context, "ChoiceSet retriever does not return single instance");
        throw (ExceptionPreConditionViolation) null; // compiler insists
    }

    public Object retrieveObjects (CallContext context) throws RetrievalFailure {
        if (target_type != null && target_type instanceof TypeImpl) {
            Class target_class = ((TypeImpl) target_type).getJavaClass(context);
            GenericVector gv = Factory_GenericVector.construct(context, target_class);
            if (target_class.isEnum()) {
                if (target_class != null && target_class.isEnum()) {
                    Object[] eccs = target_class.getEnumConstants();
                    for (Object ecc : eccs) {
                        Enum e = (Enum) ecc;
                        gv.append(context, e); // e.name()
                    }
                }
            }
            return gv;
        } else {
            return null;
        }
    }

    protected Type target_type;

    public Type getTargetType (CallContext context) {
        return this.target_type;
    }

    public void setTargetType (CallContext context, Type target_type) {
        this.target_type = target_type;
    }
}
