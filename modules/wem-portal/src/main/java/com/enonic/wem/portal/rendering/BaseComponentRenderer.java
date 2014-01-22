package com.enonic.wem.portal.rendering;


import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;

abstract class BaseComponentRenderer
    implements Renderer
{
    @Inject
    protected Client client;

    @Inject
    protected JsControllerFactory controllerFactory;

    public Response render( final Renderable renderable, final JsContext context )
    {
        final PageComponent pageComponent = (PageComponent) renderable;
        final TemplateKey componentTemplateKey = pageComponent.getTemplate();
        final Template componentTemplate = getComponentTemplate( componentTemplateKey );
        final Descriptor componentDescriptor = getComponentDescriptor( componentTemplate.getDescriptor() );
        if ( componentDescriptor == null )
        {
            throw new RenderException( "Component descriptor [{0}] not found.", componentTemplate.getDescriptor() );
        }

        // find component controller path
        final ModuleResourceKey jsModuleResource = componentDescriptor.getComponentPath();

        // create controller
        final JsController controller = this.controllerFactory.newController();
        controller.scriptDir( jsModuleResource );
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

    protected abstract Template getComponentTemplate( final TemplateKey componentTemplateKey );

    protected abstract Descriptor getComponentDescriptor( final DescriptorKey descriptorKey );

}
