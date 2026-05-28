package com.enonic.xp.portal.csp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * A Content Security Policy carried on {@link com.enonic.xp.portal.PortalRequest}.
 *
 * <p>This instance is mutable and request-scoped. Multiple contributors during request rendering
 * (platform, site app, custom apps, widgets, page controllers) may extend the same policy through
 * {@link #add}, {@link #set}, {@link #addSha}, and {@link #nonce}. The final
 * {@code Content-Security-Policy} header value is composed at portal response-flush time by
 * {@link #build()} so late additions during rendering still land in the header.</p>
 *
 * <p>Merge semantics — {@code add} is union for every directive class:</p>
 * <ul>
 *   <li><b>Source-list</b> ({@code script-src}, {@code style-src}, {@code img-src},
 *       {@code connect-src}, …): {@code add} appends new sources to the existing set, deduped.</li>
 *   <li><b>Boolean</b> ({@code upgrade-insecure-requests},
 *       {@code block-all-mixed-content}): there is no value to weaken; both {@code add} and
 *       {@code set} ensure the directive is present.</li>
 *   <li><b>Restrictive</b> ({@code frame-ancestors}, {@code base-uri}, {@code sandbox}):
 *       {@code add} unions sources. Browsers interpret the union permissively, which matches the
 *       weaker-on-conflict semantics this API targets.</li>
 * </ul>
 *
 * <p>The nonce is added to {@code script-src} only. To allow inline content in other directives
 * (e.g. {@code style-src} for inline {@code <style nonce="...">}), call:</p>
 * <pre>
 *     csp.add( "style-src", "'nonce-" + csp.nonce() + "'" );
 * </pre>
 *
 * <p>{@code 'unsafe-inline'} interaction: per W3C CSP3, modern browsers ignore
 * {@code 'unsafe-inline'} when a {@code 'nonce-…'} or {@code 'strict-dynamic'} value is also
 * present. This API does not attempt to be clever — if both are added, both are emitted in the
 * header and the browser's precedence rules apply.</p>
 */
@NullMarked
public final class ContentSecurityPolicy
{
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final Base64.Encoder HASH_BASE64 = Base64.getEncoder();

    private static final Base64.Encoder NONCE_BASE64 = Base64.getUrlEncoder().withoutPadding();

    private static final int NONCE_BYTE_LENGTH = 16;

    private static final String NONCE_DIRECTIVE = "script-src";

    private final Map<String, LinkedHashSet<String>> directives = new TreeMap<>();

    @Nullable
    private String nonceValue;

    /**
     * Unions {@code sources} into the existing source set for {@code directive}, deduped. With no
     * {@code sources}, registers the directive (useful for boolean directives like
     * {@code upgrade-insecure-requests}).
     */
    public ContentSecurityPolicy add( final String directive, final String... sources )
    {
        requireNonNull( directive, "directive is required" );
        requireNonNull( sources, "sources is required" );
        final LinkedHashSet<String> set = this.directives.computeIfAbsent( directive, k -> new LinkedHashSet<>() );
        for ( final String source : sources )
        {
            set.add( requireNonNull( source, "source is required" ) );
        }
        return this;
    }

    /**
     * Resets the source list for {@code directive} to exactly {@code sources}. Subsequent
     * {@link #add(String, String...)} calls may still extend it — there is no freeze.
     */
    public ContentSecurityPolicy set( final String directive, final String... sources )
    {
        requireNonNull( directive, "directive is required" );
        requireNonNull( sources, "sources is required" );
        final LinkedHashSet<String> set = new LinkedHashSet<>();
        for ( final String source : sources )
        {
            set.add( requireNonNull( source, "source is required" ) );
        }
        this.directives.put( directive, set );
        return this;
    }

    /**
     * Computes the SHA-256 digest of {@code content} and unions {@code 'sha256-<base64>'} into the
     * source set for {@code directive}.
     */
    public ContentSecurityPolicy addSha( final String directive, final byte[] content )
    {
        requireNonNull( directive, "directive is required" );
        requireNonNull( content, "content is required" );
        return add( directive, "'sha256-" + HASH_BASE64.encodeToString( digest( HashAlgo.SHA256, content ) ) + "'" );
    }

    /**
     * Unions {@code '<algo>-<base64>'} into the source set for {@code directive}. Use this overload
     * when the digest has been computed elsewhere (e.g. shipped with a build manifest).
     */
    public ContentSecurityPolicy addSha( final String directive, final HashAlgo algo, final String base64 )
    {
        requireNonNull( directive, "directive is required" );
        requireNonNull( algo, "algo is required" );
        requireNonNull( base64, "base64 is required" );
        return add( directive, "'" + algo.token() + "-" + base64 + "'" );
    }

    /**
     * Returns a cryptographically random, base64-encoded nonce (≥ 128 bits of entropy), lazily
     * generated and cached on first call. Every call after the first returns the same value for the
     * life of this policy instance (= life of the {@code PortalRequest}).
     *
     * <p>On the first call, {@code 'nonce-<value>'} is added to {@code script-src}. If this method
     * is never called, no {@code nonce-} entry is emitted anywhere. To allow inline content in
     * other directives (e.g. {@code style-src} for inline {@code <style nonce="...">}), call
     * {@code csp.add( "style-src", "'nonce-" + csp.nonce() + "'" )} directly.</p>
     */
    public String nonce()
    {
        if ( this.nonceValue == null )
        {
            final byte[] bytes = new byte[NONCE_BYTE_LENGTH];
            SECURE_RANDOM.nextBytes( bytes );
            this.nonceValue = NONCE_BASE64.encodeToString( bytes );
            this.directives.computeIfAbsent( NONCE_DIRECTIVE, k -> new LinkedHashSet<>() ).add( "'nonce-" + this.nonceValue + "'" );
        }
        return this.nonceValue;
    }

    /**
     * Renders the current state as a {@code Content-Security-Policy} header value. Returns the
     * empty string when no directive has been added — callers should skip emitting the header in
     * that case.
     *
     * <p>Directives are emitted in alphabetical order for deterministic output. Sources within a
     * directive follow insertion order.</p>
     */
    public String build()
    {
        if ( this.directives.isEmpty() )
        {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for ( final Map.Entry<String, LinkedHashSet<String>> entry : this.directives.entrySet() )
        {
            if ( sb.length() > 0 )
            {
                sb.append( "; " );
            }
            sb.append( entry.getKey() );
            for ( final String source : entry.getValue() )
            {
                sb.append( ' ' ).append( source );
            }
        }
        return sb.toString();
    }

    private static byte[] digest( final HashAlgo algo, final byte[] content )
    {
        try
        {
            return MessageDigest.getInstance( algo.algorithm() ).digest( content );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
