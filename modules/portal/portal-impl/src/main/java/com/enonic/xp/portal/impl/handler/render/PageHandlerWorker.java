package com.enonic.xp.portal.impl.handler.render;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.Property;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.site.Site;
import com.enonic.xp.util.Reference;
import com.enonic.xp.web.HttpStatus;

final class PageHandlerWorker
    extends RenderHandlerWorker
{
    private static final String SHORTCUT_TARGET_PROPERTY = "target";

    private final RendererFactory rendererFactory;

    private final PortalUrlService portalUrlService;

    private PageHandlerWorker( final Builder builder )
    {
        super( builder );
        rendererFactory = builder.rendererFactory;
        portalUrlService = builder.portalUrlService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public PortalWebResponse execute()
    {
        final ContentPath contentPath = this.webRequest.getContentPath();
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
        else // hasPage
        {
            final Page page = getPage( content );
            if ( page.hasTemplate() )
            {
                final PageTemplate resolvedPageTemplate = getPageTemplate( page );
                if ( resolvedPageTemplate.canRender( content.getType() ) )
                {
                    //template may be deleted or updated to not support content type after content had been created
                    pageTemplate = resolvedPageTemplate;
                }
            }
            else if ( page.hasController() )
            {
                pageDescriptor = getPageDescriptor( page.getController() );
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

        final PortalWebRequest portalWebRequest = PortalWebRequest.create( this.webRequest ).
            site( site ).
            content( effectiveContent ).
            applicationKey( applicationKey ).
            pageTemplate( pageTemplate ).
            pageDescriptor( pageDescriptor ).
            build();
        final PortalRequest portalRequest = PortalWebRequest.convertToPortalRequest( portalWebRequest );

        final Renderer<Content> renderer = this.rendererFactory.getRenderer( effectiveContent );
        final PortalResponse portalResponse = renderer.render( effectiveContent, portalRequest );
        return PortalWebResponse.convertToPortalWebResponse( portalResponse );
    }

    private PortalWebResponse renderShortcut( final Content content )
    {
        final Property shortcut = content.getData().getProperty( SHORTCUT_TARGET_PROPERTY );
        final Reference target = shortcut == null ? null : shortcut.getReference();
        if ( target == null || target.getNodeId() == null )
        {
            throw notFound( "Missing shortcut target" );
        }

        final PortalRequest portalRequest = PortalWebRequest.convertToPortalRequest( webRequest );
        final PageUrlParams pageUrlParams = new PageUrlParams().id( target.toString() ).portalRequest( portalRequest );
        pageUrlParams.getParams().putAll( this.webRequest.getParams() );

        final String targetUrl = this.portalUrlService.pageUrl( pageUrlParams );

        this.webResponse.setStatus( HttpStatus.TEMPORARY_REDIRECT );
        this.webResponse.setHeader( "Location", targetUrl );
        return this.webResponse;
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

    public static final class Builder
        extends RenderHandlerWorker.Builder<Builder>
    {
        private RendererFactory rendererFactory;

        private PortalUrlService portalUrlService;

        private Builder()
        {
        }

        public Builder rendererFactory( final RendererFactory rendererFactory )
        {
            this.rendererFactory = rendererFactory;
            return this;
        }

        public Builder portalUrlService( final PortalUrlService portalUrlService )
        {
            this.portalUrlService = portalUrlService;
            return this;
        }

        public PageHandlerWorker build()
        {
            return new PageHandlerWorker( this );
        }
    }
}
