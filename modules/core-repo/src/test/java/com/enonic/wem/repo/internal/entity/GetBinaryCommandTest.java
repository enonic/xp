package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class GetBinaryCommandTest
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
            versionService( this.versionService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
            queryService( this.queryService ).
            binaryBlobStore( this.binaryBlobStore ).
            storageService( this.storageService ).
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
            versionService( this.versionService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
            queryService( this.queryService ).
            binaryBlobStore( this.binaryBlobStore ).
            storageService( this.storageService ).
            build().
            execute();

        assertNotNull( myImage );
    }
}