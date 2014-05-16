package com.enonic.wem.portal;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.PageComponentService;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.guice.GuiceActivator;

public final class Activator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        install( new PortalModule() );

        importService( PageComponentService.class ).toSingle();
        importService( ModuleResourcePathResolver.class ).toSingle();
        importService( ImageDescriptorService.class ).toSingle();
        importService( LayoutDescriptorService.class ).toSingle();
        importService( PartDescriptorService.class ).toSingle();
        importService( ContentService.class ).toSingle();
    }
}
