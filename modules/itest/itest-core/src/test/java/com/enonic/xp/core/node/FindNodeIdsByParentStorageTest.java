package com.enonic.xp.core.node;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.storage.spi.BranchEntryRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Phase 1 Gate C new storage-only itest (BUILD-PHASE-1.md, Gate 0's proposed list #2 --
 * "or an nodb-mode variant of the existing test", the alternative Gate 0 explicitly
 * offered to modifying {@code FindNodeIdsByParentCommand}'s production routing).
 * <p>
 * {@code FindNodeIdsByParentCommand} always queries {@code NodeSearchService} (true for
 * every backend, ES included -- BUILD-PHASE-1.md's Gate 0 finding), so it is NOT given a
 * storage-side path here (that would be new Gate A/B-scale production surface, out of
 * this gate's scope -- see the final report). Instead this test proves the thing that
 * actually matters for hybrid mode's correctness: in nodb mode, the SEARCH-side listing
 * (via {@link #findByParent}, unchanged, still ES-backed) and the STORAGE-side listing
 * (the new {@code NodeStore#getChildren}, Gate C's SPI addition) agree on the same node
 * set -- i.e. hybrid mode's two halves (NoDB storage + ES search, both fed by the same
 * writes) are not drifting apart. In default (ES) mode there is no separate storage-side
 * listing to cross-check (see {@link GetChildrenByPathTest}'s javadoc), so this class
 * simply asserts {@code findByParent} itself is correct there -- a plain regression check,
 * genuinely green in both modes.
 */
class FindNodeIdsByParentStorageTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();
    }

    @Test
    void searchAndStorageAgreeOnChildren()
    {
        final Node child1 = createNode( NodePath.ROOT, "alfa" );
        final Node child2 = createNode( NodePath.ROOT, "bravo" );
        final Node child3 = createNode( NodePath.ROOT, "charlie" );
        refresh();

        final FindNodesByParentResult searchResult = findByParent( NodePath.ROOT );
        final Set<NodeId> searchIds = Set.copyOf( searchResult.getNodeIds().getSet() );
        assertEquals( Set.of( child1.id(), child2.id(), child3.id() ), searchIds );

        if ( NodbTestCluster.isEnabled() )
        {
            final List<BranchEntryRecord> storageChildren = nodeStore.getChildren( testRepoId, WS_DEFAULT, "/", 0, 10, null );
            final Set<NodeId> storageIds =
                storageChildren.stream().map( e -> NodeId.from( e.nodeId() ) ).collect( Collectors.toUnmodifiableSet() );

            assertEquals( searchIds, storageIds, "hybrid mode: storage-side (nodb) and search-side (ES) children listings must agree" );
        }
    }
}
