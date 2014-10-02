package com.enonic.wem.api.workspace;

import com.google.common.base.Strings;

public final class Workspace
{
    private final String name;

    public static Workspace from( final String name )
    {
        if ( Strings.isNullOrEmpty( name ) )
        {
            return null;
        }

        return new Workspace( name );
    }

    private Workspace( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
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

        final Workspace workspace = (Workspace) o;
        return name.equals( workspace.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}


