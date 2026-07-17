package com.enonic.nodb.engine;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validated tenant identity. Tenant ids are constrained (DESIGN.md §5/§7.2) so ONE bare
 * identity works verbatim as a Postgres schema name (also, later, an OpenSearch index
 * prefix and S3 prefix). Validation happens HERE, before any interpolation into DDL/SQL:
 * identifiers cannot be bind parameters, so this constructor check is the injection
 * defense for every schema-qualified statement built from a TenantContext. Callers
 * additionally quote identifiers (see {@link Identifiers#quote}) as defense in depth.
 */
public record TenantContext(String tenantId)
{
    private static final Pattern VALID_ID = Pattern.compile( "^[a-z][a-z0-9]{2,30}$" );

    private static final Set<String> RESERVED =
        Set.of( "public", "pg_catalog", "information_schema", "nodb_system" );

    public TenantContext
    {
        if ( tenantId == null || !VALID_ID.matcher( tenantId ).matches() )
        {
            throw new IllegalArgumentException( "Invalid tenant id: " + tenantId );
        }
        if ( RESERVED.contains( tenantId ) )
        {
            throw new IllegalArgumentException( "Reserved tenant id: " + tenantId );
        }
    }
}
