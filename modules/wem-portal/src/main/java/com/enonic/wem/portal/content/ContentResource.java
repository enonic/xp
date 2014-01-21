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
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
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
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

import static com.enonic.wem.api.command.Commands.content;
import static com.enonic.wem.api.command.Commands.page;

@Path("{mode}/{path:.+}")
public final class ContentResource
{
    private static final String EDIT_MODE = "edit";

    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentSelector;

    @Inject
    protected Client client;

    @Context
    protected HttpContext httpContext;

    @Inject
    protected JsControllerFactory controllerFactory;

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
        final Content content = getContent( this.contentSelector );
        final ContentPath path = content.getPath();
        final ModuleResourceKey jsModuleResource = getJsModuleResource( content );

        final JsContext context = new JsContext();
        context.setContent( content );
        final JsHttpRequest request = new JsHttpRequest( this.httpContext.getRequest() );
        request.setMode( this.mode );
        context.setRequest( request );
        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        portalUrlScriptBean.setContentPath( path.toString() );
        context.setPortalUrlScriptBean( portalUrlScriptBean );

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
        return pageDescriptor.getComponentPath();
    }

    private Content getContent( final String contentSelector )
    {
        final ContentPath contentPath = ContentPath.from( contentSelector );
        final Content content = getContentByPath( contentPath );
        if ( content != null )
        {
            return content;
        }

        final boolean inEditMode = EDIT_MODE.equals( this.mode );
        if ( inEditMode )
        {
            final ContentId contentId = ContentId.from( contentSelector );
            final Content contentById = getContentById( contentId );
            if ( contentById != null )
            {
                return contentById;
            }
        }
        throw PortalWebException.notFound().message( "Page [{0}] not found.", contentPath ).build();
    }

    private Content getContentByPath( final ContentPath contentPath )
    {
        try
        {
            return this.client.execute( content().get().byPath( contentPath ) );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private Content getContentById( final ContentId contentId )
    {
        try
        {
            return this.client.execute( content().get().byId( contentId ) );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private PageDescriptor getPageDescriptor( final PageTemplate pageTemplate )
    {
        final PageDescriptorKey descriptorKey = pageTemplate.getDescriptor();
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
            throw PortalWebException.notFound().message( "Page [{0}] not found.", this.contentSelector ).build();
        }
        return pageTemplate;
    }

    private Page getPage( final Content content )
    {
        if ( !content.isPage() )
        {
            throw PortalWebException.notFound().message( "Page [{0}] not found.", this.contentSelector ).build();
        }
        return content.getPage();
    }
}
