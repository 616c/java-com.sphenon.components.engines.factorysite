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
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class DataSourceConnector_Id
    extends DataSourceConnector_Coverable
{
    public DataSourceConnector_Id (CallContext context, Type type, String id, String source_location_info) {
        super(context, type, source_location_info);
        this.id = id;
        this.parameter_enabled = true;
    }

    public DataSourceConnector_Id (CallContext context, Type type, String id, DataSourceConnector higher_ranking, boolean is_optional, String source_location_info) {
        super(context, type, higher_ranking, source_location_info);
        this.id = id;
        this.parameter_enabled = true;
        this.is_optional = is_optional;
    }

    protected String id;

    public String getId(CallContext context) {
        return this.id;
    }

    protected boolean parameter_enabled;

    public boolean getParameterEnabled (CallContext context) {
        return this.parameter_enabled;
    }

    public void setParameterEnabled (CallContext context, boolean parameter_enabled) {
        this.parameter_enabled = parameter_enabled;
    }

    protected boolean is_optional;

    public boolean isOptional (CallContext context) {
        return this.is_optional;
    }

    public void setIsOptional (CallContext context, boolean is_optional) {
        this.is_optional = is_optional;
    }
}
