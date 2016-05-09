package com.enonic.xp.portal.impl.handler.asset;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.handler.EndpointHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

@Component(immediate = true, service = WebHandler.class)
public final class AssetHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/^:]+)(:[^/]+)?/(.+)" );

    private ResourceService resourceService;

    public AssetHandler()
    {
        super( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ), "asset" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final String endpointSubPath = getEndpointSubPath( webRequest );
        final Matcher matcher = PATTERN.matcher( endpointSubPath );
        if ( !matcher.find() )
        {
            throw notFound( "Not a valid asset url pattern" );
        }

        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
        return AssetHandlerWorker.create().
            webRequest( webRequest ).
            webResponse( webResponse ).
            resourceService( resourceService ).
            applicationKey( applicationKey ).
            name( matcher.group( 3 ) ).
            cacheable( matcher.group( 2 ) != null ).
            build().
            execute();
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
