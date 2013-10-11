package com.enonic.wem.api.content.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.command.template.GetTemplate;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorFactory;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.Part;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.page.TemplateId;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;

public class ControllerExecutorFactory
{
    private Client client;

    ControllerExecutor page( Page page )
    {
        PageTemplate template = getPageTemplate( page.getTemplateId() );

        PageDescriptor descriptor = getPageDescriptor( template.getDescriptor() );

        Resource controllerResource = getControllerResource( descriptor );

        Controller controller = ControllerFactory.create( controllerResource, descriptor.getControllerSetup().getParams() );

        final RootDataSet pageConfig = page.getConfig();
        final RootDataSet pageConfigFromTemplate = template.getPageConfig();
        final RootDataSet templateConfig = template.getTemplateConfig();
        final RootDataSet mergedPageConfig = mergeConfig( pageConfig, pageConfigFromTemplate );

        return new ControllerExecutor( controller, mergedPageConfig, templateConfig );
    }

    ControllerExecutor part( Part part )
    {
        PartTemplate template = getPartTemplate( part.getTemplateId() );

        PageDescriptor descriptor = getPageDescriptor( template.getDescriptor() );

        Resource controllerResource = getControllerResource( descriptor );

        Controller controller = ControllerFactory.create( controllerResource, descriptor.getControllerSetup().getParams() );

        final RootDataSet pageConfig = part.getConfig();
        final RootDataSet pageConfigFromTemplate = template.getPartConfig();
        final RootDataSet templateConfig = template.getTemplateConfig();
        final RootDataSet mergedPageConfig = mergeConfig( pageConfig, pageConfigFromTemplate );

        return new ControllerExecutor( controller, mergedPageConfig, templateConfig );
    }

    private RootDataSet mergeConfig( final RootDataSet pageConfig, final RootDataSet pageConfigFromTemplate )
    {
        return new RootDataSet();
    }


    private PageTemplate getPageTemplate( final TemplateId templateId )
    {
        final GetTemplate command = Commands.pageTemplate().get().byId( templateId );
        return (PageTemplate) client.execute( command );
    }

    private PartTemplate getPartTemplate( final TemplateId templateId )
    {
        final GetTemplate command = Commands.partTemplate().get().byId( templateId );
        return (PartTemplate) client.execute( command );
    }

    private Resource getControllerResource( final PageDescriptor descriptor )
    {
        final GetModuleResource command = Commands.module().getResource().resourceKey( descriptor.getControllerSetup().getSource() );
        return client.execute( command );
    }

    private PageDescriptor getPageDescriptor( final ModuleResourceKey key )
    {
        final GetModuleResource command = Commands.module().getResource().resourceKey( key );
        final Resource descriptorResource = client.execute( command );
        return PageDescriptorFactory.create( descriptorResource );
    }
}
