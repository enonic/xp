package com.enonic.nodb.engine.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
     * Multi-get by node_id (mirrors spi.NodeStore#getBranchEntries): returns only the
     * entries found — missing ids are simply absent, in no particular order.
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
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setArray( 3, connection.createArrayOf( "text", nodeIds.toArray( new String[0] ) ) );
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
