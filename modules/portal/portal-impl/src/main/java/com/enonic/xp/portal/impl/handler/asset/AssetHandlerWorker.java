package com.enonic.xp.portal.impl.handler.asset;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpStatus;

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

    @Override
    public void execute()
        throws Exception
    {
        resolveResource();

        this.response.status( HttpStatus.OK );
        this.response.body( resource );

        final String type = MediaTypes.instance().fromFile( this.resource.getKey().getName() ).toString();
        this.response.contentType( MediaType.parse( type ) );

        if ( cacheable )
        {
            final String cacheControlValue = "public, no-transform, max-age=31536000";
            this.response.header( HttpHeaders.CACHE_CONTROL, cacheControlValue );
        }
    }

    private void resolveResource()
    {
        this.resource = this.resourceService.getResource( ResourceKey.from( applicationKey, ROOT_ASSET_PREFIX + this.name ) );
        if ( !this.resource.exists() )
        {
            this.resource = this.resourceService.getResource( ResourceKey.from( applicationKey, SITE_ASSET_PREFIX + this.name ) );
            if ( !this.resource.exists() )
            {
                throw notFound( "Resource [%s] not found", ResourceKey.from( applicationKey, ROOT_ASSET_PREFIX + this.name ) );
            }
        }
    }
}
