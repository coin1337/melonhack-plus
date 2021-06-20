package org.reflections.vfs;

import java.io.IOException;
import java.util.jar.JarFile;
import org.reflections.Reflections;

public class ZipDir implements Vfs.Dir {
   final java.util.zip.ZipFile jarFile;

   public ZipDir(JarFile var1) {
      this.jarFile = var1;
   }

   public String getPath() {
      return this.jarFile.getName();
   }

   public Iterable<Vfs.File> getFiles() {
      return () -> {
         return this.jarFile.stream().filter((var0) -> {
            return !var0.isDirectory();
         }).map((var1) -> {
            return new ZipFile(this, var1);
         }).iterator();
      };
   }

   public void close() {
      try {
         this.jarFile.close();
      } catch (IOException var2) {
         if (Reflections.log != null) {
            Reflections.log.warn("Could not close JarFile", var2);
         }
      }

   }

   public String toString() {
      return this.jarFile.getName();
   }
}
