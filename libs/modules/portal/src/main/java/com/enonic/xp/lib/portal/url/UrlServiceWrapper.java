package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.portal.url.PortalUrlService;

public final class UrlServiceWrapper
{
    private PortalUrlService urlService;

    public String assetUrl( final ScriptValue params )
    {
        return new AssetUrlHandler( this.urlService ).createUrl( params.getMap() );
    }

    public String attachmentUrl( final ScriptValue params )
    {
        return new AttachmentUrlHandler( this.urlService ).createUrl( params.getMap() );
    }

    public String componentUrl( final ScriptValue params )
    {
        return new ComponentUrlHandler( this.urlService ).createUrl( params.getMap() );
    }

    public String imageUrl( final ScriptValue params )
    {
        return new ImageUrlHandler( this.urlService ).createUrl( params.getMap() );
    }

    public String pageUrl( final ScriptValue params )
    {
        return new PageUrlHandler( this.urlService ).createUrl( params.getMap() );
    }

    public String serviceUrl( final ScriptValue params )
    {
        return new ServiceUrlHandler( this.urlService ).createUrl( params.getMap() );
    }

    public String processHtml( final ScriptValue params )
    {
        return new ProcessHtmlHandler( this.urlService ).createUrl( params.getMap() );
    }

    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
