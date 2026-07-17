package com.enonic.nodb.server.auth;

import com.enonic.nodb.engine.TenantContext;

/**
 * The authenticated identity resolved from a verified JWT (DESIGN.md §7.2): tenant id,
 * scope, subject (control-plane account or service identity), and the token's {@code jti}
 * for attribution/audit. This is the ONLY source of tenant identity anywhere on the data
 * path — request messages never carry a tenant field, and {@link #tenantContext()} is the
 * one place a {@link TenantContext} gets constructed from it.
 */
public record TenantPrincipal(String tenantId, Scope scope, String subject, String jti)
{
    public TenantContext tenantContext()
    {
        return new TenantContext( tenantId );
    }
}
