package com.enonic.xp.inputtype;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

public final class InputTypeProperty
{
    private final String name;

    private final String value;

    private final ImmutableMap<String, String> attributes;

    private InputTypeProperty( final Builder builder )
    {
        this.name = builder.name;
        this.value = builder.value;
        this.attributes = builder.attributes.build();
    }

    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }

    public Map<String, String> getAttributes()
    {
        return this.attributes;
    }

    public String getAttribute( final String name )
    {
        return this.attributes.get( name );
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
        return Objects.equals( name, that.name ) && Objects.equals( value, that.value ) && Objects.equals( attributes, that.attributes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, value, attributes );
    }

    @Override
    public String toString()
    {
        return this.name + "=" + this.value +
            this.attributes.entrySet().stream().map( e -> e.getKey() + "=" + e.getValue() ).collect( Collectors.joining( ",", "[", "]" ) );
    }

    public static Builder create( final String name, final String value )
    {
        return new Builder( name, value );
    }

    public static class Builder
    {
        private final String name;

        private final String value;

        private final ImmutableMap.Builder<String, String> attributes;

        private Builder( final String name, final String value )
        {
            this.name = name;
            this.value = value != null ? value : "";
            this.attributes = ImmutableMap.builder();
        }

        public Builder attribute( final String name, final String value )
        {
            this.attributes.put( name, value != null ? value : "" );
            return this;
        }

        public InputTypeProperty build()
        {
            return new InputTypeProperty( this );
        }
    }
}
