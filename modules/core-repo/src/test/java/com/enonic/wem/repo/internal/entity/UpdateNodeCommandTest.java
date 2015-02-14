package com.enonic.wem.repo.internal.entity;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBinaryReferenceException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class UpdateNodeCommandTest
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
    public void add_new_binary()
        throws Exception
    {
        final PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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
    public void update_existing_binary()
        throws Exception
    {
        final PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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
            attachBinary( binaryRef, ByteSource.wrap( "my-car-image-updated-source".getBytes() ) ).
            id( node.id() ).
            build();

        final Node updatedNode = updateNode( updateNodeParams );

        assertEquals( 1, updatedNode.getAttachedBinaries().getSize() );

        final ByteSource binary = GetBinaryCommand.create().
            nodeId( updatedNode.id() ).
            binaryReference( binaryRef ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();

        final byte[] bytes = ByteStreams.toByteArray( binary.openStream() );

        assertEquals( "my-car-image-updated-source", new String( bytes, Charset.forName( "UTF-8" ) ) );
    }

    @Test
    public void keep_existing_binaries()
        throws Exception
    {
        final PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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
    public void keep_existing_binaries_also_when_new_Property_but_equal_BinaryReference()
        throws Exception
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

    @Test(expected = NodeBinaryReferenceException.class)
    public void try_add_new_without_source()
        throws Exception
    {
        final PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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

        updateNode( updateNodeParams );
    }

    @Test(expected = NodeBinaryReferenceException.class)
    public void try_setting_new_binary_into_existing_property()
        throws Exception
    {
        final PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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

        updateNode( updateNodeParams );
    }

    @Test
    public void unreferred_binary_attachment_ignored()
        throws Exception
    {
        final PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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
    public void new_binary_ref_to_already_attached_binary()
        throws Exception
    {
        final PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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

}