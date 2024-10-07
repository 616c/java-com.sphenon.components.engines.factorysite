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

import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.message.*;

public class MatchResult {
    public Type                    actual_matched_type;
    public Vector_ParEntry_long_   parameters_to_be_set;
    public Vector_ParEntry_long_   parameters_to_be_defaulted;
    public boolean                 successful;
    public MessageText             message_text;
    public SpecificScaffoldFactory specific_scaffold_factory;

    public MatchResult (Type actual_matched_type, Vector_ParEntry_long_ parameters_to_be_set, Vector_ParEntry_long_ parameters_to_be_defaulted) {
        this.actual_matched_type        = actual_matched_type;
        this.parameters_to_be_set       = parameters_to_be_set;
        this.parameters_to_be_defaulted = parameters_to_be_defaulted;
        this.successful                 = true;
    }

    public MatchResult (Type actual_matched_type, String[] parameter_names) {
        this.actual_matched_type        = actual_matched_type;
        this.parameters_to_be_set       = null;
        this.parameters_to_be_defaulted = null;
        this.successful                 = true;
    }

    public MatchResult (Type actual_matched_type) {
        this.actual_matched_type        = actual_matched_type;
        this.parameters_to_be_set       = null;
        this.parameters_to_be_defaulted = null;
        this.successful                 = true;
    }

    public MatchResult (MessageText message_text, SpecificScaffoldFactory specific_scaffold_factory) {
        this.message_text               = message_text;
        this.specific_scaffold_factory  = specific_scaffold_factory;
        this.successful                 = false;
    }
}
