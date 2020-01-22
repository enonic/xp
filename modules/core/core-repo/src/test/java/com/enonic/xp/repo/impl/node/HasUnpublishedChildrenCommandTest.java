    package com.enonic.xp.repo.impl.node;

    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;

    import com.enonic.xp.node.Node;
    import com.enonic.xp.node.NodeId;
    import com.enonic.xp.node.NodePath;
    import com.enonic.xp.node.NodeState;
    import com.enonic.xp.node.SetNodeStateParams;

    import static org.junit.jupiter.api.Assertions.assertFalse;
    import static org.junit.jupiter.api.Assertions.assertTrue;

public class HasUnpublishedChildrenCommandTest
    extends AbstractNodeTest
{

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void resolve_all_unpublished()
        throws Exception
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
    public void parent_is_already_published()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        final Node node1_1 = createNode( node1.path(), "node1_1" );

        pushNodes( CTX_OTHER.getBranch(), node1.id() );

        refresh();

        assertTrue( resolve( node1 ) );
        assertFalse( resolve( node1_1 ) );
    }

    @Test
    public void child_status_is_pending_delete()
    {

        final Node node1 = createNode( NodePath.ROOT, "node1" );
        final Node node1_1 = createNode( node1.path(), "node1_1" );

        pushNodes( CTX_OTHER.getBranch(), node1.id(), node1_1.id() );

        this.nodeService.setNodeState( SetNodeStateParams.create().
            nodeId( node1_1.id() ).
            nodeState( NodeState.PENDING_DELETE ).
            build() );

        refresh();
        assertTrue( resolve( node1 ) );
    }

    @Test
    public void child_is_moved()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        final Node node1_1 = createNode( node1.path(), "node1_1" );

        final Node node2 = createNode( NodePath.ROOT, "node2" );

        pushNodes( CTX_OTHER.getBranch(), node1.id(), node1_1.id(), node2.id() );

        refresh();

        assertFalse( resolve( node1 ) );
        assertFalse( resolve( node1_1 ) );
        assertFalse( resolve( node2 ) );

        this.nodeService.move( node1_1.id(), node2.path(), null );

        refresh();

        assertTrue( resolve( node1 ) );
        assertTrue( resolve( node2 ) );
        assertFalse( resolve( node1_1 ) );

        pushNodes( CTX_OTHER.getBranch(), node1_1.id() );

        refresh();

        assertFalse( resolve( node1 ) );
        assertFalse( resolve( node1_1 ) );
        assertFalse( resolve( node2 ) );
    }

    private boolean resolve( final Node node1 )
    {
        return HasUnpublishedChildrenCommand.create().
            parent( node1.id() ).
            target( CTX_OTHER.getBranch() ).
            storageService( this.storageService ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            build().
            execute();
    }
}
