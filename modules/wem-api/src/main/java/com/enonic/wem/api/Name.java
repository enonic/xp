package com.enonic.wem.api;


import com.google.common.base.Preconditions;

public class Name
{
    private final String value;

    public Name( final String name )
    {
        Preconditions.checkNotNull( name, "name cannot be null" );
        Preconditions.checkArgument( !name.trim().isEmpty(), "name cannot be empty" );
        Preconditions.checkArgument( name.matches( "[_a-z]([a-z0-9_\\-\\.])*" ),
                                     "A name can only start with lower case latin letters, and further consist of the same, digits or the following special chars: _-.: " +
                                         name );
        this.value = name;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof Name ) && ( (Name) o ).value.equals( this.value );
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    @Override
    public String toString()
    {
        return value;
    }

    public static Name from( final String name )
    {
        return new Name( name );
    }
}
