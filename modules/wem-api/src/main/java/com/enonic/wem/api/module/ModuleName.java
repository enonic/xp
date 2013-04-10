package com.enonic.wem.api.module;


import com.google.common.base.Preconditions;

public final class ModuleName
{
    public static final ModuleName SYSTEM = new ModuleName( "system" );

    private final String name;

    private ModuleName( final String name )
    {
        Preconditions.checkNotNull( name, "module name cannot be null" );
        Preconditions.checkArgument( !name.trim().isEmpty(), "module name cannot be empty" );
        Preconditions.checkArgument( !name.trim().isEmpty(), "module name cannot be empty" );
        Preconditions.checkArgument( name.matches( "\\p{javaLowerCase}*" ), "module name must be in lower case: " + name );
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

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    public static ModuleName from( String name )
    {
        return new ModuleName( name );
    }
}
