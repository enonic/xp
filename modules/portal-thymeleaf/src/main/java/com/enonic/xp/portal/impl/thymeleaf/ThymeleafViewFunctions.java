package com.enonic.xp.portal.impl.thymeleaf;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlParams;

final class ThymeleafViewFunctions
{
    protected PortalUrlService urlService;

    protected PortalContext context;

    private static Multimap<String, String> toMap( final List<String> params )
    {
        final Multimap<String, String> map = HashMultimap.create();
        for ( final String param : params )
        {
            addParam( map, param );
        }

        return map;
    }

    private static void addParam( final Multimap<String, String> map, final String param )
    {
        final int pos = param.indexOf( '=' );
        if ( ( pos <= 0 ) || ( pos >= param.length() ) )
        {
            return;
        }

        final String key = param.substring( 0, pos ).trim();
        final String value = param.substring( pos + 1 ).trim();
        map.put( key, value );
    }

    public String assetUrl( final List<String> params )
    {
        return assetUrl( toMap( params ) );
    }

    private String assetUrl( final Multimap<String, String> map )
    {
        final AssetUrlParams params = new AssetUrlParams().setAsMap( map ).context( this.context );
        return this.urlService.assetUrl( params );
    }

    public String pageUrl( final List<String> params )
    {
        return pageUrl( toMap( params ) );
    }

    private String pageUrl( final Multimap<String, String> map )
    {
        final PageUrlParams params = new PageUrlParams().setAsMap( map ).context( this.context );
        return this.urlService.pageUrl( params );
    }

    public String attachmentUrl( final List<String> params )
    {
        return attachmentUrl( toMap( params ) );
    }

    private String attachmentUrl( final Multimap<String, String> map )
    {
        final AttachmentUrlParams params = new AttachmentUrlParams().setAsMap( map ).context( this.context );
        return this.urlService.attachmentUrl( params );
    }

    public String componentUrl( final List<String> params )
    {
        return componentUrl( toMap( params ) );
    }

    private String componentUrl( final Multimap<String, String> map )
    {
        final ComponentUrlParams params = new ComponentUrlParams().setAsMap( map ).context( this.context );
        return this.urlService.componentUrl( params );
    }

    public String imageUrl( final List<String> params )
    {
        return imageUrl( toMap( params ) );
    }

    private String imageUrl( final Multimap<String, String> map )
    {
        final ImageUrlParams params = new ImageUrlParams().setAsMap( map ).context( this.context );
        return this.urlService.imageUrl( params );
    }

    public String serviceUrl( final List<String> params )
    {
        return serviceUrl( toMap( params ) );
    }

    private String serviceUrl( final Multimap<String, String> map )
    {
        final ServiceUrlParams params = new ServiceUrlParams().setAsMap( map ).context( this.context );
        return this.urlService.serviceUrl( params );
    }
}
