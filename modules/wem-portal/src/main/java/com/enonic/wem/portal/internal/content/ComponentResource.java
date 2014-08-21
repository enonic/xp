package com.enonic.wem.portal.internal.content;

import javax.inject.Inject;

import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.controller.JsHttpRequest;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.wem.portal.internal.rendering.RendererFactory;
import com.enonic.wem.portal.internal.script.lib.PortalUrlScriptBean;

public final class ComponentResource
    extends RenderBaseResource
{
    @Inject
    protected RendererFactory rendererFactory;

    @Override
    protected Representation doHandle()
        throws ResourceException
    {
        final String componentSelector = getAttribute( "component" );
        final ComponentPath componentPath = ComponentPath.from( componentSelector );

        final Content content = getContent( this.contentPath );

        final Content siteContent = getSite( content );
        final PageTemplate pageTemplate;
        final PageRegions pageRegions;

        if ( !content.isPage() )
        {
            pageTemplate = getDefaultPageTemplate( content.getType(), siteContent.getSite() );
            if ( pageTemplate == null )
            {
                throw notFound( "Page not found." );
            }

            pageRegions = pageTemplate.getRegions();
        }
        else
        {
            final Page page = getPage( content );
            pageTemplate = getPageTemplate( page, siteContent.getSite() );
            pageRegions = resolvePageRegions( page, pageTemplate );
        }

        final PageComponent component = pageRegions.getComponent( componentPath );
        if ( component == null )
        {
            throw notFound( "Pate component for [%s] not found", componentPath );
        }

        final Renderer<PageComponent> renderer = this.rendererFactory.getRenderer( component );

        final JsContext context = createContext( content, component, siteContent, pageTemplate );
        final RenderResult result = renderer.render( component, context );

        return toRepresentation( result );
    }

    private JsContext createContext( final Content content, final PageComponent component, final Content siteContent,
                                     final PageTemplate pageTemplate )
    {
        final JsContext context = new JsContext();
        context.setContent( content );
        context.setSiteContent( siteContent );
        context.setComponent( component );

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( this.mode );
        jsRequest.setWorkspace( this.workspace );
        jsRequest.setMethod( getRequest().getMethod().toString() );
        jsRequest.addParams( getParams() );
        context.setRequest( jsRequest );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        portalUrlScriptBean.setContentPath( content.getPath().toString() );
        portalUrlScriptBean.setModule( pageTemplate.getKey().getModuleName().toString() );
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        return context;
    }

    private static PageRegions resolvePageRegions( final Page page, final PageTemplate template )
    {
        if ( page.hasRegions() )
        {
            return page.getRegions();
        }
        else
        {
            return template.getRegions();
        }
    }
}
