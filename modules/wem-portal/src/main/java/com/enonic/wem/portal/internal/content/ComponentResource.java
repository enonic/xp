package com.enonic.wem.portal.internal.content;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.controller.PortalRequestImpl;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;

@Path("/portal/{mode}/{workspace}/{contentPath:.+}/_/component/{component:.+}")
public final class ComponentResource
    extends RenderBaseResource
{
    @PathParam("component")
    protected String componentSelector;

    @GET
    public Response handleGet()
    {
        return doHandle();
    }

    @POST
    public Response handlePost()
    {
        return doHandle();
    }

    private Response doHandle()
    {
        final ComponentPath componentPath = ComponentPath.from( this.componentSelector );
        final Content content = getContent( this.contentPath );

        final Site site = getSite( content );
        final PageTemplate pageTemplate;

        if ( content.isPageTemplate() )
        {
            pageTemplate = (PageTemplate) content;
        }
        else if ( !content.hasPage() )
        {
            pageTemplate = getDefaultPageTemplate( content.getType(), site );
            if ( pageTemplate == null )
            {
                throw notFound( "No template found for content" );
            }
        }
        else
        {
            final Page page = getPage( content );
            pageTemplate = getPageTemplate( page );
        }

        final Content effectiveContent;
        if ( !content.hasPage() )
        {
            // The Content has no Page, but it has a supporting PageTemplate, so then we use the Page from the PageTemplate instead
            effectiveContent = Content.newContent( content ).
                page( pageTemplate.getPage() ).
                build();
        }
        else
        {
            effectiveContent = content;
        }

        final PageComponent component = effectiveContent.getPage().getRegions().getComponent( componentPath );
        if ( component == null )
        {
            throw notFound( "Pate component for [%s] not found", componentPath );
        }

        final Renderer<PageComponent, PortalContext> renderer = this.rendererFactory.getRenderer( component );

        final ModuleKey moduleKey = pageTemplate.getController().getModuleKey();
        final PortalContextImpl context = createContext( effectiveContent, component, site, moduleKey );
        final RenderResult result = renderer.render( component, context );

        return toResponse( result );
    }

    private PortalContextImpl createContext( final Content content, final PageComponent component, final Site site,
                                             final ModuleKey moduleKey )
    {
        final PortalContextImpl context = new PortalContextImpl();
        context.setContent( content );
        context.setSite( site );
        context.setComponent( component );

        context.setModule( moduleKey );

        final PortalRequestImpl jsRequest = new PortalRequestImpl();
        jsRequest.setMode( this.mode );
        jsRequest.setWorkspace( this.workspace );
        jsRequest.setMethod( this.request.getMethod() );
        jsRequest.addParams( this.uriInfo.getQueryParameters() );
        context.setRequest( jsRequest );

        return context;
    }
}
