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
 * <p>Mutable and request-scoped: many contributors (platform, site app, apps, widgets, page
 * controllers) extend the same policy during rendering, and {@link #build()} composes the header at
 * response-flush time so late additions still land.</p>
 *
 * <p>It works as a <b>collector of wishes</b> that resolves to the <b>most permissive</b> result, so
 * no contributor's content is blocked by another's rule:</p>
 * <ul>
 *   <li>{@link #add} unions sources into a directive (deduped).</li>
 *   <li>{@link #set} resets a directive's sources — no freeze, so a later {@link #add} still extends.</li>
 * </ul>
 *
 * <p>One CSP3 quirk works against that goal: a browser treats a {@code 'nonce-…'}, a hash, or
 * {@code 'strict-dynamic'} as a reason to <i>ignore</i> {@code 'unsafe-inline'} (and
 * {@code 'strict-dynamic'} also ignores {@code 'self'}/host/scheme), which makes the union
 * <i>narrower</i>, not wider. So {@link #build()} applies a single rule:</p>
 * <blockquote>If a directive contains {@code 'unsafe-inline'}, its {@code 'nonce-…'}, hash, and
 * {@code 'strict-dynamic'} sources are dropped from the emitted header.</blockquote>
 * <p>{@code 'unsafe-inline'} already permits every inline script/style a nonce or hash would, so
 * this only widens the policy. For example, the strict-CSP recipe collapses to its permissive legacy
 * form:</p>
 * <pre>
 *     script-src 'nonce-r' 'strict-dynamic' https: 'unsafe-inline'
 *         -&gt; script-src https: 'unsafe-inline'
 * </pre>
 * <p>{@code 'unsafe-inline'} governs <i>inline</i> content only, and {@code 'strict-dynamic'}
 * propagation is dropped along with it, so a caller relaxing this way should also list the sources
 * its <i>external</i> scripts need ({@code 'self'}, {@code https:}, …) — those are kept. By design
 * this means a contributor that adds {@code 'unsafe-inline'} voids another's nonce/hash/{@code
 * 'strict-dynamic'} hardening on that directive: the weakest wish wins.</p>
 *
 * <p>{@code 'strict-dynamic'} <i>without</i> {@code 'unsafe-inline'} is left untouched — it has no
 * permissive superset with {@code 'self'}/host allowlists (keeping it blocks those hosts, dropping it
 * removes script propagation), so the API does not arbitrate and the browser decides.</p>
 *
 * <p>A {@code 'nonce-'} source is valid only on {@code script-src} and {@code style-src}: use
 * {@link #nonceScriptSrc()} or {@link #nonceStyleSrc()}. Both return the same request-scoped value to
 * stamp on the matching inline tag.</p>
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

    private static final String UNSAFE_INLINE = "'unsafe-inline'";

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
     * Seeds a restrictive deny-all baseline: {@code default-src 'none'}, {@code base-uri 'none'},
     * and {@code frame-ancestors 'none'}. Intended as a one-shot starting point — call it first,
     * then open up only the directives you need (e.g. {@code scriptSrc( CspSource.SELF )}).
     */
    public ContentSecurityPolicy strict()
    {
        defaultSrc( CspSource.NONE );
        baseUri( CspSource.NONE );
        frameAncestors( CspSource.NONE );
        return this;
    }

    /**
     * Seeds a strict, allowlist-free script baseline: {@code script-src 'strict-dynamic'},
     * {@code object-src 'none'}, and {@code base-uri 'none'}. {@code 'strict-dynamic'} trusts scripts
     * loaded by an already-trusted script, but something must bootstrap that trust — add a nonce
     * ({@link #nonceScriptSrc()}, which returns the value to stamp on inline {@code <script nonce>})
     * or a hash ({@link #addScriptSrcSha(byte[])}); on its own this baseline allows no scripts. Call
     * it first, then bootstrap and add more sources as needed.
     */
    public ContentSecurityPolicy strictDynamic()
    {
        scriptSrc( CspSource.STRICT_DYNAMIC );
        objectSrc( CspSource.NONE );
        baseUri( CspSource.NONE );
        return this;
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
        return addScriptSrcSha( HashAlgo.SHA256, content );
    }

    /**
     * Computes the {@code algo} digest of {@code content} and unions {@code '<algo>-<base64>'} into
     * {@code script-src}.
     */
    public ContentSecurityPolicy addScriptSrcSha( final HashAlgo algo, final byte[] content )
    {
        return addComputedSha( SCRIPT_SRC, algo, content );
    }

    /**
     * Unions a precomputed {@code '<algo>-<base64>'} digest into {@code script-src}.
     */
    public ContentSecurityPolicy addScriptSrcSha( final HashAlgo algo, final String base64 )
    {
        return addPrecomputedSha( SCRIPT_SRC, algo, base64 );
    }

    /**
     * Computes the SHA-256 digest of {@code content} and unions {@code 'sha256-<base64>'} into
     * {@code style-src}.
     */
    public ContentSecurityPolicy addStyleSrcSha( final byte[] content )
    {
        return addStyleSrcSha( HashAlgo.SHA256, content );
    }

    /**
     * Computes the {@code algo} digest of {@code content} and unions {@code '<algo>-<base64>'} into
     * {@code style-src}.
     */
    public ContentSecurityPolicy addStyleSrcSha( final HashAlgo algo, final byte[] content )
    {
        return addComputedSha( STYLE_SRC, algo, content );
    }

    /**
     * Unions a precomputed {@code '<algo>-<base64>'} digest into {@code style-src}.
     */
    public ContentSecurityPolicy addStyleSrcSha( final HashAlgo algo, final String base64 )
    {
        return addPrecomputedSha( STYLE_SRC, algo, base64 );
    }

    /**
     * Wires the request nonce into {@code script-src} and returns its value (for stamping on inline
     * {@code <script nonce="...">} tags).
     */
    public String nonceScriptSrc()
    {
        return nonceFor( SCRIPT_SRC );
    }

    /**
     * Wires the request nonce into {@code style-src} and returns its value (for stamping on inline
     * {@code <style nonce="...">} tags).
     */
    public String nonceStyleSrc()
    {
        return nonceFor( STYLE_SRC );
    }

    /**
     * The nonce is a cryptographically random, base64-encoded value (≥ 128 bits of entropy), lazily
     * generated and cached on first use. Every {@code nonce*} call returns the same value for the
     * life of this policy instance (= life of the {@code PortalRequest}); each adds
     * {@code 'nonce-<value>'} to the directive it targets. If no {@code nonce*} method is ever
     * called, no {@code nonce-} entry is emitted anywhere.
     */
    private String nonceFor( final String directive )
    {
        if ( this.nonceValue == null )
        {
            final byte[] bytes = new byte[NONCE_BYTE_LENGTH];
            SECURE_RANDOM.nextBytes( bytes );
            this.nonceValue = NONCE_BASE64.encodeToString( bytes );
        }
        this.directives.computeIfAbsent( directive, k -> new LinkedHashSet<>() ).add( "'nonce-" + this.nonceValue + "'" );
        return this.nonceValue;
    }

    /**
     * Renders the current state as a {@code Content-Security-Policy} header value. Returns the
     * empty string when no directive has been added — callers should skip emitting the header in
     * that case.
     *
     * <p>Directives are emitted in alphabetical order for deterministic output. Sources within a
     * directive follow insertion order.</p>
     *
     * <p>Relaxing resolution: in a directive that holds {@code 'unsafe-inline'}, any {@code 'nonce-…'},
     * hash, and {@code 'strict-dynamic'} source is omitted from the output — each would otherwise make
     * a browser ignore {@code 'unsafe-inline'} and allow fewer scripts/styles than was wished for.
     * {@code 'unsafe-inline'} governs only inline content, so a caller relaxing this way should also
     * list the host/scheme sources its external scripts need (e.g. {@code 'self'}, {@code https:});
     * those are kept, only the strictness sources are dropped.</p>
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
            final LinkedHashSet<String> sources = entry.getValue();
            final boolean hasUnsafeInline = sources.contains( UNSAFE_INLINE );
            for ( final String source : sources )
            {
                if ( hasUnsafeInline && isInlineStrictnessSource( source ) )
                {
                    continue;
                }
                sb.append( ' ' ).append( source );
            }
        }
        return sb.toString();
    }

    private static boolean isInlineStrictnessSource( final String source )
    {
        return source.equals( "'strict-dynamic'" ) || source.startsWith( "'nonce-" ) || source.startsWith( "'sha256-" ) ||
            source.startsWith( "'sha384-" ) || source.startsWith( "'sha512-" );
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

    private ContentSecurityPolicy addComputedSha( final String directive, final HashAlgo algo, final byte[] content )
    {
        requireNonNull( algo, "algo is required" );
        requireNonNull( content, "content is required" );
        return add( directive, "'" + algo.token() + "-" + HASH_BASE64.encodeToString( digest( algo, content ) ) + "'" );
    }

    private ContentSecurityPolicy addPrecomputedSha( final String directive, final HashAlgo algo, final String base64 )
    {
        requireNonNull( algo, "algo is required" );
        requireNonNull( base64, "base64 is required" );
        return add( directive, "'" + algo.token() + "-" + base64 + "'" );
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
