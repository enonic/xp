package com.enonic.xp.repo.impl.dump;


import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class RepoDumperTest
    extends AbstractNodeTest
{

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        createDefaultRootNode();
    }

    @Test
    public void children()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "myNode" );
        createNode( node1.path(), "myChild" );
        createNode( node1.path(), "myChild2" );
        createNode( node1.path(), "myChild3" );
        refresh();

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        final List<BranchDumpEntry> dumpEntries = writer.get( CTX_DEFAULT.getRepositoryId(), CTX_DEFAULT.getBranch() );

        assertEquals( 5, dumpEntries.size() );
    }

    @Test
    public void node_versions_stored()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "myNode" );

        final Node updatedNode = updateNode( UpdateNodeParams.create().
            id( node1.id() ).
            editor( ( node ) -> node.data.setString( "fisk", "Ost" ) ).
            build() );

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        assertTrue( writer.hasVersions( node1.getNodeVersionId(), updatedNode.getNodeVersionId() ) );
    }

    @Test
    public void binaries()
        throws Exception
    {
        final BinaryReference fiskRef = BinaryReference.from( "fisk" );

        final PropertyTree data = new PropertyTree();
        data.addBinaryReference( "myBinaryRef", fiskRef );

        final Node node1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myName" ).
            data( data ).
            attachBinary( fiskRef, ByteSource.wrap( "myBinaryData".getBytes() ) ).
            build() );

        final AttachedBinary attachedBinary = node1.getAttachedBinaries().getByBinaryReference( fiskRef );

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        assertTrue( writer.getBinaries().contains( attachedBinary.getBlobKey() ) );
    }

    @Test
    public void binaries_with_versions()
        throws Exception
    {
        final BinaryReference ref1 = BinaryReference.from( "fisk" );
        final BinaryReference ref2 = BinaryReference.from( "fisk2" );

        final PropertyTree data = new PropertyTree();
        data.addBinaryReference( "myBinaryRef", ref1 );

        final Node node1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myName" ).
            data( data ).
            attachBinary( ref1, ByteSource.wrap( "myBinaryData".getBytes() ) ).
            build() );

        final AttachedBinary originalBinary = node1.getAttachedBinaries().getByBinaryReference( ref1 );

        final Node updatedNode = updateNode( UpdateNodeParams.create().
            id( node1.id() ).
            editor( ( e ) -> {

            } ).
            attachBinary( ref2, ByteSource.wrap( "myOtherBinaryData".getBytes() ) ).
            build() );

        final AttachedBinary updateBinary = updatedNode.getAttachedBinaries().getByBinaryReference( ref1 );

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        assertTrue( writer.getBinaries().contains( originalBinary.getBlobKey() ) );
        assertTrue( writer.getBinaries().contains( updateBinary.getBlobKey() ) );
    }

    private void doDump( final TestDumpWriter writer )
    {
        NodeHelper.runAsAdmin( () -> RepoDumper.create().
            nodeService( this.nodeService ).
            repositoryService( this.repositoryService ).
            writer( writer ).
            includeBinaries( true ).
            includeVersions( true ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            build().
            execute() );
    }
}

