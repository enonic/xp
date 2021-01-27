package com.enonic.xp.portal.impl.handler.asset;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

import static com.google.common.base.Strings.nullToEmpty;

final class AssetHandlerWorker
    extends PortalHandlerWorker<WebRequest>
{
    ResourceService resourceService;

    ApplicationKey applicationKey;

    String fingerprint;

    String path;

    String cacheControlHeaderConfig;

    public AssetHandlerWorker( final WebRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        if ( request.getMethod() == HttpMethod.OPTIONS )
        {
            // it will be handled by default OPTIONS handler in BaseWebHandler
            return PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        }

        final ResourceKey assetsKey = ResourceKey.assets( applicationKey );
        final String assetPath = assetsKey.getPath() + path;

        final ResourceKey resourceKey = ResourceKey.from( applicationKey, assetPath );

        final Resource resource = resolveResource( resourceKey );

        final String type = MediaTypes.instance().fromFile( resource.getKey().getName() ).toString();
        final PortalResponse.Builder portalResponse = PortalResponse.create().
            body( resource ).
            contentType( MediaType.parse( type ) );

        if ( !nullToEmpty( this.fingerprint ).isBlank() && !nullToEmpty( cacheControlHeaderConfig ).isBlank() &&
            RunMode.get() != RunMode.DEV && resourceKey.getPath().equals( assetPath ) && fingerpintMatches( fingerprint, assetsKey ) )
        {
            portalResponse.header( HttpHeaders.CACHE_CONTROL, cacheControlHeaderConfig );
        }
        return portalResponse.build();
    }

    private Resource resolveResource( final ResourceKey resourceKey )
    {
        final Resource resource = resourceService.getResource( resourceKey );
        if ( !resource.exists() )
        {
            throw WebException.notFound( String.format( "Resource [%s] not found", resourceKey ) );
        }
        return resource;
    }

    private boolean fingerpintMatches( String providedFingerprint, final ResourceKey assetsKey )
    {
        return resourceService.resourceHash( assetsKey ).
            map( hashCode -> hashCode.toString().equals( providedFingerprint ) ).
            orElse( false );
    }
}
