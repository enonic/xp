package com.enonic.xp.inputtype;

import java.util.Objects;

public final class InputTypeProperty
{
    private final String name;

    private final GenericValue value;

    private InputTypeProperty( final Builder builder )
    {
        this.name = builder.name;
        this.value = builder.value;
    }

    public String getName()
    {
        return this.name;
    }

    public GenericValue getValue()
    {
        return this.value;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof InputTypeProperty ) )
        {
            return false;
        }
        final InputTypeProperty that = (InputTypeProperty) o;
        return Objects.equals( name, that.name ) && Objects.equals( value, that.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, value );
    }

    @Override
    public String toString()
    {
        return this.name + "=" + this.value;
    }

    public static Builder create( final String name, final GenericValue value )
    {
        return new Builder( name, value );
    }

    public static final class Builder
    {
        private final String name;

        private final GenericValue value;

        private Builder( final String name, final GenericValue value )
        {
            this.name = name;
            this.value = value != null ? value : GenericValue.stringValue( "" );
        }

        public InputTypeProperty build()
        {
            return new InputTypeProperty( this );
        }
    }
}
