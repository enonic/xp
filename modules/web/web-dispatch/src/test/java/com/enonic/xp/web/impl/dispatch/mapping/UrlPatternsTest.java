package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlPatternsTest
{
    private static Predicate<String> matcher( final String... urlPatterns )
    {
        return UrlPatterns.matcher( new LinkedHashSet<>( List.of( urlPatterns ) ) );
    }

    @Test
    void matchAll()
    {
        final Predicate<String> matcher = matcher( "/*" );

        assertTrue( matcher.test( "/" ) );
        assertTrue( matcher.test( "/a/b/c" ) );
    }

    @Test
    void matchAll_requiresTheLeadingSlash()
    {
        assertFalse( matcher( "/*" ).test( "*" ) );
    }

    @Test
    void prefix()
    {
        final Predicate<String> matcher = matcher( "/admin/*" );

        assertTrue( matcher.test( "/admin/" ) );
        assertTrue( matcher.test( "/admin/tool" ) );

        assertFalse( matcher.test( "/admin" ) );
        assertFalse( matcher.test( "/adminx/tool" ) );
        assertFalse( matcher.test( "/other/admin/" ) );
    }

    @Test
    void extension()
    {
        final Predicate<String> matcher = matcher( "*.js" );

        assertTrue( matcher.test( "/app/main.js" ) );
        assertTrue( matcher.test( ".js" ) );

        assertFalse( matcher.test( "/app/main.json" ) );
        assertFalse( matcher.test( "/js" ) );
    }

    @Test
    void exact()
    {
        final Predicate<String> matcher = matcher( "/health" );

        assertTrue( matcher.test( "/health" ) );

        assertFalse( matcher.test( "/health/" ) );
        assertFalse( matcher.test( "/health/live" ) );
        assertFalse( matcher.test( "/x/health" ) );
    }

    @Test
    void prefixAndSuffix()
    {
        final Predicate<String> matcher = matcher( "/app/*.js" );

        assertTrue( matcher.test( "/app/main.js" ) );
        assertTrue( matcher.test( "/app/.js" ) );

        assertFalse( matcher.test( "/app/main.css" ) );
        assertFalse( matcher.test( "/other/main.js" ) );
    }

    @Test
    void prefixAndSuffix_mayNotOverlap()
    {
        final Predicate<String> matcher = matcher( "/app/*.js" );

        assertFalse( matcher.test( "/app/" ) );
        assertFalse( matcher.test( "/app" ) );
    }

    @Test
    void severalWildcards()
    {
        final Predicate<String> matcher = matcher( "/a/*/b/*.html" );

        assertTrue( matcher.test( "/a//b/.html" ) );
        assertTrue( matcher.test( "/a/x/b/y.html" ) );
        assertTrue( matcher.test( "/a/x/y/b/z.html" ) );

        assertFalse( matcher.test( "/a/x/y.html" ) );
        assertFalse( matcher.test( "/a/x/b/y.htm" ) );
    }

    @Test
    void severalPatterns()
    {
        final Predicate<String> matcher = matcher( "/health", "/ready", "/metrics/*" );

        assertTrue( matcher.test( "/health" ) );
        assertTrue( matcher.test( "/ready" ) );
        assertTrue( matcher.test( "/metrics/jvm" ) );

        assertFalse( matcher.test( "/live" ) );
    }

    @Test
    void dotMatchesOnlyADot()
    {
        final Predicate<String> matcher = matcher( "/api/v1.0/*" );

        assertTrue( matcher.test( "/api/v1.0/x" ) );
        assertFalse( matcher.test( "/api/v1X0/x" ) );
    }

    @Test
    void pipeDoesNotSeparateAlternatives()
    {
        final Predicate<String> matcher = matcher( "/a|b" );

        assertTrue( matcher.test( "/a|b" ) );
        assertFalse( matcher.test( "/a" ) );
        assertFalse( matcher.test( "b" ) );
    }

    @Test
    void everyMetacharacterIsLiteral()
    {
        for ( final String metacharacter : List.of( "(", ")", "[", "]", "{", "}", "+", "?", "^", "$", "\\", "." ) )
        {
            final String urlPattern = "/x" + metacharacter + "y/*";
            assertTrue( matcher( urlPattern ).test( "/x" + metacharacter + "y/z" ), urlPattern );
            assertFalse( matcher( urlPattern ).test( "/xQy/z" ), urlPattern );
        }
    }

    @Test
    void noPatterns()
    {
        assertFalse( UrlPatterns.matcher( Set.of() ).test( "/" ) );
    }
}
