package com.enonic.xp.impl.server.rest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UrlAllowListTest
{
    @Test
    void null_patterns_match_nothing()
    {
        final UrlAllowList list = new UrlAllowList( null );
        assertThat( list.matches( "https://example.com/app.jar" ) ).isFalse();
    }

    @Test
    void empty_patterns_match_nothing()
    {
        final UrlAllowList list = new UrlAllowList( "" );
        assertThat( list.matches( "https://example.com/app.jar" ) ).isFalse();
    }

    @Test
    void blank_patterns_match_nothing()
    {
        final UrlAllowList list = new UrlAllowList( "   " );
        assertThat( list.matches( "https://example.com/app.jar" ) ).isFalse();
    }

    @Test
    void exact_match_succeeds()
    {
        final UrlAllowList list = new UrlAllowList( "https://example.com/app.jar" );
        assertThat( list.matches( "https://example.com/app.jar" ) ).isTrue();
    }

    @Test
    void exact_match_rejects_other()
    {
        final UrlAllowList list = new UrlAllowList( "https://example.com/app.jar" );
        assertThat( list.matches( "https://example.com/app.jar.bak" ) ).isFalse();
        assertThat( list.matches( "https://example.com/other.jar" ) ).isFalse();
    }

    @Test
    void wildcard_prefix_matches_any_suffix()
    {
        final UrlAllowList list = new UrlAllowList( "https://example.com/apps/*" );
        assertThat( list.matches( "https://example.com/apps/foo.jar" ) ).isTrue();
        assertThat( list.matches( "https://example.com/apps/sub/foo.jar" ) ).isTrue();
        assertThat( list.matches( "https://example.com/apps/" ) ).isTrue();
    }

    @Test
    void wildcard_prefix_rejects_non_matching()
    {
        final UrlAllowList list = new UrlAllowList( "https://example.com/apps/*" );
        assertThat( list.matches( "https://example.com/other/foo.jar" ) ).isFalse();
        assertThat( list.matches( "https://example.com/apps" ) ).isFalse();
        assertThat( list.matches( "http://example.com/apps/foo.jar" ) ).isFalse();
    }

    @Test
    void comma_separated_patterns_match_any()
    {
        final UrlAllowList list = new UrlAllowList( "https://a.example/*, https://b.example/*" );
        assertThat( list.matches( "https://a.example/foo" ) ).isTrue();
        assertThat( list.matches( "https://b.example/bar" ) ).isTrue();
        assertThat( list.matches( "https://c.example/baz" ) ).isFalse();
    }

    @Test
    void blank_entries_in_list_are_ignored()
    {
        final UrlAllowList list = new UrlAllowList( "https://a.example/*,,  ,https://b.example/*" );
        assertThat( list.matches( "https://a.example/foo" ) ).isTrue();
        assertThat( list.matches( "https://b.example/foo" ) ).isTrue();
        assertThat( list.matches( "https://c.example/foo" ) ).isFalse();
    }
}
