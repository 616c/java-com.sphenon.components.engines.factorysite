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
import com.sphenon.basics.expression.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.Vector;

import java.io.StringWriter;

public class DynamicScaffoldParameter extends ScaffoldParameter
{

    public DynamicScaffoldParameter (CallContext context, String name, Type type, String name_template, boolean is_expression, String if_expression, String foreach_expression, String variable_definition_expression, String source_location_info, long build_text_index) {
        super(context, name, type, source_location_info, build_text_index);
        this.name_template      = (name_template == null || name_template.length() == 0 ? null : new DynamicString(context, name_template, "jspp"));
        this.is_expression      = is_expression;
        this.if_expression      = (if_expression == null || if_expression.length() == 0 ? null : new Expression(context, if_expression, "jspp"));
        if (foreach_expression == null || foreach_expression.length() == 0) {
            this.foreach_expression = null;
        } else {
            this.unparsed_foreach_expression = foreach_expression;
            String fvdes[] = foreach_expression.replaceAll("(^ *)|( *$)","").split("(?: *);(?: *)");
            this.foreach_variable_name = new String[fvdes.length-1];
            this.foreach_variable_definition_expression = new Expression[fvdes.length-1];
            int index=0;
            for (String fvde : fvdes) {
                String fea[] = fvde.split("(?: *):(?: *)", 2);
                if (index == 0) {
                    this.foreach_index_name = fea[0];
                    this.foreach_expression = new Expression(context, fea[1], "jspp");
                } else {
                    this.foreach_variable_name[index-1] = fea[0];
                    this.foreach_variable_definition_expression[index-1] = new Expression(context, fea[1], "jspp");
                }
                index++;
            }
        }

        if (variable_definition_expression == null || variable_definition_expression.length() == 0) {
            this.unparsed_variable_definition_expression = null;
            this.variable_definition_expression = null;
        } else {
            this.unparsed_variable_definition_expression = variable_definition_expression;
            String vdes[] = variable_definition_expression.replaceAll("(^ *)|( *$)","").split("(?: *);(?: *)");
            this.variable_name = new String[vdes.length];
            this.variable_definition_expression = new Expression[vdes.length];
            int index=0;
            for (String vde : vdes) {
                String fea[] = vde.split("(?: *):(?: *)", 2);
                this.variable_name[index] = fea[0];
                this.variable_definition_expression[index] = new Expression(context, fea[1], "jspp");
                index++;
            }
        }
    }

    public DynamicScaffoldParameter (CallContext context, String name, Type type, String name_template, boolean is_expression, String if_expression, String foreach_expression, String variable_definition_expression, String source_location_info, long build_text_index, Type... applies_to) {
        this(context, name, type, name_template, is_expression, if_expression, foreach_expression, variable_definition_expression, source_location_info, build_text_index);
        this.applies_to = applies_to;
    }

    protected DynamicString name_template;

    public DynamicString getNameTemplate (CallContext context) {
        return this.name_template;
    }

    public void setNameTemplate (CallContext context, DynamicString name_template) {
        this.name_template = name_template;
    }

    protected boolean is_expression;

    public boolean getIsExpression (CallContext context) {
        return this.is_expression;
    }

    public void setIsExpression (CallContext context, boolean is_expression) {
        this.is_expression = is_expression;
    }

    protected Expression if_expression;

    public Expression getIfExpression (CallContext context) {
        return this.if_expression;
    }

    public void setIfExpression (CallContext context, Expression if_expression) {
        this.if_expression = if_expression;
    }

    protected Expression foreach_expression;

    public Expression getForeachExpression (CallContext context) {
        return this.foreach_expression;
    }

    public void setForeachExpression (CallContext context, Expression foreach_expression) {
        this.foreach_expression = foreach_expression;
    }

    protected String foreach_index_name;

    public String getForeachIndexName (CallContext context) {
        return this.foreach_index_name;
    }

    public void setForeachIndexName (CallContext context, String foreach_index_name) {
        this.foreach_index_name = foreach_index_name;
    }

    protected String unparsed_variable_definition_expression;

    protected Expression[] variable_definition_expression;

    public Expression[] getVariableDefinitionExpression (CallContext context) {
        return this.variable_definition_expression;
    }

    public void setVariableDefinitionExpression (CallContext context, Expression[] variable_definition_expression) {
        this.variable_definition_expression = variable_definition_expression;
    }

    protected String[] variable_name;

    public String[] getVariableName (CallContext context) {
        return this.variable_name;
    }

    public void setVariableName (CallContext context, String[] variable_name) {
        this.variable_name = variable_name;
    }

    protected String unparsed_foreach_expression;

    protected Expression[] foreach_variable_definition_expression;

    public Expression[] getForeachVariableDefinitionExpression (CallContext context) {
        return this.foreach_variable_definition_expression;
    }

    public void setForeachVariableDefinitionExpression (CallContext context, Expression[] foreach_variable_definition_expression) {
        this.foreach_variable_definition_expression = foreach_variable_definition_expression;
    }

    protected String[] foreach_variable_name;

    public String[] getForeachVariableName (CallContext context) {
        return this.foreach_variable_name;
    }

    public void setForeachVariableName (CallContext context, String[] foreach_variable_name) {
        this.foreach_variable_name = foreach_variable_name;
    }

    public void printCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder) {
        dr.add(COCPIndices.COCPDynamicScaffoldParameter);
        dr.add(coder.writeText(context, name));
        dr.add((type == null ? ((Integer) 0) : coder.writeText(context, type.getId(context))));
        dr.add((this.name_template == null ? ((Integer) 0) : coder.writeText(context, this.name_template.getStringTemplate(context))));
        dr.add(coder.writeBoolean(context, this.is_expression));
        dr.add((this.if_expression == null ? ((Integer) 0) : coder.writeText(context, this.if_expression.getExpression(context))));
        dr.add((this.unparsed_foreach_expression == null ? ((Integer) 0) : coder.writeText(context, this.unparsed_foreach_expression)));
        dr.add((this.unparsed_variable_definition_expression == null ? ((Integer) 0) : coder.writeText(context, this.unparsed_variable_definition_expression)));
        dr.add(coder.writeText(context, source_location_info));
        dr.add((int) build_text_index);

        if (applies_to == null) {
            dr.add(((Integer) 0));
        } else {
            dr.add(applies_to.length);
            for (Type t : applies_to) {
                dr.add(coder.writeText(context, t.getId(context)));
            }
        }
    }

    public String getCOCPCode(CallContext context) {
        String code = "new DynamicScaffoldParameter(context, \"" + name + "\", " + (type == null ? "null" : ("TypeManager.tryGetById(context, \"" + type.getId(context) + "\")")) + ", " + (this.name_template == null ? "null" : ("\"" + this.name_template.getStringTemplate(context) + "\"")) + ", " + this.is_expression + ", " + (this.if_expression == null ? "null" : ("\"" + Encoding.recode(context, this.if_expression.getExpression(context), Encoding.UTF8, Encoding.JAVA) + "\"")) + ", " + (this.unparsed_foreach_expression == null ? "null" : ("\"" + Encoding.recode(context, this.unparsed_foreach_expression, Encoding.UTF8, Encoding.JAVA) + "\"")) + ", " + (this.unparsed_variable_definition_expression == null ? "null" : ("\"" + Encoding.recode(context, this.unparsed_variable_definition_expression, Encoding.UTF8, Encoding.JAVA) + "\"")) + ", \"" + source_location_info + "\", " + build_text_index;
        if (applies_to == null) {
        } else {
            for (Type t : applies_to) {
                code += ", TypeManager.tryGetById(context, \"" + t.getId(context) + "\")";
            }
        }
        code += ")";
        return code;
    }
}
