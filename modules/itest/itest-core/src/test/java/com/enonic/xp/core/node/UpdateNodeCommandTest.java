package com.enonic.xp.core.node;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBinaryReferenceException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.GetBinaryCommand;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateNodeCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void add_new_binary()
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "my-car.jpg" );
        data.setBinaryReference( "my-image", binaryRef );

        final CreateNodeParams params = CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "my-car-image-source".getBytes() ) ).
            build();

        final Node node = createNode( params );

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            editor( toBeEdited -> {
                final PropertyTree nodeData = toBeEdited.data;
                final BinaryReference newBinaryRef = BinaryReference.from( "my-other-car.jpg" );
                nodeData.addBinaryReferences( "new-binary", newBinaryRef );
            } ).
            attachBinary( BinaryReference.from( "my-other-car.jpg" ), ByteSource.wrap( "my-new-binary".getBytes() ) ).
            id( node.id() ).
            build();
        final Node updatedNode = updateNode( updateNodeParams );

        assertEquals( 2, updatedNode.getAttachedBinaries().getSize() );
    }

    @Test
    void update_existing_binary()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "my-car.jpg" );
        data.setBinaryReference( "my-image", binaryRef );

        final CreateNodeParams params = CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            data( data ).
            attachBinary( binaryRef, originalBinaryData() ).
            build();

        final Node node = createNode( params );

        // Update the binary source of the binary reference

        final ByteSource updatedBinaryData = ByteSource.wrap( "my-car-image-updated-source".getBytes() );
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            editor( toBeEdited -> {
            } ).
            attachBinary( binaryRef, updatedBinaryData ).
            id( node.id() ).
            build();

        final Node updatedNode = updateNode( updateNodeParams );

        assertEquals( 1, updatedNode.getAttachedBinaries().getSize() );

        // Verify that the binary source for binary ref in the updated node is the updated version

        final ByteSource binary = GetBinaryCommand.create().
            nodeId( updatedNode.id() ).
            binaryReference( binaryRef ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        final byte[] bytes = ByteStreams.toByteArray( binary.openStream() );

        assertEquals( "my-car-image-updated-source", new String( bytes, StandardCharsets.UTF_8 ) );
    }

    private ByteSource originalBinaryData()
    {
        return ByteSource.wrap( "my-car-image-source".getBytes() );
    }

    @Test
    void keep_existing_binaries()
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "my-car.jpg" );
        data.setBinaryReference( "my-image", binaryRef );

        final CreateNodeParams params = CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "my-car-image-source".getBytes() ) ).
            build();

        final Node node = createNode( params );

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            editor( toBeEdited -> {
                final PropertyTree nodeData = toBeEdited.data;
                nodeData.addString( "newValue", "hepp" );
            } ).
            id( node.id() ).
            build();
        final Node updatedNode = updateNode( updateNodeParams );

        assertEquals( 1, updatedNode.getAttachedBinaries().getSize() );
    }

    @Test
    void keep_existing_binaries_also_when_new_property_but_equal_BinaryReference()
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "my-car.jpg" );
        data.setBinaryReference( "my-image", binaryRef );

        final CreateNodeParams params = CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "my-car-image-source".getBytes() ) ).
            build();

        final Node node = createNode( params );

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            editor( toBeEdited -> {
                toBeEdited.data.removeProperties( "my-image" );
                toBeEdited.data.setBinaryReference( "my-image", binaryRef );
            } ).
            id( node.id() ).
            build();

        final Node updatedNode = updateNode( updateNodeParams );

        assertEquals( 1, updatedNode.getAttachedBinaries().getSize() );
    }

    @Test
    void try_add_new_without_source()
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "my-car.jpg" );
        data.setBinaryReference( "my-image", binaryRef );

        final CreateNodeParams params = CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "my-car-image-source".getBytes() ) ).
            build();

        final Node node = createNode( params );

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            editor( toBeEdited -> {
                final PropertyTree nodeData = toBeEdited.data;
                nodeData.addBinaryReferences( "piratedValue", BinaryReference.from( "new-binary-ref" ) );
            } ).
            id( node.id() ).
            build();

        assertThrows(NodeBinaryReferenceException.class, () -> updateNode( updateNodeParams ));
    }

    @Test
    void try_setting_new_binary_into_existing_property()
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "my-car.jpg" );
        data.setBinaryReference( "my-image", binaryRef );

        final CreateNodeParams params = CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "my-car-image-source".getBytes() ) ).
            build();

        final Node node = createNode( params );

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            editor( toBeEdited -> {
                final PropertyTree nodeData = toBeEdited.data;
                nodeData.addBinaryReferences( "my-image", BinaryReference.from( "pirated-reference" ) );
            } ).
            id( node.id() ).
            build();

        assertThrows(NodeBinaryReferenceException.class, () -> updateNode( updateNodeParams ));
    }

    @Test
    void unreferred_binary_attachment_ignored()
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "my-car.jpg" );
        data.setBinaryReference( "my-image", binaryRef );

        final CreateNodeParams params = CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "my-car-image-source".getBytes() ) ).
            build();

        final Node node = createNode( params );

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            editor( toBeEdited -> {
                final PropertyTree nodeData = toBeEdited.data;
                nodeData.addString( "newValue", "hepp" );
            } ).
            attachBinary( BinaryReference.from( "unreferred binary" ), ByteSource.wrap( "nothing to see here".getBytes() ) ).
            id( node.id() ).
            build();

        final Node updatedNode = updateNode( updateNodeParams );

        assertEquals( 1, updatedNode.getAttachedBinaries().getSize() );
    }

    @Test
    void new_binary_ref_to_already_attached_binary()
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "my-car.jpg" );
        data.setBinaryReference( "my-image", binaryRef );

        final CreateNodeParams params = CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "my-car-image-source".getBytes() ) ).
            build();

        final Node node = createNode( params );

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            editor( toBeEdited -> {
                final PropertyTree nodeData = toBeEdited.data;
                nodeData.addBinaryReference( "my-image-copy", binaryRef );
            } ).
            id( node.id() ).
            build();

        final Node updatedNode = updateNode( updateNodeParams );

        assertEquals( 1, updatedNode.getAttachedBinaries().getSize() );
    }

    @Test
    void timestamp_updated_when_updating()
    {
        final PropertyTree data = new PropertyTree();

        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            data( data ).
            parent( NodePath.ROOT ).
            build() );

        try
        {
            Thread.sleep( 2 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }

        updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( toBeEdited -> {
                toBeEdited.data.addString( "another", "stuff" );
            } ).
            build() );

        final Node updatedNode = getNodeById( node.id() );

        assertTrue( updatedNode.getTimestamp().isAfter( node.getTimestamp() ) );
    }

    @Test
    void update_by_path()
    {
        final PropertyTree data = new PropertyTree();

        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            data( data ).
            parent( NodePath.ROOT ).
            build() );

        updateNode( UpdateNodeParams.create().
            path( node.path() ).
            editor( toBeEdited -> {
                toBeEdited.data.addString( "another", "stuff" );
            } ).
            build() );

        final Node updatedNode = getNodeById( node.id() );

        assertTrue( updatedNode.getTimestamp().isAfter( node.getTimestamp() ) );
    }

    @Test
    void unchanged_node_not_updated()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            data( new PropertyTree() ).
            parent( NodePath.ROOT ).
            build() );

        updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( toBeEdited -> {
            } ).
            build() );

        final Node updatedNode = getNodeById( node.id() );

        assertEquals( updatedNode.getTimestamp(), node.getTimestamp() );
    }

}
