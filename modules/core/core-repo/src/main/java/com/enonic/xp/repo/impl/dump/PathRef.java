package com.enonic.xp.repo.impl.dump;

import java.nio.file.Path;
import java.util.Objects;

public final class PathRef
{

    private static final String SEPARATOR = "/";

    private static final PathRef EMPTY_PATH_REF = new PathRef( "" );

    private final String path;

    private PathRef( final String path )
    {
        this.path = path;
    }

    public static PathRef of()
    {
        return EMPTY_PATH_REF;
    }

    public static PathRef of( String root )
    {
        return new PathRef( validate( root ) );
    }

    private static String validate( String element )
    {
        Objects.requireNonNull( element, "element can't be null" );
        if ( element.equals( "." ) || element.equals( ".." ) || element.contains( SEPARATOR ) )
        {
            throw new IllegalArgumentException( "Invalid element " + element );
        }
        return element;
    }

    public PathRef resolve( String other )
    {
        if ( other.isEmpty() )
        {
            return this;
        }
        if ( path.isEmpty() )
        {
            return new PathRef( validate( other ) );
        }

        return new PathRef( path + SEPARATOR + validate( other ) );
    }

    public Path asPath( Path parent )
    {
        return parent.resolve( path );
    }

    public String asString()
    {
        return path;
    }

    @Override
    public String toString()
    {
        return path;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || ( o instanceof PathRef && path.equals( ( (PathRef) o ).path ) );
    }

    @Override
    public int hashCode()
    {
        return path.hashCode();
    }
}
