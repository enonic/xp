package com.enonic.wem.portal.content;

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
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.exception.PortalWebException;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

@Path("{mode}/{content:.+}")
public final class OldContentResource
    extends OldRenderResource
{
    public final static class Request
    {
        @PathParam("mode")
        public String mode;

        @PathParam("content")
        public String contentSelector;

        @Context
        public HttpContext httpContext;
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

    private Response doHandle( final Request request )
        throws Exception
    {
        final Content content = getContent( request.contentSelector, request.mode );
        final Content siteContent = getSite( content );

        final PageTemplate pageTemplate;
        if ( !content.isPage() )
        {
            pageTemplate = getDefaultPageTemplate( content.getType(), siteContent.getSite() );
            if ( pageTemplate == null )
            {
                throw PortalWebException.notFound().message( "Page not found." ).build();
            }
        }
        else
        {
            final Page page = getPage( content );
            pageTemplate = getPageTemplate( page, siteContent.getSite() );
        }
        final PageDescriptor pageDescriptor = getPageDescriptor( pageTemplate );

        final JsContext context = new JsContext();
        context.setContent( content );
        context.setSiteContent( siteContent );
        context.setPageTemplate( pageTemplate );

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( request.mode );
        jsRequest.setMethod( request.httpContext.getRequest().getMethod() );
        jsRequest.addParams( request.httpContext.getUriInfo().getQueryParameters() );
        context.setRequest( jsRequest );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        portalUrlScriptBean.setContentPath( content.getPath().toString() );
        portalUrlScriptBean.setModule( pageTemplate.getKey().getModuleName().toString() );
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        final JsController controller = this.controllerFactory.newController();
        controller.scriptDir( pageDescriptor.getResourceKey() );
        controller.context( context );

        controller.execute();

        return new JsHttpResponseSerializer( context.getResponse() ).serialize();
    }
}
