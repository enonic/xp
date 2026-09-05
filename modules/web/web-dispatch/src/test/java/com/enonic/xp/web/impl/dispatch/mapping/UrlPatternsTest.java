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

        // as before, the leading slash is part of the pattern: an asterisk-form request target is not a path
        assertFalse( matcher.test( "*" ) );
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
        // the prefix and the suffix may not overlap to make up a match
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
    void regexMetacharactersAreLiteral()
    {
        // spliced into a regular expression, the dot used to match any character here
        final Predicate<String> dot = matcher( "/api/v1.0/*" );
        assertTrue( dot.test( "/api/v1.0/x" ) );
        assertFalse( dot.test( "/api/v1X0/x" ) );

        // and the pipe used to split the pattern into two alternatives
        final Predicate<String> pipe = matcher( "/a|b" );
        assertTrue( pipe.test( "/a|b" ) );
        assertFalse( pipe.test( "/a" ) );
        assertFalse( pipe.test( "b" ) );

        for ( final String metacharacter : List.of( "(", ")", "[", "]", "{", "}", "+", "?", "^", "$", "\\", "." ) )
        {
            // these used to throw PatternSyntaxException out of the registration instead of being matched
            final String urlPattern = "/x" + metacharacter + "y/*";
            assertTrue( matcher( urlPattern ).test( "/x" + metacharacter + "y/z" ), urlPattern );
            assertFalse( matcher( urlPattern ).test( "/xQy/z" ), urlPattern );
        }
    }

    @Test
    void noPatterns()
    {
        // the factory rejects a mapping without url patterns, a matcher without them matches nothing
        assertFalse( UrlPatterns.matcher( Set.of() ).test( "/" ) );
    }
}
