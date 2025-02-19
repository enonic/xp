package com.enonic.xp.portal.impl.rendering;

import com.google.common.base.Function;
import com.google.common.net.MediaType;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.html.HtmlBuilder;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.DescriptorBasedComponent;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.web.HttpStatus;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Objects.requireNonNullElse;

public abstract class DescriptorBasedComponentRenderer<R extends DescriptorBasedComponent>
    implements Renderer<R>
{
    private static final LiveEditAttributeInjection LIVE_EDIT_ATTRIBUTE_INJECTION = new LiveEditAttributeInjection();

    private final ControllerScriptFactory controllerScriptFactory;

    private final Class<R> type;

    private final Function<DescriptorKey, ComponentDescriptor> componentDescriptorGetter;

    public DescriptorBasedComponentRenderer( final ControllerScriptFactory controllerScriptFactory, final Class<R> type,
                                             final Function<DescriptorKey, ComponentDescriptor> componentDescriptorGetter )
    {
        this.controllerScriptFactory = controllerScriptFactory;
        this.type = type;
        this.componentDescriptorGetter = componentDescriptorGetter;
    }

    @Override
    public Class<R> getType()
    {
        return type;
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
            return renderEmptyComponent( component );
        }

        final ResourceKey script = descriptor.getComponentPath().resolve( descriptor.getComponentPath().getName() + ".js" );
        final ControllerScript controllerScript = this.controllerScriptFactory.fromScript( script );

        // render
        final Component previousComponent = portalRequest.getComponent();
        portalRequest.setComponent( component );
        try
        {
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
                        return renderErrorComponentPlaceHolder( component, "No method provided to handle request" );
                    }

                    return renderEmptyComponent( component );
                }
            }

            return LIVE_EDIT_ATTRIBUTE_INJECTION.injectLiveEditAttribute( portalResponse, component.getType() );
        }
        finally
        {
            portalRequest.setComponent( previousComponent );
        }
    }

    private PortalResponse renderEmptyComponent( final DescriptorBasedComponent component )
    {
        final String html = new HtmlBuilder().open( "div" )
            .attribute( RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE, component.getType().toString() )
            .text( "" )
            .close()
            .toString();

        return PortalResponse.create().
            contentType( MediaType.HTML_UTF_8 ).
            body( html ).
            build();
    }

    private PortalResponse renderErrorComponentPlaceHolder( final DescriptorBasedComponent component, final String errorMessage )
    {
        final String html = new HtmlBuilder().open( "div" )
            .attribute( RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE, component.getType().toString() )
            .attribute( "data-portal-placeholder", "true" )
            .attribute( "data-portal-placeholder-error", "true" )
            .open( "span" )
            .attribute( "class", "data-portal-placeholder-error" )
            .escapedText( requireNonNullElse( errorMessage, "" ) )
            .close()
            .close()
            .toString();
        return PortalResponse.create().
            contentType( MediaType.HTML_UTF_8 ).
            body( html ).
            build();
    }

    private ComponentDescriptor resolveDescriptor( final DescriptorBasedComponent component )
    {
        final DescriptorKey descriptorKey = component.getDescriptor();
        return descriptorKey == null ? null : componentDescriptorGetter.apply( descriptorKey );
    }
}
