package com.enonic.xp.portal.impl.handler.asset;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.portal.handler.PortalWebHandlerWorker;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpStatus;

final class AssetWebHandlerWorker
    extends PortalWebHandlerWorker
{
    private final static String ROOT_ASSET_PREFIX = "assets/";

    private final static String SITE_ASSET_PREFIX = "site/assets/";

    private ResourceService resourceService;

    private ApplicationKey applicationKey;

    private String name;

    private boolean cacheable;

    private AssetWebHandlerWorker( final Builder builder )
    {
        portalWebRequest = builder.portalWebRequest;
        portalWebResponse = builder.portalWebResponse;
        resourceService = builder.resourceService;
        applicationKey = builder.applicationKey;
        name = builder.name;
        cacheable = builder.cacheable;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public PortalWebResponse execute()
    {
        final Resource resource = resolveResource();

        portalWebResponse.setStatus( HttpStatus.OK );
        portalWebResponse.setBody( resource );

        final String type = MediaTypes.instance().fromFile( resource.getKey().getName() ).toString();
        this.portalWebResponse.setContentType( MediaType.parse( type ) );

        if ( cacheable )
        {
            final String cacheControlValue = "public, no-transform, max-age=31536000";
            portalWebResponse.setHeader( HttpHeaders.CACHE_CONTROL, cacheControlValue );
        }

        return portalWebResponse;
    }

    private Resource resolveResource()
    {
        Resource resource = this.resourceService.getResource( ResourceKey.from( this.applicationKey, ROOT_ASSET_PREFIX + this.name ) );
        if ( !resource.exists() )
        {
            resource = this.resourceService.getResource( ResourceKey.from( this.applicationKey, SITE_ASSET_PREFIX + this.name ) );
            if ( !resource.exists() )
            {
                throw notFound( "Resource [%s] not found", ResourceKey.from( this.applicationKey, ROOT_ASSET_PREFIX + this.name ) );
            }
        }
        return resource;
    }

    public static final class Builder
    {
        public PortalWebRequest portalWebRequest;

        public PortalWebResponse portalWebResponse;

        private ResourceService resourceService;

        private ApplicationKey applicationKey;

        private String name;

        private boolean cacheable;

        private Builder()
        {
        }

        public Builder portalWebRequest( final PortalWebRequest portalWebRequest )
        {
            this.portalWebRequest = portalWebRequest;
            return this;
        }

        public Builder portalWebResponse( final PortalWebResponse portalWebResponse )
        {
            this.portalWebResponse = portalWebResponse;
            return this;
        }

        public Builder resourceService( final ResourceService resourceService )
        {
            this.resourceService = resourceService;
            return this;
        }

        public Builder applicationKey( final ApplicationKey applicationKey )
        {
            this.applicationKey = applicationKey;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder cacheable( final boolean cacheable )
        {
            this.cacheable = cacheable;
            return this;
        }

        public AssetWebHandlerWorker build()
        {
            return new AssetWebHandlerWorker( this );
        }
    }
}
