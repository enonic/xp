package com.enonic.nodb.engine.snapshot;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import javax.sql.DataSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.search.OutboxStore;
import com.enonic.nodb.engine.store.RepoKeys;
import com.enonic.nodb.engine.store.UnknownRepoException;

/**
 * Manifest snapshots (Phase 5 Gate A; DESIGN §6, Gate 0 ratified decisions 1–3):
 * per-repo and per-tenant snapshot create, list and delete. A snapshot is the repo's
 * narrow rows — branches, versions, branch entries (heads), commits — plus the XP-shipped
 * {@code search_document} rows (ratified #3: they are the system of record for search
 * content, and a doc-less repo cannot rebuild NoDB-side), the FULL sorted hash manifest
 * (ratified #2: every distinct payload hash and binary key the snapshot's rows reference —
 * deltas were rejected), and the outbox position. It deliberately EXCLUDES derived state
 * (OpenSearch is never snapshotted) and payload/binary BYTES: the snapshot is referential,
 * content-addressing makes it incremental by construction, and the horizon rule (decision
 * 2) is what keeps the referenced content alive until {@code expires_at}.
 *
 * <h2>Create</h2>
 * <ol>
 * <li>Insert the registry row in state {@code CREATING} (one write transaction that also
 *     reads the retention policy and stamps {@code expires_at = created_at +
 *     snapshot_horizon} in the same statement). Row first, objects second: a create that
 *     dies at ANY later point leaves an identifiable non-COMPLETE row next to its orphan
 *     prefix — see the delete-ordering note below for why that direction is load-bearing.</li>
 * <li>ALL reads inside ONE {@link Tx#inTenantSnapshot} (P1's checked single-snapshot
 *     helper; FINDINGS #1). The outbox seq is captured as the FIRST statement — it both
 *     pins the repeatable-read snapshot and records the seq that snapshot is consistent
 *     with, one act, no window. For tenant scope every repo is read in this one
 *     transaction: the cross-repo consistent point DESIGN §6 promises. Each row set
 *     streams out via pgJDBC {@code CopyManager} → gzip → S3 multipart under
 *     {@code <tenant>/snapshot/<snapshot-id>/} (COPY takes no bind parameters; the only
 *     interpolated values are {@code repo_key}s read back from the database in this same
 *     transaction — the validated-identifier posture {@code RepositoryLifecycle} already
 *     uses). {@code repo_key} is EXCLUDED from every stream: it is a surrogate of THIS
 *     incarnation, and restore loads under a fresh key.</li>
 * <li>Upload {@code MANIFEST.json} (artifact list with per-object sha256/bytes/rows, repo
 *     table of contents, outbox seq), then flip the registry row to {@code COMPLETE} with
 *     the counts and {@code manifest_sha256} — only after every object is durably written.
 *     A failure anywhere instead marks the row {@code FAILED} (best-effort; a hard crash
 *     leaves {@code CREATING}) — either way the row is identifiable garbage plus an orphan
 *     prefix that {@link #delete} removes, and nothing ever trusts a non-COMPLETE row.</li>
 * </ol>
 *
 * <h2>Delete ordering — registry row FIRST, S3 prefix SECOND</h2>
 * The crash-consistency argument: the two deletes cannot be atomic, so one order must be
 * chosen by which half-state it can leave. Row-first can leave an S3 prefix with no
 * registry row — identifiable garbage by definition (the registry is the only thing that
 * makes a prefix mean anything), harmless, and swept by simply re-running delete (the
 * prefix delete always runs, row or no row). Prefix-first could leave a COMPLETE registry
 * row over a partially deleted prefix: a row that LOOKS like a restorable snapshot and is
 * not, discovered only when a restore fails halfway into it. An orphan prefix must be
 * identifiable garbage; the reverse must never be trusted as a snapshot — hence row first.
 *
 * <h2>Horizon accounting</h2>
 * Creation stamps {@code expires_at} from the policy AT CREATION TIME (a later policy
 * change never moves an existing snapshot's expiry). Delete refuses nothing — operator
 * delete is always allowed, expired or not. Expiry is ENFORCED by restore (Gate B fails
 * loudly up front on an expired referential snapshot) and HONOURED by GC (Gate C keeps
 * every in-horizon snapshot's hashes in the reachability set); this class's only horizon
 * job is making the data correct.
 */
public final class SnapshotService
{
    public static final String SCOPE_REPO = "REPO";

    public static final String SCOPE_TENANT = "TENANT";

    public static final int FORMAT_VERSION = 1;

    static final String MANIFEST_KEY = "MANIFEST.json";

    private static final Logger LOG = LoggerFactory.getLogger( SnapshotService.class );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final DataSource dataSource;

    private final SnapshotObjectStore objects;

    public SnapshotService( DataSource dataSource, SnapshotObjectStore objects )
    {
        this.dataSource = dataSource;
        this.objects = objects;
    }

    /** Per-repo snapshot: exactly one repo's row sets. Unknown repo fails with {@link UnknownRepoException}. */
    public SnapshotRecord createRepoSnapshot( TenantContext tenant, String repoId )
        throws SQLException
    {
        if ( repoId == null || repoId.isEmpty() )
        {
            throw new IllegalArgumentException( "repoId must not be empty for a REPO-scoped snapshot" );
        }
        return create( tenant, repoId );
    }

    /** Per-tenant snapshot: ALL repos, read at one cross-repo consistent point (one transaction). */
    public SnapshotRecord createTenantSnapshot( TenantContext tenant )
        throws SQLException
    {
        return create( tenant, null );
    }

    public List<SnapshotRecord> list( TenantContext tenant )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, tenant, SnapshotRegistry::list );
    }

    /**
     * Registry row first, prefix second — see the class Javadoc's crash argument. The prefix
     * delete ALWAYS runs, row or no row, so re-running delete for an id whose row is already
     * gone sweeps the orphan prefix a crash between the two halves left behind. Returns
     * whether a registry row existed; refuses nothing (expired snapshots delete like any
     * other — expiry is restore's concern, not delete's).
     */
    public boolean delete( TenantContext tenant, String snapshotId )
        throws SQLException
    {
        validateSnapshotId( snapshotId );
        SnapshotRecord removed = Tx.inTenantTx( dataSource, tenant, connection -> SnapshotRegistry.delete( connection, snapshotId ) );
        objects.deletePrefix( SnapshotObjectStore.prefix( tenant, snapshotId ) );
        return removed != null;
    }

    // ---- create --------------------------------------------------------------------------

    private SnapshotRecord create( TenantContext tenant, String repoIdOrNull )
        throws SQLException
    {
        String snapshotId = UUID.randomUUID().toString();
        String scope = repoIdOrNull == null ? SCOPE_TENANT : SCOPE_REPO;
        String location = SnapshotObjectStore.prefix( tenant, snapshotId );

        // Registry row first (state CREATING). For REPO scope the repo is resolved here so an
        // unknown repo fails before any row or object exists; the resolved key is informational
        // registry metadata only -- the authoritative repo set is re-read inside the snapshot
        // transaction below (a repo dropped in between fails the create there, and the row
        // flips to FAILED like any other mid-create failure).
        Tx.inTenantTx( dataSource, tenant, connection -> {
            Long repoKey = repoIdOrNull == null ? null : RepoKeys.resolve( connection, new RepoRef( repoIdOrNull ) );
            SnapshotRegistry.insertCreating( connection, snapshotId, scope, repoIdOrNull, repoKey, location );
            return null;
        } );

        try
        {
            Capture capture =
                Tx.inTenantSnapshot( dataSource, tenant, connection -> capture( connection, tenant, snapshotId, scope, repoIdOrNull,
                                                                                location ) );

            Tx.inTenantTx( dataSource, tenant, connection -> {
                SnapshotRegistry.markComplete( connection, snapshotId, capture.outboxSeq, capture.versionCount, capture.headCount,
                                               capture.commitCount, capture.documentCount, capture.hashCount, capture.totalBytes,
                                               capture.manifestSha256 );
                return null;
            } );
            return Tx.inTenantTx( dataSource, tenant, connection -> SnapshotRegistry.get( connection, snapshotId ) );
        }
        catch ( SQLException | RuntimeException e )
        {
            markFailedBestEffort( tenant, snapshotId );
            throw e;
        }
    }

    /**
     * All reads of one create, in ONE repeatable-read snapshot. The outbox seq is the first
     * statement: it establishes the transaction's snapshot AND is the value recorded as "the
     * seq this snapshot is consistent with" — the same act, so there is no window in which
     * they could disagree (the Phase 4 Gate C lost-write shape, structurally excluded).
     */
    private Capture capture( Connection connection, TenantContext tenant, String snapshotId, String scope, String repoIdOrNull,
                             String location )
        throws SQLException
    {
        long outboxSeq = OutboxStore.maxSeq( connection );

        List<RepoMeta> repos = listRepos( connection, repoIdOrNull );
        if ( repoIdOrNull != null && repos.isEmpty() )
        {
            // The repo existed when the registry row was inserted but is gone in this snapshot.
            throw new UnknownRepoException( repoIdOrNull );
        }

        CopyManager copyManager = connection.unwrap( PGConnection.class ).getCopyAPI();

        List<Artifact> artifacts = new ArrayList<>();
        ArrayNode repoNodes = MAPPER.createArrayNode();

        long versionCount = 0;
        long headCount = 0;
        long commitCount = 0;
        long documentCount = 0;

        for ( int i = 0; i < repos.size(); i++ )
        {
            RepoMeta repo = repos.get( i );
            String dir = "repo-" + i;
            long repoKey = validatedRepoKey( repo.repoKey() );

            Artifact branches = copyToObject( copyManager, "COPY (SELECT branch FROM branch WHERE repo_key = " + repoKey +
                " ORDER BY branch) TO STDOUT", location + dir + "/branches.copy.gz" );
            Artifact versions = copyToObject( copyManager,
                                              "COPY (SELECT version_id, node_id, node_path, ts, node_data_hash, index_config_hash, " +
                                                  "acl_hash, binary_keys, commit_id, attributes FROM node_version WHERE repo_key = " +
                                                  repoKey + " ORDER BY version_id) TO STDOUT", location + dir + "/versions.copy.gz" );
            Artifact heads = copyToObject( copyManager, "COPY (SELECT branch, node_id, version_id, node_path, ts FROM branch_entry " +
                "WHERE repo_key = " + repoKey + " ORDER BY branch, node_id) TO STDOUT", location + dir + "/heads.copy.gz" );
            Artifact commits = copyToObject( copyManager, "COPY (SELECT commit_id, message, committer, ts FROM node_commit " +
                "WHERE repo_key = " + repoKey + " ORDER BY commit_id) TO STDOUT", location + dir + "/commits.copy.gz" );
            Artifact documents = copyToObject( copyManager, "COPY (SELECT branch, node_id, doc, analyzer, ts FROM search_document " +
                "WHERE repo_key = " + repoKey + " ORDER BY branch, node_id) TO STDOUT", location + dir + "/documents.copy.gz" );

            artifacts.add( branches );
            artifacts.add( versions );
            artifacts.add( heads );
            artifacts.add( commits );
            artifacts.add( documents );

            versionCount += versions.rows();
            headCount += heads.rows();
            commitCount += commits.rows();
            documentCount += documents.rows();

            ObjectNode repoNode = repoNodes.addObject();
            repoNode.put( "repoId", repo.repoId() );
            repoNode.put( "dir", dir );
            repoNode.set( "settings", readTree( repo.settingsJson() ) );
            repoNode.set( "data", readTree( repo.dataJson() ) );
            repoNode.put( "branchCount", branches.rows() );
            repoNode.put( "versionCount", versions.rows() );
            repoNode.put( "headCount", heads.rows() );
            repoNode.put( "commitCount", commits.rows() );
            repoNode.put( "documentCount", documents.rows() );
        }

        // The FULL sorted hash manifest (ratified #2): every DISTINCT payload hash and binary
        // key referenced by the snapshot's node_version rows, as its own objects. repo_key IN
        // (-1) when the repo set is empty: repo_key is GENERATED ALWAYS AS IDENTITY (> 0), so
        // -1 matches nothing and keeps the SQL shape uniform.
        String repoKeyList = repos.isEmpty() ? "-1" : repos.stream()
            .map( repo -> Long.toString( validatedRepoKey( repo.repoKey() ) ) )
            .collect( Collectors.joining( ", " ) );

        Artifact payloadHashes = copyToObject( copyManager, "COPY (SELECT node_data_hash AS h FROM node_version WHERE repo_key IN (" +
            repoKeyList + ") UNION SELECT index_config_hash FROM node_version WHERE repo_key IN (" + repoKeyList +
            ") UNION SELECT acl_hash FROM node_version WHERE repo_key IN (" + repoKeyList + ") ORDER BY h) TO STDOUT",
                                               location + "hashes/payloads.txt.gz" );
        Artifact binaryKeys = copyToObject( copyManager, "COPY (SELECT DISTINCT bk FROM node_version, unnest(binary_keys) AS bk " +
            "WHERE repo_key IN (" + repoKeyList + ") ORDER BY bk) TO STDOUT", location + "hashes/binaries.txt.gz" );
        artifacts.add( payloadHashes );
        artifacts.add( binaryKeys );
        long hashCount = payloadHashes.rows() + binaryKeys.rows();

        // MANIFEST.json last: it lists every artifact with its sha256, so its own hash
        // (recorded on the registry row) covers the whole artifact set transitively.
        ObjectNode manifest = MAPPER.createObjectNode();
        manifest.put( "formatVersion", FORMAT_VERSION );
        manifest.put( "snapshotId", snapshotId );
        manifest.put( "tenant", tenant.tenantId() );
        manifest.put( "scope", scope );
        if ( repoIdOrNull != null )
        {
            manifest.put( "repoId", repoIdOrNull );
        }
        manifest.put( "outboxSeq", outboxSeq );
        manifest.set( "repos", repoNodes );
        ArrayNode artifactNodes = manifest.putArray( "artifacts" );
        long totalBytes = 0;
        for ( Artifact artifact : artifacts )
        {
            ObjectNode node = artifactNodes.addObject();
            node.put( "key", artifact.key().substring( location.length() ) );
            node.put( "rows", artifact.rows() );
            node.put( "bytes", artifact.bytes() );
            node.put( "sha256", artifact.sha256() );
            totalBytes += artifact.bytes();
        }

        byte[] manifestBytes = manifest.toPrettyString().getBytes( StandardCharsets.UTF_8 );
        SnapshotObjectStore.ObjectWriter manifestWriter = objects.write( location + MANIFEST_KEY );
        try
        {
            manifestWriter.write( manifestBytes );
            manifestWriter.close();
        }
        catch ( IOException | RuntimeException e )
        {
            manifestWriter.abort();
            throw asUnchecked( e );
        }
        totalBytes += manifestBytes.length;

        return new Capture( outboxSeq, versionCount, headCount, commitCount, documentCount, hashCount, totalBytes,
                            "sha256:" + HexFormat.of().formatHex( sha256().digest( manifestBytes ) ) );
    }

    /**
     * One COPY stream → gzip → S3 object. The digest and byte count are taken over the
     * object's STORED bytes (post-gzip), which is what restore re-verifies against the
     * manifest. Row count is the COPY's own handled-row count.
     */
    private Artifact copyToObject( CopyManager copyManager, String copySql, String key )
        throws SQLException
    {
        SnapshotObjectStore.ObjectWriter writer = objects.write( key );
        try
        {
            CountingDigestOutputStream counted = new CountingDigestOutputStream( writer );
            GZIPOutputStream gzip = new GZIPOutputStream( counted, 64 * 1024 );
            long rows = copyManager.copyOut( copySql, gzip );
            gzip.close(); // finishes the gzip trailer and closes the writer -> object durable
            return new Artifact( key, rows, counted.bytes(), "sha256:" + counted.hexDigest() );
        }
        catch ( SQLException e )
        {
            writer.abort();
            throw e;
        }
        catch ( IOException | RuntimeException e )
        {
            writer.abort();
            throw asUnchecked( e );
        }
    }

    private List<RepoMeta> listRepos( Connection connection, String repoIdOrNull )
        throws SQLException
    {
        String sql = "SELECT repo_key, repo_id, settings::text, data::text FROM repository" +
            ( repoIdOrNull == null ? "" : " WHERE repo_id = ?" ) + " ORDER BY repo_key";
        try (PreparedStatement statement = connection.prepareStatement( sql ))
        {
            if ( repoIdOrNull != null )
            {
                statement.setString( 1, repoIdOrNull );
            }
            try (ResultSet resultSet = statement.executeQuery())
            {
                List<RepoMeta> repos = new ArrayList<>();
                while ( resultSet.next() )
                {
                    repos.add( new RepoMeta( resultSet.getLong( 1 ), resultSet.getString( 2 ), resultSet.getString( 3 ),
                                             resultSet.getString( 4 ) ) );
                }
                return List.copyOf( repos );
            }
        }
    }

    private void markFailedBestEffort( TenantContext tenant, String snapshotId )
    {
        try
        {
            Tx.inTenantTx( dataSource, tenant, connection -> {
                SnapshotRegistry.markFailed( connection, snapshotId );
                return null;
            } );
        }
        catch ( SQLException | RuntimeException e )
        {
            LOG.warn( "Could not mark snapshot {} FAILED after a create failure; the row stays CREATING (identifiable, deletable)",
                      snapshotId, e );
        }
    }

    /**
     * repo_key is {@code GENERATED ALWAYS AS IDENTITY}, read back from the database in this
     * same transaction — but identifiers/literals interpolated into COPY SQL (COPY takes no
     * bind parameters) are validated defensively regardless, the same posture as
     * {@code RepositoryLifecycle#partitionSuffix}.
     */
    private static long validatedRepoKey( long repoKey )
    {
        if ( repoKey <= 0 )
        {
            throw new IllegalStateException( "Invalid repo_key: " + repoKey );
        }
        return repoKey;
    }

    private static void validateSnapshotId( String snapshotId )
    {
        if ( snapshotId == null || snapshotId.isEmpty() || snapshotId.contains( "/" ) )
        {
            throw new IllegalArgumentException( "Invalid snapshot id: " + snapshotId );
        }
    }

    private static com.fasterxml.jackson.databind.JsonNode readTree( String json )
    {
        try
        {
            return MAPPER.readTree( json == null ? "{}" : json );
        }
        catch ( JsonProcessingException e )
        {
            throw new IllegalStateException( "Corrupt repository settings/data JSON", e );
        }
    }

    private static RuntimeException asUnchecked( Exception e )
    {
        if ( e instanceof RuntimeException runtime )
        {
            return runtime;
        }
        return new UncheckedIOException( (IOException) e );
    }

    private static MessageDigest sha256()
    {
        try
        {
            return MessageDigest.getInstance( "SHA-256" );
        }
        catch ( NoSuchAlgorithmException e )
        {
            // SHA-256 is a mandatory JCE algorithm on every JVM (same reasoning as PayloadStore).
            throw new IllegalStateException( e );
        }
    }

    private record RepoMeta(long repoKey, String repoId, String settingsJson, String dataJson)
    {
    }

    private record Artifact(String key, long rows, long bytes, String sha256)
    {
    }

    private record Capture(long outboxSeq, long versionCount, long headCount, long commitCount, long documentCount, long hashCount,
                           long totalBytes, String manifestSha256)
    {
    }

    /** Counts and digests exactly the bytes that reach the wrapped (S3) stream. */
    private static final class CountingDigestOutputStream
        extends FilterOutputStream
    {
        private final MessageDigest digest = sha256();

        private long bytes;

        CountingDigestOutputStream( OutputStream out )
        {
            super( out );
        }

        @Override
        public void write( int b )
            throws IOException
        {
            out.write( b );
            digest.update( (byte) b );
            bytes++;
        }

        @Override
        public void write( byte[] buffer, int offset, int length )
            throws IOException
        {
            out.write( buffer, offset, length );
            digest.update( buffer, offset, length );
            bytes += length;
        }

        long bytes()
        {
            return bytes;
        }

        String hexDigest()
        {
            return HexFormat.of().formatHex( digest.digest() );
        }
    }
}
