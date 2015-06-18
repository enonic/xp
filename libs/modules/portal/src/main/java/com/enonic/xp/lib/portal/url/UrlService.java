package com.enonic.xp.lib.portal.url;

import java.util.Map;

import com.enonic.xp.portal.url.PortalUrlService;

public final class UrlService
{
    private PortalUrlService urlService;

    public void createAssetUrl( final Map<String, Object> params )
    {
        new AssetUrlHandler( this.urlService ).createUrl( params );
    }

    public void createAttachmentUrl( final Map<String, Object> params )
    {
        new AttachmentUrlHandler( this.urlService ).createUrl( params );
    }

    public void createComponentUrl( final Map<String, Object> params )
    {
        new ComponentUrlHandler( this.urlService ).createUrl( params );
    }

    public void createImageUrl( final Map<String, Object> params )
    {
        new ImageUrlHandler( this.urlService ).createUrl( params );
    }

    public void createPageUrl( final Map<String, Object> params )
    {
        new PageUrlHandler( this.urlService ).createUrl( params );
    }

    public void createProcessUrl( final Map<String, Object> params )
    {
        new ProcessHtmlHandler( this.urlService ).createUrl( params );
    }

    public void createServiceUrl( final Map<String, Object> params )
    {
        new ServiceUrlHandler( this.urlService ).createUrl( params );
    }

    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
