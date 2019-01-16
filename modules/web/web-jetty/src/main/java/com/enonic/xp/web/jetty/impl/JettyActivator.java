package com.enonic.xp.web.jetty.impl;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.eclipse.jetty.server.session.SessionDataStore;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.dispatch.DispatchServlet;
import com.enonic.xp.web.thread.ThreadPoolInfo;

@Component(immediate = true, service = JettyController.class, configurationPid = "com.enonic.xp.web.jetty")
public final class JettyActivator
    implements JettyController
{
    private BundleContext context;

    protected JettyService service;

    private JettyConfig config;

    private ServiceRegistration controllerReg;

    private ServiceRegistration statusReporterReg;

    private List<DispatchServlet> dispatchServlets;

    private ClusterConfig clusterConfig;

    private SessionDataStore sessionDataStore;

    public JettyActivator()
    {
        this.dispatchServlets = Lists.newArrayList();
    }

    @Activate
    public void activate( final BundleContext context, final JettyConfig config )
        throws Exception
    {
        this.context = context;
        fixJettyVersion();

        this.config = config;
        this.service = new JettyService();
        this.service.config = this.config;
        this.service.workerName = clusterConfig.name().toString();
        if ( clusterConfig.isEnabled() && clusterConfig.isSessionReplicationEnabled() )
        {
            this.service.sessionDataStore = sessionDataStore;
        }

        this.service.dispatcherServlets = this.dispatchServlets;
        this.service.start();

        publishController();
        publishStatusReporter();
        publishThreadPoolInfo();
    }

    @Deactivate
    public void deactivate()
        throws Exception
    {
        this.controllerReg.unregister();
        this.statusReporterReg.unregister();
        this.service.stop();
    }

    private void fixJettyVersion()
    {
        final Dictionary<String, String> headers = this.context.getBundle().getHeaders();
        final String version = headers.get( "X-Jetty-Version" );

        if ( version != null )
        {
            System.setProperty( "jetty.version", version );
        }
    }

    @Override
    public List<ServletContext> getServletContexts()
    {
        return Arrays.stream( this.service.contexts.getHandlers() ).map(
            handler -> ( (ServletContextHandler) handler ).getServletHandler().getServletContext() ).collect( Collectors.toList() );
    }

    private void publishController()
    {
        final Hashtable<String, Object> map = new Hashtable<>();
        map.put( "http.enabled", this.config.http_enabled() );
        map.put( "http.port", this.config.http_xp_port() );

        this.controllerReg = this.context.registerService( JettyController.class, this, map );
    }

    private void publishStatusReporter()
    {
        final HttpThreadPoolStatusReporter statusReporter = new HttpThreadPoolStatusReporter( this.service.server.getThreadPool() );
        this.statusReporterReg = this.context.registerService( StatusReporter.class, statusReporter, new Hashtable<>() );
    }

    private void publishThreadPoolInfo()
    {
        final ThreadPoolInfoImpl threadPoolInfo = new ThreadPoolInfoImpl( this.service.server.getThreadPool() );
        this.statusReporterReg = this.context.registerService( ThreadPoolInfo.class, threadPoolInfo, new Hashtable<>() );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addDispatchServlet( final DispatchServlet dispatchServlet )
    {
        this.dispatchServlets.add( dispatchServlet );
    }

    public void removeDispatchServlet( final DispatchServlet dispatchServlet )
    {
        this.dispatchServlets.remove( dispatchServlet );
    }

    @Reference
    public void setClusterConfig( final ClusterConfig clusterConfig )
    {
        this.clusterConfig = clusterConfig;
    }

    @Reference
    public void setSessionDataStore( final SessionDataStore sessionDataStore )
    {
        this.sessionDataStore = sessionDataStore;
    }
}
