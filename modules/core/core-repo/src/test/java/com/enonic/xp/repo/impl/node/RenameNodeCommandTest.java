package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RenameNodeParams;

import static org.junit.Assert.*;

public class RenameNodeCommandTest
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
    public void rename()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        RenameNodeCommand.create().
            params( RenameNodeParams.create().
                nodeId( createdNode.id() ).
                nodeName( NodeName.from( "my-node-edited" ) ).
                build() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        final Node renamedNode = getNodeById( createdNode.id() );

        assertEquals( "my-node-edited", renamedNode.name().toString() );
    }

    @Test
    public void timestamp_updated()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final Node beforeRename = getNodeById( createdNode.id() );

        RenameNodeCommand.create().
            params( RenameNodeParams.create().
                nodeId( createdNode.id() ).
                nodeName( NodeName.from( "my-node-edited" ) ).
                build() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        final Node renamedNode = getNodeById( createdNode.id() );

        assertTrue( beforeRename.getTimestamp().isBefore( renamedNode.getTimestamp() ) );
    }


    @Test
    public void rename_with_children()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            name( "child1_1" ).
            parent( createdNode.path() ).
            build() );

        final Node child1_2 = createNode( CreateNodeParams.create().
            name( "child1_2" ).
            parent( createdNode.path() ).
            build() );

        final Node child1_1_1 = createNode( CreateNodeParams.create().
            name( "child1_1_1" ).
            parent( child1_1.path() ).
            build() );

        final Node child1_2_1 = createNode( CreateNodeParams.create().
            name( "child1_2_1" ).
            parent( child1_2.path() ).
            build() );

        final Node child1_2_2 = createNode( CreateNodeParams.create().
            name( "child1_2_2" ).
            parent( child1_2.path() ).
            build() );

        refresh();

        RenameNodeCommand.create().
            params( RenameNodeParams.create().
                nodeId( createdNode.id() ).
                nodeName( NodeName.from( "my-node-edited" ) ).
                build() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        refresh();

        final Node renamedNode = getNodeById( createdNode.id() );
        assertEquals( "my-node-edited", renamedNode.name().toString() );

        final Node renamedChild1_1 = getNodeById( child1_1.id() );
        assertEquals( renamedNode.path(), renamedChild1_1.parentPath() );

        final Node renamedChild1_2 = getNodeById( child1_2.id() );
        assertEquals( renamedNode.path(), renamedChild1_2.parentPath() );

        final Node renamedChild1_1_1 = getNodeById( child1_1_1.id() );
        assertEquals( renamedChild1_1.path(), renamedChild1_1_1.parentPath() );

        final Node renamedChild1_2_1 = getNodeById( child1_2_1.id() );
        assertEquals( renamedChild1_2.path(), renamedChild1_2_1.parentPath() );

        final Node renamedChild1_2_2 = getNodeById( child1_2_2.id() );
        assertEquals( renamedChild1_2.path(), renamedChild1_2_2.parentPath() );
    }

    @Test
    public void rename_then_create_with_same_name()
        throws Exception
    {
        final CreateNodeParams createNodeNamedMyNodeParams = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build();

        final Node createdNode = createNode( createNodeNamedMyNodeParams );

        RenameNodeCommand.create().
            params( RenameNodeParams.create().
                nodeId( createdNode.id() ).
                nodeName( NodeName.from( "my-node-edited" ) ).
                build() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        createNode( createNodeNamedMyNodeParams );
    }
}
