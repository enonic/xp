package com.enonic.xp.web.csp;

/**
 * Hash algorithms allowed in a CSP {@code 'sha256-…'} / {@code 'sha384-…'} / {@code 'sha512-…'}
 * source expression, per W3C CSP3.
 */
public enum HashAlgo
{
    SHA256( "sha256", "SHA-256" ),
    SHA384( "sha384", "SHA-384" ),
    SHA512( "sha512", "SHA-512" );

    private final String token;

    private final String algorithm;

    HashAlgo( final String token, final String algorithm )
    {
        this.token = token;
        this.algorithm = algorithm;
    }

    /**
     * The CSP-side token used in the source expression (e.g. {@code sha256}).
     */
    public String token()
    {
        return this.token;
    }

    /**
     * The {@link java.security.MessageDigest} algorithm name (e.g. {@code SHA-256}).
     */
    public String algorithm()
    {
        return this.algorithm;
    }
}
