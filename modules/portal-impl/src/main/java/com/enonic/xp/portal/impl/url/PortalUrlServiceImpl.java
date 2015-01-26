package com.enonic.xp.portal.impl.url;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.url.AssetUrlBuilder;
import com.enonic.xp.portal.url.AttachmentUrlBuilder;
import com.enonic.xp.portal.url.ComponentUrlBuilder;
import com.enonic.xp.portal.url.ImageUrlBuilder;
import com.enonic.xp.portal.url.PageUrlBuilder;
import com.enonic.xp.portal.url.PortalUrlBuilders;
import com.enonic.xp.portal.url.PortalUrlBuildersHelper;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlBuilder;

@Component(immediate = true)
public final class PortalUrlServiceImpl
    implements PortalUrlService
{
    @Override
    public String assetUrl( final PortalContext context, final Multimap<String, String> params )
    {
        final AssetUrlBuilder builder = newBuilders( context ).assetUrl();
        return PortalUrlBuildersHelper.apply( builder, params ).build();
    }

    @Override
    public String serviceUrl( final PortalContext context, final Multimap<String, String> params )
    {
        final ServiceUrlBuilder builder = newBuilders( context ).serviceUrl();
        return PortalUrlBuildersHelper.apply( builder, params ).build();
    }

    @Override
    public String imageUrl( final PortalContext context, final Multimap<String, String> params )
    {
        final ImageUrlBuilder builder = newBuilders( context ).imageUrl();
        return PortalUrlBuildersHelper.apply( builder, params ).build();
    }

    @Override
    public String componentUrl( final PortalContext context, final Multimap<String, String> params )
    {
        final ComponentUrlBuilder builder = newBuilders( context ).componentUrl();
        return PortalUrlBuildersHelper.apply( builder, params ).build();
    }

    @Override
    public String pageUrl( final PortalContext context, final Multimap<String, String> params )
    {
        final PageUrlBuilder builder = newBuilders( context ).pageUrl();
        return PortalUrlBuildersHelper.apply( builder, params ).build();
    }

    @Override
    public String attachmentUrl( final PortalContext context, final Multimap<String, String> params )
    {
        final AttachmentUrlBuilder builder = newBuilders( context ).attachmentUrl();
        return PortalUrlBuildersHelper.apply( builder, params ).build();
    }

    private PortalUrlBuilders newBuilders( final PortalContext context )
    {
        return new PortalUrlBuilders( context );
    }
}
