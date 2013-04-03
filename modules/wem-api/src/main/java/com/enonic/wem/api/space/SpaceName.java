package com.enonic.wem.api.space;

import com.google.common.base.Preconditions;

public final class SpaceName
{
    private static final SpaceName TEMPORARY_SPACE = new SpaceName( "_temporary" );

    private final String name;

    private SpaceName( final String name )
    {
        Preconditions.checkNotNull( name, "space name cannot be null" );
        Preconditions.checkArgument( !name.trim().isEmpty(), "space name cannot be empty" );
        this.name = name;
    }

    public String name()
    {
        return this.name;
    }

    public boolean isTemporary()
    {
        return TEMPORARY_SPACE.equals( this );
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

    public static SpaceName temporary()
    {
        return TEMPORARY_SPACE;
    }

    public static SpaceName from( final String spaceName )
    {
        return new SpaceName( spaceName );
    }
}
