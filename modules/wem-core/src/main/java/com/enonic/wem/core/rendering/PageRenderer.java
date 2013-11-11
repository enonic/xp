package com.enonic.wem.core.rendering;


import java.io.IOException;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.template.GetPageTemplate;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.content.page.PageDescriptorXmlSerializer;

import static com.enonic.wem.core.rendering.RenderingResult.newRenderingResult;

public final class PageRenderer
    implements ComponentRenderer<Page>
{
    private final PageDescriptorXmlSerializer pageDescriptorXmlSerializer;

    private final ControllerFactory controllerFactory;

    private final Client client;

    public PageRenderer( final Client client )
    {
        this.client = client;
        this.controllerFactory = new ControllerFactory( client );
        this.pageDescriptorXmlSerializer = new PageDescriptorXmlSerializer();
    }

    @Override
    public RenderingResult execute( final Page page, final Context context )
    {
        final PageTemplate template = getPageTemplate( page.getTemplateName(), client );
        final PageDescriptor descriptor = getPageDescriptor( template.getDescriptor(), client );

        final ModuleResourceKey controllerResource = descriptor.getControllerResource();
        final RootDataSet pageConfig = page.getConfig();

        final Controller controller = controllerFactory.create( controllerResource, pageConfig, context );

        final ControllerResult controllerResult = controller.execute();

        return newRenderingResult().success( controllerResult.isSuccess() ).build();
    }

    private PageTemplate getPageTemplate( final PageTemplateName templateName, final Client client )
    {
        final GetPageTemplate command = Commands.template().getPageTemplate().templateName( templateName );
        return client.execute( command );
    }

    private PageDescriptor getPageDescriptor( final ModuleResourceKey key, final Client client )
    {
        final GetModuleResource command = Commands.module().getResource().resourceKey( key );
        final Resource descriptorResource = client.execute( command );
        try
        {
            final String resourceAsString = descriptorResource.readAsString();
            return pageDescriptorXmlSerializer.toPageDescriptor( resourceAsString );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Unable to retrieve page descriptor: " + key.toString(), e );
        }
    }
}
