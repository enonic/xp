package com.enonic.xp.portal.impl.resource.render;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.xp.content.Content;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.region.ComponentPath;

public final class ComponentResource
    extends RenderResource
{
    @Path("{component:.+}")
    public ComponentControllerResource controller( @PathParam("component") final String selector )
    {
        final ComponentControllerResource resource = initResource( new ComponentControllerResource() );

        final ComponentPath componentPath = ComponentPath.from( selector );
        resource.content = getContent( this.contentPath.toString() );
        resource.site = getSite( resource.content );

        final PageTemplate pageTemplate;
        final DescriptorKey pageController;

        if ( resource.content.isPageTemplate() )
        {
            // content is a page-template
            pageTemplate = (PageTemplate) resource.content;
            pageController = pageTemplate.getController();
        }
        else if ( !resource.content.hasPage() )
        {
            // content without page -> use default page-template
            pageTemplate = getDefaultPageTemplate( resource.content.getType(), resource.site );
            pageController = pageTemplate.getController();
        }
        else if ( resource.content.getPage().hasController() )
        {
            // content with controller set but no page-template (customized)
            pageTemplate = null;
            pageController = resource.content.getPage().getController();
        }
        else
        {
            // content with page-template assigned
            final Page page = getPage( resource.content );
            pageTemplate = getPageTemplate( page );
            pageController = pageTemplate.getController();
        }

        final Page effectivePage = new EffectivePageResolver( resource.content, pageTemplate ).resolve();
        final Content effectiveContent = Content.create( resource.content ).
            page( effectivePage ).
            build();

        resource.content = effectiveContent;
        resource.component = effectiveContent.getPage().getRegions().getComponent( componentPath );
        if ( resource.component == null )
        {
            throw notFound( "Page component for [%s] not found", componentPath );
        }

        resource.renderer = this.services.getRendererFactory().getRenderer( resource.component );
        resource.applicationKey = pageController.getApplicationKey();

        return resource;
    }
}
