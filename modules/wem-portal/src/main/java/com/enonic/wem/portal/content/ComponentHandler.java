package com.enonic.wem.portal.content;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.InjectParam;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.rendering.RenderResult;
import com.enonic.wem.portal.rendering.Renderer;
import com.enonic.wem.portal.rendering.RendererFactory;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

@Path("{mode}/{content:.+}/_/component/{component:.+}")
public final class ComponentHandler
    extends RenderBaseHandler
{
    public final static class Params
    {
        @PathParam("mode")
        public String mode;

        @PathParam("content")
        public String content;

        @PathParam("component")
        public String component;

        @Context
        public Request request;

        @Context
        public UriInfo uriInfo;
    }

    @Inject
    protected RendererFactory rendererFactory;

    @GET
    public Response handleGet( @InjectParam final Params params )
    {
        return doHandle( params );
    }

    @POST
    public Response handlePost( @InjectParam final Params params )
    {
        return doHandle( params );
    }

    private Response doHandle( final Params params )
    {
        final RenderingMode mode = parseMode( params.mode );
        final ComponentPath componentPath = ComponentPath.from( params.component );
        final Content content = getContent( mode, params.content );

        final Content siteContent = getSite( content );
        final PageTemplate pageTemplate;
        final PageRegions pageRegions;

        if ( !content.isPage() )
        {
            pageTemplate = getDefaultPageTemplate( content.getType(), siteContent.getSite() );
            pageRegions = pageTemplate.getRegions();
        }
        else
        {
            final Page page = getPage( content );
            pageTemplate = getPageTemplate( page, siteContent.getSite() );
            pageRegions = resolvePageRegions( page, pageTemplate );
        }

        final PageComponent component = pageRegions.getComponent( componentPath );
        if ( component == null )
        {
            throw notFound();
        }

        final Renderer<PageComponent> renderer = this.rendererFactory.getRenderer( component );

        final JsContext context = createContext( params, content, component, siteContent, pageTemplate );
        final RenderResult result = renderer.render( component, context );

        return toResponse( result );
    }

    private JsContext createContext( final Params params, final Content content, final PageComponent component, final Content siteContent,
                                     final PageTemplate pageTemplate )
    {
        final JsContext context = new JsContext();
        context.setContent( content );
        context.setSiteContent( siteContent );
        context.setComponent( component );

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( params.mode );
        jsRequest.setMethod( params.request.getMethod() );
        jsRequest.addParams( params.uriInfo.getQueryParameters() );
        context.setRequest( jsRequest );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        portalUrlScriptBean.setContentPath( content.getPath().toString() );
        portalUrlScriptBean.setModule( pageTemplate.getKey().getModuleName().toString() );
        context.setPortalUrlScriptBean( portalUrlScriptBean );

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
