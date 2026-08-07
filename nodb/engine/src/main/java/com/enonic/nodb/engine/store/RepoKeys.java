package com.enonic.nodb.engine.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.enonic.nodb.engine.model.RepoRef;

/**
 * Resolves a {@link RepoRef} (external repo id) to the surrogate {@code repo_key} used by
 * every partitioned table. Never derives repo_key any other way (schema.sql: repo_key is
 * {@code GENERATED ALWAYS AS IDENTITY}) — this lookup is the one source of truth.
 *
 * <p>Public since Phase 4 Gate A: {@code engine.search}'s stores address the same partitioned
 * tables and must resolve repo ids through this one lookup rather than duplicating it.
 */
public final class RepoKeys
{
    private RepoKeys()
    {
    }

    /**
     * The repo key, or {@code null} when the repository does not exist. Phase 4 Gate F: for callers
     * whose operation is idempotent, where an unknown repository means "nothing to do" rather than
     * an error (dropping a search index for a repository whose row is already gone).
     */
    public static Long tryResolve( Connection connection, RepoRef repo )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "SELECT repo_key FROM repository WHERE repo_id = ?" ))
        {
            statement.setString( 1, repo.repoId() );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? resultSet.getLong( 1 ) : null;
            }
        }
    }

    public static long resolve( Connection connection, RepoRef repo )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "SELECT repo_key FROM repository WHERE repo_id = ?" ))
        {
            statement.setString( 1, repo.repoId() );
            try (ResultSet resultSet = statement.executeQuery())
            {
                if ( !resultSet.next() )
                {
                    throw new UnknownRepoException( repo.repoId() );
                }
                return resultSet.getLong( 1 );
            }
        }
    }
}
