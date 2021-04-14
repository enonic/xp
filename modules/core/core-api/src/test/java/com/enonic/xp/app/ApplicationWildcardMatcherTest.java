package com.enonic.xp.app;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationWildcardMatcherTest
{
    @Test
    void matches()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );

        final ApplicationWildcardMatcher<Object> wildcardMatcher = new ApplicationWildcardMatcher<>( applicationKey, Object::toString );
        assertTrue( wildcardMatcher.matches( "${app}:*", "my.app:folder" ) );
        assertFalse( wildcardMatcher.matches( "${app}:*", "base:folder" ) );
    }

    @Test
    void resolveWildcards_exact()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );
        final ApplicationWildcardMatcher<Object> wildcardMatcher = new ApplicationWildcardMatcher<>( applicationKey, Object::toString );

        final Set<String> result = List.of( "base:folder", "my.app:folder", "my.app:quote", "my.other.app:quote" )
            .stream()
            .filter( wildcardMatcher.createPredicate( "base:folder" ) )
            .collect( Collectors.toCollection( LinkedHashSet::new ) );
        assertThat( result ).containsExactly( "base:folder" );
    }

    @Test
    void resolveWildcards_exact_from_my_app()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );
        final ApplicationWildcardMatcher<Object> wildcardMatcher = new ApplicationWildcardMatcher<>( applicationKey, Object::toString );

        final Set<String> result = List.of( "base:folder", "my.app:folder", "my.other.app:quote" )
            .stream()
            .filter( wildcardMatcher.createPredicate( "${app}:folder" ) )
            .collect( Collectors.toCollection( LinkedHashSet::new ) );
        assertThat( result ).containsExactly( "my.app:folder" );
    }

    @Test
    void resolveWildcards_all()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );
        final ApplicationWildcardMatcher<Object> wildcardMatcher = new ApplicationWildcardMatcher<>( applicationKey, Object::toString );

        final Set<String> result = List.of( "base:folder", "my.app:folder", "my.app:quote", "my.other.app:quote" )
            .stream()
            .filter( wildcardMatcher.createPredicate( "*" ) )
            .collect( Collectors.toCollection( LinkedHashSet::new ) );
        assertThat( result ).containsExactly( "base:folder", "my.app:folder", "my.app:quote", "my.other.app:quote" );
    }

    @Test
    void resolveWildcards_from_any_app()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );
        final ApplicationWildcardMatcher<Object> wildcardMatcher = new ApplicationWildcardMatcher<>( applicationKey, Object::toString );

        final Set<String> result = List.of( "base:folder", "my.app:folder", "my.app:quote", "my.other.app:quote" )
            .stream()
            .filter( wildcardMatcher.createPredicate( "*:quote" ) )
            .collect( Collectors.toCollection( LinkedHashSet::new ) );
        assertThat( result ).containsExactly( "my.app:quote", "my.other.app:quote" );
    }

    @Test
    void resolveWildcards_from_my_app()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );
        final ApplicationWildcardMatcher<Object> wildcardMatcher = new ApplicationWildcardMatcher<>( applicationKey, Object::toString );

        final Set<String> result = List.of( "base:folder", "my.app:folder", "my.app:quote", "my.other.app:quote" )
            .stream()
            .filter( wildcardMatcher.createPredicate( "${app}:*" ) )
            .collect( Collectors.toCollection( LinkedHashSet::new ) );
        assertThat( result ).containsExactly( "my.app:folder", "my.app:quote" );
    }

    @Test
    void resolveWildcards_except()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );
        final ApplicationWildcardMatcher<Object> wildcardMatcher = new ApplicationWildcardMatcher<>( applicationKey, Object::toString );

        final Set<String> result = List.of( "base:folder", "my.app:folder", "my.app:quote", "my.other.app:quote" )
            .stream()
            .filter( wildcardMatcher.createPredicate( "^(?!base:folder$).*" ) )
            .collect( Collectors.toCollection( LinkedHashSet::new ) );
        assertThat( result ).containsExactly( "my.app:folder", "my.app:quote", "my.other.app:quote" );
    }

    @Test
    void resolveWildcards_startsWith()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );
        final ApplicationWildcardMatcher<Object> wildcardMatcher = new ApplicationWildcardMatcher<>( applicationKey, Object::toString );

        final Set<String> result = List.of( "base:folder", "my.app:folder", "my.app:quote", "my.other.app:quote" )
            .stream()
            .filter( wildcardMatcher.createPredicate( "*:q*" ) )
            .collect( Collectors.toCollection( LinkedHashSet::new ) );
        assertThat( result ).containsExactly( "my.app:quote", "my.other.app:quote" );
    }
}
