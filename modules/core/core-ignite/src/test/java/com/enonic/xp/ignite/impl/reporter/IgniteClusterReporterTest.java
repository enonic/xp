package com.enonic.xp.ignite.impl.reporter;

import java.util.UUID;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.cluster.ClusterNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

import com.enonic.xp.ignite.impl.IgniteAdminClientImpl;
import com.enonic.xp.support.JsonTestHelper;

public class IgniteClusterReporterTest
{
    private Ignite ignite;

    private IgniteCluster igniteCluster;

    @Before
    public void setUp()
        throws Exception
    {
        this.ignite = Mockito.mock( Ignite.class );
        this.igniteCluster = Mockito.mock( IgniteCluster.class );
        Mockito.when( this.ignite.cluster() ).thenReturn( this.igniteCluster );
    }

    @Test
    public void report()
        throws Exception
    {
        final ClusterNode node = Mockito.mock( ClusterNode.class );
        final ClusterNode node2 = Mockito.mock( ClusterNode.class );

        Mockito.when( this.igniteCluster.localNode() ).
            thenReturn( node );

        Mockito.when( this.igniteCluster.nodes() ).
            thenReturn( Lists.newArrayList( node, node2 ) );

        mockNode( node, "11111111-1111-1111-1111-11111111" );
        mockNode( node2, "22222222-2222-2222-2222-22222222" );

        final IgniteClusterReporter reporter = new IgniteClusterReporter();
        reporter.setAdminClient( new IgniteAdminClientImpl( this.ignite ) );

        final JsonNode result = reporter.getReport();

        assertJson( "report.json", result );
    }

    private void mockNode( final ClusterNode node, final String id )
    {
        Mockito.when( node.addresses() ).thenReturn( Lists.newArrayList( "addr1", "addr2" ) );
        Mockito.when( node.hostNames() ).thenReturn( Lists.newArrayList( "host1", "host2" ) );
        Mockito.when( node.id() ).thenReturn( UUID.fromString( id ) );
        Mockito.when( node.consistentId() ).thenReturn( "node" + id.charAt( 0 ) );
    }

    private void assertJson( final String fileName, final JsonNode json )
    {
        final JsonTestHelper jsonTestHelper = new JsonTestHelper( this );
        final JsonNode jsonFromFile = jsonTestHelper.loadTestJson( fileName );
        jsonTestHelper.assertJsonEquals( jsonFromFile, json );
    }
}