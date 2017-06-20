package com.enonic.xp.repo.impl.dump;

import java.time.Instant;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.repository.SystemRepoInitializer;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.util.BinaryReference;

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
    public void repositories_loaded()
        throws Exception
    {
        final Repositories repositoriesBefore = NodeHelper.runAsAdmin( () -> this.repositoryService.list() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );

        final Repositories repositoriesAfter = NodeHelper.runAsAdmin( () -> this.repositoryService.list() );

        assertEquals( repositoriesBefore.getIds(), repositoriesAfter.getIds() );
    }

    @Test
    public void dumpAndLoad()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );

        final SystemDumpResult systemDumpResult = NodeHelper.runAsAdmin( () -> this.dumpService.systemDump( SystemDumpParams.create().
            dumpName( "testDump" ).
            build() ) );

        final BranchDumpResult result = systemDumpResult.get( CTX_DEFAULT.getRepositoryId() ).get( CTX_DEFAULT.getBranch() );
        assertNotNull( result );
        assertEquals( new Long( 2 ), result.getNumberOfNodes() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );

        final Node currentStoredNode = this.nodeService.getById( node.id() );
        assertEquals( node.getTimestamp(), currentStoredNode.getTimestamp() );
        assertEquals( node.id(), currentStoredNode.id() );
        assertEquals( node.path(), currentStoredNode.path() );
        assertEquals( node.getManualOrderValue(), currentStoredNode.getManualOrderValue() );
        assertEquals( node.getAttachedBinaries(), currentStoredNode.getAttachedBinaries() );
        assertEquals( node.getIndexConfigDocument(), currentStoredNode.getIndexConfigDocument() );
        assertEquals( node.getChildOrder(), currentStoredNode.getChildOrder() );
        assertEquals( node.getNodeState(), currentStoredNode.getNodeState() );
        assertEquals( node.getNodeType(), currentStoredNode.getNodeType() );
        assertEquals( node.data(), currentStoredNode.data() );
    }

    @Test
    public void root_node_gets_correct_properties()
        throws Exception
    {
        final AccessControlList newRepoACL = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( RoleKeys.EVERYONE ).
                allowAll().
                build() ).
            build();

        final Repository newRepo = NodeHelper.runAsAdmin( () -> this.repositoryService.createRepository( CreateRepositoryParams.create().
            repositoryId( RepositoryId.from( "my-new-repo" ) ).
            rootChildOrder( ChildOrder.manualOrder() ).
            rootPermissions( newRepoACL ).
            build() ) );

        final Context newContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( newRepo.getId() ).
            branch( RepositoryConstants.MASTER_BRANCH ).
            build();

        newContext.runWith( () -> createNode( NodePath.ROOT, "myNode" ) );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );

        final Node loadedRootNode = newContext.callWith( () -> this.nodeService.getRoot() );

        assertEquals( newRepoACL, loadedRootNode.getPermissions() );
    }

    @Test
    public void branch_created_if_missing()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        final Branch branch = CTX_DEFAULT.getBranch();
        final RepositoryId currentRepoId = CTX_DEFAULT.getRepositoryId();

        NodeHelper.runAsAdmin( () -> {
            this.dumpService.systemDump( SystemDumpParams.create().
                dumpName( "testDump" ).
                build() );

            this.repositoryService.deleteBranch( DeleteBranchParams.from( branch ) );
            assertFalse( this.repositoryService.get( currentRepoId ).getBranches().contains( branch ) );

            this.dumpService.loadSystemDump( SystemLoadParams.create().
                dumpName( "testDump" ).
                build() );

            assertTrue( this.repositoryService.get( currentRepoId ).getBranches().contains( branch ) );
            assertNotNull( this.nodeService.getById( node.id() ) );
        } );
    }

    @Test
    public void versions()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        final Node updatedNode = updateNode( node );
        final Node currentNode = updateNode( updatedNode );
        refresh();

        final NodeVersionQueryResult versionsBeforeDump = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );

        refresh();

        final NodeVersionQueryResult versionsAfterLoad = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );
        assertEquals( 3, versionsAfterLoad.getTotalHits() );

        final Node currentStoredNode = this.nodeService.getById( node.id() );
        assertEquals( currentNode.data(), currentStoredNode.data() );
        assertEquals( getOrderedTimestamps( versionsBeforeDump ), getOrderedTimestamps( versionsAfterLoad ) );
    }

    private TreeSet<Instant> getOrderedTimestamps( final NodeVersionQueryResult result )
    {
        TreeSet<Instant> timestamps = Sets.newTreeSet();
        result.getNodeVersionsMetadata().forEach( version -> timestamps.add( version.getTimestamp() ) );
        return timestamps;
    }

    @Test
    public void binaries()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "binaryRef" );
        data.addBinaryReference( "myBinary", binaryRef );

        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myNode" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "this is binary data".getBytes() ) ).
            build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );

        final Node currentStoredNode = this.nodeService.getById( node.id() );
        assertEquals( node.getAttachedBinaries(), currentStoredNode.getAttachedBinaries() );
    }

    @Test
    public void limit_number_of_versions()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );

        for ( int i = 0; i < 10; i++ )
        {
            updateNode( node );
        }

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true, SystemDumpParams.create().
            dumpName( "myTestDump" ).
            maxVersions( 5 ).
            build() ) );

        final NodeVersionQueryResult versionsAfterLoad = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            size( -1 ).
            build() );

        assertEquals( 6, versionsAfterLoad.getHits() );
    }

    @Test
    public void binaries_in_versions()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "binaryRef" );
        data.addBinaryReference( "myBinary", binaryRef );

        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myNode" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "this is binary data".getBytes() ) ).
            build() );

        final BinaryReference binaryRef2 = BinaryReference.from( "anotherBinary" );

        final Node updatedNode = updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( n -> n.data.setBinaryReference( "myBinary", binaryRef2 ) ).
            attachBinary( binaryRef2, ByteSource.wrap( "anotherBinary".getBytes() ) ).
            build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );

        final NodeVersionQueryResult versions = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );
        assertEquals( 2, versions.getHits() );

        verifyBinaries( node, updatedNode, versions );
    }

    private void verifyBinaries( final Node node, final Node updatedNode, final NodeVersionQueryResult versions )
    {
        versions.getNodeVersionsMetadata().forEach( ( version ) -> verifyVersionBinaries( node, updatedNode, version ) );
    }

    private void verifyVersionBinaries( final Node node, final Node updatedNode, final NodeVersionMetadata version )
    {
        final NodeVersion storedNode = nodeService.getByNodeVersion( version.getNodeVersionId() );

        storedNode.getAttachedBinaries().forEach( entry -> {
            assertNotNull( this.nodeService.getBinary( storedNode.getVersionId(), entry.getBinaryReference() ) );
        } );

        if ( storedNode.getVersionId().equals( node.getNodeVersionId() ) )
        {
            assertEquals( node.getAttachedBinaries(), storedNode.getAttachedBinaries() );
        }
        else if ( storedNode.getVersionId().equals( updatedNode.getNodeVersionId() ) )
        {
            assertEquals( updatedNode.getAttachedBinaries(), storedNode.getAttachedBinaries() );
        }
    }

    private void dumpDeleteAndLoad( final boolean clearBlobStore )
    {
        final SystemDumpParams params = SystemDumpParams.create().
            dumpName( "myTestDump" ).
            includeVersions( true ).
            includeBinaries( true ).
            build();

        dumpDeleteAndLoad( clearBlobStore, params );
    }

    private void dumpDeleteAndLoad( final boolean clearBlobStore, final SystemDumpParams params )
    {
        this.dumpService.systemDump( params );

        final Repositories repositories = this.repositoryService.list();

        for ( final Repository repository : repositories )
        {
            if ( !repository.getId().equals( SystemConstants.SYSTEM_REPO.getId() ) )
            {
                this.repositoryService.deleteRepository( DeleteRepositoryParams.from( repository.getId() ) );
            }
        }

        if ( clearBlobStore )
        {
            this.blobStore.clear();
        }

        new SystemRepoInitializer( this.repositoryService, this.storageService ).initialize();

        this.dumpService.loadSystemDump( SystemLoadParams.create().
            dumpName( "myTestDump" ).
            includeVersions( true ).
            build() );

        refresh();
    }

    private Node updateNode( final Node node )
    {
        return updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( ( n ) -> n.data.setInstant( "timestamp", Instant.now() ) ).
            build() );
    }
}