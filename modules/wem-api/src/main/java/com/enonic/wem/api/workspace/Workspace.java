package com.enonic.wem.api.workspace;

import com.google.common.base.Strings;

public class Workspace
{
    private static final String SEPARATOR = "-";

    private static final String PREFIX = "workspace";

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

        if ( name != null ? !name.equals( workspace.name ) : workspace.name != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }
}


