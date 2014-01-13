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
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.region.PageRegions;
import com.enonic.wem.api.content.page.region.Region;
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
    {
        return doHandle();
    }

    @POST
    public Response handlePost()
    {
        return doHandle();
    }

    @OPTIONS
    public Response handleOptions()
    {
        return doHandle();
    }

    private Response doHandle()
    {
        final ContentPath path = ContentPath.from( this.contentPath );
        final Content content = getContent( path );
        final Page page = getPage( content );

        // create context
        final JsContext context = new JsContext();
        context.setContent( content );
        final JsHttpRequest request = new JsHttpRequest( this.httpContext.getRequest() );
        request.setMode( this.mode );
        context.setRequest( request );
        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        portalUrlScriptBean.setContentPath( path.toString() );
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        final PageComponent component = resolveComponent( new ComponentName( this.componentName ), page );

        final Renderer renderer = rendererFactory.getRenderer( component );
        return renderer.render( component, context );
    }

    private PageComponent resolveComponent( final ComponentName componentName, final Page page )
    {
        final PageRegions pageRegions = page.getRegions();
        for ( Region region : pageRegions )
        {
            for ( PageComponent component : region.getComponents() )
            {
                if ( componentName.equals( getName( component ) ) )
                {
                    return component;
                }
            }
        }
        throw PortalWebException.notFound().message( "Component [{0}] not found in page [{1}].", componentName, contentPath ).build();
    }

    // TODO temp fix until we add getName to PageComponent
    private ComponentName getName( final PageComponent component )
    {
        if ( component instanceof PartComponent )
        {
            return ( (PartComponent) component ).getName();
        }
        else if ( component instanceof ImageComponent )
        {
            return ( (ImageComponent) component ).getName();
        }
        else if ( component instanceof LayoutComponent )
        {
            return ( (LayoutComponent) component ).getName();
        }
        return null;
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
