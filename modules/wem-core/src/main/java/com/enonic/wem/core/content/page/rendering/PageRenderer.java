package com.enonic.wem.core.content.page.rendering;


import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.rendering.BaseRenderer;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.Renderer;
import com.enonic.wem.core.rendering.RenderingResult;

import static com.enonic.wem.core.rendering.RenderingResult.newRenderingResult;

public final class PageRenderer
    extends BaseRenderer
    implements Renderer<Page>
{
    private final ControllerFactory controllerFactory;

    @Inject
    protected PageDescriptorService pageDescriptorService;

    @Inject
    protected PageTemplateService pageTemplateService;

    public PageRenderer( final Client client, final Context context )
    {
        super( client, context );
        this.controllerFactory = new ControllerFactory( client );
    }

    @Override
    public RenderingResult execute( final Page page )
    {
        final PageTemplate template = pageTemplateService.getByKey( page.getTemplate(), null );
        final PageDescriptor descriptor = pageDescriptorService.getByKey( template.getDescriptor() );

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
}
