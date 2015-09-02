package com.enonic.xp.portal.impl.handler;

import com.google.common.base.Strings;

import com.enonic.xp.portal.PortalRequest;

public abstract class EndpointHandler
    extends BaseHandler
{
    private final String pathPrefix;

    public EndpointHandler( final String type )
    {
        super( 0 );
        this.pathPrefix = "/_/" + type + "/";
    }

    @Override
    public final boolean canHandle( final PortalRequest req )
    {
        final String endpointPath = Strings.nullToEmpty( req.getEndpointPath() );
        return endpointPath.contains( this.pathPrefix );
    }

    protected final String findRestPath( final PortalRequest req )
    {
        final String endpointPath = Strings.nullToEmpty( req.getEndpointPath() );
        return endpointPath.substring( this.pathPrefix.length() );
    }
}
