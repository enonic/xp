package com.enonic.wem.boot;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

import com.enonic.wem.core.lifecycle.LifecycleService;
import com.enonic.wem.core.web.WebInitializer;

@Singleton
final class BootStartupManager
{
    @Inject
    protected ServletContext servletContext;

    @Inject
    protected Set<WebInitializer> initializers;

    @Inject
    protected LifecycleService lifecycleService;

    public void start()
    {
        configureWebInitializers();
        this.lifecycleService.startAll();
    }

    public void stop()
    {
        this.lifecycleService.stopAll();
    }

    private void configureWebInitializers()
    {
        for ( final WebInitializer initializer : this.initializers )
        {
            initializer.initialize( this.servletContext );
        }
    }
}
