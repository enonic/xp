package com.enonic.xp.web.jaxrs.rest.multipart;

import org.apache.commons.fileupload.FileItem;

public interface MultipartForm
    extends Iterable<FileItem>
{
    public FileItem get( String name );

    public String getAsString( final String name );

    public void delete();
}
