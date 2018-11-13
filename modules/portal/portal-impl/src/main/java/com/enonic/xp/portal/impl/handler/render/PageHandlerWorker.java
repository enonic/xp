package com.enonic.xp.portal.impl.handler.render;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.rendering.Renderer;
import com.enonic.xp.portal.impl.rendering.RendererFactory;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.site.Site;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.Reference;
import com.enonic.xp.web.HttpStatus;

final class PageHandlerWorker
    extends RenderHandlerWorker
{
    private static final String SHORTCUT_TARGET_PROPERTY = "target";

    protected RendererFactory rendererFactory;

    protected PortalUrlService portalUrlService;

    public PageHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        final ContentPath contentPath = this.request.getContentPath();
        if ( ContentConstants.CONTENT_ROOT_PARENT.toString().equals( contentPath.toString() ) )
        {
            throw notFound( "Page [%s] not found", contentPath );
        }

        final Content content = getContent( getContentSelector() );
        if ( content.getType().isShortcut() )
        {
            return renderShortcut( content );
        }

        final Site site = getSite( content );

        PageTemplate pageTemplate = null;
        PageDescriptor pageDescriptor = null;

        if ( content instanceof PageTemplate )
        {
            pageTemplate = (PageTemplate) content;
        }
        else if ( !content.hasPage() )
        {
            pageTemplate = getDefaultPageTemplate( content.getType(), site );
        }
        else
        {
            final Page page = getPage( content );
            if ( page.hasTemplate() )
            {
                try
                {
                    final PageTemplate resolvedPageTemplate = getPageTemplate( page );
                    if ( resolvedPageTemplate.canRender( content.getType() ) )
                    {
                        //template may be deleted or updated to not support content type after content had been created
                        pageTemplate = resolvedPageTemplate;
                    }
                }
                catch ( ContentNotFoundException e )
                {
                    pageTemplate = getDefaultPageTemplate( content.getType(), site );
                }
            }
            else if ( page.hasDescriptor() )
            {
                pageDescriptor = getPageDescriptor( page.getDescriptor() );
            }

        }

        if ( pageTemplate != null && pageTemplate.getController() != null )
        {
            pageDescriptor = getPageDescriptor( pageTemplate );
        }

        ApplicationKey applicationKey = null;
        if ( pageDescriptor != null )
        {
            applicationKey = pageDescriptor.getKey().getApplicationKey();
        }

        final Page effectivePage = new EffectivePageResolver( content, pageTemplate ).resolve();
        final Content effectiveContent = Content.create( content ).
            page( effectivePage ).
            build();

        this.request.setSite( site );
        this.request.setContent( effectiveContent );
        this.request.setApplicationKey( applicationKey );
        this.request.setPageTemplate( pageTemplate );
        this.request.setPageDescriptor( pageDescriptor );

        final Renderer<Content> renderer = this.rendererFactory.getRenderer( effectiveContent );
        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", effectiveContent.getPath().toString() );
            trace.put( "type", "page" );
        }
        return renderer.render( effectiveContent, this.request );
    }

    private PortalResponse renderShortcut( final Content content )
    {
        final Property shortcut = content.getData().getProperty( SHORTCUT_TARGET_PROPERTY );
        final Reference target = shortcut == null ? null : shortcut.getReference();
        if ( target == null || target.getNodeId() == null )
        {
            throw notFound( "Missing shortcut target" );
        }

        final PageUrlParams pageUrlParams = new PageUrlParams().id( target.toString() ).portalRequest( this.request );
        final Multimap<String, String> params = pageUrlParams.getParams();
        params.putAll( this.request.getParams() );
        params.putAll( getShortcutParameters( content ) );

        final String targetUrl = this.portalUrlService.pageUrl( pageUrlParams );

        return PortalResponse.create().
            status( HttpStatus.TEMPORARY_REDIRECT ).
            header( "Location", targetUrl ).
            build();
    }

    private PageDescriptor getPageDescriptor( final DescriptorKey descriptorKey )
    {
        final PageDescriptor pageDescriptor = this.pageDescriptorService.getByKey( descriptorKey );
        if ( pageDescriptor == null )
        {
            throw notFound( "Page descriptor [%s] not found", descriptorKey.getName() );
        }

        return pageDescriptor;
    }

    private Multimap<String, String> getShortcutParameters( final Content content )
    {
        final Multimap<String, String> params = HashMultimap.create();
        final ImmutableList<Property> paramsProperties = content.getData().getProperties( "parameters" );

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
