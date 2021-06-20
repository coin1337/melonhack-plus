package org.yaml.snakeyaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.emitter.Emitable;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

public class Yaml {
   protected final Resolver resolver;
   private String name;
   protected BaseConstructor constructor;
   protected Representer representer;
   protected DumperOptions dumperOptions;
   protected LoaderOptions loadingConfig;

   public Yaml() {
      this(new Constructor(), new Representer(), new DumperOptions(), new LoaderOptions(), new Resolver());
   }

   public Yaml(DumperOptions var1) {
      this(new Constructor(), new Representer(), var1);
   }

   public Yaml(LoaderOptions var1) {
      this(new Constructor(), new Representer(), new DumperOptions(), (LoaderOptions)var1);
   }

   public Yaml(Representer var1) {
      this((BaseConstructor)(new Constructor()), (Representer)var1);
   }

   public Yaml(BaseConstructor var1) {
      this(var1, new Representer());
   }

   public Yaml(BaseConstructor var1, Representer var2) {
      this(var1, var2, initDumperOptions(var2));
   }

   private static DumperOptions initDumperOptions(Representer var0) {
      DumperOptions var1 = new DumperOptions();
      var1.setDefaultFlowStyle(var0.getDefaultFlowStyle());
      var1.setDefaultScalarStyle(var0.getDefaultScalarStyle());
      var1.setAllowReadOnlyProperties(var0.getPropertyUtils().isAllowReadOnlyProperties());
      var1.setTimeZone(var0.getTimeZone());
      return var1;
   }

   public Yaml(Representer var1, DumperOptions var2) {
      this(new Constructor(), var1, var2, new LoaderOptions(), new Resolver());
   }

   public Yaml(BaseConstructor var1, Representer var2, DumperOptions var3) {
      this(var1, var2, var3, new LoaderOptions(), new Resolver());
   }

   public Yaml(BaseConstructor var1, Representer var2, DumperOptions var3, LoaderOptions var4) {
      this(var1, var2, var3, var4, new Resolver());
   }

   public Yaml(BaseConstructor var1, Representer var2, DumperOptions var3, Resolver var4) {
      this(var1, var2, var3, new LoaderOptions(), var4);
   }

   public Yaml(BaseConstructor var1, Representer var2, DumperOptions var3, LoaderOptions var4, Resolver var5) {
      if (!var1.isExplicitPropertyUtils()) {
         var1.setPropertyUtils(var2.getPropertyUtils());
      } else if (!var2.isExplicitPropertyUtils()) {
         var2.setPropertyUtils(var1.getPropertyUtils());
      }

      this.constructor = var1;
      this.constructor.setAllowDuplicateKeys(var4.isAllowDuplicateKeys());
      if (var3.getIndent() <= var3.getIndicatorIndent()) {
         throw new YAMLException("Indicator indent must be smaller then indent.");
      } else {
         var2.setDefaultFlowStyle(var3.getDefaultFlowStyle());
         var2.setDefaultScalarStyle(var3.getDefaultScalarStyle());
         var2.getPropertyUtils().setAllowReadOnlyProperties(var3.isAllowReadOnlyProperties());
         var2.setTimeZone(var3.getTimeZone());
         this.representer = var2;
         this.dumperOptions = var3;
         this.loadingConfig = var4;
         this.resolver = var5;
         this.name = "Yaml:" + System.identityHashCode(this);
      }
   }

   public String dump(Object var1) {
      ArrayList var2 = new ArrayList(1);
      var2.add(var1);
      return this.dumpAll(var2.iterator());
   }

   public Node represent(Object var1) {
      return this.representer.represent(var1);
   }

   public String dumpAll(Iterator<? extends Object> var1) {
      StringWriter var2 = new StringWriter();
      this.dumpAll(var1, var2, (Tag)null);
      return var2.toString();
   }

   public void dump(Object var1, Writer var2) {
      ArrayList var3 = new ArrayList(1);
      var3.add(var1);
      this.dumpAll(var3.iterator(), var2, (Tag)null);
   }

   public void dumpAll(Iterator<? extends Object> var1, Writer var2) {
      this.dumpAll(var1, var2, (Tag)null);
   }

   private void dumpAll(Iterator<? extends Object> var1, Writer var2, Tag var3) {
      Serializer var4 = new Serializer(new Emitter(var2, this.dumperOptions), this.resolver, this.dumperOptions, var3);

      try {
         var4.open();

         while(var1.hasNext()) {
            Node var5 = this.representer.represent(var1.next());
            var4.serialize(var5);
         }

         var4.close();
      } catch (IOException var6) {
         throw new YAMLException(var6);
      }
   }

   public String dumpAs(Object var1, Tag var2, DumperOptions.FlowStyle var3) {
      DumperOptions.FlowStyle var4 = this.representer.getDefaultFlowStyle();
      if (var3 != null) {
         this.representer.setDefaultFlowStyle(var3);
      }

      ArrayList var5 = new ArrayList(1);
      var5.add(var1);
      StringWriter var6 = new StringWriter();
      this.dumpAll(var5.iterator(), var6, var2);
      this.representer.setDefaultFlowStyle(var4);
      return var6.toString();
   }

   public String dumpAsMap(Object var1) {
      return this.dumpAs(var1, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
   }

   public List<Event> serialize(Node var1) {
      Yaml.SilentEmitter var2 = new Yaml.SilentEmitter();
      Serializer var3 = new Serializer(var2, this.resolver, this.dumperOptions, (Tag)null);

      try {
         var3.open();
         var3.serialize(var1);
         var3.close();
      } catch (IOException var5) {
         throw new YAMLException(var5);
      }

      return var2.getEvents();
   }

   public <T> T load(String var1) {
      return this.loadFromReader(new StreamReader(var1), Object.class);
   }

   public <T> T load(InputStream var1) {
      return this.loadFromReader(new StreamReader(new UnicodeReader(var1)), Object.class);
   }

   public <T> T load(Reader var1) {
      return this.loadFromReader(new StreamReader(var1), Object.class);
   }

   public <T> T loadAs(Reader var1, Class<T> var2) {
      return this.loadFromReader(new StreamReader(var1), var2);
   }

   public <T> T loadAs(String var1, Class<T> var2) {
      return this.loadFromReader(new StreamReader(var1), var2);
   }

   public <T> T loadAs(InputStream var1, Class<T> var2) {
      return this.loadFromReader(new StreamReader(new UnicodeReader(var1)), var2);
   }

   private Object loadFromReader(StreamReader var1, Class<?> var2) {
      Composer var3 = new Composer(new ParserImpl(var1), this.resolver);
      this.constructor.setComposer(var3);
      return this.constructor.getSingleData(var2);
   }

   public Iterable<Object> loadAll(Reader var1) {
      Composer var2 = new Composer(new ParserImpl(new StreamReader(var1)), this.resolver);
      this.constructor.setComposer(var2);
      Iterator var3 = new Iterator<Object>() {
         public boolean hasNext() {
            return Yaml.this.constructor.checkData();
         }

         public Object next() {
            return Yaml.this.constructor.getData();
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
      return new Yaml.YamlIterable(var3);
   }

   public Iterable<Object> loadAll(String var1) {
      return this.loadAll((Reader)(new StringReader(var1)));
   }

   public Iterable<Object> loadAll(InputStream var1) {
      return this.loadAll((Reader)(new UnicodeReader(var1)));
   }

   public Node compose(Reader var1) {
      Composer var2 = new Composer(new ParserImpl(new StreamReader(var1)), this.resolver);
      this.constructor.setComposer(var2);
      return var2.getSingleNode();
   }

   public Iterable<Node> composeAll(Reader var1) {
      final Composer var2 = new Composer(new ParserImpl(new StreamReader(var1)), this.resolver);
      this.constructor.setComposer(var2);
      Iterator var3 = new Iterator<Node>() {
         public boolean hasNext() {
            return var2.checkNode();
         }

         public Node next() {
            return var2.getNode();
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
      return new Yaml.NodeIterable(var3);
   }

   public void addImplicitResolver(Tag var1, Pattern var2, String var3) {
      this.resolver.addImplicitResolver(var1, var2, var3);
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public Iterable<Event> parse(Reader var1) {
      final ParserImpl var2 = new ParserImpl(new StreamReader(var1));
      Iterator var3 = new Iterator<Event>() {
         public boolean hasNext() {
            return var2.peekEvent() != null;
         }

         public Event next() {
            return var2.getEvent();
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
      return new Yaml.EventIterable(var3);
   }

   public void setBeanAccess(BeanAccess var1) {
      this.constructor.getPropertyUtils().setBeanAccess(var1);
      this.representer.getPropertyUtils().setBeanAccess(var1);
   }

   public void addTypeDescription(TypeDescription var1) {
      this.constructor.addTypeDescription(var1);
      this.representer.addTypeDescription(var1);
   }

   private static class EventIterable implements Iterable<Event> {
      private Iterator<Event> iterator;

      public EventIterable(Iterator<Event> var1) {
         this.iterator = var1;
      }

      public Iterator<Event> iterator() {
         return this.iterator;
      }
   }

   private static class NodeIterable implements Iterable<Node> {
      private Iterator<Node> iterator;

      public NodeIterable(Iterator<Node> var1) {
         this.iterator = var1;
      }

      public Iterator<Node> iterator() {
         return this.iterator;
      }
   }

   private static class YamlIterable implements Iterable<Object> {
      private Iterator<Object> iterator;

      public YamlIterable(Iterator<Object> var1) {
         this.iterator = var1;
      }

      public Iterator<Object> iterator() {
         return this.iterator;
      }
   }

   private static class SilentEmitter implements Emitable {
      private List<Event> events;

      private SilentEmitter() {
         this.events = new ArrayList(100);
      }

      public List<Event> getEvents() {
         return this.events;
      }

      public void emit(Event var1) throws IOException {
         this.events.add(var1);
      }

      // $FF: synthetic method
      SilentEmitter(Object var1) {
         this();
      }
   }
}
