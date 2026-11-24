package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NameFilterTest
{
    @Test
    void first_matching_pattern_decides()
    {
        assertThat( NameFilter.parse( "some.app.*, !some.app.name" ).accept( "some.app.name" ) ).isTrue();
        assertThat( NameFilter.parse( "!some.app.name, some.app.*" ).accept( "some.app.name" ) ).isFalse();
        assertThat( NameFilter.parse( "!some.app.name, some.app.*" ).accept( "some.app.other" ) ).isTrue();
    }

    @Test
    void unmatched_name_is_rejected()
    {
        assertThat( NameFilter.parse( "some.app.name" ).accept( "another.app.name" ) ).isFalse();
    }

    @Test
    void star_matches_every_name()
    {
        assertThat( NameFilter.parse( "*" ).accept( "any:thing" ) ).isTrue();
        assertThat( NameFilter.parse( "!*" ).accept( "any:thing" ) ).isFalse();
    }

    @Test
    void pattern_without_star_matches_exactly()
    {
        final NameFilter filter = NameFilter.parse( "media:image" );

        assertThat( filter.accept( "media:image" ) ).isTrue();
        assertThat( filter.accept( "media:images" ) ).isFalse();
        assertThat( filter.accept( "xmedia:image" ) ).isFalse();
    }

    @Test
    void regex_characters_are_literal()
    {
        assertThat( NameFilter.parse( "a.b" ).accept( "aXb" ) ).isFalse();
    }

    @Test
    void blank_entries_are_skipped()
    {
        final NameFilter filter = NameFilter.parse( " a , , !b* ," );

        assertThat( filter.getPatterns() ).containsExactly( "a", "b*" );
        assertThat( filter.isEmpty() ).isFalse();
        assertThat( NameFilter.parse( " , " ).isEmpty() ).isTrue();
    }
}
