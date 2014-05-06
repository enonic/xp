package com.enonic.wem.boot;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

import com.enonic.wem.core.lifecycle.LifecycleService;

@Singleton
final class BootStartupManager
{
    @Inject
    protected ServletContext servletContext;

    @Inject
    protected WebInitializer initializer;

    @Inject
    protected LifecycleService lifecycleService;

    public void start()
    {
        this.initializer.initialize( this.servletContext );
        this.lifecycleService.startAll();
    }

    public void stop()
    {
        this.lifecycleService.stopAll();
    }
}
