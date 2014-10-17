package com.enonic.wem.portal.internal.content.page;


import java.text.MessageFormat;

import com.enonic.wem.api.content.page.AbstractDescriptorBasedPageComponent;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.RenderingMode;
import com.enonic.wem.portal.internal.controller.ControllerScript;
import com.enonic.wem.portal.internal.controller.ControllerScriptFactory;
import com.enonic.wem.portal.internal.controller.PortalResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;

public abstract class DescriptorBasedPageComponentRenderer<R extends AbstractDescriptorBasedPageComponent>
    implements Renderer<R, PortalContext>
{
    private static final String EMPTY_COMPONENT_EDIT_MODE_HTML =
        "<div data-live-edit-type=\"{0}\" data-live-edit-empty-component=\"true\" class=\"live-edit-empty-component\"></div>";

    private static final String EMPTY_COMPONENT_PREVIEW_MODE_HTML = "<div></div>";

    protected ControllerScriptFactory controllerScriptFactory;

    public final RenderResult render( final R pageComponent, final PortalContext context )
    {
        final Descriptor descriptor = resolveDescriptor( pageComponent );
        if ( descriptor == null )
        {
            return renderEmptyComponent( pageComponent, context );
        }

        // create controller
        final ControllerScript controllerScript = this.controllerScriptFactory.newController( descriptor.getComponentPath() );

        // render
        final PageComponent previousComponent = context.getComponent();
        try
        {
            context.setComponent( pageComponent );
            controllerScript.execute( context );
            return new PortalResponseSerializer( context.getResponse() ).serialize();
        }
        finally
        {
            context.setComponent( previousComponent );
        }
    }

    private RenderResult renderEmptyComponent( final AbstractDescriptorBasedPageComponent pageComponent, final PortalContext context )
    {
        final RenderingMode renderingMode = getRenderingMode( context );
        switch ( renderingMode )
        {
            case EDIT:
                return renderEmptyComponentEditMode( pageComponent );

            case PREVIEW:
                return renderEmptyComponentPreviewMode();

            case LIVE:
                throw new DescriptorNotFoundException( pageComponent.getDescriptor() );

            default:
                throw new DescriptorNotFoundException( pageComponent.getDescriptor() );
        }
    }

    private RenderResult renderEmptyComponentEditMode( final AbstractDescriptorBasedPageComponent pageComponent )
    {
        final String html = MessageFormat.format( EMPTY_COMPONENT_EDIT_MODE_HTML, pageComponent.getType().toString() );

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

    private Descriptor resolveDescriptor( final AbstractDescriptorBasedPageComponent pageComponent )
    {
        final DescriptorKey descriptorKey = pageComponent.getDescriptor();
        return descriptorKey == null ? null : getComponentDescriptor( descriptorKey );
    }

    protected abstract Descriptor getComponentDescriptor( final DescriptorKey descriptorKey );

    private RenderingMode getRenderingMode( final PortalContext context )
    {
        final PortalRequest req = context.getRequest();
        return req == null ? RenderingMode.LIVE : req.getMode();
    }

    public void setControllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }
}
