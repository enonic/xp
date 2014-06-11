package com.enonic.wem.portal.content;

import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.rendering.RenderResult;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

public final class ContentResource
    extends RenderResource
{
    @Override
    protected Representation doHandle()
        throws ResourceException
    {
        final Content content = getContent( this.contentPath );
        final Content siteContent = getSite( content );

        final PageTemplate pageTemplate;
        if ( !content.isPage() )
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

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( this.mode );
        jsRequest.setMethod( getRequest().getMethod().toString() );
        jsRequest.addParams( getParams() );
        context.setRequest( jsRequest );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        portalUrlScriptBean.setContentPath( content.getPath().toString() );
        portalUrlScriptBean.setModule( pageTemplate.getKey().getModuleName().toString() );
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        final JsController controller = this.controllerFactory.newController();
        controller.scriptDir( pageDescriptor.getResourceKey() );
        controller.context( context );
        controller.execute();

        final RenderResult result = new JsHttpResponseSerializer( context.getResponse() ).serialize();
        return toRepresentation( result );
    }
}
