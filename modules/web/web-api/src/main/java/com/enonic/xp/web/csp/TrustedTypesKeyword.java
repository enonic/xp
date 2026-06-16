package com.enonic.xp.web.csp;

/**
 * Special keyword values for the {@code trusted-types} directive, used alongside (user-defined)
 * policy names via {@link ContentSecurityPolicy#trustedTypes}. Emitted as-is — single-quoted for
 * {@code 'none'} / {@code 'allow-duplicates'}, and the bare wildcard {@code *}.
 */
public final class TrustedTypesKeyword
{
    public static final String ALLOW_DUPLICATES = "'allow-duplicates'";

    public static final String NONE = "'none'";

    public static final String WILDCARD = "*";

    private TrustedTypesKeyword()
    {
    }
}
