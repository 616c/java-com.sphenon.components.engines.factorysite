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
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.Map;
import java.util.List;

public class GenericRetriever_Map implements GenericRetriever {
    static public class Factory implements GenericRetriever.Factory {
        protected List<Type> handled_types;
        public Factory(CallContext context, List<Type> handled_types) {
            this.handled_types = handled_types;
        }
        public List<Type> getHandledTypes(CallContext context) {
            return this.handled_types;
        }
        public Type getRetrieverType(CallContext context, Type type) {
            return TypeManager.getParametrised(context, GenericRetriever_Map.class, type);
        }
    }
}
