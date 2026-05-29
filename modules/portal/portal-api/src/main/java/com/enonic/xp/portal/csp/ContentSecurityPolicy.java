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
 * the typed per-directive methods or through the generic escape hatches {@link #add},
 * {@link #set}, and {@link #addSha}. The final {@code Content-Security-Policy} header value is
 * composed at portal response-flush time by {@link #build()} so late additions during rendering
 * still land in the header.</p>
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

    private static final String SCRIPT_SRC = "script-src";

    private static final String STYLE_SRC = "style-src";

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

    public ContentSecurityPolicy defaultSrc( final CspSource... sources )
    {
        return addTokens( "default-src", sources );
    }

    public ContentSecurityPolicy defaultSrc( final String... sources )
    {
        return add( "default-src", sources );
    }

    public ContentSecurityPolicy scriptSrc( final CspSource... sources )
    {
        return addTokens( SCRIPT_SRC, sources );
    }

    public ContentSecurityPolicy scriptSrc( final String... sources )
    {
        return add( SCRIPT_SRC, sources );
    }

    public ContentSecurityPolicy styleSrc( final CspSource... sources )
    {
        return addTokens( STYLE_SRC, sources );
    }

    public ContentSecurityPolicy styleSrc( final String... sources )
    {
        return add( STYLE_SRC, sources );
    }

    public ContentSecurityPolicy imgSrc( final CspSource... sources )
    {
        return addTokens( "img-src", sources );
    }

    public ContentSecurityPolicy imgSrc( final String... sources )
    {
        return add( "img-src", sources );
    }

    public ContentSecurityPolicy fontSrc( final CspSource... sources )
    {
        return addTokens( "font-src", sources );
    }

    public ContentSecurityPolicy fontSrc( final String... sources )
    {
        return add( "font-src", sources );
    }

    public ContentSecurityPolicy connectSrc( final CspSource... sources )
    {
        return addTokens( "connect-src", sources );
    }

    public ContentSecurityPolicy connectSrc( final String... sources )
    {
        return add( "connect-src", sources );
    }

    public ContentSecurityPolicy mediaSrc( final CspSource... sources )
    {
        return addTokens( "media-src", sources );
    }

    public ContentSecurityPolicy mediaSrc( final String... sources )
    {
        return add( "media-src", sources );
    }

    public ContentSecurityPolicy objectSrc( final CspSource... sources )
    {
        return addTokens( "object-src", sources );
    }

    public ContentSecurityPolicy objectSrc( final String... sources )
    {
        return add( "object-src", sources );
    }

    public ContentSecurityPolicy frameSrc( final CspSource... sources )
    {
        return addTokens( "frame-src", sources );
    }

    public ContentSecurityPolicy frameSrc( final String... sources )
    {
        return add( "frame-src", sources );
    }

    public ContentSecurityPolicy workerSrc( final CspSource... sources )
    {
        return addTokens( "worker-src", sources );
    }

    public ContentSecurityPolicy workerSrc( final String... sources )
    {
        return add( "worker-src", sources );
    }

    public ContentSecurityPolicy manifestSrc( final CspSource... sources )
    {
        return addTokens( "manifest-src", sources );
    }

    public ContentSecurityPolicy manifestSrc( final String... sources )
    {
        return add( "manifest-src", sources );
    }

    public ContentSecurityPolicy childSrc( final CspSource... sources )
    {
        return addTokens( "child-src", sources );
    }

    public ContentSecurityPolicy childSrc( final String... sources )
    {
        return add( "child-src", sources );
    }

    public ContentSecurityPolicy frameAncestors( final CspSource... sources )
    {
        return addTokens( "frame-ancestors", sources );
    }

    public ContentSecurityPolicy frameAncestors( final String... sources )
    {
        return add( "frame-ancestors", sources );
    }

    public ContentSecurityPolicy baseUri( final CspSource... sources )
    {
        return addTokens( "base-uri", sources );
    }

    public ContentSecurityPolicy baseUri( final String... sources )
    {
        return add( "base-uri", sources );
    }

    public ContentSecurityPolicy formAction( final CspSource... sources )
    {
        return addTokens( "form-action", sources );
    }

    public ContentSecurityPolicy formAction( final String... sources )
    {
        return add( "form-action", sources );
    }

    /**
     * Registers the boolean {@code upgrade-insecure-requests} directive.
     */
    public ContentSecurityPolicy upgradeInsecureRequests()
    {
        return add( "upgrade-insecure-requests" );
    }

    /**
     * Unions {@code flags} into the {@code sandbox} directive. With no flags, registers an empty
     * {@code sandbox} (all restrictions applied).
     */
    public ContentSecurityPolicy sandbox( final SandboxFlag... flags )
    {
        requireNonNull( flags, "flags is required" );
        final String[] tokens = new String[flags.length];
        for ( int i = 0; i < flags.length; i++ )
        {
            tokens[i] = requireNonNull( flags[i], "flag is required" ).token();
        }
        return add( "sandbox", tokens );
    }

    /**
     * Computes the SHA-256 digest of {@code content} and unions {@code 'sha256-<base64>'} into
     * {@code script-src}.
     */
    public ContentSecurityPolicy addScriptSrcSha( final byte[] content )
    {
        return addSha( SCRIPT_SRC, content );
    }

    /**
     * Unions a precomputed {@code '<algo>-<base64>'} digest into {@code script-src}.
     */
    public ContentSecurityPolicy addScriptSrcSha( final HashAlgo algo, final String base64 )
    {
        return addSha( SCRIPT_SRC, algo, base64 );
    }

    /**
     * Computes the SHA-256 digest of {@code content} and unions {@code 'sha256-<base64>'} into
     * {@code style-src}.
     */
    public ContentSecurityPolicy addStyleSrcSha( final byte[] content )
    {
        return addSha( STYLE_SRC, content );
    }

    /**
     * Unions a precomputed {@code '<algo>-<base64>'} digest into {@code style-src}.
     */
    public ContentSecurityPolicy addStyleSrcSha( final HashAlgo algo, final String base64 )
    {
        return addSha( STYLE_SRC, algo, base64 );
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
            this.directives.computeIfAbsent( SCRIPT_SRC, k -> new LinkedHashSet<>() ).add( "'nonce-" + this.nonceValue + "'" );
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

    private ContentSecurityPolicy addTokens( final String directive, final CspSource[] sources )
    {
        requireNonNull( sources, "sources is required" );
        final String[] tokens = new String[sources.length];
        for ( int i = 0; i < sources.length; i++ )
        {
            tokens[i] = requireNonNull( sources[i], "source is required" ).token();
        }
        return add( directive, tokens );
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
