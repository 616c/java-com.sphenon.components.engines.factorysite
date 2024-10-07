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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.returncodes.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Vector;

public class BuildAssertion {
    public BuildAssertion(CallContext context, String expression_text, String pass, String source_code_location, AssertionType assertion_type, Scaffold scaffold) {
        this.expression = (expression_text == null || expression_text.length() == 0 ? null : new Expression(context, expression_text, "jspp"));
        this.pass = (pass == null ? 1 : Integer.parseInt(pass));
        this.source_code_location = source_code_location;
        this.assertion_type = assertion_type;
        this.scaffold = scaffold;
    }

    static public enum AssertionType { PRE, POST, ASSERT };

    protected AssertionType assertion_type;

    public AssertionType getAssertionType (CallContext context) {
        return this.assertion_type;
    }

    public void setAssertionType (CallContext context, AssertionType assertion_type) {
        this.assertion_type = assertion_type;
    }

    protected Scope scope;

    public Scope getScope (CallContext context) {
        return this.scope;
    }

    public void setScope (CallContext context, Scope scope) {
        this.scope = scope;
    }

    public void check(CallContext context, int current_pass) {
        if (current_pass != this.pass) { return; }
        Object condo = null;
        try {
            condo = this.expression.evaluate(context, scope);
        } catch (EvaluationFailure ef) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of expression '%(expression)' failed", "expression", this.expression.getExpression(context));
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        Boolean cond;
        try {
            cond = (Boolean) condo;
        } catch (ClassCastException cce) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, cce, "Pre condition expression in OCP at '%(location)' does not evaluate to a Boolean instance, but to a '%(got)'", "got", condo.getClass(), "location", this.source_code_location);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        if (cond == false) {
            switch (this.assertion_type) {
                case PRE:
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Pre condition violation in OCP at '%(location)', '%(expression)'", "expression", this.expression.getExpression(context), "location", this.source_code_location);
                case POST:
                    CustomaryContext.create((Context)context).throwPostConditionViolation(context, "Post condition violation in OCP at '%(location)', '%(expression)'", "expression", this.expression.getExpression(context), "location", this.source_code_location);
                case ASSERT:
                    CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, "Assertion proved false in OCP at '%(location)', '%(expression)'", "expression", this.expression.getExpression(context), "location", this.source_code_location);
            }
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
        }
    }

    protected Expression expression;

    public Expression getExpression (CallContext context) {
        return this.expression;
    }

    public void setExpression (CallContext context, Expression expression) {
        this.expression = expression;
    }

    protected int pass;

    public int getPass (CallContext context) {
        return this.pass;
    }

    public void setPass (CallContext context, int pass) {
        this.pass = pass;
    }

    protected Scaffold scaffold;

    public Scaffold getScaffold (CallContext context) {
        return this.scaffold;
    }

    public void setScaffold (CallContext context, Scaffold scaffold) {
        this.scaffold = scaffold;
    }

    protected String source_code_location;

    public String getSourceCodeLocation (CallContext context) {
        return this.source_code_location;
    }

    public void setSourceCodeLocation (CallContext context, String source_code_location) {
        this.source_code_location = source_code_location;
    }
}
