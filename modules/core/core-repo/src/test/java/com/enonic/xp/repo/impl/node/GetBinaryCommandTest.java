package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetBinaryCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void get_by_reference()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference imageRef = BinaryReference.from( "myImage" );

        data.addBinaryReferences( "myBinary", imageRef );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( imageRef, ByteSource.wrap( "thisIsMyImage".getBytes() ) ).
            build() );

        final ByteSource myImage = GetBinaryCommand.create().
            nodeId( node.id() ).
            binaryReference( imageRef ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertNotNull( myImage );
    }

    @Test
    public void get_by_propertyPath()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference imageRef = BinaryReference.from( "myImage" );

        data.addBinaryReferences( "myBinary", imageRef );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( imageRef, ByteSource.wrap( "thisIsMyImage".getBytes() ) ).
            build() );

        final ByteSource myImage = GetBinaryCommand.create().
            nodeId( node.id() ).
            propertyPath( PropertyPath.from( "myBinary" ) ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertNotNull( myImage );
    }
}
