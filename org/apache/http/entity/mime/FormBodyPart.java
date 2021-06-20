package org.apache.http.entity.mime;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.util.Args;

public class FormBodyPart {
   private final String name;
   private final Header header;
   private final ContentBody body;

   public FormBodyPart(String var1, ContentBody var2) {
      Args.notNull(var1, "Name");
      Args.notNull(var2, "Body");
      this.name = var1;
      this.body = var2;
      this.header = new Header();
      this.generateContentDisp(var2);
      this.generateContentType(var2);
      this.generateTransferEncoding(var2);
   }

   public String getName() {
      return this.name;
   }

   public ContentBody getBody() {
      return this.body;
   }

   public Header getHeader() {
      return this.header;
   }

   public void addField(String var1, String var2) {
      Args.notNull(var1, "Field name");
      this.header.addField(new MinimalField(var1, var2));
   }

   protected void generateContentDisp(ContentBody var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("form-data; name=\"");
      var2.append(this.getName());
      var2.append("\"");
      if (var1.getFilename() != null) {
         var2.append("; filename=\"");
         var2.append(var1.getFilename());
         var2.append("\"");
      }

      this.addField("Content-Disposition", var2.toString());
   }

   protected void generateContentType(ContentBody var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(var1.getMimeType());
      if (var1.getCharset() != null) {
         var2.append("; charset=");
         var2.append(var1.getCharset());
      }

      this.addField("Content-Type", var2.toString());
   }

   protected void generateTransferEncoding(ContentBody var1) {
      this.addField("Content-Transfer-Encoding", var1.getTransferEncoding());
   }
}
