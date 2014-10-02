package com.enonic.wem.portal.internal.content;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.controller.JsController;
import com.enonic.wem.portal.internal.controller.JsHttpRequest;
import com.enonic.wem.portal.internal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;

@Path("/{mode}/{workspace}/{contentPath:.+}")
public final class ContentResource
    extends RenderBaseResource
{
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
        final Content content = getContent( this.contentPath );
        final Content siteContent = getSite( content );

        final PageTemplate pageTemplate;
        if ( !content.hasPage() )
        {
            pageTemplate = getDefaultPageTemplate( content.getType(), siteContent.getSite() );
            if ( pageTemplate == null )
            {
                throw notFound( "Page not found." );
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
        context.setResolvedModule( pageTemplate.getKey().getModuleName().toString() );

        // createResourceUrl('my.css');
        // /portal/edit/workspace/path/to/content/_/public/mymodule-1.0.0/my.css

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( this.mode );
        jsRequest.setWorkspace( this.workspace );
        jsRequest.setMethod( this.request.getMethod() );
        jsRequest.addParams( this.uriInfo.getQueryParameters() );
        context.setRequest( jsRequest );

        final JsController controller = this.controllerFactory.newController( pageDescriptor.getResourceKey() );
        controller.execute( context );

        final RenderResult result = new JsHttpResponseSerializer( context.getResponse() ).serialize();
        return toResponse( result );
    }
}
