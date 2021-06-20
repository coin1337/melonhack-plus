package org.reflections.adapters;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import org.reflections.ReflectionsException;
import org.reflections.util.Utils;
import org.reflections.vfs.Vfs;

public class JavassistAdapter implements MetadataAdapter<ClassFile, FieldInfo, MethodInfo> {
   public static boolean includeInvisibleTag = true;

   public List<FieldInfo> getFields(ClassFile var1) {
      return var1.getFields();
   }

   public List<MethodInfo> getMethods(ClassFile var1) {
      return var1.getMethods();
   }

   public String getMethodName(MethodInfo var1) {
      return var1.getName();
   }

   public List<String> getParameterNames(MethodInfo var1) {
      String var2 = var1.getDescriptor();
      var2 = var2.substring(var2.indexOf("(") + 1, var2.lastIndexOf(")"));
      return this.splitDescriptorToTypeNames(var2);
   }

   public List<String> getClassAnnotationNames(ClassFile var1) {
      return this.getAnnotationNames((AnnotationsAttribute)var1.getAttribute("RuntimeVisibleAnnotations"), includeInvisibleTag ? (AnnotationsAttribute)var1.getAttribute("RuntimeInvisibleAnnotations") : null);
   }

   public List<String> getFieldAnnotationNames(FieldInfo var1) {
      return this.getAnnotationNames((AnnotationsAttribute)var1.getAttribute("RuntimeVisibleAnnotations"), includeInvisibleTag ? (AnnotationsAttribute)var1.getAttribute("RuntimeInvisibleAnnotations") : null);
   }

   public List<String> getMethodAnnotationNames(MethodInfo var1) {
      return this.getAnnotationNames((AnnotationsAttribute)var1.getAttribute("RuntimeVisibleAnnotations"), includeInvisibleTag ? (AnnotationsAttribute)var1.getAttribute("RuntimeInvisibleAnnotations") : null);
   }

   public List<String> getParameterAnnotationNames(MethodInfo var1, int var2) {
      ArrayList var3 = new ArrayList();
      List var4 = Arrays.asList((ParameterAnnotationsAttribute)var1.getAttribute("RuntimeVisibleParameterAnnotations"), (ParameterAnnotationsAttribute)var1.getAttribute("RuntimeInvisibleParameterAnnotations"));
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         ParameterAnnotationsAttribute var6 = (ParameterAnnotationsAttribute)var5.next();
         if (var6 != null) {
            Annotation[][] var7 = var6.getAnnotations();
            if (var2 < var7.length) {
               Annotation[] var8 = var7[var2];
               var3.addAll(this.getAnnotationNames(var8));
            }
         }
      }

      return var3;
   }

   public String getReturnTypeName(MethodInfo var1) {
      String var2 = var1.getDescriptor();
      var2 = var2.substring(var2.lastIndexOf(")") + 1);
      return (String)this.splitDescriptorToTypeNames(var2).get(0);
   }

   public String getFieldName(FieldInfo var1) {
      return var1.getName();
   }

   public ClassFile getOrCreateClassObject(Vfs.File var1) {
      InputStream var2 = null;

      ClassFile var4;
      try {
         var2 = var1.openInputStream();
         DataInputStream var3 = new DataInputStream(new BufferedInputStream(var2));
         var4 = new ClassFile(var3);
      } catch (IOException var8) {
         throw new ReflectionsException("could not create class file from " + var1.getName(), var8);
      } finally {
         Utils.close(var2);
      }

      return var4;
   }

   public String getMethodModifier(MethodInfo var1) {
      int var2 = var1.getAccessFlags();
      return AccessFlag.isPrivate(var2) ? "private" : (AccessFlag.isProtected(var2) ? "protected" : (this.isPublic(var2) ? "public" : ""));
   }

   public String getMethodKey(ClassFile var1, MethodInfo var2) {
      return this.getMethodName(var2) + "(" + Utils.join(this.getParameterNames(var2), ", ") + ")";
   }

   public String getMethodFullKey(ClassFile var1, MethodInfo var2) {
      return this.getClassName(var1) + "." + this.getMethodKey(var1, var2);
   }

   public boolean isPublic(Object var1) {
      Integer var2 = var1 instanceof ClassFile ? ((ClassFile)var1).getAccessFlags() : (var1 instanceof FieldInfo ? ((FieldInfo)var1).getAccessFlags() : var1 instanceof MethodInfo ? ((MethodInfo)var1).getAccessFlags() : null);
      return var2 != null && AccessFlag.isPublic(var2);
   }

   public String getClassName(ClassFile var1) {
      return var1.getName();
   }

   public String getSuperclassName(ClassFile var1) {
      return var1.getSuperclass();
   }

   public List<String> getInterfacesNames(ClassFile var1) {
      return Arrays.asList(var1.getInterfaces());
   }

   public boolean acceptsInput(String var1) {
      return var1.endsWith(".class");
   }

   private List<String> getAnnotationNames(AnnotationsAttribute... var1) {
      return var1 != null ? (List)Arrays.stream(var1).filter(Objects::nonNull).flatMap((var0) -> {
         return Arrays.stream(var0.getAnnotations());
      }).map(Annotation::getTypeName).collect(Collectors.toList()) : Collections.emptyList();
   }

   private List<String> getAnnotationNames(Annotation[] var1) {
      return (List)Arrays.stream(var1).map(Annotation::getTypeName).collect(Collectors.toList());
   }

   private List<String> splitDescriptorToTypeNames(String var1) {
      Object var2 = new ArrayList();
      if (var1 != null && var1.length() != 0) {
         ArrayList var3 = new ArrayList();
         javassist.bytecode.Descriptor.Iterator var4 = new javassist.bytecode.Descriptor.Iterator(var1);

         while(var4.hasNext()) {
            var3.add(var4.next());
         }

         var3.add(var1.length());
         var2 = (List)IntStream.range(0, var3.size() - 1).mapToObj((var2x) -> {
            return Descriptor.toString(var1.substring((Integer)var3.get(var2x), (Integer)var3.get(var2x + 1)));
         }).collect(Collectors.toList());
      }

      return (List)var2;
   }
}
