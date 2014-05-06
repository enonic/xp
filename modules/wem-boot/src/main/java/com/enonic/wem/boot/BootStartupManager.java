package com.enonic.wem.boot;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

import com.enonic.wem.core.lifecycle.LifecycleService;

@Singleton
final class BootStartupManager
{
    @Inject
    protected WebInitializer initializer;

    @Inject
    protected LifecycleService lifecycleService;

    public void start( final ServletContext servletContext )
    {
        this.initializer.initialize( servletContext );
        this.lifecycleService.startAll();
    }

    public void stop()
    {
        this.lifecycleService.stopAll();
    }
}
