package org.apache.http.entity.mime.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.ContentType;
import org.apache.http.util.Args;

public class FileBody extends AbstractContentBody {
   private final File file;
   private final String filename;

   /** @deprecated */
   @Deprecated
   public FileBody(File var1, String var2, String var3, String var4) {
      this(var1, ContentType.create(var3, var4), var2);
   }

   /** @deprecated */
   @Deprecated
   public FileBody(File var1, String var2, String var3) {
      this(var1, (String)null, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   public FileBody(File var1, String var2) {
      this(var1, (ContentType)ContentType.create(var2), (String)null);
   }

   public FileBody(File var1) {
      this(var1, ContentType.DEFAULT_BINARY, var1 != null ? var1.getName() : null);
   }

   public FileBody(File var1, ContentType var2, String var3) {
      super(var2);
      Args.notNull(var1, "File");
      this.file = var1;
      this.filename = var3;
   }

   public FileBody(File var1, ContentType var2) {
      this(var1, (ContentType)var2, (String)null);
   }

   public InputStream getInputStream() throws IOException {
      return new FileInputStream(this.file);
   }

   public void writeTo(OutputStream var1) throws IOException {
      Args.notNull(var1, "Output stream");
      FileInputStream var2 = new FileInputStream(this.file);

      try {
         byte[] var3 = new byte[4096];

         int var4;
         while((var4 = var2.read(var3)) != -1) {
            var1.write(var3, 0, var4);
         }

         var1.flush();
      } finally {
         var2.close();
      }
   }

   public String getTransferEncoding() {
      return "binary";
   }

   public long getContentLength() {
      return this.file.length();
   }

   public String getFilename() {
      return this.filename;
   }

   public File getFile() {
      return this.file;
   }
}
