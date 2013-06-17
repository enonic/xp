package com.enonic.wem.web.util;

public interface MimeTypeResolver
{
    String getMimeType( String fileName );

    String getMimeTypeByExtension( String ext );

    String getExtension( String mimeType );
}
