package com.enonic.wem.portal.internal.view;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.url.AssetUrlBuilder;
import com.enonic.wem.portal.url.AttachmentUrlBuilder;
import com.enonic.wem.portal.url.ComponentUrlBuilder;
import com.enonic.wem.portal.url.GeneralUrlBuilder;
import com.enonic.wem.portal.url.ImageUrlBuilder;
import com.enonic.wem.portal.url.PageUrlBuilder;
import com.enonic.wem.portal.url.PortalUrlBuilders;
import com.enonic.wem.portal.url.ServiceUrlBuilder;
import com.enonic.wem.portal.view.ViewFunctions;

@Component
public final class ViewFunctionsImpl
    implements ViewFunctions
{
    private String getSystemParam( final Multimap<String, String> params, final String name )
    {
        final Collection<String> values = params.removeAll( name );
        if ( values == null )
        {
            return null;
        }

        if ( values.isEmpty() )
        {
            return null;
        }

        return values.iterator().next();
    }

    private PortalUrlBuilders urlBuilders()
    {
        final PortalContext context = PortalContextAccessor.get();
        return new PortalUrlBuilders( context );
    }

    @Override
    public String url( final Multimap<String, String> params )
    {
        final GeneralUrlBuilder builder = urlBuilders().generalUrl();
        builder.path( getSystemParam( params, "_path" ) );
        builder.params( params );
        return builder.toString();
    }

    @Override
    public String assetUrl( final Multimap<String, String> params )
    {
        final AssetUrlBuilder builder = urlBuilders().assetUrl();
        builder.path( getSystemParam( params, "_path" ) );
        builder.params( params );
        return builder.toString();
    }

    @Override
    public String pageUrl( final Multimap<String, String> params )
    {
        final PageUrlBuilder builder = urlBuilders().pageUrl();
        builder.params( params );
        return builder.toString();
    }

    @Override
    public String attachmentUrl( final Multimap<String, String> params )
    {
        final AttachmentUrlBuilder builder = urlBuilders().attachmentUrl();
        builder.params( params );
        return builder.toString();
    }

    @Override
    public String componentUrl( final Multimap<String, String> params )
    {
        final ComponentUrlBuilder builder = urlBuilders().componentUrl();
        builder.component( getSystemParam( params, "_component" ) );
        builder.params( params );
        return builder.toString();
    }

    @Override
    public String imageUrl( final Multimap<String, String> params )
    {
        final ImageUrlBuilder builder = urlBuilders().imageUrl();
        builder.imageId( getSystemParam( params, "_id" ) );
        builder.imageName( getSystemParam( params, "_name" ) );
        builder.quality( getSystemParam( params, "_quality" ) );
        builder.filter( getSystemParam( params, "_filter" ) );
        builder.background( getSystemParam( params, "_background" ) );
        builder.params( params );
        return builder.toString();
    }

    @Override
    public String serviceUrl( final Multimap<String, String> params )
    {
        final ServiceUrlBuilder builder = urlBuilders().serviceUrl();
        builder.service( getSystemParam( params, "_service" ) );
        builder.params( params );
        return builder.toString();
    }
}
