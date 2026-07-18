package com.enonic.xp.core.node;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.node.RefreshCommand;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RefreshCommandTest
    extends AbstractNodeTest
{

    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();
    }

    @Test
    void refresh_non_existing_repository()
    {
        // Phase 1 Gate C (nodb/BUILD-PHASE-1.md): RepositoryStorageAdmin#refresh is a
        // documented no-op for the nodb backend (Postgres transactional visibility is
        // strictly stronger than an ES refresh -- see NodbRepositoryStorageAdmin's
        // javadoc) -- deliberately no RPC call at all, so it can never detect a
        // non-existing repository the way IndexServiceInternalImpl#refresh does. This is
        // a genuine, anticipated semantic difference (nodb.proto's reconciliation notes),
        // not a gap in this fixture -- skip rather than fail in nodb mode.
        Assumptions.assumeFalse( NodbTestCluster.isEnabled(),
                                  "refresh() is a no-op for the nodb backend and cannot detect a non-existing repository" );

        final RepositoryId nonExistingRepoId = RepositoryId.from( "non-existing-repo" );

        ContextBuilder.from( ctxDefault() )
            .repositoryId( nonExistingRepoId )
            .build()
            .runWith( () -> assertThrows( IndexException.class, () -> RefreshCommand.create()
                .repositoryStorageAdmin( this.repositoryStorageAdmin )
                .nodeSearchIndex( nodeSearchIndex )
                .refreshMode( RefreshMode.ALL )
                .build()
                .execute() ) );
    }

    @Test
    void refresh_existing_repository()
    {
        assertDoesNotThrow(
            () -> RefreshCommand.create().repositoryStorageAdmin( this.repositoryStorageAdmin )
                .nodeSearchIndex( nodeSearchIndex ).refreshMode( RefreshMode.ALL ).build().execute() );
    }
}
