package com.sphenon.basics.data;

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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.engines.factorysite.factories.*;

import java.util.Hashtable;

abstract public class DataSource_OnDemand<T>  implements DataSource<T> {
    public DataSource_OnDemand(CallContext context) {
    }
    
    protected T object_cache = null;
    
    protected ActivityClass preparation;

    public ActivityClass getPreparation (CallContext context) {
        return this.preparation;
    }

    public ActivityClass defaultPreparation (CallContext context) {
        return null;
    }

    public void setPreparation (CallContext context, ActivityClass preparation) {
        this.preparation = preparation;
    }

    protected boolean use_application_class_loader = false;
    
    public boolean isUseApplicationClassLoader(CallContext context) {
        return use_application_class_loader;
    }

    public void setUseApplicationClassLoader(CallContext context, boolean use_application_class_loader) {
        this.use_application_class_loader = use_application_class_loader;
    }
    
    public boolean defaultUseApplicationClassLoader(CallContext context){
        return this.use_application_class_loader;
    } 

    abstract protected T createOnDemandInstance(CallContext context);
    abstract protected String getErrorInfo(CallContext context);

    public Object getObject(CallContext context) {
        return this.get(context);
    }

    public T get(CallContext context) {
        ClassLoader current = null;
        try{
            if (this.use_application_class_loader) {
                current = com.sphenon.basics.application.ApplicationContext.setApplicationClassLoader(context);
            }
            if (this.object_cache == null) {

                if (this.preparation != null) {
                    try {
                        Activity a = this.preparation.instantiate(context, null);
                        Execution e = a.execute(context);
                        if (e.getProblemState(context).isOk(context) == false) {
                            // [Issue] actually, "Execution" is at a similar
                            // level as "Exception" is, i.e. it contains detailed,
                            // structured infos, therefore it should be possible
                            // to attach it similarly to ExceptionError and to
                            // dump it when ExcepionErrors are presented
                            Dumper.dump(context, "DataSource OnDemand Failure", e);
                            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, "Preparation of OnDemand '%(info)' failed, activity '%(activity)', result: '%(execution)'", "info", this.getErrorInfo(context), "activity", this.preparation, "execution", e);
                            throw (ExceptionEnvironmentFailure) null; // compiler insists
                        }
                    } catch (EvaluationFailure ef) {
                        CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ef, "Preparation of OnDemand '%(info)' failed, activity '%(activity)'", "info", this.getErrorInfo(context), "activity", this.preparation);
                        throw (ExceptionEnvironmentFailure) null; // compiler insists
                    }
                }

                this.object_cache = createOnDemandInstance(context);
            }
            return this.object_cache;
        } finally{
            if (this.use_application_class_loader) {
                com.sphenon.basics.application.ApplicationContext.resetApplicationClassLoader(context, current);
            }
        }
    } 
} 
