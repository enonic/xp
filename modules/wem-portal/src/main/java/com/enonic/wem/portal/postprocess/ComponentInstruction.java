package com.enonic.wem.portal.postprocess;

import java.util.List;

import javax.ws.rs.core.Response;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.rendering.RenderException;
import com.enonic.wem.portal.rendering.Renderer;
import com.enonic.wem.portal.rendering.RendererFactory;

final class ComponentInstruction
    implements PostProcessInstruction
{
    private final RendererFactory rendererFactory;

    private final JsContext context;

    public ComponentInstruction( final RendererFactory rendererFactory, final JsContext context )
    {
        this.rendererFactory = rendererFactory;
        this.context = context;
    }

    @Override
    public String evaluate( final PostProcessEvaluator evaluator, final String content )
    {
        if ( !content.startsWith( "COMPONENT " ) )
        {
            return null;
        }

        final List<String> list = Lists.newArrayList( Splitter.on( ' ' ).omitEmptyStrings().split( content ) );
        if ( list.size() != 2 )
        {
            return null;
        }

        final String path = list.get( 1 );
        final String result = renderComponent( context, path );

        if ( result == null )
        {
            return null;
        }

        return evaluator.evaluate( result );
    }

    private String renderComponent( final JsContext context, final String path )
    {
        final ComponentPath componentPath = ComponentPath.from( path );
        final PageComponent component = resolveComponent( context, componentPath );

        final Renderer renderer = this.rendererFactory.getRenderer( component );
        final Response result = renderer.render( component, context );

        final Object body = result.getEntity();
        if ( body instanceof String )
        {
            return (String) body;
        }

        return null;
    }

    private PageComponent resolveComponent( final JsContext context, final ComponentPath path )
    {
        final Content content = context.getContent();
        if ( content == null || content.getPage() == null )
        {
            return null;
        }

        final Page page = content.getPage();
        final PageRegions pageRegions = resolvePageRegions( page, context.getPageTemplate() );

        PageComponent component = pageRegions.getComponent( path );
        if ( component == null )
        {
            // TODO: Hack: See if component still exist in page template
            component = context.getPageTemplate().getRegions().getComponent( path );
            if ( component == null )
            {
                throw new RenderException( "Component not found: [{0}]", path );
            }
        }

        return component;
    }

    private PageRegions resolvePageRegions( final Page page, final PageTemplate pageTemplate )
    {
        if ( page.hasRegions() )
        {
            return page.getRegions();
        }
        else
        {
            return pageTemplate.getRegions();
        }
    }
}
