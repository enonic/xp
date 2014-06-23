package com.enonic.wem.portal.script.lib;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

public final class PortalUrlScriptBean
{
    private static final String PUBLIC_RESOURCE_TYPE = "public";

    private static final String SERVICE_RESOURCE_TYPE = "service";

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

    public PortalUrlBuilder createUrl( final String path )
    {
        return PortalUrlBuilder.createUrl( this.baseUrl ).
            mode( this.mode ).
            workspace( this.workspace ).
            resourcePath( path );
    }

    public PortalUrlBuilder createResourceUrl( final String resourcePath )
    {
        return PortalUrlBuilder.createUrl( this.baseUrl ).
            mode( this.mode ).
            workspace( this.workspace ).
            contentPath( contentPath ).
            resourceType( PUBLIC_RESOURCE_TYPE ).
            module( this.module ).
            resourcePath( resourcePath );
    }

    public PortalImageUrlBuilder createImageUrl( final String name )
    {
        return PortalImageUrlBuilder.createImageUrl( this.baseUrl ).
            mode( this.mode ).
            workspace( this.workspace ).
            contentPath( contentPath ).
            resourcePath( name );
    }

    public PortalImageByIdUrlBuilder createImageByIdUrl( final ContentId contentId )
    {
        return PortalImageByIdUrlBuilder.createImageUrl( this.baseUrl ).
            mode( this.mode ).
            workspace( this.workspace ).
            contentPath( contentPath ).
            imageContent( contentId );
    }

    public PortalUrlBuilder createServiceUrl( final String name )
    {
        return PortalUrlBuilder.createUrl( this.baseUrl ).
            mode( this.mode ).
            workspace( this.workspace ).
            contentPath( contentPath ).
            resourceType( SERVICE_RESOURCE_TYPE ).
            resourcePath( name );
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
