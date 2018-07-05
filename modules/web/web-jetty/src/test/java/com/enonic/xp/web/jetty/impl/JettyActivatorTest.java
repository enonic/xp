package com.enonic.xp.web.jetty.impl;

import java.util.Hashtable;

import org.eclipse.jetty.server.session.SessionDataStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.web.dispatch.DispatchServlet;

import static org.junit.Assert.*;

public class JettyActivatorTest
{
    private BundleContext bundleContext;

    private JettyActivator activator;

    private JettyConfig config;

    private ClusterConfig clusterConfig;

    private SessionDataStore sessionDataStore;

    @Before
    public void setup()
        throws Exception
    {
        System.getProperties().remove( "jetty.version" );

        final Bundle bundle = Mockito.mock( Bundle.class );
        final Hashtable<String, String> headers = new Hashtable<>();
        headers.put( "X-Jetty-Version", "9.x" );
        Mockito.when( bundle.getHeaders() ).thenReturn( headers );

        this.bundleContext = Mockito.mock( BundleContext.class, (Answer) this::defaultAnswer );
        Mockito.when( this.bundleContext.createFilter( Mockito.anyString() ) ).thenReturn( Mockito.mock( Filter.class ) );
        Mockito.when( this.bundleContext.getBundle() ).thenReturn( bundle );

        this.activator = new JettyActivator();
        this.activator.setDispatchServlet( Mockito.mock( DispatchServlet.class ) );

        this.sessionDataStore = Mockito.mock( SessionDataStore.class );
        this.activator.setSessionDataStore( this.sessionDataStore );

        this.clusterConfig = Mockito.mock( ClusterConfig.class );
        Mockito.when( this.clusterConfig.name() ).thenReturn( ClusterNodeId.from( "localNodeName" ) );
        this.activator.setClusterConfig( this.clusterConfig );

        this.config = new JettyConfigMockFactory().newConfig();
        Mockito.when( this.config.http_port() ).thenReturn( 0 );
    }

    @Test
    public void testLifecycle()
        throws Exception
    {
        this.activator.activate( this.bundleContext, this.config );

        assertEquals( "9.x", System.getProperty( "jetty.version" ) );
        assertNotNull( this.activator.service );
        assertTrue( this.activator.service.server.isRunning() );

        this.activator.deactivate();
    }

    @Test
    public void testSessionReplicationEnabled()
        throws Exception
    {
        Mockito.when( this.clusterConfig.isSessionReplicationEnabled() ).thenReturn( true );
        Mockito.when( this.clusterConfig.isEnabled() ).thenReturn( true );

        this.activator.activate( this.bundleContext, this.config );

        assertEquals( this.sessionDataStore, this.activator.service.sessionDataStore );
    }

    @Test
    public void testSessionReplicationDisabled()
        throws Exception
    {
        Mockito.when( this.clusterConfig.isSessionReplicationEnabled() ).thenReturn( false );
        Mockito.when( this.clusterConfig.isEnabled() ).thenReturn( true );

        this.activator.activate( this.bundleContext, this.config );

        assertNull( this.activator.service.sessionDataStore );
    }

    @Test
    public void testClusterDisabled()
        throws Exception
    {
        Mockito.when( this.clusterConfig.isEnabled() ).thenReturn( false );

        this.activator.activate( this.bundleContext, this.config );

        assertNull( this.activator.service.sessionDataStore );
    }

    private Object defaultAnswer( final InvocationOnMock invocation )
    {
        if ( invocation.getMethod().getName().equals( "registerService" ) )
        {
            return newServiceRegistration();
        }

        return null;
    }

    private ServiceRegistration<?> newServiceRegistration()
    {
        final ServiceReference ref = newServiceReference();
        final ServiceRegistration reg = Mockito.mock( ServiceRegistration.class );
        Mockito.when( reg.getReference() ).thenReturn( ref );
        return reg;
    }

    private ServiceReference newServiceReference()
    {
        final ServiceReference ref = Mockito.mock( ServiceReference.class );
        Mockito.when( ref.getProperty( "service.id" ) ).thenReturn( 1L );
        return ref;
    }
}
