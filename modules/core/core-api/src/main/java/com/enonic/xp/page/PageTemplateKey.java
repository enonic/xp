package com.enonic.xp.page;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.ContentId;

@Beta
public final class PageTemplateKey
{
    private final ContentId id;

    private PageTemplateKey( final ContentId id )
    {
        this.id = id;
    }

    public ContentId getContentId()
    {
        return this.id;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof PageTemplateKey ) && ( (PageTemplateKey) o ).id.equals( this.id );
    }

    @Override
    public int hashCode()
    {
        return this.id.hashCode();
    }

    @Override
    public String toString()
    {
        return this.id.toString();
    }

    public static PageTemplateKey from( final String value )
    {
        return from( ContentId.from( value ) );
    }

    public static PageTemplateKey from( final ContentId value )
    {
        return new PageTemplateKey( value );
    }
}
