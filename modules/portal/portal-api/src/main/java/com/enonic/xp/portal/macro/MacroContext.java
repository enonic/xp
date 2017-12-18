package com.enonic.xp.portal.macro;

import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.base.Ascii;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.portal.PortalRequest;

@Beta
public final class MacroContext
{
    private final String name;

    private final String body;

    private final ImmutableListMultimap<String, String> params;

    private final PortalRequest request;

    private final String document;

    private MacroContext( final Builder builder )
    {
        this.name = builder.name;
        this.body = builder.body;
        this.params = builder.paramsBuilder.build();
        this.request = builder.request;
        this.document = builder.document == null ? "" : builder.document;
    }

    public String getName()
    {
        return name;
    }

    public String getBody()
    {
        return body;
    }

    @Deprecated
    public String getParam( final String name )
    {
        final ImmutableList<String> values = this.params.get( name );
        return values.isEmpty() ? null : values.stream().collect( Collectors.joining( "," ) );
    }

    @Deprecated
    public ImmutableMap<String, String> getParams()
    {
        final ImmutableMap.Builder<String, String> mapParams = ImmutableMap.builder();
        for ( String key : this.params.keySet() )
        {
            final String value = this.params.get( key ).stream().collect( Collectors.joining( "," ) );
            mapParams.put( key, value );
        }
        return mapParams.build();
    }

    public ImmutableList<String> getParameter( final String name )
    {
        return this.params.get( name );
    }

    public ImmutableListMultimap<String, String> getParameters()
    {
        return params;
    }

    public PortalRequest getRequest()
    {
        return request;
    }

    public String getDocument()
    {
        return document;
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
        return Objects.equals( name, that.name ) && Objects.equals( body, that.body ) && Objects.equals( params, that.params ) &&
            Objects.equals( request, that.request ) && Objects.equals( document, that.document );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, body, params, request, document );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "name", name ).
            add( "body", body ).
            add( "params", params ).
            add( "request", request ).
            add( "document", Ascii.truncate( document, 20, "..." ) ).
            toString();
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

        private final ImmutableListMultimap.Builder<String, String> paramsBuilder;

        private PortalRequest request;

        private String document;

        public Builder()
        {
            this.paramsBuilder = ImmutableListMultimap.builder();
        }

        private Builder( final MacroContext macroContext )
        {
            this.name = macroContext.name;
            this.body = macroContext.body;
            this.paramsBuilder = ImmutableListMultimap.builder();
            this.paramsBuilder.putAll( macroContext.params );
            this.request = macroContext.request;
            this.document = macroContext.getDocument();
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

        public Builder document( final String document )
        {
            this.document = document;
            return this;
        }

        public MacroContext build()
        {
            return new MacroContext( this );
        }
    }
}
