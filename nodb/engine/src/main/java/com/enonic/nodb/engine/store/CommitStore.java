package com.enonic.nodb.engine.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.enonic.nodb.engine.model.CommitRecord;
import com.enonic.nodb.engine.model.RepoRef;

/**
 * COMMIT document equivalent (schema.sql {@code node_commit}). Unpartitioned, low volume
 * — row-scoped by {@code commit_id} (PK) is sufficient, per schema.sql's own comment.
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

    public static CommitRecord get( Connection connection, String commitId )
        throws SQLException
    {
        try (PreparedStatement statement =
                 connection.prepareStatement( "SELECT commit_id, message, committer, ts FROM node_commit WHERE commit_id = ?" ))
        {
            statement.setString( 1, commitId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                if ( !resultSet.next() )
                {
                    return null;
                }
                return new CommitRecord( resultSet.getString( "commit_id" ), resultSet.getString( "message" ),
                                          resultSet.getString( "committer" ), resultSet.getTimestamp( "ts" ).toInstant() );
            }
        }
    }
}
