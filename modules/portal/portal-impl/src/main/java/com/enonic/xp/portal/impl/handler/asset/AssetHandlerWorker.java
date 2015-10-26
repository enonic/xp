package com.enonic.xp.portal.impl.handler.asset;

import java.util.concurrent.TimeUnit;

import com.google.common.net.MediaType;

import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.handler.PortalHandlerWorker;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpStatus;

final class AssetHandlerWorker
    extends PortalHandlerWorker
{
    private final static long CACHE_TIME = TimeUnit.MINUTES.toSeconds( 10 );

    protected ResourceService resourceService;

    protected ResourceKey resourceKey;

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

        if ( this.request.getMode() == RenderMode.LIVE )
        {
            this.response.header( "Cache-Control", "no-transform, max-age=" + CACHE_TIME );
        }
        else if ( cacheable )
        {
            setResponseCacheable( true );
        }
    }

    private void resolveResource()
    {
        this.resource = this.resourceService.getResource( this.resourceKey );
        if ( !this.resource.exists() )
        {
            throw notFound( "Resource [%s] not found", this.resourceKey );
        }
    }
}
