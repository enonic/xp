package com.enonic.xp.core.node;

import java.util.UUID;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.nodb.NodbTenant;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.storage.nodb.NodbBinaryBlobStore;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Phase 3 Gate C (nodb/BUILD-PHASE-3.md), work order item 2's dual-tenant payload
 * isolation check -- the same security property {@link NodbBinaryCrossTenantIsolationTest}
 * proves for binaries (Phase 2 Gate C), one segment kind lower: node/index-config/ACL
 * payloads. {@code payload} is a per-tenant-SCHEMA table (nodb/schema/schema.sql, no
 * tenant column at all -- isolation comes from which schema the row physically lives in,
 * see {@code com.enonic.nodb.engine.TenantContext}), so two tenants storing the identical
 * bytes get two entirely independent rows, one per schema, and tenant B's own gRPC
 * channel/token has no way to address tenant A's row even by the identical hash string.
 * <p>
 * Exercised through {@link NodbBinaryBlobStore} directly (its documented defensive
 * write-side {@code PutPayload} path, same class/pattern the binary isolation test uses
 * for the binary segment) rather than the full {@code nodeService} stack, since a second,
 * independently-provisioned tenant (via {@link NodbTestCluster#freshTenant()}) has no
 * bootstrapped repository/root node of its own -- the segment-level {@code BlobStore} API
 * is the correct, minimal layer for this check, matching the binary isolation test's own
 * scope.
 */
class PayloadDualTenantIsolationTest
    extends AbstractNodeTest
{
    @Test
    void twoTenantsStoringIdenticalBytes_getIndependentPayloadRows_andCannotReadEachOthers()
        throws java.io.IOException
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "payload tenant isolation has no ES-mode equivalent" );

        final NodbTenant tenantA = NodbTestCluster.get().freshTenant();
        final NodbTenant tenantB = NodbTestCluster.get().freshTenant();
        assertNotEquals( tenantA.tenantId(), tenantB.tenantId() );

        try
        {
            final RepositoryId repoId = RepositoryId.from( "shared-repo-name" );
            final Segment nodeSegment = RepositorySegmentUtils.toSegment( repoId, NodeConstants.NODE_SEGMENT_LEVEL );

            final NodbBinaryBlobStore blobStoreA = new NodbBinaryBlobStore( new MemoryBlobStore(), tenantA.client() );
            final NodbBinaryBlobStore blobStoreB = new NodbBinaryBlobStore( new MemoryBlobStore(), tenantB.client() );

            // Identical bytes written under BOTH tenants -- same content-addressed hash,
            // if hashing alone determined the key.
            final byte[] content = ( "{\"identical-payload-content\":\"" + UUID.randomUUID() + "\"}" ).getBytes();
            final BlobKey keyA = blobStoreA.addRecord( nodeSegment, ByteSource.wrap( content ) ).getKey();
            final BlobKey keyB = blobStoreB.addRecord( nodeSegment, ByteSource.wrap( content ) ).getKey();

            // Same hash (content-addressed), but each landed in ITS OWN tenant schema.
            assertEquals( keyA, keyB, "identical bytes must hash identically regardless of tenant" );

            // Ground truth: each tenant's OWN `payload` table has exactly one row for this
            // hash -- two independent rows total, not one shared row.
            assertEquals( 1L, NodbPayloadGroundTruth.countPayloadRows( tenantA.tenantId(), keyA.toString() ) );
            assertEquals( 1L, NodbPayloadGroundTruth.countPayloadRows( tenantB.tenantId(), keyB.toString() ) );

            // Each tenant can read its own copy back -- both independently, from their own
            // gRPC channel/token, proving neither tenant's write depended on the other's.
            final BlobRecord readBackA = blobStoreA.getRecord( nodeSegment, keyA );
            assertArrayEquals( content, readBackA.getBytes().read() );
            final BlobRecord readBackB = blobStoreB.getRecord( nodeSegment, keyB );
            assertArrayEquals( content, readBackB.getBytes().read() );
        }
        finally
        {
            tenantA.close();
            tenantB.close();
        }
    }

    /**
     * A DIFFERENT hash written ONLY under tenant A must be invisible via tenant B's own
     * {@code GetPayload} call -- proving isolation is enforced by the tenant-scoped gRPC
     * channel/token (server-side {@code SET LOCAL ROLE <tenant>} + {@code search_path}),
     * not merely a client-side convention.
     */
    @Test
    void tenantBCannotReadTenantAsPayloadViaGetPayload()
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "payload tenant isolation has no ES-mode equivalent" );

        final NodbTenant tenantA = NodbTestCluster.get().freshTenant();
        final NodbTenant tenantB = NodbTestCluster.get().freshTenant();

        try
        {
            final RepositoryId repoId = RepositoryId.from( "shared-repo-name" );
            final Segment aclSegment = RepositorySegmentUtils.toSegment( repoId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL );

            final NodbBinaryBlobStore blobStoreA = new NodbBinaryBlobStore( new MemoryBlobStore(), tenantA.client() );
            final NodbBinaryBlobStore blobStoreB = new NodbBinaryBlobStore( new MemoryBlobStore(), tenantB.client() );

            final byte[] content = ( "tenant-a-only-acl-" + UUID.randomUUID() ).getBytes();
            final BlobKey key = blobStoreA.addRecord( aclSegment, ByteSource.wrap( content ) ).getKey();

            // BlobStore's documented contract for a missing record is null, not an
            // exception -- tenant B genuinely has no row at this hash in ITS OWN schema.
            assertNull( blobStoreB.getRecord( aclSegment, key ), "tenant B must not be able to GetPayload tenant A's hash" );
            assertEquals( 0L, NodbPayloadGroundTruth.countPayloadRows( tenantB.tenantId(), key.toString() ) );
            assertEquals( 1L, NodbPayloadGroundTruth.countPayloadRows( tenantA.tenantId(), key.toString() ) );
        }
        finally
        {
            tenantA.close();
            tenantB.close();
        }
    }
}
