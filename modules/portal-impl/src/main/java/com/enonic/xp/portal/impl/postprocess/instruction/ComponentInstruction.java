package com.enonic.xp.portal.impl.postprocess.instruction;

import java.util.List;

import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.page.Page;
import com.enonic.xp.content.page.PageRegions;
import com.enonic.xp.content.page.region.Component;
import com.enonic.xp.content.page.region.ComponentName;
import com.enonic.xp.content.page.region.ComponentPath;
import com.enonic.xp.content.page.region.ComponentService;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;
import com.enonic.xp.portal.rendering.RenderResult;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.rendering.RendererFactory;

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
    public String evaluate( final PortalRequest portalRequest, final PortalResponse portalResponse, final String instruction )
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
        return renderComponent( portalRequest, portalResponse, path );
    }

    private String renderComponent( final PortalRequest portalRequest, final PortalResponse portalResponse, final String componentSelector )
    {
        final Component component;
        if ( !componentSelector.startsWith( MODULE_COMPONENT_PREFIX ) )
        {
            final ComponentPath componentPath = ComponentPath.from( componentSelector );
            component = resolveComponent( portalRequest, componentPath );
        }
        else
        {
            final String name = substringAfter( componentSelector, MODULE_COMPONENT_PREFIX );
            final ComponentName componentName = name != null ? new ComponentName( name ) : null;
            final ModuleKey currentModule = portalRequest.getPageTemplate().getController().getModuleKey();
            component = componentService.getByName( currentModule, componentName );
        }
        return renderComponent( portalRequest, portalResponse, component );
    }

    private String renderComponent( final PortalRequest portalRequest, final PortalResponse portalResponse, final Component component )
    {
        final Renderer<Component> renderer = this.rendererFactory.getRenderer( component );
        if ( renderer == null )
        {
            throw new RenderException( "No Renderer found for: " + component.getClass().getSimpleName() );
        }

        final RenderResult result = renderer.render( component, portalRequest );
        return result.getAsString();
    }

    private Component resolveComponent( final PortalRequest portalRequest, final ComponentPath path )
    {
        final Content content = portalRequest.getContent();
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
            component = portalRequest.getPageTemplate().getRegions().getComponent( path );
            if ( component == null )
            {
                throw new RenderException( "Component not found: [{0}]", path );
            }
        }

        return component;
    }
}
