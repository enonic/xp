package com.enonic.xp.page;

import com.enonic.xp.content.ContentId;


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
        return this == o || o instanceof PageTemplateKey && this.id.equals( ( (PageTemplateKey) o ).id );
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
