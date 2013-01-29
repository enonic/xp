package com.enonic.wem.api.space;

import com.google.common.base.Preconditions;

public final class SpaceName
{
    private final String name;

    private SpaceName( final String name )
    {
        Preconditions.checkNotNull( name, "name cannot be null" );
        this.name = name;
    }

    public String name()
    {
        return this.name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof SpaceName ) )
        {
            return false;
        }

        final SpaceName that = (SpaceName) o;
        return name.equals( that.name() );
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

    public static SpaceName from( final String spaceName )
    {
        return new SpaceName( spaceName );
    }
}
