package com.enonic.xp.core.impl.event.cluster;

import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.collect.UnmodifiableIterator;
import org.elasticsearch.transport.TransportRequest;
import org.elasticsearch.transport.TransportResponseHandler;
import org.elasticsearch.transport.TransportService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.Event2;


public class ClusterEventSenderTest
{
    private ClusterEventSender clusterEventSender;

    private TransportService transportService;

    private DiscoveryNode localNode;

    private DiscoveryNode node1;

    private DiscoveryNode node2;

    @Before
    public void before()
    {
        //Mocks the Elasticsearch nodes
        this.localNode = Mockito.mock( DiscoveryNode.class );
        this.node1 = Mockito.mock( DiscoveryNode.class );
        this.node2 = Mockito.mock( DiscoveryNode.class );
        final DiscoveryNodes discoveryNodes = Mockito.mock( DiscoveryNodes.class );
        final ImmutableList<DiscoveryNode> nodeImmutableList =
            ImmutableList.copyOf( new DiscoveryNode[]{this.localNode, this.node1, this.node2} );
        final UnmodifiableIterator<DiscoveryNode> discoveryNodeUnmodifiableIterator = nodeImmutableList.iterator();
        Mockito.when( discoveryNodes.iterator() ).thenReturn( discoveryNodeUnmodifiableIterator );

        //Mocks Elasticsearch cluster service
        final ClusterState clusterState = Mockito.mock( ClusterState.class );
        final ClusterService clusterService = Mockito.mock( ClusterService.class );
        Mockito.when( clusterService.localNode() ).thenReturn( this.localNode );
        Mockito.when( clusterService.state() ).thenReturn( clusterState );
        Mockito.when( clusterState.nodes() ).thenReturn( discoveryNodes );

        //Mocks Elasticsearch transport service
        this.transportService = Mockito.mock( TransportService.class );

        this.clusterEventSender = new ClusterEventSender();
        this.clusterEventSender.setClusterService( clusterService );
        this.clusterEventSender.setTransportService( this.transportService );

    }

    @Test
    public void onEvent2()
    {
        final Event2 event2 = Event2.create( "aaa" ).distributed( true ).build();
        this.clusterEventSender.onEvent( event2 );

        Mockito.verify( this.transportService, Mockito.times( 0 ) ).
            sendRequest( Mockito.eq( this.localNode ), Mockito.eq( "xp/event" ), Mockito.any( TransportRequest.class ),
                         Mockito.any( TransportResponseHandler.class ) );
        Mockito.verify( this.transportService ).
            sendRequest( Mockito.eq( this.node1 ), Mockito.eq( "xp/event" ), Mockito.any( TransportRequest.class ),
                         Mockito.any( TransportResponseHandler.class ) );
        Mockito.verify( this.transportService ).
            sendRequest( Mockito.eq( this.node2 ), Mockito.eq( "xp/event" ), Mockito.any( TransportRequest.class ),
                         Mockito.any( TransportResponseHandler.class ) );
        Mockito.verify( this.transportService, Mockito.times( 2 ) ).
            sendRequest( Mockito.any( DiscoveryNode.class ), Mockito.anyString(), Mockito.any( TransportRequest.class ),
                         Mockito.any( TransportResponseHandler.class ) );
    }


    @Test
    public void onNonDistributableEvent()
    {
        final Event2 event2 = Event2.create( "aaa" ).build();
        this.clusterEventSender.onEvent( event2 );

        Mockito.verify( this.transportService, Mockito.times( 0 ) ).
            sendRequest( Mockito.any( DiscoveryNode.class ), Mockito.anyString(), Mockito.any( TransportRequest.class ),
                         Mockito.any( TransportResponseHandler.class ) );
    }


    @Test
    public void onNonEvent2()
    {
        final Event event = Mockito.mock( Event.class );
        this.clusterEventSender.onEvent( event );

        Mockito.verify( this.transportService, Mockito.times( 0 ) ).
            sendRequest( Mockito.any( DiscoveryNode.class ), Mockito.anyString(), Mockito.any( TransportRequest.class ),
                         Mockito.any( TransportResponseHandler.class ) );
    }
}
