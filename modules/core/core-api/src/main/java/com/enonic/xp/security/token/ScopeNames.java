package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

/**
 * Scope names as RFC 6749 section 3.3 defines them: one or more printable ASCII characters,
 * excluding space, double quote and backslash.
 */
@PublicApi
@NullMarked
public final class ScopeNames
{
    private ScopeNames()
    {
    }

    /**
     * Returns the scope when it is a valid scope name.
     *
     * @throws IllegalArgumentException when it is not
     */
    public static String checkScope( final String scope )
    {
        if ( !isScope( scope ) )
        {
            throw new IllegalArgumentException( "Not a scope name: [" + scope + "]" );
        }
        return scope;
    }

    /**
     * Whether the string is a valid scope name.
     */
    public static boolean isScope( final String scope )
    {
        if ( scope.isEmpty() )
        {
            return false;
        }
        for ( int i = 0; i < scope.length(); i++ )
        {
            final char c = scope.charAt( i );
            if ( c < 0x21 || c > 0x7E || c == '"' || c == '\\' )
            {
                return false;
            }
        }
        return true;
    }
}
