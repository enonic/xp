package com.enonic.wem.portal.content.page;


import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.rendering.Renderer;

abstract class PageComponentRenderer
    implements Renderer
{
    @Inject
    protected Client client;

    @Inject
    protected JsControllerFactory controllerFactory;

    public Response render( final Renderable renderable, final JsContext context )
    {
        final PageComponent pageComponent = (PageComponent) renderable;
        final TemplateKey templateKey = pageComponent.getTemplate();
        final Template template = resolveTemplate( templateKey );

        final Descriptor descriptor = resolveDescriptor( templateKey, template );

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

    private Template resolveTemplate( final TemplateKey templateKey )
    {
        try
        {
            return getComponentTemplate( templateKey );
        }
        catch ( NotFoundException e )
        {
            throw new TemplateNotFoundException( templateKey, e );
        }
    }

    private Descriptor resolveDescriptor( final TemplateKey templateKey, final Template componentTemplate )
    {
        final Descriptor descriptor = getComponentDescriptor( componentTemplate.getDescriptor() );
        if ( descriptor == null )
        {
            throw new DescriptorNotFoundException( templateKey, componentTemplate.getDescriptor() );
        }
        return descriptor;
    }

    protected abstract Template getComponentTemplate( final TemplateKey componentTemplateKey );

    protected abstract Descriptor getComponentDescriptor( final DescriptorKey descriptorKey );

}
