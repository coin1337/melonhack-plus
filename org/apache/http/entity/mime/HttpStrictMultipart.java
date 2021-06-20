package org.apache.http.entity.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

class HttpStrictMultipart extends AbstractMultipartForm {
   private final List<FormBodyPart> parts;

   public HttpStrictMultipart(String var1, Charset var2, String var3, List<FormBodyPart> var4) {
      super(var1, var2, var3);
      this.parts = var4;
   }

   public List<FormBodyPart> getBodyParts() {
      return this.parts;
   }

   protected void formatMultipartHeader(FormBodyPart var1, OutputStream var2) throws IOException {
      Header var3 = var1.getHeader();
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         MinimalField var5 = (MinimalField)var4.next();
         writeField(var5, var2);
      }

   }
}
