package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.repo.impl.node.MoveNodeCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RenameNodeCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
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

        MoveNodeCommand.create()
            .id( createdNode.id() )
            .newNodeName( NodeName.from( "my-node-edited" ) )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.searchService )
            .storageService( this.storageService )
            .build()
            .execute();

        final Node renamedNode = getNodeById( createdNode.id() );

        assertEquals( "my-node-edited", renamedNode.name().toString() );
    }

    @Test
    public void rename_to_same()
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final MoveNodeCommand command = MoveNodeCommand.create()
            .id( createdNode.id() )
            .newNodeName( NodeName.from( "my-node" ) )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.searchService )
            .storageService( this.storageService )
            .build();

        assertThrows( NodeAlreadyExistAtPathException.class, command::execute );
    }

    @Test
    public void rename_to_existing_fails()
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createNode( CreateNodeParams.create().
            name( "my-node-existing" ).
            parent( NodePath.ROOT ).
            build() );

        final MoveNodeCommand command = MoveNodeCommand.create()
            .id( createdNode.id() )
            .newNodeName( NodeName.from( "my-node-existing" ) )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.searchService )
            .storageService( this.storageService )
            .build();

        assertThrows( NodeAlreadyExistAtPathException.class, command::execute );
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

        MoveNodeCommand.create()
            .id( createdNode.id() )
            .newNodeName( NodeName.from( "my-node-edited" ) )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.searchService )
            .storageService( this.storageService )
            .build()
            .execute();

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

        MoveNodeCommand.create()
            .id( createdNode.id() )
            .newNodeName( NodeName.from( "my-node-edited" ) )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.searchService )
            .storageService( this.storageService )
            .build()
            .execute();

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

        MoveNodeCommand.create()
            .id( createdNode.id() )
            .newNodeName( NodeName.from( "my-node-edited" ) )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.searchService )
            .storageService( this.storageService )
            .build()
            .execute();


        createNode( createNodeNamedMyNodeParams );
    }

    @Test
    public void cannot_rename_root_node()
        throws Exception
    {
        assertThrows( OperationNotPermittedException.class, () -> MoveNodeCommand.create()
            .id( Node.ROOT_UUID )
            .newNodeName( NodeName.from( "my-node-edited" ) )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.searchService )
            .storageService( this.storageService )
            .build()
            .execute() );
    }
}
