package com.enonic.xp.core.node;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 1 Gate C new storage-only itest (BUILD-PHASE-1.md, Gate 0's proposed list #4):
 * exercises {@code NodeStore#existsBranchEntry} end-to-end (via {@code BranchServiceImpl#exists},
 * the one production caller) -- a genuine behavioral difference from
 * {@code getBranchEntry() != null} (no full-row fetch: {@code BranchStore.existsByNodeId}'s
 * {@code SELECT 1 ... LIMIT 1} on the nodb side, a plain existence probe on the ES side),
 * not a duplicate assertion of an existing test. Pure storage: no query, no search index.
 */
class ExistsBranchEntryTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();
    }

    @Test
    void existsIsTrueAfterCreateAndFalseForAnUnknownId()
    {
        final Node node = createNode( NodePath.ROOT, "exists-me" );
        final InternalContext context = InternalContext.from( ctxDefault() );

        assertTrue( branchService.exists( node.id(), context ) );
        assertFalse( branchService.exists( NodeId.from( "no-such-node-id" ), context ) );
    }

    @Test
    void existsIsFalseAfterDelete()
    {
        final Node node = createNode( NodePath.ROOT, "delete-me" );
        final InternalContext context = InternalContext.from( ctxDefault() );
        assertTrue( branchService.exists( node.id(), context ) );

        // Deliberately NOT DeleteNodeCommand: it queries NodeSearchService internally to
        // enumerate descendants (BUILD-PHASE-1.md's Gate 0 finding) -- deleting the
        // branch entry directly via NodeStore keeps this test genuinely storage-only.
        nodeStore.deleteBranchEntries( testRepoId, WS_DEFAULT, List.of( node.id().toString() ) );

        assertFalse( branchService.exists( node.id(), context ) );
    }
}
