package com.enonic.xp.core.node;

import java.util.UUID;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.InternalContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 3 Gate C (nodb/BUILD-PHASE-3.md): two nodes whose index-config/ACL segments
 * serialize to IDENTICAL bytes (both are plain siblings under root, no explicit
 * permissions/index-config given -- {@code CreateNodeCommand} resolves both to the
 * parent's inherited permissions and the shared default index-config document, per
 * {@code CreateNodeCommand#getAccessControlEntries}) must land as exactly ONE row in the
 * tenant's {@code payload} table -- {@code PayloadStore#putPayload}'s
 * {@code ON CONFLICT (hash) DO NOTHING} dedup, exercised here through XP's real create
 * path rather than directly against the engine (nodb/engine's own {@code EngineStoreTest}
 * already covers the engine-level mechanics; this proves XP's write path actually relies
 * on it end to end).
 */
class PayloadDedupTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "payload-table dedup ground truth has no ES-mode equivalent" );
        createDefaultRootNode();
    }

    @Test
    void twoNodesWithIdenticalIndexConfigAndAcl_shareOnePayloadRowEach()
    {
        final String tenantId = NodbTestCluster.get().tenantForClass( this.getClass() ).tenantId();

        final Node nodeA = createNode( CreateNodeParams.create()
                                            .parent( NodePath.ROOT )
                                            .name( "dedup-a-" + UUID.randomUUID() )
                                            .data( uniqueData() )
                                            .build() );
        final NodeVersionKey keyA = getVersionKey( nodeA );

        // Baseline AFTER node A but BEFORE node B: captured rather than assumed to be a
        // fixed number, because createDefaultRootNode() (this class's own @BeforeEach)
        // ALSO inherits/shares this exact ACL and default index-config content -- the
        // root node's own version already references the same hash, so the true count
        // at this point is "however many versions share it so far" (root + A), not
        // necessarily 1. The delta after creating B is what actually proves dedup did
        // not drop a reference, independent of how many other versions happen to share
        // the same content.
        final long indexConfigCountAfterA =
            NodbPayloadGroundTruth.countVersionsReferencingHash( tenantId, "index_config_hash", keyA.getIndexConfigBlobKey().toString() );
        final long aclCountAfterA =
            NodbPayloadGroundTruth.countVersionsReferencingHash( tenantId, "acl_hash", keyA.getAccessControlBlobKey().toString() );

        final Node nodeB = createNode( CreateNodeParams.create()
                                            .parent( NodePath.ROOT )
                                            .name( "dedup-b-" + UUID.randomUUID() )
                                            .data( uniqueData() )
                                            .build() );
        final NodeVersionKey keyB = getVersionKey( nodeB );

        // The node-data segment must differ (unique content per node)...
        assertTrue( !keyA.getNodeBlobKey().equals( keyB.getNodeBlobKey() ), "node-data hashes must differ -- content is unique per node" );

        // ...but index-config and ACL are identical content, so the SAME hash both times.
        assertEquals( keyA.getIndexConfigBlobKey(), keyB.getIndexConfigBlobKey(), "identical index-config content must hash identically" );
        assertEquals( keyA.getAccessControlBlobKey(), keyB.getAccessControlBlobKey(), "identical ACL content must hash identically" );

        // Ground truth: exactly ONE payload row for that shared hash, not two, even
        // though (at least) two separate node creates each attempted to write it.
        assertEquals( 1L, NodbPayloadGroundTruth.countPayloadRows( tenantId, keyA.getIndexConfigBlobKey().toString() ),
                      "shared index-config content must dedup to a single payload row" );
        assertEquals( 1L, NodbPayloadGroundTruth.countPayloadRows( tenantId, keyA.getAccessControlBlobKey().toString() ),
                      "shared ACL content must dedup to a single payload row" );

        // ...while node B's version genuinely ADDED a new reference to that one row
        // (proves the dedup didn't silently drop B's reference -- it's genuinely shared,
        // not overwritten): the count after B must be exactly one more than after A.
        assertEquals( indexConfigCountAfterA + 1,
                      NodbPayloadGroundTruth.countVersionsReferencingHash( tenantId, "index_config_hash",
                                                                            keyA.getIndexConfigBlobKey().toString() ),
                      "node B's version must add exactly one new reference to the shared index-config row" );
        assertEquals( aclCountAfterA + 1,
                      NodbPayloadGroundTruth.countVersionsReferencingHash( tenantId, "acl_hash", keyA.getAccessControlBlobKey().toString() ),
                      "node B's version must add exactly one new reference to the shared ACL row" );
    }

    private NodeVersionKey getVersionKey( final Node node )
    {
        final NodeVersion version = this.storageService.getVersion( node.getNodeVersionId(), InternalContext.from( ctxDefault() ) );
        return version.getNodeVersionKey();
    }

    private static PropertyTree uniqueData()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "unique", UUID.randomUUID().toString() );
        return data;
    }
}
