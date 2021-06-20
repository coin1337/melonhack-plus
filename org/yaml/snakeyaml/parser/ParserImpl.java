package org.yaml.snakeyaml.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.util.ArrayStack;

public class ParserImpl implements Parser {
   private static final Map<String, String> DEFAULT_TAGS = new HashMap();
   protected final Scanner scanner;
   private Event currentEvent;
   private final ArrayStack<Production> states;
   private final ArrayStack<Mark> marks;
   private Production state;
   private VersionTagsTuple directives;

   public ParserImpl(StreamReader var1) {
      this((Scanner)(new ScannerImpl(var1)));
   }

   public ParserImpl(Scanner var1) {
      this.scanner = var1;
      this.currentEvent = null;
      this.directives = new VersionTagsTuple((DumperOptions.Version)null, new HashMap(DEFAULT_TAGS));
      this.states = new ArrayStack(100);
      this.marks = new ArrayStack(10);
      this.state = new ParserImpl.ParseStreamStart();
   }

   public boolean checkEvent(Event.ID var1) {
      this.peekEvent();
      return this.currentEvent != null && this.currentEvent.is(var1);
   }

   public Event peekEvent() {
      if (this.currentEvent == null && this.state != null) {
         this.currentEvent = this.state.produce();
      }

      return this.currentEvent;
   }

   public Event getEvent() {
      this.peekEvent();
      Event var1 = this.currentEvent;
      this.currentEvent = null;
      return var1;
   }

   private VersionTagsTuple processDirectives() {
      DumperOptions.Version var1 = null;
      HashMap var2 = new HashMap();

      while(this.scanner.checkToken(Token.ID.Directive)) {
         DirectiveToken var3 = (DirectiveToken)this.scanner.getToken();
         List var4;
         if (var3.getName().equals("YAML")) {
            if (var1 != null) {
               throw new ParserException((String)null, (Mark)null, "found duplicate YAML directive", var3.getStartMark());
            }

            var4 = var3.getValue();
            Integer var5 = (Integer)var4.get(0);
            if (var5 != 1) {
               throw new ParserException((String)null, (Mark)null, "found incompatible YAML document (version 1.* is required)", var3.getStartMark());
            }

            Integer var6 = (Integer)var4.get(1);
            switch(var6) {
            case 0:
               var1 = DumperOptions.Version.V1_0;
               break;
            default:
               var1 = DumperOptions.Version.V1_1;
            }
         } else if (var3.getName().equals("TAG")) {
            var4 = var3.getValue();
            String var9 = (String)var4.get(0);
            String var10 = (String)var4.get(1);
            if (var2.containsKey(var9)) {
               throw new ParserException((String)null, (Mark)null, "duplicate tag handle " + var9, var3.getStartMark());
            }

            var2.put(var9, var10);
         }
      }

      if (var1 != null || !var2.isEmpty()) {
         Iterator var7 = DEFAULT_TAGS.keySet().iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();
            if (!var2.containsKey(var8)) {
               var2.put(var8, DEFAULT_TAGS.get(var8));
            }
         }

         this.directives = new VersionTagsTuple(var1, var2);
      }

      return this.directives;
   }

   private Event parseFlowNode() {
      return this.parseNode(false, false);
   }

   private Event parseBlockNodeOrIndentlessSequence() {
      return this.parseNode(true, true);
   }

   private Event parseNode(boolean var1, boolean var2) {
      Mark var4 = null;
      Mark var5 = null;
      Mark var6 = null;
      Object var3;
      if (this.scanner.checkToken(Token.ID.Alias)) {
         AliasToken var7 = (AliasToken)this.scanner.getToken();
         var3 = new AliasEvent(var7.getValue(), var7.getStartMark(), var7.getEndMark());
         this.state = (Production)this.states.pop();
      } else {
         String var13 = null;
         TagTuple var8 = null;
         if (this.scanner.checkToken(Token.ID.Anchor)) {
            AnchorToken var9 = (AnchorToken)this.scanner.getToken();
            var4 = var9.getStartMark();
            var5 = var9.getEndMark();
            var13 = var9.getValue();
            if (this.scanner.checkToken(Token.ID.Tag)) {
               TagToken var10 = (TagToken)this.scanner.getToken();
               var6 = var10.getStartMark();
               var5 = var10.getEndMark();
               var8 = var10.getValue();
            }
         } else if (this.scanner.checkToken(Token.ID.Tag)) {
            TagToken var14 = (TagToken)this.scanner.getToken();
            var4 = var14.getStartMark();
            var6 = var4;
            var5 = var14.getEndMark();
            var8 = var14.getValue();
            if (this.scanner.checkToken(Token.ID.Anchor)) {
               AnchorToken var16 = (AnchorToken)this.scanner.getToken();
               var5 = var16.getEndMark();
               var13 = var16.getValue();
            }
         }

         String var15 = null;
         String var11;
         if (var8 != null) {
            String var17 = var8.getHandle();
            var11 = var8.getSuffix();
            if (var17 != null) {
               if (!this.directives.getTags().containsKey(var17)) {
                  throw new ParserException("while parsing a node", var4, "found undefined tag handle " + var17, var6);
               }

               var15 = (String)this.directives.getTags().get(var17) + var11;
            } else {
               var15 = var11;
            }
         }

         if (var4 == null) {
            var4 = this.scanner.peekToken().getStartMark();
            var5 = var4;
         }

         var3 = null;
         boolean var18 = var15 == null || var15.equals("!");
         if (var2 && this.scanner.checkToken(Token.ID.BlockEntry)) {
            var5 = this.scanner.peekToken().getEndMark();
            var3 = new SequenceStartEvent(var13, var15, var18, var4, var5, Boolean.FALSE);
            this.state = new ParserImpl.ParseIndentlessSequenceEntry();
         } else if (this.scanner.checkToken(Token.ID.Scalar)) {
            ScalarToken var19 = (ScalarToken)this.scanner.getToken();
            var5 = var19.getEndMark();
            ImplicitTuple var12;
            if ((!var19.getPlain() || var15 != null) && !"!".equals(var15)) {
               if (var15 == null) {
                  var12 = new ImplicitTuple(false, true);
               } else {
                  var12 = new ImplicitTuple(false, false);
               }
            } else {
               var12 = new ImplicitTuple(true, false);
            }

            var3 = new ScalarEvent(var13, var15, var12, var19.getValue(), var4, var5, var19.getStyle());
            this.state = (Production)this.states.pop();
         } else if (this.scanner.checkToken(Token.ID.FlowSequenceStart)) {
            var5 = this.scanner.peekToken().getEndMark();
            var3 = new SequenceStartEvent(var13, var15, var18, var4, var5, Boolean.TRUE);
            this.state = new ParserImpl.ParseFlowSequenceFirstEntry();
         } else if (this.scanner.checkToken(Token.ID.FlowMappingStart)) {
            var5 = this.scanner.peekToken().getEndMark();
            var3 = new MappingStartEvent(var13, var15, var18, var4, var5, Boolean.TRUE);
            this.state = new ParserImpl.ParseFlowMappingFirstKey();
         } else if (var1 && this.scanner.checkToken(Token.ID.BlockSequenceStart)) {
            var5 = this.scanner.peekToken().getStartMark();
            var3 = new SequenceStartEvent(var13, var15, var18, var4, var5, Boolean.FALSE);
            this.state = new ParserImpl.ParseBlockSequenceFirstEntry();
         } else if (var1 && this.scanner.checkToken(Token.ID.BlockMappingStart)) {
            var5 = this.scanner.peekToken().getStartMark();
            var3 = new MappingStartEvent(var13, var15, var18, var4, var5, Boolean.FALSE);
            this.state = new ParserImpl.ParseBlockMappingFirstKey();
         } else {
            if (var13 == null && var15 == null) {
               if (var1) {
                  var11 = "block";
               } else {
                  var11 = "flow";
               }

               Token var20 = this.scanner.peekToken();
               throw new ParserException("while parsing a " + var11 + " node", var4, "expected the node content, but found " + var20.getTokenId(), var20.getStartMark());
            }

            var3 = new ScalarEvent(var13, var15, new ImplicitTuple(var18, false), "", var4, var5, '\u0000');
            this.state = (Production)this.states.pop();
         }
      }

      return (Event)var3;
   }

   private Event processEmptyScalar(Mark var1) {
      return new ScalarEvent((String)null, (String)null, new ImplicitTuple(true, false), "", var1, var1, '\u0000');
   }

   static {
      DEFAULT_TAGS.put("!", "!");
      DEFAULT_TAGS.put("!!", "tag:yaml.org,2002:");
   }

   private class ParseFlowMappingEmptyValue implements Production {
      private ParseFlowMappingEmptyValue() {
      }

      public Event produce() {
         ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingKey(false);
         return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
      }

      // $FF: synthetic method
      ParseFlowMappingEmptyValue(Object var2) {
         this();
      }
   }

   private class ParseFlowMappingValue implements Production {
      private ParseFlowMappingValue() {
      }

      public Event produce() {
         Token var1;
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            var1 = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingKey(false));
               return ParserImpl.this.parseFlowNode();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingKey(false);
               return ParserImpl.this.processEmptyScalar(var1.getEndMark());
            }
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingKey(false);
            var1 = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(var1.getStartMark());
         }
      }

      // $FF: synthetic method
      ParseFlowMappingValue(Object var2) {
         this();
      }
   }

   private class ParseFlowMappingKey implements Production {
      private boolean first = false;

      public ParseFlowMappingKey(boolean var2) {
         this.first = var2;
      }

      public Event produce() {
         Token var1;
         if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
            if (!this.first) {
               if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                  var1 = ParserImpl.this.scanner.peekToken();
                  throw new ParserException("while parsing a flow mapping", (Mark)ParserImpl.this.marks.pop(), "expected ',' or '}', but got " + var1.getTokenId(), var1.getStartMark());
               }

               ParserImpl.this.scanner.getToken();
            }

            if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
               var1 = ParserImpl.this.scanner.getToken();
               if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
                  ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingValue());
                  return ParserImpl.this.parseFlowNode();
               }

               ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingValue();
               return ParserImpl.this.processEmptyScalar(var1.getEndMark());
            }

            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingEmptyValue());
               return ParserImpl.this.parseFlowNode();
            }
         }

         var1 = ParserImpl.this.scanner.getToken();
         MappingEndEvent var2 = new MappingEndEvent(var1.getStartMark(), var1.getEndMark());
         ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
         ParserImpl.this.marks.pop();
         return var2;
      }
   }

   private class ParseFlowMappingFirstKey implements Production {
      private ParseFlowMappingFirstKey() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(var1.getStartMark());
         return (ParserImpl.this.new ParseFlowMappingKey(true)).produce();
      }

      // $FF: synthetic method
      ParseFlowMappingFirstKey(Object var2) {
         this();
      }
   }

   private class ParseFlowSequenceEntryMappingEnd implements Production {
      private ParseFlowSequenceEntryMappingEnd() {
      }

      public Event produce() {
         ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntry(false);
         Token var1 = ParserImpl.this.scanner.peekToken();
         return new MappingEndEvent(var1.getStartMark(), var1.getEndMark());
      }

      // $FF: synthetic method
      ParseFlowSequenceEntryMappingEnd(Object var2) {
         this();
      }
   }

   private class ParseFlowSequenceEntryMappingValue implements Production {
      private ParseFlowSequenceEntryMappingValue() {
      }

      public Event produce() {
         Token var1;
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            var1 = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntryMappingEnd());
               return ParserImpl.this.parseFlowNode();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingEnd();
               return ParserImpl.this.processEmptyScalar(var1.getEndMark());
            }
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingEnd();
            var1 = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(var1.getStartMark());
         }
      }

      // $FF: synthetic method
      ParseFlowSequenceEntryMappingValue(Object var2) {
         this();
      }
   }

   private class ParseFlowSequenceEntryMappingKey implements Production {
      private ParseFlowSequenceEntryMappingKey() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.getToken();
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntryMappingValue());
            return ParserImpl.this.parseFlowNode();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingValue();
            return ParserImpl.this.processEmptyScalar(var1.getEndMark());
         }
      }

      // $FF: synthetic method
      ParseFlowSequenceEntryMappingKey(Object var2) {
         this();
      }
   }

   private class ParseFlowSequenceEntry implements Production {
      private boolean first = false;

      public ParseFlowSequenceEntry(boolean var2) {
         this.first = var2;
      }

      public Event produce() {
         Token var1;
         if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
            if (!this.first) {
               if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                  var1 = ParserImpl.this.scanner.peekToken();
                  throw new ParserException("while parsing a flow sequence", (Mark)ParserImpl.this.marks.pop(), "expected ',' or ']', but got " + var1.getTokenId(), var1.getStartMark());
               }

               ParserImpl.this.scanner.getToken();
            }

            if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
               var1 = ParserImpl.this.scanner.peekToken();
               MappingStartEvent var3 = new MappingStartEvent((String)null, (String)null, true, var1.getStartMark(), var1.getEndMark(), Boolean.TRUE);
               ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingKey();
               return var3;
            }

            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntry(false));
               return ParserImpl.this.parseFlowNode();
            }
         }

         var1 = ParserImpl.this.scanner.getToken();
         SequenceEndEvent var2 = new SequenceEndEvent(var1.getStartMark(), var1.getEndMark());
         ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
         ParserImpl.this.marks.pop();
         return var2;
      }
   }

   private class ParseFlowSequenceFirstEntry implements Production {
      private ParseFlowSequenceFirstEntry() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(var1.getStartMark());
         return (ParserImpl.this.new ParseFlowSequenceEntry(true)).produce();
      }

      // $FF: synthetic method
      ParseFlowSequenceFirstEntry(Object var2) {
         this();
      }
   }

   private class ParseBlockMappingValue implements Production {
      private ParseBlockMappingValue() {
      }

      public Event produce() {
         Token var1;
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            var1 = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingKey());
               return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingKey();
               return ParserImpl.this.processEmptyScalar(var1.getEndMark());
            }
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingKey();
            var1 = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(var1.getStartMark());
         }
      }

      // $FF: synthetic method
      ParseBlockMappingValue(Object var2) {
         this();
      }
   }

   private class ParseBlockMappingKey implements Production {
      private ParseBlockMappingKey() {
      }

      public Event produce() {
         Token var1;
         if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
            var1 = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingValue());
               return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingValue();
               return ParserImpl.this.processEmptyScalar(var1.getEndMark());
            }
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
            var1 = ParserImpl.this.scanner.peekToken();
            throw new ParserException("while parsing a block mapping", (Mark)ParserImpl.this.marks.pop(), "expected <block end>, but found " + var1.getTokenId(), var1.getStartMark());
         } else {
            var1 = ParserImpl.this.scanner.getToken();
            MappingEndEvent var2 = new MappingEndEvent(var1.getStartMark(), var1.getEndMark());
            ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
            ParserImpl.this.marks.pop();
            return var2;
         }
      }

      // $FF: synthetic method
      ParseBlockMappingKey(Object var2) {
         this();
      }
   }

   private class ParseBlockMappingFirstKey implements Production {
      private ParseBlockMappingFirstKey() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(var1.getStartMark());
         return (ParserImpl.this.new ParseBlockMappingKey()).produce();
      }

      // $FF: synthetic method
      ParseBlockMappingFirstKey(Object var2) {
         this();
      }
   }

   private class ParseIndentlessSequenceEntry implements Production {
      private ParseIndentlessSequenceEntry() {
      }

      public Event produce() {
         Token var1;
         if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
            var1 = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseIndentlessSequenceEntry());
               return (ParserImpl.this.new ParseBlockNode()).produce();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseIndentlessSequenceEntry();
               return ParserImpl.this.processEmptyScalar(var1.getEndMark());
            }
         } else {
            var1 = ParserImpl.this.scanner.peekToken();
            SequenceEndEvent var2 = new SequenceEndEvent(var1.getStartMark(), var1.getEndMark());
            ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
            return var2;
         }
      }

      // $FF: synthetic method
      ParseIndentlessSequenceEntry(Object var2) {
         this();
      }
   }

   private class ParseBlockSequenceEntry implements Production {
      private ParseBlockSequenceEntry() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
            BlockEntryToken var3 = (BlockEntryToken)ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.BlockEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseBlockSequenceEntry());
               return (ParserImpl.this.new ParseBlockNode()).produce();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseBlockSequenceEntry();
               return ParserImpl.this.processEmptyScalar(var3.getEndMark());
            }
         } else {
            Token var1;
            if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
               var1 = ParserImpl.this.scanner.peekToken();
               throw new ParserException("while parsing a block collection", (Mark)ParserImpl.this.marks.pop(), "expected <block end>, but found " + var1.getTokenId(), var1.getStartMark());
            } else {
               var1 = ParserImpl.this.scanner.getToken();
               SequenceEndEvent var2 = new SequenceEndEvent(var1.getStartMark(), var1.getEndMark());
               ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
               ParserImpl.this.marks.pop();
               return var2;
            }
         }
      }

      // $FF: synthetic method
      ParseBlockSequenceEntry(Object var2) {
         this();
      }
   }

   private class ParseBlockSequenceFirstEntry implements Production {
      private ParseBlockSequenceFirstEntry() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(var1.getStartMark());
         return (ParserImpl.this.new ParseBlockSequenceEntry()).produce();
      }

      // $FF: synthetic method
      ParseBlockSequenceFirstEntry(Object var2) {
         this();
      }
   }

   private class ParseBlockNode implements Production {
      private ParseBlockNode() {
      }

      public Event produce() {
         return ParserImpl.this.parseNode(true, false);
      }

      // $FF: synthetic method
      ParseBlockNode(Object var2) {
         this();
      }
   }

   private class ParseDocumentContent implements Production {
      private ParseDocumentContent() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.DocumentEnd, Token.ID.StreamEnd)) {
            Event var1 = ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
            ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
            return var1;
         } else {
            ParserImpl.ParseBlockNode var2 = ParserImpl.this.new ParseBlockNode();
            return var2.produce();
         }
      }

      // $FF: synthetic method
      ParseDocumentContent(Object var2) {
         this();
      }
   }

   private class ParseDocumentEnd implements Production {
      private ParseDocumentEnd() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.peekToken();
         Mark var2 = var1.getStartMark();
         Mark var3 = var2;
         boolean var4 = false;
         if (ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
            var1 = ParserImpl.this.scanner.getToken();
            var3 = var1.getEndMark();
            var4 = true;
         }

         DocumentEndEvent var5 = new DocumentEndEvent(var2, var3, var4);
         ParserImpl.this.state = ParserImpl.this.new ParseDocumentStart();
         return var5;
      }

      // $FF: synthetic method
      ParseDocumentEnd(Object var2) {
         this();
      }
   }

   private class ParseDocumentStart implements Production {
      private ParseDocumentStart() {
      }

      public Event produce() {
         while(ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
            ParserImpl.this.scanner.getToken();
         }

         Object var1;
         if (!ParserImpl.this.scanner.checkToken(Token.ID.StreamEnd)) {
            Token var2 = ParserImpl.this.scanner.peekToken();
            Mark var3 = var2.getStartMark();
            VersionTagsTuple var4 = ParserImpl.this.processDirectives();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.DocumentStart)) {
               throw new ParserException((String)null, (Mark)null, "expected '<document start>', but found " + ParserImpl.this.scanner.peekToken().getTokenId(), ParserImpl.this.scanner.peekToken().getStartMark());
            }

            var2 = ParserImpl.this.scanner.getToken();
            Mark var5 = var2.getEndMark();
            var1 = new DocumentStartEvent(var3, var5, true, var4.getVersion(), var4.getTags());
            ParserImpl.this.states.push(ParserImpl.this.new ParseDocumentEnd());
            ParserImpl.this.state = ParserImpl.this.new ParseDocumentContent();
         } else {
            StreamEndToken var6 = (StreamEndToken)ParserImpl.this.scanner.getToken();
            var1 = new StreamEndEvent(var6.getStartMark(), var6.getEndMark());
            if (!ParserImpl.this.states.isEmpty()) {
               throw new YAMLException("Unexpected end of stream. States left: " + ParserImpl.this.states);
            }

            if (!ParserImpl.this.marks.isEmpty()) {
               throw new YAMLException("Unexpected end of stream. Marks left: " + ParserImpl.this.marks);
            }

            ParserImpl.this.state = null;
         }

         return (Event)var1;
      }

      // $FF: synthetic method
      ParseDocumentStart(Object var2) {
         this();
      }
   }

   private class ParseImplicitDocumentStart implements Production {
      private ParseImplicitDocumentStart() {
      }

      public Event produce() {
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.StreamEnd)) {
            ParserImpl.this.directives = new VersionTagsTuple((DumperOptions.Version)null, ParserImpl.DEFAULT_TAGS);
            Token var5 = ParserImpl.this.scanner.peekToken();
            Mark var2 = var5.getStartMark();
            DocumentStartEvent var4 = new DocumentStartEvent(var2, var2, false, (DumperOptions.Version)null, (Map)null);
            ParserImpl.this.states.push(ParserImpl.this.new ParseDocumentEnd());
            ParserImpl.this.state = ParserImpl.this.new ParseBlockNode();
            return var4;
         } else {
            ParserImpl.ParseDocumentStart var1 = ParserImpl.this.new ParseDocumentStart();
            return var1.produce();
         }
      }

      // $FF: synthetic method
      ParseImplicitDocumentStart(Object var2) {
         this();
      }
   }

   private class ParseStreamStart implements Production {
      private ParseStreamStart() {
      }

      public Event produce() {
         StreamStartToken var1 = (StreamStartToken)ParserImpl.this.scanner.getToken();
         StreamStartEvent var2 = new StreamStartEvent(var1.getStartMark(), var1.getEndMark());
         ParserImpl.this.state = ParserImpl.this.new ParseImplicitDocumentStart();
         return var2;
      }

      // $FF: synthetic method
      ParseStreamStart(Object var2) {
         this();
      }
   }
}
