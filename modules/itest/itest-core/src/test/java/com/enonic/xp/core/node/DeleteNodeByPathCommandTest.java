package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.DeleteNodeCommand;

import static org.junit.jupiter.api.Assertions.assertNull;

class DeleteNodeByPathCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void delete_by_path()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            name( "child1" ).
            parent( node.path() ).
            build() );

        final Node child2 = createNode( CreateNodeParams.create().
            name( "child2" ).
            parent( node.path() ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            name( "child1_1" ).
            parent( child1.path() ).
            build() );

        final Node child1_1_1 = createNode( CreateNodeParams.create().
            name( "child1_1_1" ).
            parent( child1_1.path() ).
            build() );

        DeleteNodeCommand.create().
            nodePath( node.path() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertNull( getNodeById( node.id() ) );
        assertNull( getNodeById( child1.id() ) );
        assertNull( getNodeById( child2.id() ) );
        assertNull( getNodeById( child1_1.id() ) );
        assertNull( getNodeById( child1_1_1.id() ) );
    }
}
