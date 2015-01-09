package com.enonic.wem.portal.internal.content;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.controller.PortalRequestImpl;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.xp.portal.PortalContext;

@Path("/portal/{mode}/{workspace}/{contentPath:.+}")
public final class ContentResource
    extends RenderBaseResource
{
    private Form form;

    @GET
    public Response handleGet()
    {
        return doHandle();
    }

    @POST
    public Response handlePost( final Form form )
    {
        this.form = form;
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
                throw notFound( "No template found for content" );
            }
        }
        else
        {
            final Page page = getPage( content );
            pageTemplate = getPageTemplate( page );
        }

        PageDescriptor pageDescriptor = null;
        if ( pageTemplate.getController() != null )
        {
            pageDescriptor = getPageDescriptor( pageTemplate );
        }

        final Content effectiveContent;
        if ( !content.hasPage() )
        {
            // The Content has no Page, but it has a supporting PageTemplate, so then we use the Page from the PageTemplate instead
            effectiveContent = Content.newContent( content ).
                page( pageTemplate.getPage() ).
                build();
        }
        else
        {
            effectiveContent = content;
        }

        final PortalContextImpl context = new PortalContextImpl();
        context.setContent( effectiveContent );
        context.setSite( site );
        context.setPageTemplate( pageTemplate );
        context.setPageDescriptor( pageDescriptor );
        if ( pageDescriptor != null )
        {
            context.setModule( pageDescriptor.getKey().getModuleKey() );
        }

        final PortalRequestImpl jsRequest = new PortalRequestImpl();
        jsRequest.setMode( this.mode );
        jsRequest.setWorkspace( this.workspace );
        jsRequest.setMethod( this.request.getMethod() );
        jsRequest.addParams( this.uriInfo.getQueryParameters() );
        if ( this.form != null )
        {
            jsRequest.addFormParams( this.form.asMap() );
        }
        jsRequest.addHeaders( this.httpHeaders.getRequestHeaders() );
        context.setRequest( jsRequest );

        final Renderer<Content, PortalContext> renderer = this.rendererFactory.getRenderer( effectiveContent );
        final RenderResult result = renderer.render( effectiveContent, context );
        return toResponse( result );
    }
}
