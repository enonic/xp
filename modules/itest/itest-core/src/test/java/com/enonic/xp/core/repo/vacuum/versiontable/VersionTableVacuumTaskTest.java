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
import com.enonic.xp.repo.impl.vacuum.versiontable.VersionTableVacuumTask;
import com.enonic.xp.vacuum.VacuumTaskResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionTableVacuumTaskTest
    extends AbstractNodeTest
{
    private VersionTableVacuumTask task;

    // "0" makes more human sense, but clock is not monotonic.
    // Negative threshold queries versions timestamps in future, so nearly created nodes guaranteed to be found.
    private static final long NEGATIVE_AGE_THRESHOLD_MILLIS = -Duration.of( 1, ChronoUnit.HOURS ).toMillis();

    // Number of versions created on fresh setup.
    private static int SYSTEM_NODES_COUNT = 7;

    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();

        this.task = new VersionTableVacuumTask();
        this.task.setNodeService( this.nodeService );
        this.task.setRepositoryService( this.repositoryService );
        this.task.setVersionService( this.versionService );
        this.task.setBlobStore( this.blobStore );
    }

    @Test
    void delete_node_deletes_versions()
    {
        // Do enough updates to go over the default batch-size
        final int updates = 1500;
        final int expectedVersionCount = updates + 1;

        final Node node1 = createNode( NodePath.ROOT, "node1" );
        updateNode( node1.id(), updates );
        doDeleteNode( node1.id() );

        assertVersions( node1.id(), expectedVersionCount );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().
            ageThreshold( NEGATIVE_AGE_THRESHOLD_MILLIS ).
            build() ) );
        refresh();

        assertEquals( updates + 8, result.getProcessed() );
        //Old version of CMS repository entry is also delete. Remove +1 when config to specify repository is implemented
        assertEquals( expectedVersionCount + 1, result.getDeleted() );

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

        assertEquals( 8, result.getProcessed() );
        //Old version of CMS repository entry is also delete. Set to 1 when config to specify repository is implemented
        assertEquals( 2, result.getDeleted() );
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

        assertEquals( 8, result.getProcessed() );
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
