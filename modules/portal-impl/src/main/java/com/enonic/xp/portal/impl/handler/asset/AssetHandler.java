package com.enonic.xp.portal.impl.handler.asset;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.PortalHandler;
import com.enonic.xp.portal.impl.handler.EndpointHandler;
import com.enonic.xp.portal.impl.handler.PortalHandlerWorker;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.web.HttpMethod;

@Component(immediate = true, service = PortalHandler.class)
public final class AssetHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/(.+)" );

    private final static String ASSET_PREFIX = "site/assets/";

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

        final AssetHandlerWorker worker = new AssetHandlerWorker();
        worker.resourceKey = resolveResourceKey( restPath );
        worker.resourceService = this.resourceService;

        return worker;
    }

    private ResourceKey resolveResourceKey( final String restPath )
    {
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw notFound( "Not a valid asset url pattern" );
        }

        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
        return ResourceKey.from( applicationKey, ASSET_PREFIX + matcher.group( 2 ) );
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
