package org.reflections.vfs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Collections;
import org.reflections.ReflectionsException;

public class SystemDir implements Vfs.Dir {
   private final File file;

   public SystemDir(File var1) {
      if (var1 == null || var1.isDirectory() && var1.canRead()) {
         this.file = var1;
      } else {
         throw new RuntimeException("cannot use dir " + var1);
      }
   }

   public String getPath() {
      return this.file == null ? "/NO-SUCH-DIRECTORY/" : this.file.getPath().replace("\\", "/");
   }

   public Iterable<Vfs.File> getFiles() {
      return (Iterable)(this.file != null && this.file.exists() ? () -> {
         try {
            return Files.walk(this.file.toPath()).filter((var0) -> {
               return Files.isRegularFile(var0, new LinkOption[0]);
            }).map((var1) -> {
               return new SystemFile(this, var1.toFile());
            }).iterator();
         } catch (IOException var2) {
            throw new ReflectionsException("could not get files for " + this.file, var2);
         }
      } : Collections.emptyList());
   }

   public void close() {
   }

   public String toString() {
      return this.getPath();
   }
}
