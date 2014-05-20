package com.enonic.wem.portal.content;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.InjectParam;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.content.page.PageComponentResolver;
import com.enonic.wem.portal.content.page.PageRegionsResolver;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.exception.PortalWebException;
import com.enonic.wem.portal.rendering.Renderer;
import com.enonic.wem.portal.rendering.RendererFactory;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

import static com.enonic.wem.api.rendering.RenderingMode.LIVE;

@Path("{mode}/{path:.+}/_/component/{component:.+}")
public final class ComponentResource
    extends RenderResource
{
    public final class Request
    {
        @PathParam("mode")
        protected String mode;

        @PathParam("path")
        protected String contentPath;

        @PathParam("component")
        protected String componentSelector;

        @Context
        protected HttpContext httpContext;
    }

    @GET
    public Response handleGet( @InjectParam final Request request )
        throws Exception
    {
        return doHandle( request );
    }

    @POST
    public Response handlePost( @InjectParam final Request request )
        throws Exception
    {
        return doHandle( request );
    }

    @OPTIONS
    public Response handleOptions( @InjectParam final Request request )
        throws Exception
    {
        return doHandle( request );
    }

    @Inject
    protected RendererFactory rendererFactory;

    private Response doHandle( final Request request )
        throws Exception
    {
        final ComponentPath componentPath = ComponentPath.from( request.componentSelector );

        final Content content = getContent( request.contentPath, request.mode );

        final Content siteContent = getSite( content );
        final PageTemplate pageTemplate;
        final PageRegions pageRegions;
        if ( !content.isPage() )
        {
            pageTemplate = getDefaultPageTemplate( content.getType(), siteContent.getSite() );
            if ( pageTemplate == null )
            {
                throw PortalWebException.notFound().message( "Page not found." ).build();
            }
            pageRegions = pageTemplate.getRegions();
        }
        else
        {
            final Page page = getPage( content );
            pageTemplate = getPageTemplate( page, siteContent.getSite() );
            pageRegions = PageRegionsResolver.resolve( page, pageTemplate );
        }

        final PageComponent component = PageComponentResolver.resolve( componentPath, pageRegions );

        final Renderer<PageComponent> renderer = rendererFactory.getRenderer( component );

        final JsContext context = createContext( request, content, component, siteContent, pageTemplate );
        return renderer.render( component, context );
    }

    private JsContext createContext( final Request request, final Content content, final PageComponent component, final Content siteContent,
                                     final PageTemplate pageTemplate )
    {
        final JsContext context = new JsContext();
        context.setContent( content );
        context.setSiteContent( siteContent );
        context.setComponent( component );

        final JsHttpRequest jsRequest = new JsHttpRequest( request.httpContext.getRequest() );
        jsRequest.setMode( RenderingMode.from( request.mode, LIVE ) );
        context.setRequest( jsRequest );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        portalUrlScriptBean.setContentPath( content.getPath().toString() );
        portalUrlScriptBean.setModule( pageTemplate.getKey().getModuleName().toString() );
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        return context;
    }
}
