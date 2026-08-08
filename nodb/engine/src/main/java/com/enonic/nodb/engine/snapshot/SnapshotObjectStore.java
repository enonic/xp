package com.enonic.nodb.engine.snapshot;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;

import com.enonic.nodb.engine.TenantContext;

/**
 * Object-store IO for snapshot artifacts (Phase 5 Gate A; Gate 0 ratified decision 1):
 * snapshot BYTES live in S3 under {@code <tenant>/snapshot/<snapshot-id>/} — COPY streams
 * + the full sorted hash manifest, gzip, multipart — while Postgres holds only the
 * registry row. Follows {@code BinaryStore}'s client conventions exactly (same bucket,
 * same {@code <tenant>/...} key layout one prefix over from {@code <tenant>/binary/}, and
 * in the server the SAME {@link S3Client} instance — one client, one credential set, one
 * endpoint config).
 *
 * <p><b>Streaming multipart upload</b>: {@link #write} returns an {@link ObjectWriter}
 * that buffers up to one part ({@value #DEFAULT_PART_SIZE} bytes by default) in heap and
 * uploads parts as they fill — a snapshot stream of any size is uploaded with bounded
 * memory and no local spool file. Objects smaller than one part are PUT directly (S3
 * multipart requires ≥5 MiB parts except the last). {@link ObjectWriter#abort()} tears
 * down a started multipart upload so a failed create leaves no half-assembled object —
 * only completed objects (orphans under a non-COMPLETE registry row, which delete removes).
 */
public final class SnapshotObjectStore
{
    /** S3's own minimum for every part except the last. */
    public static final int MIN_PART_SIZE = 5 * 1024 * 1024;

    private static final int DEFAULT_PART_SIZE = 8 * 1024 * 1024;

    private final S3Client s3;

    private final String bucket;

    private final int partSize;

    public SnapshotObjectStore( S3Client s3, String bucket )
    {
        this( s3, bucket, DEFAULT_PART_SIZE );
    }

    public SnapshotObjectStore( S3Client s3, String bucket, int partSize )
    {
        if ( partSize < MIN_PART_SIZE )
        {
            throw new IllegalArgumentException( "partSize must be >= " + MIN_PART_SIZE + " (S3 multipart minimum), got " + partSize );
        }
        this.s3 = s3;
        this.bucket = bucket;
        this.partSize = partSize;
    }

    /**
     * Shares {@code binaries}' S3 client and bucket (see the class Javadoc). A factory
     * rather than accessor-plumbing in the server so server MAIN code never references an
     * AWS SDK type — the same posture its build file documents for the Binaries service.
     */
    public static SnapshotObjectStore sharing( com.enonic.nodb.engine.binary.BinaryStore binaries )
    {
        return new SnapshotObjectStore( binaries.s3Client(), binaries.bucket() );
    }

    /** {@code <tenant>/snapshot/<snapshotId>/} — the one place the snapshot key layout is built. */
    public static String prefix( TenantContext tenant, String snapshotId )
    {
        return tenant.tenantId() + "/snapshot/" + snapshotId + "/";
    }

    public ObjectWriter write( String key )
    {
        return new ObjectWriter( key );
    }

    public InputStream get( String key )
    {
        return s3.getObject( GetObjectRequest.builder().bucket( bucket ).key( key ).build() );
    }

    /** All object keys under {@code prefix}, paginated; sorted (S3 lists in key order). */
    public List<String> listKeys( String prefix )
    {
        List<String> keys = new ArrayList<>();
        String continuationToken = null;
        do
        {
            ListObjectsV2Request.Builder request = ListObjectsV2Request.builder().bucket( bucket ).prefix( prefix );
            if ( continuationToken != null )
            {
                request.continuationToken( continuationToken );
            }
            ListObjectsV2Response response = s3.listObjectsV2( request.build() );
            for ( S3Object object : response.contents() )
            {
                keys.add( object.key() );
            }
            continuationToken = response.isTruncated() ? response.nextContinuationToken() : null;
        }
        while ( continuationToken != null );
        return List.copyOf( keys );
    }

    /**
     * Deletes every object under {@code prefix}; returns how many were removed. Idempotent
     * like {@code BinaryStore#delete} — an empty or absent prefix is a successful no-op,
     * which is what lets snapshot delete double as the recovery sweep for its own crash
     * window (registry row already gone, prefix still there).
     */
    public int deletePrefix( String prefix )
    {
        int deleted = 0;
        List<String> keys = listKeys( prefix );
        for ( int from = 0; from < keys.size(); from += 1000 )
        {
            List<ObjectIdentifier> batch = keys.subList( from, Math.min( from + 1000, keys.size() ) )
                .stream()
                .map( key -> ObjectIdentifier.builder().key( key ).build() )
                .toList();
            s3.deleteObjects(
                DeleteObjectsRequest.builder().bucket( bucket ).delete( Delete.builder().objects( batch ).build() ).build() );
            deleted += batch.size();
        }
        return deleted;
    }

    /**
     * One streamed object: buffers a part, uploads parts as they fill, and on {@link #close}
     * either PUTs the single buffered part (small object) or completes the multipart upload.
     * The object becomes visible under its key ONLY on a successful {@code close()} — S3
     * multipart semantics, which is exactly the durability edge the registry's COMPLETE
     * transition relies on.
     */
    public final class ObjectWriter
        extends OutputStream
    {
        private final String key;

        private final byte[] buffer = new byte[partSize];

        private int position;

        private String uploadId;

        private final List<CompletedPart> parts = new ArrayList<>();

        private boolean closed;

        private ObjectWriter( String key )
        {
            this.key = key;
        }

        @Override
        public void write( int b )
        {
            buffer[position++] = (byte) b;
            if ( position == partSize )
            {
                uploadPart();
            }
        }

        @Override
        public void write( byte[] bytes, int offset, int length )
        {
            while ( length > 0 )
            {
                int chunk = Math.min( length, partSize - position );
                System.arraycopy( bytes, offset, buffer, position, chunk );
                position += chunk;
                offset += chunk;
                length -= chunk;
                if ( position == partSize )
                {
                    uploadPart();
                }
            }
        }

        private void uploadPart()
        {
            if ( uploadId == null )
            {
                uploadId = s3.createMultipartUpload( CreateMultipartUploadRequest.builder().bucket( bucket ).key( key ).build() )
                    .uploadId();
            }
            int partNumber = parts.size() + 1;
            String eTag = s3.uploadPart( UploadPartRequest.builder()
                                             .bucket( bucket )
                                             .key( key )
                                             .uploadId( uploadId )
                                             .partNumber( partNumber )
                                             .build(), RequestBody.fromBytes( Arrays.copyOf( buffer, position ) ) ).eTag();
            parts.add( CompletedPart.builder().partNumber( partNumber ).eTag( eTag ).build() );
            position = 0;
        }

        @Override
        public void close()
        {
            if ( closed )
            {
                return;
            }
            closed = true;
            if ( uploadId == null )
            {
                s3.putObject( software.amazon.awssdk.services.s3.model.PutObjectRequest.builder().bucket( bucket ).key( key ).build(),
                              RequestBody.fromBytes( Arrays.copyOf( buffer, position ) ) );
                return;
            }
            if ( position > 0 )
            {
                uploadPart();
            }
            s3.completeMultipartUpload( CompleteMultipartUploadRequest.builder()
                                            .bucket( bucket )
                                            .key( key )
                                            .uploadId( uploadId )
                                            .multipartUpload( CompletedMultipartUpload.builder().parts( parts ).build() )
                                            .build() );
            uploadId = null;
        }

        /**
         * Failure path: tear down a started multipart upload; never makes the object visible.
         * Best-effort (an already-completed or already-gone upload is silently ignored) — this
         * runs on error paths where the original failure must win, not a cleanup hiccup.
         */
        public void abort()
        {
            closed = true;
            if ( uploadId != null )
            {
                try
                {
                    s3.abortMultipartUpload(
                        AbortMultipartUploadRequest.builder().bucket( bucket ).key( key ).uploadId( uploadId ).build() );
                }
                catch ( RuntimeException ignore )
                {
                    // best-effort cleanup on an error path; the original failure propagates
                }
                uploadId = null;
            }
        }
    }
}
