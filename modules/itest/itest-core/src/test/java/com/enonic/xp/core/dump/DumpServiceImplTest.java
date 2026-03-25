package com.enonic.xp.core.dump;

import java.nio.file.Files;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.app.VirtualAppConstants;
import com.enonic.xp.core.impl.audit.AuditLogConstants;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.dump.SystemDumpListener;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.node.ApplyVersionAttributesParams;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.GetNodeVersionsResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.PushNodeParams;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.config.RepoConfigurationDynamic;
import com.enonic.xp.repo.impl.dump.DumpServiceImpl;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.repository.RepositoryCreator;
import com.enonic.xp.repo.impl.repository.RepositoryEntry;
import com.enonic.xp.repo.impl.repository.RepositorySettings;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.scheduler.SchedulerConstants;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GenericValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class DumpServiceImplTest
    extends AbstractNodeTest
{
    private static final AtomicLong UPDATE_COUNTER = new AtomicLong();

    private DumpServiceImpl dumpService;

    public DumpServiceImplTest()
    {
        super( true );
    }

    @BeforeEach
    void setUp()
    {
        final RepoConfigurationDynamic repoConfiguration = new RepoConfigurationDynamic();
        repoConfiguration.activate( Map.of( "dumps.dir", temporaryFolder.toString() ) );
        this.dumpService =
            new DumpServiceImpl( eventPublisher, BLOB_STORE, this.nodeService, this.repositoryEntryService, this.nodeRepositoryService,
                                 this.storageService, this.branchService, repoConfiguration );
    }

    @Test
    void zip_unzip()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );
        updateNode( node );
        updateNode( node );
        updateNode( node );

        refresh();

        final SystemDumpResult systemDumpResult = NodeHelper.runAsAdmin(
            () -> this.dumpService.dump( SystemDumpParams.create().includeVersions( true ).dumpName( "testDump" ).build() ) );

        // 4 of node, 1 of root
        assertEquals( 5, systemDumpResult.get( testRepoId ).getVersions() );
        final BranchDumpResult branchDumpResult = systemDumpResult.get( testRepoId ).get( WS_DEFAULT );

        assertEquals( 2, branchDumpResult.getSuccessful() );

        NodeHelper.runAsAdmin(
            () -> this.dumpService.load( SystemLoadParams.create().includeVersions( true ).dumpName( "testDump" ).build() ) );

        final Repositories newRepos = NodeHelper.runAsAdmin( this::doListRepositories );

        assertEquals( RepositoryIds.from( testRepoId, RepositoryId.from( "system-repo" ), RepositoryId.from( "system.auditlog" ),
                                          RepositoryId.from( "system.scheduler" ), RepositoryId.from( "system.app" ) ), newRepos.getIds() );
    }

    @Test
    void admin_role_required()
    {
        assertThrows( RepoDumpException.class, () -> {
            doDump( SystemDumpParams.create().dumpName( "testDump" ).build() );

        } );
    }

    @Test
    void existing_repositories_deleted()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );

        NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create().dumpName( "testDump" ).build() ) );

        NodeHelper.runAsAdmin(
            () -> doDump( SystemDumpParams.create().dumpName( "myTestDump" ).includeVersions( true ).includeBinaries( true ).build() ) );

        final Node toBeDeleted = createNode( NodePath.ROOT, "should-be-deleted" );

        NodeHelper.runAsAdmin( this::doLoad );

        // assertNotNull( getNode( node.id() ) );
        assertNull( getNode( toBeDeleted.id() ) );
    }


    @Test
    void obsolete_repository_deleted()
    {
        final RepositoryEntry newRepoInsideDump =
            NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "new-repo-inside-dump" ), false ) );

        NodeHelper.runAsAdmin( () -> doDump( SystemDumpParams.create().dumpName( "myTestDump" ).build() ) );

        final RepositoryEntry newRepoOutsideDump =
            NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "new-repo-outside-dump" ), false ) );

        final Repositories oldRepos = NodeHelper.runAsAdmin( this::doListRepositories );

        NodeHelper.runAsAdmin( this::doLoad );

        final Repositories newRepos = NodeHelper.runAsAdmin( this::doListRepositories );

        assertEquals( 7, oldRepos.getIds().getSize() );
        assertEquals( 6, newRepos.getIds().getSize() );

        assertThat( newRepos ).map( Repository::getId ).contains( newRepoInsideDump.getId() ).doesNotContain( newRepoOutsideDump.getId() );
    }

    @Test
    void transient_repository_deleted()
    {
        final RepositoryEntry transientRepo =
            NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "transient-repo" ), true ) );

        NodeHelper.runAsAdmin( () -> doDump( SystemDumpParams.create().dumpName( "myTestDump" ).build() ) );

        NodeHelper.runAsAdmin( this::doLoad );

        final Repositories newRepos = NodeHelper.runAsAdmin( this::doListRepositories );

        assertThat( newRepos ).map( Repository::getId ).doesNotContain( transientRepo.getId() );
    }

    @Test
    void repositories_loaded()
    {
        final Repositories repositoriesBefore = NodeHelper.runAsAdmin( this::doListRepositories );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );

        final Repositories repositoriesAfter = NodeHelper.runAsAdmin( this::doListRepositories );

        assertEquals( repositoriesBefore.getIds(), repositoriesAfter.getIds() );
    }

    @Test
    void dumpAndLoad()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );

        final SystemDumpResult systemDumpResult =
            NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create().dumpName( "testDump" ).build() ) );

        final BranchDumpResult result = systemDumpResult.get( testRepoId ).get( WS_DEFAULT );
        assertNotNull( result );
        assertEquals( 2, result.getSuccessful() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );

        final Node currentStoredNode = this.nodeService.getById( node.id() );
        assertEquals( node.getTimestamp(), currentStoredNode.getTimestamp() );
        assertEquals( node.id(), currentStoredNode.id() );
        assertEquals( node.path(), currentStoredNode.path() );
        assertEquals( node.getManualOrderValue(), currentStoredNode.getManualOrderValue() );
        assertEquals( node.getAttachedBinaries(), currentStoredNode.getAttachedBinaries() );
        assertEquals( node.getIndexConfigDocument(), currentStoredNode.getIndexConfigDocument() );
        assertEquals( node.getChildOrder(), currentStoredNode.getChildOrder() );
        assertEquals( node.getNodeType(), currentStoredNode.getNodeType() );
        assertEquals( node.data(), currentStoredNode.data() );
    }

    @Test
    void dumpAndLoadWithAttachments()
    {
        NodeHelper.runAsAdmin( () -> {
            final RepositoryEntry newRepo = NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "new-repo" ), false ) );

            final PropertyTree data = newRepo.getData().copy();
            data.addBinaryReference( "attachmentName", BinaryReference.from( "image.png" ) );

            final RepositoryEntry updatedEntry = RepositoryEntry.create()
                .id( newRepo.getId() )
                .settings( newRepo.getSettings() )
                .data( data )
                .attachments( newRepo.getAttachments() )
                .transientFlag( newRepo.isTransient() )
                .modelVersion( newRepo.getModelVersion() )
                .build();

            final BinaryAttachments binaryAttachments = BinaryAttachments.create()
                .add( new BinaryAttachment( BinaryReference.from( "image.png" ), ByteSource.wrap( "attachmentName".getBytes() ) ) )
                .build();

            repositoryEntryService.updateRepositoryEntry( updatedEntry, binaryAttachments );
        } );

        NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create().dumpName( "testDump" ).build() ) );

        NodeHelper.runAsAdmin( () -> {
            dumpDeleteAndLoad();

            final AttachedBinaries attachedBinaries =
                repositoryEntryService.getRepositoryEntry( RepositoryId.from( "new-repo" ) ).getAttachments();

            assertEquals( 1, attachedBinaries.getSize() );
            assertNotNull( attachedBinaries.getByBinaryReference( BinaryReference.from( "image.png" ) ) );
        } );

    }

    @Test
    void dump_long_filename()
    {
        final String nodeName = "this-is-my-node-with-very-long-filename-more-than-100-characters-yes-it-has-to-be-very-long-indeed-sir";
        final Node node = createNode( NodePath.ROOT, nodeName );

        final SystemDumpResult systemDumpResult =
            NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create().dumpName( "testDump" ).build() ) );

        final BranchDumpResult result = systemDumpResult.get( testRepoId ).get( WS_DEFAULT );
        assertNotNull( result );
        assertEquals( 2, result.getSuccessful() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );

        final Node storedNode = this.nodeService.getById( node.id() );
        assertNotNull( storedNode );
        assertEquals( nodeName, storedNode.name().toString() );
    }

    @Test
    void verify_result()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );
        updateNode( node );
        updateNode( node );
        updateNode( node );

        refresh();

        final SystemDumpResult systemDumpResult =
            NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create().dumpName( "testDump" ).build() ) );

        // 4 of node, 1 of root
        assertEquals( 5, systemDumpResult.get( testRepoId ).getVersions() );
        final BranchDumpResult branchDumpResult = systemDumpResult.get( testRepoId ).get( WS_DEFAULT );

        assertEquals( 2, branchDumpResult.getSuccessful() );
    }

    @Test
    void root_node_gets_correct_properties()
    {
        final AccessControlList newRepoACL =
            AccessControlList.create().add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allowAll().build() ).build();

        final RepositoryEntry newRepo = NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "my-new-repo" ), false ) );

        final Context newContext = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( newRepo.getId() )
            .branch( RepositoryConstants.MASTER_BRANCH )
            .build();

        newContext.runWith( () -> createNode( NodePath.ROOT, "my-node" ) );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );

        final Node loadedRootNode = newContext.callWith( () -> getNodeById( NodeId.ROOT ) );

        assertEquals( newRepoACL, loadedRootNode.getPermissions() );
    }

    @Test
    void branch_created_if_missing()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );

        NodeHelper.runAsAdmin( () -> {
            doDump( SystemDumpParams.create().dumpName( "testDump" ).build() );

            this.dumpService.load( SystemLoadParams.create().dumpName( "testDump" ).build() );

            assertNotNull( this.nodeService.getById( node.id() ) );
        } );
    }

    @Test
    void versions()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );
        final Node updatedNode = updateNode( node );
        final Node currentNode = updateNode( updatedNode );
        refresh();

        final GetNodeVersionsResult versionsBeforeDump =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );

        refresh();

        final GetNodeVersionsResult versionsAfterLoad =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );
        assertEquals( 3, versionsAfterLoad.getTotalHits() );

        final Node currentStoredNode = this.nodeService.getById( node.id() );
        assertEquals( currentNode.data(), currentStoredNode.data() );
        assertEquals( getOrderedTimestamps( versionsBeforeDump ), getOrderedTimestamps( versionsAfterLoad ) );
    }

    @Test
    void same_version_in_different_branches_not_duplicated()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );
        final NodeIds ids = NodeIds.from( node.id() );
        this.nodeService.push( PushNodeParams.create().ids( ids ).target( WS_OTHER ).build() );
        refresh();

        final GetNodeVersionsResult versionsBeforeDump =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );

        assertEquals( 1, versionsBeforeDump.getTotalHits() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );

        refresh();

        final GetNodeVersionsResult versionsAfterLoad =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );

        versionsAfterLoad.getNodeVersions().forEach( ( e ) -> System.out.println( e.getNodeVersionId() + " - " + e.getTimestamp() ) );

        assertEquals( 1, versionsAfterLoad.getTotalHits() );
    }

    @Test
    void different_versions_in_different_branches_not_duplicated()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );
        updateNode( node );
        final NodeIds ids = NodeIds.from( node.id() );
        this.nodeService.push( PushNodeParams.create().ids( ids ).target( WS_OTHER ).build() );
        updateNode( node );
        updateNode( node );
        refresh();

        final GetNodeVersionsResult versionsBeforeDump =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );
        refresh();

        final GetNodeVersionsResult versionsAfterLoad =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );

        assertEquals( versionsBeforeDump.getTotalHits(), versionsAfterLoad.getTotalHits() );
    }

    @Test
    void active_versions_after_load()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );
        final NodeIds ids = NodeIds.from( node.id() );
        this.nodeService.push( PushNodeParams.create().ids( ids ).target( WS_OTHER ).build() );
        updateNode( node );
        refresh();

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );
        refresh();

        final GetActiveNodeVersionsResult activeVersions = this.nodeService.getActiveVersions(
            GetActiveNodeVersionsParams.create().branches( Branches.from( WS_DEFAULT, WS_OTHER ) ).nodeId( node.id() ).build() );

        final Node defaultBranchNode = ctxDefault().callWith( () -> this.nodeService.getById( node.id() ) );
        final Node otherBranchNode = ctxOther().callWith( () -> this.nodeService.getById( node.id() ) );

        final Map<Branch, NodeVersion> activeVersionsMap = activeVersions.getNodeVersions();

        assertEquals( 2, activeVersionsMap.size() );
        assertEquals( defaultBranchNode.getNodeVersionId(), activeVersionsMap.get( WS_DEFAULT ).getNodeVersionId() );
        assertEquals( otherBranchNode.getNodeVersionId(), activeVersionsMap.get( WS_OTHER ).getNodeVersionId() );
    }

    @Test
    void active_versions_in_versions_list()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );
        final NodeIds ids = NodeIds.from( node.id() );
        this.nodeService.push( PushNodeParams.create().ids( ids ).target( WS_OTHER ).build() );
        updateNode( node );
        refresh();

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );
        refresh();

        final GetActiveNodeVersionsResult activeVersions = this.nodeService.getActiveVersions(
            GetActiveNodeVersionsParams.create().branches( Branches.from( WS_DEFAULT, WS_OTHER ) ).nodeId( node.id() ).build() );

        final Map<Branch, NodeVersion> activeVersionsMap = activeVersions.getNodeVersions();

        final GetNodeVersionsResult versionsAfterLoad =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );

        activeVersionsMap.values()
            .forEach( key -> assertTrue( versionsAfterLoad.getNodeVersions().getAllVersionIds().contains( key.getNodeVersionId() ) ) );
    }

    @Test
    void version_ids_should_stay_the_same_if_no_changes()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );

        renameNode( node.id(), "renamed" );

        final NodeIds ids = NodeIds.from( node.id() );
        this.nodeService.push( PushNodeParams.create().ids( ids ).target( WS_OTHER ).build() );
        updateNode( node );
        refresh();

        final GetNodeVersionsResult versionsBeforeLoad =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );
        refresh();

        final GetNodeVersionsResult versionsAfterLoad =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );

        final NodeVersionIds versionIdsBeforeLoad = versionsBeforeLoad.getNodeVersions().getAllVersionIds();
        final NodeVersionIds versionIdsAfterLoad = versionsAfterLoad.getNodeVersions().getAllVersionIds();

        assertEquals( versionIdsBeforeLoad, versionIdsAfterLoad );
    }

    private TreeSet<Instant> getOrderedTimestamps( final GetNodeVersionsResult result )
    {
        TreeSet<Instant> timestamps = new TreeSet<>();
        result.getNodeVersions().forEach( version -> timestamps.add( version.getTimestamp() ) );
        return timestamps;
    }

    @Test
    void binaries()
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "binaryRef" );
        data.addBinaryReference( "myBinary", binaryRef );

        final Node node = createNode( CreateNodeParams.create()
                                          .parent( NodePath.ROOT )
                                          .name( "myNode" )
                                          .data( data )
                                          .attachBinary( binaryRef, ByteSource.wrap( "this is binary data".getBytes() ) )
                                          .build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );

        final Node currentStoredNode = this.nodeService.getById( node.id() );
        assertEquals( node.getAttachedBinaries(), currentStoredNode.getAttachedBinaries() );
    }

    @Test
    void limit_number_of_versions()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );

        for ( int i = 0; i < 10; i++ )
        {
            updateNode( node );
        }

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( SystemDumpParams.create().dumpName( "myTestDump" ).maxVersions( 5 ).build() ) );

        final GetNodeVersionsResult versionsAfterLoad =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).size( -1 ).build() );

        assertEquals( 6, versionsAfterLoad.getNodeVersions().getSize() );
    }

    @Test
    void number_of_versions_in_other_repo()
    {
        final RepositoryEntry myRepo = NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "myrepo" ), false ) );

        final Context myRepoContext = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( myRepo.getId() )
            .branch( RepositoryConstants.MASTER_BRANCH )
            .build();

        final Node myNode = myRepoContext.callWith( () -> createNode( NodePath.ROOT, "my-node" ) );
        myRepoContext.runWith( () -> updateNode( myNode ) );
        myRepoContext.runWith( () -> updateNode( myNode ) );
        myRepoContext.runWith( () -> updateNode( myNode ) );

        final SystemLoadResult dumpResult =
            NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( SystemDumpParams.create().dumpName( "myTestDump" ).build() ) );

        final RepoLoadResult repoLoadResult = getRepoLoadResult( dumpResult, myRepo.getId() );

        final VersionsLoadResult versionsLoadResult = repoLoadResult.getVersionsLoadResult();
        assertNotNull( versionsLoadResult );
        // One for root, 4 for myNode
        assertEquals( 5, versionsLoadResult.getSuccessful() );
    }

    private RepoLoadResult getRepoLoadResult( final SystemLoadResult result, final RepositoryId repositoryId )
    {

        for ( final RepoLoadResult next : result )
        {
            if ( next.getRepositoryId().equals( repositoryId ) )
            {
                return next;
            }
        }
        return null;
    }

    @Test
    void binaries_in_versions()
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "binaryRef" );
        data.addBinaryReference( "myBinary", binaryRef );

        final Node node = createNode( CreateNodeParams.create()
                                          .parent( NodePath.ROOT )
                                          .name( "myNode" )
                                          .data( data )
                                          .attachBinary( binaryRef, ByteSource.wrap( "this is binary data".getBytes() ) )
                                          .build() );

        final BinaryReference binaryRef2 = BinaryReference.from( "anotherBinary" );

        final Node updatedNode = updateNode( UpdateNodeParams.create()
                                                 .id( node.id() )
                                                 .editor( n -> n.data.setBinaryReference( "myBinary", binaryRef2 ) )
                                                 .attachBinary( binaryRef2, ByteSource.wrap( "anotherBinary".getBytes() ) )
                                                 .build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad() );

        final GetNodeVersionsResult versions = this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );
        assertEquals( 2, versions.getNodeVersions().getSize() );

        verifyBinaries( node, updatedNode, versions );
    }

    @Test
    void dumpAndLoadListener()
    {
        createNode( NodePath.ROOT, "my-node" );

        final SystemDumpListener systemDumpListener = mock( SystemDumpListener.class );
        NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create()
                                                                .dumpName( "myTestDump" )
                                                                .includeVersions( true )
                                                                .includeBinaries( true )
                                                                .listener( systemDumpListener )
                                                                .build() ) );

        Mockito.verify( systemDumpListener ).dumpingBranch( testRepoId, WS_DEFAULT, 2 );
        Mockito.verify( systemDumpListener ).dumpingBranch( testRepoId, WS_OTHER, 1 );
        Mockito.verify( systemDumpListener ).dumpingBranch( AuditLogConstants.AUDIT_LOG_REPO_ID, AUDIT_LOG_BRANCH, 1 );
        Mockito.verify( systemDumpListener ).dumpingBranch( SchedulerConstants.SCHEDULER_REPO_ID, SCHEDULER_BRANCH, 1 );
        Mockito.verify( systemDumpListener ).dumpingBranch( SystemConstants.SYSTEM_REPO_ID, SystemConstants.BRANCH_SYSTEM, 7 );
        Mockito.verify( systemDumpListener, Mockito.times( 13 ) ).nodeDumped();

        final SystemLoadListener systemLoadListener = mock( SystemLoadListener.class );
        NodeHelper.runAsAdmin( () -> this.dumpService.load(
            SystemLoadParams.create().dumpName( "myTestDump" ).includeVersions( true ).listener( systemLoadListener ).build() ) );

        Mockito.verify( systemLoadListener ).loadingBranch( testRepoId, ctxDefault().getBranch(), 2L );
        Mockito.verify( systemLoadListener ).loadingVersions( testRepoId );
        Mockito.verify( systemLoadListener ).loadingBranch( testRepoId, WS_OTHER, 1L );
        Mockito.verify( systemLoadListener ).loadingVersions( testRepoId );
        Mockito.verify( systemLoadListener ).loadingBranch( AuditLogConstants.AUDIT_LOG_REPO_ID, AUDIT_LOG_BRANCH, 1L );
        Mockito.verify( systemLoadListener ).loadingBranch( SchedulerConstants.SCHEDULER_REPO_ID, SCHEDULER_BRANCH, 1L );
        Mockito.verify( systemLoadListener ).loadingVersions( AuditLogConstants.AUDIT_LOG_REPO_ID );
        Mockito.verify( systemLoadListener ).loadingVersions( SchedulerConstants.SCHEDULER_REPO_ID );
        Mockito.verify( systemLoadListener ).loadingBranch( SystemConstants.SYSTEM_REPO_ID, SystemConstants.BRANCH_SYSTEM, 7L );
        Mockito.verify( systemLoadListener ).loadingVersions( SystemConstants.SYSTEM_REPO_ID );
        Mockito.verify( systemLoadListener, Mockito.times( 25 ) ).entryLoaded();
    }

    @Test
    void skip_versions()
    {
        final Node node = createNode( NodePath.ROOT, "my-node" );
        final Node updatedNode = updateNode( node );
        final Node currentNode = updateNode( updatedNode );
        refresh();

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( false ) );

        refresh();

        final GetNodeVersionsResult versionsAfterLoad =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );
        assertEquals( 1, versionsAfterLoad.getTotalHits() );

        final Node currentStoredNode = this.nodeService.getById( node.id() );
        assertEquals( currentNode.data(), currentStoredNode.data() );
    }

    @Test
    void version_attributes_preserved_in_dump()
    {
        // Create a node with multiple versions
        final Node node = createNode( NodePath.ROOT, "my-node-with-attributes" );
        final Node updatedNode = updateNode( node );
        refresh();

        // Get the version IDs
        final GetNodeVersionsResult versions = this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );
        assertEquals( 2, versions.getTotalHits() );

        // Apply attributes to the first version
        final NodeVersionId firstVersionId = versions.getNodeVersions().get( 0 ).getNodeVersionId();
        final Attributes testAttributes = Attributes.create()
            .attribute( "testKey1", GenericValue.stringValue( "testValue1" ) )
            .attribute( "testKey2", GenericValue.numberValue( 42 ) )
            .attribute( "testKey3", GenericValue.booleanValue( true ) )
            .build();

        this.nodeService.applyVersionAttributes(
            ApplyVersionAttributesParams.create().nodeVersionId( firstVersionId ).addAttributes( testAttributes ).build() );

        refresh();

        // Verify attributes were applied
        final GetNodeVersionsResult versionsWithAttrs =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );
        final NodeVersion nodeVersion = versionsWithAttrs.getNodeVersions()
            .stream()
            .filter( v -> v.getNodeVersionId().equals( firstVersionId ) )
            .findFirst()
            .orElseThrow();

        assertEquals( "testValue1", nodeVersion.getAttributes().get( "testKey1" ).asString() );
        assertEquals( 42, nodeVersion.getAttributes().get( "testKey2" ).asInteger() );
        assertTrue( nodeVersion.getAttributes().get( "testKey3" ).asBoolean() );

        // Dump and load with versions included
        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( true ) );

        refresh();

        // Verify attributes are preserved after dump/restore
        final GetNodeVersionsResult versionsAfterLoad =
            this.nodeService.getVersions( GetNodeVersionsParams.create().nodeId( node.id() ).build() );
        assertEquals( 2, versionsAfterLoad.getTotalHits() );

        final NodeVersion restoredVersion = versionsAfterLoad.getNodeVersions()
            .stream()
            .filter( v -> v.getNodeVersionId().equals( firstVersionId ) )
            .findFirst()
            .orElseThrow();

        // Verify all attributes were preserved
        assertThat( restoredVersion.getAttributes() ).isNotNull();
        assertEquals( "testValue1", restoredVersion.getAttributes().get( "testKey1" ).asString() );
        assertEquals( 42, restoredVersion.getAttributes().get( "testKey2" ).asInteger() );
        assertTrue( restoredVersion.getAttributes().get( "testKey3" ).asBoolean() );
    }

    private void verifyBinaries( final Node node, final Node updatedNode, final GetNodeVersionsResult versions )
    {
        versions.getNodeVersions().forEach( ( version ) -> verifyVersionBinaries( node, updatedNode, version ) );
    }

    private void verifyVersionBinaries( final Node node, final Node updatedNode, final NodeVersion version )
    {
        final NodeStoreVersion storedNode =
            this.storageService.getNodeVersion( version.getNodeVersionKey(), InternalContext.from( ContextAccessor.current() ) );

        storedNode.attachedBinaries()
            .forEach( entry -> assertNotNull(
                this.nodeService.getBinary( version.getNodeId(), version.getNodeVersionId(), entry.getBinaryReference() ) ) );

        if ( version.getNodeVersionId().equals( node.getNodeVersionId() ) )
        {
            assertEquals( node.getAttachedBinaries(), storedNode.attachedBinaries() );
        }
        else if ( version.getNodeVersionId().equals( updatedNode.getNodeVersionId() ) )
        {
            assertEquals( updatedNode.getAttachedBinaries(), storedNode.attachedBinaries() );
        }
    }

    private SystemLoadResult dumpDeleteAndLoad()
    {
        return dumpDeleteAndLoad( true );
    }

    private SystemLoadResult dumpDeleteAndLoad( final boolean includeVersions )
    {
        final SystemDumpParams params =
            SystemDumpParams.create().dumpName( "myTestDump" ).includeVersions( includeVersions ).includeBinaries( true ).build();

        return dumpDeleteAndLoad( params );
    }

    private SystemLoadResult dumpDeleteAndLoad( final SystemDumpParams params )
    {
        doDump( params );

        doListRepositories().stream()
            .map( Repository::getId )
            .filter( Predicate.isEqual( SystemConstants.SYSTEM_REPO_ID )
                         .or( Predicate.isEqual( AuditLogConstants.AUDIT_LOG_REPO_ID ) )
                         .or( Predicate.isEqual( SchedulerConstants.SCHEDULER_REPO_ID ) )
                         .or( Predicate.isEqual( VirtualAppConstants.VIRTUAL_APP_REPO_ID ) )
                         .negate() )
            .forEach( this::doDeleteRepository );

        return doLoad();
    }

    private SystemLoadResult doLoad()
    {
        return this.dumpService.load( SystemLoadParams.create().dumpName( "myTestDump" ).includeVersions( true ).build() );
    }

    private void doDump( final SystemDumpParams params )
    {
        this.dumpService.dump( params );
    }

    private RepositoryEntry doCreateRepository( final RepositoryId repositoryId, boolean transientFlag )
    {
        final AccessControlList permissions =
            AccessControlList.create().add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allowAll().build() ).build();

        return new RepositoryCreator( nodeRepositoryService, storageService, repositoryEntryService ).createRepository(
            CreateRepositoryParams.create()
                .repositoryId( repositoryId )
                .transientFlag( transientFlag )
                .rootPermissions( permissions )
                .build(), RepositorySettings.create().build(), AttachedBinaries.empty(), false );
    }

    private void doDeleteRepository( final RepositoryId repositoryId )
    {
        this.repositoryEntryService.deleteRepositoryEntry( repositoryId );
        this.nodeRepositoryService.delete( repositoryId );

        this.storageService.invalidate();
    }

    private Repositories doListRepositories()
    {
        final Repositories.Builder repositories = Repositories.create();

        repositoryEntryService.findRepositoryEntryIds()
            .stream()
            .map( repositoryEntryService::getRepositoryEntry )
            .filter( Objects::nonNull )
            .map( entry -> Repository.create()
                .id( entry.getId() )
                .branches( Branches.from( RepositoryConstants.MASTER_BRANCH ) )
                .data( entry.getData().copy() )
                .attachments( entry.getAttachments() )
                .transientFlag( entry.isTransient() )
                .build() )
            .forEach( repositories::add );

        return repositories.build();
    }

    private Node updateNode( final Node node )
    {
        return updateNode( UpdateNodeParams.create().id( node.id() ).editor( ( n ) -> {
            // Guarantee new node version is created. Without it update is ignored because node is not changed.
            // N.B. Writing "current time" (even System#nanoTime) does not have the same guarantee due low timer resolution.
            n.data.setLong( "update", UPDATE_COUNTER.incrementAndGet() );
        } ).build() );
    }

    @Test
    void partial_load_single_repository()
    {
        final RepositoryEntry repo1 = NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "repo-to-load" ), false ) );
        final RepositoryEntry repo2 = NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "repo-to-keep" ), false ) );

        final Context repo1Context = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( repo1.getId() )
            .branch( RepositoryConstants.MASTER_BRANCH )
            .build();

        final Context repo2Context = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( repo2.getId() )
            .branch( RepositoryConstants.MASTER_BRANCH )
            .build();

        final Node nodeInRepo1 = repo1Context.callWith( () -> createNode( NodePath.ROOT, "node-in-repo1" ) );
        final Node nodeInRepo2 = repo2Context.callWith( () -> createNode( NodePath.ROOT, "node-in-repo2" ) );

        NodeHelper.runAsAdmin( () -> doDump( SystemDumpParams.create().dumpName( "partialDump" ).build() ) );

        // Delete node from repo1 after dump - this will be restored by partial load
        repo1Context.runWith( () -> nodeService.delete( DeleteNodeParams.create().nodeId( nodeInRepo1.id() ).build() ) );
        repo1Context.runWith( () -> nodeService.refresh( RefreshMode.ALL ) );
        assertNull( repo1Context.callWith( () -> getNode( nodeInRepo1.id() ) ) );

        // Add new node to repo1 after dump - this should be removed by partial load
        final Node newNodeInRepo1 = repo1Context.callWith( () -> createNode( NodePath.ROOT, "new-node-in-repo1" ) );

        // Add new node to repo2 after dump - this should survive partial load
        final Node newNodeInRepo2 = repo2Context.callWith( () -> createNode( NodePath.ROOT, "new-node-in-repo2" ) );

        // Partial load only repo1
        NodeHelper.runAsAdmin( () -> this.dumpService.load( SystemLoadParams.create()
                                                                .dumpName( "partialDump" )
                                                                .includeVersions( true )
                                                                .repositories( RepositoryIds.from( repo1.getId() ) )
                                                                .build() ) );

        // Verify repo1 was restored from dump (original node exists, new node removed)
        final Node restoredNodeInRepo1 = repo1Context.callWith( () -> getNode( nodeInRepo1.id() ) );
        assertNotNull( restoredNodeInRepo1 );
        assertNull( repo1Context.callWith( () -> getNode( newNodeInRepo1.id() ) ) );

        // Verify repo2 was NOT touched (both old and new nodes exist)
        final Node existingNodeInRepo2 = repo2Context.callWith( () -> getNode( nodeInRepo2.id() ) );
        assertNotNull( existingNodeInRepo2 );
        final Node newNodeStillExists = repo2Context.callWith( () -> getNode( newNodeInRepo2.id() ) );
        assertNotNull( newNodeStillExists );
    }

    @Test
    void partial_load_preserves_repositories_outside_list()
    {
        final RepositoryEntry repoInsideList =
            NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "repo-inside-list" ), false ) );
        final RepositoryEntry repoOutsideList =
            NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "repo-outside-list" ), false ) );

        NodeHelper.runAsAdmin( () -> doDump( SystemDumpParams.create().dumpName( "partialDump2" ).build() ) );

        final Repositories reposBefore = NodeHelper.runAsAdmin( this::doListRepositories );

        // Partial load only repoInsideList
        NodeHelper.runAsAdmin( () -> this.dumpService.load( SystemLoadParams.create()
                                                                .dumpName( "partialDump2" )
                                                                .includeVersions( true )
                                                                .repositories( RepositoryIds.from( repoInsideList.getId() ) )
                                                                .build() ) );

        final Repositories reposAfter = NodeHelper.runAsAdmin( this::doListRepositories );

        // Both repositories should still exist
        assertThat( reposAfter ).map( Repository::getId ).contains( repoInsideList.getId(), repoOutsideList.getId() );
        assertEquals( reposBefore.getIds().getSize(), reposAfter.getIds().getSize() );
    }

    @Test
    void partial_load_rejects_system_repositories()
    {
        NodeHelper.runAsAdmin( () -> doDump( SystemDumpParams.create().dumpName( "partialDump3" ).build() ) );

        assertThrows( RuntimeException.class, () -> NodeHelper.runAsAdmin( () -> this.dumpService.load( SystemLoadParams.create()
                                                                                                            .dumpName( "partialDump3" )
                                                                                                            .includeVersions( true )
                                                                                                            .repositories(
                                                                                                                RepositoryIds.from(
                                                                                                                    SystemConstants.SYSTEM_REPO_ID ) )
                                                                                                            .build() ) ) );
    }

    @Test
    void partial_load_branches_count_only_for_requested_repos()
    {
        NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "repo-a" ), false ) );
        NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "repo-b" ), false ) );

        NodeHelper.runAsAdmin( () -> doDump( SystemDumpParams.create().dumpName( "branchCountDump" ).build() ) );

        final SystemLoadListener listener = mock( SystemLoadListener.class );

        NodeHelper.runAsAdmin( () -> this.dumpService.load( SystemLoadParams.create()
                                                                .dumpName( "branchCountDump" )
                                                                .includeVersions( true )
                                                                .repositories( RepositoryIds.from( RepositoryId.from( "repo-a" ) ) )
                                                                .listener( listener )
                                                                .build() ) );

        // Only repo-a branches should be counted (1 branch: master), not repo-b
        Mockito.verify( listener ).totalBranches( 1L );
    }

    @Test
    void partial_dump_rejects_system_repo()
    {
        assertThrows( RepoDumpException.class, () -> NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create()
                                                                                                             .dumpName(
                                                                                                                 "partialDumpSystemRepo" )
                                                                                                             .repositories(
                                                                                                                 RepositoryIds.from(
                                                                                                                     SystemConstants.SYSTEM_REPO_ID ) )
                                                                                                             .build() ) ) );
    }

    @Test
    void partial_dump_rejects_multiple_system_repositories()
    {
        assertThrows( RepoDumpException.class, () -> NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create()
                                                                                                             .dumpName(
                                                                                                                 "partialDumpMultipleSysRepos" )
                                                                                                             .repositories(
                                                                                                                 RepositoryIds.from(
                                                                                                                     SystemConstants.SYSTEM_REPO_ID,
                                                                                                                     RepositoryId.from(
                                                                                                                         "system.auditlog" ),
                                                                                                                     RepositoryId.from(
                                                                                                                         "system.scheduler" ) ) )
                                                                                                             .build() ) ) );
    }

    @Test
    void partial_dump_rejects_system_repo_mixed_with_regular()
    {
        NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "regular-repo" ), false ) );
        assertThrows( RepoDumpException.class, () -> NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create()
                                                                                                             .dumpName( "partialDumpMixed" )
                                                                                                             .repositories(
                                                                                                                 RepositoryIds.from(
                                                                                                                     RepositoryId.from(
                                                                                                                         "regular-repo" ),
                                                                                                                     SystemConstants.SYSTEM_REPO_ID ) )
                                                                                                             .build() ) ) );
    }


    @Test
    void partial_dump_excludes_system_repo_from_result()
    {
        NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "my-repo" ), false ) );

        final SystemDumpResult dumpResult = NodeHelper.runAsAdmin( () -> this.dumpService.dump( SystemDumpParams.create()
                                                                                                    .dumpName(
                                                                                                        "partialDumpExcludesSystemRepo" )
                                                                                                    .repositories( RepositoryIds.from(
                                                                                                        RepositoryId.from( "my-repo" ) ) )
                                                                                                    .build() ) );

        assertNull( dumpResult.get( SystemConstants.SYSTEM_REPO_ID ) );
        assertNotNull( dumpResult.get( RepositoryId.from( "my-repo" ) ) );

        // Verify load without specifying repositories detects it as partial dump and rejects it
        assertThrows( RepoLoadException.class, () -> NodeHelper.runAsAdmin( () -> this.dumpService.load(
            SystemLoadParams.create().dumpName( "partialDumpExcludesSystemRepo" ).includeVersions( true ).build() ) ) );

        // Verify partial load with specific repositories works
        NodeHelper.runAsAdmin( () -> this.dumpService.load( SystemLoadParams.create()
                                                                .dumpName( "partialDumpExcludesSystemRepo" )
                                                                .includeVersions( true )
                                                                .repositories( RepositoryIds.from( RepositoryId.from( "my-repo" ) ) )
                                                                .build() ) );
    }

    @Test
    void full_load_rejects_partial_dump()
    {
        NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "my-repo" ), false ) );

        NodeHelper.runAsAdmin( () -> doDump( SystemDumpParams.create()
                                                 .dumpName( "partialDumpFullLoadTest" )
                                                 .repositories( RepositoryIds.from( RepositoryId.from( "my-repo" ) ) )
                                                 .build() ) );

        assertThrows( RepoLoadException.class, () -> NodeHelper.runAsAdmin( () -> this.dumpService.load(
            SystemLoadParams.create().dumpName( "partialDumpFullLoadTest" ).includeVersions( true ).build() ) ) );
    }

    @Test
    void partial_dump_includes_transient_repository()
    {
        final RepositoryEntry transientRepo =
            NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "transient-repo" ), true ) );

        final Context transientRepoContext = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( transientRepo.getId() )
            .branch( RepositoryConstants.MASTER_BRANCH )
            .build();

        final Node nodeInTransientRepo = transientRepoContext.callWith( () -> createNode( NodePath.ROOT, "transient-node" ) );

        // Partial dump explicitly requesting the transient repo
        final SystemDumpResult dumpResult = NodeHelper.runAsAdmin( () -> {
            final SystemDumpParams params = SystemDumpParams.create()
                .dumpName( "partialTransientDump" )
                .repositories( RepositoryIds.from( transientRepo.getId() ) )
                .includeVersions( true )
                .includeBinaries( true )
                .build();
            return this.dumpService.dump( params );
        } );

        // Verify transient repo is included in the dump result
        final RepoDumpResult transientRepoDumpResult = dumpResult.get( transientRepo.getId() );
        assertNotNull( transientRepoDumpResult );

        final BranchDumpResult branchResult = transientRepoDumpResult.get( RepositoryConstants.MASTER_BRANCH );
        assertNotNull( branchResult );
        assertEquals( 2, branchResult.getSuccessful() ); // root + transient-node

        // Partial load and verify data is restored
        transientRepoContext.runWith( () -> nodeService.delete( DeleteNodeParams.create().nodeId( nodeInTransientRepo.id() ).build() ) );
        transientRepoContext.runWith( () -> nodeService.refresh( RefreshMode.ALL ) );
        assertNull( transientRepoContext.callWith( () -> getNode( nodeInTransientRepo.id() ) ) );

        NodeHelper.runAsAdmin( () -> this.dumpService.load( SystemLoadParams.create()
                                                                .dumpName( "partialTransientDump" )
                                                                .includeVersions( true )
                                                                .repositories( RepositoryIds.from( transientRepo.getId() ) )
                                                                .build() ) );

        final Node restoredNode = transientRepoContext.callWith( () -> getNode( nodeInTransientRepo.id() ) );
        assertNotNull( restoredNode );
        assertEquals( nodeInTransientRepo.id(), restoredNode.id() );
    }

    @Test
    void partial_load_skips_repo_not_found_in_dump()
        throws Exception
    {
        NodeHelper.runAsAdmin( () -> doCreateRepository( RepositoryId.from( "real-repo" ), false ) );

        NodeHelper.runAsAdmin( () -> doDump( SystemDumpParams.create().dumpName( "missingEntryDump" ).build() ) );

        // Create a fake repo directory in the dump so getRepositories() includes it,
        // but there is no corresponding entry in system-repo within the dump
        Files.createDirectories( temporaryFolder.resolve( "missingEntryDump" ).resolve( "meta" ).resolve( "ghost-repo" ) );

        final SystemLoadResult result = NodeHelper.runAsAdmin( () -> this.dumpService.load( SystemLoadParams.create()
                                                                                                .dumpName( "missingEntryDump" )
                                                                                                .includeVersions( true )
                                                                                                .repositories( RepositoryIds.from(
                                                                                                    RepositoryId.from( "ghost-repo" ) ) )
                                                                                                .build() ) );

        // ghost-repo has no entry in dump's system-repo, so nothing should be loaded
        assertThat( result ).isEmpty();
    }
}
