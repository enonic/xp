package com.enonic.xp.web.csp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * A Content Security Policy carried on {@link com.enonic.xp.web.WebRequest}.
 *
 * <p>Mutable and request-scoped: many contributors (platform, site app, apps, widgets, page
 * controllers) extend the same policy during rendering, and the platform serializes it into the
 * {@code Content-Security-Policy} response header alongside the other headers, so late additions
 * still land. A non-empty policy wins over a header set directly on the response; a header a
 * portal controller sets directly is folded into the policy first ({@link #resetTo} semantics), so
 * later contributions still apply on top of it.</p>
 *
 * <p>Contributions are merged by plain <b>union</b>: each directive's source list is the union of
 * every contributor's sources, emitted as written. The API deliberately does <i>not</i> arbitrate
 * between sources that interact in the browser — notably {@code 'unsafe-inline'} sharing a directive
 * with a {@code 'nonce-…'}, hash, or {@code 'strict-dynamic'} (which makes the browser ignore
 * {@code 'unsafe-inline'}). That precedence is the browser's documented behaviour and the secure
 * default; a contributor that genuinely needs the looser source to win uses a policy-level method
 * rather than relying on emergent merge magic.</p>
 *
 * <p><b>Contributor methods</b> (used by parts, layouts, widgets, page controllers) are additive:
 * the per-directive methods ({@link #scriptSrc}, {@link #styleSrc}, {@link #imgSrc}, …, the nonce and
 * hash helpers) and the generic {@link #add}. They only union sources.</p>
 *
 * <p><b>Policy-level methods</b> (for the platform or site owner) change the whole policy and are not
 * additive — use sparingly, not from a part: {@link #strict()} (deny-all baseline), {@link #override}
 * (replace a directive's sources, dropping what others set), {@link #reset} / {@link #resetAll()}
 * (remove directives, or all of them), and {@link #resetTo} (replace the policy with a raw header
 * value's rules). There is no per-source
 * removal; {@code reset} removes whole directives. To relax another contributor's hardening — e.g. an
 * editor that must allow inline styles over a strict {@code style-src} — {@code override} the
 * directive: replacing it drops the nonce/hash that would otherwise neutralize {@code 'unsafe-inline'},
 * so the relaxation is an explicit, greppable act by the contributor that needs it.</p>
 *
 * <p>Since the enforcing and report-only headers can legitimately coexist on one response, the
 * report-only rules are a second, independent rule set reached via {@link #reportOnly()}. It shares
 * the request nonce and builds its own {@code Content-Security-Policy-Report-Only} value.
 * Deliberately not exposed to the JavaScript API.</p>
 *
 * <p>A response can also carry several <i>enforced</i> policies: the browser enforces each one
 * independently, so a load must satisfy all of them — an extra policy can only restrict, never
 * broaden. {@link #addPolicy()} appends such a policy (comma-joined into the same header by
 * {@link #build()}), for a context that must impose a baseline on content whose own policy it does
 * not fully trust. Also not exposed to the JavaScript API.</p>
 *
 * <p>A {@code 'nonce-'} source is valid only on {@code script-src} and {@code style-src}: use
 * {@link #nonceScriptSrc()} or {@link #nonceStyleSrc()}. Both return the same request-scoped value to
 * stamp on the matching inline tag. The request nonce is the <i>only</i> nonce this policy will
 * carry: a {@code 'nonce-…'} source supplied from outside is rejected by {@link #add} /
 * {@link #override} and dropped by {@link #resetTo}, because a caller-supplied nonce is necessarily
 * static across requests (or worse, attacker-known), which defeats the point of nonces.</p>
 */
@NullMarked
public final class ContentSecurityPolicy
{
    public static final String HEADER_NAME = "Content-Security-Policy";

    public static final String REPORT_ONLY_HEADER_NAME = "Content-Security-Policy-Report-Only";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final Base64.Encoder HASH_BASE64 = Base64.getEncoder();

    private static final Base64.Encoder NONCE_BASE64 = Base64.getUrlEncoder().withoutPadding();

    private static final int NONCE_BYTE_LENGTH = 16;

    private static final String SCRIPT_SRC = "script-src";

    private static final String STYLE_SRC = "style-src";

    private static final String NONE = "'none'";

    private static final String NONCE_SOURCE_PREFIX = "'nonce-";

    private static final Pattern DIRECTIVE_NAME = Pattern.compile( "[a-zA-Z][a-zA-Z0-9-]*" );

    private final Map<String, LinkedHashSet<String>> directives = new TreeMap<>();

    private final List<ContentSecurityPolicy> additionalPolicies = new ArrayList<>();

    private final Nonce nonce;

    @Nullable
    private ContentSecurityPolicy reportOnly;

    public ContentSecurityPolicy()
    {
        this.nonce = new Nonce();
    }

    private ContentSecurityPolicy( final Nonce nonce )
    {
        this.nonce = nonce;
        this.reportOnly = this;
    }

    /**
     * The companion rule set emitted as {@code Content-Security-Policy-Report-Only}. The two
     * headers can legitimately coexist on one response (e.g. enforce a settled policy while
     * trialling a stricter one), so the report-only rules are an independent
     * {@code ContentSecurityPolicy} with the full contributor/policy-level API; it shares the
     * request nonce. Lazily created; while left empty, no report-only header is emitted.
     */
    public ContentSecurityPolicy reportOnly()
    {
        if ( this.reportOnly == null )
        {
            this.reportOnly = new ContentSecurityPolicy( this.nonce );
        }
        return this.reportOnly;
    }

    /**
     * Appends another <i>enforced</i> policy to this response and returns it. The browser enforces
     * every policy independently — a load must satisfy all of them — so an added policy can only
     * restrict, never broaden, what this policy allows. Its use is imposing a baseline on content
     * whose own policy the serving context does not fully trust (e.g. an admin context rendering
     * site content); each call appends one more policy, comma-joined into the same header by
     * {@link #build()}. While left empty, an added policy is not emitted.
     *
     * <p>The returned rule set has the full API — seed it from a raw header value with
     * {@link #resetTo} — and shares the request nonce: chain {@link #nonceScriptSrc()} so inline
     * scripts stamped with the request nonce satisfy the added policy too. Do not nonce a directive
     * that relies on {@code 'unsafe-inline'}: a nonce makes the browser ignore it.</p>
     *
     * <p>Added policies are separate policies, not directives: {@link #reset}, {@link #resetAll()}
     * and {@link #resetTo} on this policy do not touch them. Like {@link #reportOnly()},
     * deliberately not exposed to the JavaScript API.</p>
     */
    public ContentSecurityPolicy addPolicy()
    {
        final ContentSecurityPolicy additional = new ContentSecurityPolicy( this.nonce );
        this.additionalPolicies.add( additional );
        return additional;
    }

    /**
     * Unions {@code sources} into the existing source set for {@code directive}, deduped. With no
     * {@code sources}, registers the directive (useful for boolean directives like
     * {@code upgrade-insecure-requests}).
     *
     * @throws IllegalArgumentException when {@code directive} is not a valid directive name, or a
     * source contains whitespace, control characters, {@code ;} or {@code ,} — tokens that would
     * smuggle extra directives into the emitted header — or is a {@code 'nonce-…'} source, which
     * only {@link #nonceScriptSrc()} / {@link #nonceStyleSrc()} may mint.
     */
    public ContentSecurityPolicy add( final String directive, final String... sources )
    {
        requireNonNull( sources, "sources is required" );
        final LinkedHashSet<String> set = this.directives.computeIfAbsent( validDirective( directive ), k -> new LinkedHashSet<>() );
        for ( final String source : sources )
        {
            set.add( validSource( source ) );
        }
        return this;
    }

    /**
     * Replaces the source list for {@code directive} with exactly {@code sources}, overriding what
     * other contributors set. A policy-level escape hatch — unlike {@link #add(String, String...)} it
     * is not additive and can narrow the policy. There is no freeze, so a later {@code add} still
     * extends. To remove a directive entirely, use {@link #reset(String...)}.
     */
    public ContentSecurityPolicy override( final String directive, final String... sources )
    {
        requireNonNull( sources, "sources is required" );
        final LinkedHashSet<String> set = new LinkedHashSet<>();
        for ( final String source : sources )
        {
            set.add( validSource( source ) );
        }
        this.directives.put( validDirective( directive ), set );
        return this;
    }

    /**
     * Removes the named directives, overriding what other contributors set — e.g.
     * {@code reset("upgrade-insecure-requests")} is how a boolean directive is unset. With no
     * argument, removes nothing. For a clean slate use {@link #resetAll()}. A policy-level escape
     * hatch — not additive.
     */
    public ContentSecurityPolicy reset( final String... directives )
    {
        requireNonNull( directives, "directives is required" );
        for ( final String directive : directives )
        {
            this.directives.remove( requireNonNull( directive, "directive is required" ) );
        }
        return this;
    }

    /**
     * Removes every directive (a clean slate), overriding what other contributors set. The cached
     * nonce is left intact so it stays stable for the request. A policy-level escape hatch — not
     * additive, not for parts.
     */
    public ContentSecurityPolicy resetAll()
    {
        this.directives.clear();
        return this;
    }

    /**
     * Replaces the whole policy with the directives parsed from a raw header value —
     * {@link #resetAll()} plus the header's own rules. This is the meaning a
     * {@code Content-Security-Policy} header set directly by a controller gets when the platform
     * folds it into the request policy: the directly-set header overrides everything contributed
     * before it, while later contributions still apply on top. A {@code null} or empty value is
     * effectively {@code resetAll()} — if nothing is added afterwards, no header is emitted.
     * Parsing is lenient, mirroring the browser: tokens that would not survive {@link #add}
     * validation are skipped rather than thrown (hand-built headers are arbitrary), and of
     * repeated directives only the first occurrence counts. {@code 'nonce-…'} sources are likewise
     * dropped — a nonce baked into a header value is static across requests; use
     * {@link #nonceScriptSrc()} / {@link #nonceStyleSrc()}. A policy-level escape hatch — not
     * additive.
     */
    public ContentSecurityPolicy resetTo( @Nullable final String headerValue )
    {
        resetAll();
        if ( headerValue == null )
        {
            return this;
        }
        for ( final String part : headerValue.split( ";" ) )
        {
            final String[] tokens = part.trim().split( "\\s+" );
            final String directive = tokens[0];
            if ( !DIRECTIVE_NAME.matcher( directive ).matches() || this.directives.containsKey( directive ) )
            {
                continue;
            }
            final LinkedHashSet<String> set = new LinkedHashSet<>();
            for ( int i = 1; i < tokens.length; i++ )
            {
                if ( isValidSource( tokens[i] ) )
                {
                    set.add( tokens[i] );
                }
            }
            this.directives.put( directive, set );
        }
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

    public ContentSecurityPolicy scriptSrcElem( final CspSource... sources )
    {
        return addTokens( "script-src-elem", sources );
    }

    public ContentSecurityPolicy scriptSrcElem( final String... sources )
    {
        return add( "script-src-elem", sources );
    }

    public ContentSecurityPolicy scriptSrcAttr( final CspSource... sources )
    {
        return addTokens( "script-src-attr", sources );
    }

    public ContentSecurityPolicy scriptSrcAttr( final String... sources )
    {
        return add( "script-src-attr", sources );
    }

    public ContentSecurityPolicy styleSrcElem( final CspSource... sources )
    {
        return addTokens( "style-src-elem", sources );
    }

    public ContentSecurityPolicy styleSrcElem( final String... sources )
    {
        return add( "style-src-elem", sources );
    }

    public ContentSecurityPolicy styleSrcAttr( final CspSource... sources )
    {
        return addTokens( "style-src-attr", sources );
    }

    public ContentSecurityPolicy styleSrcAttr( final String... sources )
    {
        return add( "style-src-attr", sources );
    }

    /**
     * Adds the {@code report-to} directive naming a reporting group. The group must be defined by a
     * companion {@code Reporting-Endpoints} response header — that header is the caller's
     * responsibility. ({@code report-uri} is deprecated; use {@link #add} if you still need it.)
     */
    public ContentSecurityPolicy reportTo( final String group )
    {
        return add( "report-to", group );
    }

    /**
     * Registers {@code require-trusted-types-for 'script'} (the only sink group the spec defines).
     */
    public ContentSecurityPolicy requireTrustedTypesForScript()
    {
        return add( "require-trusted-types-for", "'script'" );
    }

    /**
     * Adds the special {@link TrustedTypesKeyword} tokens ({@code 'none'}, {@code 'allow-duplicates'},
     * {@code *}) to the {@code trusted-types} directive. Use {@link #trustedTypes(String...)} for
     * policy names.
     */
    public ContentSecurityPolicy trustedTypes( final TrustedTypesKeyword... keywords )
    {
        requireNonNull( keywords, "keywords is required" );
        final String[] tokens = new String[keywords.length];
        for ( int i = 0; i < keywords.length; i++ )
        {
            tokens[i] = requireNonNull( keywords[i], "keyword is required" ).token();
        }
        return add( "trusted-types", tokens );
    }

    /**
     * Adds (user-defined) policy names to the {@code trusted-types} directive. For the special
     * keywords use {@link #trustedTypes(TrustedTypesKeyword...)}.
     */
    public ContentSecurityPolicy trustedTypes( final String... values )
    {
        return add( "trusted-types", values );
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
    public ContentSecurityPolicy shaScriptSrc( final byte[] content )
    {
        return shaScriptSrc( HashAlgo.SHA256, content );
    }

    /**
     * Computes the {@code algo} digest of {@code content} and unions {@code '<algo>-<base64>'} into
     * {@code script-src}.
     */
    public ContentSecurityPolicy shaScriptSrc( final HashAlgo algo, final byte[] content )
    {
        return addComputedSha( SCRIPT_SRC, algo, content );
    }

    /**
     * Unions a precomputed {@code '<algo>-<base64>'} digest into {@code script-src}.
     */
    public ContentSecurityPolicy shaScriptSrc( final HashAlgo algo, final String base64 )
    {
        return addPrecomputedSha( SCRIPT_SRC, algo, base64 );
    }

    /**
     * Computes the SHA-256 digest of {@code content} and unions {@code 'sha256-<base64>'} into
     * {@code style-src}.
     */
    public ContentSecurityPolicy shaStyleSrc( final byte[] content )
    {
        return shaStyleSrc( HashAlgo.SHA256, content );
    }

    /**
     * Computes the {@code algo} digest of {@code content} and unions {@code '<algo>-<base64>'} into
     * {@code style-src}.
     */
    public ContentSecurityPolicy shaStyleSrc( final HashAlgo algo, final byte[] content )
    {
        return addComputedSha( STYLE_SRC, algo, content );
    }

    /**
     * Unions a precomputed {@code '<algo>-<base64>'} digest into {@code style-src}.
     */
    public ContentSecurityPolicy shaStyleSrc( final HashAlgo algo, final String base64 )
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
        final String nonceValue = this.nonce.get();
        this.directives.computeIfAbsent( directive, k -> new LinkedHashSet<>() ).add( "'nonce-" + nonceValue + "'" );
        return nonceValue;
    }

    /**
     * Renders the current state as a {@code Content-Security-Policy} header value. Returns the
     * empty string when no directive has been added — callers should skip emitting the header in
     * that case.
     *
     * <p>Directives are emitted in alphabetical order for deterministic output; sources within a
     * directive follow insertion order. Sources are emitted as the plain union of all contributions —
     * the browser applies its own precedence between interacting sources (the API does not). The one
     * tidy-up: a redundant {@code 'none'} (the union identity — it matches nothing) is omitted from a
     * directive that also carries real sources, since any real source already supersedes it.</p>
     *
     * <p>Non-empty policies appended via {@link #addPolicy()} follow, comma-separated, in insertion
     * order — the standard form for several policies in one header value.</p>
     */
    public String build()
    {
        final StringBuilder sb = new StringBuilder();
        for ( final Map.Entry<String, LinkedHashSet<String>> entry : this.directives.entrySet() )
        {
            if ( sb.length() > 0 )
            {
                sb.append( "; " );
            }
            sb.append( entry.getKey() );
            final LinkedHashSet<String> sources = entry.getValue();
            final boolean dropNone = sources.size() > 1 && sources.contains( NONE );
            for ( final String source : sources )
            {
                if ( dropNone && source.equals( NONE ) )
                {
                    continue;
                }
                sb.append( ' ' ).append( source );
            }
        }
        for ( final ContentSecurityPolicy additional : this.additionalPolicies )
        {
            final String value = additional.build();
            if ( !value.isEmpty() )
            {
                if ( sb.length() > 0 )
                {
                    sb.append( ", " );
                }
                sb.append( value );
            }
        }
        return sb.toString();
    }

    private static String validDirective( final String directive )
    {
        requireNonNull( directive, "directive is required" );
        if ( !DIRECTIVE_NAME.matcher( directive ).matches() )
        {
            throw new IllegalArgumentException( "Invalid CSP directive name: " + directive );
        }
        return directive;
    }

    private static String validSource( final String source )
    {
        requireNonNull( source, "source is required" );
        if ( isExternalNonce( source ) )
        {
            throw new IllegalArgumentException(
                "A 'nonce-' source cannot be supplied; only nonceScriptSrc()/nonceStyleSrc() mint the request nonce: " + source );
        }
        if ( !isValidSource( source ) )
        {
            throw new IllegalArgumentException( "Invalid CSP source: " + source );
        }
        return source;
    }

    private static boolean isExternalNonce( final String source )
    {
        return source.regionMatches( true, 0, NONCE_SOURCE_PREFIX, 0, NONCE_SOURCE_PREFIX.length() );
    }

    private static boolean isValidSource( final String source )
    {
        if ( source.isEmpty() || isExternalNonce( source ) )
        {
            return false;
        }
        for ( int i = 0; i < source.length(); i++ )
        {
            final char c = source.charAt( i );
            if ( c <= ' ' || c == ';' || c == ',' || c == 0x7F )
            {
                return false;
            }
        }
        return true;
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

    /**
     * The request nonce, shared between the enforcing and report-only rule sets: a
     * cryptographically random, base64-encoded value (≥ 128 bits of entropy), lazily generated and
     * cached on first use.
     */
    private static final class Nonce
    {
        @Nullable
        private String value;

        String get()
        {
            if ( this.value == null )
            {
                final byte[] bytes = new byte[NONCE_BYTE_LENGTH];
                SECURE_RANDOM.nextBytes( bytes );
                this.value = NONCE_BASE64.encodeToString( bytes );
            }
            return this.value;
        }
    }
}
