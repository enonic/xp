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
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

final class AssetHandlerWorker
    extends PortalHandlerWorker
{
    private final static String ROOT_ASSET_PREFIX = "assets/";

    private final static String SITE_ASSET_PREFIX = "site/assets/";

    private ResourceService resourceService;

    private ApplicationKey applicationKey;

    private String name;

    private boolean cacheable;

    private AssetHandlerWorker( final Builder builder )
    {
        super( builder );
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
    public WebResponse execute()
    {
        final Resource resource = resolveResource();

        webResponse.setStatus( HttpStatus.OK );
        webResponse.setBody( resource );

        final String type = MediaTypes.instance().fromFile( resource.getKey().getName() ).toString();
        this.webResponse.setContentType( MediaType.parse( type ) );

        if ( cacheable )
        {
            final String cacheControlValue = "public, no-transform, max-age=31536000";
            webResponse.setHeader( HttpHeaders.CACHE_CONTROL, cacheControlValue );
        }

        return webResponse;
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
        extends PortalHandlerWorker.Builder<Builder, WebRequest, WebResponse>
    {
        private ResourceService resourceService;

        private ApplicationKey applicationKey;

        private String name;

        private boolean cacheable;

        private Builder()
        {
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

        public AssetHandlerWorker build()
        {
            return new AssetHandlerWorker( this );
        }
    }
}
