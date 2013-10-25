package com.enonic.wem.core.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.template.GetTemplate;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorFactory;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateId;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;

public final class PageComponentType
    implements ComponentType<Page>
{
    private final ControllerFactory controllerFactory;

    private final Client client;

    public PageComponentType( final Client client )
    {
        this.client = client;
        controllerFactory = new ControllerFactory( client );
    }

    @Override
    public RenderingResult execute( final Page page, final Context context )
    {
        final PageTemplate template = getPageTemplate( page.getTemplateId(), client );
        final PageDescriptor descriptor = getPageDescriptor( template.getDescriptor(), client );

        final ModuleResourceKey controllerResource = descriptor.getControllerResource();
        final RootDataSet pageConfig = page.getConfig();

        final Controller controller = controllerFactory.create( controllerResource, pageConfig );

        return controller.execute( context );
    }

    private PageTemplate getPageTemplate( final PageTemplateId templateId, final Client client )
    {
        final GetTemplate command = Commands.template().get().templateId( templateId );
        return (PageTemplate) client.execute( command );
    }

    private PageDescriptor getPageDescriptor( final ModuleResourceKey key, final Client client )
    {
        final GetModuleResource command = Commands.module().getResource().resourceKey( key );
        final Resource descriptorResource = client.execute( command );
        return PageDescriptorFactory.create( descriptorResource );
    }
}
