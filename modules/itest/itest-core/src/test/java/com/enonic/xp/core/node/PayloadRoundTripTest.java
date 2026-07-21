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
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositorySegmentUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Phase 3 Gate C (nodb/BUILD-PHASE-3.md): node payloads (node-data/index-config/ACL) are
 * NATIVE in NoDB's own {@code payload} table -- no hybrid-mode BlobStore hop -- exercised
 * end-to-end through XP's real {@code nodeService}/command-layer path (the fixture wires
 * this automatically since Phase 3 Gate B: {@code NodeStorageServiceImpl} hands the three
 * serialized segments to {@code NodeStore#storeNode}, and nodb mode's implementation
 * ({@code NodbNodeStore}) rides them into one {@code WriteBatch} transaction -- see
 * {@code NodbNodeStore}'s class javadoc). Nothing about the fixture itself needed to
 * change for payloads to land in NoDB: it already constructs the real Gate-B client
 * classes, and Gate B's write-path restructuring is what makes this test meaningful
 * rather than a no-op.
 */
class PayloadRoundTripTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "payload-table ground truth has no ES-mode equivalent" );
        createDefaultRootNode();
    }

    @Test
    void createReadBack_propertyTreeIdentical_andPayloadRowsExistForAllThreeSegments()
    {
        final String tenantId = NodbTestCluster.get().tenantForClass( this.getClass() ).tenantId();

        final PropertyTree data = new PropertyTree();
        data.addString( "greeting", "hello-payload-" + UUID.randomUUID() );
        data.addLong( "answer", 42L );

        final Node created = createNode( CreateNodeParams.create()
                                              .parent( NodePath.ROOT )
                                              .name( "payload-round-trip-" + UUID.randomUUID() )
                                              .data( data )
                                              .build() );

        // Read back through the ordinary command path -- proves the round trip end to
        // end, not merely that SOME bytes were written somewhere.
        final Node readBack = getNodeById( created.id() );
        assertEquals( data, readBack.data(), "property tree must be byte-for-byte identical after a native-NoDB round trip" );

        // Ground truth: all three payload segments of the committed version are present
        // as rows in THIS tenant's own `payload` table (the node_version FK guarantees
        // this structurally, but assert it directly anyway per the work order).
        final NodeVersion version = this.storageService.getVersion( created.getNodeVersionId(), InternalContext.from( ctxDefault() ) );
        final NodeVersionKey key = version.getNodeVersionKey();

        assertPayloadRowExists( tenantId, key.getNodeBlobKey().toString() );
        assertPayloadRowExists( tenantId, key.getIndexConfigBlobKey().toString() );
        assertPayloadRowExists( tenantId, key.getAccessControlBlobKey().toString() );
    }

    /**
     * "File store idle for payloads" proof (work order item 1's CRITICAL check): after a
     * real node create/update in nodb mode, the shared {@link #BLOB_STORE} (the
     * file/memory {@code BlobStore} every segment used to land on pre-Phase-3) must have
     * received ZERO records for the node/index-config/ACL blob types -- every payload
     * write went to NoDB instead. Checked against the SAME repository this test's node
     * was just written into, right after the write, so the assertion cannot pass merely
     * because nothing ran yet.
     */
    @Test
    void fileBlobStoreReceivesNoPayloadSegmentWritesInNodbMode()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "field", "value-" + UUID.randomUUID() );

        final Node node =
            createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "idle-file-store-" + UUID.randomUUID() ).data( data ).build() );
        updateNode( com.enonic.xp.node.UpdateNodeParams.create()
                        .id( node.id() )
                        .editor( toBeEdited -> toBeEdited.data.addString( "extra", "changed" ) )
                        .build() );

        assertEquals( 0, BLOB_STORE.list( RepositorySegmentUtils.toSegment( testRepoId, NodeConstants.NODE_SEGMENT_LEVEL ) ).count(),
                       "file BlobStore must not receive node-data segment writes in nodb mode" );
        assertEquals( 0,
                      BLOB_STORE.list( RepositorySegmentUtils.toSegment( testRepoId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL ) ).count(),
                      "file BlobStore must not receive index-config segment writes in nodb mode" );
        assertEquals( 0,
                      BLOB_STORE.list( RepositorySegmentUtils.toSegment( testRepoId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL ) ).count(),
                      "file BlobStore must not receive ACL segment writes in nodb mode" );
    }

    private static void assertPayloadRowExists( final String tenantId, final String hash )
    {
        assertEquals( 1L, NodbPayloadGroundTruth.countPayloadRows( tenantId, hash ),
                      "expected exactly one payload row for hash " + hash + " in tenant " + tenantId );
    }
}
