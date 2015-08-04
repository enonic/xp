package com.enonic.xp.form.inputtype;

import java.util.Objects;

import com.google.common.annotations.Beta;

@Beta
public final class InputTypeName
{
    private final String name;

    private InputTypeName( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof InputTypeName ) )
        {
            return false;
        }

        final InputTypeName other = (InputTypeName) o;
        return Objects.equals( this.name, other.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.name );
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    public static InputTypeName from( final String name )
    {
        return new InputTypeName( name );
    }
}
