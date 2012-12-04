package com.enonic.wem.api.module;


public class ModuleName
{
    private final String name;

    public ModuleName( final String name )
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ModuleName ) && ( (ModuleName) o ).name.equals( this.name );
    }

    public static ModuleName from( String value )
    {
        return new ModuleName( value );
    }
}
