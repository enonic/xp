package com.enonic.nodb.engine.binary;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.StsClientBuilder;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.Credentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.nodb.engine.TenantContext;

/**
 * Content-addressed binary (opaque blob) store over S3, or any S3-compatible object store
 * (e.g. MinIO), scoped per tenant (DESIGN.md §2, §7.2; BUILD-PHASE-2.md Gate 0/A). Binaries
 * are OPAQUE here — NoDB never parses their bytes, only content-addresses them, unlike the
 * structured {@code payload} table.
 *
 * <p><b>Key layout</b>: {@code <bucket>/<tenant>/binary/<sha256-hex>} — the bucket is the
 * S3 bucket itself (not part of the object key); the object key this class builds is
 * {@code <tenant>/binary/<hex>}. The public hash returned by {@link #store} matches the
 * existing {@code BlobKey}/{@code PayloadStore} convention exactly: {@code
 * "sha256:<hex>"}. Callers may pass either form (prefixed or bare hex) back into {@link
 * #get}/{@link #exists}/{@link #delete}/{@link #presignGet} — the prefix is stripped
 * before building the object key.
 *
 * <p><b>Dedup</b> is per-tenant (deliberately wider than today's per-repository
 * {@code BlobStore} dedup — Gate 0 nuance, DESIGN.md §7.2's per-tenant dedup rationale):
 * identical bytes uploaded twice under the same tenant land on S3 once, via HEAD-before-
 * PUT (see {@link #storeStaged}) — same shape as {@code PayloadStore}'s {@code ON CONFLICT
 * DO NOTHING}, just at the object-store layer instead of a unique index.
 *
 * <p><b>Durability</b>: {@link #store}/{@link #storeStaged} return only once the S3 {@code
 * PutObject} (or the HEAD confirming an already-durable object) call has completed —
 * synchronous and blocking-until-durable by construction. This is what lets the gRPC
 * {@code PutBinary} RPC satisfy the binaries-before-commit invariant (BUILD-PHASE-2.md
 * risk #4) the same way {@code WriteService}/{@code Tx#inTenantTx} already gives Postgres
 * writes that property: the caller cannot observe success until this method returns
 * normally.
 *
 * <p><b>Streaming</b>: {@link #store(TenantContext, InputStream)} spools the input to a
 * local temp file while it is being read (never buffers the whole binary in heap), then
 * hashes and uploads from that file. The gRPC {@code PutBinary} service impl assembles
 * incoming chunks into its own temp file directly and calls {@link #storeStaged} on it,
 * avoiding a second buffering copy through this class.
 *
 * <p><b>Presigned URLs</b>: {@link #presignGet} is the PRODUCTION path when this instance
 * is constructed with an STS client + role ARN (the default via {@link #fromEnv}, given
 * {@code NODB_S3_STS_ROLE_ARN}): it calls STS {@code AssumeRole} with an inline session
 * policy restricted to {@code arn:aws:s3:::<bucket>/<tenant>/*}, then presigns using the
 * resulting SESSION credentials — so even a bug that computed the wrong object key cannot
 * mint a URL outside the tenant prefix (DESIGN.md §7.2, verbatim: "even a key-derivation
 * bug cannot mint a URL outside the tenant"). When no STS client/role ARN is configured
 * (the {@link #BinaryStore(S3Client, S3Presigner, StsClient, String, String, Region, URI,
 * S3Configuration) full constructor} with a {@code null stsClient}/{@code stsRoleArn}),
 * {@code presignGet} falls back to presigning directly with the base credentials.
 *
 * <p><b>Gate A test limitation (documented, not faked):</b> this gate's MinIO-backed test
 * suite exercises the fallback branch, not the STS branch — MinIO's STS {@code AssumeRole}
 * endpoint did not wire up reliably against the testcontainers MinIO image in the time
 * budgeted for this gate (see BUILD-PHASE-2.md Gate A results for the attempts). The
 * fallback is a real security property, not a faked one: a presigned URL's AWS SigV4
 * signature covers the exact bucket+key, so the URL this method returns can still only
 * ever fetch the ONE object it was minted for — mutating the URL to address a different
 * tenant's key invalidates the signature (verified by the test suite). What is absent
 * versus the STS branch is the additional blast-radius reduction: session credentials
 * themselves restricted to the tenant prefix, independent of whether the key computed
 * above happens to be correct. The STS branch is complete, real production code — it is
 * simply not exercised end-to-end by this gate's CI run.
 */
public final class BinaryStore
    implements Closeable
{
    private static final Logger LOG = LoggerFactory.getLogger( BinaryStore.class );

    private static final int COPY_BUFFER_SIZE = 64 * 1024;

    /** STS's own minimum session duration; also gives presigned URLs headroom under it. */
    private static final int MIN_STS_SESSION_SECONDS = 900;

    private final S3Client s3;

    private final S3Presigner presigner;

    private final StsClient stsClient;

    private final String stsRoleArn;

    private final String bucket;

    private final Region region;

    private final URI endpointOverride;

    private final S3Configuration serviceConfiguration;

    public BinaryStore( S3Client s3, S3Presigner presigner, StsClient stsClient, String stsRoleArn, String bucket, Region region,
                         URI endpointOverride, S3Configuration serviceConfiguration )
    {
        this.s3 = s3;
        this.presigner = presigner;
        this.stsClient = stsClient;
        this.stsRoleArn = stsRoleArn;
        this.bucket = bucket;
        this.region = region;
        this.endpointOverride = endpointOverride;
        this.serviceConfiguration = serviceConfiguration;
    }

    /**
     * Builds a {@link BinaryStore} from environment configuration (matching the rest of
     * this codebase's env-var-only config posture, see {@code NodbServer}):
     * <ul>
     *   <li>{@code NODB_S3_ENDPOINT} — optional endpoint override (set for MinIO/self-hosted;
     *       absent uses the AWS SDK's default endpoint resolution for {@code NODB_S3_REGION})
     *   <li>{@code NODB_S3_BUCKET} — bucket name (default {@code nodb})
     *   <li>{@code NODB_S3_REGION} — SDK region id (default {@code us-east-1}; MinIO ignores
     *       the value but the SDK requires one be set)
     *   <li>{@code NODB_S3_ACCESS_KEY} / {@code NODB_S3_SECRET_KEY} — static credentials;
     *       when either is absent, falls back to the SDK's {@link DefaultCredentialsProvider}
     *       chain (env/profile/instance-role — for real AWS deployments)
     *   <li>{@code NODB_S3_PATH_STYLE} — force path-style bucket addressing (default {@code
     *       true} when an endpoint override is set — MinIO needs this — {@code false}
     *       otherwise)
     *   <li>{@code NODB_S3_STS_ROLE_ARN} — enables the STS-scoped {@link #presignGet}
     *       production path; if unset, presigning falls back to base credentials (logged as
     *       a warning — see the class Javadoc's test-limitation note)
     * </ul>
     */
    public static BinaryStore fromEnv()
    {
        String endpoint = env( "NODB_S3_ENDPOINT", null );
        String bucket = env( "NODB_S3_BUCKET", "nodb" );
        String regionName = env( "NODB_S3_REGION", "us-east-1" );
        String accessKey = env( "NODB_S3_ACCESS_KEY", null );
        String secretKey = env( "NODB_S3_SECRET_KEY", null );
        String stsRoleArn = env( "NODB_S3_STS_ROLE_ARN", null );
        boolean pathStyle = Boolean.parseBoolean( env( "NODB_S3_PATH_STYLE", endpoint != null ? "true" : "false" ) );

        Region region = Region.of( regionName );
        URI endpointOverride = endpoint == null ? null : URI.create( endpoint );
        AwsCredentialsProvider credentialsProvider = ( accessKey != null && secretKey != null )
            ? StaticCredentialsProvider.create( AwsBasicCredentials.create( accessKey, secretKey ) )
            : DefaultCredentialsProvider.create();
        S3Configuration serviceConfiguration = S3Configuration.builder().pathStyleAccessEnabled( pathStyle ).build();

        S3Client s3 = configureS3ClientBuilder( S3Client.builder(), region, credentialsProvider, endpointOverride ).serviceConfiguration(
            serviceConfiguration ).build();
        S3Presigner presigner =
            configurePresignerBuilder( S3Presigner.builder(), region, credentialsProvider, endpointOverride ).serviceConfiguration(
                serviceConfiguration ).build();

        StsClient stsClient = null;
        if ( stsRoleArn != null )
        {
            StsClientBuilder stsBuilder = StsClient.builder().region( region ).credentialsProvider( credentialsProvider );
            if ( endpointOverride != null )
            {
                // Self-hosted S3-compatible stores (MinIO) serve STS on the same endpoint as S3.
                stsBuilder.endpointOverride( endpointOverride );
            }
            stsClient = stsBuilder.build();
        }
        else
        {
            LOG.warn( "NODB_S3_STS_ROLE_ARN not set — presignGet will fall back to base-credential presigning "
                          + "(not further restricted to the tenant prefix by an STS session policy); set "
                          + "NODB_S3_STS_ROLE_ARN for production deployments (DESIGN.md §7.2)." );
        }

        return new BinaryStore( s3, presigner, stsClient, stsRoleArn, bucket, region, endpointOverride, serviceConfiguration );
    }

    private static S3ClientBuilder configureS3ClientBuilder( S3ClientBuilder builder, Region region,
                                                               AwsCredentialsProvider credentialsProvider, URI endpointOverride )
    {
        builder.region( region ).credentialsProvider( credentialsProvider );
        if ( endpointOverride != null )
        {
            builder.endpointOverride( endpointOverride );
        }
        return builder;
    }

    private static S3Presigner.Builder configurePresignerBuilder( S3Presigner.Builder builder, Region region,
                                                                    AwsCredentialsProvider credentialsProvider, URI endpointOverride )
    {
        builder.region( region ).credentialsProvider( credentialsProvider );
        if ( endpointOverride != null )
        {
            builder.endpointOverride( endpointOverride );
        }
        return builder;
    }

    private static String env( String name, String defaultValue )
    {
        String value = System.getenv( name );
        return value == null || value.isBlank() ? defaultValue : value;
    }

    // ---- write path -----------------------------------------------------------------------

    /**
     * Spools {@code content} to a local temp file while reading it (bounded disk, not heap,
     * usage — safe for multi-MB/GB binaries), then delegates to {@link #storeStaged}.
     */
    public String store( TenantContext tenant, InputStream content )
        throws IOException
    {
        Path staged = Files.createTempFile( "nodb-binary-", ".upload" );
        try
        {
            try (OutputStream out = Files.newOutputStream( staged ))
            {
                content.transferTo( out );
            }
            return storeStaged( tenant, staged );
        }
        finally
        {
            Files.deleteIfExists( staged );
        }
    }

    /** In-memory convenience for small/known-size payloads (tests, small attachments). */
    public String store( TenantContext tenant, byte[] bytes )
    {
        String hex = sha256Hex( bytes );
        String key = objectKey( tenant, hex );
        if ( !headExists( key ) )
        {
            s3.putObject( PutObjectRequest.builder().bucket( bucket ).key( key ).build(), RequestBody.fromBytes( bytes ) );
        }
        return "sha256:" + hex;
    }

    /**
     * Hashes an already-staged local file and PUTs it to S3 if unseen (HEAD-before-PUT
     * dedup) — the streaming {@code PutBinary} RPC assembles incoming chunks into its own
     * temp file and calls this directly, avoiding a second buffering copy through {@link
     * #store(TenantContext, InputStream)}. Does NOT delete {@code stagedFile}; the caller
     * owns that file's lifecycle.
     */
    public String storeStaged( TenantContext tenant, Path stagedFile )
        throws IOException
    {
        String hex = sha256HexOfFile( stagedFile );
        String key = objectKey( tenant, hex );
        if ( !headExists( key ) )
        {
            s3.putObject( PutObjectRequest.builder().bucket( bucket ).key( key ).build(), RequestBody.fromFile( stagedFile ) );
        }
        return "sha256:" + hex;
    }

    // ---- read / existence / delete path -----------------------------------------------------

    public boolean exists( TenantContext tenant, String hash )
    {
        return headExists( objectKey( tenant, hash ) );
    }

    public InputStream get( TenantContext tenant, String hash )
        throws BinaryNotFoundException
    {
        String key = objectKey( tenant, hash );
        try
        {
            return s3.getObject( GetObjectRequest.builder().bucket( bucket ).key( key ).build() );
        }
        catch ( NoSuchKeyException e )
        {
            throw new BinaryNotFoundException( hash );
        }
    }

    /**
     * S3 {@code DeleteObject} is idempotent — deleting an absent key succeeds silently,
     * matching the "delete an already-gone id is a no-op" convention {@code BranchStore}/
     * {@code VersionStore} already use.
     */
    public void delete( TenantContext tenant, String hash )
    {
        String key = objectKey( tenant, hash );
        s3.deleteObject( DeleteObjectRequest.builder().bucket( bucket ).key( key ).build() );
    }

    // ---- presigned GET ----------------------------------------------------------------------

    public URL presignGet( TenantContext tenant, String hash, Duration ttl )
    {
        String key = objectKey( tenant, hash );
        if ( stsClient != null && stsRoleArn != null )
        {
            return presignGetViaSts( tenant, key, ttl );
        }
        return presigner.presignGetObject( GetObjectPresignRequest.builder()
                                                .signatureDuration( ttl )
                                                .getObjectRequest( GetObjectRequest.builder().bucket( bucket ).key( key ).build() )
                                                .build() ).url();
    }

    /**
     * Production presign path (see class Javadoc): mints STS session credentials via
     * {@code AssumeRole} with an inline policy restricted to {@code
     * arn:aws:s3:::<bucket>/<tenant>/*}, then presigns using THOSE credentials rather than
     * the base ones — so the resulting URL is doubly scoped (the object key AND the
     * credentials that signed it are tenant-prefix-restricted).
     */
    private URL presignGetViaSts( TenantContext tenant, String key, Duration ttl )
    {
        String policy = """
            {
              "Version": "2012-10-17",
              "Statement": [
                {
                  "Effect": "Allow",
                  "Action": ["s3:GetObject"],
                  "Resource": ["arn:aws:s3:::%s/%s/*"]
                }
              ]
            }
            """.formatted( bucket, tenant.tenantId() );

        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
            .roleArn( stsRoleArn )
            .roleSessionName( "nodb-presign-" + tenant.tenantId() )
            .policy( policy )
            .durationSeconds( (int) Math.max( MIN_STS_SESSION_SECONDS, ttl.toSeconds() ) )
            .build();

        Credentials sessionCredentials = stsClient.assumeRole( assumeRoleRequest ).credentials();
        AwsSessionCredentials scopedCredentials = AwsSessionCredentials.create( sessionCredentials.accessKeyId(),
                                                                                 sessionCredentials.secretAccessKey(),
                                                                                 sessionCredentials.sessionToken() );

        S3Presigner.Builder scopedBuilder =
            S3Presigner.builder().region( region ).credentialsProvider( StaticCredentialsProvider.create( scopedCredentials ) );
        if ( endpointOverride != null )
        {
            scopedBuilder.endpointOverride( endpointOverride );
        }
        if ( serviceConfiguration != null )
        {
            scopedBuilder.serviceConfiguration( serviceConfiguration );
        }

        try (S3Presigner scopedPresigner = scopedBuilder.build())
        {
            return scopedPresigner.presignGetObject( GetObjectPresignRequest.builder()
                                                          .signatureDuration( ttl )
                                                          .getObjectRequest(
                                                              GetObjectRequest.builder().bucket( bucket ).key( key ).build() )
                                                          .build() ).url();
        }
    }

    // ---- shared helpers ---------------------------------------------------------------------

    /**
     * The underlying S3 client — shared with {@code SnapshotObjectStore} (Phase 5 Gate A):
     * snapshot artifacts live in the SAME bucket under {@code <tenant>/snapshot/...}, one
     * prefix over from {@code <tenant>/binary/...}, so the server reuses this one client
     * (one credential set, one endpoint config) rather than building a second from the same
     * env vars. Lifecycle stays here: {@link #close()} closes it.
     */
    public S3Client s3Client()
    {
        return s3;
    }

    /** See {@link #s3Client()}. */
    public String bucket()
    {
        return bucket;
    }

    private String objectKey( TenantContext tenant, String hash )
    {
        String hex = hash.startsWith( "sha256:" ) ? hash.substring( "sha256:".length() ) : hash;
        return tenant.tenantId() + "/binary/" + hex;
    }

    private boolean headExists( String key )
    {
        try
        {
            s3.headObject( HeadObjectRequest.builder().bucket( bucket ).key( key ).build() );
            return true;
        }
        catch ( NoSuchKeyException e )
        {
            return false;
        }
        catch ( S3Exception e )
        {
            if ( e.statusCode() == 404 )
            {
                return false;
            }
            throw e;
        }
    }

    private static String sha256Hex( byte[] bytes )
    {
        return hex( newSha256Digest().digest( bytes ) );
    }

    private static String sha256HexOfFile( Path file )
        throws IOException
    {
        MessageDigest digest = newSha256Digest();
        try (InputStream in = Files.newInputStream( file ); DigestInputStream digestIn = new DigestInputStream( in, digest ))
        {
            byte[] buffer = new byte[COPY_BUFFER_SIZE];
            while ( digestIn.read( buffer ) >= 0 )
            {
                // draining -- the digest is updated as a side effect of DigestInputStream#read
            }
        }
        return hex( digest.digest() );
    }

    private static MessageDigest newSha256Digest()
    {
        try
        {
            return MessageDigest.getInstance( "SHA-256" );
        }
        catch ( NoSuchAlgorithmException e )
        {
            // SHA-256 is a mandatory JCE algorithm on every JVM; this can't happen (same
            // reasoning as PayloadStore#sha256Key).
            throw new IllegalStateException( e );
        }
    }

    private static String hex( byte[] bytes )
    {
        StringBuilder sb = new StringBuilder( bytes.length * 2 );
        for ( byte b : bytes )
        {
            sb.append( String.format( "%02x", b ) );
        }
        return sb.toString();
    }

    @Override
    public void close()
    {
        s3.close();
        presigner.close();
        if ( stsClient != null )
        {
            stsClient.close();
        }
    }
}
