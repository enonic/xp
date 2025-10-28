package com.enonic.xp.core.node;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.node.LoadNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeLoadException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.node.LoadNodeCommand;
import com.enonic.xp.repo.impl.node.NodeHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoadNodeCommandTest
    extends AbstractNodeTest
{
    @Test
    void load_node()
    {
        final Node node = Node.create().
            id( NodeId.from( "myNodeId" ) ).
            parentPath( NodePath.ROOT ).
            name( "fisk" ).
            childOrder( ChildOrder.manualOrder() ).
            nodeType( NodeType.from( "myNodes" ) ).
            timestamp( Instant.now() ).
            nodeVersionId( NodeVersionId.from( "nodeVersionId" ) ).
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
    void node_already_exists_in_path()
    {
        final Node originalNode = createNode( NodePath.ROOT, "fisk" );
        nodeService.refresh( RefreshMode.ALL );

        final Node nodeToLoad = Node.create( originalNode ).
            id( new NodeId() ).
            parentPath( NodePath.ROOT ).
            name( "fisk" ).
            timestamp( Instant.now() ).
            nodeVersionId( NodeVersionId.from( "nodeVersionId" ) ).
            build();

        final LoadNodeParams loadParams = LoadNodeParams.create().
            node( nodeToLoad ).
            build();

        doLoadNode( loadParams );

        refresh();

        final Node storedNode = getNodeByPath( nodeToLoad.path() );
        assertNotNull( storedNode );
        assertNotEquals( originalNode.id(), storedNode.id() );
    }

    @Test
    void parent_does_not_exist()
    {
        final LoadNodeParams loadParams = LoadNodeParams.create().
            node( Node.create().
                id( new NodeId() ).
                parentPath( new NodePath( "/ost" ) ).
                name( "fisk" ).
                timestamp( Instant.now() ).
                nodeVersionId( NodeVersionId.from( "nodeVersionId" ) ).
                build() ).
            build();

        assertThrows(NodeLoadException.class, () -> doLoadNode( loadParams ));
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
