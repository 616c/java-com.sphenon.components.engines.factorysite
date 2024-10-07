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

import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.metadata.exceptions.*;
import com.sphenon.basics.metadata.tplinst.*;
import com.sphenon.basics.validation.returncodes.ValidationFailure;

public class SpecificTypeFactory_Aggregate implements SpecificTypeFactory {

    public SpecificTypeFactory_Aggregate (CallContext context) {
    }

    public Type tryCreation (CallContext context, String type_name) throws ValidationFailure {
        if ( ! type_name.matches("^Aggregate::.*")) {
            return null;
        }

        String aggregate_class = type_name.substring(11);
        try {
            return new TypeImpl_Aggregate(context, aggregate_class);
        } catch (NoSuchClass nsc) {
            ValidationFailure.createAndThrow(context, nsc, "aggregate creation failed");
            throw (ValidationFailure) null;
        }
    }

}
