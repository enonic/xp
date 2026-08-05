package com.enonic.nodb.engine.store;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.model.VersionQuery;
import com.enonic.nodb.engine.model.VersionQueryResult;
import com.enonic.nodb.engine.model.VersionRecord;

/**
 * VERSION document equivalent (schema.sql {@code node_version}, LIST-partitioned by
 * repo_key with no default partition — callers must have created the repo's partition
 * via {@link RepositoryLifecycle#createRepository} before storing).
 */
public final class VersionStore
{
    private VersionStore()
    {
    }

    /** {@link RepoRef}-addressed variant — see {@link BranchStore#getByNodeId(Connection, RepoRef, String, String)}. */
    public static void store( Connection connection, RepoRef repo, VersionRecord version )
        throws SQLException
    {
        store( connection, RepoKeys.resolve( connection, repo ), version );
    }

    public static void store( Connection connection, long repoKey, VersionRecord version )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            INSERT INTO node_version
                (repo_key, version_id, node_id, node_path, ts, node_data_hash, index_config_hash, acl_hash, binary_keys, commit_id, attributes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb)
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, version.versionId() );
            statement.setString( 3, version.nodeId() );
            statement.setString( 4, version.nodePath() );
            statement.setTimestamp( 5, Timestamp.from( version.timestamp() ) );
            statement.setString( 6, version.nodeDataHash() );
            statement.setString( 7, version.indexConfigHash() );
            statement.setString( 8, version.aclHash() );
            statement.setArray( 9, connection.createArrayOf( "text", version.binaryKeys().toArray( new String[0] ) ) );
            statement.setString( 10, version.commitId() );
            statement.setString( 11, JsonAttributes.toJson( version.attributes() ) );
            statement.executeUpdate();
        }
    }

    /** {@link RepoRef}-addressed variant — see {@link BranchStore#getByNodeId(Connection, RepoRef, String, String)}. */
    public static VersionRecord get( Connection connection, RepoRef repo, String versionId )
        throws SQLException
    {
        return get( connection, RepoKeys.resolve( connection, repo ), versionId );
    }

    /**
     * Repo-scoped lookup (Phase 3.5 gate P2): version identity is (repo_key, version_id) —
     * the same version_id string may legitimately exist in more than one repo of a tenant,
     * so the repo_key predicate is mandatory, never an optimization. Predicating on the
     * partition key also gives partition pruning for free.
     */
    public static VersionRecord get( Connection connection, long repoKey, String versionId )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            SELECT version_id, node_id, node_path, ts, node_data_hash, index_config_hash, acl_hash, binary_keys, commit_id, attributes
            FROM node_version WHERE repo_key = ? AND version_id = ?
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, versionId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? map( resultSet ) : null;
            }
        }
    }

    /** {@link RepoRef}-addressed variant — see {@link BranchStore#getByNodeId(Connection, RepoRef, String, String)}. */
    public static VersionQueryResult findVersions( Connection connection, RepoRef repo, VersionQuery query )
        throws SQLException
    {
        return findVersions( connection, RepoKeys.resolve( connection, repo ), query );
    }

    /**
     * The bounded findVersions surface (Phase 3.5 Gate A): history-by-node (order
     * {@code ts DESC, version_id ASC} with keyset cursor, served by the
     * {@code node_version_by_node_v2} index), dump/vacuum range scans (ts bounds +
     * {@code version_id} keyset) and blob-key usage checks (GIN containment on
     * {@code binary_keys} / equality on {@code node_data_hash}). Bound inclusivity and the
     * {@code size} convention ({@code 0} = count-only, {@code -1} = all) are documented on
     * {@link VersionQuery}. {@code totalHits} is accurate regardless of the paging window.
     * Repo-scoped like every Phase 3.5 read: the {@code repo_key} predicate is mandatory
     * (gate P2), never an optimization.
     */
    public static VersionQueryResult findVersions( Connection connection, long repoKey, VersionQuery query )
        throws SQLException
    {
        StringBuilder where = new StringBuilder( "WHERE repo_key = ?" );
        List<Object> whereParams = new ArrayList<>();
        whereParams.add( repoKey );
        if ( query.nodeId() != null )
        {
            where.append( " AND node_id = ?" );
            whereParams.add( query.nodeId() );
        }
        if ( query.tsFloor() != null )
        {
            where.append( " AND ts >= ?" );
            whereParams.add( Timestamp.from( query.tsFloor() ) );
        }
        if ( query.tsCeiling() != null )
        {
            where.append( " AND ts <= ?" );
            whereParams.add( Timestamp.from( query.tsCeiling() ) );
        }
        if ( query.versionIdAfter() != null )
        {
            where.append( " AND version_id > ?" );
            whereParams.add( query.versionIdAfter() );
        }
        if ( query.blobKeyTerm() != null )
        {
            switch ( query.blobKeyTerm().field() )
            {
                case BINARY_KEYS ->
                {
                    where.append( " AND binary_keys @> ?" );
                    whereParams.add( connection.createArrayOf( "text", new String[]{query.blobKeyTerm().blobKey()} ) );
                }
                case NODE_DATA_HASH ->
                {
                    where.append( " AND node_data_hash = ?" );
                    whereParams.add( query.blobKeyTerm().blobKey() );
                }
            }
        }
        if ( query.cursor() != null )
        {
            where.append( " AND (ts < ? OR (ts = ? AND version_id > ?))" );
            Timestamp cursorTs = Timestamp.from( query.cursor().ts() );
            whereParams.add( cursorTs );
            whereParams.add( cursorTs );
            whereParams.add( query.cursor().versionId() );
        }

        if ( query.size() == 0 )
        {
            return new VersionQueryResult( countVersions( connection, where.toString(), whereParams ), List.of() );
        }

        StringBuilder sql = new StringBuilder( """
            SELECT version_id, node_id, node_path, ts, node_data_hash, index_config_hash, acl_hash, binary_keys, commit_id, attributes,
                   count(*) OVER () AS total_hits
            FROM node_version
            """ ).append( where );
        switch ( query.order() )
        {
            case TS_DESC_ID_ASC -> sql.append( " ORDER BY ts DESC, version_id ASC" );
            case ID_ASC -> sql.append( " ORDER BY version_id ASC" );
            case UNORDERED ->
            {
            }
        }
        List<Object> params = new ArrayList<>( whereParams );
        if ( query.size() > 0 )
        {
            sql.append( " OFFSET ? LIMIT ?" );
            params.add( query.from() );
            params.add( query.size() );
        }
        else if ( query.from() > 0 )
        {
            sql.append( " OFFSET ?" );
            params.add( query.from() );
        }

        try (PreparedStatement statement = connection.prepareStatement( sql.toString() ))
        {
            bind( statement, params );
            try (ResultSet resultSet = statement.executeQuery())
            {
                long totalHits = 0;
                List<VersionRecord> versions = new ArrayList<>();
                while ( resultSet.next() )
                {
                    totalHits = resultSet.getLong( "total_hits" );
                    versions.add( map( resultSet ) );
                }
                if ( versions.isEmpty() && ( query.from() > 0 || query.cursor() != null ) )
                {
                    // A window past the end returns no rows, so the window-function count
                    // never arrives — fall back to a plain count for an accurate totalHits.
                    totalHits = countVersions( connection, where.toString(), whereParams );
                }
                return new VersionQueryResult( totalHits, List.copyOf( versions ) );
            }
        }
    }

    /** {@link RepoRef}-addressed variant — see {@link BranchStore#getByNodeId(Connection, RepoRef, String, String)}. */
    public static Map<String, VersionRecord> getActiveVersions( Connection connection, RepoRef repo, String nodeId,
                                                                 Collection<String> branches )
        throws SQLException
    {
        return getActiveVersions( connection, RepoKeys.resolve( connection, repo ), nodeId, branches );
    }

    /**
     * Active version of one node per branch, ONE round trip (Phase 3.5 Gate A — the
     * per-branch getBranchEntry+getVersion loop collapsed into the same
     * {@code branch_entry}⋈{@code node_version} join {@link BranchStore}'s reads already
     * use). Branches where the node does not exist are simply absent from the result.
     */
    public static Map<String, VersionRecord> getActiveVersions( Connection connection, long repoKey, String nodeId,
                                                                 Collection<String> branches )
        throws SQLException
    {
        if ( branches.isEmpty() )
        {
            return Map.of();
        }
        try (PreparedStatement statement = connection.prepareStatement( """
            SELECT be.branch, nv.version_id, nv.node_id, nv.node_path, nv.ts, nv.node_data_hash, nv.index_config_hash, nv.acl_hash,
                   nv.binary_keys, nv.commit_id, nv.attributes
            FROM branch_entry be
            JOIN node_version nv ON nv.repo_key = be.repo_key AND nv.version_id = be.version_id
            WHERE be.repo_key = ? AND be.node_id = ? AND be.branch = ANY(?)
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, nodeId );
            statement.setArray( 3, connection.createArrayOf( "text", branches.toArray( new String[0] ) ) );
            try (ResultSet resultSet = statement.executeQuery())
            {
                Map<String, VersionRecord> result = new LinkedHashMap<>();
                while ( resultSet.next() )
                {
                    result.put( resultSet.getString( "branch" ), map( resultSet ) );
                }
                return Map.copyOf( result );
            }
        }
    }

    private static long countVersions( Connection connection, String where, List<Object> whereParams )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "SELECT count(*) FROM node_version " + where ))
        {
            bind( statement, whereParams );
            try (ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                return resultSet.getLong( 1 );
            }
        }
    }

    private static void bind( PreparedStatement statement, List<Object> params )
        throws SQLException
    {
        for ( int i = 0; i < params.size(); i++ )
        {
            Object param = params.get( i );
            if ( param instanceof Long longValue )
            {
                statement.setLong( i + 1, longValue );
            }
            else if ( param instanceof Integer intValue )
            {
                statement.setInt( i + 1, intValue );
            }
            else if ( param instanceof String stringValue )
            {
                statement.setString( i + 1, stringValue );
            }
            else if ( param instanceof Timestamp timestampValue )
            {
                statement.setTimestamp( i + 1, timestampValue );
            }
            else if ( param instanceof Array arrayValue )
            {
                statement.setArray( i + 1, arrayValue );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported parameter type: " + param.getClass() );
            }
        }
    }

    /** {@link RepoRef}-addressed variant — see {@link BranchStore#getByNodeId(Connection, RepoRef, String, String)}. */
    public static void delete( Connection connection, RepoRef repo, String versionId )
        throws SQLException
    {
        delete( connection, RepoKeys.resolve( connection, repo ), versionId );
    }

    /**
     * Vacuum/retention op (DESIGN.md §6, mirrors spi.NodeStore#deleteVersion) — repo-scoped,
     * same reasoning as {@link #get(Connection, long, String)} (Phase 3.5 gate P2).
     * {@code branch_entry} has an FK to {@code node_version}, so deleting a version
     * still referenced by a live branch entry fails with a foreign-key-violation
     * {@link SQLException} (left to the caller to avoid — a Phase 1 itest rarely exercises
     * this directly per BUILD-PHASE-1.md's reconciliation table). A no-op (0 rows affected)
     * for an already-absent version id, matching plain {@code DELETE} semantics.
     */
    public static void delete( Connection connection, long repoKey, String versionId )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "DELETE FROM node_version WHERE repo_key = ? AND version_id = ?" ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, versionId );
            statement.executeUpdate();
        }
    }

    private static VersionRecord map( ResultSet resultSet )
        throws SQLException
    {
        Array sqlArray = resultSet.getArray( "binary_keys" );
        List<String> binaryKeys = new ArrayList<>();
        if ( sqlArray != null )
        {
            for ( Object element : (Object[]) sqlArray.getArray() )
            {
                binaryKeys.add( (String) element );
            }
        }
        Map<String, String> attributes = JsonAttributes.fromJson( resultSet.getString( "attributes" ) );

        return new VersionRecord( resultSet.getString( "version_id" ), resultSet.getString( "node_id" ),
                                   resultSet.getString( "node_path" ), resultSet.getTimestamp( "ts" ).toInstant(),
                                   resultSet.getString( "node_data_hash" ), resultSet.getString( "index_config_hash" ),
                                   resultSet.getString( "acl_hash" ), List.copyOf( binaryKeys ), resultSet.getString( "commit_id" ),
                                   attributes );
    }
}
