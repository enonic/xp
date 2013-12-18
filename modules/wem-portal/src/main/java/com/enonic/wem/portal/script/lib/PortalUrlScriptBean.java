package com.enonic.wem.portal.script.lib;

import com.enonic.wem.web.servlet.ServletRequestUrlHelper;

public final class PortalUrlScriptBean
{
    private static final String PUBLIC_URL = "public";

    private static final String IMAGE_URL = "image";

    private static final String SERVICE_URL = "service";

    private String mode;

    private String baseUrl;

    private String contentPath;

    public PortalUrlScriptBean()
    {
        this.baseUrl = ServletRequestUrlHelper.createUrl( "" );
    }

    public PortalUrlBuilder createUrl( final String path )
    {
        return PortalUrlBuilder.createUrl( this.baseUrl ).
            mode( this.mode ).
            resourcePath( path );
    }

    public PortalUrlBuilder createResourceUrl( final String resourcePath )
    {
        return PortalUrlBuilder.createUrl( this.baseUrl ).
            mode( this.mode ).
            contentPath( contentPath ).
            service( PUBLIC_URL ).
            resourcePath( resourcePath );
    }

    public PortalUrlBuilder createImageUrl( final String name )
    {
        return PortalUrlBuilder.createUrl( this.baseUrl ).
            mode( this.mode ).
            contentPath( contentPath ).
            service( IMAGE_URL ).
            resourcePath( name );
    }

    public PortalUrlBuilder createServiceUrl( final String name )
    {
        return PortalUrlBuilder.createUrl( this.baseUrl ).
            mode( this.mode ).
            contentPath( contentPath ).
            service( SERVICE_URL ).
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
}
