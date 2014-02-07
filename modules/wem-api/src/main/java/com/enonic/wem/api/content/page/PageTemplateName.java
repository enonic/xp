package com.enonic.wem.api.content.page;


import static com.google.common.base.Preconditions.checkNotNull;

public class PageTemplateName
{
    private final String name;

    public PageTemplateName( final String name )
    {
        checkNotNull( name, "Template name cannot be null" );
        this.name = name;
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

        if ( !name.equals( that.name ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static PageTemplateName from( final String name )
    {
        return new PageTemplateName( name );
    }
}
