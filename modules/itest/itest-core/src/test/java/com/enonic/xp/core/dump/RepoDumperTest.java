package com.enonic.xp.core.dump;


import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.dump.RepoDumper;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepoDumperTest
    extends AbstractNodeTest
{

    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();
    }

    @Test
    void children()
    {
        final Node node1 = createNode( NodePath.ROOT, "myNode" );
        createNode( node1.path(), "myChild" );
        createNode( node1.path(), "myChild2" );
        createNode( node1.path(), "myChild3" );

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        final List<BranchDumpEntry> dumpEntries = writer.get( testRepoId, ctxDefault().getBranch() );

        assertEquals( 5, dumpEntries.size() );
    }

    @Test
    void node_versions_stored()
    {
        final Node node1 = createNode( NodePath.ROOT, "myNode" );

        updateNode( UpdateNodeParams.create().id( node1.id() ).editor( ( node ) -> node.data.setString( "fisk", "Ost" ) ).build() );

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        final List<BranchDumpEntry> dumpEntries = writer.get( testRepoId, ctxDefault().getBranch() );

        assertEquals( 2, dumpEntries.size() );
        // 2 versions for the root node on draft and master + 2 versions of the node
        Assertions.assertEquals( 4, writer.getNodeVersionKeys().size() );
    }

    @Test
    void binaries()
    {
        final BinaryReference fiskRef = BinaryReference.from( "fisk" );

        final PropertyTree data = new PropertyTree();
        data.addBinaryReference( "myBinaryRef", fiskRef );

        final Node node1 = createNode( CreateNodeParams.create()
                                           .parent( NodePath.ROOT )
                                           .name( "myName" )
                                           .data( data )
                                           .attachBinary( fiskRef, ByteSource.wrap( "myBinaryData".getBytes() ) )
                                           .build() );

        final AttachedBinary attachedBinary = node1.getAttachedBinaries().getByBinaryReference( fiskRef );

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        Assertions.assertTrue( writer.getBinaries().contains( BlobKey.from( attachedBinary.getBlobKey() ) ) );
    }

    @Test
    void binaries_with_versions()
    {
        final BinaryReference ref1 = BinaryReference.from( "fisk" );
        final BinaryReference ref2 = BinaryReference.from( "fisk2" );

        final PropertyTree data = new PropertyTree();
        data.addBinaryReference( "myBinaryRef", ref1 );

        final Node node1 = createNode( CreateNodeParams.create()
                                           .parent( NodePath.ROOT )
                                           .name( "myName" )
                                           .data( data )
                                           .attachBinary( ref1, ByteSource.wrap( "myBinaryData".getBytes() ) )
                                           .build() );

        final AttachedBinary originalBinary = node1.getAttachedBinaries().getByBinaryReference( ref1 );

        final Node updatedNode = updateNode( UpdateNodeParams.create().id( node1.id() ).editor( ( e ) -> {

        } ).attachBinary( ref2, ByteSource.wrap( "myOtherBinaryData".getBytes() ) ).build() );

        final AttachedBinary updateBinary = updatedNode.getAttachedBinaries().getByBinaryReference( ref1 );

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        Assertions.assertTrue( writer.getBinaries().contains( BlobKey.from( originalBinary.getBlobKey() ) ) );
        Assertions.assertTrue( writer.getBinaries().contains( BlobKey.from( updateBinary.getBlobKey() ) ) );
    }

    @Test
    void versions_max_age_filter()
    {
        final Node node1 = createNode( NodePath.ROOT, "myNode" );
        createNode( node1.path(), "myChild" );
        final Node node2 = createNode( node1.path(), "myChild2" );
        createNode( node1.path(), "myChild3" );

        nodeService.patch( PatchNodeParams.create().id( node1.id() ).editor( e -> e.data.addBoolean( "bool", true ) ).build() );
        nodeService.patch( PatchNodeParams.create().id( node2.id() ).editor( e -> e.data.addBoolean( "bool", true ) ).build() );

        final TestDumpWriter writer = new TestDumpWriter();

        RepoDumpResult result = doDump( writer, -1 );
        assertEquals( 0, result.getVersions() );

        result = doDump( writer, 1 );
        assertEquals( 8, result.getVersions() );
    }

    private void doDump( final TestDumpWriter writer )
    {
        doDump( writer, null );
    }

    private RepoDumpResult doDump( final TestDumpWriter writer, final Integer maxAge )
    {
        return NodeHelper.runAsAdmin( () -> RepoDumper.create()
            .nodeService( this.nodeService )
            .writer( writer )
            .includeBinaries( true )
            .includeVersions( true )
            .repository( this.repositoryService.get( testRepoId ) )
            .maxAge( maxAge )
            .build()
            .execute() );
    }
}

