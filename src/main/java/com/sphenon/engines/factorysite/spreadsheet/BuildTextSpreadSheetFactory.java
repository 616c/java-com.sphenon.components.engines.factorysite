package com.sphenon.engines.factorysite.spreadsheet;

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
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.performance.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.data.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.markdown.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

import java.io.InputStream;

import org.odftoolkit.simple.*;
import org.odftoolkit.simple.table.*;

public class BuildTextSpreadSheetFactory implements BuildTextFactory {

    static final public Class _class = BuildTextSpreadSheetFactory.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    protected BuildTextMarkDownFactory btmdf;

    public BuildTextSpreadSheetFactory (CallContext context, Data_MediaObject data, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, transform(context, data.getStream(context), data.getDispositionFilename(context)), bt_by_oid_override, signature_override, data.getDispositionFilename(context));
    }

    public BuildTextSpreadSheetFactory (CallContext context, InputStream input_stream, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, transform(context, input_stream, "<stream>"), bt_by_oid_override, signature_override, "<stream>");
    }

    public BuildTextSpreadSheetFactory (CallContext context, File file, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        this(context, transform(context, file, file.getPath()), bt_by_oid_override, signature_override, file.getPath());
    }

    static protected InputStream transform(CallContext context, InputStream input_stream, String info) throws InvalidDocument {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(bw);

            System.err.println("~~ ODO ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            SpreadsheetDocument ods = SpreadsheetDocument.loadDocument(input_stream);

            for (int s=0; s<ods.getSheetCount(); s++) {
                Table table = ods.getSheetByIndex(s);
                if (table.getTableName().startsWith("#") == false) {
                    for (int r=0; r<table.getRowCount(); r++) {
                        Row row = table.getRowByIndex(r);
                        boolean leading = true;
                        for (int c=0; c<row.getCellCount(); c++) {
                            Cell cell = row.getCellByIndex(c);
                            String text = cell.getDisplayText().trim();
                            // all sheets except first are indented
                            // that allows e.g. package,class,class,...
                            if (s != 0) { pw.print("  "); System.err.print("  "); }
                            if (text.isEmpty()) {
                                if (leading) { pw.print("  "); System.err.print("  "); }
                            } else {
                                if ( ! leading) { pw.print(" "); System.err.print(" "); }
                                pw.print(text); System.err.print(text);
                                leading = false;
                            }
                        }
                        pw.print("\n"); System.err.print("\n");
                    }
                }
            }
            System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            pw.close();
            bw.close();
            osw.close();
            baos.close();
            input_stream.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return new BufferedInputStream(bais);
        } catch (Throwable t) {
            InvalidDocument.createAndThrow(context, t, "The OCP in SpreadsheetDocument %(info)'' could not be read.", "info", info);
            throw (InvalidDocument) null; // compiler insists
        }
    }

    static protected InputStream transform(CallContext context, File file, String info) throws InvalidDocument {
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStream result = transform(context, fis, info);
            fis.close();
            return result;
        } catch (IOException ioe) {
            InvalidDocument.createAndThrow(context, ioe, "The OCP in SpreadsheetDocument %(info)'' could not be read.", "info", info);
            throw (InvalidDocument) null; // compiler insists
        }
    }

    protected BuildTextSpreadSheetFactory (CallContext context, InputStream input_stream, Map<String, BuildText> bt_by_oid_override, String signature_override, String info) throws InvalidDocument {
        this.btmdf = new BuildTextMarkDownFactory (context, input_stream, bt_by_oid_override, signature_override);
    }

    public BuildText getBuildText(CallContext context) {
        return this.btmdf.getBuildText(context);
    }

    public String getNameSpace(CallContext context) {
        return this.btmdf.getNameSpace(context);
    }

    public String getOCPId(CallContext context) {
        return this.btmdf.getOCPId(context);
    }

    public String getSignature(CallContext context) {
        return this.btmdf.getSignature(context);
    }

    public String getBase(CallContext context) {
        return this.btmdf.getBase(context);
    }

    public String getPolymorphic(CallContext context) {
        return this.btmdf.getPolymorphic(context);
    }

    public HashMap<String,String> getMetaData (CallContext context) {
        return this.btmdf.getMetaData(context);
    }
}
