package org.apache.http.entity.mime.content;

public interface ContentDescriptor {
   String getMimeType();

   String getMediaType();

   String getSubType();

   String getCharset();

   String getTransferEncoding();

   long getContentLength();
}
