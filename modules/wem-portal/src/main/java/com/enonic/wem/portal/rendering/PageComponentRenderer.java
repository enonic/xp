package com.enonic.wem.portal.rendering;


import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.exception.PortalWebException;

import static com.enonic.wem.api.command.Commands.page;

public final class PageComponentRenderer
{
    @Inject
    protected Client client;

    @Inject
    protected JsControllerFactory controllerFactory;

    public PageComponentRenderer()
    {
    }

    public Response render( final PageComponent pageComponent, final JsContext context )
    {
        final TemplateKey componentTemplateKey = pageComponent.getTemplate();
        final Template componentTemplate = getComponentTemplate( componentTemplateKey );
        final Descriptor componentDescriptor = getComponentDescriptor( componentTemplate );

        // find component controller path
        final ModuleResourceKey jsModuleResource = componentDescriptor.getComponentPath();

        // create controller
        final JsController controller = this.controllerFactory.newController();
        controller.scriptDir( jsModuleResource );
        controller.context( context );

        // render
        return controller.execute();
    }

    private Template getComponentTemplate( final TemplateKey componentTemplateKey )
    {
        try
        {
            switch ( componentTemplateKey.getTemplateType() )
            {
                case IMAGE:
                    return client.execute( page().template().image().getByKey().key( (ImageTemplateKey) componentTemplateKey ) );
                case LAYOUT:
                    return client.execute( page().template().layout().getByKey().key( (LayoutTemplateKey) componentTemplateKey ) );
                case PAGE:
                    return client.execute( page().template().page().getByKey().key( (PageTemplateKey) componentTemplateKey ) );
                case PART:
                    return client.execute( page().template().part().getByKey().key( (PartTemplateKey) componentTemplateKey ) );
                default:
                    return null;
            }
        }
        catch ( NotFoundException e )
        {
            return null;
        }
    }

    private Descriptor getComponentDescriptor( final Template template )
    {
        final DescriptorKey descriptorKey = template.getDescriptor();
        switch ( descriptorKey.getDescriptorType() )
        {
            case IMAGE:
                return this.client.execute( page().descriptor().image().getByKey( (ImageDescriptorKey) descriptorKey ) );

            case LAYOUT:
                return this.client.execute( page().descriptor().layout().getByKey( (LayoutDescriptorKey) descriptorKey ) );
            case PAGE:
                return this.client.execute( page().descriptor().page().getByKey( (PageDescriptorKey) descriptorKey ) );
            case PART:
                return this.client.execute( page().descriptor().part().getByKey( (PartDescriptorKey) descriptorKey ) );
            default:
                throw PortalWebException.notFound().message( "Component descriptor for template [{0}] not found.",
                                                             template.getName() ).build();
        }
    }
}
