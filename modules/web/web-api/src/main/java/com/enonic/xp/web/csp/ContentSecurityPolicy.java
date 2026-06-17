package com.enonic.xp.web.csp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
 * <p>Contributions are merged by plain <b>union</b>: each directive's serialized-source-list is the
 * union of every contributor's source expressions, emitted as written. The API deliberately does
 * <i>not</i> arbitrate between source expressions that interact in the browser — notably
 * {@code 'unsafe-inline'} sharing a directive with a nonce-source, hash-source, or
 * {@code 'strict-dynamic'} (any of which makes the browser ignore {@code 'unsafe-inline'}). That
 * precedence is the browser's documented behaviour and the secure default; a contributor that
 * genuinely needs the looser source to win uses a policy-level method rather than relying on emergent
 * merge magic.</p>
 *
 * <p><b>Contributor methods</b> (used by parts, layouts, widgets, page controllers) are additive:
 * the per-directive methods ({@link #scriptSrc}, {@link #styleSrc}, {@link #imgSrc}, …, the nonce and
 * hash helpers), the generic {@link #add}, and {@link #merge} (union the directives parsed from a
 * raw header value). They only union source expressions.</p>
 *
 * <p><b>Policy-level methods</b> (for the platform or site owner) change the whole policy and are not
 * additive — use sparingly, not from a part: {@link #strict()} (deny-all baseline), {@link #override}
 * (replace a directive's sources, dropping what others set), {@link #reset} (remove directives),
 * {@link #resetTo} (replace the policy with a raw header value's rules; an empty value clears
 * it) and {@link #clearAdditionalPolicies()} (drop the appended enforced policies). There is no per-source
 * removal; {@code reset} removes whole directives. To relax another contributor's hardening — e.g. an
 * editor that must allow inline styles over a strict {@code style-src} — {@code override} the directive:
 * replacing it drops the nonce/hash that would otherwise neutralize {@code 'unsafe-inline'}</p>
 *
 * <p>Since the enforcing and report-only headers can legitimately coexist on one response, the
 * report-only rules are a second, independent rule set reached via {@link #reportOnly()}. It shares
 * the request nonce and builds its own {@code Content-Security-Policy-Report-Only} value.</p>
 *
 * <p>A response can also carry several <i>enforced</i> policies: the browser enforces each one
 * independently, so a load must satisfy all of them — an extra policy can only restrict, never
 * broaden. {@link #addPolicy()} appends such a policy (comma-joined into the same header value by
 * {@link #serialize()}), for a context that must impose a baseline on content
 * whose own policy it does not fully trust. Conversely, a trusted rendering host that frames and
 * instruments untrusted content uses {@link #clearAdditionalPolicies()} to drop additional enforced
 * policies the content contributed (which it cannot relax otherwise), and empties the
 * {@link #reportOnly()} rule set ({@code reportOnly().resetTo(null).clearAdditionalPolicies()}) so a
 * report-only policy it contributed does not ride onto its response.</p>
 *
 * <p>A nonce-source is valid only on the script and style fetch directives — {@code script-src}
 * and {@code script-src-elem}, {@code style-src} and {@code style-src-elem}: use
 * {@link #nonceScriptSrc()}, {@link #nonceScriptSrcElem()}, {@link #nonceStyleSrc()} or
 * {@link #nonceStyleSrcElem()}. They all return the same request-scoped base64 value to set as the
 * {@code nonce} attribute on the matching inline element. The request nonce is the <i>only</i> nonce
 * this policy will carry: a nonce-source supplied from outside is rejected by {@link #add} /
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

    private static final String NONCE_SOURCE_PREFIX = "'nonce-";

    private static final Pattern DIRECTIVE_NAME = Pattern.compile( "[a-zA-Z][a-zA-Z0-9-]*" );

    private final Map<String, LinkedHashSet<String>> directives = new TreeMap<>();

    private final List<ContentSecurityPolicy> additionalPolicies = new ArrayList<>();

    private final Nonce nonce;

    private final boolean topLevel;

    @Nullable
    private ContentSecurityPolicy reportOnly;

    public ContentSecurityPolicy()
    {
        this.nonce = new Nonce();
        this.topLevel = true;
    }

    private ContentSecurityPolicy( final Nonce nonce )
    {
        this.nonce = nonce;
        this.topLevel = false;
    }

    /**
     * The companion rule set emitted as {@code Content-Security-Policy-Report-Only}. The two
     * headers can legitimately coexist on one response (e.g. enforce a settled policy while
     * trialling a stricter one), so the report-only rules are an independent
     * {@code ContentSecurityPolicy} with the full contributor/policy-level API; it shares the
     * request nonce. Lazily created; while left empty, no report-only header is emitted.
     *
     * <p>Report-only is a single companion of the request's top-level enforced policy, so this is
     * only valid there. Calling it on an added policy ({@link #addPolicy()}) or on the report-only
     * companion itself throws {@link IllegalStateException}: report-only has no report-only of its
     * own, and an added policy is not where the request's companion lives. Report-only policies never
     * block; each is just one more observed policy, so for several of them call {@link #addPolicy()}
     * on the returned set.</p>
     *
     * @throws IllegalStateException if called on an added or report-only policy rather than on the
     * top-level enforced policy
     */
    public ContentSecurityPolicy reportOnly()
    {
        if ( !this.topLevel )
        {
            throw new IllegalStateException(
                "reportOnly() is only available on the request's top-level enforced policy, not on an added or report-only policy" );
        }
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
     * site content); each call appends one more policy, comma-joined into the same header value.
     * While left empty, an added policy is not emitted.
     *
     * <p>The returned rule set has the full API and shares the request nonce. Order matters when
     * seeding it from a raw header value: {@link #resetTo} first (it starts from a clean slate and
     * would drop an already-wired nonce entry), then {@link #nonceScriptSrc()} so inline scripts
     * stamped with the request nonce satisfy the added policy too. Do not nonce a directive that
     * relies on {@code 'unsafe-inline'}: a nonce makes the browser ignore it.</p>
     *
     * <p>Added policies are separate policies, not directives: {@link #reset} and {@link #resetTo}
     * on this policy do not touch them.</p>
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
     * only the {@code nonce*} methods may generate.
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
     * Unions every directive parsed from a raw header value into this policy.
     * Existing directives are extended and absent ones are added,
     * so a caller can grant extra permissions on top of a policy built elsewhere without
     * restating it (and without discarding a nonce already wired into {@code script-src}/{@code style-src}).
     * Parsing is lenient, mirroring the browser: tokens that would not survive
     * {@link #add} validation are skipped rather than thrown, and {@code 'nonce-…'} sources are dropped
     * (a nonce baked into a header value is static across requests). A {@code null} value adds nothing.
     * Several comma-separated policies are flattened into one additive set of directives — no additional
     * enforced policy is created, since an extra policy could only restrict, the opposite of the intent.
     * Additive — safe to call from a contributor.
     */
    public ContentSecurityPolicy merge( @Nullable final String headerValue )
    {
        if ( headerValue == null )
        {
            return this;
        }
        for ( final String part : headerValue.replace( ',', ';' ).split( ";" ) )
        {
            final String[] tokens = part.trim().split( "\\s+" );
            final String directive = tokens[0];
            if ( !DIRECTIVE_NAME.matcher( directive ).matches() )
            {
                continue;
            }
            final LinkedHashSet<String> set = this.directives.computeIfAbsent( directive, k -> new LinkedHashSet<>() );
            for ( int i = 1; i < tokens.length; i++ )
            {
                if ( isValidSource( tokens[i] ) )
                {
                    set.add( tokens[i] );
                }
            }
        }
        return this;
    }

    /**
     * The sources currently declared for {@code directive}, in the order they are emitted (already
     * deduplicated, since contributions union into a set), or {@link Optional#empty()} if no
     * contributor has declared it. A present directive with no sources (a boolean directive such as
     * {@code upgrade-insecure-requests}) returns an empty list. The returned list is an immutable
     * snapshot. Lets a baseline gap-fill what is missing without overriding what is set —
     * {@code if ( policy.directive( name ).isEmpty() ) policy.add( name, … )} — and a contributor
     * probe what another already set. Reads this rule set only; the report-only set is reached via
     * {@link #reportOnly()}.
     */
    public Optional<List<String>> directive( final String directive )
    {
        final LinkedHashSet<String> sources = this.directives.get( directive );
        return sources == null ? Optional.empty() : Optional.of( List.copyOf( sources ) );
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
     * argument, removes nothing. To clear the whole policy use {@link #resetTo} with an empty
     * value. A policy-level escape hatch — not additive.
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
     * Replaces this rule set's directives with the single policy parsed from a raw header value. This
     * is the meaning a {@code Content-Security-Policy} header set directly by a controller gets when
     * the platform folds it into the request policy: the directly-set header overrides everything
     * contributed before it, while later contributions still apply on top. A {@code null}, empty
     * or blank value clears the directives — if nothing is added afterwards, no header is emitted.
     * The request nonce stays stable. Parsing is lenient, mirroring the browser: tokens that would
     * not survive {@link #add} validation are skipped rather than thrown (hand-built headers are
     * arbitrary), and of repeated directives only the first occurrence counts.
     * {@code 'nonce-…'} sources are likewise dropped — a nonce baked into a header value is static
     * across requests; use the {@code nonce*} methods.
     *
     * <p>Operates on this rule set's directives only. A {@code ,} (which in a header value would
     * begin a further enforced policy) and everything after it is ignored — only the first policy is
     * applied; this never creates or touches {@link #addPolicy() additional policies}. To impose
     * several enforced policies, call {@link #addPolicy()} explicitly. A policy-level escape hatch —
     * not additive.</p>
     */
    public ContentSecurityPolicy resetTo( @Nullable final String headerValue )
    {
        this.directives.clear();
        if ( headerValue == null )
        {
            return this;
        }
        final int comma = headerValue.indexOf( ',' );
        parsePolicy( comma < 0 ? headerValue : headerValue.substring( 0, comma ) );
        return this;
    }

    /**
     * Removes every <i>additional</i> enforced policy appended to this rule set via
     * {@link #addPolicy()}, while leaving this rule set's own directives untouched. The
     * {@link #reportOnly()} rule set is a sibling and is not affected.
     *
     * <p>For a trusted rendering host (e.g. an admin context that frames and instruments content it
     * does not fully trust): an additional enforced policy can only <i>restrict</i> — the browser
     * enforces every policy on the response independently — so one contributed by the framed content
     * could block resources the host injects, and the host cannot relax it by editing its own
     * directives. Dropping the additional policies lets the host's own policy be authoritative. A
     * policy-level escape hatch — not additive.</p>
     */
    public ContentSecurityPolicy clearAdditionalPolicies()
    {
        this.additionalPolicies.clear();
        return this;
    }

    private void parsePolicy( final String policyValue )
    {
        for ( final String part : policyValue.split( ";" ) )
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

    public ContentSecurityPolicy defaultSrc( final String... sources )
    {
        return add( CspDirective.DEFAULT_SRC, sources );
    }

    public ContentSecurityPolicy scriptSrc( final String... sources )
    {
        return add( CspDirective.SCRIPT_SRC, sources );
    }

    public ContentSecurityPolicy styleSrc( final String... sources )
    {
        return add( CspDirective.STYLE_SRC, sources );
    }

    public ContentSecurityPolicy imgSrc( final String... sources )
    {
        return add( CspDirective.IMG_SRC, sources );
    }

    public ContentSecurityPolicy fontSrc( final String... sources )
    {
        return add( CspDirective.FONT_SRC, sources );
    }

    public ContentSecurityPolicy connectSrc( final String... sources )
    {
        return add( CspDirective.CONNECT_SRC, sources );
    }

    public ContentSecurityPolicy mediaSrc( final String... sources )
    {
        return add( CspDirective.MEDIA_SRC, sources );
    }

    public ContentSecurityPolicy objectSrc( final String... sources )
    {
        return add( CspDirective.OBJECT_SRC, sources );
    }

    public ContentSecurityPolicy frameSrc( final String... sources )
    {
        return add( CspDirective.FRAME_SRC, sources );
    }

    public ContentSecurityPolicy workerSrc( final String... sources )
    {
        return add( CspDirective.WORKER_SRC, sources );
    }

    public ContentSecurityPolicy manifestSrc( final String... sources )
    {
        return add( CspDirective.MANIFEST_SRC, sources );
    }

    public ContentSecurityPolicy childSrc( final String... sources )
    {
        return add( CspDirective.CHILD_SRC, sources );
    }

    public ContentSecurityPolicy frameAncestors( final String... sources )
    {
        return add( CspDirective.FRAME_ANCESTORS, sources );
    }

    public ContentSecurityPolicy baseUri( final String... sources )
    {
        return add( CspDirective.BASE_URI, sources );
    }

    public ContentSecurityPolicy formAction( final String... sources )
    {
        return add( CspDirective.FORM_ACTION, sources );
    }

    public ContentSecurityPolicy scriptSrcElem( final String... sources )
    {
        return add( CspDirective.SCRIPT_SRC_ELEM, sources );
    }

    public ContentSecurityPolicy scriptSrcAttr( final String... sources )
    {
        return add( CspDirective.SCRIPT_SRC_ATTR, sources );
    }

    public ContentSecurityPolicy styleSrcElem( final String... sources )
    {
        return add( CspDirective.STYLE_SRC_ELEM, sources );
    }

    public ContentSecurityPolicy styleSrcAttr( final String... sources )
    {
        return add( CspDirective.STYLE_SRC_ATTR, sources );
    }

    /**
     * Adds the {@code report-to} directive naming a reporting group. The group must be defined by a
     * companion {@code Reporting-Endpoints} response header — that header is the caller's
     * responsibility. ({@code report-uri} is deprecated; use {@link #add} if you still need it.)
     */
    public ContentSecurityPolicy reportTo( final String group )
    {
        return add( CspDirective.REPORT_TO, group );
    }

    /**
     * Registers {@code require-trusted-types-for 'script'}.
     */
    public ContentSecurityPolicy requireTrustedTypesForScript()
    {
        return add( CspDirective.REQUIRE_TRUSTED_TYPES_FOR, "'script'" );
    }

    /**
     * Adds (user-defined) policy names and/or the special {@link TrustedTypesKeyword} keyword
     * constants ({@code 'none'}, {@code 'allow-duplicates'}, {@code *}) to the {@code trusted-types}
     * directive.
     */
    public ContentSecurityPolicy trustedTypes( final String... values )
    {
        return add( CspDirective.TRUSTED_TYPES, values );
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
        return add( CspDirective.SANDBOX, tokens );
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
        return addComputedSha( CspDirective.SCRIPT_SRC, algo, content );
    }

    /**
     * Unions a precomputed {@code '<algo>-<base64>'} digest into {@code script-src}.
     */
    public ContentSecurityPolicy shaScriptSrc( final HashAlgo algo, final String base64 )
    {
        return addPrecomputedSha( CspDirective.SCRIPT_SRC, algo, base64 );
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
        return addComputedSha( CspDirective.STYLE_SRC, algo, content );
    }

    /**
     * Unions a precomputed {@code '<algo>-<base64>'} digest into {@code style-src}.
     */
    public ContentSecurityPolicy shaStyleSrc( final HashAlgo algo, final String base64 )
    {
        return addPrecomputedSha( CspDirective.STYLE_SRC, algo, base64 );
    }

    /**
     * Adds a nonce-source ({@code 'nonce-<base64>'}) carrying the request nonce to {@code script-src}
     * and returns the base64 value, to set as the {@code nonce} attribute of the matching inline
     * {@code <script>}.
     */
    public String nonceScriptSrc()
    {
        return nonceFor( CspDirective.SCRIPT_SRC );
    }

    /**
     * Adds a nonce-source for the request nonce to {@code script-src-elem} and returns its base64
     * value, for a {@code <script>} element under a {@code script-src-elem} that uses
     * {@code 'strict-dynamic'}.
     */
    public String nonceScriptSrcElem()
    {
        return nonceFor( CspDirective.SCRIPT_SRC_ELEM );
    }

    /**
     * Adds a nonce-source ({@code 'nonce-<base64>'}) carrying the request nonce to {@code style-src}
     * and returns the base64 value, to set as the {@code nonce} attribute of the matching inline
     * {@code <style>}.
     */
    public String nonceStyleSrc()
    {
        return nonceFor( CspDirective.STYLE_SRC );
    }

    /**
     * Adds a nonce-source ({@code 'nonce-<base64>'}) carrying the request nonce to
     * {@code style-src-elem} and returns the base64 value, to set as the {@code nonce} attribute of
     * the matching {@code <style>} or {@code <link rel=stylesheet>}.
     */
    public String nonceStyleSrcElem()
    {
        return nonceFor( CspDirective.STYLE_SRC_ELEM );
    }

    /**
     * The nonce is a cryptographically random, base64-encoded value (≥ 128 bits of entropy). Every
     * {@code nonce*} call returns the same value for the life of this policy instance (= life of
     * the {@code PortalRequest}); each adds {@code 'nonce-<value>'} to the directive it targets.
     * If no {@code nonce*} method is ever called, no {@code nonce-} entry is emitted anywhere.
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
     * that case. This is the platform's response-serialization step, run when the web response
     * completes; contributors build the policy up through the methods above and do not call this.
     *
     * <p>Directives are emitted in alphabetical order for deterministic output; sources within a
     * directive follow insertion order. Sources are emitted as the plain union of all contributions —
     * the browser applies its own precedence between interacting sources (the policy does not). The
     * one tidy-up: a redundant {@code 'none'} (the union identity — it matches nothing) is omitted
     * from a directive that also carries real sources, since any real source already supersedes it.</p>
     *
     * <p>Non-empty policies appended via {@link #addPolicy()} follow, comma-separated, in insertion
     * order — the standard form for several policies in one header value.</p>
     */
    public String serialize()
    {
        final StringBuilder sb = new StringBuilder();
        for ( final Map.Entry<String, LinkedHashSet<String>> entry : this.directives.entrySet() )
        {
            if ( !sb.isEmpty() )
            {
                sb.append( "; " );
            }
            sb.append( entry.getKey() );
            final LinkedHashSet<String> sources = entry.getValue();
            final boolean dropNone = sources.size() > 1 && sources.contains( CspSource.NONE );
            for ( final String source : sources )
            {
                if ( dropNone && source.equals( CspSource.NONE ) )
                {
                    continue;
                }
                sb.append( ' ' ).append( source );
            }
        }
        for ( final ContentSecurityPolicy additional : this.additionalPolicies )
        {
            final String value = additional.serialize();
            if ( !value.isEmpty() )
            {
                if ( !sb.isEmpty() )
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
                "A 'nonce-' source cannot be supplied; only the nonce* methods generate the request nonce: " + source );
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
