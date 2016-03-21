package com.enonic.xp.macro;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import java.util.Objects;

@Beta
public class Macro
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

    public ImmutableMap<String, String> getParams()
    {
        return params;
    }

    public String getParam( final String key )
    {
        return this.params.get( key );
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

    public String toString()
    {
        return this.key.toString() + "=" + this.body + "[" + Joiner.on( "," ).withKeyValueSeparator( "=" ).join( this.params ) + "]";
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final MacroKey key, final String body )
    {
        return new Builder( key, body );
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

        private Builder( final MacroKey key, final String body )
        {
            this.key = key;
            this.body = body;
            this.paramsBuilder = ImmutableMap.builder();
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
