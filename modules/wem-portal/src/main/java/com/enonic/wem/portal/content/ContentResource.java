package com.enonic.wem.portal.content;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.HttpContext;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.exception.PortalWebException;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

import static com.enonic.wem.api.rendering.RenderingMode.LIVE;

@Path("{mode}/{content:.+}")
public final class ContentResource
    extends RenderResource
{
    @PathParam("mode")
    protected String mode;

    @PathParam("content")
    protected String contentSelector;

    @Context
    protected HttpContext httpContext;

    protected Response doHandle()
        throws Exception
    {
        final Content content = getContent( this.contentSelector, this.mode );
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

        final JsHttpRequest request = new JsHttpRequest( this.httpContext.getRequest() );
        request.setMode( RenderingMode.from( this.mode, LIVE ) );
        context.setRequest( request );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        portalUrlScriptBean.setContentPath( content.getPath().toString() );
        portalUrlScriptBean.setModule( pageTemplate.getKey().getModuleName().toString() );
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        final JsController controller = this.controllerFactory.newController();
        controller.scriptDir( pageDescriptor.getResourceKey() );
        controller.context( context );

        return controller.execute();
    }
}
