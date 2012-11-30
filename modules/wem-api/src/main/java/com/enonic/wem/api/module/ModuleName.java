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

    public static ModuleName from( String value )
    {
        return new ModuleName( value );
    }
}
