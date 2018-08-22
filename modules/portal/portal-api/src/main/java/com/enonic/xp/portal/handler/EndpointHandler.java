package com.enonic.xp.portal.handler;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.handler.BaseWebHandler;

public abstract class EndpointHandler
    extends BaseWebHandler
{
    private final Pattern pathPrefix;


    public EndpointHandler( final String type )
    {
        pathPrefix = Pattern.compile( "^/_/" + type + "(/|$)" );
    }

    public EndpointHandler( final EnumSet<HttpMethod> methodsAllowed, final String type )
    {
        super( methodsAllowed );
        pathPrefix = Pattern.compile( "^/_/" + type + "(/|$)" );
    }

    @Override
    public boolean canHandle( final WebRequest req )
    {
        final String endpointPath = Strings.nullToEmpty( req.getEndpointPath() );
        return pathPrefix.matcher( endpointPath ).find();
    }

    protected final String findRestPath( final WebRequest req )
    {
        final String endpointPath = Strings.nullToEmpty( req.getEndpointPath() );
        final Matcher matcher = pathPrefix.matcher( endpointPath );
        matcher.find();
        return endpointPath.substring(matcher.group( 0 ).length());
    }

    protected boolean isPortalBase( final WebRequest req )
    {
        return req instanceof PortalRequest && ( (PortalRequest) req ).isPortalBase();
    }
}
