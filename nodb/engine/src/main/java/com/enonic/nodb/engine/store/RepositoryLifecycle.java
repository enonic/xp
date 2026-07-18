package com.enonic.nodb.engine.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.enonic.nodb.engine.model.RepoRef;

/**
 * Repo/branch lifecycle: create = repository row + partitions; delete = detach+drop
 * (DESIGN.md §4, schema.sql header comment).
 *
 * <p><b>branch_entry sub-partitioning choice for this slice:</b> schema.sql sketches
 * per-BRANCH sub-partitions created with per-branch DDL. Instead, {@link #createRepository}
 * creates the repo's {@code branch_entry_<repoKey>} partition as
 * {@code PARTITION BY LIST (branch)} with a single DEFAULT sub-partition
 * ({@code branch_entry_<repoKey>_default}). Every branch (draft, master, any fork target)
 * lands in that DEFAULT partition with zero DDL at branch-create time — {@link #createBranch}
 * only needs to insert the {@code branch} row. This trades away per-branch DDL pruning
 * (DESIGN.md §10 risk #6: catalog/relation pressure from tenants × repos × branches) for
 * simplicity in slice 1; per-branch sub-partition DDL can be added later as a targeted
 * optimization (e.g. only for branches past a churn/size threshold) without changing the
 * BranchStore/WriteService call shape, since both already address rows by
 * {@code (repo_key, branch, node_id)} regardless of which physical partition holds them.
 */
public final class RepositoryLifecycle
{
    private RepositoryLifecycle()
    {
    }

    public static long createRepository( Connection connection, String repoId, String settingsJson )
        throws SQLException
    {
        long repoKey;
        try (PreparedStatement statement =
                 connection.prepareStatement( "INSERT INTO repository (repo_id, settings) VALUES (?, ?::jsonb) RETURNING repo_key" ))
        {
            statement.setString( 1, repoId );
            statement.setString( 2, settingsJson == null ? "{}" : settingsJson );
            try (ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                repoKey = resultSet.getLong( 1 );
            }
        }

        String suffix = partitionSuffix( repoKey );
        try (Statement statement = connection.createStatement())
        {
            statement.execute( "CREATE TABLE node_version_" + suffix + " PARTITION OF node_version FOR VALUES IN (" + repoKey + ")" );
            statement.execute( "CREATE TABLE branch_entry_" + suffix + " PARTITION OF branch_entry FOR VALUES IN (" + repoKey +
                                    ") PARTITION BY LIST (branch)" );
            statement.execute( "CREATE TABLE branch_entry_" + suffix + "_default PARTITION OF branch_entry_" + suffix + " DEFAULT" );
        }
        return repoKey;
    }

    /**
     * Existence check for spi.RepositoryStorageAdmin#indexExists — returns a boolean rather
     * than throwing, unlike every DML/DDL operation above that addresses a repo via {@link
     * RepoRef} (those go through {@link RepoKeys#resolve}, which throws {@link
     * UnknownRepoException} for a missing repo — the right behavior for an operation that
     * NEEDS the repo to exist, but not for a method whose entire purpose is answering
     * "does it exist").
     */
    public static boolean repositoryExists( Connection connection, String repoId )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "SELECT 1 FROM repository WHERE repo_id = ?" ))
        {
            statement.setString( 1, repoId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }
    }

    public static void createBranch( Connection connection, long repoKey, String branch )
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
     * Drops the repo's partitions and its {@code repository} row. {@code node_commit} is
     * NOT partition-scoped (DESIGN.md §10 risk #10: repo drop is not purely DDL) and has a
     * plain FK to {@code repository} with no {@code ON DELETE CASCADE}, so its rows for
     * this repo are deleted explicitly before the {@code repository} row itself, ahead of
     * the FK check.
     */
    public static void deleteRepository( Connection connection, RepoRef repo )
        throws SQLException
    {
        long repoKey = RepoKeys.resolve( connection, repo );
        String suffix = partitionSuffix( repoKey );

        try (Statement statement = connection.createStatement())
        {
            // branch_entry's partition must go first: its rows FK-reference node_version,
            // so node_version's partition can't be dropped while they still exist.
            statement.execute( "DROP TABLE IF EXISTS branch_entry_" + suffix );
        }

        // node_version is referenced by an FK from branch_entry (the top-level parent
        // table, declared once for the whole partitioned hierarchy): Postgres ties that
        // FK's enforcement to each individual referenced partition, so a plain DROP TABLE
        // on node_version_<repoKey> fails ("other objects depend on it") even after every
        // row referencing it is gone. DETACH removes the partition from the partitioned
        // hierarchy (and with it, that per-partition FK dependency) before the DROP.
        if ( tableExists( connection, "node_version_" + suffix ) )
        {
            try (Statement statement = connection.createStatement())
            {
                statement.execute( "ALTER TABLE node_version DETACH PARTITION node_version_" + suffix );
                statement.execute( "DROP TABLE node_version_" + suffix );
            }
        }

        try (PreparedStatement statement = connection.prepareStatement( "DELETE FROM node_commit WHERE repo_key = ?" ))
        {
            statement.setLong( 1, repoKey );
            statement.executeUpdate();
        }

        // `branch` rows cascade via repository's ON DELETE CASCADE FK; branch_entry rows
        // are already gone with their partition above.
        try (PreparedStatement statement = connection.prepareStatement( "DELETE FROM repository WHERE repo_key = ?" ))
        {
            statement.setLong( 1, repoKey );
            statement.executeUpdate();
        }
    }

    /**
     * repo_key is a {@code bigint GENERATED ALWAYS AS IDENTITY} value read straight back
     * from the database — not user input — but identifiers can never be bind parameters,
     * so this is validated defensively (same posture as {@link com.enonic.nodb.engine.TenantContext})
     * before any interpolation into partition DDL.
     */
    private static String partitionSuffix( long repoKey )
    {
        if ( repoKey <= 0 )
        {
            throw new IllegalStateException( "Invalid repo_key: " + repoKey );
        }
        return Long.toString( repoKey );
    }

    private static boolean tableExists( Connection connection, String unqualifiedTableName )
        throws SQLException
    {
        // to_regclass resolves against the connection's current search_path, so this
        // correctly checks existence within the tenant schema without hardcoding it.
        try (PreparedStatement statement = connection.prepareStatement( "SELECT to_regclass(?)" ))
        {
            statement.setString( 1, unqualifiedTableName );
            try (ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                return resultSet.getString( 1 ) != null;
            }
        }
    }
}
