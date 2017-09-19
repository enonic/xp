package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDeleteException;
import com.enonic.xp.node.UpdateNodeParams;

public class DeleteVersionCommandTest
    extends AbstractNodeTest
{

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }


    @Test(expected = NodeVersionDeleteException.class)
    public void not_allowed_to_delete_version_of_node_in_use()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );

        refresh();

        doDeleteVersion( node1 );
    }

    @Test
    public void delete()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        updateNode( UpdateNodeParams.create().
            id( node1.id() ).
            editor( n -> {
                node1.data().setString( "myValue", "1" );
            } ).
            build() );

        refresh();

        doDeleteNode( node1.id() );

        doDeleteVersion( node1 );
    }

    private void doDeleteVersion( final Node node1 )
    {
        DeleteVersionCommand.create().nodeVersionId( node1.getNodeVersionId() ).
            indexServiceInternal( indexServiceInternal ).
            storageService( storageService ).
            searchService( searchService ).
            repositoryService( repositoryService ).
            build().
            execute();
    }
}