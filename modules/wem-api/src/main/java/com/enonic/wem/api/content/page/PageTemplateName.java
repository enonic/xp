package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.ContentName;

public class PageTemplateName
    extends ContentName
{
    public PageTemplateName( final String name )
    {
        super( name );
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

        final PageTemplateName that = (PageTemplateName) o;
        return super.equals( that );
    }

    public static PageTemplateName from( final String name )
    {
        return new PageTemplateName( name );
    }

    public static PageTemplateName from( final ContentName name )
    {
        return new PageTemplateName( name.toString() );
    }
}
