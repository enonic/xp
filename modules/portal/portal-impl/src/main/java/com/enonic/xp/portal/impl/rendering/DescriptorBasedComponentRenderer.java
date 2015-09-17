package com.enonic.xp.portal.impl.rendering;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.controller.ControllerScript;
import com.enonic.xp.portal.impl.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.Descriptor;
import com.enonic.xp.region.DescriptorBasedComponent;

public abstract class DescriptorBasedComponentRenderer<R extends DescriptorBasedComponent>
    implements Renderer<R>
{
    private static final String EMPTY_COMPONENT_EDIT_MODE_HTML =
        "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></div>";

    private static final String EMPTY_COMPONENT_PREVIEW_MODE_HTML =
        "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></div>";

    private static final String COMPONENT_PLACEHOLDER_HTML =
        "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\" data-portal-placeholder=\"true\"></div>";

    private static final LiveEditAttributeInjection LIVE_EDIT_ATTRIBUTE_INJECTION = new LiveEditAttributeInjection();

    protected ControllerScriptFactory controllerScriptFactory;

    @Override
    public final PortalResponse render( final R component, final PortalRequest portalRequest )
    {
        final Descriptor descriptor = resolveDescriptor( component );
        if ( descriptor == null )
        {
            return renderEmptyComponent( component, portalRequest );
        }

        // create controller
        final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( descriptor.getComponentPath() );

        // render
        final Component previousComponent = portalRequest.getComponent();
        final ApplicationKey previousApplication = portalRequest.getApplicationKey();

        try
        {
            portalRequest.setComponent( component );
            portalRequest.setApplicationKey( descriptor.getKey().getApplicationKey() );
            final PortalResponse portalResponse = controllerScript.execute( portalRequest );

            final RenderMode renderMode = getRenderingMode( portalRequest );
            final String contentType = portalResponse.getContentType();
            if ( renderMode == RenderMode.EDIT && contentType != null && contentType.startsWith( "text/html" ) )
            {
                final Object bodyObj = portalResponse.getBody();
                if ( ( bodyObj == null ) || bodyObj instanceof String && StringUtils.isBlank( (String) bodyObj ) )
                {
                    return renderEmptyComponentPlaceHolder( component );
                }
            }

            final PortalResponse injectedResponse =
                LIVE_EDIT_ATTRIBUTE_INJECTION.injectLiveEditAttribute( portalResponse, component.getType() );
            return new PortalResponseSerializer( injectedResponse ).serialize();
        }
        finally
        {
            portalRequest.setComponent( previousComponent );
            portalRequest.setApplicationKey( previousApplication );
        }
    }

    private PortalResponse renderEmptyComponent( final DescriptorBasedComponent component, final PortalRequest portalRequest )
    {
        final RenderMode renderMode = getRenderingMode( portalRequest );
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

    private PortalResponse renderEmptyComponentEditMode( final DescriptorBasedComponent component )
    {
        final String html = MessageFormat.format( EMPTY_COMPONENT_EDIT_MODE_HTML, component.getType().toString() );

        return PortalResponse.create().
            contentType( "text/html" ).
            body( html ).
            build();
    }

    private PortalResponse renderEmptyComponentPreviewMode()
    {
        final String html = EMPTY_COMPONENT_PREVIEW_MODE_HTML;

        return PortalResponse.create().
            contentType( "text/html" ).
            body( html ).
            build();
    }

    private PortalResponse renderEmptyComponentLiveMode()
    {
        // TODO: Should probably be different than preview.
        return renderEmptyComponentPreviewMode();
    }

    private PortalResponse renderEmptyComponentPlaceHolder( final DescriptorBasedComponent component )
    {
        final String html = MessageFormat.format( COMPONENT_PLACEHOLDER_HTML, component.getType().toString() );

        return PortalResponse.create().
            contentType( "text/html" ).
            body( html ).
            build();
    }

    private Descriptor resolveDescriptor( final DescriptorBasedComponent component )
    {
        final DescriptorKey descriptorKey = component.getDescriptor();
        return descriptorKey == null ? null : getComponentDescriptor( descriptorKey );
    }

    protected abstract Descriptor getComponentDescriptor( final DescriptorKey descriptorKey );

    private RenderMode getRenderingMode( final PortalRequest portalRequest )
    {
        return portalRequest == null ? RenderMode.LIVE : portalRequest.getMode();
    }

    public void setControllerScriptFactory( final ControllerScriptFactory value )
    {
        this.controllerScriptFactory = value;
    }
}
