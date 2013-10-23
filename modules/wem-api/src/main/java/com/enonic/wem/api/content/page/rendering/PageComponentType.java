package com.enonic.wem.api.content.page.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.command.template.GetTemplate;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorFactory;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.TemplateId;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.rendering.ComponentType;
import com.enonic.wem.api.rendering.Context;
import com.enonic.wem.api.rendering.RenderingResult;
import com.enonic.wem.api.resource.Resource;

public final class PageComponentType
    extends ComponentType<Page>
{
    @Override
    public RenderingResult execute( final Page page, final Context context, final Client client )
    {
        final PageTemplate template = getPageTemplate( page.getTemplateId(), client );
        final PageDescriptor descriptor = getPageDescriptor( template.getDescriptor(), client );
        final Resource controllerResource = getControllerResource( descriptor, client );
        final Controller controller = ControllerFactory.create( controllerResource, descriptor.getControllerSetup().getParams() );

        final RootDataSet pageConfig = page.getConfig();
        final RootDataSet pageConfigFromTemplate = template.getPageConfig();
        final RootDataSet mergedPageConfig = mergeConfig( pageConfig, pageConfigFromTemplate );

        final RootDataSet templateConfig = template.getTemplateConfig();

        return new ControllerExecutor( controller, mergedPageConfig, templateConfig ).execute();
    }

    private PageTemplate getPageTemplate( final TemplateId templateId, final Client client )
    {
        final GetTemplate command = Commands.pageTemplate().get().byId( templateId );
        return (PageTemplate) client.execute( command );
    }

    private PageDescriptor getPageDescriptor( final ModuleResourceKey key, final Client client )
    {
        final GetModuleResource command = Commands.module().getResource().resourceKey( key );
        final Resource descriptorResource = client.execute( command );
        return PageDescriptorFactory.create( descriptorResource );
    }
}
