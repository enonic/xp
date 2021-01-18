package com.enonic.xp.portal.impl.handler.asset;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class AssetHandler
    extends EndpointHandler
{
    private static final Pattern PATTERN = Pattern.compile( "([^/^:]+)(?::([^/]+))?/(.+)" );

    private ResourceService resourceService;

    @Activate
    public AssetHandler( @Reference final ResourceService resourceService )
    {
        super( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ), "asset" );
        this.resourceService = resourceService;
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

        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
        final String fingerprint = matcher.group( 2 );
        final String path = matcher.group( 3 );
        final ResourceKey assetsKey = ResourceKey.assets( applicationKey );
        final String assetPath = assetsKey.getPath() + path;
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, assetPath );

        final AssetHandlerWorker worker = new AssetHandlerWorker( webRequest );

        worker.resourceKey = resourceKey;
        worker.resourceService = this.resourceService;

        worker.cacheable = fingerprint != null && RunMode.get() != RunMode.DEV && resourceKey.getPath().equals( assetPath ) &&
            fingerpintMatches( fingerprint, assetsKey );

        return worker.execute();
    }

    private boolean fingerpintMatches( String providedFingerprint, final ResourceKey assetsKey )
    {
        return resourceService.resourceHash( assetsKey ).
            map( hashCode -> hashCode.toString().equals( providedFingerprint ) ).
            orElse( false );
    }
}
