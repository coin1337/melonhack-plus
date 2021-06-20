package org.reflections;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.serializers.Serializer;
import org.reflections.serializers.XmlSerializer;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.reflections.util.Utils;
import org.reflections.vfs.Vfs;
import org.slf4j.Logger;

public class Reflections {
   public static Logger log = Utils.findLogger(Reflections.class);
   protected final transient Configuration configuration;
   protected Store store;

   public Reflections(Configuration var1) {
      this.configuration = var1;
      this.store = new Store();
      if (var1.getScanners() != null && !var1.getScanners().isEmpty()) {
         Iterator var2 = var1.getScanners().iterator();

         while(var2.hasNext()) {
            Scanner var3 = (Scanner)var2.next();
            var3.setConfiguration(var1);
         }

         this.scan();
         if (var1.shouldExpandSuperTypes()) {
            this.expandSuperTypes();
         }
      }

   }

   public Reflections(String var1, Scanner... var2) {
      this(var1, var2);
   }

   public Reflections(Object... var1) {
      this((Configuration)ConfigurationBuilder.build(var1));
   }

   protected Reflections() {
      this.configuration = new ConfigurationBuilder();
      this.store = new Store();
   }

   protected void scan() {
      if (this.configuration.getUrls() != null && !this.configuration.getUrls().isEmpty()) {
         if (log != null && log.isDebugEnabled()) {
            log.debug("going to scan these urls: {}", this.configuration.getUrls());
         }

         long var1 = System.currentTimeMillis();
         int var3 = 0;
         ExecutorService var4 = this.configuration.getExecutorService();
         ArrayList var5 = new ArrayList();
         Iterator var6 = this.configuration.getUrls().iterator();

         while(var6.hasNext()) {
            URL var7 = (URL)var6.next();

            try {
               if (var4 != null) {
                  var5.add(var4.submit(() -> {
                     if (log != null) {
                        log.debug("[{}] scanning {}", Thread.currentThread().toString(), var7);
                     }

                     this.scan(var7);
                  }));
               } else {
                  this.scan(var7);
               }

               ++var3;
            } catch (ReflectionsException var10) {
               if (log != null) {
                  log.warn("could not create Vfs.Dir from url. ignoring the exception and continuing", var10);
               }
            }
         }

         if (var4 != null) {
            var6 = var5.iterator();

            while(var6.hasNext()) {
               Future var11 = (Future)var6.next();

               try {
                  var11.get();
               } catch (Exception var9) {
                  throw new RuntimeException(var9);
               }
            }
         }

         if (var4 != null) {
            var4.shutdown();
         }

         if (log != null) {
            log.info(String.format("Reflections took %d ms to scan %d urls, producing %s %s", System.currentTimeMillis() - var1, var3, producingDescription(this.store), var4 instanceof ThreadPoolExecutor ? String.format("[using %d cores]", ((ThreadPoolExecutor)var4).getMaximumPoolSize()) : ""));
         }

      } else {
         if (log != null) {
            log.warn("given scan urls are empty. set urls in the configuration");
         }

      }
   }

   private static String producingDescription(Store var0) {
      int var1 = 0;
      int var2 = 0;

      String var4;
      for(Iterator var3 = var0.keySet().iterator(); var3.hasNext(); var2 += var0.values(var4).size()) {
         var4 = (String)var3.next();
         var1 += var0.keys(var4).size();
      }

      return String.format("%d keys and %d values", var1, var2);
   }

   protected void scan(URL var1) {
      Vfs.Dir var2 = Vfs.fromURL(var1);

      try {
         Iterator var3 = var2.getFiles().iterator();

         while(var3.hasNext()) {
            Vfs.File var4 = (Vfs.File)var3.next();
            Predicate var5 = this.configuration.getInputsFilter();
            String var6 = var4.getRelativePath();
            String var7 = var6.replace('/', '.');
            if (var5 == null || var5.test(var6) || var5.test(var7)) {
               Object var8 = null;
               Iterator var9 = this.configuration.getScanners().iterator();

               while(var9.hasNext()) {
                  Scanner var10 = (Scanner)var9.next();

                  try {
                     if (var10.acceptsInput(var6) || var10.acceptsInput(var7)) {
                        var8 = var10.scan(var4, var8, this.store);
                     }
                  } catch (Exception var15) {
                     if (log != null) {
                        log.debug("could not scan file {} in url {} with scanner {}", new Object[]{var4.getRelativePath(), var1.toExternalForm(), var10.getClass().getSimpleName(), var15});
                     }
                  }
               }
            }
         }
      } finally {
         var2.close();
      }

   }

   public static Reflections collect() {
      return collect("META-INF/reflections/", (new FilterBuilder()).include(".*-reflections.xml"));
   }

   public static Reflections collect(String var0, Predicate<String> var1, Serializer... var2) {
      Object var3 = var2 != null && var2.length == 1 ? var2[0] : new XmlSerializer();
      Collection var4 = ClasspathHelper.forPackage(var0);
      if (var4.isEmpty()) {
         return null;
      } else {
         long var5 = System.currentTimeMillis();
         Reflections var7 = new Reflections();
         Iterable var8 = Vfs.findFiles(var4, var0, var1);
         Iterator var9 = var8.iterator();

         while(var9.hasNext()) {
            Vfs.File var10 = (Vfs.File)var9.next();
            InputStream var11 = null;

            try {
               var11 = var10.openInputStream();
               var7.merge(((Serializer)var3).read(var11));
            } catch (IOException var16) {
               throw new ReflectionsException("could not merge " + var10, var16);
            } finally {
               Utils.close(var11);
            }
         }

         if (log != null) {
            log.info(String.format("Reflections took %d ms to collect %d url, producing %s", System.currentTimeMillis() - var5, var4.size(), producingDescription(var7.store)));
         }

         return var7;
      }
   }

   public Reflections collect(InputStream var1) {
      try {
         this.merge(this.configuration.getSerializer().read(var1));
         if (log != null) {
            log.info("Reflections collected metadata from input stream using serializer " + this.configuration.getSerializer().getClass().getName());
         }

         return this;
      } catch (Exception var3) {
         throw new ReflectionsException("could not merge input stream", var3);
      }
   }

   public Reflections collect(File var1) {
      FileInputStream var2 = null;

      Reflections var3;
      try {
         var2 = new FileInputStream(var1);
         var3 = this.collect((InputStream)var2);
      } catch (FileNotFoundException var7) {
         throw new ReflectionsException("could not obtain input stream from file " + var1, var7);
      } finally {
         Utils.close(var2);
      }

      return var3;
   }

   public Reflections merge(Reflections var1) {
      this.store.merge(var1.store);
      return this;
   }

   public void expandSuperTypes() {
      String var1 = Utils.index(SubTypesScanner.class);
      Set var2 = this.store.keys(var1);
      var2.removeAll(this.store.values(var1));
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         Class var5 = ReflectionUtils.forName(var4, this.loaders());
         if (var5 != null) {
            this.expandSupertypes(this.store, var4, var5);
         }
      }

   }

   private void expandSupertypes(Store var1, String var2, Class<?> var3) {
      Iterator var4 = ReflectionUtils.getSuperTypes(var3).iterator();

      while(var4.hasNext()) {
         Class var5 = (Class)var4.next();
         if (var1.put(SubTypesScanner.class, var5.getName(), var2)) {
            if (log != null) {
               log.debug("expanded subtype {} -> {}", var5.getName(), var2);
            }

            this.expandSupertypes(var1, var5.getName(), var5);
         }
      }

   }

   public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> var1) {
      return ReflectionUtils.forNames(this.store.getAll(SubTypesScanner.class, var1.getName()), this.loaders());
   }

   public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> var1) {
      return this.getTypesAnnotatedWith(var1, false);
   }

   public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> var1, boolean var2) {
      Set var3 = this.store.get(TypeAnnotationsScanner.class, var1.getName());
      var3.addAll(this.getAllAnnotated(var3, var1, var2));
      return ReflectionUtils.forNames(var3, this.loaders());
   }

   public Set<Class<?>> getTypesAnnotatedWith(Annotation var1) {
      return this.getTypesAnnotatedWith(var1, false);
   }

   public Set<Class<?>> getTypesAnnotatedWith(Annotation var1, boolean var2) {
      Set var3 = this.store.get(TypeAnnotationsScanner.class, var1.annotationType().getName());
      Set var4 = Utils.filter((Collection)ReflectionUtils.forNames(var3, this.loaders()), (Predicate)ReflectionUtils.withAnnotation(var1));
      Set var5 = ReflectionUtils.forNames(Utils.filter(this.getAllAnnotated(Utils.names((Collection)var4), var1.annotationType(), var2), (var1x) -> {
         return !var3.contains(var1x);
      }), this.loaders());
      var4.addAll(var5);
      return var4;
   }

   protected Collection<String> getAllAnnotated(Collection<String> var1, Class<? extends Annotation> var2, boolean var3) {
      Set var4;
      if (var3) {
         if (var2.isAnnotationPresent(Inherited.class)) {
            var4 = this.store.get((Class)SubTypesScanner.class, (Collection)Utils.filter(var1, (var1x) -> {
               Class var2 = ReflectionUtils.forName(var1x, this.loaders());
               return var2 != null && !var2.isInterface();
            }));
            return this.store.getAllIncluding(SubTypesScanner.class, var4);
         } else {
            return var1;
         }
      } else {
         var4 = this.store.getAllIncluding(TypeAnnotationsScanner.class, var1);
         return this.store.getAllIncluding(SubTypesScanner.class, var4);
      }
   }

   public Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> var1) {
      return Utils.getMethodsFromDescriptors(this.store.get(MethodAnnotationsScanner.class, var1.getName()), this.loaders());
   }

   public Set<Method> getMethodsAnnotatedWith(Annotation var1) {
      return Utils.filter((Collection)this.getMethodsAnnotatedWith(var1.annotationType()), (Predicate)ReflectionUtils.withAnnotation(var1));
   }

   public Set<Method> getMethodsMatchParams(Class<?>... var1) {
      return Utils.getMethodsFromDescriptors(this.store.get(MethodParameterScanner.class, Utils.names(var1).toString()), this.loaders());
   }

   public Set<Method> getMethodsReturn(Class var1) {
      return Utils.getMethodsFromDescriptors(this.store.get((Class)MethodParameterScanner.class, (Collection)Utils.names(var1)), this.loaders());
   }

   public Set<Method> getMethodsWithAnyParamAnnotated(Class<? extends Annotation> var1) {
      return Utils.getMethodsFromDescriptors(this.store.get(MethodParameterScanner.class, var1.getName()), this.loaders());
   }

   public Set<Method> getMethodsWithAnyParamAnnotated(Annotation var1) {
      return Utils.filter((Collection)this.getMethodsWithAnyParamAnnotated(var1.annotationType()), (Predicate)ReflectionUtils.withAnyParameterAnnotation(var1));
   }

   public Set<Constructor> getConstructorsAnnotatedWith(Class<? extends Annotation> var1) {
      return Utils.getConstructorsFromDescriptors(this.store.get(MethodAnnotationsScanner.class, var1.getName()), this.loaders());
   }

   public Set<Constructor> getConstructorsAnnotatedWith(Annotation var1) {
      return Utils.filter((Collection)this.getConstructorsAnnotatedWith(var1.annotationType()), (Predicate)ReflectionUtils.withAnnotation(var1));
   }

   public Set<Constructor> getConstructorsMatchParams(Class<?>... var1) {
      return Utils.getConstructorsFromDescriptors(this.store.get(MethodParameterScanner.class, Utils.names(var1).toString()), this.loaders());
   }

   public Set<Constructor> getConstructorsWithAnyParamAnnotated(Class<? extends Annotation> var1) {
      return Utils.getConstructorsFromDescriptors(this.store.get(MethodParameterScanner.class, var1.getName()), this.loaders());
   }

   public Set<Constructor> getConstructorsWithAnyParamAnnotated(Annotation var1) {
      return Utils.filter((Collection)this.getConstructorsWithAnyParamAnnotated(var1.annotationType()), (Predicate)ReflectionUtils.withAnyParameterAnnotation(var1));
   }

   public Set<Field> getFieldsAnnotatedWith(Class<? extends Annotation> var1) {
      return (Set)this.store.get(FieldAnnotationsScanner.class, var1.getName()).stream().map((var1x) -> {
         return Utils.getFieldFromString(var1x, this.loaders());
      }).collect(Collectors.toSet());
   }

   public Set<Field> getFieldsAnnotatedWith(Annotation var1) {
      return Utils.filter((Collection)this.getFieldsAnnotatedWith(var1.annotationType()), (Predicate)ReflectionUtils.withAnnotation(var1));
   }

   public Set<String> getResources(Predicate<String> var1) {
      Set var2 = Utils.filter((Collection)this.store.keys(Utils.index(ResourcesScanner.class)), (Predicate)var1);
      return this.store.get((Class)ResourcesScanner.class, (Collection)var2);
   }

   public Set<String> getResources(Pattern var1) {
      return this.getResources((var1x) -> {
         return var1.matcher(var1x).matches();
      });
   }

   public List<String> getMethodParamNames(Method var1) {
      Set var2 = this.store.get(MethodParameterNamesScanner.class, Utils.name(var1));
      return var2.size() == 1 ? Arrays.asList(((String)var2.iterator().next()).split(", ")) : Collections.emptyList();
   }

   public List<String> getConstructorParamNames(Constructor var1) {
      Set var2 = this.store.get(MethodParameterNamesScanner.class, Utils.name(var1));
      return var2.size() == 1 ? Arrays.asList(((String)var2.iterator().next()).split(", ")) : Collections.emptyList();
   }

   public Set<Member> getFieldUsage(Field var1) {
      return Utils.getMembersFromDescriptors(this.store.get(MemberUsageScanner.class, Utils.name(var1)));
   }

   public Set<Member> getMethodUsage(Method var1) {
      return Utils.getMembersFromDescriptors(this.store.get(MemberUsageScanner.class, Utils.name(var1)));
   }

   public Set<Member> getConstructorUsage(Constructor var1) {
      return Utils.getMembersFromDescriptors(this.store.get(MemberUsageScanner.class, Utils.name(var1)));
   }

   public Set<String> getAllTypes() {
      HashSet var1 = new HashSet(this.store.getAll(SubTypesScanner.class, Object.class.getName()));
      if (var1.isEmpty()) {
         throw new ReflectionsException("Couldn't find subtypes of Object. Make sure SubTypesScanner initialized to include Object class - new SubTypesScanner(false)");
      } else {
         return var1;
      }
   }

   public Store getStore() {
      return this.store;
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public File save(String var1) {
      return this.save(var1, this.configuration.getSerializer());
   }

   public File save(String var1, Serializer var2) {
      File var3 = var2.save(this, var1);
      if (log != null) {
         log.info("Reflections successfully saved in " + var3.getAbsolutePath() + " using " + var2.getClass().getSimpleName());
      }

      return var3;
   }

   private ClassLoader[] loaders() {
      return this.configuration.getClassLoaders();
   }
}
