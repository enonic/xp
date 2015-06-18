package com.enonic.xp.lib.portal.url;

import java.util.Map;

import com.enonic.xp.portal.url.PortalUrlService;

public final class UrlService
{
    private PortalUrlService urlService;

    public String createAssetUrl( final Map<String, Object> params )
    {
        return new AssetUrlHandler( this.urlService ).createUrl( params );
    }

    public String createAttachmentUrl( final Map<String, Object> params )
    {
        return new AttachmentUrlHandler( this.urlService ).createUrl( params );
    }

    public String createComponentUrl( final Map<String, Object> params )
    {
        return new ComponentUrlHandler( this.urlService ).createUrl( params );
    }

    public String createImageUrl( final Map<String, Object> params )
    {
        return new ImageUrlHandler( this.urlService ).createUrl( params );
    }

    public String createPageUrl( final Map<String, Object> params )
    {
        return new PageUrlHandler( this.urlService ).createUrl( params );
    }

    public String createProcessUrl( final Map<String, Object> params )
    {
        return new ProcessHtmlHandler( this.urlService ).createUrl( params );
    }

    public String createServiceUrl( final Map<String, Object> params )
    {
        return new ServiceUrlHandler( this.urlService ).createUrl( params );
    }

    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
