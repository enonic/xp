package com.enonic.xp.web.csp;

/**
 * Special keywords for the {@code trusted-types} directive, used alongside (user-defined) policy
 * names. Keyword tokens are emitted as-is — single-quoted for {@code 'none'} /
 * {@code 'allow-duplicates'}, and the bare wildcard {@code *}.
 */
public enum TrustedTypesKeyword
{
    ALLOW_DUPLICATES( "'allow-duplicates'" ),
    NONE( "'none'" ),
    WILDCARD( "*" );

    private final String token;

    TrustedTypesKeyword( final String token )
    {
        this.token = token;
    }

    /**
     * The {@code trusted-types} token (e.g. {@code 'allow-duplicates'}).
     */
    public String token()
    {
        return this.token;
    }
}
