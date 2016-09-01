package com.enonic.xp.portal.impl.handler.asset;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.portal.handler.PortalHandler;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.web.HttpMethod;

@Component(immediate = true, service = PortalHandler.class)
public final class AssetHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/^:]+)(:[^/]+)?/(.+)" );

    private ResourceService resourceService;

    public AssetHandler()
    {
        super( "asset" );
        setMethodsAllowed( HttpMethod.GET, HttpMethod.HEAD );
    }

    @Override
    protected PortalHandlerWorker newWorker( final PortalRequest req )
        throws Exception
    {
        final String restPath = findRestPath( req );

        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw notFound( "Not a valid asset url pattern" );
        }

        final AssetHandlerWorker worker = new AssetHandlerWorker();
        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
        worker.cacheable = matcher.group( 2 ) != null;
        worker.applicationKey = applicationKey;
        worker.name = matcher.group( 3 );
        worker.resourceService = this.resourceService;

        return worker;
    }

    @Override
    protected void checkAdminAccess( final PortalRequest req )
    {
        //Asset is accessible everywhere
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
