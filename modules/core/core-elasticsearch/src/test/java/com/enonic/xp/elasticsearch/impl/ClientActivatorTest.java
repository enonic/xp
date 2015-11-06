package com.enonic.xp.elasticsearch.impl;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.node.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ClientActivatorTest
{
    private BundleContext context;

    private ClientActivator activator;

    private ServiceRegistration<Client> clientReg;

    private ClusterAdminClient clusterAdminClient;

    @Before
    public void setup()
        throws Exception
    {
        this.context = Mockito.mock( BundleContext.class );
        this.activator = new ClientActivator();

        this.clientReg = mockRegisterService( Client.class );

        final Node node = Mockito.mock( Node.class );
        this.activator.setNode( node );

        final Client client = Mockito.mock( Client.class );
        Mockito.when( node.client() ).thenReturn( client );

        final AdminClient adminClient = Mockito.mock( AdminClient.class );
        Mockito.when( client.admin() ).thenReturn( adminClient );

        this.clusterAdminClient = Mockito.mock( ClusterAdminClient.class );
        Mockito.when( adminClient.cluster() ).thenReturn( this.clusterAdminClient );
    }

    private void setClusterHealth( final ClusterHealthStatus status )
    {
        final ClusterHealthResponse response = Mockito.mock( ClusterHealthResponse.class );
        Mockito.when( response.getStatus() ).thenReturn( status );

        final PlainActionFuture<ClusterHealthResponse> action = new PlainActionFuture<>();
        action.onResponse( response );

        Mockito.when( this.clusterAdminClient.health( Mockito.any() ) ).thenReturn( action );
    }

    @Test
    public void testLifeCycle_green()
        throws Exception
    {
        setClusterHealth( ClusterHealthStatus.GREEN );

        this.activator.activate( this.context );
        Assert.assertNotNull( this.activator.reg );
        Assert.assertSame( this.clientReg, this.activator.reg );

        this.activator.deactivate();
        Mockito.verify( this.clientReg, Mockito.times( 1 ) ).unregister();
    }

    @Test
    public void testLifeCycle_red()
        throws Exception
    {
        setClusterHealth( ClusterHealthStatus.RED );

        this.activator.activate( this.context );
        Assert.assertNull( this.activator.reg );

        this.activator.deactivate();
    }

    @Test
    public void testLifeCycle_red_then_green()
        throws Exception
    {
        setClusterHealth( ClusterHealthStatus.RED );

        this.activator.activate( this.context );
        Assert.assertNull( this.activator.reg );

        setClusterHealth( ClusterHealthStatus.GREEN );
        Thread.sleep( 1200L );

        Assert.assertNotNull( this.activator.reg );
        Assert.assertSame( this.clientReg, this.activator.reg );

        this.activator.deactivate();
        Mockito.verify( this.clientReg, Mockito.times( 1 ) ).unregister();
    }

    @SuppressWarnings("unchecked")
    private <T> ServiceRegistration<T> mockRegisterService( final Class<T> type )
    {
        final ServiceRegistration<T> reg = Mockito.mock( ServiceRegistration.class );
        Mockito.when( this.context.registerService( Mockito.eq( type ), Mockito.any( type ), Mockito.any() ) ).thenReturn( reg );
        return reg;
    }
}
