package com.enonic.nodb.engine.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.enonic.nodb.engine.model.CommitRecord;
import com.enonic.nodb.engine.model.RepoRef;

/**
 * COMMIT document equivalent (schema.sql {@code node_commit}). Unpartitioned, low volume.
 * {@code commit_id} alone is the PK (so no data collision exists within a tenant), but
 * addressing is repo-scoped anyway (Phase 3.5 Gate A, closing the Gate 0 holdout): reads
 * carry and predicate on {@code repo_key}, same as version identity under gate P2.
 */
public final class CommitStore
{
    private CommitStore()
    {
    }

    /** {@link RepoRef}-addressed variant — see {@link BranchStore#getByNodeId(Connection, RepoRef, String, String)}. */
    public static void store( Connection connection, RepoRef repo, CommitRecord commit )
        throws SQLException
    {
        store( connection, RepoKeys.resolve( connection, repo ), commit );
    }

    public static void store( Connection connection, long repoKey, CommitRecord commit )
        throws SQLException
    {
        try (PreparedStatement statement =
                 connection.prepareStatement( "INSERT INTO node_commit (commit_id, repo_key, message, committer, ts) VALUES (?, ?, ?, ?, ?)" ))
        {
            statement.setString( 1, commit.commitId() );
            statement.setLong( 2, repoKey );
            statement.setString( 3, commit.message() );
            statement.setString( 4, commit.committer() );
            statement.setTimestamp( 5, Timestamp.from( commit.timestamp() ) );
            statement.executeUpdate();
        }
    }

    /** {@link RepoRef}-addressed variant — see {@link BranchStore#getByNodeId(Connection, RepoRef, String, String)}. */
    public static CommitRecord get( Connection connection, RepoRef repo, String commitId )
        throws SQLException
    {
        return get( connection, RepoKeys.resolve( connection, repo ), commitId );
    }

    /**
     * Repo-scoped lookup (Phase 3.5 Gate A): a commit belonging to another repo of the same
     * tenant is not addressable — the Gate 0 inventory flagged the previous tenant-global
     * {@code commit_id}-only form as the one unscoped holdout after gate P2.
     */
    public static CommitRecord get( Connection connection, long repoKey, String commitId )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT commit_id, message, committer, ts FROM node_commit WHERE repo_key = ? AND commit_id = ?" ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, commitId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? map( resultSet ) : null;
            }
        }
    }

    /** {@link RepoRef}-addressed variant — see {@link BranchStore#getByNodeId(Connection, RepoRef, String, String)}. */
    public static List<CommitRecord> findByRepo( Connection connection, RepoRef repo )
        throws SQLException
    {
        return findByRepo( connection, RepoKeys.resolve( connection, repo ) );
    }

    /**
     * All commits of one repo (Phase 3.5 Gate A: RepoDumper's dump enumeration), served by
     * the {@code node_commit_by_repo} index; ordered by {@code (ts, commit_id)} for a
     * deterministic dump.
     */
    public static List<CommitRecord> findByRepo( Connection connection, long repoKey )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT commit_id, message, committer, ts FROM node_commit WHERE repo_key = ? ORDER BY ts, commit_id" ))
        {
            statement.setLong( 1, repoKey );
            try (ResultSet resultSet = statement.executeQuery())
            {
                List<CommitRecord> result = new ArrayList<>();
                while ( resultSet.next() )
                {
                    result.add( map( resultSet ) );
                }
                return List.copyOf( result );
            }
        }
    }

    private static CommitRecord map( ResultSet resultSet )
        throws SQLException
    {
        return new CommitRecord( resultSet.getString( "commit_id" ), resultSet.getString( "message" ),
                                  resultSet.getString( "committer" ), resultSet.getTimestamp( "ts" ).toInstant() );
    }
}
