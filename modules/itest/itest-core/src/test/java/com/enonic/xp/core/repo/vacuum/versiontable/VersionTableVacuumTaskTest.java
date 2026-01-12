package com.enonic.xp.core.repo.vacuum.versiontable;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.node.ApplyVersionAttributesParams;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repo.impl.vacuum.versiontable.VersionTableVacuumTask;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.vacuum.VacuumConstants;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionTableVacuumTaskTest
    extends AbstractNodeTest
{
    private VersionTableVacuumTask task;

    // "0" makes more human sense, but clock is not monotonic.
    // Negative threshold queries versions timestamps in future, so nearly created nodes guaranteed to be found.
    private static final long NEGATIVE_AGE_THRESHOLD_MILLIS = -Duration.of( 1, ChronoUnit.HOURS ).toMillis();

    VersionTableVacuumTaskTest()
    {
        super( true );
    }

    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();

        this.task =
            new VersionTableVacuumTask( this.nodeService, this.repositoryService, this.versionService, this.branchService, BLOB_STORE );
    }

    @Test
    void delete_node_deletes_versions()
    {
        final int updates = 10_000;
        final int versionsBatchSize = updates / 2; // to make sure nodes fetched in batches
        final int expectedVersionCount = updates + 1;

        final Node node1 = createNode( NodePath.ROOT, "node1" );
        updateNode( node1.id(), updates );
        doDeleteNode( node1.id() );
        refresh();

        assertVersions( node1.id(), expectedVersionCount );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().vacuumStartedAt( Instant.now() ).
            ageThreshold( NEGATIVE_AGE_THRESHOLD_MILLIS ).
            versionsBatchSize( versionsBatchSize ).
            build() ) );
        refresh();

        assertEquals( updates + 14, result.getProcessed() );
        //Old version of CMS repository entry is also delete. Remove +1 when config to specify repository is implemented
        assertEquals( expectedVersionCount + 1, result.getDeleted() );

        assertVersions( node1.id(), 0 );
    }

    @Test
    void version_deleted_in_all_branches()
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        pushNodes( WS_OTHER, node1.id() );

        doDeleteNode( node1.id() );

        ctxOther().runWith( () -> doDeleteNode( node1.id() ) );

        refresh();
        assertVersions( node1.id(), 1 );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().
            vacuumStartedAt( Instant.now() ).
            ageThreshold( NEGATIVE_AGE_THRESHOLD_MILLIS ).
            build() ) );
        refresh();

        assertEquals( 14, result.getProcessed() );
        //Old version of CMS repository entry is also delete. Set to 1 when config to specify repository is implemented
        assertEquals( 2, result.getDeleted() );
        assertVersions( node1.id(), 0 );
    }

    @Test
    void version_not_deleted_in_all_branches()
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        pushNodes( WS_OTHER, node1.id() );
        refresh();

        doDeleteNode( node1.id() );

        refresh();
        assertVersions( node1.id(), 1 );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().
            vacuumStartedAt( Instant.now() ).
            ageThreshold( NEGATIVE_AGE_THRESHOLD_MILLIS ).
            build() ) );
        refresh();

        assertEquals( 14, result.getProcessed() );
        //Old version of CMS repository entry is also delete. Set to 0 when config to specify repository is implemented
        assertEquals( 1, result.getDeleted() );

        assertVersions( node1.id(), 1 );
    }

    @Test
    void age_threshold()
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        updateNode( node1.id(), 2 );

        doDeleteNode( node1.id() );
        refresh();
        assertVersions( node1.id(), 3 );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create()
                                                                                            .vacuumStartedAt( Instant.now() )
                                                                                            .ageThreshold(
                                                                                                Duration.of( 1, ChronoUnit.HOURS )
                                                                                                    .toMillis() )
                                                                                            .build() ) );
        refresh();

        assertEquals( 0, result.getProcessed() );
        assertEquals( 0, result.getDeleted() );

        assertVersions( node1.id(), 3 );
    }

    @Test
    void testDeleteOnTransientRepository()
    {
        final RepositoryId repositoryId = RepositoryId.from( "com.test.transient" + System.currentTimeMillis() );
        final Context context = ContextBuilder.create()
            .branch( ContentConstants.BRANCH_MASTER )
            .repositoryId( repositoryId )
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.ADMIN )
                           .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
                           .build() )
            .build();

        context.callWith( () -> {
            createTransientRepo( repositoryId );

            Instant initTime = Instant.now();
            Clock clock = Clock.fixed( initTime, ZoneId.systemDefault() );

            this.task.setClock( clock );

            final Node node1 = createNode( NodePath.ROOT, "node1" );
            updateNode( node1.id(), 1 );

            refresh();
            assertVersions( node1.id(), 2 );

            Instant newTime = initTime.plus( 3, ChronoUnit.MINUTES );
            this.task.setClock( Clock.fixed( newTime, ZoneId.systemDefault() ) );

            final VacuumTaskResult result =
                NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().vacuumStartedAt( Instant.now() ).build() ) );

            refresh();

            assertEquals( 3, result.getProcessed() );
            assertEquals( 1, result.getDeleted() );

            assertVersions( node1.id(), 1 );

            return null;
        } );
    }

    private void createTransientRepo( final RepositoryId repositoryId )
    {
        final AccessControlList rootPermissions =
            AccessControlList.of( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() );

        this.repositoryService.createRepository(
            CreateRepositoryParams.create().repositoryId( repositoryId ).rootPermissions( rootPermissions ).transientFlag( true ).build() );

        refresh();
    }

    private void assertVersions( final NodeId nodeId, final int versions )
    {
        final NodeVersionQueryResult result = this.nodeService.findVersions( NodeVersionQuery.create().
            nodeId( nodeId ).
            size( 0 ).
            build() );

        assertEquals( versions, result.getTotalHits(), "Wrong number of versions found" );
    }

    private void updateNode( final NodeId nodeId, final int updates )
    {
        for ( int i = 0; i < updates; i++ )
        {
            // Every update should introduce a change into the node
            final long value = i;
            updateNode( UpdateNodeParams.create().
                id( nodeId ).
                editor( node -> node.data.setLong( "someValue", value ) ).
                build() );
        }
    }

    @Test
    void version_with_preventVacuum_attribute_not_deleted()
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        refresh();
        
        // Get the first version ID
        final NodeVersionQueryResult versionsResult = this.nodeService.findVersions( NodeVersionQuery.create()
            .nodeId( node1.id() )
            .size( 1 )
            .build() );
        final NodeVersionId firstVersionId = versionsResult.getNodeVersionMetadatas().first().getNodeVersionId();
        
        // Mark the first version to prevent vacuum (just presence of attribute matters)
        NodeHelper.runAsAdmin( () -> {
            this.nodeService.applyVersionAttributes( ApplyVersionAttributesParams.create()
                .nodeVersionId( firstVersionId )
                .addAttributes( Attributes.create()
                    .attribute( VacuumConstants.PREVENT_VACUUM_ATTRIBUTE, GenericValue.stringValue( "" ) )
                    .build() )
                .build() );
            return null;
        } );
        
        // Update the node to create more versions
        updateNode( node1.id(), 2 );
        
        // Delete the node
        doDeleteNode( node1.id() );
        refresh();
        
        // Should have 3 versions total (1 initial + 2 updates, delete doesn't create version)
        assertVersions( node1.id(), 3 );
        
        // Run vacuum
        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create()
            .vacuumStartedAt( Instant.now() )
            .ageThreshold( NEGATIVE_AGE_THRESHOLD_MILLIS )
            .build() ) );
        refresh();
        
        // The protected version should be skipped
        assertEquals( 1, result.getSkipped(), "One version should have been skipped" );
        
        // Only the first version (protected) should remain
        assertVersions( node1.id(), 1 );
        
        // Verify it's the first version that remains
        final NodeVersionQueryResult remainingVersions = this.nodeService.findVersions( NodeVersionQuery.create()
            .nodeId( node1.id() )
            .size( 1 )
            .build() );
        assertEquals( firstVersionId, remainingVersions.getNodeVersionMetadatas().first().getNodeVersionId(),
            "The protected version should remain" );
    }
}
