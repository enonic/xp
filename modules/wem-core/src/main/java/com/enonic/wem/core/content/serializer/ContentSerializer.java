package com.enonic.wem.core.content.serializer;


import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.support.serializer.SerializingException;

public interface ContentSerializer
{
    public String toString( Content content )
        throws SerializingException;

    public Content toContent( String xml );
}
