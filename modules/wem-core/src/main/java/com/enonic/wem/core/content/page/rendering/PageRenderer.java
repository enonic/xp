package com.enonic.wem.core.content.page.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.GetPageDescriptor;
import com.enonic.wem.api.command.content.page.GetPageTemplateByKey;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.rendering.BaseRenderer;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.Renderer;
import com.enonic.wem.core.rendering.RenderingResult;

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

        final ModuleResourceKey controllerResource = descriptor.getModuleResourceKey();
        final RootDataSet config = page.hasConfig() ? page.getConfig() : template.getConfig();

        PageRegions pageRegions = template.getRegions();
        if ( page.hasRegions() )
        {
            pageRegions = page.getRegions();
        }
        printRegions( pageRegions );

        final Controller controller = controllerFactory.create( controllerResource, config, context );

        final ControllerResult controllerResult = controller.execute();

        return newRenderingResult().success( controllerResult.isSuccess() ).build();
    }

    private void printRegions( final PageRegions pageRegions )
    {
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

    private PageDescriptor getPageDescriptor( final PageDescriptorKey key, final Client client )
    {
        final GetPageDescriptor command = Commands.page().descriptor().page().getByKey( key );
        return client.execute( command );
    }
}
