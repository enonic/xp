package com.enonic.xp.portal.impl.postprocess.instruction;

import java.util.List;

import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.page.Page;
import com.enonic.xp.core.content.page.PageRegions;
import com.enonic.xp.core.content.page.region.Component;
import com.enonic.xp.core.content.page.region.ComponentName;
import com.enonic.xp.core.content.page.region.ComponentPath;
import com.enonic.xp.core.content.page.region.ComponentService;
import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.rendering.RenderResult;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;

import static org.apache.commons.lang.StringUtils.substringAfter;

@org.osgi.service.component.annotations.Component(immediate = true)
public final class ComponentInstruction
    implements PostProcessInstruction
{
    static final String MODULE_COMPONENT_PREFIX = "module:";

    private RendererFactory rendererFactory;

    private ComponentService componentService;

    @Reference
    public void setRendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
    }

    @Reference
    public void setComponentService( final ComponentService componentService )
    {
        this.componentService = componentService;
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
        final Component component;
        if ( !componentSelector.startsWith( MODULE_COMPONENT_PREFIX ) )
        {
            final ComponentPath componentPath = ComponentPath.from( componentSelector );
            component = resolveComponent( context, componentPath );
        }
        else
        {
            final String name = substringAfter( componentSelector, MODULE_COMPONENT_PREFIX );
            final ComponentName componentName = name != null ? new ComponentName( name ) : null;
            final ModuleKey currentModule = context.getPageTemplate().getController().getModuleKey();
            component = componentService.getByName( currentModule, componentName );
        }
        return renderComponent( context, component );
    }

    private String renderComponent( final PortalContext context, final Component component )
    {
        final Renderer<Component> renderer = this.rendererFactory.getRenderer( component );
        if ( renderer == null )
        {
            throw new RenderException( "No Renderer found for: " + component.getClass().getSimpleName() );
        }

        final RenderResult result = renderer.render( component, context );
        return result.getAsString();
    }

    private Component resolveComponent( final PortalContext context, final ComponentPath path )
    {
        final Content content = context.getContent();
        if ( content == null )
        {
            return null;
        }

        final Page page = content.getPage();
        final PageRegions pageRegions = page.getRegions();
        Component component = pageRegions.getComponent( path );
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
}
