package com.enonic.xp.portal.impl.handler.render;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.content.Content;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.site.Site;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.Reference;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

final class PageHandlerWorker
{
    private static final String SHORTCUT_TARGET_PROPERTY = "target";

    private final PortalRequest request;

    RendererDelegate rendererDelegate;

    PortalUrlService portalUrlService;

    PageResolver pageResolver;

    PageDescriptorService pageDescriptorService;

    PageHandlerWorker( final PortalRequest request )
    {
        this.request = request;
    }

    public PortalResponse execute()
    {
        final Content content = PortalRequestHelper.getContentOrElseThrow( request );

        if ( content.getType().isShortcut() )
        {
            return renderShortcut( content );
        }

        final Site site = PortalRequestHelper.getSiteOrElseThrow( request );

        final PageResolverResult resolvedPage = pageResolver.resolve( content, site.getPath() );

        final Page effectivePage = resolvedPage.getEffectivePageOrElseThrow( request.getMode() );
        final Content effectiveContent = Content.create( content ).page( effectivePage ).build();

        this.request.setContent( effectiveContent );
        this.request.setPageDescriptor( resolvedPage.getPageDescriptor() );
        this.request.setApplicationKey( resolvedPage.getApplicationKey() );

        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", effectiveContent.getPath().toString() );
            trace.put( "type", "page" );
        }

        return rendererDelegate.render( effectiveContent, this.request );
    }

    private PortalResponse renderShortcut( final Content content )
    {
        final Property shortcut = content.getData().getProperty( SHORTCUT_TARGET_PROPERTY );
        final Reference target = shortcut == null ? null : shortcut.getReference();
        if ( target == null )
        {
            throw WebException.notFound( "Missing shortcut target" );
        }

        final PageUrlParams pageUrlParams = new PageUrlParams().id( target.toString() );
        final Multimap<String, String> params = pageUrlParams.getParams();
        params.putAll( this.request.getParams() );
        params.putAll( getShortcutParameters( content ) );

        final String targetUrl = this.portalUrlService.pageUrl( pageUrlParams );

        return PortalResponse.create().status( HttpStatus.TEMPORARY_REDIRECT ).header( "Location", targetUrl ).build();
    }

    private Multimap<String, String> getShortcutParameters( final Content content )
    {
        final Multimap<String, String> params = HashMultimap.create();
        final List<Property> paramsProperties = content.getData().getProperties( "parameters" );

        if ( paramsProperties != null )
        {
            for ( Property paramsProperty : paramsProperties )
            {
                final PropertySet paramsSet = paramsProperty.getSet();
                if ( paramsSet != null )
                {
                    final String name = paramsSet.getString( "name" );
                    final String value = paramsSet.getString( "value" );
                    if ( name != null && value != null )
                    {
                        params.put( name, value );
                    }
                }
            }
        }
        return params;
    }
}
