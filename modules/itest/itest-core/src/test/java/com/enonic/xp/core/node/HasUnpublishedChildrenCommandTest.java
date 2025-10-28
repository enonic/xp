package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.HasUnpublishedChildrenCommand;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HasUnpublishedChildrenCommandTest
    extends AbstractNodeTest
{

    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void resolve_all_unpublished()
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        final Node node1_1 = createNode( node1.path(), "node1_1" );
        final Node node1_1_1 = createNode( node1_1.path(), "node1_1_1" );
        final Node nodeWithoutParent = Node.create( NodeId.from( "node0" ) ).build();

        refresh();

        assertTrue( resolve( node1 ) );
        assertTrue( resolve( node1_1 ) );
        assertFalse( resolve( node1_1_1 ) );
        assertFalse( resolve( nodeWithoutParent ) );
    }

    @Test
    void parent_is_already_published()
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        final Node node1_1 = createNode( node1.path(), "node1_1" );
        refresh();

        pushNodes( WS_OTHER, node1.id() );

        assertTrue( resolve( node1 ) );
        assertFalse( resolve( node1_1 ) );
    }

    private boolean resolve( final Node node1 )
    {
        return HasUnpublishedChildrenCommand.create().
            parent( node1.id() ).
            target( WS_OTHER ).
            storageService( this.storageService ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            build().
            execute();
    }


}
