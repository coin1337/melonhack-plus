package org.reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.Utils;

public abstract class ReflectionUtils {
   public static boolean includeObject = false;
   private static List<String> primitiveNames;
   private static List<Class> primitiveTypes;
   private static List<String> primitiveDescriptors;

   public static Set<Class<?>> getAllSuperTypes(Class<?> var0, Predicate<? super Class<?>>... var1) {
      LinkedHashSet var2 = new LinkedHashSet();
      if (var0 != null && (includeObject || !var0.equals(Object.class))) {
         var2.add(var0);
         Iterator var3 = getSuperTypes(var0).iterator();

         while(var3.hasNext()) {
            Class var4 = (Class)var3.next();
            var2.addAll(getAllSuperTypes(var4));
         }
      }

      return Utils.filter((Collection)var2, (Predicate[])var1);
   }

   public static Set<Class<?>> getSuperTypes(Class<?> var0) {
      LinkedHashSet var1 = new LinkedHashSet();
      Class var2 = var0.getSuperclass();
      Class[] var3 = var0.getInterfaces();
      if (var2 != null && (includeObject || !var2.equals(Object.class))) {
         var1.add(var2);
      }

      if (var3 != null && var3.length > 0) {
         var1.addAll(Arrays.asList(var3));
      }

      return var1;
   }

   public static Set<Method> getAllMethods(Class<?> var0, Predicate<? super Method>... var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = getAllSuperTypes(var0).iterator();

      while(var3.hasNext()) {
         Class var4 = (Class)var3.next();
         var2.addAll(getMethods(var4, var1));
      }

      return var2;
   }

   public static Set<Method> getMethods(Class<?> var0, Predicate<? super Method>... var1) {
      return Utils.filter((Object[])(var0.isInterface() ? var0.getMethods() : var0.getDeclaredMethods()), (Predicate[])var1);
   }

   public static Set<Constructor> getAllConstructors(Class<?> var0, Predicate<? super Constructor>... var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = getAllSuperTypes(var0).iterator();

      while(var3.hasNext()) {
         Class var4 = (Class)var3.next();
         var2.addAll(getConstructors(var4, var1));
      }

      return var2;
   }

   public static Set<Constructor> getConstructors(Class<?> var0, Predicate<? super Constructor>... var1) {
      return Utils.filter((Object[])var0.getDeclaredConstructors(), (Predicate[])var1);
   }

   public static Set<Field> getAllFields(Class<?> var0, Predicate<? super Field>... var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = getAllSuperTypes(var0).iterator();

      while(var3.hasNext()) {
         Class var4 = (Class)var3.next();
         var2.addAll(getFields(var4, var1));
      }

      return var2;
   }

   public static Set<Field> getFields(Class<?> var0, Predicate<? super Field>... var1) {
      return Utils.filter((Object[])var0.getDeclaredFields(), (Predicate[])var1);
   }

   public static <T extends AnnotatedElement> Set<Annotation> getAllAnnotations(T var0, Predicate<Annotation>... var1) {
      HashSet var2 = new HashSet();
      if (var0 instanceof Class) {
         Iterator var3 = getAllSuperTypes((Class)var0).iterator();

         while(var3.hasNext()) {
            Class var4 = (Class)var3.next();
            var2.addAll(getAnnotations(var4, var1));
         }
      } else {
         var2.addAll(getAnnotations(var0, var1));
      }

      return var2;
   }

   public static <T extends AnnotatedElement> Set<Annotation> getAnnotations(T var0, Predicate<Annotation>... var1) {
      return Utils.filter((Object[])var0.getDeclaredAnnotations(), (Predicate[])var1);
   }

   public static <T extends AnnotatedElement> Set<T> getAll(Set<T> var0, Predicate<? super T>... var1) {
      return Utils.filter((Collection)var0, (Predicate[])var1);
   }

   public static <T extends Member> Predicate<T> withName(String var0) {
      return (var1) -> {
         return var1 != null && var1.getName().equals(var0);
      };
   }

   public static <T extends Member> Predicate<T> withPrefix(String var0) {
      return (var1) -> {
         return var1 != null && var1.getName().startsWith(var0);
      };
   }

   public static <T extends AnnotatedElement> Predicate<T> withPattern(String var0) {
      return (var1) -> {
         return Pattern.matches(var0, var1.toString());
      };
   }

   public static <T extends AnnotatedElement> Predicate<T> withAnnotation(Class<? extends Annotation> var0) {
      return (var1) -> {
         return var1 != null && var1.isAnnotationPresent(var0);
      };
   }

   public static <T extends AnnotatedElement> Predicate<T> withAnnotations(Class<? extends Annotation>... var0) {
      return (var1) -> {
         return var1 != null && Arrays.equals(var0, annotationTypes(var1.getAnnotations()));
      };
   }

   public static <T extends AnnotatedElement> Predicate<T> withAnnotation(Annotation var0) {
      return (var1) -> {
         return var1 != null && var1.isAnnotationPresent(var0.annotationType()) && areAnnotationMembersMatching(var1.getAnnotation(var0.annotationType()), var0);
      };
   }

   public static <T extends AnnotatedElement> Predicate<T> withAnnotations(Annotation... var0) {
      return (var1) -> {
         if (var1 != null) {
            Annotation[] var2 = var1.getAnnotations();
            if (var2.length == var0.length) {
               return IntStream.range(0, var2.length).allMatch((var2x) -> {
                  return areAnnotationMembersMatching(var2[var2x], var0[var2x]);
               });
            }
         }

         return true;
      };
   }

   public static Predicate<Member> withParameters(Class<?>... var0) {
      return (var1) -> {
         return Arrays.equals(parameterTypes(var1), var0);
      };
   }

   public static Predicate<Member> withParametersAssignableTo(Class... var0) {
      return (var1) -> {
         return isAssignable(var0, parameterTypes(var1));
      };
   }

   public static Predicate<Member> withParametersAssignableFrom(Class... var0) {
      return (var1) -> {
         return isAssignable(parameterTypes(var1), var0);
      };
   }

   public static Predicate<Member> withParametersCount(int var0) {
      return (var1) -> {
         return var1 != null && parameterTypes(var1).length == var0;
      };
   }

   public static Predicate<Member> withAnyParameterAnnotation(Class<? extends Annotation> var0) {
      return (var1) -> {
         return var1 != null && annotationTypes((Collection)parameterAnnotations(var1)).stream().anyMatch((var1x) -> {
            return var1x.equals(var0);
         });
      };
   }

   public static Predicate<Member> withAnyParameterAnnotation(Annotation var0) {
      return (var1) -> {
         return var1 != null && parameterAnnotations(var1).stream().anyMatch((var1x) -> {
            return areAnnotationMembersMatching(var0, var1x);
         });
      };
   }

   public static <T> Predicate<Field> withType(Class<T> var0) {
      return (var1) -> {
         return var1 != null && var1.getType().equals(var0);
      };
   }

   public static <T> Predicate<Field> withTypeAssignableTo(Class<T> var0) {
      return (var1) -> {
         return var1 != null && var0.isAssignableFrom(var1.getType());
      };
   }

   public static <T> Predicate<Method> withReturnType(Class<T> var0) {
      return (var1) -> {
         return var1 != null && var1.getReturnType().equals(var0);
      };
   }

   public static <T> Predicate<Method> withReturnTypeAssignableTo(Class<T> var0) {
      return (var1) -> {
         return var1 != null && var0.isAssignableFrom(var1.getReturnType());
      };
   }

   public static <T extends Member> Predicate<T> withModifier(int var0) {
      return (var1) -> {
         return var1 != null && (var1.getModifiers() & var0) != 0;
      };
   }

   public static Predicate<Class<?>> withClassModifier(int var0) {
      return (var1) -> {
         return var1 != null && (var1.getModifiers() & var0) != 0;
      };
   }

   public static Class<?> forName(String var0, ClassLoader... var1) {
      if (getPrimitiveNames().contains(var0)) {
         return (Class)getPrimitiveTypes().get(getPrimitiveNames().indexOf(var0));
      } else {
         String var2;
         if (var0.contains("[")) {
            int var3 = var0.indexOf("[");
            var2 = var0.substring(0, var3);
            String var4 = var0.substring(var3).replace("]", "");
            if (getPrimitiveNames().contains(var2)) {
               var2 = (String)getPrimitiveDescriptors().get(getPrimitiveNames().indexOf(var2));
            } else {
               var2 = "L" + var2 + ";";
            }

            var2 = var4 + var2;
         } else {
            var2 = var0;
         }

         ArrayList var11 = new ArrayList();
         ClassLoader[] var12 = ClasspathHelper.classLoaders(var1);
         int var5 = var12.length;
         int var6 = 0;

         while(var6 < var5) {
            ClassLoader var7 = var12[var6];
            if (var2.contains("[")) {
               try {
                  return Class.forName(var2, false, var7);
               } catch (Throwable var10) {
                  var11.add(new ReflectionsException("could not get type for name " + var0, var10));
               }
            }

            try {
               return var7.loadClass(var2);
            } catch (Throwable var9) {
               var11.add(new ReflectionsException("could not get type for name " + var0, var9));
               ++var6;
            }
         }

         if (Reflections.log != null) {
            Iterator var13 = var11.iterator();

            while(var13.hasNext()) {
               ReflectionsException var14 = (ReflectionsException)var13.next();
               Reflections.log.warn("could not get type for name " + var0 + " from any class loader", var14);
            }
         }

         return null;
      }
   }

   public static <T> Set<Class<? extends T>> forNames(Collection<String> var0, ClassLoader... var1) {
      return (Set)var0.stream().map((var1x) -> {
         return forName(var1x, var1);
      }).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   private static Class[] parameterTypes(Member var0) {
      return var0 != null ? (var0.getClass() == Method.class ? ((Method)var0).getParameterTypes() : (var0.getClass() == Constructor.class ? ((Constructor)var0).getParameterTypes() : null)) : null;
   }

   private static Set<Annotation> parameterAnnotations(Member var0) {
      Annotation[][] var1 = var0 instanceof Method ? ((Method)var0).getParameterAnnotations() : (var0 instanceof Constructor ? ((Constructor)var0).getParameterAnnotations() : (Annotation[][])null);
      return (Set)Arrays.stream(var1).flatMap(Arrays::stream).collect(Collectors.toSet());
   }

   private static Set<Class<? extends Annotation>> annotationTypes(Collection<Annotation> var0) {
      return (Set)var0.stream().map(Annotation::annotationType).collect(Collectors.toSet());
   }

   private static Class<? extends Annotation>[] annotationTypes(Annotation[] var0) {
      return (Class[])Arrays.stream(var0).map(Annotation::annotationType).toArray((var0x) -> {
         return new Class[var0x];
      });
   }

   private static void initPrimitives() {
      if (primitiveNames == null) {
         primitiveNames = Arrays.asList("boolean", "char", "byte", "short", "int", "long", "float", "double", "void");
         primitiveTypes = Arrays.asList(Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE);
         primitiveDescriptors = Arrays.asList("Z", "C", "B", "S", "I", "J", "F", "D", "V");
      }

   }

   private static List<String> getPrimitiveNames() {
      initPrimitives();
      return primitiveNames;
   }

   private static List<Class> getPrimitiveTypes() {
      initPrimitives();
      return primitiveTypes;
   }

   private static List<String> getPrimitiveDescriptors() {
      initPrimitives();
      return primitiveDescriptors;
   }

   private static boolean areAnnotationMembersMatching(Annotation var0, Annotation var1) {
      if (var1 != null && var0.annotationType() == var1.annotationType()) {
         Method[] var2 = var0.annotationType().getDeclaredMethods();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Method var5 = var2[var4];

            try {
               if (!var5.invoke(var0).equals(var5.invoke(var1))) {
                  return false;
               }
            } catch (Exception var7) {
               throw new ReflectionsException(String.format("could not invoke method %s on annotation %s", var5.getName(), var0.annotationType()), var7);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean isAssignable(Class[] var0, Class[] var1) {
      if (var0 != null) {
         return var0.length != var1.length ? false : IntStream.range(0, var0.length).noneMatch((var2) -> {
            return !var1[var2].isAssignableFrom(var0[var2]) || var1[var2] == Object.class && var0[var2] != Object.class;
         });
      } else {
         return var1 == null || var1.length == 0;
      }
   }
}
