package org.reflections.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.reflections.ReflectionsException;

public class FilterBuilder implements Predicate<String> {
   private final List<Predicate<String>> chain;

   public FilterBuilder() {
      this.chain = new ArrayList();
   }

   private FilterBuilder(Collection<Predicate<String>> var1) {
      this.chain = new ArrayList(var1);
   }

   public FilterBuilder include(String var1) {
      return this.add(new FilterBuilder.Include(var1));
   }

   public FilterBuilder exclude(String var1) {
      this.add(new FilterBuilder.Exclude(var1));
      return this;
   }

   public FilterBuilder add(Predicate<String> var1) {
      this.chain.add(var1);
      return this;
   }

   public FilterBuilder includePackage(Class<?> var1) {
      return this.add(new FilterBuilder.Include(packageNameRegex(var1)));
   }

   public FilterBuilder excludePackage(Class<?> var1) {
      return this.add(new FilterBuilder.Exclude(packageNameRegex(var1)));
   }

   public FilterBuilder includePackage(String... var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         this.add(new FilterBuilder.Include(prefix(var5)));
      }

      return this;
   }

   public FilterBuilder excludePackage(String var1) {
      return this.add(new FilterBuilder.Exclude(prefix(var1)));
   }

   private static String packageNameRegex(Class<?> var0) {
      return prefix(var0.getPackage().getName() + ".");
   }

   public static String prefix(String var0) {
      return var0.replace(".", "\\.") + ".*";
   }

   public String toString() {
      return Utils.join(this.chain, ", ");
   }

   public boolean test(String var1) {
      boolean var2 = this.chain.isEmpty() || this.chain.get(0) instanceof FilterBuilder.Exclude;
      Iterator var3 = this.chain.iterator();

      while(var3.hasNext()) {
         Predicate var4 = (Predicate)var3.next();
         if ((!var2 || !(var4 instanceof FilterBuilder.Include)) && (var2 || !(var4 instanceof FilterBuilder.Exclude))) {
            var2 = var4.test(var1);
            if (!var2 && var4 instanceof FilterBuilder.Exclude) {
               break;
            }
         }
      }

      return var2;
   }

   public static FilterBuilder parse(String var0) {
      ArrayList var1 = new ArrayList();
      if (!Utils.isEmpty(var0)) {
         String[] var2 = var0.split(",");
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            String var6 = var5.trim();
            char var7 = var6.charAt(0);
            String var8 = var6.substring(1);
            Object var9;
            switch(var7) {
            case '+':
               var9 = new FilterBuilder.Include(var8);
               break;
            case '-':
               var9 = new FilterBuilder.Exclude(var8);
               break;
            default:
               throw new ReflectionsException("includeExclude should start with either + or -");
            }

            var1.add(var9);
         }

         return new FilterBuilder(var1);
      } else {
         return new FilterBuilder();
      }
   }

   public static FilterBuilder parsePackages(String var0) {
      ArrayList var1 = new ArrayList();
      if (!Utils.isEmpty(var0)) {
         String[] var2 = var0.split(",");
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            String var6 = var5.trim();
            char var7 = var6.charAt(0);
            String var8 = var6.substring(1);
            if (!var8.endsWith(".")) {
               var8 = var8 + ".";
            }

            var8 = prefix(var8);
            Object var9;
            switch(var7) {
            case '+':
               var9 = new FilterBuilder.Include(var8);
               break;
            case '-':
               var9 = new FilterBuilder.Exclude(var8);
               break;
            default:
               throw new ReflectionsException("includeExclude should start with either + or -");
            }

            var1.add(var9);
         }

         return new FilterBuilder(var1);
      } else {
         return new FilterBuilder();
      }
   }

   public static class Exclude extends FilterBuilder.Matcher {
      public Exclude(String var1) {
         super(var1);
      }

      public boolean test(String var1) {
         return !this.pattern.matcher(var1).matches();
      }

      public String toString() {
         return "-" + super.toString();
      }
   }

   public static class Include extends FilterBuilder.Matcher {
      public Include(String var1) {
         super(var1);
      }

      public boolean test(String var1) {
         return this.pattern.matcher(var1).matches();
      }

      public String toString() {
         return "+" + super.toString();
      }
   }

   public abstract static class Matcher implements Predicate<String> {
      final Pattern pattern;

      public Matcher(String var1) {
         this.pattern = Pattern.compile(var1);
      }

      public String toString() {
         return this.pattern.pattern();
      }
   }
}
