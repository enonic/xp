package com.enonic.wem.web.boot;

import javax.servlet.ServletContextEvent;
import org.springframework.web.context.ContextLoaderListener;

public final class BootContextListener
    extends ContextLoaderListener
{
    private BootEnvironment env;

    @Override
    public void contextInitialized( final ServletContextEvent event )
    {
        this.env = new BootEnvironment();
        this.env.initialize();
        super.contextInitialized( event );
    }

    @Override
    public void contextDestroyed( final ServletContextEvent event )
    {
        super.contextDestroyed( event );
        this.env.destroy();
    }
}
