package com.enonic.xp.core.node;

import java.util.UUID;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

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
 * Phase 2 Gate C (nodb/BUILD-PHASE-2.md): cross-tenant binary isolation through XP's own
 * {@code BlobStore} SPI -- the same security property nodb/server's own
 * {@code BinariesServiceIntegrationTest} proves directly against the {@code Binaries} gRPC
 * service, exercised here one layer up, through {@link NodbBinaryBlobStore}, the component
 * production actually wires. Two independent, freshly-provisioned tenants share ONE NoDB
 * server and ONE MinIO bucket (see {@link NodbTestCluster}); a binary written under tenant
 * A must be invisible (not merely "different", genuinely absent) via tenant B's own
 * {@code BlobStore}, and the ground truth is checked directly against S3 as well -- the
 * object physically exists ONLY under tenant A's {@code <tenantId>/binary/<hash>} prefix,
 * never tenant B's, regardless of what either tenant's client reports.
 */
class NodbBinaryCrossTenantIsolationTest
    extends AbstractNodeTest
{
    @Test
    void tenantBCannotSeeGetOrDeleteTenantAsBinary_evenForTheIdenticalHash()
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "binary tenant isolation has no ES-mode equivalent" );

        final NodbTenant tenantA = NodbTestCluster.get().freshTenant();
        final NodbTenant tenantB = NodbTestCluster.get().freshTenant();
        assertNotEquals( tenantA.tenantId(), tenantB.tenantId() );

        try
        {
            // Same underlying repository id / delegate on purpose in both stores: isolation
            // must come from the tenant-scoped gRPC client/token, not from anything else
            // differing between the two BlobStore instances.
            final RepositoryId repoId = RepositoryId.from( "shared-repo-name" );
            final Segment binarySegment = RepositorySegmentUtils.toSegment( repoId, NodeConstants.BINARY_SEGMENT_LEVEL );

            final NodbBinaryBlobStore blobStoreA = new NodbBinaryBlobStore( new MemoryBlobStore(), tenantA.client() );
            final NodbBinaryBlobStore blobStoreB = new NodbBinaryBlobStore( new MemoryBlobStore(), tenantB.client() );

            final byte[] content = ( "cross-tenant-binary-" + UUID.randomUUID() ).getBytes();
            final BlobRecord written = blobStoreA.addRecord( binarySegment, ByteSource.wrap( content ) );
            final BlobKey key = written.getKey();

            // tenant B's own store has no such object under ITS prefix -- BlobStore's
            // documented contract for a missing record is null, not an exception.
            assertNull( blobStoreB.getRecord( binarySegment, key ), "tenant B must not see tenant A's binary, even for the identical hash" );

            // tenant B "deleting" the same key is a no-op against its own (empty) prefix --
            // tenant A's copy must survive.
            blobStoreB.removeRecord( binarySegment, key );
            final BlobRecord stillThere = blobStoreA.getRecord( binarySegment, key );
            assertArrayEquals( content, stillThere.getBytes().read(), "tenant B's delete call must not remove tenant A's binary" );

            // Ground truth at the S3 level: the object physically exists ONLY under tenant
            // A's prefix, never tenant B's -- independent of what either tenant's client
            // reports through the BlobStore SPI above.
            final String hex = key.toString().substring( "sha256:".length() );
            final String tenantAKey = tenantA.tenantId() + "/binary/" + hex;
            final String tenantBKey = tenantB.tenantId() + "/binary/" + hex;
            assertEquals( 1, countS3ObjectsWithPrefix( tenantAKey ) );
            assertEquals( 0, countS3ObjectsWithPrefix( tenantBKey ) );
        }
        catch ( java.io.IOException e )
        {
            throw new java.io.UncheckedIOException( e );
        }
        finally
        {
            tenantA.close();
            tenantB.close();
        }
    }

    @Test
    void tenantACanDeleteItsOwnBinary()
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "binary tenant isolation has no ES-mode equivalent" );

        final NodbTenant tenantA = NodbTestCluster.get().freshTenant();
        try
        {
            final RepositoryId repoId = RepositoryId.from( "shared-repo-name" );
            final Segment binarySegment = RepositorySegmentUtils.toSegment( repoId, NodeConstants.BINARY_SEGMENT_LEVEL );
            final NodbBinaryBlobStore blobStoreA = new NodbBinaryBlobStore( new MemoryBlobStore(), tenantA.client() );

            final byte[] content = ( "own-binary-" + UUID.randomUUID() ).getBytes();
            final BlobKey key = blobStoreA.addRecord( binarySegment, ByteSource.wrap( content ) ).getKey();

            blobStoreA.removeRecord( binarySegment, key );
            assertNull( blobStoreA.getRecord( binarySegment, key ) );
        }
        finally
        {
            tenantA.close();
        }
    }

    private long countS3ObjectsWithPrefix( final String prefix )
    {
        return NodbTestCluster.get()
            .s3Client()
            .listObjectsV2( ListObjectsV2Request.builder().bucket( NodbTestCluster.get().s3Bucket() ).prefix( prefix ).build() )
            .contents()
            .size();
    }
}
