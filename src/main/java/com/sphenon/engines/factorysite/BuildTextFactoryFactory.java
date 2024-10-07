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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.performance.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.services.*;

import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.xml.*;
import com.sphenon.engines.factorysite.json.*;
import com.sphenon.engines.factorysite.yaml.*;
import com.sphenon.engines.factorysite.markdown.*;
import com.sphenon.engines.factorysite.spreadsheet.*;
import com.sphenon.engines.factorysite.diagram.*;

import java.util.*;
import java.io.*;

public class BuildTextFactoryFactory {

    static protected enum Format {
        XML,
        JSON,
        YAML,
        MarkDown,
        SpreadSheet,
        Diagram,
        Dynamic
    };

    static protected Map<String, Format> map;

    static protected Format getFormat(CallContext context, String filename) {
        if (filename == null || filename.isEmpty()) {
            return Format.Dynamic;
        }
        int    dotpos    = filename.lastIndexOf('.');
        String extension = filename.substring(dotpos+1);
        if (map == null) {
            map = new HashMap<String, Format>();
        }
        Format format = map.get(extension);
        if (format == null) {
            String f = Factory_Aggregate.getProperty(context, "FormatOfExtension." + extension, "XML");
            if (f.matches("XML")) {
                format = Format.XML;
            } else if (f.matches("JSON")) {
                format = Format.JSON;
            } else if (f.matches("YAML")) {
                format = Format.YAML;
            } else if (f.matches("MarkDown")) {
                format = Format.MarkDown;
            } else if (f.matches("SpreadSheet")) {
                format = Format.SpreadSheet;
            } else if (f.matches("Diagram")) {
                format = Format.Diagram;
            } else if (f.matches("Dynamic")) {
                format = Format.Dynamic;
            }
            map.put(extension, format);
        }
        return format;
    }

    static public BuildTextFactory create(CallContext context, Data_MediaObject data, Map<String, BuildText> bt_by_oid_override, String signature_override) throws InvalidDocument {
        String filename = data.getDispositionFilename(context);
        Format format = getFormat(context, filename);

        if (format == Format.Dynamic) {
            // to avoid recreation of the stream when getStream is called:
            data = Data_MediaObject_Stream.create(context, data);

            InputStream is = data.getStream(context);
            if (is.markSupported() == false) {
                is = new BufferedInputStream(is);
                ((Data_MediaObject_Stream) data).setStream(context, is);
            }

            try {
                int look_at = 1024;
                is.mark(look_at + 4);
                int b;
                int c = 0;
                boolean white;
                do {
                    b = is.read();
                    c++;
                    white = (b == ' ' || b == '\n' || b == '\r' || b == '\t');
                } while(c < look_at && white);
                if (white) {
                    format = Format.XML;
                } else if (b == '<') {
                    format = Format.XML;

                    byte[] ba = new byte[look_at + 4];
                    ba[0] = (byte) b;
                    int c2 = is.read(ba, 1, look_at - c);
                    String header = new String(ba, 0, c2+1);
                    if (header.matches("<\\?[^?]+\\?>[ \t\n]*<dia:diagram[ >].*")) {
                        format = Format.Diagram;
                    }
                } else if (b == '%' || b == '-') {
                    format = Format.YAML;
                } else if (b == '{' || b == '[' || b == '"' || b == '\'') {
                    format = Format.JSON;
                } else if (b == 0x50 && is.read() == 0x4B && is.read() == 0x03 && is.read() == 0x04) {
                    // https://pkware.cachefly.net/webdocs/casestudies/APPNOTE.TXT
                    // 4.3.6 Overall .ZIP file format
                    // 4.3.7  Local file header
                    format = Format.SpreadSheet;
                } else if (b == '@') {
                    // to be defined, just an idea; like @...namespace... in the beginning of MDOCP files
                    format = Format.MarkDown;
                } else {
                    format = Format.JSON;
                }
                is.reset();
            } catch (IOException ioe) {
                InvalidDocument.createAndThrow(context, ioe, "Could not determine file type by peeking into OCP stream '%(name)'", "name", filename);
                throw (InvalidDocument) null; // compiler insists
            }
        }

        BuildTextFactory btf = null;

        switch (format) {
            case XML:
                btf = new BuildTextXMLFactory(context, data, bt_by_oid_override, signature_override);
                break;
            case JSON:
                btf = new BuildTextJSONFactory(context, data, bt_by_oid_override, signature_override);
                break;
            case YAML:
                btf = new BuildTextYAMLFactory(context, data, bt_by_oid_override, signature_override);
                break;
            case MarkDown:
                btf = new BuildTextMarkDownFactory(context, data, bt_by_oid_override, signature_override);
                break;
            case SpreadSheet:
                btf = new BuildTextSpreadSheetFactory(context, data, bt_by_oid_override, signature_override);
                break;
            case Diagram:
                btf = new BuildTextDiagramFactory(context, data, bt_by_oid_override, signature_override);
                break;
        }

        return btf;
    }
}
