package com.enonic.xp.repo.impl.dump;

import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.dump.SystemDumpListener;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemDumpUpgradeParams;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeCommitQueryResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre5.Pre5ContentConstants;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.node.RenameNodeCommand;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
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
import com.enonic.xp.upgrade.UpgradeListener;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.Reference;
import com.enonic.xp.util.Version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DumpServiceImplTest
    extends AbstractNodeTest
{
    private DumpServiceImpl dumpService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.dumpService = new DumpServiceImpl();
        this.dumpService.setBlobStore( this.blobStore );
        this.dumpService.setNodeService( this.nodeService );
        this.dumpService.setRepositoryService( this.repositoryService );
        this.dumpService.setBasePath( temporaryFolder.toFile().toPath() );
        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getInstalledApplications() ).thenReturn( Applications.empty() );
        this.dumpService.setApplicationService( applicationService );
    }

    @Test
    public void admin_role_required()
        throws Exception
    {
        assertThrows( RepoDumpException.class, () -> {
            doDump( SystemDumpParams.create().
                dumpName( "testDump" ).
                build() );

        } );
    }

    @Test
    public void existing_repositories_deleted()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );

        NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create().
            dumpName( "testDump" ).
            build() ) );

        NodeHelper.runAsAdmin( () -> doDump( SystemDumpParams.create().dumpName( "myTestDump" ).
            includeVersions( true ).
            includeBinaries( true ).
            build() ) );

        final Node toBeDeleted = createNode( NodePath.ROOT, "ShouldBeDeleted" );

        NodeHelper.runAsAdmin( this::doLoad );

        // assertNotNull( getNode( node.id() ) );
        assertNull( getNode( toBeDeleted.id() ) );
    }


    @Test
    public void obsolete_repository_deleted()
        throws Exception
    {
        final AccessControlList newRepoACL = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( RoleKeys.EVERYONE ).
                allowAll().
                build() ).
            build();

        final Repository newRepoInsideDump =
            NodeHelper.runAsAdmin( () -> this.repositoryService.createRepository( CreateRepositoryParams.create().
                repositoryId( RepositoryId.from( "new-repo-inside-dump" ) ).
                rootChildOrder( ChildOrder.manualOrder() ).
                rootPermissions( newRepoACL ).
                build() ) );

        NodeHelper.runAsAdmin( () -> doDump( SystemDumpParams.create().dumpName( "myTestDump" ).
            build() ) );

        final Repository newRepoOutsideDump =
            NodeHelper.runAsAdmin( () -> this.repositoryService.createRepository( CreateRepositoryParams.create().
                repositoryId( RepositoryId.from( "new-repo-outside-dump" ) ).
                rootChildOrder( ChildOrder.manualOrder() ).
                rootPermissions( newRepoACL ).
                build() ) );

        final Repositories oldRepos = NodeHelper.runAsAdmin( () -> this.repositoryService.list() );

        NodeHelper.runAsAdmin( this::doLoad );

        final Repositories newRepos = NodeHelper.runAsAdmin( () -> this.repositoryService.list() );

        assertEquals( 4, oldRepos.getIds().getSize() );
        assertEquals( 3, newRepos.getIds().getSize() );

        assertNotNull( newRepos.getRepositoryById( newRepoInsideDump.getId() ) );
        assertNull( newRepos.getRepositoryById( newRepoOutsideDump.getId() ) );
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

        final SystemDumpResult systemDumpResult = NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create().
            dumpName( "testDump" ).
            build() ) );

        final BranchDumpResult result = systemDumpResult.get( CTX_DEFAULT.getRepositoryId() ).get( CTX_DEFAULT.getBranch() );
        assertNotNull( result );
        assertEquals( 2, result.getSuccessful() );

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
    public void dump_long_filename()
        throws Exception
    {
        final String nodeName = "this-is-my-node-with-very-long-filename-more-than-100-characters-yes-it-has-to-be-very-long-indeed-sir";
        final Node node = createNode( NodePath.ROOT, nodeName );

        final SystemDumpResult systemDumpResult = NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create().
            dumpName( "testDump" ).
            build() ) );

        final BranchDumpResult result = systemDumpResult.get( CTX_DEFAULT.getRepositoryId() ).get( CTX_DEFAULT.getBranch() );
        assertNotNull( result );
        assertEquals( 2, result.getSuccessful() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );

        final Node storedNode = this.nodeService.getById( node.id() );
        assertNotNull( storedNode );
        assertEquals( nodeName, storedNode.name().toString() );
    }

    @Test
    public void verify_result()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        updateNode( node );
        updateNode( node );
        updateNode( node );

        final SystemDumpResult systemDumpResult = NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create().
            dumpName( "testDump" ).
            build() ) );

        // 4 of node, 1 of root
        assertEquals( 5, systemDumpResult.get( CTX_DEFAULT.getRepositoryId() ).getVersions() );
        final BranchDumpResult branchDumpResult = systemDumpResult.get( CTX_DEFAULT.getRepositoryId() ).get( CTX_DEFAULT.getBranch() );

        assertEquals( 2, branchDumpResult.getSuccessful() );
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
            doDump( SystemDumpParams.create().
                dumpName( "testDump" ).
                build() );

            this.repositoryService.deleteBranch( DeleteBranchParams.from( branch ) );
            assertFalse( this.repositoryService.get( currentRepoId ).getBranches().contains( branch ) );

            this.dumpService.load( SystemLoadParams.create().
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

    @Test
    public void same_version_in_different_branches_not_duplicated()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        this.nodeService.push( NodeIds.from( node.id() ), CTX_OTHER.getBranch() );
        refresh();

        final NodeVersionQueryResult versionsBeforeDump = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );

        assertEquals( 1, versionsBeforeDump.getTotalHits() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );

        refresh();

        final NodeVersionQueryResult versionsAfterLoad = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );

        versionsAfterLoad.getNodeVersionsMetadata().forEach(
            ( e ) -> System.out.println( e.getNodeVersionId() + " - " + e.getTimestamp() ) );

        assertEquals( 1, versionsAfterLoad.getTotalHits() );
    }

    @Test
    public void different_versions_in_different_branches_not_duplicated()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        updateNode( node );
        this.nodeService.push( NodeIds.from( node.id() ), CTX_OTHER.getBranch() );
        updateNode( node );
        updateNode( node );
        refresh();

        final NodeVersionQueryResult versionsBeforeDump = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );
        refresh();

        final NodeVersionQueryResult versionsAfterLoad = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );

        assertEquals( versionsBeforeDump.getTotalHits(), versionsAfterLoad.getTotalHits() );
    }

    @Test
    public void active_versions_after_load()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        this.nodeService.push( NodeIds.from( node.id() ), CTX_OTHER.getBranch() );
        updateNode( node );
        refresh();

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );
        refresh();

        final GetActiveNodeVersionsResult activeVersions = this.nodeService.getActiveVersions( GetActiveNodeVersionsParams.create().
            branches( Branches.from( CTX_DEFAULT.getBranch(), CTX_OTHER.getBranch() ) ).
            nodeId( node.id() ).
            build() );

        final Node defaultBranchNode = CTX_DEFAULT.callWith( () -> this.nodeService.getById( node.id() ) );
        final Node otherBranchNode = CTX_OTHER.callWith( () -> this.nodeService.getById( node.id() ) );

        final ImmutableMap<Branch, NodeVersionMetadata> activeVersionsMap = activeVersions.getNodeVersions();

        assertEquals( 2, activeVersionsMap.size() );
        assertEquals( defaultBranchNode.getNodeVersionId(), activeVersionsMap.get( CTX_DEFAULT.getBranch() ).getNodeVersionId() );
        assertEquals( otherBranchNode.getNodeVersionId(), activeVersionsMap.get( CTX_OTHER.getBranch() ).getNodeVersionId() );
    }

    @Test
    public void active_versions_in_versions_list()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        this.nodeService.push( NodeIds.from( node.id() ), CTX_OTHER.getBranch() );
        updateNode( node );
        refresh();

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );
        refresh();

        final GetActiveNodeVersionsResult activeVersions = this.nodeService.getActiveVersions( GetActiveNodeVersionsParams.create().
            branches( Branches.from( CTX_DEFAULT.getBranch(), CTX_OTHER.getBranch() ) ).
            nodeId( node.id() ).
            build() );

        final ImmutableMap<Branch, NodeVersionMetadata> activeVersionsMap = activeVersions.getNodeVersions();

        final NodeVersionQueryResult versionsAfterLoad = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );

        activeVersionsMap.values().forEach(
            key -> assertTrue( versionsAfterLoad.getNodeVersionsMetadata().getAllVersionIds().contains( key.getNodeVersionId() ) ) );
    }

    @Test
    public void version_ids_should_stay_the_same_if_no_changes()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );

        RenameNodeCommand.create().
            searchService( this.searchService ).
            storageService( this.storageService ).
            indexServiceInternal( this.indexServiceInternal ).
            params( RenameNodeParams.create().
                nodeId( node.id() ).
                nodeName( NodeName.from( "renamed" ) ).
                build() ).
            build().
            execute();

        this.nodeService.push( NodeIds.from( node.id() ), CTX_OTHER.getBranch() );
        updateNode( node );
        refresh();

        final NodeVersionQueryResult versionsBeforeLoad = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );
        refresh();

        final NodeVersionQueryResult versionsAfterLoad = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );

        final NodeVersionIds versionIdsBeforeLoad = versionsBeforeLoad.getNodeVersionsMetadata().getAllVersionIds();
        final NodeVersionIds versionIdsAfterLoad = versionsAfterLoad.getNodeVersionsMetadata().getAllVersionIds();

        assertEquals( versionIdsBeforeLoad, versionIdsAfterLoad );
    }

    private TreeSet<Instant> getOrderedTimestamps( final NodeVersionQueryResult result )
    {
        TreeSet<Instant> timestamps = new TreeSet<>();
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
            build() );

        assertEquals( 6, versionsAfterLoad.getHits() );
    }

    @Test
    public void number_of_versions_in_other_repo()
    {
        final Repository myRepo = NodeHelper.runAsAdmin( () -> this.repositoryService.createRepository( CreateRepositoryParams.create().
            repositoryId( RepositoryId.from( "myrepo" ) ).
            rootPermissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( CTX_DEFAULT.getAuthInfo().getUser().getKey() ).
                    allowAll().
                    build() ).
                build() ).
            build() ) );

        final Context myRepoContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( myRepo.getId() ).
            branch( RepositoryConstants.MASTER_BRANCH ).
            build();

        final Node myNode = myRepoContext.callWith( () -> createNode( NodePath.ROOT, "myNode" ) );
        myRepoContext.runWith( () -> updateNode( myNode ) );
        myRepoContext.runWith( () -> updateNode( myNode ) );
        myRepoContext.runWith( () -> updateNode( myNode ) );

        final SystemLoadResult dumpResult = NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true, SystemDumpParams.create().
            dumpName( "myTestDump" ).
            build() ) );

        final RepoLoadResult repoLoadResult = getRepoLoadResult( dumpResult, myRepo.getId() );

        final VersionsLoadResult versionsLoadResult = repoLoadResult.getVersionsLoadResult();
        assertNotNull( versionsLoadResult );
        // One for root, 4 for myNode
        assertEquals( 5, versionsLoadResult.getSuccessful() );
    }

    private RepoLoadResult getRepoLoadResult( final SystemLoadResult result, final RepositoryId repositoryId )
    {
        final Iterator<RepoLoadResult> iterator = result.iterator();

        while ( iterator.hasNext() )
        {
            final RepoLoadResult next = iterator.next();
            if ( next.getRepositoryId().equals( repositoryId ) )
            {
                return next;
            }
        }
        return null;
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

    @Test
    public void dumpAndLoadListener()
    {
        createNode( NodePath.ROOT, "myNode" );

        final SystemDumpListener systemDumpListener = Mockito.mock( SystemDumpListener.class );
        NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create().
            dumpName( "myTestDump" ).
            includeVersions( true ).
            includeBinaries( true ).
            listener( systemDumpListener ).
            build() ) );

        Mockito.verify( systemDumpListener ).dumpingBranch( CTX_DEFAULT.getRepositoryId(), CTX_DEFAULT.getBranch(), 2 );
        Mockito.verify( systemDumpListener ).dumpingBranch( CTX_OTHER.getRepositoryId(), CTX_OTHER.getBranch(), 1 );
        Mockito.verify( systemDumpListener ).dumpingBranch( SystemConstants.SYSTEM_REPO.getId(), SystemConstants.BRANCH_SYSTEM, 4 );
        Mockito.verify( systemDumpListener, Mockito.times( 7 ) ).nodeDumped();

        final SystemLoadListener systemLoadListener = Mockito.mock( SystemLoadListener.class );
        NodeHelper.runAsAdmin( () -> this.dumpService.load( SystemLoadParams.create().
            dumpName( "myTestDump" ).
            includeVersions( true ).
            listener( systemLoadListener ).
            build() ) );

        Mockito.verify( systemLoadListener ).loadingBranch( CTX_DEFAULT.getRepositoryId(), CTX_DEFAULT.getBranch(), 2L );
        Mockito.verify( systemLoadListener ).loadingBranch( CTX_OTHER.getRepositoryId(), CTX_OTHER.getBranch(), 1L );
        Mockito.verify( systemLoadListener ).loadingVersions( CTX_OTHER.getRepositoryId() );
        Mockito.verify( systemLoadListener ).loadingBranch( SystemConstants.SYSTEM_REPO.getId(), SystemConstants.BRANCH_SYSTEM, 4L );
        Mockito.verify( systemLoadListener ).loadingVersions( SystemConstants.SYSTEM_REPO.getId() );
        Mockito.verify( systemLoadListener, Mockito.times( 2 + 1 + 2 + 4 + 4 ) ).entryLoaded();
    }

    @Test
    public void skip_versions()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        final Node updatedNode = updateNode( node );
        final Node currentNode = updateNode( updatedNode );
        refresh();

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true, false ) );

        refresh();

        final NodeVersionQueryResult versionsAfterLoad = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );
        assertEquals( 1, versionsAfterLoad.getTotalHits() );

        final Node currentStoredNode = this.nodeService.getById( node.id() );
        assertEquals( currentNode.data(), currentStoredNode.data() );
    }

    @Test
    public void upgrade_up_to_date()
    {
        NodeHelper.runAsAdmin( () -> {
            doDump( SystemDumpParams.create().
                dumpName( "testDump" ).
                build() );

            final SystemDumpUpgradeParams params = SystemDumpUpgradeParams.create().
                dumpName( "testDump" ).
                build();
            final DumpUpgradeResult result = this.dumpService.upgrade( params );
            assertEquals( DumpConstants.MODEL_VERSION, result.getInitialVersion() );
            assertEquals( DumpConstants.MODEL_VERSION, result.getUpgradedVersion() );
        } );
    }

    @Test
    public void upgrade()
        throws Exception
    {
        final String dumpName = "testDump";
        createIncompatibleDump( dumpName );

        NodeHelper.runAsAdmin( () -> {
            final UpgradeListener upgradeListener = Mockito.mock( UpgradeListener.class );

            final SystemDumpUpgradeParams params = SystemDumpUpgradeParams.create().
                dumpName( dumpName ).
                upgradeListener( upgradeListener ).
                build();

            final DumpUpgradeResult result = this.dumpService.upgrade( params );
            assertEquals( new Version( 0, 0, 0 ), result.getInitialVersion() );
            assertEquals( DumpConstants.MODEL_VERSION, result.getUpgradedVersion() );

            Mockito.verify( upgradeListener, Mockito.times( 8 ) ).upgraded();
            Mockito.verify( upgradeListener, Mockito.times( 1 ) ).total( 8 );

            FileDumpReader reader = new FileDumpReader( temporaryFolder.toFile().toPath(), dumpName, null );
            final DumpMeta updatedMeta = reader.getDumpMeta();
            assertEquals( DumpConstants.MODEL_VERSION, updatedMeta.getModelVersion() );
        } );
    }

    @Test
    @Disabled("Needs discuss how upgrade old versions (Currently, no mapping for timestamp)")
    public void loadWithUpgrade()
        throws Exception
    {
        final String dumpName = "testDump";
        createIncompatibleDump( dumpName );

        NodeHelper.runAsAdmin( () -> {
            this.dumpService.load( SystemLoadParams.create().
                dumpName( dumpName ).
                upgrade( true ).
                includeVersions( true ).
                build() );

            FileDumpReader reader = new FileDumpReader( temporaryFolder.toFile().toPath(), dumpName, null );
            final DumpMeta updatedMeta = reader.getDumpMeta();
            assertEquals( DumpConstants.MODEL_VERSION, updatedMeta.getModelVersion() );

            final NodeId nodeId = NodeId.from( "f0fb822c-092d-41f9-a961-f3811d81e55a" );
            final NodeId fragmentNodeId = NodeId.from( "7ee16649-85c6-4a76-8788-74be03be6c7a" );
            final NodeId postNodeId = NodeId.from( "1f798176-5868-411b-8093-242820c20620" );
            final NodePath nodePath = NodePath.create( "/content/mysite" ).build();
            final NodeVersionId draftNodeVersionId = NodeVersionId.from( "f3765655d5f0c7c723887071b517808dae00556c" );
            final NodeVersionId masterNodeVersionId = NodeVersionId.from( "02e61f29a57309834d96bbf7838207ac456bbf5c" );

            final Node draftNode = nodeService.getById( nodeId );
            assertNotNull( draftNode );
            assertEquals( draftNodeVersionId, draftNode.getNodeVersionId() );
            assertEquals( nodePath, draftNode.path() );
            assertEquals( "2019-02-20T14:44:06.883Z", draftNode.getTimestamp().toString() );

            final Node masterNode = ContextBuilder.
                from( ContextAccessor.current() ).
                branch( Branch.from( "master" ) ).
                build().
                callWith( () -> nodeService.getById( nodeId ) );
            assertNotNull( masterNode );
            assertEquals( masterNodeVersionId, masterNode.getNodeVersionId() );
            assertEquals( nodePath, masterNode.path() );
            assertEquals( "2018-11-23T11:14:21.662Z", masterNode.getTimestamp().toString() );

            checkCommitUpgrade( nodeId );
            checkPageFlatteningUpgradePage( draftNode );

            final Node fragmentNode = nodeService.getById( fragmentNodeId );
            checkPageFlatteningUpgradeFragment( fragmentNode );

            checkRepositoryUpgrade( updatedMeta );

            final Node postNode = nodeService.getById( postNodeId );
            checkHtmlAreaUpgrade( draftNode, postNode );

            checkLanguageUpgrade( draftNode );
        } );
    }

    private void checkRepositoryUpgrade( final DumpMeta updatedMeta )
    {
        final RepoDumpResult repoDumpResult = updatedMeta.getSystemDumpResult().get( ContentConstants.CONTENT_REPO_ID );
        assertNotNull( repoDumpResult );

        assertNull( repositoryService.get( Pre5ContentConstants.CONTENT_REPO_ID ) );
    }

    private void checkCommitUpgrade( final NodeId nodeId )
    {
        nodeService.refresh( RefreshMode.ALL );

        final NodeCommitQuery nodeCommitQuery = NodeCommitQuery.create().build();
        final NodeCommitQueryResult nodeCommitQueryResult = ContextBuilder.
            from( ContextAccessor.current() ).
            branch( Branch.from( "master" ) ).
            build().
            callWith( () -> nodeService.findCommits( nodeCommitQuery ) );
        assertEquals( 1, nodeCommitQueryResult.getTotalHits() );

        final NodeCommitEntry commitEntry = nodeCommitQueryResult.getNodeCommitEntries().
            iterator().
            next();
        final NodeCommitId nodeCommitId = commitEntry.getNodeCommitId();
        assertEquals( "Dump upgrade", commitEntry.getMessage() );
        assertEquals( "user:system:node-su", commitEntry.getCommitter().toString() );

        final GetActiveNodeVersionsParams activeNodeVersionsParams = GetActiveNodeVersionsParams.create().
            nodeId( nodeId ).
            branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
            build();
        final GetActiveNodeVersionsResult activeNodeVersionsResult = ContextBuilder.
            from( ContextAccessor.current() ).
            branch( Branch.from( "master" ) ).
            build().
            callWith( () -> nodeService.getActiveVersions( activeNodeVersionsParams ) );
        final NodeVersionMetadata draftNodeVersion = activeNodeVersionsResult.getNodeVersions().
            get( ContentConstants.BRANCH_DRAFT );
        assertNull( draftNodeVersion.getNodeCommitId() );
        final NodeVersionMetadata masterNodeVersion = activeNodeVersionsResult.getNodeVersions().
            get( ContentConstants.BRANCH_MASTER );
        assertEquals( nodeCommitId, masterNodeVersion.getNodeCommitId() );

        final NodeVersionQuery nodeVersionQuery = NodeVersionQuery.create().
            nodeId( nodeId ).
            build();
        final NodeVersionQueryResult versionQueryResult = ContextBuilder.
            from( ContextAccessor.current() ).
            branch( Branch.from( "draft" ) ).
            build().
            callWith( () -> nodeService.findVersions( nodeVersionQuery ) );
        assertEquals( 16, versionQueryResult.getTotalHits() );
    }

    private void checkPageFlatteningUpgradePage( final Node node )
    {
        final PropertyTree nodeData = node.data();
        assertTrue( nodeData.hasProperty( "components" ) );
        assertFalse( nodeData.hasProperty( "page" ) );

        //Check page component
        final Iterable<PropertySet> components = nodeData.getSets( "components" );
        final PropertySet pageComponent = components.iterator().next();
        assertEquals( "page", pageComponent.getString( "type" ) );
        assertEquals( "/", pageComponent.getString( "path" ) );
        final PropertySet pageComponentData = pageComponent.getSet( "page" );
        assertEquals( "com.enonic.app.superhero:default", pageComponentData.getString( "descriptor" ) );
        assertEquals( Boolean.TRUE, pageComponentData.getBoolean( "customized" ) );
        final PropertySet pageConfig = pageComponentData.getSet( "config" );
        assertNotNull( pageConfig.getSet( "com-enonic-app-superhero" ).getSet( "default" ) );

        //Checks layout component
        final PropertySet layoutComponent = StreamSupport.stream( components.spliterator(), false ).
            filter( component -> "/main/0".equals( component.getString( "path" ) ) ).findFirst().orElseThrow();
        assertEquals( "layout", layoutComponent.getString( "type" ) );
        final PropertySet layoutComponentData = layoutComponent.getSet( "layout" );
        assertEquals( "com.enonic.app.superhero:two-column", layoutComponentData.getString( "descriptor" ) );
        assertNotNull( layoutComponentData.getSet( "config" ).getSet( "com-enonic-app-superhero" ).getSet( "two-column" ) );

        //Checks image component
        final PropertySet imageComponent = StreamSupport.stream( components.spliterator(), false ).
            filter( component -> "/main/0/left/0".equals( component.getString( "path" ) ) ).findFirst().orElseThrow();
        assertEquals( "image", imageComponent.getString( "type" ) );
        final PropertySet imageComponentData = imageComponent.getSet( "image" );
        assertEquals( "cf09fe7a-1be9-46bb-ad84-87ba69630cb7", imageComponentData.getString( "id" ) );
        assertEquals( "A caption", imageComponentData.getString( "caption" ) );

        //Checks part component
        final PropertySet partComponent = StreamSupport.stream( components.spliterator(), false ).
            filter( component -> "/main/0/right/0".equals( component.getString( "path" ) ) ).findFirst().orElseThrow();
        assertEquals( "part", partComponent.getString( "type" ) );
        final PropertySet partComponentData = partComponent.getSet( "part" );
        assertEquals( "com.enonic.app.superhero:tag-cloud", partComponentData.getString( "descriptor" ) );
        assertNotNull( partComponentData.getSet( "config" ).getSet( "com-enonic-app-superhero" ).getSet( "tag-cloud" ) );

        //Checks fragment component
        final PropertySet fragmentComponent = StreamSupport.stream( components.spliterator(), false ).
            filter( component -> "/main/0/right/1".equals( component.getString( "path" ) ) ).findFirst().orElseThrow();
        assertEquals( "fragment", fragmentComponent.getString( "type" ) );
        final PropertySet fragmentComponentData = fragmentComponent.getSet( "fragment" );
        assertEquals( "7ee16649-85c6-4a76-8788-74be03be6c7a", fragmentComponentData.getString( "id" ) );

        //Checks text component
        final PropertySet textComponent = StreamSupport.stream( components.spliterator(), false ).
            filter( component -> "/main/1".equals( component.getString( "path" ) ) ).findFirst().orElseThrow();
        assertEquals( "text", textComponent.getString( "type" ) );
        final PropertySet textComponentData = textComponent.getSet( "text" );
        assertEquals( "<p>text1</p>\n" + "\n" + "<p>&nbsp;</p>\n", textComponentData.getString( "value" ) );
    }

    private void checkHtmlAreaUpgrade( final Node siteNode, final Node postNode )
    {
        final Iterable<Reference> siteProcessedReferences = siteNode.data().getReferences( "processedReferences" );
        assertIterableEquals( List.of( Reference.from( "5343c381-d2b2-4257-871c-4479c486cfdc" ) ), siteProcessedReferences );

        final Iterable<Reference> postProcessedReferences = postNode.data().getReferences( "processedReferences" );
        assertIterableEquals(
            List.of( Reference.from( "20df93a4-db5c-431e-8ed0-5c6946322bc7" ), Reference.from( "cf09fe7a-1be9-46bb-ad84-87ba69630cb7" ) ),
            postProcessedReferences );

        final String postValue = postNode.data().getString( "data.post" );
        assertTrue( postValue.contains( "<figure class=\"editor-align-justify\">" ) );
        assertTrue( postValue.contains( "<figure class=\"editor-align-justify editor-style-original\">" ) );
        assertTrue( postValue.contains( "src=\"media://cf09fe7a-1be9-46bb-ad84-87ba69630cb7\"" ) );
    }

    private void checkLanguageUpgrade( final Node draftNode )
    {
        final IndexConfig indexConfigBefore = draftNode.getIndexConfigDocument().getConfigForPath( PropertyPath.from( "language" ) );
        final List<String> languages = draftNode.getIndexConfigDocument().getAllTextConfig().getLanguages();

        assertEquals( IndexConfig.NGRAM, indexConfigBefore );

        assertEquals( 1, languages.size() );
        assertEquals( "no", languages.get( 0 ) );
    }

    private void checkPageFlatteningUpgradeFragment( final Node node )
    {
        final PropertyTree nodeData = node.data();
        assertTrue( nodeData.hasProperty( "components" ) );
        assertFalse( nodeData.hasProperty( "fragment" ) );

        final Iterable<PropertySet> components = nodeData.getSets( "components" );
        final PropertySet partComponent = components.iterator().next();
        assertEquals( "part", partComponent.getString( "type" ) );
        assertEquals( "/", partComponent.getString( "path" ) );
        final PropertySet partComponentData = partComponent.getSet( "part" );
        assertEquals( "com.enonic.app.superhero:meta", partComponentData.getString( "descriptor" ) );
        assertNotNull( partComponentData.getSet( "config" ).getSet( "com-enonic-app-superhero" ).getSet( "meta" ) );
    }

    private Path createIncompatibleDump( final String dumpName )
        throws Exception
    {
        final URI oldDumpUri = getClass().
            getResource( "/dumps/dump-6-15-5" ).
            toURI();
        final Path oldDumpFile = Path.of( oldDumpUri );
        final Path tmpDumpFile = this.temporaryFolder.resolve( dumpName );
        FileUtils.copyDirectoryRecursively( oldDumpFile, tmpDumpFile );
        return tmpDumpFile;
    }

    private void verifyBinaries( final Node node, final Node updatedNode, final NodeVersionQueryResult versions )
    {
        versions.getNodeVersionsMetadata().forEach( ( version ) -> verifyVersionBinaries( node, updatedNode, version ) );
    }

    private void verifyVersionBinaries( final Node node, final Node updatedNode, final NodeVersionMetadata version )
    {
        final NodeVersion storedNode = nodeService.getByNodeVersionKey( version.getNodeVersionKey() );

        storedNode.getAttachedBinaries().forEach( entry -> assertNotNull(
            this.nodeService.getBinary( version.getNodeId(), version.getNodeVersionId(), entry.getBinaryReference() ) ) );

        if ( version.getNodeVersionId().equals( node.getNodeVersionId() ) )
        {
            assertEquals( node.getAttachedBinaries(), storedNode.getAttachedBinaries() );
        }
        else if ( version.getNodeVersionId().equals( updatedNode.getNodeVersionId() ) )
        {
            assertEquals( updatedNode.getAttachedBinaries(), storedNode.getAttachedBinaries() );
        }
    }

    private SystemLoadResult dumpDeleteAndLoad( final boolean clearBlobStore )
    {
        return dumpDeleteAndLoad( clearBlobStore, true );
    }

    private SystemLoadResult dumpDeleteAndLoad( final boolean clearBlobStore, final boolean includeVersions )
    {
        final SystemDumpParams params = SystemDumpParams.create().
            dumpName( "myTestDump" ).
            includeVersions( includeVersions ).
            includeBinaries( true ).
            build();

        return dumpDeleteAndLoad( clearBlobStore, params );
    }

    private SystemLoadResult dumpDeleteAndLoad( final boolean clearBlobStore, final SystemDumpParams params )
    {
        doDump( params );

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

        this.deleteSystemIndices();

        SystemRepoInitializer.create().
            setIndexServiceInternal( indexServiceInternal ).
            setRepositoryService( repositoryService ).
            setNodeStorageService( storageService ).
            build().
            initialize();

        final SystemLoadResult result = doLoad();

        refresh();

        return result;
    }

    private SystemLoadResult doLoad()
    {
        return this.dumpService.load( SystemLoadParams.create().
            dumpName( "myTestDump" ).
            includeVersions( true ).
            build() );
    }

    private void doDump( final SystemDumpParams params )
    {
        this.dumpService.dump( params );
    }

    private Node updateNode( final Node node )
    {
        return updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( ( n ) -> n.data.setInstant( "timestamp", Instant.now() ) ).
            build() );
    }

    private void deleteSystemIndices()
    {
        IndexNameResolver.resolveSearchIndexNames( SystemConstants.SYSTEM_REPO.getId(), SystemConstants.SYSTEM_REPO.getBranches() ).
            forEach( this.indexServiceInternal::deleteIndices );
        this.indexServiceInternal.deleteIndices( IndexNameResolver.resolveBranchIndexName( SystemConstants.SYSTEM_REPO.getId() ) );
        this.indexServiceInternal.deleteIndices( IndexNameResolver.resolveVersionIndexName( SystemConstants.SYSTEM_REPO.getId() ) );
        this.indexServiceInternal.deleteIndices( IndexNameResolver.resolveCommitIndexName( SystemConstants.SYSTEM_REPO.getId() ) );

    }
}
