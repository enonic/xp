package com.enonic.wem.portal.internal.postprocess.instruction;

import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentService;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.internal.rendering.RenderException;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.wem.portal.internal.rendering.RendererFactory;
import com.enonic.wem.portal.postprocess.PostProcessInstruction;

import static org.apache.commons.lang.StringUtils.substringAfter;

public final class ComponentInstruction
    implements PostProcessInstruction
{
    static final String MODULE_COMPONENT_PREFIX = "module:";

    private final RendererFactory rendererFactory;

    private final PageComponentService pageComponentService;

    public ComponentInstruction( final RendererFactory rendererFactory, final PageComponentService pageComponentService )
    {
        this.rendererFactory = rendererFactory;
        this.pageComponentService = pageComponentService;
    }

    @Override
    public String evaluate( final PortalContext context, final String instruction )
    {
        if ( !instruction.startsWith( "COMPONENT " ) )
        {
            return null;
        }

        final List<String> list = Lists.newArrayList( Splitter.on( ' ' ).omitEmptyStrings().split( instruction ) );
        if ( list.size() != 2 )
        {
            return null;
        }

        final String path = list.get( 1 );
        return renderComponent( context, path );
    }

    private String renderComponent( final PortalContext context, final String componentSelector )
    {
        final PageComponent component;
        if ( !componentSelector.startsWith( MODULE_COMPONENT_PREFIX ) )
        {
            final ComponentPath componentPath = ComponentPath.from( componentSelector );
            component = resolveComponent( context, componentPath );
        }
        else
        {
            final String name = substringAfter( componentSelector, MODULE_COMPONENT_PREFIX );
            final ComponentName componentName = new ComponentName( name );
            final ModuleKey currentModule = context.getPageTemplate().getDescriptor().getModuleKey();
            component = pageComponentService.getByName( currentModule, componentName );
        }
        return renderPageComponent( context, component );
    }

    private String renderPageComponent( final PortalContext context, final PageComponent component )
    {
        final Renderer<PageComponent, PortalContext> renderer = this.rendererFactory.getRenderer( component );
        if ( renderer == null )
        {
            throw new RenderException( "No Renderer found for: " + component.getClass().getSimpleName() );
        }

        final RenderResult result = renderer.render( component, context );
        return result.getAsString();
    }

    private PageComponent resolveComponent( final PortalContext context, final ComponentPath path )
    {
        final Content content = context.getContent();
        if ( content == null )
        {
            return null;
        }

        final Page page = content.getPage();
        final PageRegions pageRegions;
        if ( ( page != null ) && page.hasRegions() )
        {
            pageRegions = resolvePageRegions( page, context.getPageTemplate() );
        }
        else
        {
            pageRegions = context.getPageTemplate().getRegions();
        }

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
