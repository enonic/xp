package com.enonic.wem.api.form.inputtype;


import java.util.Objects;

public final class InputTypeName
{
    private final String ref;

    private final String name;

    private final boolean custom;

    public static InputTypeName from( final String s )
    {
        if ( s.startsWith( "custom:" ) )
        {
            return new InputTypeName( s.substring( "custom:".length(), s.length() ), true );
        }
        else
        {
            return new InputTypeName( s, false );
        }
    }

    public static InputTypeName from( final InputType inputType )
    {
        return InputTypeName.from( inputType.getName() );
    }

    public InputTypeName( final String name, final boolean custom )
    {
        this.name = name;
        this.custom = custom;
        this.ref = custom ? "custom:" + name : "" + name;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isCustom()
    {
        return this.custom;
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

        return Objects.equals( ref, other.ref ) && Objects.equals( ref, other.ref );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( ref );
    }

    public String toString()
    {
        return ref;
    }
}
