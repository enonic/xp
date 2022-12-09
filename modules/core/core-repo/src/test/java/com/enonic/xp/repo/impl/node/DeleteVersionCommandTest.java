package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDeleteException;
import com.enonic.xp.node.UpdateNodeParams;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteVersionCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    void not_allowed_to_delete_version_of_node_in_use()
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );

        assertThrows( NodeVersionDeleteException.class, () -> doDeleteVersion( node1 ) );
    }

    @Test
    void delete()
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        updateNode( UpdateNodeParams.create().id( node1.id() ).editor( n -> node1.data().setString( "myValue", "1" ) ).build() );

        doDeleteNode( node1.id() );

        doDeleteVersion( node1 );
    }

    private void doDeleteVersion( final Node node )
    {
        DeleteVersionCommand.create()
            .nodeId( node.id() )
            .nodeVersionId( node.getNodeVersionId() )
            .indexServiceInternal( indexServiceInternal )
            .storageService( storageService )
            .searchService( searchService )
            .repositoryService( repositoryService )
            .build()
            .execute();
    }
}
