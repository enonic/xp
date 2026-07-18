package com.enonic.xp.core.node;

import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.storage.spi.BranchEntryRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Phase 1 Gate C new storage-only itest (BUILD-PHASE-1.md, Gate 0's proposed list #1):
 * exercises {@code NodeStore#getChildren} directly -- paginated (from/size), asserting
 * order and count without touching {@code NodeQuery}/{@code NodeSearchService} at all.
 * <p>
 * <b>nodb-only by design, not by gap:</b> {@code NodeStore#getChildren} has no default-mode
 * (ES) implementation to fall back to -- ES's storage index has never supported a
 * path-prefix children query (children listing has always gone through
 * {@code NodeSearchIndex}/{@code FindNodeIdsByParentCommand} for every backend, ES
 * included; see the SPI method's own javadoc and BUILD-PHASE-1.md's Gate 0 finding). A test
 * that "directly exercises the SPI method" therefore cannot be meaningfully dual-mode: this
 * class skips (via {@link Assumptions}, not a failure) outside nodb mode. The command-level
 * equivalent that DOES run in both modes -- and additionally cross-checks storage/search
 * agreement in nodb mode -- is {@link FindNodeIdsByParentStorageTest}.
 */
class GetChildrenByPathTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "NodeStore#getChildren has no ES-mode implementation by design" );
        createDefaultRootNode();
    }

    @Test
    void childrenAreOrderedByPath()
    {
        for ( final String name : List.of( "charlie", "alfa", "bravo" ) )
        {
            createNode( NodePath.ROOT, name );
        }

        final List<BranchEntryRecord> children = nodeStore.getChildren( testRepoId, WS_DEFAULT, "/", 0, 10, null );

        assertEquals( List.of( "/alfa", "/bravo", "/charlie" ), children.stream().map( BranchEntryRecord::nodePath ).toList() );
    }

    @Test
    void paginationSlicesTheOrderedResult()
    {
        for ( final String name : List.of( "n0", "n1", "n2", "n3", "n4" ) )
        {
            createNode( NodePath.ROOT, name );
        }

        final List<BranchEntryRecord> page = nodeStore.getChildren( testRepoId, WS_DEFAULT, "/", 2, 2, null );

        assertEquals( List.of( "/n2", "/n3" ), page.stream().map( BranchEntryRecord::nodePath ).toList() );
    }

    @Test
    void nestedChildrenAreScopedToTheirParent()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        createNode( parent.path(), "child-a" );
        createNode( parent.path(), "child-b" );
        createNode( NodePath.ROOT, "sibling" );

        final List<BranchEntryRecord> rootChildren = nodeStore.getChildren( testRepoId, WS_DEFAULT, "/", 0, 10, null );
        assertEquals( List.of( "/parent", "/sibling" ), rootChildren.stream().map( BranchEntryRecord::nodePath ).toList() );

        final List<BranchEntryRecord> parentChildren = nodeStore.getChildren( testRepoId, WS_DEFAULT, "/parent", 0, 10, null );
        assertEquals( List.of( "/parent/child-a", "/parent/child-b" ),
                      parentChildren.stream().map( BranchEntryRecord::nodePath ).toList() );
    }
}
