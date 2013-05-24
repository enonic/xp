package com.enonic.wem.admin.rest.service.upload;

import java.io.IOException;
import java.io.InputStream;

public interface UploadService
{
    public UploadItem getItem( final String id );

    public void removeItem( final String id );

    public UploadItem upload( String name, String mediaType, InputStream in )
        throws IOException;
}
