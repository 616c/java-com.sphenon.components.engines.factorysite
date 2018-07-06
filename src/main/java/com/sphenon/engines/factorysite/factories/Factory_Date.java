package com.sphenon.engines.factorysite.factories;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.Date;
import java.util.Locale;
import java.text.*;

public class Factory_Date {

    public Date create (CallContext context) {
        if (this.date == null || date.length() == 0) { return null; }
        DateFormat df = null;
        Locale l = this.locale != null ? Locale.forLanguageTag(this.locale) : null;
        if (this.format != null) {
            if (l != null) {
                df = new SimpleDateFormat(this.format, l);
            } else {
                df = new SimpleDateFormat(this.format);
            }
        } else {
            if (l != null) {
                df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, l);
            } else {
                df = DateFormat.getDateTimeInstance();
            }
        }
        ParsePosition pp = new ParsePosition(0);
        Date result = df.parse(this.date, pp);
        if (result == null || pp.getIndex() != this.date.length()) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Invalid date '%(date)'", "date", this.date);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        return result;
    }

    protected String date;

    public String getDate (CallContext context) {
        return this.date;
    }

    public void setDate (CallContext context, String date) {
        this.date = (date == null ? null : date.trim());
    }

    protected String format;

    public String getFormat (CallContext context) {
        return this.format;
    }

    public String defaultFormat (CallContext context) {
        return null;
    }

    public void setFormat (CallContext context, String format) {
        this.format = format;
    }

    protected String locale;

    public String getLocale (CallContext context) {
        return this.locale;
    }

    public String defaultLocale (CallContext context) {
        return null;
    }

    public void setLocale (CallContext context, String locale) {
        this.locale = locale;
    }
}
