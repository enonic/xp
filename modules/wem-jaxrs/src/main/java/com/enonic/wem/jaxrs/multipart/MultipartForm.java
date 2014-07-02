package com.enonic.wem.jaxrs.multipart;

public interface MultipartForm
    extends Iterable<MultipartItem>
{
    public MultipartItem get( String name );

    public void delete();
}
