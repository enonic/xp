package com.enonic.xp.macro;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

@Beta
public final class Macro
{
    private final MacroKey key;

    private final String body;

    private final ImmutableMap<String, String> params;

    private Macro( final Builder builder )
    {
        this.key = builder.key;
        this.body = builder.body;
        this.params = builder.paramsBuilder.build();
    }

    public MacroKey getKey()
    {
        return key;
    }

    public String getBody()
    {
        return body;
    }

    public Iterable<String> getParamNames()
    {
        return params.keySet();
    }

    public String getParam( final String name )
    {
        return this.params.get( name );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Macro that = (Macro) o;
        return Objects.equals( this.key, that.key ) && Objects.equals( this.body, that.body ) && this.params.equals( that.params );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( key, body, params );
    }

    public String toString()
    {
        final String body =  this.body != null ? ( "=" + this.body ) : "";
        return this.key.toString() + body + "[" +
            Joiner.on( "," ).withKeyValueSeparator( "=" ).join( this.params ) + "]";
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder copyOf( final Macro macro )
    {
        return new Builder( macro );
    }

    public static class Builder
    {

        private MacroKey key;

        private String body;

        private final ImmutableMap.Builder<String, String> paramsBuilder;

        public Builder()
        {
            this.paramsBuilder = ImmutableMap.builder();
        }

        private Builder( final Macro macro )
        {
            this.key = macro.key;
            this.body = macro.body;
            this.paramsBuilder = ImmutableMap.builder();
            this.paramsBuilder.putAll( macro.params );
        }

        public Builder key( final MacroKey key )
        {
            this.key = key;
            return this;
        }

        public Builder body( final String body )
        {
            this.body = body;
            return this;
        }

        public Builder param( final String key, final String value )
        {
            this.paramsBuilder.put( key, value );
            return this;
        }

        public Macro build()
        {
            return new Macro( this );
        }
    }
}
