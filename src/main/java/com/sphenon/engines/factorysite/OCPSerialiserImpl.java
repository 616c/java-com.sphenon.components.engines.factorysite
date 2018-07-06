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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.engines.aggregator.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.IOException;

public class OCPSerialiserImpl implements OCPSerialiser {

    static public void serialise(CallContext context, Object object, String name, PrintWriter print_writer, boolean xml_header, int indent) {
        OCPSerialiserImpl osi = new OCPSerialiserImpl(context, print_writer);
        osi.indent = indent;
        osi.serialise(context, object, name, xml_header, false);
    }

    static public void serialise(CallContext context, Object object, String name, TreeLeaf tree_leaf, boolean xml_header, int indent) {
        OCPSerialiserImpl osi = new OCPSerialiserImpl(context, tree_leaf);
        osi.indent = indent;
        osi.serialise(context, object, name, xml_header, false);
    }

    static public void serialise(CallContext context, Object object, String name, String locator, boolean xml_header, int indent) {
        OCPSerialiserImpl osi = new OCPSerialiserImpl(context, locator);
        osi.indent = indent;
        osi.serialise(context, object, name, xml_header, false);
    }

    static public void serialise(CallContext context, Object object, String name, PrintWriter print_writer, boolean xml_header, int indent, int pass) {
        serialise(context, object, name, print_writer, xml_header, indent, pass, null);
    }

    static public void serialise(CallContext context, Object object, String name, PrintWriter print_writer, boolean xml_header, int indent, int pass, OIDSpace oid_space) {
        OCPSerialiserImpl osi = new OCPSerialiserImpl(context, print_writer, oid_space);
        osi.indent = indent;
        osi.pass = pass;
        osi.serialise(context, object, name, xml_header, false);
    }

    static public void serialise(CallContext context, Object object, String name, TreeLeaf tree_leaf, boolean xml_header, int indent, int pass) {
        serialise(context, object, name, tree_leaf, xml_header, indent, pass, null);
    }

    static public void serialise(CallContext context, Object object, String name, TreeLeaf tree_leaf, boolean xml_header, int indent, int pass, OIDSpace oid_space) {
        OCPSerialiserImpl osi = new OCPSerialiserImpl(context, tree_leaf, oid_space);
        osi.indent = indent;
        osi.pass = pass;
        osi.serialise(context, object, name, xml_header, false);
    }

    static public void serialise(CallContext context, Object object, String name, String locator, boolean xml_header, int indent, int pass) {
        serialise(context, object, name, locator, xml_header, indent, pass, null);
    }

    static public void serialise(CallContext context, Object object, String name, String locator, boolean xml_header, int indent, int pass, OIDSpace oid_space) {
        OCPSerialiserImpl osi = new OCPSerialiserImpl(context, locator, oid_space);
        osi.indent = indent;
        osi.pass = pass;
        osi.serialise(context, object, name, xml_header, false);
    }

    protected OCPSerialiserImpl(CallContext context, OIDSpace oid_space) {
        this.indent = 0;
        this.oid_space = oid_space != null ? oid_space : new OIDSpace(context);
    }

    public OCPSerialiserImpl(CallContext context, PrintWriter print_writer) {
        this(context, print_writer, null);
    }

    public OCPSerialiserImpl(CallContext context, PrintWriter print_writer, OIDSpace oid_space) {
        this(context, oid_space);
        this.print_writer = print_writer;
    }

    public OCPSerialiserImpl(CallContext context, TreeLeaf tree_leaf) {
        this(context, tree_leaf, null);
    }

    public OCPSerialiserImpl(CallContext context, TreeLeaf tree_leaf, OIDSpace oid_space) {
        this(context, oid_space);
        this.tree_leaf = tree_leaf;
    }

    public OCPSerialiserImpl(CallContext context, String locator) {
        this(context, locator, null);
    }

    public OCPSerialiserImpl(CallContext context, String locator, OIDSpace oid_space) {
        this(context, oid_space);
        try {
            this.tree_leaf = (TreeLeaf) Factory_TreeNode.construct(context, locator, NodeType.LEAF, true);
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, "Cannot create OCP serialiser");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    protected TreeLeaf    tree_leaf;
    protected PrintWriter print_writer;
    protected OIDSpace    oid_space;

    public PrintWriter getPrintWriter (CallContext context) {
        return this.print_writer;
    }

    protected int indent;

    public int getIndent (CallContext context) {
        return this.indent;
    }

    public void printIndent (CallContext context) {
        for (int i=0; i<indent; i++) {
            this.print_writer.print(" ");
        }
    }

    protected int pass;

    public int getPass (CallContext context) {
        return this.pass;
    }

    public void serialise(CallContext context, Object object, String name, boolean as_reference) {
        this.serialise(context, object, name, false, as_reference);
    }

    public void serialise(CallContext context, Object object, String name, boolean as_reference, String cls) {
        this.serialise(context, object, name, false, as_reference, cls);
    }

    public void serialise(CallContext context, Object object, String name, boolean xml_header, boolean as_reference) {
        serialise(context, object, name, xml_header, as_reference, "");
    }

    public void serialise(CallContext context, Object object, String name, boolean xml_header, boolean as_reference, String cls) {
        OutputStream os = null;
        if (this.print_writer == null) {
            if (this.tree_leaf != null) {
                Data_MediaObject data = ((Data_MediaObject)(((NodeContent_Data)(tree_leaf.getContent(context))).getData(context)));
                os = data.getOutputStream(context);
                this.print_writer = new PrintWriter(os);
            }
        }

        String tag_name = name == null ? null : name.replace(':','-');
        String oidref = null;

        try {
            if (xml_header) {
                this.print_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            }
            this.printIndent(context);
            if (object == null) {
                this.print_writer.print("<" + tag_name + " NULL=\"true\"/>\n");
            } else if (object instanceof Boolean) {
                this.print_writer.print("<" + tag_name + (cls == null ? "" : (" CLASS=\"" + (cls.isEmpty() ? "Boolean" : cls) + "\"")) + ">");
                this.print_writer.print(((Boolean) object).toString());
                this.print_writer.print("</" + tag_name + ">\n");
            } else if (object instanceof Byte) {
                this.print_writer.print("<" + tag_name + (cls == null ? "" : (" CLASS=\"" + (cls.isEmpty() ? "Byte" : cls) + "\"")) + ">");
                this.print_writer.print(((Byte) object).toString());
                this.print_writer.print("</" + tag_name + ">\n");
            } else if (object instanceof Character) {
                this.print_writer.print("<" + tag_name + (cls == null ? "" : (" CLASS=\"" + (cls.isEmpty() ? "Character" : cls) + "\"")) + ">");
                this.print_writer.print(((Character) object).toString());
                this.print_writer.print("</" + tag_name + ">\n");
            } else if (object instanceof Short) {
                this.print_writer.print("<" + tag_name + (cls == null ? "" : (" CLASS=\"" + (cls.isEmpty() ? "Short" : cls) + "\"")) + ">");
                this.print_writer.print(((Short) object).toString());
                this.print_writer.print("</" + tag_name + ">\n");
            } else if (object instanceof Integer) {
                this.print_writer.print("<" + tag_name + (cls == null ? "" : (" CLASS=\"" + (cls.isEmpty() ? "Integer" : cls) + "\"")) + ">");
                this.print_writer.print(((Integer) object).toString());
                this.print_writer.print("</" + tag_name + ">\n");
            } else if (object instanceof Long) {
                this.print_writer.print("<" + tag_name + (cls == null ? "" : (" CLASS=\"" + (cls.isEmpty() ? "Long" : cls) + "\"")) + ">");
                this.print_writer.print(((Long) object).toString());
                this.print_writer.print("</" + tag_name + ">\n");
            } else if (object instanceof Float) {
                this.print_writer.print("<" + tag_name + (cls == null ? "" : (" CLASS=\"" + (cls.isEmpty() ? "Float" : cls) + "\"")) + ">");
                this.print_writer.print(((Float) object).toString());
                this.print_writer.print("</" + tag_name + ">\n");
            } else if (object instanceof Double) {
                this.print_writer.print("<" + tag_name + (cls == null ? "" : (" CLASS=\"" + (cls.isEmpty() ? "Double" : cls) + "\"")) + ">");
                this.print_writer.print(((Double) object).toString());
                this.print_writer.print("</" + tag_name + ">\n");
            } else if (object instanceof String) {
                this.print_writer.print("<" + tag_name + (cls == null ? "" : (" CLASS=\"" + (cls.isEmpty() ? "String" : cls) + "\"")) + ">");
                this.print_writer.print(Encoding.recode(context, ((String) object), Encoding.UTF8, Encoding.XML));
                this.print_writer.print("</" + tag_name + ">\n");
            } else if (object.getClass().isEnum()) {
                this.print_writer.print("<" + tag_name + (cls == null || cls.isEmpty() ? "" : (" CLASS=\"" + cls + "\"")) + ">");
                this.print_writer.print(Encoding.recode(context, object.toString(), Encoding.UTF8, Encoding.XML));
                this.print_writer.print("</" + tag_name + ">\n");
            } else if (    (oidref = (    object != null
                                       && (this.oid_space.isDefined(context, object) || this.oid_space.defineOnlyExplicitly(context, object))
                                     ) ? this.oid_space.getReference(context, object) : null)
                           != null) {
                this.print_writer.print("<" + tag_name + " OIDREF=\"" + oidref + "\"/>\n");
            } else if (object instanceof OCPSerialisable) {
                OCPSerialisable ocps = (OCPSerialisable) object;
                if (name == null) {
                    name = ocps.ocpDefaultName(context);
                }
                String oc = ocps.ocpClass(context);
                String of = ocps.ocpFactory(context);
                String or = ocps.ocpRetriever(context);
                String oid = this.oid_space.define(context, object);
                this.print_writer.print("<" + name + (oid != null ? (" OID=\"" + oid + "\"") : ""));
                if (oc != null && (as_reference ? (or == null) : (of == null))) {
                    if (cls != null) { this.print_writer.print(" CLASS=\"" + (cls.isEmpty() ? oc : cls) + "\""); }
                }
                if (as_reference) {
                    if (or != null) {
                        this.print_writer.print(" RETRIEVER=\"" + or + "\"");
                    }
                } else {
                    if (of != null) {
                        this.print_writer.print(" FACTORY=\"" + of + "\"");
                    }
                }
                if (this.pass != 0) {
                    this.print_writer.print(" PASS=\"" + this.pass + "\"");
                }
                if (ocps.ocpEmpty(context)) {
                    this.print_writer.print("/>\n");
                } else {
                    this.print_writer.print(">");
                    if (ocps.ocpContainsData(context) == false) { this.print_writer.print("\n"); }
                    this.indent += 2;
                    ocps.ocpSerialise(context, this, as_reference);
                    this.indent -= 2;
                    if (ocps.ocpContainsData(context) == false) { this.printIndent(context); }
                    this.print_writer.print("</" + name + ">\n");
                }
            } else {
                this.print_writer.print("<!-- '" + (name == null ? "<unnamed>" : tag_name) + "'not serialisable to OCP: ");
                this.print_writer.print(object.getClass().getName().replaceFirst(".*\\.",""));
                this.print_writer.print(" -->\n");
            }
        } finally {
            if (os != null) {
                this.print_writer.close();
                this.print_writer = null;
                try {
                    os.close();
                } catch(IOException ioe) {
                    CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "After serialising OCP stream could not be closed");
                    throw (ExceptionEnvironmentFailure) null; // compiler insists
                }
            }
        }
    }
}
