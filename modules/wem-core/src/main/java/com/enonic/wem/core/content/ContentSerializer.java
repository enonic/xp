package com.enonic.wem.core.content;


public interface ContentSerializer
{
    public String toString( Content content );

    public Content toContent( String xml );
}
