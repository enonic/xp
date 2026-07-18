package com.enonic.nodb.engine.store;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enonic.nodb.engine.model.RepoRef;
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

    /**
     * Looks up a version by id alone (no repo_key): version ids are globally unique
     * within a tenant, and this mirrors the SPI's {@code getVersion(repo, versionId)}
     * shape at the point the repo has already been resolved by the caller. Scans across
     * all attached partitions (no partition pruning) — acceptable at this slice's scale;
     * a repo-scoped overload can be added later if this becomes a hot path.
     */
    public static VersionRecord get( Connection connection, String versionId )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            SELECT version_id, node_id, node_path, ts, node_data_hash, index_config_hash, acl_hash, binary_keys, commit_id, attributes
            FROM node_version WHERE version_id = ?
            """ ))
        {
            statement.setString( 1, versionId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? map( resultSet ) : null;
            }
        }
    }

    /**
     * Vacuum/retention op (DESIGN.md §6, mirrors spi.NodeStore#deleteVersion) — no repo_key
     * scoping, same reasoning as {@link #get}: version ids are globally unique within a
     * tenant. {@code branch_entry} has an FK to {@code node_version}, so deleting a version
     * still referenced by a live branch entry fails with a foreign-key-violation
     * {@link SQLException} (left to the caller to avoid — a Phase 1 itest rarely exercises
     * this directly per BUILD-PHASE-1.md's reconciliation table). A no-op (0 rows affected)
     * for an already-absent version id, matching plain {@code DELETE} semantics.
     */
    public static void delete( Connection connection, String versionId )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "DELETE FROM node_version WHERE version_id = ?" ))
        {
            statement.setString( 1, versionId );
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
