package com.enonic.xp.web.jetty.impl;

import java.util.Collections;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.VirtualThreadPool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.DispatchServlet;
import com.enonic.xp.web.jetty.impl.session.JettySessionStoreConfigurator;
import com.enonic.xp.web.jetty.impl.websocket.WebSocketSessionTracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JettyActivatorTest
{
    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceRegistration<Server> serverServiceRegistration;

    private JettyConfig config;

    @Test
    void testLifecycle()
        throws Exception
    {
        when( bundleContext.registerService( eq( Server.class ), any( Server.class ), any() ) ).
            thenReturn( serverServiceRegistration );

        this.config = mock( JettyConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( this.config.http_web_port() ).thenReturn( 0 );
        when( this.config.http_statistics_port() ).thenReturn( 0 );
        when( this.config.http_management_port() ).thenReturn( 0 );

        final JettySessionStoreConfigurator jettySessionStoreConfigurator = Mockito.mock( JettySessionStoreConfigurator.class );
        final DispatchServlet xpDispatcherServlet = mock( DispatchServlet.class );
        when( xpDispatcherServlet.getConnector() ).thenReturn( DispatchConstants.WEB_CONNECTOR );
        JettyActivator activator = new JettyActivator( config, bundleContext, jettySessionStoreConfigurator, new WebSocketSessionTracker(),
                                                       Collections.singletonList( xpDispatcherServlet ) );

        activator.activate();

        Mockito.verify( jettySessionStoreConfigurator ).configure( any( Server.class ), eq( 360 ) );

        activator.deactivate();
    }

    @Test
    void testLifecycle_withVirtualThreads()
        throws Exception
    {
        final ArgumentCaptor<Server> serverCaptor = ArgumentCaptor.forClass( Server.class );
        when( bundleContext.registerService( eq( Server.class ), serverCaptor.capture(), any() ) ).
            thenReturn( serverServiceRegistration );

        this.config = mock( JettyConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( this.config.http_web_port() ).thenReturn( 0 );
        when( this.config.http_statistics_port() ).thenReturn( 0 );
        when( this.config.http_management_port() ).thenReturn( 0 );
        when( this.config.threadPool_virtualThreads() ).thenReturn( true );
        when( this.config.threadPool_virtualThreads_maxConcurrent() ).thenReturn( 16 );

        final JettySessionStoreConfigurator jettySessionStoreConfigurator = Mockito.mock( JettySessionStoreConfigurator.class );
        final DispatchServlet xpDispatcherServlet = mock( DispatchServlet.class );
        when( xpDispatcherServlet.getConnector() ).thenReturn( DispatchConstants.WEB_CONNECTOR );
        final JettyActivator activator =
            new JettyActivator( config, bundleContext, jettySessionStoreConfigurator, new WebSocketSessionTracker(),
                                Collections.singletonList( xpDispatcherServlet ) );

        activator.activate();

        // the VirtualThreadPool is the QueuedThreadPool's virtual-threads executor and a managed bean,
        // so it starts with the server and carries the configured concurrency bound
        final QueuedThreadPool threadPool = (QueuedThreadPool) serverCaptor.getValue().getThreadPool();
        final VirtualThreadPool virtualThreadPool = threadPool.getBean( VirtualThreadPool.class );
        assertNotNull( virtualThreadPool );
        assertEquals( virtualThreadPool, threadPool.getVirtualThreadsExecutor() );
        assertEquals( 16, virtualThreadPool.getMaxConcurrentTasks() );
        assertTrue( virtualThreadPool.isRunning() );

        activator.deactivate();

        // stopping the server stops the managed VirtualThreadPool bean
        assertFalse( virtualThreadPool.isRunning() );
    }

    @Test
    void sessionScavengeInterval_derived_from_session_timeout()
    {
        assertEquals( 360, JettyActivator.sessionScavengeIntervalSeconds( 60 ) ); // default 60 min timeout -> 6 min sweep
        assertEquals( 10, JettyActivator.sessionScavengeIntervalSeconds( 1 ) ); // floor: 10 s
        assertEquals( 600, JettyActivator.sessionScavengeIntervalSeconds( 1440 ) ); // cap: Jetty's default 10 min
        assertEquals( 600, JettyActivator.sessionScavengeIntervalSeconds( 0 ) ); // sessions never expire -> Jetty's default
    }
}
