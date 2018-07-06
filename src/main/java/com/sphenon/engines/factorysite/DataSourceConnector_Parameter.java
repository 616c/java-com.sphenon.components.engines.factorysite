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
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class DataSourceConnector_Parameter
    extends DataSourceConnector_Coverable
{
    protected String name;
    protected boolean is_optional;
    protected boolean is_static;

    public boolean isOptional (CallContext context) {
        return this.is_optional;
    }

    public void setIsOptional (CallContext context, boolean is_optional) {
        this.is_optional = is_optional;
    }

    public boolean isStatic (CallContext context) {
        return this.is_static;
    }

    public void setIsStatic (CallContext context, boolean is_static) {
        this.is_static = is_static;
    }

    public String getName(CallContext context) {
        return this.name;
    }

    public DataSourceConnector_Parameter (CallContext context, Type type, String name, String source_location_info) {
        super(context, type, source_location_info);
        this.name = name;
        this.is_optional = false;
        this.is_static = false;
    }

    public DataSourceConnector_Parameter (CallContext context, Type type, String name, DataSourceConnector higher_ranking, String source_location_info) {
        super(context, type, higher_ranking, source_location_info);
        this.name = name;
        this.is_optional = false;
        this.is_static = false;
    }

    public DataSourceConnector_Parameter (CallContext context, Type type, String name, DataSourceConnector higher_ranking, boolean is_optional, boolean is_static, String source_location_info) {
        super(context, type, source_location_info);
        this.name = name;
        this.is_optional = is_optional;
        this.is_static = is_static;
        this.higher_ranking = higher_ranking;
    }

    public void setDataSource(CallContext context, DataSource data_source) throws TypeMismatch {
        if (this.is_static && this.isValid(context)) {
            CustomaryContext.create(Context.create(context)).throwProtocolViolation(context, "Static parameter '%(name)' of factory site must not be set twice", "name", this.name);
            throw (ExceptionProtocolViolation) null; // compiler insists
        }
        super.setDataSource(context, data_source);
    }

    public void compile(CallContext context, java.io.PrintStream code, String var_prefix, String indent, Vector_String_long_ pars, boolean do_not_append_par) throws DataSourceUnavailable {
        if (! do_not_append_par) {
            pars.append(context, (this.getType(context) == null ? "Object" : ((TypeImpl) (this.getType(context))).getJavaClassName(context)) + " " + var_prefix + "_o = null;");
        }
        code.println(indent + "        try {");
        code.println(indent + "            " + var_prefix + "_o = " + (this.getType(context) == null ? "" : "(" + ((TypeImpl) (this.getType(context))).getJavaClassName(context) + ")") + " parameters.get(\"" + this.name + "\");");
        code.println(indent + "        } catch (Throwable t) { handle(context, t, \"" + (this.getType(context) == null ? "Object" : ((TypeImpl) (this.getType(context))).getJavaClassName(context)) + "\", \"parameter\"); }");
    }
}
