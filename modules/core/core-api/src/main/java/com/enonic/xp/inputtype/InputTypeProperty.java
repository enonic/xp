package com.enonic.xp.inputtype;

import java.util.Map;
import java.util.Objects;

import com.google.common.base.Joiner;
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
        return o instanceof InputTypeProperty && equals( (InputTypeProperty) o );
    }

    private boolean equals( final InputTypeProperty o )
    {
        return Objects.equals( this.name, o.name ) && Objects.equals( this.value, o.value ) && this.attributes.equals( o.attributes );
    }

    public String toString()
    {
        return this.name + "=" + this.value + "[" + Joiner.on( "," ).withKeyValueSeparator( "=" ).join( this.attributes ) + "]";
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
