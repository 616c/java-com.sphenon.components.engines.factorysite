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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.debug.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.factories.*;

import java.lang.reflect.Method;

public class Reloadable<TargetType> implements Interceptor {
    static final public Class _class = Reloadable.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    protected TargetType object;
    protected Factory_Aggregate factory;

    public Reloadable(CallContext context, TargetType object, Factory_Aggregate factory) {
        this.object = object;
        this.factory = factory;
    }

    static public<TargetType> TargetType wrap(CallContext context, TargetType object, Factory_Aggregate factory) {
        return Delegate.create(object, new Reloadable(context, object, factory));
    }

    public boolean matches(Object target, Method method, Object[] arguments){
        return true;
    }

    public Object handleInvocation(Object proxy, Delegate delegate, Object target, Method method, Object[] arguments) throws Throwable {
        CallContext context = (arguments != null && arguments.length > 0 && arguments[0] instanceof CallContext ? ((CallContext) (arguments[0])) : RootContext.getFallbackCallContext());

        if (factory.checkChanged(context)) {
            target = factory.create(context);
            delegate.setTarget(target);
            this.object = (TargetType) target;
        }

        Object result = method.invoke(target, arguments);

        return result;
    }
}
