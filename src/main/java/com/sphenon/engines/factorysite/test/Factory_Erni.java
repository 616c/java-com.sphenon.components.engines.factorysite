package com.sphenon.engines.factorysite.test;

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

public class Factory_Erni
{
    private Bert bert;
    private Erni erni;

    public void setBert(CallContext context, Bert bert) {
        this.bert = bert;
    }

    public Erni create (CallContext context) {
        Erni erni = this.precreate(context);
        this.erni = null;
        erni.setBert(context, bert);
        return erni;
    }

    public Erni precreate (CallContext context) {
        if (this.erni == null) {
            this.erni = new Erni(context);
        }
        return this.erni;
    }
}
