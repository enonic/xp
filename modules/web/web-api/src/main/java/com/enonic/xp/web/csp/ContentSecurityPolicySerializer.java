package com.enonic.xp.web.csp;

import java.util.SequencedSet;

import org.jspecify.annotations.NullMarked;

/**
 * Renders a {@link ContentSecurityPolicy} rule set to its header value. This is the platform's
 * response-serialization step, run when the web response completes; contributors build the policy
 * up through {@link ContentSecurityPolicy} and never call this.
 *
 * <p>Directives are emitted in alphabetical order for deterministic output; sources within a
 * directive follow insertion order, as the plain union of all contributions — the browser applies
 * its own precedence between interacting sources (the policy does not). The one tidy-up: a
 * redundant {@code 'none'} (the union identity — it matches nothing) is dropped from a directive
 * that also carries real sources. Non-empty policies appended via
 * {@link ContentSecurityPolicy#addPolicy()} follow, comma-separated, in insertion order — the
 * standard form for several policies in one header value.</p>
 */
@NullMarked
public final class ContentSecurityPolicySerializer
{
    private static final String NONE = "'none'";

    private ContentSecurityPolicySerializer()
    {
    }

    /**
     * The {@code Content-Security-Policy} (or {@code Content-Security-Policy-Report-Only}) header
     * value for {@code policy}, or the empty string when it carries no directives — callers skip
     * emitting the header in that case.
     */
    public static String serialize( final ContentSecurityPolicy policy )
    {
        final StringBuilder sb = new StringBuilder();
        for ( final String directive : policy.directiveNames() )
        {
            final SequencedSet<String> sources = policy.directive( directive ).orElseThrow();
            if ( sb.length() > 0 )
            {
                sb.append( "; " );
            }
            sb.append( directive );
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
        for ( final ContentSecurityPolicy additional : policy.addedPolicies() )
        {
            final String value = serialize( additional );
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
}
