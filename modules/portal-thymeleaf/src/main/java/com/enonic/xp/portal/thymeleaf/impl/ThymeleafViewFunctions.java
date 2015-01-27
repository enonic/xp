package com.enonic.xp.portal.thymeleaf.impl;

import java.util.List;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlBuilders;
import com.enonic.xp.portal.url.PortalUrlBuildersHelper;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlParams;

final class ThymeleafViewFunctions
{
    protected PortalUrlService urlService;

    protected PortalContext context;

    private PortalUrlBuilders createUrlBuilders()
    {
        return new PortalUrlBuilders( this.context );
    }

    private Multimap<String, String> toMap( final List<String> params )
    {
        return PortalUrlBuildersHelper.toParamMap( params );
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
        return PortalUrlBuildersHelper.apply( createUrlBuilders().attachmentUrl(), toMap( params ) ).toString();
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
