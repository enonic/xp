package com.enonic.xp.core.repo.vacuum.versiontable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repo.impl.vacuum.versiontable.VersionTableCleanupTask;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionTableCleanupTaskTest
    extends AbstractNodeTest
{
    private VersionTableCleanupTask task;

    // "0" makes more human sense, but clock is not monotonic.
    // Negative threshold queries versions timestamps in future, so nearly created nodes guaranteed to be found.
    private static final long NEGATIVE_AGE_THRESHOLD_MILLIS = -Duration.of( 1, ChronoUnit.HOURS ).toMillis();

    // Number of versions created on fresh setup.
    private int SYSTEM_NODES_COUNT = 7;

    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();

        this.task = new VersionTableCleanupTask();
        this.task.setNodeService( this.nodeService );
        this.task.setRepositoryService( this.repositoryService );
        this.task.setVersionService( this.versionService );
    }

    @Test
    void delete_node_deletes_versions()
    {
        // Do enough updates to go over the default batch-size
        final int updates = 1000;

        final Node node1 = createNode( NodePath.ROOT, "node1" );
        updateNode( node1.id(), updates );
        doDeleteNode( node1.id() );

        int versionsCount = 1 + updates;

        assertVersions( node1.id(), versionsCount );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().
            ageThreshold( NEGATIVE_AGE_THRESHOLD_MILLIS ).
            build() ) );
        refresh();

        assertAll( () -> assertEquals( versionsCount + SYSTEM_NODES_COUNT, result.getProcessed(), "All versions should be processed" ),
                   () -> assertEquals( versionsCount, result.getDeleted(), "All version updates should be processed" ),
                   () -> assertEquals( SYSTEM_NODES_COUNT, result.getInUse(), "Some versions are in use" ),
                   () -> assertEquals( 0, result.getFailed(), "Some version cleanup failed" ) );

        assertVersions( node1.id(), 0 );
    }

    @Test
    void version_deleted_in_all_branches()
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        pushNodes( NodeIds.from( node1.id() ), CTX_OTHER.getBranch() );
        refresh();

        this.nodeService.deleteById( node1.id() );
        CTX_OTHER.runWith( () -> this.nodeService.deleteById( node1.id() ) );

        refresh();
        assertVersions( node1.id(), 1 );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().
            ageThreshold( NEGATIVE_AGE_THRESHOLD_MILLIS ).
            build() ) );
        refresh();

        assertEquals( SYSTEM_NODES_COUNT + 1, result.getProcessed() );
        assertEquals( 1, result.getDeleted() );
        assertVersions( node1.id(), 0 );
    }

    @Test
    void version_not_deleted_in_all_branches()
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        pushNodes( NodeIds.from( node1.id() ), CTX_OTHER.getBranch() );
        refresh();

        this.nodeService.deleteById( node1.id() );

        refresh();
        assertVersions( node1.id(), 1 );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().
            ageThreshold( NEGATIVE_AGE_THRESHOLD_MILLIS ).
            build() ) );
        refresh();

        assertEquals( SYSTEM_NODES_COUNT + 1, result.getProcessed() );
        assertEquals( 0, result.getDeleted() );

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

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().
            ageThreshold( Duration.of( 1, ChronoUnit.HOURS ).toMillis() ).
            build() ) );
        refresh();

        assertEquals( 0, result.getProcessed() );
        assertEquals( 0, result.getDeleted() );

        assertVersions( node1.id(), 3 );
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
}
