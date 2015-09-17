package com.enonic.xp.web.jaxrs.multipart;

import org.apache.commons.fileupload.FileItem;

public interface MultipartForm
    extends Iterable<FileItem>
{
    FileItem get( String name );

    String getAsString( final String name );

    void delete();
}
