package com.enonic.xp.portal.impl.postprocess.instruction;

import java.text.MessageFormat;
import java.util.List;

import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Splitter;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
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
import com.enonic.xp.region.Region;
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
        final PortalResponse portalResponse = componentPath == null ? null : renderComponent( portalRequest, componentPath );

        return portalResponse;
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
        if ( FRAGMENT_COMPONENT.equalsIgnoreCase( componentSelector ) )
        {
            return resolveFragmentComponent( portalRequest );
        }

        if ( componentSelector.startsWith( APPLICATION_COMPONENT_PREFIX ) )
        {
            return resolveComponentFromModule( componentSelector, portalRequest.getApplicationKey() );
        }

        return resolveComponent( portalRequest, ComponentPath.from( componentSelector ) );
    }

    private Component resolveComponentFromModule( final String componentSelector, final ApplicationKey currentApplication )
    {
        final String name = componentSelector.substring( APPLICATION_COMPONENT_PREFIX.length() );
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

    private Component resolveComponent( final PortalRequest portalRequest, final ComponentPath path )
    {
        final Content content = portalRequest.getContent();

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

        if ( component instanceof LayoutComponent )
        {
            return resolveLayoutWithRegions( (LayoutComponent) component, content.getPage() );
        }

        return component;
    }

    private LayoutComponent resolveLayoutWithRegions( final LayoutComponent existingLayout, final Page page )
    {
        if ( !existingLayout.hasDescriptor() )
        {
            return existingLayout;
        }

        final LayoutComponent layoutFromDescriptor = (LayoutComponent) componentService.getByKey( existingLayout.getDescriptor() );

        if ( layoutFromDescriptor == null )
        {
            return existingLayout;
        }

        final LayoutComponent layoutComponent = buildLayoutWithRegions( existingLayout, layoutFromDescriptor );
        setParentRegionOnLayout( page, existingLayout, layoutComponent );
        return layoutComponent;
    }

    private LayoutComponent buildLayoutWithRegions( final LayoutComponent existingLayout, final LayoutComponent layoutFromDescriptor )
    {
        final LayoutComponent.Builder layoutBuilder = LayoutComponent.create( existingLayout );
        final LayoutRegions.Builder regionsBuilder = LayoutRegions.create();

        layoutFromDescriptor.getRegions().forEach( region -> {
            final Region existingRegion = existingLayout.getRegion( region.getName() );
            final Region regionToAdd = existingRegion == null ? Region.create().name( region.getName() ).build() : existingRegion;
            regionsBuilder.add( regionToAdd );
        } );

        return layoutBuilder.regions( regionsBuilder.build() ).build();
    }

    private void setParentRegionOnLayout( final Page page, final LayoutComponent existingLayout, final LayoutComponent resultingLayout )
    {
        if ( page == null || page.getRegions() == null )
        {
            return;
        }

        page.getRegions().forEach( region -> {
            final int index = region.getIndex( existingLayout );

            if ( index > -1 )
            {
                Region.create( region ).set( index, resultingLayout ).build(); // The only way to set layout's parent region
            }
        } );
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

    private Component resolveFragmentComponent( final PortalRequest portalRequest )
    {
        final Component fragmentComponent = getPageFragment( portalRequest );
        return processFragment( fragmentComponent );
    }

    private Component processFragment( final Component fragmentComponent )
    {
        if ( fragmentComponent instanceof LayoutComponent )
        {
            return resolveLayoutWithRegions( (LayoutComponent) fragmentComponent, null  );
        }

        return fragmentComponent;
    }

    private Component getPageFragment( final PortalRequest portalRequest )
    {
        final Content content = portalRequest.getContent();

        if ( content == null || content.getPage() == null )
        {
            return null;
        }

        return content.getPage().getFragment();
    }
}
