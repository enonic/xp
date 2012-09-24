package com.enonic.wem.api.content.type;

public interface ContentTypeSerializer
{
    public String toString( ContentType type );

    public ContentType toContentType( String xml );
}
