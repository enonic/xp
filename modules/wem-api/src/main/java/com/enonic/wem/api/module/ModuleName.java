package com.enonic.wem.api.module;

import com.google.common.base.Preconditions;

public final class ModuleName
{
    private final String name;

    private ModuleName( final String name )
    {
        Preconditions.checkArgument( !name.contains( ModuleKey.SEPARATOR ),
                                     "ModuleName [" + name + "] cannot contain " + ModuleKey.SEPARATOR );
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
