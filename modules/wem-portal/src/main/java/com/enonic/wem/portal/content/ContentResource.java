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
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.exception.PortalWebException;

import static com.enonic.wem.api.command.Commands.content;
import static com.enonic.wem.api.command.Commands.page;

@Path("{mode}/{path:.+}")
public final class ContentResource
{
    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentPath;

    @Inject
    protected Client client;

    @Context
    protected HttpContext httpContext;

    @Inject
    protected JsControllerFactory controllerFactory;

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
        final ModuleResourceKey jsModuleResource = getJsModuleResource( content );

        final JsContext context = new JsContext();
        context.setContent( new JsContextContent( content ) );
        context.setRequest( new JsHttpRequest( this.httpContext.getRequest() ) );

        final JsController controller = this.controllerFactory.newController();
        controller.scriptDir( jsModuleResource );
        controller.context( context );

        return controller.execute();
    }

    private ModuleResourceKey getJsModuleResource( final Content content )
    {
        final Page page = getPage( content );
        final PageTemplate pageTemplate = getPageTemplate( page );
        final PageDescriptor pageDescriptor = getPageDescriptor( pageTemplate );
        return pageDescriptor.getControllerResource();
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

    private PageDescriptor getPageDescriptor( final PageTemplate pageTemplate )
    {
        final ModuleResourceKey resourceKey = pageTemplate.getDescriptor();
        final PageDescriptorKey descriptorKey = PageDescriptorKey.from( resourceKey.getModuleKey(), resourceKey.getPath() );
        final PageDescriptor pageDescriptor = this.client.execute( page().descriptor().page().getByKey( descriptorKey ) );
        if ( pageDescriptor == null )
        {
            throw PortalWebException.notFound().message( "Page descriptor for template [{0}] not found.", pageTemplate.getName() ).build();
        }
        return pageDescriptor;
    }

    private PageTemplate getPageTemplate( final Page page )
    {
        final PageTemplate pageTemplate = this.client.execute( page().template().page().getByKey().key( page.getTemplate() ) );
        if ( pageTemplate == null )
        {
            throw PortalWebException.notFound().message( "Page [{0}] not found.", this.contentPath ).build();
        }
        return pageTemplate;
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
