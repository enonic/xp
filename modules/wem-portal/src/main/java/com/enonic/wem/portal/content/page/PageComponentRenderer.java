package com.enonic.wem.portal.content.page;


import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.rendering.Renderer;

abstract class PageComponentRenderer
    implements Renderer<PageComponent>
{
    @Inject
    protected Client client;

    @Inject
    protected JsControllerFactory controllerFactory;

    public Response render( final PageComponent pageComponent, final JsContext context )
    {
        final Descriptor descriptor = resolveDescriptor( pageComponent.getDescriptor() );

        // create controller
        final JsController controller = this.controllerFactory.newController();
        controller.scriptDir( descriptor.getComponentPath() );
        controller.context( context );

        // render
        final PageComponent previousComponent = context.getComponent();
        try
        {
            context.setComponent( pageComponent );
            return controller.execute();
        }
        finally
        {
            context.setComponent( previousComponent );
        }
    }


    private Descriptor resolveDescriptor( final DescriptorKey descriptorKey )
    {
        final Descriptor descriptor = getComponentDescriptor( descriptorKey );
        if ( descriptor == null )
        {
            throw new DescriptorNotFoundException( descriptorKey );
        }
        return descriptor;
    }

    protected abstract Descriptor getComponentDescriptor( final DescriptorKey descriptorKey );

}
