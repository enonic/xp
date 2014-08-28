package com.enonic.wem.portal.internal.script.lib;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;
import com.enonic.wem.portal.url.GeneralUrlBuilder;
import com.enonic.wem.portal.url.ImageUrlBuilder;
import com.enonic.wem.portal.url.PublicUrlBuilder;
import com.enonic.wem.portal.url.ServiceUrlBuilder;

public final class PortalUrlScriptBean
{
    private String mode;

    private String workspace;

    private String baseUrl;

    private String contentPath;

    private String module;

    public PortalUrlScriptBean()
    {
        this.baseUrl = ServletRequestUrlHelper.createUri( "" );
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

    public void setMode( final String mode )
    {
        this.mode = mode;
    }

    public void setContentPath( final String contentPath )
    {
        this.contentPath = contentPath;
    }

    public void setModule( final String module )
    {
        this.module = module;
    }

    public void setWorkspace( final String workspace )
    {
        this.workspace = workspace;
    }
}
