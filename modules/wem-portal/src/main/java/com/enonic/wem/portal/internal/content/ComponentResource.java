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
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.controller.JsHttpRequest;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.wem.portal.internal.rendering.RendererFactory;

@Path("/{mode}/{workspace}/{contentPath:.+}/_/component/{component:.+}")
public final class ComponentResource
    extends RenderBaseResource
{
    protected RendererFactory rendererFactory;

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

        final Content siteContent = getSite( content );
        final PageTemplate pageTemplate;
        final PageRegions pageRegions;

        if ( !content.hasPage() )
        {
            pageTemplate = getDefaultPageTemplate( content.getType(), siteContent );
            if ( pageTemplate == null )
            {
                throw notFound( "Page not found." );
            }

            pageRegions = pageTemplate.getRegions();
        }
        else
        {
            final Page page = getPage( content );
            pageTemplate = getPageTemplate( page );
            pageRegions = resolvePageRegions( page, pageTemplate );
        }

        final PageComponent component = pageRegions.getComponent( componentPath );
        if ( component == null )
        {
            throw notFound( "Pate component for [%s] not found", componentPath );
        }

        final Renderer<PageComponent> renderer = this.rendererFactory.getRenderer( component );

        final ModuleName moduleName = pageTemplate.getDescriptor().getModuleKey().getName();
        final JsContext context = createContext( content, component, siteContent, moduleName );
        final RenderResult result = renderer.render( component, context );

        return toResponse( result );
    }

    private JsContext createContext( final Content content, final PageComponent component, final Content siteContent,
                                     final ModuleName moduleName )
    {
        final JsContext context = new JsContext();
        context.setContent( content );
        context.setSiteContent( siteContent );
        context.setComponent( component );

        context.setResolvedModule( moduleName.toString() );

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( this.mode );
        jsRequest.setWorkspace( this.workspace );
        jsRequest.setMethod( this.request.getMethod() );
        jsRequest.addParams( this.uriInfo.getQueryParameters() );
        context.setRequest( jsRequest );

        return context;
    }

    private static PageRegions resolvePageRegions( final Page page, final PageTemplate template )
    {
        if ( page.hasRegions() )
        {
            return page.getRegions();
        }
        else
        {
            return template.getRegions();
        }
    }
}
