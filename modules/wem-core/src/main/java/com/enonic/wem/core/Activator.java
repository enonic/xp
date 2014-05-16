package com.enonic.wem.core;

import java.io.File;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.PageComponentService;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.core.home.HomeDir;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.guice.GuiceActivator;

public final class Activator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        final String karafHome = System.getProperty( "karaf.home" );
        new HomeDir( new File( karafHome, "wem.home" ) );

        // Install core module
        install( new CoreModule() );

        // Export needed services
        exportService( ModuleResourcePathResolver.class ).to( ModuleResourcePathResolver.class );
        exportService( PageComponentService.class ).to( PageComponentService.class );
        exportService( ImageDescriptorService.class ).to( ImageDescriptorService.class );
        exportService( LayoutDescriptorService.class ).to( LayoutDescriptorService.class );
        exportService( PartDescriptorService.class ).to( PartDescriptorService.class );
        exportService( ContentService.class ).to( ContentService.class );
    }
}
