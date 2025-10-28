package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.GetBinaryByVersionCommand;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GetBinaryByVersionCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void testExecute()
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

        final ByteSource myImage = GetBinaryByVersionCommand.create().
            nodeId( node.id() ).
            nodeVersionId( node.getNodeVersionId() ).
            binaryReference( imageRef ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertNotNull( myImage );
    }
}
