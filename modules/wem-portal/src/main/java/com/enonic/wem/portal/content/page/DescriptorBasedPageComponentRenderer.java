package com.enonic.wem.portal.content.page;


import java.text.MessageFormat;

import javax.inject.Inject;

import com.enonic.wem.api.content.page.AbstractDescriptorBasedPageComponent;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.rendering.RenderResult;
import com.enonic.wem.portal.rendering.Renderer;

abstract class DescriptorBasedPageComponentRenderer
    implements Renderer<AbstractDescriptorBasedPageComponent>
{

    private static final String EMPTY_COMPONENT_EDIT_MODE_HTML =
        "<div data-live-edit-type=\"{0}\" data-live-edit-empty-component=\"true\" class=\"live-edit-empty-component\"></div>";

    private static final String EMPTY_COMPONENT_PREVIEW_MODE_HTML = "<div></div>";

    @Inject
    protected JsControllerFactory controllerFactory;

    public RenderResult render( final AbstractDescriptorBasedPageComponent pageComponent, final JsContext context )
    {
        final Descriptor descriptor = resolveDescriptor( pageComponent );
        if ( descriptor == null )
        {
            return renderEmptyComponent( pageComponent, context );
        }

        // create controller
        final JsController controller = this.controllerFactory.newController();
        controller.scriptDir( descriptor.getComponentPath() );
        controller.context( context );

        // render
        final PageComponent previousComponent = context.getComponent();
        try
        {
            context.setComponent( pageComponent );
            controller.execute();
            return new JsHttpResponseSerializer( context.getResponse() ).serialize();
        }
        finally
        {
            context.setComponent( previousComponent );
        }
    }

    private RenderResult renderEmptyComponent( final AbstractDescriptorBasedPageComponent pageComponent, final JsContext context )
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

    private RenderingMode getRenderingMode( final JsContext context )
    {
        final JsHttpRequest req = context.getRequest();
        return req == null ? RenderingMode.LIVE : req.getMode();
    }
}
