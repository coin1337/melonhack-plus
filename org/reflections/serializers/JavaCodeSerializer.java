package org.reflections.serializers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.Utils;

public class JavaCodeSerializer implements Serializer {
   private static final String pathSeparator = "_";
   private static final String doubleSeparator = "__";
   private static final String dotSeparator = ".";
   private static final String arrayDescriptor = "$$";
   private static final String tokenSeparator = "_";

   public Reflections read(InputStream var1) {
      throw new UnsupportedOperationException("read is not implemented on JavaCodeSerializer");
   }

   public File save(Reflections var1, String var2) {
      if (var2.endsWith("/")) {
         var2 = var2.substring(0, var2.length() - 1);
      }

      String var3 = var2.replace('.', '/').concat(".java");
      File var4 = Utils.prepareFile(var3);
      int var7 = var2.lastIndexOf(46);
      String var5;
      String var6;
      if (var7 == -1) {
         var5 = "";
         var6 = var2.substring(var2.lastIndexOf(47) + 1);
      } else {
         var5 = var2.substring(var2.lastIndexOf(47) + 1, var7);
         var6 = var2.substring(var7 + 1);
      }

      try {
         StringBuilder var8 = new StringBuilder();
         var8.append("//generated using Reflections JavaCodeSerializer").append(" [").append(new Date()).append("]").append("\n");
         if (var5.length() != 0) {
            var8.append("package ").append(var5).append(";\n");
            var8.append("\n");
         }

         var8.append("public interface ").append(var6).append(" {\n\n");
         var8.append(this.toString(var1));
         var8.append("}\n");
         Files.write((new File(var3)).toPath(), var8.toString().getBytes(Charset.defaultCharset()), new OpenOption[0]);
         return var4;
      } catch (IOException var9) {
         throw new RuntimeException();
      }
   }

   public String toString(Reflections var1) {
      if (var1.getStore().keys(Utils.index(TypeElementsScanner.class)).isEmpty() && Reflections.log != null) {
         Reflections.log.warn("JavaCodeSerializer needs TypeElementsScanner configured");
      }

      StringBuilder var2 = new StringBuilder();
      Object var3 = new ArrayList();
      int var4 = 1;
      ArrayList var5 = new ArrayList(var1.getStore().keys(Utils.index(TypeElementsScanner.class)));
      Collections.sort(var5);

      List var8;
      for(Iterator var6 = var5.iterator(); var6.hasNext(); var3 = var8) {
         String var7 = (String)var6.next();
         var8 = Arrays.asList(var7.split("\\."));

         int var9;
         for(var9 = 0; var9 < Math.min(var8.size(), ((List)var3).size()) && ((String)var8.get(var9)).equals(((List)var3).get(var9)); ++var9) {
         }

         int var10;
         for(var10 = ((List)var3).size(); var10 > var9; --var10) {
            --var4;
            var2.append(Utils.repeat("\t", var4)).append("}\n");
         }

         for(var10 = var9; var10 < var8.size() - 1; ++var10) {
            var2.append(Utils.repeat("\t", var4++)).append("public interface ").append(this.getNonDuplicateName((String)var8.get(var10), var8, var10)).append(" {\n");
         }

         String var24 = (String)var8.get(var8.size() - 1);
         ArrayList var11 = new ArrayList();
         ArrayList var12 = new ArrayList();
         ArrayList var13 = new ArrayList();
         Set var14 = var1.getStore().get(Utils.index(TypeElementsScanner.class), var7);
         List var15 = (List)StreamSupport.stream(var14.spliterator(), false).sorted().collect(Collectors.toList());
         Iterator var16 = var15.iterator();

         String var17;
         while(var16.hasNext()) {
            var17 = (String)var16.next();
            if (var17.startsWith("@")) {
               var11.add(var17.substring(1));
            } else if (var17.contains("(")) {
               if (!var17.startsWith("<")) {
                  int var18 = var17.indexOf(40);
                  String var19 = var17.substring(0, var18);
                  String var20 = var17.substring(var18 + 1, var17.indexOf(")"));
                  String var21 = "";
                  if (var20.length() != 0) {
                     var21 = "_" + var20.replace(".", "_").replace(", ", "__").replace("[]", "$$");
                  }

                  String var22 = var19 + var21;
                  if (!var13.contains(var19)) {
                     var13.add(var19);
                  } else {
                     var13.add(var22);
                  }
               }
            } else if (!Utils.isEmpty(var17)) {
               var12.add(var17);
            }
         }

         var2.append(Utils.repeat("\t", var4++)).append("public interface ").append(this.getNonDuplicateName(var24, var8, var8.size() - 1)).append(" {\n");
         if (!var12.isEmpty()) {
            var2.append(Utils.repeat("\t", var4++)).append("public interface fields {\n");
            var16 = var12.iterator();

            while(var16.hasNext()) {
               var17 = (String)var16.next();
               var2.append(Utils.repeat("\t", var4)).append("public interface ").append(this.getNonDuplicateName(var17, var8)).append(" {}\n");
            }

            --var4;
            var2.append(Utils.repeat("\t", var4)).append("}\n");
         }

         String var25;
         if (!var13.isEmpty()) {
            var2.append(Utils.repeat("\t", var4++)).append("public interface methods {\n");
            var16 = var13.iterator();

            while(var16.hasNext()) {
               var17 = (String)var16.next();
               var25 = this.getNonDuplicateName(var17, var12);
               var2.append(Utils.repeat("\t", var4)).append("public interface ").append(this.getNonDuplicateName(var25, var8)).append(" {}\n");
            }

            --var4;
            var2.append(Utils.repeat("\t", var4)).append("}\n");
         }

         if (!var11.isEmpty()) {
            var2.append(Utils.repeat("\t", var4++)).append("public interface annotations {\n");
            var16 = var11.iterator();

            while(var16.hasNext()) {
               var17 = (String)var16.next();
               var25 = this.getNonDuplicateName(var17, var8);
               var2.append(Utils.repeat("\t", var4)).append("public interface ").append(var25).append(" {}\n");
            }

            --var4;
            var2.append(Utils.repeat("\t", var4)).append("}\n");
         }
      }

      for(int var23 = ((List)var3).size(); var23 >= 1; --var23) {
         var2.append(Utils.repeat("\t", var23)).append("}\n");
      }

      return var2.toString();
   }

   private String getNonDuplicateName(String var1, List<String> var2, int var3) {
      String var4 = this.normalize(var1);

      for(int var5 = 0; var5 < var3; ++var5) {
         if (var4.equals(var2.get(var5))) {
            return this.getNonDuplicateName(var4 + "_", var2, var3);
         }
      }

      return var4;
   }

   private String normalize(String var1) {
      return var1.replace(".", "_");
   }

   private String getNonDuplicateName(String var1, List<String> var2) {
      return this.getNonDuplicateName(var1, var2, var2.size());
   }

   public static Class<?> resolveClassOf(Class var0) throws ClassNotFoundException {
      Class var1 = var0;

      LinkedList var2;
      for(var2 = new LinkedList(); var1 != null; var1 = var1.getDeclaringClass()) {
         var2.addFirst(var1.getSimpleName());
      }

      String var3 = Utils.join(var2.subList(1, var2.size()), ".").replace(".$", "$");
      return Class.forName(var3);
   }

   public static Class<?> resolveClass(Class var0) {
      try {
         return resolveClassOf(var0);
      } catch (Exception var2) {
         throw new ReflectionsException("could not resolve to class " + var0.getName(), var2);
      }
   }

   public static Field resolveField(Class var0) {
      try {
         String var1 = var0.getSimpleName();
         Class var2 = var0.getDeclaringClass().getDeclaringClass();
         return resolveClassOf(var2).getDeclaredField(var1);
      } catch (Exception var3) {
         throw new ReflectionsException("could not resolve to field " + var0.getName(), var3);
      }
   }

   public static Annotation resolveAnnotation(Class var0) {
      try {
         String var1 = var0.getSimpleName().replace("_", ".");
         Class var2 = var0.getDeclaringClass().getDeclaringClass();
         Class var3 = resolveClassOf(var2);
         Class var4 = ReflectionUtils.forName(var1);
         Annotation var5 = var3.getAnnotation(var4);
         return var5;
      } catch (Exception var6) {
         throw new ReflectionsException("could not resolve to annotation " + var0.getName(), var6);
      }
   }

   public static Method resolveMethod(Class var0) {
      String var1 = var0.getSimpleName();

      try {
         String var2;
         Class[] var3;
         if (var1.contains("_")) {
            var2 = var1.substring(0, var1.indexOf("_"));
            String[] var4 = var1.substring(var1.indexOf("_") + 1).split("__");
            var3 = new Class[var4.length];

            for(int var5 = 0; var5 < var4.length; ++var5) {
               String var6 = var4[var5].replace("$$", "[]").replace("_", ".");
               var3[var5] = ReflectionUtils.forName(var6);
            }
         } else {
            var2 = var1;
            var3 = null;
         }

         Class var8 = var0.getDeclaringClass().getDeclaringClass();
         return resolveClassOf(var8).getDeclaredMethod(var2, var3);
      } catch (Exception var7) {
         throw new ReflectionsException("could not resolve to method " + var0.getName(), var7);
      }
   }
}
