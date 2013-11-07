package com.enonic.wem.api.content.page;


import static com.google.common.base.Preconditions.checkNotNull;

public abstract class TemplateName
{
    private final String name;

    protected TemplateName( final String name )
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

        final TemplateName that = (TemplateName) o;

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
}
