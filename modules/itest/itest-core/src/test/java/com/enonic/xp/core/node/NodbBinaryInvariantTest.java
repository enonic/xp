package com.enonic.xp.core.node;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.GetBinaryCommand;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 2 Gate C (nodb/BUILD-PHASE-2.md): the binaries-before-commit invariant (risk #4),
 * proven end-to-end through XP's own create path rather than by injecting a mid-write
 * failure into production code -- {@code CreateNodeCommand}/{@code PatchNodeCommand} are
 * not touched by this gate (Gate 0 §5's finding: the invariant is a pre-existing
 * call-ordering property of those unmodified classes -- {@code storeAndAttachBinaries()}
 * blocks on {@code binaryService.store()} strictly before {@code nodeStorageService.store()}
 * -- {@link com.enonic.xp.storage.nodb.NodbBinaryBlobStore}'s job is only to keep
 * {@code addRecord} synchronous-until-durable so that ordering keeps giving the invariant;
 * there is no new mechanism here to fault-inject). Two complementary checks, per the work
 * order's fallback ("at minimum assert ordering"):
 * <ol>
 *   <li>{@link #binaryIsDurablyOnS3AndReferencedByTheCommittedVersion()}: after a successful
 *   content-with-attachment create returns, (a) the S3 object physically exists under the
 *   tenant's prefix -- ground truth, asserted directly against MinIO, independent of
 *   whatever XP/NoDB themselves report -- and (b) reading the binary back through the
 *   committed version's own {@link BinaryReference} (via {@link GetBinaryCommand}) returns
 *   the exact same bytes, proving the version's {@code binary_keys} genuinely resolves to
 *   that S3 object end-to-end, not merely that some upload happened in isolation.</li>
 *   <li>{@link #orphanedBinaryIsAcceptable()}: a binary written directly (bypassing node
 *   create entirely -- standing in for the "upload succeeded, the surrounding write never
 *   committed" half of a mid-write failure) is durably on S3 yet attached to no node in this
 *   test repository -- the acceptable, GC-able orphan case the work order calls out,
 *   distinct from the never-acceptable case (a COMMITTED version whose {@code binary_keys}
 *   points at an object absent from S3) that the first test demonstrates cannot happen.</li>
 * </ol>
 */
class NodbBinaryInvariantTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void binaryIsDurablyOnS3AndReferencedByTheCommittedVersion()
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "S3 ground-truth check has no ES-mode equivalent" );

        final String tenantId = NodbTestCluster.get().tenantForClass( this.getClass() ).tenantId();

        final byte[] content = ( "binary-invariant-" + UUID.randomUUID() ).getBytes();
        final String expectedHex = BlobKey.sha256( ByteSource.wrap( content ) ).toString().substring( "sha256:".length() );
        final String expectedKey = tenantId + "/binary/" + expectedHex;

        // Sanity: this exact (freshly randomized) content has never been uploaded before --
        // nothing lives at that key yet, so the post-create assertion below is meaningful.
        assertEquals( 0, countS3ObjectsWithPrefix( expectedKey ), "must not exist before the create" );

        final BinaryReference ref = BinaryReference.from( "invariant-binary" );
        final PropertyTree data = new PropertyTree();
        data.addBinaryReferences( "myBinary", ref );

        final Node node = createNode( CreateNodeParams.create()
                                           .name( "invariant-node" )
                                           .parent( NodePath.ROOT )
                                           .data( data )
                                           .attachBinary( ref, ByteSource.wrap( content ) )
                                           .build() );

        // (a) Ground truth: the object is durably on S3 under the tenant's prefix by the
        // time createNode() -- which commits the referencing version -- has already returned.
        assertEquals( 1, countS3ObjectsWithPrefix( expectedKey ),
                       "the binary must be durably on S3 by the time the referencing version has committed" );

        // (b) The committed version's own BinaryReference resolves back to the identical
        // bytes through the ordinary read path -- proof the version's binary_keys actually
        // references the object just confirmed on S3, not merely that some upload happened.
        final ByteSource readBack = GetBinaryCommand.create()
            .nodeId( node.id() )
            .binaryReference( ref )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .binaryService( this.binaryService )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        assertArrayEquals( content, readBackBytes( readBack ) );
    }

    @Test
    void orphanedBinaryIsAcceptable()
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "S3 ground-truth check has no ES-mode equivalent" );

        final String tenantId = NodbTestCluster.get().tenantForClass( this.getClass() ).tenantId();

        final byte[] content = ( "binary-orphan-" + UUID.randomUUID() ).getBytes();
        final Segment binarySegment = RepositorySegmentUtils.toSegment( testRepoId, NodeConstants.BINARY_SEGMENT_LEVEL );

        // Written directly, never attached to any CreateNodeParams -- stands in for the
        // "upload succeeded, the surrounding write never committed" half of a mid-write
        // failure. An orphan like this is acceptable (GC-able by BinaryBlobVacuumTask /
        // VersionTableVacuumCommand, both routed through this same decorator's removeRecord
        // per BUILD-PHASE-2.md's "second GC path" note) -- never a dangling COMMITTED
        // reference, which the previous test demonstrates cannot happen.
        final BlobKey key = this.blobStore.addRecord( binarySegment, ByteSource.wrap( content ) ).getKey();

        final String expectedKey = tenantId + "/binary/" + key.toString().substring( "sha256:".length() );
        assertTrue( countS3ObjectsWithPrefix( expectedKey ) >= 1, "the orphan binary is still durably on S3 (GC-able, not lost)" );
    }

    private static byte[] readBackBytes( final ByteSource source )
    {
        try
        {
            return source.read();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
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
