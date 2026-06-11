package com.enonic.xp.web.csp;

/**
 * Common source-list values for a CSP source expression, per W3C CSP3. Keyword sources are emitted
 * with their canonical single-quote wrappers (e.g. {@code 'self'}); scheme sources are emitted
 * verbatim (e.g. {@code data:}). Other free-form sources (hosts, paths, URLs, other schemes) are
 * passed as plain strings.
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
    REPORT_SAMPLE( "'report-sample'" ),
    DATA( "data:" ),
    BLOB( "blob:" );

    private final String token;

    CspSource( final String token )
    {
        this.token = token;
    }

    /**
     * The CSP token: single-quoted for keywords (e.g. {@code 'self'}), verbatim for schemes
     * (e.g. {@code data:}).
     */
    public String token()
    {
        return this.token;
    }
}
