package com.enonic.wem.boot;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import com.enonic.wem.admin.ResourceServlet;
import com.enonic.wem.admin.app.AppServlet;
import com.enonic.wem.admin.rest.RestServlet;
import com.enonic.wem.core.home.HomeDir;
import com.enonic.wem.core.lifecycle.LifecycleService;
import com.enonic.wem.core.web.servlet.RequestContextListener;
import com.enonic.wem.portal.PortalServlet;

public final class BootActivator
    implements BundleActivator
{
    private final static Logger LOG = LoggerFactory.getLogger( BootActivator.class );

    @Inject
    protected LifecycleService lifecycleService;

    @Inject
    protected RestServlet restServlet;

    @Inject
    protected ResourceServlet resourceServlet;

    @Inject
    protected AppServlet appServlet;

    @Inject
    protected PortalServlet portalServlet;

    private BundleContext bundleContext;

    private List<ServiceRegistration> serviceRegistrations;

    @Override
    public void start( final BundleContext context )
        throws Exception
    {
        this.bundleContext = context;
        this.serviceRegistrations = new ArrayList<>();

        final File homeDir = new File( this.bundleContext.getProperty( "karaf.home" ), "cms.home" );
        new HomeDir( homeDir );

        createInjector();
        this.lifecycleService.startAll();

        registerRequestListener();
        // registerPortalServlet();
        registerResourceServlet();
        registerRestServlet();
    }

    @Override
    public void stop( final BundleContext context )
        throws Exception
    {
        this.serviceRegistrations.forEach( this::unregister );
        this.lifecycleService.stopAll();
    }

    private void unregister( final ServiceRegistration reg )
    {
        reg.unregister();
    }

    private void createInjector()
    {
        LOG.info( "Creating injector for all beans." );

        final Injector injector = Guice.createInjector( Stage.PRODUCTION, new BootModule() );
        injector.injectMembers( this );
    }

    private <T> void register( final Class<T> type, final T service, final Hashtable<String, Object> props )
    {
        final ServiceRegistration reg = this.bundleContext.registerService( type, service, props );
        this.serviceRegistrations.add( reg );
    }

    private void registerRequestListener()
    {
        final Hashtable<String, Object> props = new Hashtable<>();
        register( ServletRequestListener.class, new RequestContextListener(), props );
    }

    private void registerPortalServlet()
    {
        final Hashtable<String, Object> props = new Hashtable<>();
        props.put( "alias", "/portal" );
        register( HttpServlet.class, this.portalServlet, props );
    }

    private void registerResourceServlet()
    {
        final Hashtable<String, Object> props = new Hashtable<>();
        props.put( "alias", "/" );
        register( HttpServlet.class, this.resourceServlet, props );
    }

    private void registerRestServlet()
    {
        final Hashtable<String, Object> props = new Hashtable<>();
        props.put( "alias", "/admin/rest" );
        register( HttpServlet.class, this.restServlet, props );
    }
}
