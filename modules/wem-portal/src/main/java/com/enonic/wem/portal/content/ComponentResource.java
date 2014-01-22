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

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.exception.PortalWebException;
import com.enonic.wem.portal.rendering.Renderer;
import com.enonic.wem.portal.rendering.RendererFactory;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

import static com.enonic.wem.api.command.Commands.content;

@Path("{mode}/{path:.+}/_/component/{component}")
public final class ComponentResource
{
    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentPath;

    @PathParam("component")
    protected String componentName;

    @Inject
    protected Client client;

    @Inject
    protected RendererFactory rendererFactory;

    @Context
    protected HttpContext httpContext;

    @GET
    public Response handleGet()
        throws Exception
    {
        return doHandle();
    }

    @POST
    public Response handlePost()
        throws Exception
    {
        return doHandle();
    }

    @OPTIONS
    public Response handleOptions()
        throws Exception
    {
        return doHandle();
    }

    private Response doHandle()
        throws Exception
    {
        final ContentPath path = ContentPath.from( this.contentPath );
        final Content content = getContent( path );
        final Page page = getPage( content );

        final PageComponent component = getComponent( new ComponentName( this.componentName ), page );

        final Renderer renderer = rendererFactory.getRenderer( component );

        final JsContext context = createContext( content, component );
        return renderer.render( component, context );
    }

    private JsContext createContext( final Content content, final PageComponent component )
    {
        final JsContext context = new JsContext();
        context.setContent( content );

        final JsHttpRequest request = new JsHttpRequest( this.httpContext.getRequest() );
        request.setMode( this.mode );
        context.setRequest( request );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        portalUrlScriptBean.setContentPath( content.getPath().toString() );
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        return context;
    }

    private PageComponent getComponent( final ComponentName componentName, final Page page )
    {
        final PageComponent component = page.getComponent( componentName );
        if ( component != null )
        {
            return component;
        }
        throw PortalWebException.notFound().message( "Component [{0}] not found in page [{1}].", componentName, contentPath ).build();
    }

    private Content getContent( final ContentPath contentPath )
    {
        final Content content = this.client.execute( content().get().byPath( contentPath ) );
        if ( content != null )
        {
            return content;
        }

        throw PortalWebException.notFound().message( "Page [{0}] not found.", contentPath ).build();
    }

    private Page getPage( final Content content )
    {
        if ( !content.isPage() )
        {
            throw PortalWebException.notFound().message( "Page [{0}] not found.", this.contentPath ).build();
        }
        return content.getPage();
    }
}
