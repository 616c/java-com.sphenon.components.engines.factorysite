package com.sphenon.engines.factorysite.yaml;

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

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class BuildTextYAMLDatainfo {

    public BuildTextYAMLDatainfo () {
    }

    protected String o_id_ref;

    public String getOIdRef () {
        return this.o_id_ref;
    }

    public void setOIdRef (String o_id_ref) {
        this.o_id_ref = o_id_ref;
    }

    protected String id_ref;

    public String getIdRef () {
        return this.id_ref;
    }

    public void setIdRef (String id_ref) {
        this.id_ref = id_ref;
    }

    protected String parameter;

    public String getParameter () {
        return this.parameter;
    }

    public void setParameter (String parameter) {
        this.parameter = parameter;
    }

    protected String j_null;

    public String getNull () {
        return this.j_null;
    }

    public void setNull (String j_null) {
        this.j_null = j_null;
    }

    protected String expression;

    public String getExpression () {
        return this.expression;
    }

    public void setExpression (String expression) {
        this.expression = expression;
    }

    protected String expression_type;

    public String getExpressionType () {
        return this.expression_type;
    }

    public void setExpressionType (String expression_type) {
        this.expression_type = expression_type;
    }

    protected String locator;

    public String getLocator () {
        return this.locator;
    }

    public void setLocator (String locator) {
        this.locator = locator;
    }

    protected String locator_base;

    public String getLocatorBase () {
        return this.locator_base;
    }

    public void setLocatorBase (String locator_base) {
        this.locator_base = locator_base;
    }

    protected String locator_base_o_id_ref;

    public String getLocatorBaseOIdRef () {
        return this.locator_base_o_id_ref;
    }

    public void setLocatorBaseOIdRef (String locator_base_o_id_ref) {
        this.locator_base_o_id_ref = locator_base_o_id_ref;
    }

    protected String locator_base_parameter;

    public String getLocatorBaseParameter () {
        return this.locator_base_parameter;
    }

    public void setLocatorBaseParameter (String locator_base_parameter) {
        this.locator_base_parameter = locator_base_parameter;
    }
}
