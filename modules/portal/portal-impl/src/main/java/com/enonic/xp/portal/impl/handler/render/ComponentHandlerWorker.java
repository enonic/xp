package com.enonic.xp.portal.impl.handler.render;

import com.enonic.xp.content.Content;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.site.Site;

final class ComponentHandlerWorker
    extends RenderHandlerWorker
{
    protected ComponentPath componentPath;

    protected RendererFactory rendererFactory;

    protected PostProcessor postProcessor;

    @Override
    public void execute()
        throws Exception
    {
        final Content content = getContent( getContentSelector() );
        final Site site = getSite( content );

        final PageTemplate pageTemplate;
        final DescriptorKey pageController;
        Component component = null;

        if ( content.isPageTemplate() )
        {
            // content is a page-template
            pageTemplate = (PageTemplate) content;
            pageController = pageTemplate.getController();
        }
        else if ( !content.hasPage() )
        {
            // content without page -> use default page-template
            pageTemplate = getDefaultPageTemplate( content.getType(), site );
            pageController = pageTemplate.getController();
        }
        else if ( content.getPage().hasController() )
        {
            // content with controller set but no page-template (customized)
            pageTemplate = null;
            pageController = content.getPage().getController();
        }
        else if ( content.getType().isFragment() )
        {
            // fragment content, try resolving component path in Layout fragment
            pageTemplate = null;
            pageController = null;
            final Component fragmentComponent = content.getPage().getFragment();
            if ( this.componentPath.isEmpty() )
            {
                component = fragmentComponent;
            }
            else if ( fragmentComponent instanceof LayoutComponent )
            {
                component = ( (LayoutComponent) fragmentComponent ).getComponent( this.componentPath );
            }
        }
        else
        {
            // content with page-template assigned
            final Page page = getPage( content );
            pageTemplate = getPageTemplate( page );
            pageController = pageTemplate.getController();
        }

        final Page effectivePage = new EffectivePageResolver( content, pageTemplate ).resolve();
        final Content effectiveContent = Content.create( content ).
            page( effectivePage ).
            build();

        if ( component == null )
        {
            component = effectiveContent.getPage().getRegions().getComponent( this.componentPath );
        }
        if ( component == null )
        {
            throw notFound( "Page component for [%s] not found", this.componentPath );
        }

        this.request.setSite( site );
        this.request.setContent( effectiveContent );
        this.request.setComponent( component );
        this.request.setApplicationKey( pageController != null ? pageController.getApplicationKey() : null );
        this.request.setPageTemplate( pageTemplate );
        this.request.setPageDescriptor( null );

        final Renderer<Component> renderer = this.rendererFactory.getRenderer( component );
        PortalResponse response = renderer.render( component, this.request );
        response = this.postProcessor.processResponseInstructions( this.request, response );

        this.response = PortalResponse.create( response );
    }
}
