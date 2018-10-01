package com.enonic.xp.portal.impl.rendering;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

import com.google.common.html.HtmlEscapers;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.DescriptorBasedComponent;
import com.enonic.xp.web.HttpStatus;

public abstract class DescriptorBasedComponentRenderer<R extends DescriptorBasedComponent>
    implements Renderer<R>
{
    private static final String EMPTY_COMPONENT_EDIT_MODE_HTML =
        "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></div>";

    private static final String EMPTY_COMPONENT_PREVIEW_MODE_HTML =
        "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></div>";

    private static final String COMPONENT_PLACEHOLDER_ERROR_HTML = "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE +
        "=\"{0}\" data-portal-placeholder=\"true\" data-portal-placeholder-error=\"true\"><span class=\"data-portal-placeholder-error\">{1}</span></div>";

    private static final LiveEditAttributeInjection LIVE_EDIT_ATTRIBUTE_INJECTION = new LiveEditAttributeInjection();

    protected ControllerScriptFactory controllerScriptFactory;

    @Override
    public final PortalResponse render( final R component, final PortalRequest portalRequest )
    {
        try
        {
            return doRender( component, portalRequest );
        }
        catch ( Exception e )
        {
            final RenderMode renderMode = getRenderingMode( portalRequest );
            if ( renderMode == RenderMode.EDIT )
            {
                return renderErrorComponentPlaceHolder( component, e.getMessage() );
            }
            throw e;
        }
    }

    private PortalResponse doRender( final R component, final PortalRequest portalRequest )
    {
        final ComponentDescriptor descriptor = resolveDescriptor( component );
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
            final MediaType contentType = portalResponse.getContentType();
            if ( renderMode == RenderMode.EDIT && contentType != null && contentType.withoutParameters().type().equals( "text" ) )
            {
                final Object bodyObj = portalResponse.getBody();
                if ( ( bodyObj == null ) || bodyObj instanceof String && StringUtils.isBlank( (String) bodyObj ) )
                {
                    if ( portalResponse.getStatus().equals( HttpStatus.METHOD_NOT_ALLOWED ) )
                    {
                        final String errorMessage = "No method provided to handle request";
                        return renderErrorComponentPlaceHolder( component, errorMessage );
                    }

                    return renderEmptyComponent( component, portalRequest );
                }
            }

            final PortalResponse injectedResponse =
                LIVE_EDIT_ATTRIBUTE_INJECTION.injectLiveEditAttribute( portalResponse, component.getType() );
            return injectedResponse;
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
            case INLINE:
                return renderEmptyComponentPreviewMode( component );

            case LIVE:
                return renderEmptyComponentPreviewMode( component );

            default:
                throw new DescriptorNotFoundException( component.getDescriptor() );
        }
    }

    private PortalResponse renderEmptyComponentEditMode( final DescriptorBasedComponent component )
    {
        final String html = MessageFormat.format( EMPTY_COMPONENT_EDIT_MODE_HTML, component.getType().toString() );

        return PortalResponse.create().
            contentType( MediaType.create( "text", "html" ) ).
            body( html ).
            build();
    }

    private PortalResponse renderEmptyComponentPreviewMode( final DescriptorBasedComponent component )
    {
        final String html = MessageFormat.format( EMPTY_COMPONENT_PREVIEW_MODE_HTML, component.getType().toString() );

        return PortalResponse.create().
            contentType( MediaType.create( "text", "html" ) ).
            body( html ).
            build();
    }

    private PortalResponse renderErrorComponentPlaceHolder( final DescriptorBasedComponent component, final String errorMessage )
    {
        final String escapedMessage = errorMessage == null ? "" : HtmlEscapers.htmlEscaper().escape( errorMessage );
        final String html = MessageFormat.format( COMPONENT_PLACEHOLDER_ERROR_HTML, component.getType().toString(), escapedMessage );

        return PortalResponse.create().
            contentType( MediaType.create( "text", "html" ) ).
            body( html ).
            build();
    }

    private ComponentDescriptor resolveDescriptor( final DescriptorBasedComponent component )
    {
        final DescriptorKey descriptorKey = component.getDescriptor();
        return descriptorKey == null ? null : getComponentDescriptor( descriptorKey );
    }

    protected abstract ComponentDescriptor getComponentDescriptor( final DescriptorKey descriptorKey );

    private RenderMode getRenderingMode( final PortalRequest portalRequest )
    {
        return portalRequest == null ? RenderMode.LIVE : portalRequest.getMode();
    }

    public void setControllerScriptFactory( final ControllerScriptFactory value )
    {
        this.controllerScriptFactory = value;
    }
}
