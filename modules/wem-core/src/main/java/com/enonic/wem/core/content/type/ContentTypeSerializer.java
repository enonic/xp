package com.enonic.wem.core.content.type;

import com.enonic.wem.api.content.type.ContentType;

public interface ContentTypeSerializer
{
    public String toString( ContentType type );

    public ContentType toContentType( String xml );
}
