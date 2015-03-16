package com.enonic.xp.portal.impl.rendering;

import java.text.MessageFormat;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.region.Component;
import com.enonic.xp.content.page.region.Descriptor;
import com.enonic.xp.content.page.region.DescriptorBasedComponent;
import com.enonic.xp.portal.impl.controller.ControllerScript;
import com.enonic.xp.portal.impl.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.rendering.RenderResult;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;

public abstract class DescriptorBasedComponentRenderer<R extends DescriptorBasedComponent>
    implements Renderer<R>
{
    private static final String EMPTY_COMPONENT_EDIT_MODE_HTML = "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></div>";

    private static final String EMPTY_COMPONENT_PREVIEW_MODE_HTML = "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></div>";

    private static final LiveEditAttributeInjection LIVE_EDIT_ATTRIBUTE_INJECTION = new LiveEditAttributeInjection();

    protected ControllerScriptFactory controllerScriptFactory;

    @Override
    public final RenderResult render( final R component, final PortalContext context )
    {
        final Descriptor descriptor = resolveDescriptor( component );
        if ( descriptor == null )
        {
            return renderEmptyComponent( component, context );
        }

        // create controller
        final ControllerScript controllerScript = this.controllerScriptFactory.newController( descriptor.getComponentPath() );

        // render
        final Component previousComponent = context.getComponent();
        try
        {
            context.setComponent( component );
            controllerScript.execute( context );
            final PortalResponse response = context.getResponse();
            LIVE_EDIT_ATTRIBUTE_INJECTION.injectLiveEditAttribute( response, component.getType() );
            return new PortalResponseSerializer( response ).serialize();
        }
        finally
        {
            context.setComponent( previousComponent );
        }
    }

    private RenderResult renderEmptyComponent( final DescriptorBasedComponent component, final PortalContext context )
    {
        final RenderMode renderMode = getRenderingMode( context );
        switch ( renderMode )
        {
            case EDIT:
                return renderEmptyComponentEditMode( component );

            case PREVIEW:
                return renderEmptyComponentPreviewMode();

            case LIVE:
                return renderEmptyComponentLiveMode();

            default:
                throw new DescriptorNotFoundException( component.getDescriptor() );
        }
    }

    private RenderResult renderEmptyComponentEditMode( final DescriptorBasedComponent component )
    {
        final String html = MessageFormat.format( EMPTY_COMPONENT_EDIT_MODE_HTML, component.getType().toString() );

        return RenderResult.newRenderResult().
            type( "text/html" ).
            entity( html ).
            build();
    }

    private RenderResult renderEmptyComponentPreviewMode()
    {
        final String html = EMPTY_COMPONENT_PREVIEW_MODE_HTML;

        return RenderResult.newRenderResult().
            type( "text/html" ).
            entity( html ).
            build();
    }

    private RenderResult renderEmptyComponentLiveMode()
    {
        // TODO: Should probably be different than preview.
        return renderEmptyComponentPreviewMode();
    }

    private Descriptor resolveDescriptor( final DescriptorBasedComponent component )
    {
        final DescriptorKey descriptorKey = component.getDescriptor();
        return descriptorKey == null ? null : getComponentDescriptor( descriptorKey );
    }

    protected abstract Descriptor getComponentDescriptor( final DescriptorKey descriptorKey );

    private RenderMode getRenderingMode( final PortalContext context )
    {
        return context == null ? RenderMode.LIVE : context.getMode();
    }

    public void setControllerScriptFactory( final ControllerScriptFactory value )
    {
        this.controllerScriptFactory = value;
    }
}
