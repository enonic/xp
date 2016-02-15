package com.enonic.xp.web.multipart;

public interface MultipartForm
    extends Iterable<MultipartItem>
{
    boolean isEmpty();

    int getSize();

    MultipartItem get( String name );

    MultipartItem get( String name, int index );

    String getAsString( String name );

    void delete();
}
