package com.enonic.wem.core.content.page.rendering;


import java.io.IOException;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.GetPageTemplateByKey;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.region.PageRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.rendering.BaseRenderer;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.Renderer;
import com.enonic.wem.core.rendering.RenderingResult;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.command.Commands.page;
import static com.enonic.wem.core.rendering.RenderingResult.newRenderingResult;

public final class PageRenderer
    extends BaseRenderer
    implements Renderer<Page>
{
    private final ControllerFactory controllerFactory;

    public PageRenderer( final Client client, final Context context )
    {
        super( client, context );
        this.controllerFactory = new ControllerFactory( client );
    }

    @Override
    public RenderingResult execute( final Page page )
    {
        final PageTemplate template = getPageTemplate( page.getTemplate(), client );
        final PageDescriptor descriptor = getPageDescriptor( template.getDescriptor(), client );

        final ModuleResourceKey controllerResource = descriptor.getControllerResource();
        final RootDataSet config = page.hasConfig() ? page.getConfig() : template.getConfig();

        printRegions( config, descriptor );

        final Controller controller = controllerFactory.create( controllerResource, config, context );

        final ControllerResult controllerResult = controller.execute();

        return newRenderingResult().success( controllerResult.isSuccess() ).build();
    }

    private void printRegions( final RootDataSet config, final PageDescriptor descriptor )
    {
        final PageRegions pageRegions = PageRegions.resolve( config, descriptor.getConfigForm() );
        for ( Region region : pageRegions )
        {
            System.out.println( region.getName() );
        }
    }

    private PageTemplate getPageTemplate( final PageTemplateKey pageTemplateKey, final Client client )
    {
        final GetPageTemplateByKey command = page().template().page().getByKey().key( pageTemplateKey );
        return client.execute( command );
    }

    private PageDescriptor getPageDescriptor( final ModuleResourceKey key, final Client client )
    {
        final GetModuleResource command = Commands.module().getResource().resourceKey( key );
        final Resource descriptorResource = client.execute( command );
        try
        {
            final String resourceAsString = descriptorResource.readAsString();
            final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();
            XmlSerializers.pageDescriptor().parse( resourceAsString ).to( builder );
            return builder.build();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Unable to retrieve page descriptor: " + key.toString(), e );
        }
    }
}
