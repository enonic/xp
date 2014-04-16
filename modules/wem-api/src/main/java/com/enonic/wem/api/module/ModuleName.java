package com.enonic.wem.api.module;

public final class ModuleName
{
    private final String name;

    private ModuleName( final String name )
    {
        this.name = name;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ModuleName ) && ( (ModuleName) o ).name.equals( this.name );
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

    public static ModuleName from( final String name )
    {
        return new ModuleName( name );
    }
}
