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
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

import com.sphenon.engines.factorysite.tocp.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ExpressionEvaluator_TOCP implements ExpressionEvaluator {
    static final public Class _class = ExpressionEvaluator_TOCP.class;

    static protected Configuration config;
    static { config = Configuration.create(RootContext.getInitialisationContext(), _class); };

    public ExpressionEvaluator_TOCP (CallContext context) {
        this.result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
        this.activity_interface = new Class_ActivityInterface(context);
        this.activity_interface.addAttribute(context, this.result_attribute);
    }

    protected Class_ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    public String[] getIds(CallContext context) {
        return new String[] { "tocp", "ocp" };
    }

    public Object evaluate(CallContext context, String string, Scope scope, com.sphenon.basics.data.DataSink<Execution> execution_sink) throws EvaluationFailure {
        if ("help".equals(string)) { return config.get(context, "HelpText", (String) null); }
        try {

            TOCPASTNode tan = TOCPASTNode.parseTOCP(context, string);
            Pair_BuildText_String_ pbts = TOCPBuildText.create(context, tan);
            // Dumper.dump(context, null, pbts.getItem1(context));
            Map<String,String> mapping = tan.getArgumentMapping(context);
            List<Object> argument_list = new ArrayList<Object>();
            if (mapping != null) {
                for (String key : mapping.keySet()) {
                    Object value = scope.tryGet(context, mapping.get(key));
                    if (value != null) {
                        argument_list.add(key);
                        argument_list.add(value);
                    }
                }
            }
            Object[] arguments = new Object[argument_list.size()];
            argument_list.toArray(arguments);
            Object result = Factory_Aggregate.construct(context, pbts.getItem1(context), tan.getNameSpace(context), arguments);

            return result;
        } catch (Throwable t) {
            EvaluationFailure.createAndThrow(context, t, "Evaluation failure");
            throw (EvaluationFailure) null;
        }
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute);
    }
}
