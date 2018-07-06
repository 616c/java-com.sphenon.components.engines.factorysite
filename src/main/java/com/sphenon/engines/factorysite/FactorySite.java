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
import com.sphenon.basics.expression.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.util.Vector;
import java.util.HashMap;

public interface FactorySite 
{
    // public void resetParameters(CallContext context);
    // public void setParameter(CallContext context, String name, DataSource ds) throws DoesNotExist, TypeMismatch;
    // public void setOutParameter(CallContext context, String name, DataSourceSlot dss) throws DoesNotExist, TypeMismatch;
    public void setStaticParameter(CallContext context, String name, Object object) throws DoesNotExist, TypeMismatch;
    public void setOptionalStaticParameter(CallContext context, String name, Object object) throws TypeMismatch;

    public Object build (CallContext context, java.util.Map parameters) throws BuildFailure;
    public Object build (CallContext context, java.util.Map parameters, int interruption_pass) throws BuildFailure;
    public Object prebuild (CallContext context) throws BuildFailure;
    public boolean performPasses(CallContext context, int interruption_pass) throws BuildFailure;
//     public void compile (CallContext context, java.io.OutputStream javaout, String classname, String subpackage) throws CompileFailure;
    public ValidationFailure validateParameters (CallContext context, java.util.Map parameters);
    public DataSource getMainDataSource (CallContext context);
    public Map_DataSourceConnector_String_ getParameters (CallContext call_context);

    public String getSiteId(CallContext context);
    public DataSource getDataSourceById(CallContext context, String oid);

    // maintained for the sake of dynamic expression evaluation
    public Scope getCurrentScope(CallContext context) throws DataSourceUnavailable;
    public Scope getCurrentScopeOverride(CallContext context);
    public void setCurrentScopeOverride(CallContext context, Scope scope);

    public int getCurrentPass(CallContext context);

    public void addPostponedPreCondition(CallContext context, BuildAssertion build_assertion);
    public void addPostponedPostCondition(CallContext context, BuildAssertion build_assertion);
    public void addPostponedPreBuildScript(CallContext context, BuildScript build_script);
    public void addPostponedPostBuildScript(CallContext context, BuildScript build_script);

    public long getLastModification (CallContext context);

    public Vector_String_long_ getRootArguments (CallContext context);

    public String getDefaultEvaluator (CallContext context);

    public HashMap<String,Vector<Pair<String,Object>>> getMetaData (CallContext context);
    public Vector<Pair<String,Object>> getMetaData (CallContext context, String key);
}
