package com.sphenon.engines.factorysite.returncodes;

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
import com.sphenon.basics.variatives.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

public class BuildFailure extends ReturnCode {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(com.sphenon.basics.context.classes.RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.returncodes.BuildFailure"); };

    protected BuildFailure (CallContext context, Throwable cause, Message message) {
        super(context, cause, message);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, Message message) {
        if ((notification_level & Notifier.MORE_VERBOSE) != 0) { CustomaryContext.create(Context.create(context)).sendTrace(context, Notifier.MORE_VERBOSE, "returning 'BuildFailure' : %(message)", "message", message == null ? (Object) "(no details)" : (Object) message); }
        return new BuildFailure(context, cause, message);
    }

    static public BuildFailure createBuildFailure (CallContext context) {
        return createBuildFailure(context, (Throwable) null, (Message) null);
    }

    static public void createAndThrow (CallContext context) throws BuildFailure {
        throw createBuildFailure(context);
    }

    static public BuildFailure createBuildFailure (CallContext context, String message) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, String message) throws BuildFailure {
        throw createBuildFailure(context, message);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, String message) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, String message) throws BuildFailure {
        throw createBuildFailure(context, cause, message);
    }

    static public BuildFailure createBuildFailure (CallContext context, String message, Object[][] attributes) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, attributes));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, String message, Object[][] attributes) throws BuildFailure {
        throw createBuildFailure(context, message, attributes);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, String message, Object[][] attributes) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, attributes));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, String message, Object[][] attributes) throws BuildFailure {
        throw createBuildFailure(context, cause, message, attributes);
    }

    static public BuildFailure createBuildFailure (CallContext context, String message, String an1, Object av1) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, String message, String an1, Object av1) throws BuildFailure {
        throw createBuildFailure(context, message, an1, av1);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, String message, String an1, Object av1) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, String message, String an1, Object av1) throws BuildFailure {
        throw createBuildFailure(context, cause, message, an1, av1);
    }

    static public BuildFailure createBuildFailure (CallContext context, String message, String an1, Object av1, String an2, Object av2) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, String message, String an1, Object av1, String an2, Object av2) throws BuildFailure {
        throw createBuildFailure(context, message, an1, av1, an2, av2);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, String message, String an1, Object av1, String an2, Object av2) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, String message, String an1, Object av1, String an2, Object av2) throws BuildFailure {
        throw createBuildFailure(context, cause, message, an1, av1, an2, av2);
    }

    static public BuildFailure createBuildFailure (CallContext context, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3) throws BuildFailure {
        throw createBuildFailure(context, message, an1, av1, an2, av2, an3, av3);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3) throws BuildFailure {
        throw createBuildFailure(context, cause, message, an1, av1, an2, av2, an3, av3);
    }

    static public BuildFailure createBuildFailure (CallContext context, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3, an4, av4));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4) throws BuildFailure {
        throw createBuildFailure(context, message, an1, av1, an2, av2, an3, av3, an4, av4);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3, an4, av4));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4) throws BuildFailure {
        throw createBuildFailure(context, cause, message, an1, av1, an2, av2, an3, av3, an4, av4);
    }

    static public BuildFailure createBuildFailure (CallContext context, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4, String an5, Object av5) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3, an4, av4, an5, av5));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4, String an5, Object av5) throws BuildFailure {
        throw createBuildFailure(context, message, an1, av1, an2, av2, an3, av3, an4, av4, an5, av5);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4, String an5, Object av5) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3, an4, av4, an5, av5));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, String message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4, String an5, Object av5) throws BuildFailure {
        throw createBuildFailure(context, cause, message, an1, av1, an2, av2, an3, av3, an4, av4, an5, av5);
    }

    static public BuildFailure createBuildFailure (CallContext context, VariativeString message) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, VariativeString message) throws BuildFailure {
        throw createBuildFailure(context, message);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, VariativeString message) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, VariativeString message) throws BuildFailure {
        throw createBuildFailure(context, cause, message);
    }

    static public BuildFailure createBuildFailure (CallContext context, VariativeString message, Object[][] attributes) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, attributes));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, VariativeString message, Object[][] attributes) throws BuildFailure {
        throw createBuildFailure(context, message, attributes);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, VariativeString message, Object[][] attributes) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, attributes));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, VariativeString message, Object[][] attributes) throws BuildFailure {
        throw createBuildFailure(context, cause, message, attributes);
    }

    static public BuildFailure createBuildFailure (CallContext context, VariativeString message, String an1, Object av1) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, VariativeString message, String an1, Object av1) throws BuildFailure {
        throw createBuildFailure(context, message, an1, av1);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, VariativeString message, String an1, Object av1) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, VariativeString message, String an1, Object av1) throws BuildFailure {
        throw createBuildFailure(context, cause, message, an1, av1);
    }

    static public BuildFailure createBuildFailure (CallContext context, VariativeString message, String an1, Object av1, String an2, Object av2) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, VariativeString message, String an1, Object av1, String an2, Object av2) throws BuildFailure {
        throw createBuildFailure(context, message, an1, av1, an2, av2);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, VariativeString message, String an1, Object av1, String an2, Object av2) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, VariativeString message, String an1, Object av1, String an2, Object av2) throws BuildFailure {
        throw createBuildFailure(context, cause, message, an1, av1, an2, av2);
    }

    static public BuildFailure createBuildFailure (CallContext context, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3) throws BuildFailure {
        throw createBuildFailure(context, message, an1, av1, an2, av2, an3, av3);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3) throws BuildFailure {
        throw createBuildFailure(context, cause, message, an1, av1, an2, av2, an3, av3);
    }

    static public BuildFailure createBuildFailure (CallContext context, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3, an4, av4));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4) throws BuildFailure {
        throw createBuildFailure(context, message, an1, av1, an2, av2, an3, av3, an4, av4);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3, an4, av4));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4) throws BuildFailure {
        throw createBuildFailure(context, cause, message, an1, av1, an2, av2, an3, av3, an4, av4);
    }

    static public BuildFailure createBuildFailure (CallContext context, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4, String an5, Object av5) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3, an4, av4, an5, av5));
        return createBuildFailure(context, (Throwable) null, msg);
    }

    static public void createAndThrow (CallContext context, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4, String an5, Object av5) throws BuildFailure {
        throw createBuildFailure(context, message, an1, av1, an2, av2, an3, av3, an4, av4, an5, av5);
    }

    static public BuildFailure createBuildFailure (CallContext context, Throwable cause, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4, String an5, Object av5) {
        Message msg = DetailMessage.create(context, MessageText.create(context, message, an1, av1, an2, av2, an3, av3, an4, av4, an5, av5));
        return createBuildFailure(context, cause, msg);
    }

    static public void createAndThrow (CallContext context, Throwable cause, VariativeString message, String an1, Object av1, String an2, Object av2, String an3, Object av3, String an4, Object av4, String an5, Object av5) throws BuildFailure {
        throw createBuildFailure(context, cause, message, an1, av1, an2, av2, an3, av3, an4, av4, an5, av5);
    }
}
