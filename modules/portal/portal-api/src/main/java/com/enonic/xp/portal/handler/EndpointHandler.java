package com.enonic.xp.portal.handler;

import java.util.EnumSet;

import com.google.common.base.Strings;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.handler.BaseWebHandler;

public abstract class EndpointHandler
    extends BaseWebHandler
{
    private final String pathPrefix;

    public EndpointHandler( final String type )
    {
        this.pathPrefix = "/_/" + type + "/";
    }

    public EndpointHandler( final EnumSet<HttpMethod> methodsAllowed, final String type )
    {
        super( methodsAllowed );
        this.pathPrefix = "/_/" + type + "/";
    }

    @Override
    public boolean canHandle( final WebRequest req )
    {
        final String endpointPath = Strings.nullToEmpty( req.getEndpointPath() );
        return endpointPath.startsWith( this.pathPrefix );
    }

    protected final String findRestPath( final WebRequest req )
    {
        final String endpointPath = Strings.nullToEmpty( req.getEndpointPath() );
        return endpointPath.substring( this.pathPrefix.length() );
    }

    protected boolean isPortalBase( final WebRequest req )
    {
        return req instanceof PortalRequest && ( (PortalRequest) req ).isPortalBase();
    }
}
