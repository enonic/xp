package com.enonic.wem.core.schema.content.serializer;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.core.support.serializer.ParsingException;
import com.enonic.wem.core.support.serializer.SerializingException;

public interface ContentTypeSerializer
{
    public String toString( ContentType type )
        throws SerializingException;

    public ContentType toContentType( String xml )
        throws ParsingException;
}
