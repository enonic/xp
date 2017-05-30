package com.enonic.xp.core.impl.dump;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.core.impl.dump.model.DumpEntry;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;

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

        final List<DumpEntry> dumpedEntries = writer.get( CTX_DEFAULT.getRepositoryId(), CTX_DEFAULT.getBranch() );

        assertEquals( 4, dumpedEntries.size() );
    }

    @Test
    public void several_versions()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "myNode" );

        updateNode( UpdateNodeParams.create().
            id( node1.id() ).
            editor( ( node ) -> node.data.setString( "fisk", "Ost" ) ).
            build() );

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        final List<DumpEntry> dumpedEntries = writer.get( CTX_DEFAULT.getRepositoryId(), CTX_DEFAULT.getBranch() );

        assertEquals( 1, dumpedEntries.size() );
        final DumpEntry node1Dump = dumpedEntries.get( 0 );
        assertEquals( 1, node1Dump.getOtherVersions().size() );
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
        assertEquals( 1, node1Dump.getOtherVersions().size() );
        assertEquals( newName.toString(), node1Dump.getCurrentVersion().getNodePath().getName() );
        assertEquals( node1.name().toString(), node1Dump.getOtherVersions().get( 0 ).getNodePath().getName() );
    }


    private void doDump( final TestDumpWriter writer )
    {
        NodeHelper.runAsAdmin( () -> RepoDumper.create().
            nodeService( this.nodeService ).
            repositoryService( this.repositoryService ).
            blobStore( this.blobStore ).
            writer( writer ).
            includeBinaries( true ).
            includeVersions( true ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            build().
            execute() );
    }
}

