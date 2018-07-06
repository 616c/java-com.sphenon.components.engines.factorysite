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
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.returncodes.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.util.Vector;

public interface SpecificScaffoldFactory {
    // please see also comment in ScaffoldFactory where 'isMatching' is called
    public MatchResult isMatching (CallContext context, Type actual_matched_type, Vector_ScaffoldParameter_long_ parameters, Map_TypeOrNull_String_ parameters_by_name, boolean allow_missing_arguments) throws InvalidFactory, InvalidRetriever, InvalidClass;
    public Scaffold create (CallContext context, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]>  pre_build_scripts, Vector<String[]>  post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidFactory, InvalidRetriever, InvalidClass;

    public String getTypeContext(CallContext context);
    public String getBuildString(CallContext context) throws InvalidFactory, InvalidRetriever, InvalidClass;
    public Vector<ParEntry> getFormalScaffoldParameters (CallContext context);
    public Type getComponentTypeOfCollection(CallContext context);
}
