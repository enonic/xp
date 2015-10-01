package com.enonic.xp.web.multipart;

public interface MultipartForm
    extends Iterable<MultipartItem>
{
    MultipartItem get( String name );

    String getAsString( String name );

    void delete();
}
