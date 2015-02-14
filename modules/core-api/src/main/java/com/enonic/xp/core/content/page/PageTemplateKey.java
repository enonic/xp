package com.enonic.xp.core.content.page;


import com.enonic.xp.core.content.ContentId;

public final class PageTemplateKey
    extends ContentId
{
    private PageTemplateKey( final String id )
    {
        super( id );
    }

    private PageTemplateKey( final ContentId id )
    {
        super( id.toString() );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final PageTemplateKey that = (PageTemplateKey) o;
        return super.equals( that );
    }

    public static PageTemplateKey from( final String value )
    {
        return new PageTemplateKey( value );
    }

    public static PageTemplateKey from( final ContentId value )
    {
        return new PageTemplateKey( value );
    }
}
