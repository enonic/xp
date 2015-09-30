package com.enonic.xp.portal.impl.handler.render;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.PortalResponse;
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

    protected RendererFactory rendererFactory;

    protected PortalUrlService portalUrlService;

    @Override
    public void execute()
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
            renderShortcut( content );
            return;
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

        this.request.setSite( site );
        this.request.setContent( effectiveContent );
        this.request.setApplicationKey( applicationKey );
        this.request.setPageTemplate( pageTemplate );
        this.request.setPageDescriptor( pageDescriptor );

        final Renderer<Content> renderer = this.rendererFactory.getRenderer( effectiveContent );
        final PortalResponse response = renderer.render( effectiveContent, this.request );
        this.response = PortalResponse.create( response );
    }

    private void renderShortcut( final Content content )
    {
        final Reference target = content.getData().getProperty( SHORTCUT_TARGET_PROPERTY ).getReference();
        if ( target == null || target.getNodeId() == null )
        {
            throw notFound( "Missing shortcut target" );
        }

        final PageUrlParams pageUrlParams = new PageUrlParams().id( target.toString() ).portalRequest( this.request );
        pageUrlParams.getParams().putAll( this.request.getParams() );

        final String targetUrl = this.portalUrlService.pageUrl( pageUrlParams );

        this.response.status( HttpStatus.TEMPORARY_REDIRECT );
        this.response.header( "Location", targetUrl );
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
}
