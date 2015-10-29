package com.enonic.xp.repo.impl.node.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.event.Event2;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.storage.StorageService;

public class NodeEventListenerTest
{
    private NodeEventListener nodeEventListener;

    private StorageService storageService;

    @Before
    public void setUp()
        throws Exception
    {
        nodeEventListener = new NodeEventListener();
        storageService = Mockito.mock( StorageService.class );
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalid_event_no_list()
        throws Exception
    {
        nodeEventListener.setStorageService( storageService );

        nodeEventListener.onEvent( Event2.create( NodeEvents.NODE_CREATED_EVENT ).
            value( "fisk", "ost" ).
            build() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalid_event_nodes_not_list()
        throws Exception
    {
        nodeEventListener.setStorageService( storageService );

        nodeEventListener.onEvent( Event2.create( NodeEvents.NODE_CREATED_EVENT ).
            value( "nodes", "ost" ).
            build() );
    }

    @Test
    public void node_create_event()
        throws Exception
    {
        nodeEventListener.setStorageService( storageService );

        nodeEventListener.onEvent( NodeEvents.created( Node.create().
            id( NodeId.from( "node1" ) ).
            parentPath( NodePath.ROOT ).
            name( "nodeName" ).
            build() ) );

        Mockito.verify( storageService, Mockito.times( 1 ) );
    }
}