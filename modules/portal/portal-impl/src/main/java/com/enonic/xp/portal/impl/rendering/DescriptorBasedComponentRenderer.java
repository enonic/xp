package com.enonic.xp.portal.impl.rendering;

import java.text.MessageFormat;

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
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.web.HttpStatus;

import static com.google.common.base.Strings.nullToEmpty;

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

    private final ControllerScriptFactory controllerScriptFactory;

    public DescriptorBasedComponentRenderer( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }

    @Override
    public final PortalResponse render( final R component, final PortalRequest portalRequest )
    {
        try
        {
            return doRender( component, portalRequest );
        }
        catch ( Exception e )
        {
            final RenderMode renderMode = portalRequest.getMode();
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

        final ResourceKey script = descriptor.getComponentPath().resolve( descriptor.getComponentPath().getName() + ".js" );
        final ControllerScript controllerScript = this.controllerScriptFactory.fromScript( script );

        // render
        final Component previousComponent = portalRequest.getComponent();
        final ApplicationKey previousApplication = portalRequest.getApplicationKey();

        try
        {
            portalRequest.setComponent( component );
            portalRequest.setApplicationKey( descriptor.getKey().getApplicationKey() );
            final PortalResponse portalResponse = controllerScript.execute( portalRequest );

            final RenderMode renderMode = portalRequest.getMode();
            final MediaType contentType = portalResponse.getContentType();
            if ( renderMode == RenderMode.EDIT && contentType != null && contentType.type().equals( "text" ) )
            {
                final Object bodyObj = portalResponse.getBody();
                if ( bodyObj == null || ( bodyObj instanceof String && nullToEmpty( (String) bodyObj ).isBlank() ) )
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
        final RenderMode renderMode = portalRequest.getMode();
        if ( renderMode == RenderMode.EDIT )
        {
            return renderEmptyComponentEditMode( component );
        }
        else
        {
            return renderEmptyComponentPreviewMode( component );
        }
    }

    private PortalResponse renderEmptyComponentEditMode( final DescriptorBasedComponent component )
    {
        final String html = MessageFormat.format( EMPTY_COMPONENT_EDIT_MODE_HTML, component.getType().toString() );

        return PortalResponse.create().
            contentType( MediaType.HTML_UTF_8 ).
            body( html ).
            build();
    }

    private PortalResponse renderEmptyComponentPreviewMode( final DescriptorBasedComponent component )
    {
        final String html = MessageFormat.format( EMPTY_COMPONENT_PREVIEW_MODE_HTML, component.getType().toString() );

        return PortalResponse.create().
            contentType( MediaType.HTML_UTF_8 ).
            body( html ).
            build();
    }

    private PortalResponse renderErrorComponentPlaceHolder( final DescriptorBasedComponent component, final String errorMessage )
    {
        final String escapedMessage = errorMessage == null ? "" : HtmlEscapers.htmlEscaper().escape( errorMessage );
        final String html = MessageFormat.format( COMPONENT_PLACEHOLDER_ERROR_HTML, component.getType().toString(), escapedMessage );

        return PortalResponse.create().
            contentType( MediaType.HTML_UTF_8 ).
            body( html ).
            build();
    }

    private ComponentDescriptor resolveDescriptor( final DescriptorBasedComponent component )
    {
        final DescriptorKey descriptorKey = component.getDescriptor();
        return descriptorKey == null ? null : getComponentDescriptor( descriptorKey );
    }

    protected abstract ComponentDescriptor getComponentDescriptor( DescriptorKey descriptorKey );
}
