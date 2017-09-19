package com.enonic.xp.core.repo.vacuum.versiontable;

import java.security.SecureRandom;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

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

import static org.junit.Assert.*;

public class VersionTableCleanupTaskTest
    extends AbstractNodeTest
{
    private final SecureRandom random = new SecureRandom();

    private VersionTableCleanupTask task;

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        createDefaultRootNode();

        this.task = new VersionTableCleanupTask();
        this.task.setNodeService( this.nodeService );
        this.task.setRepositoryService( this.repositoryService );
        this.task.setVersionService( this.versionService );
    }

    @Test
    public void delete_node_deletes_versions()
        throws Exception
    {
        // Do enough updates to go over the default batch-size
        final int updates = 1000;

        final Node node1 = createNode( NodePath.ROOT, "node1" );
        updateNode( node1.id(), updates );
        doDeleteNode( node1.id() );

        assertVersions( node1.id(), updates + 1 );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().
            ageThreshold( 0 ).
            build() ) );
        refresh();

        assertEquals( updates + 8, result.getProcessed() );
        assertEquals( updates + 1, result.getDeleted() );

        assertVersions( node1.id(), 0 );
    }

    @Test
    public void version_deleted_in_all_branches()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        pushNodes( NodeIds.from( node1.id() ), CTX_OTHER.getBranch() );
        refresh();

        this.nodeService.deleteById( node1.id() );
        CTX_OTHER.runWith( () -> this.nodeService.deleteById( node1.id() ) );

        refresh();
        assertVersions( node1.id(), 1 );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().
            ageThreshold( 0 ).
            build() ) );
        refresh();

        assertEquals( 8, result.getProcessed() );
        assertEquals( 1, result.getDeleted() );
        assertVersions( node1.id(), 0 );
    }

    @Test
    public void version_not_deleted_in_all_branches()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        pushNodes( NodeIds.from( node1.id() ), CTX_OTHER.getBranch() );
        refresh();

        this.nodeService.deleteById( node1.id() );

        refresh();
        assertVersions( node1.id(), 1 );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().
            ageThreshold( 0 ).
            build() ) );
        refresh();

        assertEquals( 8, result.getProcessed() );
        assertEquals( 0, result.getDeleted() );

        assertVersions( node1.id(), 1 );
    }

    @Test
    public void age_threshold()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        updateNode( node1.id(), 2 );

        doDeleteNode( node1.id() );
        refresh();
        assertVersions( node1.id(), 3 );

        final VacuumTaskResult result = NodeHelper.runAsAdmin( () -> this.task.execute( VacuumTaskParams.create().
            ageThreshold( 100_000 ).
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

        assertEquals( "Wrong number of versions found", versions, result.getTotalHits() );
    }

    private void updateNode( final NodeId nodeId, final int updates )
    {
        for ( int i = 0; i < updates; i++ )
        {
            updateNode( UpdateNodeParams.create().
                id( nodeId ).
                editor( node -> {
                    node.data.setInstant( "now", Instant.now() );
                    node.data.setLong( "random", random.nextLong() );
                } ).
                build() );
        }
    }
}
