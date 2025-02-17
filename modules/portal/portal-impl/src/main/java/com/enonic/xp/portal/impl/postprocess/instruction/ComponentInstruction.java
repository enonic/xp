package com.enonic.xp.portal.impl.postprocess.instruction;

import java.text.MessageFormat;
import java.util.List;

import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Splitter;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.ComponentService;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

@org.osgi.service.component.annotations.Component(immediate = true)
public final class ComponentInstruction
    implements PostProcessInstruction
{
    private static final String APPLICATION_COMPONENT_PREFIX = "module:";

    public static final String FRAGMENT_COMPONENT = "fragment";

    public static final String COMPONENT_INSTRUCTION_PREFIX = "COMPONENT";

    private RendererDelegate rendererDelegate;

    private ComponentService componentService;

    @Reference
    public void setRendererDelegate( final RendererDelegate rendererDelegate )
    {
        this.rendererDelegate = rendererDelegate;
    }

    @Reference
    public void setComponentService( final ComponentService componentService )
    {
        this.componentService = componentService;
    }

    @Override
    public PortalResponse evaluate( final PortalRequest portalRequest, final String instruction )
    {
        final String componentPath = getComponentPathFromInstruction( instruction );
        return componentPath == null ? null : renderComponent( portalRequest, componentPath );
    }

    private String getComponentPathFromInstruction( final String instruction )
    {
        if ( !Instruction.isInstruction( instruction, COMPONENT_INSTRUCTION_PREFIX ) )
        {
            return null;
        }

        final List<String> list = Splitter.on( ' ' ).omitEmptyStrings().splitToList( instruction );

        if ( list.size() != 2 )
        {
            return null;
        }

        return list.get( 1 );
    }

    private PortalResponse renderComponent( final PortalRequest portalRequest, final String componentSelector )
    {
        final Component component = resolveComponent( portalRequest, componentSelector );
        return renderComponent( portalRequest, component );
    }

    private Component resolveComponent( final PortalRequest portalRequest, final String componentSelector )
    {
        if ( componentSelector.startsWith( APPLICATION_COMPONENT_PREFIX ) )
        {
            return resolveComponentFromModule( componentSelector.substring( APPLICATION_COMPONENT_PREFIX.length() ),
                                               portalRequest.getApplicationKey() );
        }
        else if ( FRAGMENT_COMPONENT.equalsIgnoreCase( componentSelector ) )
        {
            return getPageFragment( portalRequest.getContent() );
        }
        else
        {
            return resolveComponent( portalRequest.getContent(), ComponentPath.from( componentSelector ) );
        }
    }

    private Component resolveComponentFromModule( final String name, final ApplicationKey currentApplication )
    {
        return currentApplication == null ? null : componentService.getByKey( DescriptorKey.from( currentApplication, name ) );
    }

    private PortalResponse renderComponent( final PortalRequest portalRequest, final Component component )
    {
        final Trace trace = Tracer.newTrace( "renderComponent" );
        if ( trace == null )
        {
            return rendererDelegate.render( component, portalRequest );
        }

        trace.put( "componentPath", component.getPath() );
        trace.put( "type", component.getType().toString() );
        return Tracer.trace( trace, () -> rendererDelegate.render( component, portalRequest ) );
    }

    private Component resolveComponent( final Content content, final ComponentPath path )
    {
        if ( content == null )
        {
            return null;
        }

        if ( content.getType().isFragment() )
        {
            return resolveComponentInFragment( content, path );
        }

        Component component = content.getPage().getRegions().getComponent( path );

        if ( component == null )
        {
            throw new RenderException( MessageFormat.format( "Component not found: [{0}]", path ) );
        }

        return component;
    }

    private Component resolveComponentInFragment( final Content content, final ComponentPath path )
    {
        final Component fragmentComponent = content.getPage().getFragment();

        if ( !( fragmentComponent instanceof LayoutComponent ) )
        {
            throw new RenderException( MessageFormat.format( "Component not found: [{0}]", path ) );
        }

        final LayoutComponent layout = (LayoutComponent) fragmentComponent;
        final LayoutRegions pageRegions = layout.getRegions();
        final Component component = pageRegions.getComponent( path );

        if ( component == null )
        {
            throw new RenderException( MessageFormat.format( "Component not found: [{0}]", path ) );
        }

        return component;
    }

    private Component getPageFragment( final Content content )
    {
        if ( content == null || content.getPage() == null )
        {
            return null;
        }

        return content.getPage().getFragment();
    }
}
