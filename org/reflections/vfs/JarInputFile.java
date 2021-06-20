package org.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

public class JarInputFile implements Vfs.File {
   private final ZipEntry entry;
   private final JarInputDir jarInputDir;
   private final long fromIndex;
   private final long endIndex;

   public JarInputFile(ZipEntry var1, JarInputDir var2, long var3, long var5) {
      this.entry = var1;
      this.jarInputDir = var2;
      this.fromIndex = var3;
      this.endIndex = var5;
   }

   public String getName() {
      String var1 = this.entry.getName();
      return var1.substring(var1.lastIndexOf("/") + 1);
   }

   public String getRelativePath() {
      return this.entry.getName();
   }

   public InputStream openInputStream() throws IOException {
      return new InputStream() {
         public int read() throws IOException {
            if (JarInputFile.this.jarInputDir.cursor >= JarInputFile.this.fromIndex && JarInputFile.this.jarInputDir.cursor <= JarInputFile.this.endIndex) {
               int var1 = JarInputFile.this.jarInputDir.jarInputStream.read();
               ++JarInputFile.this.jarInputDir.cursor;
               return var1;
            } else {
               return -1;
            }
         }
      };
   }
}
