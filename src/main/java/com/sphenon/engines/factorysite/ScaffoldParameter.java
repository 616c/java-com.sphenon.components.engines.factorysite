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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.Vector;

import java.io.StringWriter;

public class ScaffoldParameter
{

    protected String name;
    protected Type   type;
    protected String source_location_info;
    protected long   build_text_index;
    protected DataSourceSlotImpl data_source_slot;

    public ScaffoldParameter (CallContext context, String name, Type type, String source_location_info, long build_text_index) {
        this.name = name;
        this.type = type;
        this.source_location_info = source_location_info;
        this.build_text_index = build_text_index;
        this.data_source_slot = new DataSourceSlotImpl(context, type, source_location_info + "[name:" + name + "]");
    }

    public ScaffoldParameter (CallContext context, String name, Type type, String source_location_info, long build_text_index, Type... applies_to) {
        this(context, name, type, source_location_info, build_text_index);
        this.applies_to = applies_to;
    }

    public String getName (CallContext context) {
        return this.name;
    }

    public Type getType (CallContext context) {
        return this.type;
    }

    public void setType (CallContext context, Type type) {
        this.type = type;
        if (this.data_source_slot != null) {
            this.data_source_slot.setType(context, type);
        } else {
            // if this exception is called, please contact a developer :*) to analyse the problem (stack trace dumped, please provide)
            (new Throwable()).printStackTrace();
            CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, "this code fragment is not expected to be called (fix me)");
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
            // this.data_source_slot = new DataSourceSlotImpl(context, type, "");
        }
    }

    public void refineType(CallContext context, Type new_type) {
        if (new_type != null) {
            if (this.type == null) {
                this.setType(context, new_type);
            } else if (this.type.isA(context, new_type)) {
                // do nothing
            } else if (new_type.isA(context, this.type)) {
                this.setType(context, new_type);
            } else if (TypeManager.isAErased(context, this.type, new_type)) {
                // do nothing
            } else if (TypeManager.isAErased(context, new_type, this.type)) {
                this.setType(context, new_type);
            } else {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot refine type of scaffold parameter '%(name)' from '%(current)' to new '%(new)', types do not match", "name", this.name, "current", this.type.getName(context), "new", new_type.getName(context));
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        }
    }

    public DataSource getValue (CallContext context) {
        return this.data_source_slot.getDataSource(context);
    }

    public void setValue (CallContext context, DataSource data_source) throws TypeMismatch {
        this.data_source_slot.setDataSource(context, data_source);
    }

    protected Type[] applies_to;

    public Type[] getAppliesTo (CallContext context) {
        return this.applies_to;
    }

    public void setAppliesTo (CallContext context, Type[] applies_to) {
        this.applies_to = applies_to;
    }

    public long getBuildTextIndex (CallContext context) {
        return this.build_text_index;
    }

    public void printCOCPCode(CallContext context, StringWriter sw, Vector<Integer> dr, FactorySiteTextBased.Coder coder) {
        dr.add(COCPIndices.COCPScaffoldParameter);
        dr.add(coder.writeText(context, name));
        dr.add((type == null ? ((Integer) 0) : coder.writeText(context, type.getId(context))));
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
        String code = "new ScaffoldParameter(context, \"" + name + "\", " + (type == null ? "null" : ("TypeManager.tryGetById(context, \"" + type.getId(context) + "\")")) + ", \"" + source_location_info + "\", " + build_text_index;
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
