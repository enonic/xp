package com.enonic.xp.web.csp;

/**
 * Common source-list values for a CSP source expression, per W3C CSP3. Keyword sources carry their
 * canonical single-quote wrappers (e.g. {@code 'self'}); scheme sources are verbatim (e.g.
 * {@code data:}). Pass these constants to the per-directive methods ({@link ContentSecurityPolicy#scriptSrc}
 * and friends) or {@link ContentSecurityPolicy#add}; other free-form sources (hosts, paths, URLs,
 * other schemes) are passed as plain strings.
 */
public final class CspSource
{
    public static final String SELF = "'self'";

    public static final String NONE = "'none'";

    public static final String UNSAFE_INLINE = "'unsafe-inline'";

    public static final String UNSAFE_EVAL = "'unsafe-eval'";

    public static final String STRICT_DYNAMIC = "'strict-dynamic'";

    public static final String UNSAFE_HASHES = "'unsafe-hashes'";

    public static final String WASM_UNSAFE_EVAL = "'wasm-unsafe-eval'";

    public static final String REPORT_SAMPLE = "'report-sample'";

    public static final String DATA = "data:";

    public static final String BLOB = "blob:";

    /**
     * The {@code *} wildcard: matches network-scheme sources only — notably <i>not</i>
     * {@code data:}, {@code blob:} or {@code filesystem:}, which must be allowed explicitly.
     */
    public static final String WILDCARD = "*";

    private CspSource()
    {
    }
}
