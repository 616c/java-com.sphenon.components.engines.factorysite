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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.monitoring.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

public interface Scaffold
  extends DataSource
{
    public Vector_ScaffoldParameter_long_ getParameters (CallContext context);
    public Type getType (CallContext context);
    public Object getValueAsObject (CallContext context) throws DataSourceUnavailable, IgnoreErraneousDataSource;
//     public void compile(CallContext context, java.io.PrintStream code, String var_prefix, String indent, Vector_String_long_ pars, boolean do_not_append_par) throws DataSourceUnavailable;
    public boolean hasVariableSignature(CallContext context);
    public Type getComponentType (CallContext context);
    public void reset(CallContext context);
    public FactorySite getFactorySite(CallContext context);
    public String getOID(CallContext context);
    public int getPass(CallContext context);
    public ProblemMonitor getLocalProblemMonitor(CallContext context);
    public void skip(CallContext context);
    public boolean isSkipped(CallContext context);
}
