package com.enonic.xp.web.handler;

import com.google.common.base.Strings;

public abstract class EndpointWebHandler
    extends BaseWebHandler
{
    private final String pathPrefix;

    public EndpointWebHandler( final String type )
    {
        this.pathPrefix = "/_/" + type + "/";
    }

    public EndpointWebHandler( final int order, final String type )
    {
        super( order );
        this.pathPrefix = "/_/" + type + "/";
    }

    @Override
    public final boolean canHandle( final WebRequest webRequest )
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
