package com.enonic.wem.portal.internal.content;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.portal.internal.content.page.JsPageRendererContext;
import com.enonic.wem.portal.internal.content.page.PageRendererContext;
import com.enonic.wem.portal.internal.controller.JsHttpRequest;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;

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
        final Site site = getSite( content );

        final PageTemplate pageTemplate;
        if ( content instanceof PageTemplate )
        {
            pageTemplate = (PageTemplate) content;
        }
        else if ( !content.hasPage() )
        {
            pageTemplate = getDefaultPageTemplate( content.getType(), site );
            if ( pageTemplate == null )
            {
                throw notFound( "Page not found." );
            }
        }
        else
        {
            final Page page = getPage( content );
            pageTemplate = getPageTemplate( page );
        }

        PageDescriptor pageDescriptor = null;
        if ( pageTemplate.getDescriptor() != null )
        {
            pageDescriptor = getPageDescriptor( pageTemplate );
        }

        final JsPageRendererContext context = new JsPageRendererContext();
        context.setContent( content );
        context.setSite( site );
        context.setPageTemplate( pageTemplate );
        context.setPageDescriptor( pageDescriptor );
        if ( pageDescriptor != null )
        {
            context.setModule( getModule( pageDescriptor.getKey().getModuleKey() ) );
        }

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( this.mode );
        jsRequest.setWorkspace( this.workspace );
        jsRequest.setMethod( this.request.getMethod() );
        jsRequest.addParams( this.uriInfo.getQueryParameters() );
        context.setRequest( jsRequest );

        final Renderer<Content, PageRendererContext> renderer = this.rendererFactory.getRenderer( content );
        final RenderResult result = renderer.render( content, context );
        return toResponse( result );
    }
}
