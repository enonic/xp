package com.enonic.nodb.engine;

/**
 * Thrown by {@link MigrationRunner} when the migration set on the classpath cannot be
 * reconciled with what a tenant schema has already had applied: an edited (tampered)
 * migration file, a renamed/reordered manifest slot, a non-ordered manifest, or a tenant
 * that is ahead of the manifest. A dedicated type (rather than a plain
 * {@link java.sql.SQLException} distinguished by message text) so callers and tests can
 * tell "the migration set is not forward-only" apart from an ordinary DDL failure.
 */
public final class MigrationIntegrityException
    extends IllegalStateException
{
    MigrationIntegrityException( String message )
    {
        super( message );
    }
}
