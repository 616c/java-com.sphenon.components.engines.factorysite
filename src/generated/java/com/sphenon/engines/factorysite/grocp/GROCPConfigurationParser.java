/* Generated By:JavaCC: Do not edit this line. GROCPConfigurationParser.java */
package com.sphenon.engines.factorysite.grocp;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.diagram.*;

import java.io.*;
import java.util.*;

public class GROCPConfigurationParser implements GROCPConfigurationParserConstants {

    static protected GROCPConfigurationParser grocp_configuration_parser;
    static protected boolean debug = true;

    static protected GROCPConfigurationParser getGROCPConfigurationParser(CallContext context, InputStream stream, String source_info) throws ParseException, IOException {
        if (grocp_configuration_parser == null) { grocp_configuration_parser = new GROCPConfigurationParser(context, stream, source_info); }
        else                                    { grocp_configuration_parser.ReInit(context, stream, source_info); }
        return grocp_configuration_parser;
    }

    static protected GROCPConfigurationParser getGROCPConfigurationParser(CallContext context, String string, String source_info) throws ParseException, IOException {
        if (grocp_configuration_parser == null) { grocp_configuration_parser = new GROCPConfigurationParser(context, string, source_info); }
        else                                    { grocp_configuration_parser.ReInit(context, string, source_info); }
        return grocp_configuration_parser;
    }

    static synchronized public void retrieveGROCPConfiguration(CallContext context, String name_space, DIAConfiguration dia_configuration, DIAItem dia_item) throws ParseException {
        String path = name_space.replaceAll("/([0-9.]+)(?=/|$)","-$1");
        OCPFinder ocp_finder = new OCPFinder(context, path, null, null, null);
        try {
            ocp_finder.findOCP(context, "grocp.cfg");
        } catch (ValidationFailure vf) {
            CustomaryContext.create((Context)context).throwVerificationFailure(context, vf, "While parsing GROCPConfiguration '%(info)', defining resource '%(path)' for namespace '%(namespace)' not found", "path", path, "namespace", name_space, "info", path);
            throw (ExceptionVerificationFailure) null; // compiler insists
        }
        if (ocp_finder.found == false) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "While parsing GROCPConfiguration '%(info)', defining resource '%(path)' for namespace '%(namespace)' not found", "path", path, "namespace", name_space, "info", path);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        Data_MediaObject data = ((Data_MediaObject)(((NodeContent_Data)(((TreeLeaf) ocp_finder.result_node).getContent(context))).getData(context)));
        InputStream stream = data.getStream(context);
        try {
            getGROCPConfigurationParser(context, stream, path).GROCPConfiguration(context, dia_configuration, dia_item);
        } catch (ParseException pe) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, pe, "Parsing GROCPConfiguration '%(info)' from '%(path)' for namespace '%(namespace)' failed", "path", path, "namespace", name_space, "info", path);
            throw (ExceptionConfigurationError) null; // compiler insists
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, ioe, "Parsing GROCPConfiguration '%(info)' from '%(path)' for namespace '%(namespace)' failed", "path", path, "namespace", name_space, "info", path);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    static synchronized public void processGROCPConfiguration(CallContext context, String string, DIAConfiguration dia_configuration, DIAItem dia_item) throws ParseException {
        try {
            getGROCPConfigurationParser(context, string, dia_item.getSourceInfo(context)).GROCPConfiguration(context, dia_configuration, dia_item);
        } catch (ParseException pe) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, pe, "Parsing GROCPConfiguration '%(info)' failed", "info", dia_item.getSourceInfo(context));
            throw (ExceptionConfigurationError) null; // compiler insists
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, ioe, "Parsing GROCPConfiguration '%(info)' failed", "info", dia_item.getSourceInfo(context));
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    protected String source_info;

    public GROCPConfigurationParser (CallContext context, String string, String source_info) {
        this(new BufferedReader(new StringReader(string)));
        this.source_info = source_info;
    }

    public void ReInit (CallContext context, String string, String source_info) {
        ReInit(new BufferedReader(new StringReader(string)));
        this.source_info = source_info;
    }

    public GROCPConfigurationParser (CallContext context, InputStream stream, String source_info) throws IOException {
        this(new BufferedReader(new InputStreamReader(stream, "UTF-8")));
        this.source_info = source_info;
    }

    public void ReInit (CallContext context, InputStream stream, String source_info) throws IOException {
        ReInit(new BufferedReader(new InputStreamReader(stream, "UTF-8")));
        this.source_info = source_info;
    }

    public String getPosition(CallContext context) {
        return   "[line "    + jj_input_stream.getBeginLine()
                             + (jj_input_stream.getBeginLine() != jj_input_stream.getEndLine() ? ("-" + jj_input_stream.getEndLine()) : "")
               + ", column " + jj_input_stream.getBeginColumn()
                             + (jj_input_stream.getBeginColumn() != jj_input_stream.getEndColumn() ? ("-" + jj_input_stream.getEndColumn()) : "")
               + "]";
    }

  final public String SlashedPath() throws ParseException {
                         Token token; String result; String postfix = null;
    token = jj_consume_token(XALNUM);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SLASH:
      jj_consume_token(SLASH);
      postfix = SlashedPath();
      break;
    default:
      jj_la1[0] = jj_gen;
      ;
    }
                                                         result = token.image + (postfix == null ? "" : ("/" + postfix));
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public String Quoted() throws ParseException {
                    Token token;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CHARACTER_LITERAL:
      token = jj_consume_token(CHARACTER_LITERAL);
      break;
    case STRING_LITERAL:
      token = jj_consume_token(STRING_LITERAL);
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return token.image.substring(1, token.image.length() - 1);}
    throw new Error("Missing return statement in function");
  }

  final public String UnspacedWord() throws ParseException {
                          Token token; StringBuffer value;
    value = new StringBuffer();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case XALNUM:
      token = jj_consume_token(XALNUM);
                                    value.append(token.image);
      break;
    case SLASH:
      token = jj_consume_token(SLASH);
                                    value.append(token.image);
      break;
    case ANY:
      token = jj_consume_token(ANY);
                                    value.append(token.image);
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CHARACTER_LITERAL:
      case STRING_LITERAL:
      case XALNUM:
      case SLASH:
      case ANY:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CHARACTER_LITERAL:
        token = jj_consume_token(CHARACTER_LITERAL);
                                    value.append(token.image);
        break;
      case STRING_LITERAL:
        token = jj_consume_token(STRING_LITERAL);
                                    value.append(token.image);
        break;
      case XALNUM:
        token = jj_consume_token(XALNUM);
                                    value.append(token.image);
        break;
      case SLASH:
        token = jj_consume_token(SLASH);
                                    value.append(token.image);
        break;
      case ANY:
        token = jj_consume_token(ANY);
                                    value.append(token.image);
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    {if (true) return value.toString();}
    throw new Error("Missing return statement in function");
  }

  final public String Word() throws ParseException {
                  String word;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case XALNUM:
    case SLASH:
    case ANY:
      word = UnspacedWord();
      break;
    case CHARACTER_LITERAL:
    case STRING_LITERAL:
      word = Quoted();
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return word;}
    throw new Error("Missing return statement in function");
  }

  final public void EOL() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COMMENT:
      jj_consume_token(COMMENT);
      break;
    case NL:
      jj_consume_token(NL);
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void KeyList(List<String> keys) throws ParseException {
                                    String key;
    key = Word();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[7] = jj_gen;
      ;
    }
                            keys.add(key);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COMMA:
      jj_consume_token(COMMA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[8] = jj_gen;
        ;
      }
      KeyList(keys);
      break;
    default:
      jj_la1[9] = jj_gen;
      ;
    }
  }

  final public void Definition(CallContext context, DIAConfiguration dia_configuration, DIAItem dia_item) throws ParseException {
                                                                                               String name; List<String> keys; String value = "true"; boolean is_rule = false;
    keys = new ArrayList<String>();
    name = Word();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[10] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPBRK:
      jj_consume_token(OPBRK);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[11] = jj_gen;
        ;
      }
      KeyList(keys);
      jj_consume_token(CLBRK);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[12] = jj_gen;
        ;
      }
                                                            is_rule = true;
      break;
    default:
      jj_la1[13] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COLON:
    case EQUAL:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COLON:
        jj_consume_token(COLON);
        break;
      case EQUAL:
        jj_consume_token(EQUAL);
        break;
      default:
        jj_la1[14] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[15] = jj_gen;
        ;
      }
      value = Word();
      break;
    default:
      jj_la1[16] = jj_gen;
      ;
    }
    if (is_rule) { dia_configuration.setProperty(context, name, keys, value); }
    else         { dia_item.setProperty(context, name, value); }
  }

  final public void GROCPConfiguration(CallContext context, DIAConfiguration dia_configuration, DIAItem dia_item) throws ParseException {
                                                                                                       Token token;

    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[17] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CHARACTER_LITERAL:
    case STRING_LITERAL:
    case XALNUM:
    case SLASH:
    case ANY:
      Definition(context, dia_configuration, dia_item);
      break;
    default:
      jj_la1[18] = jj_gen;
      ;
    }
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NL:
      case COMMENT:
        ;
        break;
      default:
        jj_la1[19] = jj_gen;
        break label_2;
      }
      EOL();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[20] = jj_gen;
        ;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CHARACTER_LITERAL:
      case STRING_LITERAL:
      case XALNUM:
      case SLASH:
      case ANY:
        Definition(context, dia_configuration, dia_item);
        break;
      default:
        jj_la1[21] = jj_gen;
        ;
      }
    }
    jj_consume_token(0);

  }

  /** Generated Token Manager. */
  public GROCPConfigurationParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[22];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x200,0x18,0x2220,0x2238,0x2238,0x2238,0x44,0x2,0x2,0x400,0x2,0x2,0x2,0x80,0x1800,0x2,0x1800,0x2,0x2238,0x44,0x2,0x2238,};
   }

  /** Constructor with InputStream. */
  public GROCPConfigurationParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public GROCPConfigurationParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new GROCPConfigurationParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public GROCPConfigurationParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new GROCPConfigurationParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public GROCPConfigurationParser(GROCPConfigurationParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(GROCPConfigurationParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[14];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 22; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 14; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
