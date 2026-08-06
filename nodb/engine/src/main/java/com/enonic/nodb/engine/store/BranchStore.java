package com.enonic.nodb.engine.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.enonic.nodb.engine.model.BranchEntryPage;
import com.enonic.nodb.engine.model.BranchEntryRecord;
import com.enonic.nodb.engine.model.Page;
import com.enonic.nodb.engine.model.RepoRef;

/**
 * BRANCH document equivalent (schema.sql {@code branch_entry}). LIST-partitioned by
 * repo_key with a per-repo sub-partition further LIST-partitioned by branch (see
 * {@link RepositoryLifecycle} for the DEFAULT-sub-partition choice made for this slice).
 */
public final class BranchStore
{
    /**
     * Shared read-side projection: every {@code branch_entry} read joins {@code node_version}
     * ON {@code (repo_key, version_id)} (the FK already declared in schema.sql) to recover
     * {@code node_data_hash}/{@code index_config_hash}/{@code acl_hash} in the same query --
     * the Phase 1 Gate C N+1 fix (BUILD-PHASE-1.md): these three columns live only in
     * {@code node_version}, not {@code branch_entry}, but the XP SPI's BranchEntryRecord (and
     * now proto.BranchEntry) carry them, so a single JOIN replaces what used to be a
     * follow-up {@code GetVersion} call per branch-entry read. The join is a plain inner
     * join, not a LEFT JOIN: the FK guarantees exactly one matching {@code node_version} row
     * for every {@code branch_entry} row, and all three hash columns are NOT NULL there.
     */
    private static final String JOINED_SELECT = """
        SELECT be.branch, be.node_id, be.version_id, be.node_path, be.ts,
               nv.node_data_hash, nv.index_config_hash, nv.acl_hash
        FROM branch_entry be
        JOIN node_version nv ON nv.repo_key = be.repo_key AND nv.version_id = be.version_id
        """;

    /**
     * {@link #JOINED_SELECT} plus the keyset column. {@code lower(node_path)} is returned by the
     * server rather than recomputed by the caller on purpose: the cursor must be compared by
     * PostgreSQL's {@code lower()} under the database's collation, and having Java produce it with
     * {@code toLowerCase} would put a second, subtly different lowercasing on the critical path of
     * a paging predicate — the classic way a walk skips or repeats rows.
     */
    private static final String LISTING_SELECT = """
        SELECT be.branch, be.node_id, be.version_id, be.node_path, be.ts,
               nv.node_data_hash, nv.index_config_hash, nv.acl_hash,
               lower(be.node_path) AS path_key
        FROM branch_entry be
        JOIN node_version nv ON nv.repo_key = be.repo_key AND nv.version_id = be.version_id
        """;

    /**
     * The listing's sort/keyset key: the lowercased path in {@code "C"} (byte) collation.
     *
     * <p>{@code COLLATE "C"} is load-bearing twice over. <b>Parity:</b> the ES path field these
     * listings replace was a lowercased keyword, sorted byte-lexicographically — while this
     * database's default collation is {@code en_US.utf8}, which sorts punctuation at a secondary
     * level and would order {@code /content-sibling} between {@code /content/a} and
     * {@code /content/b} rather than before both. <b>Index:</b> {@code branch_entry_path_lower} is
     * a {@code text_pattern_ops} index (migration 002), i.e. byte-ordered, so this is the only
     * collation under which one index serves the prefix scan AND the ordered keyset walk.
     *
     * <p>The one property the delete cascade actually depends on — a node's descendants sorting
     * after it, hence BEFORE it when descending — holds in any collation, since a strict prefix
     * always compares less than its extensions. The collation choice is about matching ES's
     * observable order, not about correctness of the cascade.
     */
    private static final String PATH_KEY = "lower(be.node_path) COLLATE \"C\"";

    private BranchStore()
    {
    }

    /** {@link RepoRef}-addressed variant — see {@link #getByNodeId(Connection, RepoRef, String, String)}. */
    public static void store( Connection connection, RepoRef repo, BranchEntryRecord entry )
        throws SQLException
    {
        store( connection, RepoKeys.resolve( connection, repo ), entry );
    }

    /**
     * Upsert: {@code (repo_key, branch, node_id)} is the PK. {@code branch_entry} has an FK
     * to an existing {@code branch} row (schema.sql), so the first write into a branch value
     * NoDB has never seen before is auto-vivified here — the same way {@link
     * WriteService#forkBranch} already auto-creates its own target branch row — rather than
     * requiring a separate branch-create call first. XP has no bulk branch-copy operation of
     * its own (see BUILD-PHASE-1.md's Gate 0 finding): {@code RepositoryServiceImpl.createBranch()}
     * is just a single {@code storeBranchEntry}-equivalent write of the root node into a new
     * branch value, so this is the one place that write needs to succeed without a prior
     * explicit branch-create RPC, matching ES's implicit-branch semantics (a "branch" was
     * never a first-class entity there, just a field value on documents).
     */
    public static void store( Connection connection, long repoKey, BranchEntryRecord entry )
        throws SQLException
    {
        ensureBranch( connection, repoKey, entry.branch() );
        try (PreparedStatement statement = connection.prepareStatement( """
            INSERT INTO branch_entry (repo_key, branch, node_id, version_id, node_path, ts)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (repo_key, branch, node_id) DO UPDATE
                SET version_id = EXCLUDED.version_id, node_path = EXCLUDED.node_path, ts = EXCLUDED.ts
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, entry.branch() );
            statement.setString( 3, entry.nodeId() );
            statement.setString( 4, entry.versionId() );
            statement.setString( 5, entry.nodePath() );
            statement.setTimestamp( 6, Timestamp.from( entry.timestamp() ) );
            statement.executeUpdate();
        }
    }

    /**
     * {@code branch} is a plain DML row (not DDL), so this is safe to run under a tenant
     * role's INSERT grant ({@link com.enonic.nodb.engine.Tx#inTenantTx}) — same posture as
     * {@link WriteService#forkBranch}'s own identical statement for its target branch.
     */
    private static void ensureBranch( Connection connection, long repoKey, String branch )
        throws SQLException
    {
        try (PreparedStatement statement =
                 connection.prepareStatement( "INSERT INTO branch (repo_key, branch) VALUES (?, ?) ON CONFLICT DO NOTHING" ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.executeUpdate();
        }
    }

    /**
     * Same as {@link #getByNodeId(Connection, long, String, String)}, addressed by the
     * external {@link RepoRef} instead of the surrogate repo_key — the shape callers
     * outside this package (e.g. the gRPC server, which only ever sees repo ids off the
     * wire) actually have. Resolves via {@link RepoKeys}, same as {@link WriteService}.
     */
    public static BranchEntryRecord getByNodeId( Connection connection, RepoRef repo, String branch, String nodeId )
        throws SQLException
    {
        return getByNodeId( connection, RepoKeys.resolve( connection, repo ), branch, nodeId );
    }

    public static BranchEntryRecord getByNodeId( Connection connection, long repoKey, String branch, String nodeId )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( JOINED_SELECT + """
            WHERE be.repo_key = ? AND be.branch = ? AND be.node_id = ?
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setString( 3, nodeId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? map( resultSet ) : null;
            }
        }
    }

    /** {@link RepoRef}-addressed variant — see {@link #getByNodeId(Connection, RepoRef, String, String)}. */
    public static boolean existsByNodeId( Connection connection, RepoRef repo, String branch, String nodeId )
        throws SQLException
    {
        return existsByNodeId( connection, RepoKeys.resolve( connection, repo ), branch, nodeId );
    }

    /**
     * Existence check without fetching the entry's fields (mirrors spi.NodeStore#existsBranchEntry:
     * "no ES {@code _source} fetch"). {@code LIMIT 1} makes this a plain index probe rather
     * than a full row materialization — a genuine behavioral difference from {@link
     * #getByNodeId(Connection, long, String, String)}{@code  != null}, not just a duplicate query.
     */
    public static boolean existsByNodeId( Connection connection, long repoKey, String branch, String nodeId )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT 1 FROM branch_entry WHERE repo_key = ? AND branch = ? AND node_id = ? LIMIT 1" ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setString( 3, nodeId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }
    }

    /** {@link RepoRef}-addressed variant — see {@link #getByNodeId(Connection, RepoRef, String, String)}. */
    public static List<BranchEntryRecord> getByNodeIds( Connection connection, RepoRef repo, String branch, Collection<String> nodeIds )
        throws SQLException
    {
        return getByNodeIds( connection, RepoKeys.resolve( connection, repo ), branch, nodeIds );
    }

    /**
     * Multi-get by node_id (mirrors spi.NodeStore#getBranchEntries): returns only the entries found
     * — missing ids are simply absent — <b>in the order the ids were asked for</b>.
     *
     * <p>The ordering is not a nicety, and it was found by Gate D rather than reasoned about. XP's
     * {@code BranchServiceImpl.get(Iterable&lt;NodeId&gt;)} passes this result straight through to
     * {@code Nodes}, and Elasticsearch's multi-get answers in the REQUESTED order — so
     * {@code nodeService.getByIds} has always been order-preserving, and callers depend on it.
     * The obvious ones are the itests that take a query's ordered {@code getNodeIds()} and then
     * assert the order of {@code getNodes(ids)}: without this clause a perfectly correct sort comes
     * back scrambled, which is what made all 16 {@code FindNodesByQueryCommandTest_icuSort} cases
     * fail while the corpus's ICU rows — which read hit ids straight off the search result — passed.
     * A plain {@code = ANY} returns heap/index order, which for generated node ids looks random and
     * differs run to run: a silent reordering, not an error.
     *
     * <p>{@code array_position} over the same array the predicate uses: the set is already bounded
     * by the {@code IN}-list, so the sort is over a handful of rows and needs no index.
     */
    public static List<BranchEntryRecord> getByNodeIds( Connection connection, long repoKey, String branch, Collection<String> nodeIds )
        throws SQLException
    {
        if ( nodeIds.isEmpty() )
        {
            return List.of();
        }
        try (PreparedStatement statement = connection.prepareStatement( JOINED_SELECT + """
            WHERE be.repo_key = ? AND be.branch = ? AND be.node_id = ANY(?)
            ORDER BY array_position(?, be.node_id)
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setArray( 3, connection.createArrayOf( "text", nodeIds.toArray( new String[0] ) ) );
            statement.setArray( 4, connection.createArrayOf( "text", nodeIds.toArray( new String[0] ) ) );
            try (ResultSet resultSet = statement.executeQuery())
            {
                List<BranchEntryRecord> result = new ArrayList<>();
                while ( resultSet.next() )
                {
                    result.add( map( resultSet ) );
                }
                return List.copyOf( result );
            }
        }
    }

    /** {@link RepoRef}-addressed variant — see {@link #getByNodeId(Connection, RepoRef, String, String)}. */
    public static List<String> getBranchesWithNode( Connection connection, RepoRef repo, String nodeId )
        throws SQLException
    {
        return getBranchesWithNode( connection, RepoKeys.resolve( connection, repo ), nodeId );
    }

    /**
     * Branches containing the given node — replaces the cross-branch storage-index query
     * spi.NodeStore#getBranchesWithNode used to need ES for.
     */
    public static List<String> getBranchesWithNode( Connection connection, long repoKey, String nodeId )
        throws SQLException
    {
        try (PreparedStatement statement =
                 connection.prepareStatement( "SELECT DISTINCT branch FROM branch_entry WHERE repo_key = ? AND node_id = ?" ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, nodeId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                List<String> result = new ArrayList<>();
                while ( resultSet.next() )
                {
                    result.add( resultSet.getString( 1 ) );
                }
                return List.copyOf( result );
            }
        }
    }

    /** {@link RepoRef}-addressed variant — see {@link #getByNodeId(Connection, RepoRef, String, String)}. */
    public static BranchEntryRecord getByPath( Connection connection, RepoRef repo, String branch, String nodePath )
        throws SQLException
    {
        return getByPath( connection, RepoKeys.resolve( connection, repo ), branch, nodePath );
    }

    public static BranchEntryRecord getByPath( Connection connection, long repoKey, String branch, String nodePath )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( JOINED_SELECT + """
            WHERE be.repo_key = ? AND be.branch = ? AND be.node_path = ?
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setString( 3, nodePath );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? map( resultSet ) : null;
            }
        }
    }

    /**
     * Children of {@code parentPath}, ordered by node_path. {@code parent_path} is a
     * generated column (schema.sql): {@code NULL} for the root node itself, and the empty
     * string {@code ""} for direct children of root (regexp-stripping "/child" from
     * "/child" leaves ""), so the conventional root path "/" is translated to "" here to
     * match that generated-column convention.
     */
    /** {@link RepoRef}-addressed variant — see {@link #getByNodeId(Connection, RepoRef, String, String)}. */
    public static List<BranchEntryRecord> getChildren( Connection connection, RepoRef repo, String branch, String parentPath, Page page )
        throws SQLException
    {
        return getChildren( connection, RepoKeys.resolve( connection, repo ), branch, parentPath, page );
    }

    public static List<BranchEntryRecord> getChildren( Connection connection, long repoKey, String branch, String parentPath, Page page )
        throws SQLException
    {
        String parentPathKey = "/".equals( parentPath ) ? "" : parentPath;
        try (PreparedStatement statement = connection.prepareStatement( JOINED_SELECT + """
            WHERE be.repo_key = ? AND be.branch = ? AND be.parent_path = ?
            ORDER BY be.node_path
            OFFSET ? LIMIT ?
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setString( 3, parentPathKey );
            statement.setInt( 4, page.from() );
            statement.setInt( 5, page.size() );
            try (ResultSet resultSet = statement.executeQuery())
            {
                List<BranchEntryRecord> result = new ArrayList<>();
                while ( resultSet.next() )
                {
                    result.add( map( resultSet ) );
                }
                return List.copyOf( result );
            }
        }
    }

    /**
     * Branch-entry LISTING (Phase 4 decision D2, nodb/BUILD-PHASE-4.md): one keyset page of a
     * subtree or of a whole branch.
     *
     * <p>Reproduces what the three {@code NodeBranchQuery} call sites used to ask the ES storage
     * index. {@code pathPrefix} {@code null} means the whole branch; otherwise the STRICT subtree
     * below it ({@code lower(node_path) LIKE lower(prefix) || '/%'} — the prefix row itself is
     * excluded, which is exactly what {@code DeleteNodeCommand}'s {@code like '<path>/*'} means;
     * that command adds the node itself back separately).
     *
     * <p><b>Everything is evaluated on {@code lower(node_path)}</b>, for two reasons that
     * coincide: parity (the ES path field is lowercased and {@code NodePath} equality ignores
     * case) and the index — {@code branch_entry_path_lower (repo_key, branch,
     * lower(node_path) text_pattern_ops)}, added by migration 002, is the only index that can
     * serve both the prefix scan and the ordered keyset walk. The DB collation is
     * {@code en_US.utf8} with no {@code text_pattern_ops} on the unique path index, so ordering
     * by the raw column would neither use that index nor sort like the ES field did.
     *
     * <p>The keyset is {@code (lower(node_path), node_id)} — a total order even if two rows ever
     * collided on the lowercased path — and it is EXCLUSIVE: a page resumes strictly after
     * {@code afterPath}/{@code afterNodeId}. One row beyond {@code pageSize} is fetched to
     * answer {@code hasMore} without an extra empty round trip.
     */
    public static BranchEntryPage listEntries( Connection connection, RepoRef repo, String branch, String pathPrefix, boolean descending,
                                                String afterPath, String afterNodeId, int pageSize, boolean withTotal )
        throws SQLException
    {
        return listEntries( connection, RepoKeys.resolve( connection, repo ), branch, pathPrefix, descending, afterPath, afterNodeId,
                             pageSize, withTotal );
    }

    public static BranchEntryPage listEntries( Connection connection, long repoKey, String branch, String pathPrefix, boolean descending,
                                                String afterPath, String afterNodeId, int pageSize, boolean withTotal )
        throws SQLException
    {
        String prefixPattern = pathPrefix == null ? null : escapeLike( pathPrefix.toLowerCase( Locale.ROOT ) ) + "/%";

        long totalHits = withTotal ? countEntries( connection, repoKey, branch, prefixPattern ) : BranchEntryPage.NO_TOTAL;

        String direction = descending ? "DESC" : "ASC";
        String comparison = descending ? "<" : ">";
        StringBuilder sql = new StringBuilder( LISTING_SELECT ).append( " WHERE be.repo_key = ? AND be.branch = ?" );
        if ( prefixPattern != null )
        {
            sql.append( " AND lower(be.node_path) LIKE ?" );
        }
        if ( afterPath != null && !afterPath.isEmpty() )
        {
            sql.append( " AND (" )
                .append( PATH_KEY )
                .append( ' ' )
                .append( comparison )
                .append( " ? OR (lower(be.node_path) = ? AND be.node_id COLLATE \"C\" " )
                .append( comparison )
                .append( " ?))" );
        }
        sql.append( " ORDER BY " )
            .append( PATH_KEY )
            .append( ' ' )
            .append( direction )
            .append( ", be.node_id COLLATE \"C\" " )
            .append( direction )
            .append( " LIMIT ?" );

        try (PreparedStatement statement = connection.prepareStatement( sql.toString() ))
        {
            int index = 1;
            statement.setLong( index++, repoKey );
            statement.setString( index++, branch );
            if ( prefixPattern != null )
            {
                statement.setString( index++, prefixPattern );
            }
            if ( afterPath != null && !afterPath.isEmpty() )
            {
                statement.setString( index++, afterPath );
                statement.setString( index++, afterPath );
                statement.setString( index++, afterNodeId == null ? "" : afterNodeId );
            }
            statement.setInt( index, pageSize + 1 );

            List<BranchEntryRecord> entries = new ArrayList<>();
            String nextPath = "";
            String nextNodeId = "";
            boolean hasMore = false;
            try (ResultSet resultSet = statement.executeQuery())
            {
                while ( resultSet.next() )
                {
                    if ( entries.size() == pageSize )
                    {
                        hasMore = true;
                        break;
                    }
                    entries.add( map( resultSet ) );
                    nextPath = resultSet.getString( "path_key" );
                    nextNodeId = resultSet.getString( "node_id" );
                }
            }
            return new BranchEntryPage( entries, nextPath, nextNodeId, hasMore, totalHits );
        }
    }

    /**
     * The up-front total the listing's consumers need before iterating
     * ({@code ReindexListener#branch(repositoryId, branch, size)} reports it, and the ES path
     * read it off the search response). A plain indexed count on the same predicate as the walk.
     */
    private static long countEntries( Connection connection, long repoKey, String branch, String prefixPattern )
        throws SQLException
    {
        String sql = "SELECT count(*) FROM branch_entry WHERE repo_key = ? AND branch = ?" +
            ( prefixPattern == null ? "" : " AND lower(node_path) LIKE ?" );
        try (PreparedStatement statement = connection.prepareStatement( sql ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            if ( prefixPattern != null )
            {
                statement.setString( 3, prefixPattern );
            }
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? resultSet.getLong( 1 ) : 0;
            }
        }
    }

    /** {@link RepoRef}-addressed variant — see {@link #getByNodeId(Connection, RepoRef, String, String)}. */
    public static List<String> diffBranches( Connection connection, RepoRef repo, String source, String target, String pathScope,
                                              Collection<String> excludes, int limit )
        throws SQLException
    {
        return diffBranches( connection, RepoKeys.resolve( connection, repo ), source, target, pathScope, excludes, limit );
    }

    /**
     * Branch diff / resolve-sync-work (Phase 3.5 Gate A): DISTINCT ids of nodes present in
     * exactly one of (source, target), or present in both with different version ids —
     * {@code branch_entry} semantics as pinned by Gate 0 (nodb/BUILD-PHASE-3.5.md; the ES
     * {@code DiffQueryFactory} query is the reference, not the specification):
     * <ul>
     *   <li>the scope root itself IS included ({@code path = scope OR path LIKE scope/%});</li>
     *   <li>path comparison is CASE-INSENSITIVE ({@code lower(node_path)}, matching both the
     *   lowercased ES path index and the {@code branch_entry_path_lower} index that serves
     *   these predicates);</li>
     *   <li>scope and excludes are evaluated PER SIDE — source rows against the source
     *   branch's own paths, target rows against the target branch's — the form that makes
     *   renames behave (a node moved out of scope in one branch still diffs via the branch
     *   where it is in scope);</li>
     *   <li>excludes match EXACT paths only (case-insensitively), never subtrees — the ES
     *   reference is a {@code terms} query on full lowercased paths, which is what makes
     *   the HasUnpublishedChildren pattern (scope = parent, excludes = [parent]) mean
     *   "children of parent, excluding parent itself";</li>
     *   <li>a node in both branches with different version ids yields ONE id (the GROUP BY
     *   dedups, even when the paths differ between sides); same version id in both — absent.</li>
     *   <li>result order is deterministic: ascending by the node's (lowercased) path, the
     *   smaller of the two sides' paths when they differ — parents always precede their
     *   children, the order the search-index path yields and resolve-sync-work consumers
     *   assert (Phase 3.5 Gate B).</li>
     * </ul>
     * {@code pathScope} {@code null} means the whole branch (callers pass {@code null}, not
     * {@code "/"}, for a root scope — the XP commands normalize). {@code limit <= 0} means
     * all ids; {@code limit = 1} is the cheap existence-only probe (HasUnpublishedChildren).
     */
    public static List<String> diffBranches( Connection connection, long repoKey, String source, String target, String pathScope,
                                              Collection<String> excludes, int limit )
        throws SQLException
    {
        String sideConditions = "";
        if ( pathScope != null )
        {
            sideConditions += " AND (lower(side.node_path) = ? OR lower(side.node_path) LIKE ?)";
        }
        if ( !excludes.isEmpty() )
        {
            sideConditions += " AND NOT (lower(side.node_path) = ANY(?))";
        }
        String oneSide = """
            SELECT side.node_id, lower(side.node_path) AS node_path
            FROM branch_entry side
            LEFT JOIN branch_entry other ON other.repo_key = ? AND other.branch = ? AND other.node_id = side.node_id
            WHERE side.repo_key = ? AND side.branch = ?
              AND (other.node_id IS NULL OR other.version_id <> side.version_id)
            """ + sideConditions;
        String sql = "SELECT node_id FROM (" + oneSide + " UNION " + oneSide + ") diff GROUP BY node_id ORDER BY min(node_path)" +
            ( limit > 0 ? " LIMIT ?" : "" );

        try (PreparedStatement statement = connection.prepareStatement( sql ))
        {
            int index = bindDiffSide( statement, 1, connection, repoKey, source, target, pathScope, excludes );
            index = bindDiffSide( statement, index, connection, repoKey, target, source, pathScope, excludes );
            if ( limit > 0 )
            {
                statement.setInt( index, limit );
            }
            try (ResultSet resultSet = statement.executeQuery())
            {
                List<String> result = new ArrayList<>();
                while ( resultSet.next() )
                {
                    result.add( resultSet.getString( 1 ) );
                }
                return List.copyOf( result );
            }
        }
    }

    private static int bindDiffSide( PreparedStatement statement, int index, Connection connection, long repoKey, String side,
                                      String other, String pathScope, Collection<String> excludes )
        throws SQLException
    {
        statement.setLong( index++, repoKey );
        statement.setString( index++, other );
        statement.setLong( index++, repoKey );
        statement.setString( index++, side );
        if ( pathScope != null )
        {
            String lowered = pathScope.toLowerCase();
            statement.setString( index++, lowered );
            statement.setString( index++, escapeLike( lowered ) + "/%" );
        }
        if ( !excludes.isEmpty() )
        {
            statement.setArray( index++, connection.createArrayOf( "text", excludes.stream()
                .map( String::toLowerCase )
                .toArray( String[]::new ) ) );
        }
        return index;
    }

    /** LIKE-pattern escaping for the scope prefix: node paths may legally contain {@code _} (and in principle {@code %}). */
    private static String escapeLike( String value )
    {
        return value.replace( "\\", "\\\\" ).replace( "%", "\\%" ).replace( "_", "\\_" );
    }

    /** {@link RepoRef}-addressed variant — see {@link #getByNodeId(Connection, RepoRef, String, String)}. */
    public static void delete( Connection connection, RepoRef repo, String branch, Collection<String> nodeIds )
        throws SQLException
    {
        delete( connection, RepoKeys.resolve( connection, repo ), branch, nodeIds );
    }

    public static void delete( Connection connection, long repoKey, String branch, Collection<String> nodeIds )
        throws SQLException
    {
        if ( nodeIds.isEmpty() )
        {
            return;
        }
        try (PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM branch_entry WHERE repo_key = ? AND branch = ? AND node_id = ANY(?)" ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setArray( 3, connection.createArrayOf( "text", nodeIds.toArray( new String[0] ) ) );
            statement.executeUpdate();
        }
    }

    /** Maps a row from {@link #JOINED_SELECT} -- includes the joined node_version hash columns. */
    private static BranchEntryRecord map( ResultSet resultSet )
        throws SQLException
    {
        return new BranchEntryRecord( resultSet.getString( "branch" ), resultSet.getString( "node_id" ),
                                       resultSet.getString( "version_id" ), resultSet.getString( "node_path" ),
                                       resultSet.getTimestamp( "ts" ).toInstant(), resultSet.getString( "node_data_hash" ),
                                       resultSet.getString( "index_config_hash" ), resultSet.getString( "acl_hash" ) );
    }
}
