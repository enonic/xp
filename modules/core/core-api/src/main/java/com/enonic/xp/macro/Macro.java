package com.enonic.xp.macro;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;

@Beta
public final class Macro
{
    private final String name;

    private final String body;

    private final ImmutableMap<String, String> params;

    private Macro( final Builder builder )
    {
        this.name = builder.name;
        this.body = builder.body == null ? "" : builder.body;
        this.params = builder.paramsBuilder.build();
    }

    public String getName()
    {
        return name;
    }

    public String getBody()
    {
        return body;
    }

    public String getParam( final String name )
    {
        return this.params.get( name );
    }

    public ImmutableMap<String, String> getParams()
    {
        return params;
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
        return Objects.equals( this.name, that.name ) && Objects.equals( this.body, that.body ) && this.params.equals( that.params );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, body, params );
    }

    public String toString()
    {
        final StringBuilder result = new StringBuilder( "[" ).append( name );
        if ( params.isEmpty() && body.isEmpty() )
        {
            result.append( "/]" );
        }
        else
        {
            for ( String paramName : params.keySet() )
            {
                result.append( " " ).append( paramName ).append( "=\"" );
                result.append( escapeParam( params.get( paramName ) ) );
                result.append( "\"" );
            }
            if ( body.isEmpty() )
            {
                result.append( "/]" );
            }
            else
            {
                result.append( "]" ).append( body ).append( "[/" ).append( name ).append( "]" );
            }
        }
        return result.toString();
    }

    private String escapeParam( final String value )
    {
        return value.replace( "\\", "\\\\" ).replace( "\"", "\\\"" );
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

        private String name;

        private String body;

        private final ImmutableMap.Builder<String, String> paramsBuilder;

        public Builder()
        {
            this.paramsBuilder = ImmutableMap.builder();
        }

        private Builder( final Macro macro )
        {
            this.name = macro.name;
            this.body = macro.body;
            this.paramsBuilder = ImmutableMap.builder();
            this.paramsBuilder.putAll( macro.params );
        }

        public Builder name( final String name )
        {
            this.name = name;
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
