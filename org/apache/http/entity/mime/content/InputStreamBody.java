package org.apache.http.entity.mime.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.ContentType;
import org.apache.http.util.Args;

public class InputStreamBody extends AbstractContentBody {
   private final InputStream in;
   private final String filename;

   /** @deprecated */
   @Deprecated
   public InputStreamBody(InputStream var1, String var2, String var3) {
      this(var1, ContentType.create(var2), var3);
   }

   public InputStreamBody(InputStream var1, String var2) {
      this(var1, ContentType.DEFAULT_BINARY, var2);
   }

   public InputStreamBody(InputStream var1, ContentType var2, String var3) {
      super(var2);
      Args.notNull(var1, "Input stream");
      this.in = var1;
      this.filename = var3;
   }

   public InputStreamBody(InputStream var1, ContentType var2) {
      this(var1, (ContentType)var2, (String)null);
   }

   public InputStream getInputStream() {
      return this.in;
   }

   public void writeTo(OutputStream var1) throws IOException {
      Args.notNull(var1, "Output stream");

      try {
         byte[] var2 = new byte[4096];

         int var3;
         while((var3 = this.in.read(var2)) != -1) {
            var1.write(var2, 0, var3);
         }

         var1.flush();
      } finally {
         this.in.close();
      }
   }

   public String getTransferEncoding() {
      return "binary";
   }

   public long getContentLength() {
      return -1L;
   }

   public String getFilename() {
      return this.filename;
   }
}
