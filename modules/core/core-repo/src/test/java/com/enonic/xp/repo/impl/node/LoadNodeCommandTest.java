package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import org.junit.Test;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.node.LoadNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeLoadException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeType;

import static org.junit.Assert.*;

public class LoadNodeCommandTest
    extends AbstractNodeTest
{
    @Test
    public void load_node()
        throws Exception
    {
        final Node node = Node.create().
            id( NodeId.from( "myNodeId" ) ).
            parentPath( NodePath.ROOT ).
            name( "fisk" ).
            nodeState( NodeState.DEFAULT ).
            childOrder( ChildOrder.manualOrder() ).
            nodeType( NodeType.from( "myNodes" ) ).
            timestamp( Instant.now() ).
            build();

        final LoadNodeParams loadParams = LoadNodeParams.create().
            node( node ).
            build();

        doLoadNode( loadParams );

        final Node storedNode = NodeHelper.runAsAdmin( () -> getNode( node.id() ) );

        assertNodeProperties( node, storedNode );
    }

    private void assertNodeProperties( final Node node, final Node storedNode )
    {
        assertEquals( node.path(), storedNode.path() );
        assertEquals( node.id(), storedNode.id() );
        assertEquals( node.getTimestamp(), storedNode.getTimestamp() );
        assertEquals( node.getIndexConfigDocument(), storedNode.getIndexConfigDocument() );
        assertEquals( node.getPermissions(), storedNode.getPermissions() );
        assertEquals( node.getManualOrderValue(), storedNode.getManualOrderValue() );
    }

    @Test
    public void node_already_exists_in_path()
        throws Exception
    {
        final Node originalNode = createNode( NodePath.ROOT, "fisk" );

        final Node nodeToLoad = Node.create( originalNode ).
            id( new NodeId() ).
            parentPath( NodePath.ROOT ).
            name( "fisk" ).
            timestamp( Instant.now() ).
            build();

        final LoadNodeParams loadParams = LoadNodeParams.create().
            node( nodeToLoad ).
            build();

        refresh();

        doLoadNode( loadParams );

        refresh();

        final Node storedNode = getNodeByPath( nodeToLoad.path() );
        assertNotNull( storedNode );
        assertNotEquals( originalNode.id(), storedNode.id() );
    }

    @Test(expected = NodeLoadException.class)
    public void parent_does_not_exist()
        throws Exception
    {
        final LoadNodeParams loadParams = LoadNodeParams.create().
            node( Node.create().
                id( new NodeId() ).
                parentPath( NodePath.create( "ost" ).build() ).
                name( "fisk" ).
                timestamp( Instant.now() ).
                build() ).
            build();

        doLoadNode( loadParams );
    }

    private LoadNodeResult doLoadNode( final LoadNodeParams loadParams )
    {
        return LoadNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            params( loadParams ).
            build().
            execute();
    }
}