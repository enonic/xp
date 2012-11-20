package com.enonic.wem.core.content;


import com.enonic.wem.api.content.Content;

public interface ContentSerializer
{
    public String toString( Content content )
        throws SerializingException;

    public Content toContent( String xml );
}
