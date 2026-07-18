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
    private BranchStore()
    {
    }

    /** Upsert: {@code (repo_key, branch, node_id)} is the PK. */
    public static void store( Connection connection, long repoKey, BranchEntryRecord entry )
        throws SQLException
    {
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
        try (PreparedStatement statement = connection.prepareStatement( """
            SELECT branch, node_id, version_id, node_path, ts FROM branch_entry
            WHERE repo_key = ? AND branch = ? AND node_id = ?
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
    public static BranchEntryRecord getByPath( Connection connection, RepoRef repo, String branch, String nodePath )
        throws SQLException
    {
        return getByPath( connection, RepoKeys.resolve( connection, repo ), branch, nodePath );
    }

    public static BranchEntryRecord getByPath( Connection connection, long repoKey, String branch, String nodePath )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            SELECT branch, node_id, version_id, node_path, ts FROM branch_entry
            WHERE repo_key = ? AND branch = ? AND node_path = ?
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
        try (PreparedStatement statement = connection.prepareStatement( """
            SELECT branch, node_id, version_id, node_path, ts FROM branch_entry
            WHERE repo_key = ? AND branch = ? AND parent_path = ?
            ORDER BY node_path
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

    private static BranchEntryRecord map( ResultSet resultSet )
        throws SQLException
    {
        return new BranchEntryRecord( resultSet.getString( "branch" ), resultSet.getString( "node_id" ),
                                       resultSet.getString( "version_id" ), resultSet.getString( "node_path" ),
                                       resultSet.getTimestamp( "ts" ).toInstant() );
    }
}
