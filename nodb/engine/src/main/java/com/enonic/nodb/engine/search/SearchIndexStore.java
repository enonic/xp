package com.enonic.nodb.engine.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code search_index} (migration 003): the authoritative alias→generation map.
 *
 * <p>DESIGN §5 is explicit that "names are constructed one-way from TenantContext; nothing
 * correctness- or security-relevant ever parses a name back — the authoritative alias→generation
 * mapping is NoDB metadata". This is that metadata. OpenSearch's own alias API could answer
 * "which index does this alias point at" too, but not "which template and which projection built
 * it", and those two are what make a rebuild decidable rather than guessed.
 */
public final class SearchIndexStore
{
    public static final String STATE_LIVE = "LIVE";

    public static final String STATE_BUILDING = "BUILDING";

    public static final String STATE_RETIRED = "RETIRED";

    private SearchIndexStore()
    {
    }

    public static void record( Connection connection, long repoKey, SearchIndexRecord record )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            INSERT INTO search_index (repo_key, generation, alias_name, index_name, template_version, projection_version, state)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (repo_key, generation) DO UPDATE
                SET alias_name = EXCLUDED.alias_name, index_name = EXCLUDED.index_name,
                    template_version = EXCLUDED.template_version, projection_version = EXCLUDED.projection_version,
                    state = EXCLUDED.state
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setInt( 2, record.generation() );
            statement.setString( 3, record.aliasName() );
            statement.setString( 4, record.indexName() );
            statement.setInt( 5, record.templateVersion() );
            statement.setInt( 6, record.projectionVersion() );
            statement.setString( 7, record.state() );
            statement.executeUpdate();
        }
    }

    /** The generation the alias currently points at, or {@code null} if this repo has no index. */
    public static SearchIndexRecord live( Connection connection, long repoKey )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            SELECT generation, alias_name, index_name, template_version, projection_version, state
            FROM search_index WHERE repo_key = ? AND state = ?
            ORDER BY generation DESC LIMIT 1
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, STATE_LIVE );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? map( resultSet ) : null;
            }
        }
    }

    public static List<SearchIndexRecord> list( Connection connection, long repoKey )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            SELECT generation, alias_name, index_name, template_version, projection_version, state
            FROM search_index WHERE repo_key = ? ORDER BY generation
            """ ))
        {
            statement.setLong( 1, repoKey );
            List<SearchIndexRecord> records = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery())
            {
                while ( resultSet.next() )
                {
                    records.add( map( resultSet ) );
                }
            }
            return List.copyOf( records );
        }
    }

    /**
     * Highest generation ever allocated for this repo, or 0. Read from the FULL history rather
     * than from the live row so a generation number is never reused after a failed rebuild left a
     * BUILDING row behind — reusing one would mean PUTting an index name that may still exist.
     */
    public static int maxGeneration( Connection connection, long repoKey )
        throws SQLException
    {
        try (PreparedStatement statement =
                 connection.prepareStatement( "SELECT coalesce(max(generation), 0) FROM search_index WHERE repo_key = ?" ))
        {
            statement.setLong( 1, repoKey );
            try (ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                return resultSet.getInt( 1 );
            }
        }
    }

    public static void deleteAll( Connection connection, long repoKey )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "DELETE FROM search_index WHERE repo_key = ?" ))
        {
            statement.setLong( 1, repoKey );
            statement.executeUpdate();
        }
    }

    private static SearchIndexRecord map( ResultSet resultSet )
        throws SQLException
    {
        return new SearchIndexRecord( resultSet.getInt( 1 ), resultSet.getString( 2 ), resultSet.getString( 3 ), resultSet.getInt( 4 ),
                                      resultSet.getInt( 5 ), resultSet.getString( 6 ) );
    }

    public record SearchIndexRecord(int generation, String aliasName, String indexName, int templateVersion, int projectionVersion,
                                    String state)
    {
    }
}
