package com.enonic.xp.elasticsearch.impl;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.cluster.ClusterHealth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class ElasticsearchClusterTest
{
    private BundleContext context;

    private ElasticsearchCluster activator;

    private ServiceRegistration<Client> clientReg;

    private ClusterAdminClient clusterAdminClient;

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.context = Mockito.mock( BundleContext.class );
        final Node node = Mockito.mock( Node.class );
        //this.activator.setClient( node );

        this.clientReg = mockRegisterService( Client.class );

        final Client client = Mockito.mock( Client.class );
        Mockito.when( node.client() ).thenReturn( client );

        final AdminClient adminClient = Mockito.mock( AdminClient.class );
        Mockito.when( client.admin() ).thenReturn( adminClient );

        this.clusterAdminClient = Mockito.mock( ClusterAdminClient.class );
        Mockito.when( adminClient.cluster() ).thenReturn( this.clusterAdminClient );

        final ImmutableOpenMap<String, IndexMetaData> indices = ImmutableOpenMap.<String, IndexMetaData>builder().build();
        final MetaData metaData = Mockito.mock( MetaData.class );
        Mockito.when( metaData.getIndices() ).thenReturn( indices );
        final ClusterState clusterState = Mockito.mock( ClusterState.class );
        Mockito.when( clusterState.getMetaData() ).thenReturn( metaData );
        final ClusterStateResponse clusterStateResponse = Mockito.mock( ClusterStateResponse.class );
        Mockito.when( clusterStateResponse.getState() ).thenReturn( clusterState );
        final PlainActionFuture<ClusterStateResponse> actionClusterStateResponse = new PlainActionFuture<>();
        actionClusterStateResponse.onResponse( clusterStateResponse );
        Mockito.when( clusterAdminClient.state( Mockito.any() ) ).thenReturn( actionClusterStateResponse );
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

        assertFalse( this.activator.isEnabled() );

        this.activator.enable();
        assertTrue( this.activator.isEnabled() );

        this.activator.disable();
        Mockito.verify( this.clientReg, Mockito.times( 1 ) ).unregister();
    }

    @Test
    public void health_red()
        throws Exception
    {
        setClusterHealth( ClusterHealthStatus.RED );
        assertEquals( ClusterHealth.red().getStatus(), this.activator.getHealth().getStatus() );
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