package com.enonic.wem.core.content.type;

public interface ContentTypeSerializer
{
    public String toString( ContentType type );

    public ContentType toContentType( String xml );
}
