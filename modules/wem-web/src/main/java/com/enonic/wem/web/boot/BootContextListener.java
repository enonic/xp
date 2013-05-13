package com.enonic.wem.web.boot;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRegistration;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.enonic.wem.web.ResourceServlet;
import com.enonic.wem.web.rest.JaxRsServlet;

public final class BootContextListener
    extends ContextLoaderListener
{
    private BootEnvironment env;

    @Override
    public void contextInitialized( final ServletContextEvent event )
    {
        configure( event.getServletContext() );

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

    private void configure( final ServletContext context )
    {
        context.addListener( new RequestContextListener() );

        final ServletRegistration.Dynamic restServlet = context.addServlet( "rest", new JaxRsServlet() );
        restServlet.setLoadOnStartup( 1 );
        restServlet.addMapping( "/admin/rest/*" );
        restServlet.addMapping( "/dev/rest/*" );

        final ServletRegistration.Dynamic resourceServlet = context.addServlet( "resource", new ResourceServlet() );
        restServlet.setLoadOnStartup( 2 );
        resourceServlet.addMapping( "/" );
    }

    @Override
    protected WebApplicationContext createWebApplicationContext( final ServletContext sc )
    {
        final XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setServletContext( sc );
        context.setConfigLocation( "classpath:/META-INF/spring/web-context.xml" );
        return context;
    }
}
