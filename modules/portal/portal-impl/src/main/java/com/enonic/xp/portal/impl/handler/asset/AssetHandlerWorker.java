package com.enonic.xp.portal.impl.handler.asset;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

final class AssetHandlerWorker
    extends PortalHandlerWorker
{
    protected ResourceService resourceService;

    protected ResourceKey resourceKey;

    protected boolean cacheable;

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

        final Resource resource = resolveResource();

        final String type = MediaTypes.instance().fromFile( resource.getKey().getName() ).toString();
        final PortalResponse.Builder portalResponse = PortalResponse.create().
            body( resource ).
            contentType( MediaType.parse( type ) );

        if ( cacheable )
        {
            final String cacheControlValue = "public, no-transform, max-age=31536000";
            portalResponse.header( HttpHeaders.CACHE_CONTROL, cacheControlValue );
        }
        return portalResponse.build();
    }

    private Resource resolveResource()
    {
        final Resource resource = this.resourceService.getResource( resourceKey );
        if ( !resource.exists() )
        {
            throw WebException.notFound( String.format( "Resource [%s] not found", resourceKey ) );
        }
        return resource;
    }
}
