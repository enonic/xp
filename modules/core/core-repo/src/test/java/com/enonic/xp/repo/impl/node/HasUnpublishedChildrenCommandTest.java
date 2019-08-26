package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class HasUnpublishedChildrenCommandTest
    extends AbstractNodeTest
{

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
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
