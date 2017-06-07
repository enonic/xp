package com.enonic.xp.repo.impl.dump;

import java.time.Instant;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpParams;
import com.enonic.xp.dump.DumpResult;
import com.enonic.xp.dump.LoadParams;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.Assert.*;

public class DumpServiceImplTest
    extends AbstractNodeTest
{

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private DumpServiceImpl dumpService;

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.dumpService = new DumpServiceImpl();
        this.dumpService.setBlobStore( this.blobStore );
        this.dumpService.setNodeService( this.nodeService );
        this.dumpService.setRepositoryService( this.repositoryService );
        this.dumpService.setBasePath( tempFolder.getRoot().toPath() );
    }

    @Test
    public void dump()
        throws Exception
    {
        createNode( NodePath.ROOT, "myNode" );

        final DumpResult dumpResult = NodeHelper.runAsAdmin( () -> this.dumpService.dump( DumpParams.create().
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            dumpName( "testDump" ).
            build() ) );

        final BranchDumpResult result = dumpResult.get( CTX_DEFAULT.getBranch() );
        assertNotNull( result );
        assertEquals( new Long( 1 ), result.getNumberOfNodes() );
    }

    @Test
    public void branch_created_if_missing()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        final Branch branch = CTX_DEFAULT.getBranch();
        final RepositoryId currentRepoId = CTX_DEFAULT.getRepositoryId();

        NodeHelper.runAsAdmin( () -> {
            this.dumpService.dump( DumpParams.create().
                repositoryId( currentRepoId ).
                dumpName( "testDump" ).
                build() );

            this.repositoryService.deleteBranch( DeleteBranchParams.from( branch ) );
            assertFalse( this.repositoryService.get( currentRepoId ).getBranches().contains( branch ) );

            this.dumpService.load( LoadParams.create().
                dumpName( "testDump" ).
                repositoryId( currentRepoId ).
                build() );

            assertTrue( this.repositoryService.get( currentRepoId ).getBranches().contains( branch ) );
            assertNotNull( this.nodeService.getById( node.id() ) );
        } );
    }

    @Test
    public void versions()
        throws Exception
    {
        final RepositoryId currentRepoId = CTX_DEFAULT.getRepositoryId();
        final Branch branch = CTX_DEFAULT.getBranch();

        final Node node = createNode( NodePath.ROOT, "myNode" );
        updateNode( node );
        updateNode( node );
        refresh();

        NodeHelper.runAsAdmin( () -> {
            final DumpResult result = this.dumpService.dump( DumpParams.create().
                repositoryId( currentRepoId ).
                dumpName( "myTestDump" ).
                includeVersions( true ).
                includeBinaries( true ).
                build() );

            assertEquals( new Long( 3 ), result.get( CTX_DEFAULT.getBranch() ).
                getNumberOfVersions() );

            this.repositoryService.deleteBranch( DeleteBranchParams.from( branch ) );
            refresh();

            this.dumpService.load( LoadParams.create().
                dumpName( "myTestDump" ).
                repositoryId( currentRepoId ).
                build() );

            final NodeVersionQueryResult versionResult2 = this.nodeService.findVersions( GetNodeVersionsParams.create().
                nodeId( node.id() ).
                build() );

            assertEquals( 3, versionResult2.getTotalHits() );
        } );

    }

    private void updateNode( final Node node )
    {
        updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( ( n ) -> n.data.setInstant( "timestamp", Instant.now() ) ).
            build() );
    }

}