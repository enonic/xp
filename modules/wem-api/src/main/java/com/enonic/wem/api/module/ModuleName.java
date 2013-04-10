package com.enonic.wem.api.module;


import com.enonic.wem.api.Name;

public final class ModuleName
    extends Name
{
    public static final ModuleName SYSTEM = new ModuleName( "system" );

    private ModuleName( final String name )
    {
        super( name );
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ModuleName ) && super.equals( o );
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    public static ModuleName from( String name )
    {
        return new ModuleName( name );
    }
}
