package com.enonic.xp.portal.macro;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.portal.PortalRequest;

@Beta
public final class MacroContext
{
    private final String name;

    private final String body;

    private final ImmutableMap<String, String> params;

    private final PortalRequest request;

    private MacroContext( final Builder builder )
    {
        this.name = builder.name;
        this.body = builder.body;
        this.params = builder.paramsBuilder.build();
        this.request = builder.request;
    }

    public String getName()
    {
        return name;
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

    public ImmutableMap<String, String> getParams()
    {
        return params;
    }

    public PortalRequest getRequest()
    {
        return request;
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
        final MacroContext that = (MacroContext) o;
        return Objects.equals( name, that.name ) &&
            Objects.equals( body, that.body ) &&
            Objects.equals( params, that.params ) &&
            Objects.equals( request, that.request );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, body, params, request );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "name", name ).
            add( "body", body ).
            add( "params", params ).
            add( "request", request ).toString();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder copyOf( final MacroContext macroContext )
    {
        return new Builder( macroContext );
    }

    public static class Builder
    {

        private String name;

        private String body;

        private final ImmutableMap.Builder<String, String> paramsBuilder;

        private PortalRequest request;

        public Builder()
        {
            this.paramsBuilder = ImmutableMap.builder();
        }

        private Builder( final MacroContext macroContext )
        {
            this.name = macroContext.name;
            this.body = macroContext.body;
            this.paramsBuilder = ImmutableMap.builder();
            this.paramsBuilder.putAll( macroContext.params );
            this.request = macroContext.request;
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

        public Builder param( final String name, final String value )
        {
            this.paramsBuilder.put( name, value );
            return this;
        }

        public Builder request( final PortalRequest request )
        {
            this.request = request;
            return this;
        }

        public MacroContext build()
        {
            return new MacroContext( this );
        }
    }
}
