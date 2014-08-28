package com.enonic.wem.portal.url;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.portal.PortalContext;

public final class PortalUrlBuilders
{
    private final String mode;

    private final String workspace;

    private final String baseUrl;

    private final String contentPath;

    private final String module;

    public PortalUrlBuilders( final PortalContext context )
    {
        this.baseUrl = context.getRequest().getBaseUri();
        this.workspace = context.getRequest().getWorkspace().toString();
        this.mode = context.getRequest().getMode().toString();
        this.contentPath = context.getContent().getPath().toString();
        this.module = context.getResolvedModule().toString();
    }

    // TODO: Hack!
    public String getBaseUrl()
    {
        return this.baseUrl;
    }

    public GeneralUrlBuilder createUrl( final String path )
    {
        return new GeneralUrlBuilder().
            baseUri( this.baseUrl ).
            mode( this.mode ).
            workspace( this.workspace ).
            contentPath( path );
    }

    public PublicUrlBuilder createResourceUrl( final String resourcePath )
    {
        return new PublicUrlBuilder().
            baseUri( this.baseUrl ).
            mode( this.mode ).
            workspace( this.workspace ).
            contentPath( contentPath ).
            module( this.module ).
            resourcePath( resourcePath );
    }

    public ImageUrlBuilder createImageUrl( final String name )
    {
        return new ImageUrlBuilder().
            baseUri( this.baseUrl ).
            mode( this.mode ).
            workspace( this.workspace ).
            contentPath( contentPath ).
            imageName( name );
    }

    public ImageUrlBuilder createImageByIdUrl( final ContentId contentId )
    {
        return new ImageUrlBuilder().
            baseUri( this.baseUrl ).
            mode( this.mode ).
            workspace( this.workspace ).
            contentPath( contentPath ).
            imageId( contentId );
    }

    public ServiceUrlBuilder createServiceUrl( final String name )
    {
        return new ServiceUrlBuilder().
            baseUri( this.baseUrl ).
            mode( this.mode ).
            workspace( this.workspace ).
            contentPath( contentPath ).
            module( this.module ).
            serviceName( name );
    }
}
