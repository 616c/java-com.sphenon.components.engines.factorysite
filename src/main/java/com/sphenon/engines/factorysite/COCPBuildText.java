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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.Vector;

public interface COCPBuildText {

    public class Parameter {
        public String  name;
        public Type    type;
        public boolean optional;
        public Parameter(CallContext context, String name, String type_id, boolean optional) {
            this.name     = name;
            this.type     = TypeManager.tryGetById(context, type_id);
            this.optional = optional;
        }
    }

    public boolean isExpanded (CallContext context);
    public void setIsExpanded (CallContext context, boolean is_expanded);

    public BuildTextScaffoldFactory getScaffoldFactory (CallContext context);
    public void setScaffoldFactory (CallContext context, BuildTextScaffoldFactory scaffold_factory);

    public Vector<Parameter> getParametersToDeclare (CallContext context);
    public void setParametersToDeclare (CallContext context, Vector<Parameter> parameters_to_declare);
    public void addParameterToDeclare (CallContext context, String name, String type_id, boolean optional);
}
