package com.enonic.xp.portal.handler;

import java.util.EnumSet;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.handler.BaseWebHandler;

public abstract class EndpointHandler
    extends BaseWebHandler
{
    private final String path;

    private final String pathPrefix;

    public EndpointHandler( final String endpointType )
    {
        this( HttpMethod.standard(), endpointType );
    }

    public EndpointHandler( final EnumSet<HttpMethod> methodsAllowed, final String endpointType )
    {
        super( methodsAllowed );
        this.path = "/_/" + endpointType;
        this.pathPrefix = path + "/";
    }

    @Override
    public boolean canHandle( final WebRequest req )
    {
        final String endpointPath = req.getEndpointPath();
        return endpointPath != null && ( endpointPath.startsWith( pathPrefix ) || endpointPath.equals( path ) );
    }

    protected String findPreRestPath( final WebRequest req )
    {
        final String rawPath = req.getRawPath();
        final int endpointPathIndex = rawPath.indexOf( "/_/" );
        return rawPath.substring( 0, endpointPathIndex + path.length() );
    }

    protected final String findRestPath( final WebRequest req )
    {
        final String endpointPath = req.getEndpointPath();
        return endpointPath.length() > pathPrefix.length() ? endpointPath.substring( pathPrefix.length() ) : "";
    }

}
