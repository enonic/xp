package com.enonic.xp.repo.impl.dump;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
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

        final List<DumpEntry> dumpEntries = writer.get( CTX_DEFAULT.getRepositoryId(), CTX_DEFAULT.getBranch() );

        assertEquals( 4, dumpEntries.size() );
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
    public void node_versions_meta_data_stored()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "myNode" );

        final Node updatedNode = updateNode( UpdateNodeParams.create().
            id( node1.id() ).
            editor( ( node ) -> node.data.setString( "fisk", "Ost" ) ).
            build() );

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        assertTrue( hasVersionMeta( writer, node1.id(), node1.getNodeVersionId(), updatedNode.getNodeVersionId() ) );
    }

    @Test
    public void renamed()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "myNode" );

        final NodeName newName = NodeName.from( "updatedName" );
        this.nodeService.rename( RenameNodeParams.create().
            nodeId( node1.id() ).
            nodeName( newName ).
            build() );

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        final List<DumpEntry> dumpedEntries = writer.get( CTX_DEFAULT.getRepositoryId(), CTX_DEFAULT.getBranch() );

        assertEquals( 1, dumpedEntries.size() );
        final DumpEntry node1Dump = dumpedEntries.get( 0 );
        assertEquals( 2, node1Dump.getVersions().size() );
        //  assertEquals( newName.toString(), node1Dump.getVersions().iterator().next().getNodePath().getName() );
        //  assertEquals( node1.name().toString(), node1Dump.getOtherVersions().get( 0 ).getNodePath().getName() );
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

    @Test
    public void dumpMeta()
        throws Exception
    {
        createNode( NodePath.ROOT, "myNode" );
        final TestDumpWriter writer = new TestDumpWriter();
        doDump( writer );

        assertNotNull( writer.getDumpMeta() );
        assertEquals( "x-y-z", writer.getDumpMeta().getXpVersion() );
    }

    private boolean hasVersionMeta( final TestDumpWriter writer, final NodeId nodeId, final NodeVersionId... versionIds )
    {
        final List<DumpEntry> dumpedEntries = writer.get( CTX_DEFAULT.getRepositoryId(), CTX_DEFAULT.getBranch() );

        for ( final DumpEntry entry : dumpedEntries )
        {
            if ( entry.getNodeId().equals( nodeId ) )
            {
                return entry.getAllVersionIds().containsAll( Arrays.asList( versionIds ) );
            }
        }
        return false;
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
            xpVersion( "x-y-z" ).
            build().
            execute() );
    }
}

