package org.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.Utils;

public abstract class Vfs {
   private static List<Vfs.UrlType> defaultUrlTypes = new ArrayList(Arrays.asList(Vfs.DefaultUrlTypes.values()));

   public static List<Vfs.UrlType> getDefaultUrlTypes() {
      return defaultUrlTypes;
   }

   public static void setDefaultURLTypes(List<Vfs.UrlType> var0) {
      defaultUrlTypes = var0;
   }

   public static void addDefaultURLTypes(Vfs.UrlType var0) {
      defaultUrlTypes.add(0, var0);
   }

   public static Vfs.Dir fromURL(URL var0) {
      return fromURL(var0, defaultUrlTypes);
   }

   public static Vfs.Dir fromURL(URL var0, List<Vfs.UrlType> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Vfs.UrlType var3 = (Vfs.UrlType)var2.next();

         try {
            if (var3.matches(var0)) {
               Vfs.Dir var4 = var3.createDir(var0);
               if (var4 != null) {
                  return var4;
               }
            }
         } catch (Throwable var5) {
            if (Reflections.log != null) {
               Reflections.log.warn("could not create Dir using " + var3 + " from url " + var0.toExternalForm() + ". skipping.", var5);
            }
         }
      }

      throw new ReflectionsException("could not create Vfs.Dir from url, no matching UrlType was found [" + var0.toExternalForm() + "]\neither use fromURL(final URL url, final List<UrlType> urlTypes) or use the static setDefaultURLTypes(final List<UrlType> urlTypes) or addDefaultURLTypes(UrlType urlType) with your specialized UrlType.");
   }

   public static Vfs.Dir fromURL(URL var0, Vfs.UrlType... var1) {
      return fromURL(var0, Arrays.asList(var1));
   }

   public static Iterable<Vfs.File> findFiles(Collection<URL> var0, String var1, Predicate<String> var2) {
      Predicate var3 = (var2x) -> {
         String var3 = var2x.getRelativePath();
         if (!var3.startsWith(var1)) {
            return false;
         } else {
            String var4 = var3.substring(var3.indexOf(var1) + var1.length());
            return !Utils.isEmpty(var4) && var2.test(var4.substring(1));
         }
      };
      return findFiles(var0, var3);
   }

   public static Iterable<Vfs.File> findFiles(Collection<URL> var0, Predicate<Vfs.File> var1) {
      return () -> {
         return var0.stream().flatMap((var0x) -> {
            try {
               return StreamSupport.stream(fromURL(var0x).getFiles().spliterator(), false);
            } catch (Throwable var2) {
               if (Reflections.log != null) {
                  Reflections.log.error("could not findFiles for url. continuing. [" + var0x + "]", var2);
               }

               return Stream.of();
            }
         }).filter(var1).iterator();
      };
   }

   public static java.io.File getFile(URL var0) {
      java.io.File var1;
      String var2;
      try {
         var2 = var0.toURI().getSchemeSpecificPart();
         if ((var1 = new java.io.File(var2)).exists()) {
            return var1;
         }
      } catch (URISyntaxException var6) {
      }

      try {
         var2 = URLDecoder.decode(var0.getPath(), "UTF-8");
         if (var2.contains(".jar!")) {
            var2 = var2.substring(0, var2.lastIndexOf(".jar!") + ".jar".length());
         }

         if ((var1 = new java.io.File(var2)).exists()) {
            return var1;
         }
      } catch (UnsupportedEncodingException var5) {
      }

      try {
         var2 = var0.toExternalForm();
         if (var2.startsWith("jar:")) {
            var2 = var2.substring("jar:".length());
         }

         if (var2.startsWith("wsjar:")) {
            var2 = var2.substring("wsjar:".length());
         }

         if (var2.startsWith("file:")) {
            var2 = var2.substring("file:".length());
         }

         if (var2.contains(".jar!")) {
            var2 = var2.substring(0, var2.indexOf(".jar!") + ".jar".length());
         }

         if (var2.contains(".war!")) {
            var2 = var2.substring(0, var2.indexOf(".war!") + ".war".length());
         }

         if ((var1 = new java.io.File(var2)).exists()) {
            return var1;
         }

         var2 = var2.replace("%20", " ");
         if ((var1 = new java.io.File(var2)).exists()) {
            return var1;
         }
      } catch (Exception var4) {
      }

      return null;
   }

   private static boolean hasJarFileInPath(URL var0) {
      return var0.toExternalForm().matches(".*\\.jar(\\!.*|$)");
   }

   public static enum DefaultUrlTypes implements Vfs.UrlType {
      jarFile {
         public boolean matches(URL var1) {
            return var1.getProtocol().equals("file") && Vfs.hasJarFileInPath(var1);
         }

         public Vfs.Dir createDir(URL var1) throws Exception {
            return new ZipDir(new JarFile(Vfs.getFile(var1)));
         }
      },
      jarUrl {
         public boolean matches(URL var1) {
            return "jar".equals(var1.getProtocol()) || "zip".equals(var1.getProtocol()) || "wsjar".equals(var1.getProtocol());
         }

         public Vfs.Dir createDir(URL var1) throws Exception {
            try {
               URLConnection var2 = var1.openConnection();
               if (var2 instanceof JarURLConnection) {
                  var2.setUseCaches(false);
                  return new ZipDir(((JarURLConnection)var2).getJarFile());
               }
            } catch (Throwable var3) {
            }

            java.io.File var4 = Vfs.getFile(var1);
            return var4 != null ? new ZipDir(new JarFile(var4)) : null;
         }
      },
      directory {
         public boolean matches(URL var1) {
            if (var1.getProtocol().equals("file") && !Vfs.hasJarFileInPath(var1)) {
               java.io.File var2 = Vfs.getFile(var1);
               return var2 != null && var2.isDirectory();
            } else {
               return false;
            }
         }

         public Vfs.Dir createDir(URL var1) throws Exception {
            return new SystemDir(Vfs.getFile(var1));
         }
      },
      jboss_vfs {
         public boolean matches(URL var1) {
            return var1.getProtocol().equals("vfs");
         }

         public Vfs.Dir createDir(URL var1) throws Exception {
            Object var2 = var1.openConnection().getContent();
            Class var3 = ClasspathHelper.contextClassLoader().loadClass("org.jboss.vfs.VirtualFile");
            java.io.File var4 = (java.io.File)var3.getMethod("getPhysicalFile").invoke(var2);
            String var5 = (String)var3.getMethod("getName").invoke(var2);
            java.io.File var6 = new java.io.File(var4.getParentFile(), var5);
            if (!var6.exists() || !var6.canRead()) {
               var6 = var4;
            }

            return (Vfs.Dir)(var6.isDirectory() ? new SystemDir(var6) : new ZipDir(new JarFile(var6)));
         }
      },
      jboss_vfsfile {
         public boolean matches(URL var1) throws Exception {
            return "vfszip".equals(var1.getProtocol()) || "vfsfile".equals(var1.getProtocol());
         }

         public Vfs.Dir createDir(URL var1) throws Exception {
            return (new UrlTypeVFS()).createDir(var1);
         }
      },
      bundle {
         public boolean matches(URL var1) throws Exception {
            return var1.getProtocol().startsWith("bundle");
         }

         public Vfs.Dir createDir(URL var1) throws Exception {
            return Vfs.fromURL((URL)ClasspathHelper.contextClassLoader().loadClass("org.eclipse.core.runtime.FileLocator").getMethod("resolve", URL.class).invoke((Object)null, var1));
         }
      },
      jarInputStream {
         public boolean matches(URL var1) throws Exception {
            return var1.toExternalForm().contains(".jar");
         }

         public Vfs.Dir createDir(URL var1) throws Exception {
            return new JarInputDir(var1);
         }
      };

      private DefaultUrlTypes() {
      }

      // $FF: synthetic method
      DefaultUrlTypes(Object var3) {
         this();
      }
   }

   public interface UrlType {
      boolean matches(URL var1) throws Exception;

      Vfs.Dir createDir(URL var1) throws Exception;
   }

   public interface File {
      String getName();

      String getRelativePath();

      InputStream openInputStream() throws IOException;
   }

   public interface Dir {
      String getPath();

      Iterable<Vfs.File> getFiles();

      void close();
   }
}
