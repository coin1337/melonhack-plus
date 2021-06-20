package org.apache.http.entity.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

class HttpBrowserCompatibleMultipart extends AbstractMultipartForm {
   private final List<FormBodyPart> parts;

   public HttpBrowserCompatibleMultipart(String var1, Charset var2, String var3, List<FormBodyPart> var4) {
      super(var1, var2, var3);
      this.parts = var4;
   }

   public List<FormBodyPart> getBodyParts() {
      return this.parts;
   }

   protected void formatMultipartHeader(FormBodyPart var1, OutputStream var2) throws IOException {
      Header var3 = var1.getHeader();
      MinimalField var4 = var3.getField("Content-Disposition");
      writeField(var4, this.charset, var2);
      String var5 = var1.getBody().getFilename();
      if (var5 != null) {
         MinimalField var6 = var3.getField("Content-Type");
         writeField(var6, this.charset, var2);
      }

   }
}
