package com.enonic.xp.portal.impl.resource.render;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.page.Page;
import com.enonic.xp.core.content.page.PageTemplate;
import com.enonic.xp.core.content.page.region.ComponentPath;

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

        if ( resource.content.isPageTemplate() )
        {
            pageTemplate = (PageTemplate) resource.content;
        }
        else if ( !resource.content.hasPage() )
        {
            pageTemplate = getDefaultPageTemplate( resource.content.getType(), resource.site );
            if ( pageTemplate == null )
            {
                throw notFound( "No template found for content" );
            }
        }
        else
        {
            final Page page = getPage( resource.content );
            pageTemplate = getPageTemplate( page );
        }

        final Page effectivePage = new EffectivePageResolver( resource.content, pageTemplate ).resolve();
        final Content effectiveContent = Content.newContent( resource.content ).
            page( effectivePage ).
            build();

        resource.content = effectiveContent;
        resource.component = effectiveContent.getPage().getRegions().getComponent( componentPath );
        if ( resource.component == null )
        {
            throw notFound( "Page component for [%s] not found", componentPath );
        }

        resource.renderer = this.services.getRendererFactory().getRenderer( resource.component );
        resource.moduleKey = pageTemplate.getController().getModuleKey();

        return resource;
    }
}
