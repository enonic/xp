package com.enonic.xp.web.handler;

import java.util.EnumSet;

import com.google.common.base.Strings;

import com.enonic.xp.web.HttpMethod;

public abstract class EndpointHandler
    extends BaseWebHandler
{
    private final String pathPrefix;

    public EndpointHandler( final String type )
    {
        this.pathPrefix = "/_/" + type + "/";
    }

    public EndpointHandler( final int order, final String type )
    {
        super( order );
        this.pathPrefix = "/_/" + type + "/";
    }

    public EndpointHandler( final EnumSet<HttpMethod> methodsAllowed, final String type )
    {
        super( methodsAllowed );
        this.pathPrefix = "/_/" + type + "/";
    }

    public EndpointHandler( final int order, final EnumSet<HttpMethod> methodsAllowed, final String type )
    {
        super( order, methodsAllowed );
        this.pathPrefix = "/_/" + type + "/";
    }

    @Override
    public boolean canHandle( final WebRequest webRequest )
    {
        final String endpointPath = Strings.nullToEmpty( webRequest.getEndpointPath() );
        return endpointPath.startsWith( this.pathPrefix );
    }

    protected final String getEndpointSubPath( final WebRequest webRequest )
    {
        final String endpointPath = Strings.nullToEmpty( webRequest.getEndpointPath() );
        return endpointPath.substring( this.pathPrefix.length() );
    }
}
