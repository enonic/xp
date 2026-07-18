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
 */
final class RepoKeys
{
    private RepoKeys()
    {
    }

    static long resolve( Connection connection, RepoRef repo )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "SELECT repo_key FROM repository WHERE repo_id = ?" ))
        {
            statement.setString( 1, repo.repoId() );
            try (ResultSet resultSet = statement.executeQuery())
            {
                if ( !resultSet.next() )
                {
                    throw new SQLException( "Unknown repo id: " + repo.repoId() );
                }
                return resultSet.getLong( 1 );
            }
        }
    }
}
