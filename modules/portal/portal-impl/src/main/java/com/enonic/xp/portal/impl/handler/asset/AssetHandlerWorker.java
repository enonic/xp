package com.enonic.xp.portal.impl.handler.asset;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;

final class AssetHandlerWorker
    extends PortalHandlerWorker
{
    private final static String ROOT_ASSET_PREFIX = "assets/";

    private final static String SITE_ASSET_PREFIX = "site/assets/";

    protected ResourceService resourceService;

    protected ApplicationKey applicationKey;

    protected String name;

    protected boolean cacheable;

    private Resource resource;

    public AssetHandlerWorker( final WebRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        resolveResource();

        if ( request.getMethod() == HttpMethod.OPTIONS )
        {
            // it will be handled by default OPTIONS handler in BaseWebHandler
            return PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        }

        final String type = MediaTypes.instance().fromFile( this.resource.getKey().getName() ).toString();
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

    private void resolveResource()
    {
        this.resource = this.resourceService.getResource( ResourceKey.from( this.applicationKey, ROOT_ASSET_PREFIX + this.name ) );
        if ( !this.resource.exists() )
        {
            this.resource = this.resourceService.getResource( ResourceKey.from( this.applicationKey, SITE_ASSET_PREFIX + this.name ) );
            if ( !this.resource.exists() )
            {
                throw notFound( "Resource [%s] not found", ResourceKey.from( this.applicationKey, ROOT_ASSET_PREFIX + this.name ) );
            }
        }
    }
}
