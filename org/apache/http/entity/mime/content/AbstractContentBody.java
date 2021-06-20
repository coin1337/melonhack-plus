package org.apache.http.entity.mime.content;

import java.nio.charset.Charset;
import org.apache.http.entity.ContentType;
import org.apache.http.util.Args;

public abstract class AbstractContentBody implements ContentBody {
   private final ContentType contentType;

   public AbstractContentBody(ContentType var1) {
      Args.notNull(var1, "Content type");
      this.contentType = var1;
   }

   /** @deprecated */
   @Deprecated
   public AbstractContentBody(String var1) {
      this(ContentType.parse(var1));
   }

   public ContentType getContentType() {
      return this.contentType;
   }

   public String getMimeType() {
      return this.contentType.getMimeType();
   }

   public String getMediaType() {
      String var1 = this.contentType.getMimeType();
      int var2 = var1.indexOf(47);
      return var2 != -1 ? var1.substring(0, var2) : var1;
   }

   public String getSubType() {
      String var1 = this.contentType.getMimeType();
      int var2 = var1.indexOf(47);
      return var2 != -1 ? var1.substring(var2 + 1) : null;
   }

   public String getCharset() {
      Charset var1 = this.contentType.getCharset();
      return var1 != null ? var1.name() : null;
   }
}
