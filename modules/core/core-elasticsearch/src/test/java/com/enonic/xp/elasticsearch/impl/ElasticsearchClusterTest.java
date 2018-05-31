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

import com.enonic.xp.cluster.ClusterHealth;

import static org.junit.Assert.*;

public class ElasticsearchClusterTest
{
    private BundleContext context;

    private ElasticsearchCluster activator;

    private ServiceRegistration<Client> clientReg;

    private ClusterAdminClient clusterAdminClient;

    @Before
    public void setup()
        throws Exception
    {
        this.context = Mockito.mock( BundleContext.class );
        this.activator = new ElasticsearchCluster();

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
    public void test_enable()
        throws Exception
    {
        setClusterHealth( ClusterHealthStatus.GREEN );

        this.activator.activate( this.context );
        Assert.assertNull( this.activator.reg );

        this.activator.enable();
        Assert.assertNotNull( this.activator.reg );
        Assert.assertSame( this.clientReg, this.activator.reg );

        this.activator.disable();
        Mockito.verify( this.clientReg, Mockito.times( 1 ) ).unregister();
    }

    @Test
    public void health_red()
        throws Exception
    {
        setClusterHealth( ClusterHealthStatus.RED );
        assertEquals( ClusterHealth.red(), this.activator.getHealth() );
    }

    @Test
    public void health_green()
        throws Exception
    {
        setClusterHealth( ClusterHealthStatus.YELLOW );
        assertEquals( ClusterHealth.yellow(), this.activator.getHealth() );
    }

    @Test
    public void health_yellow()
        throws Exception
    {
        setClusterHealth( ClusterHealthStatus.GREEN );
        assertEquals( ClusterHealth.green(), this.activator.getHealth() );
    }

    @SuppressWarnings("unchecked")
    private <T> ServiceRegistration<T> mockRegisterService( final Class<T> type )
    {
        final ServiceRegistration<T> reg = Mockito.mock( ServiceRegistration.class );
        Mockito.when( this.context.registerService( Mockito.eq( type ), Mockito.any( type ), Mockito.any() ) ).thenReturn( reg );
        return reg;
    }
}
