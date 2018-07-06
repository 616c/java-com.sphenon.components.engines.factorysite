package com.sphenon.engines.factorysite.json;

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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * A preprocessor for a JSON OCP input source.
 * An implementation of this class needs to be registered at the BuildTextJSONFactory.
 */

public interface BuildTextJSONPreprocessor {
    // /**
    //  * For each OCP file to process, this method is called on this preprocessor
    //  * instance, if it's registered.
    //  * If it is successful, it may return a document, which is used by the
    //  * FactorySite for further processing (building the scaffold network).
    //  * If it returns null, a standard method for parsing the input_source
    //  * is used (based on DOMParser).
    //  */
    // public Document getDocument(CallContext context, JSONInputSource input_source);
}
