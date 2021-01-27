package com.enonic.xp.portal.impl.handler.asset;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class, configurationPid = "com.enonic.xp.portal")
public final class AssetHandler
    extends EndpointHandler
{
    private static final Pattern PATTERN = Pattern.compile( "([^/^:]+)(?::([^/]+))?/(.+)" );

    private final ResourceService resourceService;

    private volatile String cacheControlHeader;

    @Activate
    public AssetHandler( @Reference final ResourceService resourceService )
    {
        super( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ), "asset" );
        this.resourceService = resourceService;
    }

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        cacheControlHeader = config.asset_cacheControl();
    }

    @Override
    protected PortalResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        final String restPath = findRestPath( webRequest );

        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw WebException.notFound( "Not a valid asset url pattern" );
        }

        final AssetHandlerWorker assetHandlerWorker = new AssetHandlerWorker( webRequest );
        assetHandlerWorker.resourceService = resourceService;
        assetHandlerWorker.applicationKey = ApplicationKey.from( matcher.group( 1 ) );
        assetHandlerWorker.fingerprint = matcher.group( 2 );
        assetHandlerWorker.path = matcher.group( 3 );
        assetHandlerWorker.cacheControlHeaderConfig = cacheControlHeader;

        return assetHandlerWorker.execute();
    }
}
