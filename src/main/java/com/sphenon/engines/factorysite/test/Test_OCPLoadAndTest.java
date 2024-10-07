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
import com.sphenon.basics.notification.*;
import com.sphenon.basics.monitoring.*;
import com.sphenon.basics.testing.TestRun;
import com.sphenon.basics.testing.TestResult;
import com.sphenon.basics.testing.TestResult_ExceptionRaised;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.gates.*;

import java.util.Hashtable;

public class Test_OCPLoadAndTest extends com.sphenon.basics.testing.classes.TestBase {

    static final public Class _class = Test_OCPLoadAndTest.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    public Test_OCPLoadAndTest (CallContext context) {
    }

    public String getId(CallContext context) {
        if (this.id == null) {
            this.id = "OCPLoadAndTest:" + aggregate_class;
        }
        return this.id;
    }

    protected String aggregate_class;

    public String getAggregateClass (CallContext context) {
        return this.aggregate_class;
    }

    public void setAggregateClass (CallContext context, String aggregate_class) {
        this.aggregate_class = aggregate_class;
    }

    protected Hashtable arguments;

    public Hashtable getArguments (CallContext context) {
        return this.arguments;
    }

    public void setArguments (CallContext context, Hashtable arguments) {
        this.arguments = arguments;
    }

    public Hashtable defaultArguments (CallContext context) {
        return null;
    }

    public TestResult perform (CallContext context, TestRun test_run) {
        try {
            com.sphenon.basics.testing.Test test = (com.sphenon.basics.testing.Test) Factory_Aggregate.construct(context, this.aggregate_class, this.arguments);

            return test.perform(context, test_run);
        } catch (Throwable t) {
            return new TestResult_ExceptionRaised(context, t);
        }
    }
}
