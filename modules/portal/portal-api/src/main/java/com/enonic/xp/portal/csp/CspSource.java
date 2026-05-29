package com.enonic.xp.portal.csp;

/**
 * Special source-list keywords used in a CSP source expression, per W3C CSP3. These are emitted
 * with their canonical single-quote wrappers (e.g. {@code 'self'}). Free-form sources (hosts,
 * schemes, paths, URLs) are passed as plain strings.
 */
public enum CspSource
{
    SELF( "'self'" ),
    NONE( "'none'" ),
    UNSAFE_INLINE( "'unsafe-inline'" ),
    UNSAFE_EVAL( "'unsafe-eval'" ),
    STRICT_DYNAMIC( "'strict-dynamic'" ),
    UNSAFE_HASHES( "'unsafe-hashes'" ),
    WASM_UNSAFE_EVAL( "'wasm-unsafe-eval'" ),
    REPORT_SAMPLE( "'report-sample'" );

    private final String token;

    CspSource( final String token )
    {
        this.token = token;
    }

    /**
     * The single-quoted CSP token (e.g. {@code 'self'}).
     */
    public String token()
    {
        return this.token;
    }
}
