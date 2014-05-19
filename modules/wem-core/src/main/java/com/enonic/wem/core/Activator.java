package com.enonic.wem.core;

import java.io.File;

import javax.inject.Inject;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.PageComponentService;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.core.config.ConfigProperties;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.home.HomeDir;
import com.enonic.wem.core.lifecycle.LifecycleService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.guice.GuiceActivator;

public final class Activator
    extends GuiceActivator
{
    @Inject
    protected LifecycleService lifecycleService;

    @Override
    protected void configure()
    {
        final String karafHome = System.getProperty( "karaf.home" );
        new HomeDir( new File( karafHome, "wem.home" ) );

        // Install core module
        install( new CoreModule() );

        // Export needed services
        service( ModuleResourcePathResolver.class ).export();
        service( PageComponentService.class ).export();
        service( ImageDescriptorService.class ).export();
        service( LayoutDescriptorService.class ).export();
        service( PartDescriptorService.class ).export();
        service( ContentService.class ).export();
        service( ConfigProperties.class ).export();
        service( SystemConfig.class ).export();
    }

    @Override
    protected void doStart()
        throws Exception
    {
        this.lifecycleService.startAll();
    }

    @Override
    protected void doStop()
        throws Exception
    {
        this.lifecycleService.stopAll();
    }
}
