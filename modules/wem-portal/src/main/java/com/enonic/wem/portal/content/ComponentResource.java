package com.enonic.wem.portal.content;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.HttpContext;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.portal.content.page.PageComponentResolver;
import com.enonic.wem.portal.content.page.PageRegionsResolver;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.rendering.Renderer;
import com.enonic.wem.portal.rendering.RendererFactory;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

@Path("{mode}/{path:.+}/_/component/{component:.+}")
public final class ComponentResource
    extends RenderResource
{
    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentPath;

    @PathParam("component")
    protected String componentSelector;

    @Inject
    protected RendererFactory rendererFactory;

    @Context
    protected HttpContext httpContext;

    @Override
    protected Response doHandle()
        throws Exception
    {
        final Content content = getContent( this.contentPath, this.mode );
        final Content siteContent = getSite( content );
        final Page page = getPage( content );
        final PageTemplate pageTemplate = getPageTemplate( page, siteContent.getSite() );

        final ComponentPath componentPath = ComponentPath.from( this.componentSelector );
        final PageRegions pageRegions = PageRegionsResolver.resolve( page, pageTemplate );
        final PageComponent component = PageComponentResolver.resolve( componentPath, pageRegions );

        final Renderer<PageComponent> renderer = rendererFactory.getRenderer( component );

        final JsContext context = createContext( content, component, siteContent, pageTemplate );
        return renderer.render( component, context );
    }

    private JsContext createContext( final Content content, final PageComponent component, final Content siteContent,
                                     final PageTemplate pageTemplate )
    {
        final JsContext context = new JsContext();
        context.setContent( content );
        context.setSiteContent( siteContent );
        context.setComponent( component );

        final JsHttpRequest request = new JsHttpRequest( this.httpContext.getRequest() );
        request.setMode( this.mode );
        context.setRequest( request );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        portalUrlScriptBean.setContentPath( content.getPath().toString() );
        portalUrlScriptBean.setModule( pageTemplate.getKey().getModuleName().toString() );
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        return context;
    }

}
