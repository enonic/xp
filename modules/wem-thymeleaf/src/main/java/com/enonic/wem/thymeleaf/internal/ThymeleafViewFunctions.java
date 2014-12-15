package com.enonic.wem.thymeleaf.internal;

import java.util.List;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.url.PortalUrlBuilders;
import com.enonic.wem.portal.url.PortalUrlBuildersHelper;

final class ThymeleafViewFunctions
{
    private PortalUrlBuilders createUrlBuilders()
    {
        final PortalContext context = PortalContextAccessor.get();
        return new PortalUrlBuilders( context );
    }

    private Multimap<String, String> toMap( final List<String> params )
    {
        return PortalUrlBuildersHelper.toParamMap( params );
    }

    public String url( final List<String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().generalUrl(), toMap( params ) ).toString();
    }

    public String assetUrl( final List<String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().assetUrl(), toMap( params ) ).toString();
    }

    public String pageUrl( final List<String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().pageUrl(), toMap( params ) ).toString();
    }

    public String attachmentUrl( final List<String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().attachmentUrl(), toMap( params ) ).toString();
    }

    public String componentUrl( final List<String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().componentUrl(), toMap( params ) ).toString();
    }

    public String imageUrl( final List<String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().imageUrl(), toMap( params ) ).toString();
    }

    public String serviceUrl( final List<String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().serviceUrl(), toMap( params ) ).toString();
    }
}
