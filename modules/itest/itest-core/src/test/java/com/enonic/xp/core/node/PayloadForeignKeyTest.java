package com.enonic.xp.core.node;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.storage.spi.NodeSegments;
import com.enonic.xp.storage.spi.PayloadSegment;
import com.enonic.xp.storage.spi.VersionRecord;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 3 Gate C (nodb/BUILD-PHASE-3.md), work order item 2's optional FK surfacing
 * check (#10b): a contrived version write whose {@code node_data_hash} references a
 * payload that was never stored -- and, critically, was never even MENTIONED among the
 * segments this same write carries (so {@code WriteService}'s {@code needPayload}
 * pre-check, which only inspects {@code request.payloads()}, cannot catch it) -- must be
 * rejected by the re-added {@code node_version} FK, surfaced as {@code FAILED_PRECONDITION}
 * at the gRPC boundary (see {@code NodeStoreService#mapSqlException}'s SQLSTATE 23503
 * mapping) rather than silently committing a dangling reference.
 * <p>
 * Constructed directly against the public {@code NodeStore} SPI (no proto/gRPC-stub
 * access needed -- itest-core has no compile-time dependency on {@code io.grpc}, a
 * transitive-only dependency of {@code core-storage-nodb-client}): {@link VersionRecord}'s
 * hash fields are copied verbatim into the wire message by
 * {@code RecordMapper.toProtoVersion} independently of whatever {@link NodeSegments}
 * rides alongside it in the same call -- a caller bug (this test's contrivance) can
 * therefore reference a hash the segments never actually established, which is exactly
 * the scenario the FK exists to catch structurally. The failure is asserted by message
 * content rather than an {@code io.grpc.StatusRuntimeException} type check for the same
 * classpath reason: {@code NodbStatusMapper#translate}'s un-mapped-status branch embeds
 * the literal gRPC status code name into {@code NodbClientException}'s own message
 * (documented, stable production behavior), which is sufficient to prove FAILED_PRECONDITION
 * specifically reached the client, not merely "some error occurred".
 */
class PayloadForeignKeyTest
    extends AbstractNodeTest
{
    @Test
    void versionReferencingAPayloadHashNeverWrittenInTheSameBatch_isRejectedWithFailedPrecondition()
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "the payload FK is nodb-specific -- default mode has no such constraint" );

        final byte[] nodeDataBytes = ( "node-data-" + UUID.randomUUID() ).getBytes();
        final byte[] indexConfigBytes = ( "index-config-" + UUID.randomUUID() ).getBytes();
        final byte[] aclBytes = ( "acl-" + UUID.randomUUID() ).getBytes();

        final String indexConfigHash = BlobKey.sha256( ByteSource.wrap( indexConfigBytes ) ).toString();
        final String aclHash = BlobKey.sha256( ByteSource.wrap( aclBytes ) ).toString();

        // The segments this write actually carries -- all three are genuine, will be
        // inserted into `payload` fine on their own.
        final NodeSegments segments = new NodeSegments( new PayloadSegment( BlobKey.sha256( ByteSource.wrap( nodeDataBytes ) ).toString(),
                                                                             nodeDataBytes ),
                                                          new PayloadSegment( indexConfigHash, indexConfigBytes ),
                                                          new PayloadSegment( aclHash, aclBytes ) );

        // ...but the VERSION deliberately references a DIFFERENT, bogus node-data hash --
        // never inlined, never hash-only-referenced anywhere in `segments` above, so
        // WriteService's pre-check never flags it; only the FK on INSERT can catch this.
        final String bogusNodeDataHash = "sha256:" + "0".repeat( 64 );

        final VersionRecord version =
            new VersionRecord( "fk-test-version-" + UUID.randomUUID(), "fk-test-node-" + UUID.randomUUID(), "/fk-test-" + UUID.randomUUID(),
                                Instant.now(), bogusNodeDataHash, indexConfigHash, aclHash, List.of(), null, null );

        final Exception thrown = assertThrows( Exception.class, () -> nodeStore.storeVersion( testRepoId, version, segments ) );

        assertTrue( mentionsFailedPrecondition( thrown ),
                    "expected the node_version payload FK violation to surface as FAILED_PRECONDITION somewhere in the cause chain of: " +
                        thrown );
    }

    private static boolean mentionsFailedPrecondition( final Throwable thrown )
    {
        for ( Throwable t = thrown; t != null; t = t.getCause() )
        {
            if ( t.getMessage() != null && t.getMessage().contains( "FAILED_PRECONDITION" ) )
            {
                return true;
            }
        }
        return false;
    }
}
