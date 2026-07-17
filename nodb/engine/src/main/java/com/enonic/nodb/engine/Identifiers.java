package com.enonic.nodb.engine;

/**
 * Defense-in-depth SQL identifier quoting. Every identifier passed here is expected to
 * already be validated (e.g. via {@link TenantContext}) before reaching this point —
 * identifiers cannot be bind parameters, so validation is the real injection defense;
 * quoting only guards against Postgres's own case-folding/reserved-word rules.
 */
final class Identifiers
{
    private Identifiers()
    {
    }

    static String quote( String identifier )
    {
        return '"' + identifier.replace( "\"", "\"\"" ) + '"';
    }
}
