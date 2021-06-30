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
        assertTrue( wildcardMatcher.matches( "folder", "my.app:folder" ) );
        assertTrue( wildcardMatcher.matches( "${app}:folder", "my.app:folder" ) );
        assertTrue( wildcardMatcher.matches( "my.app:folder", "my.app:folder" ) );
        assertTrue( wildcardMatcher.matches( "${app}:*", "my.app:folder" ) );

        assertFalse( wildcardMatcher.matches( "fold", "my.app:folder" ) );
        assertFalse( wildcardMatcher.matches( "folder", "not.my.app:folder" ) );
        assertFalse( wildcardMatcher.matches( "${app}:*", "mytapp:folder" ) );
        assertFalse( wildcardMatcher.matches( "${app}:*", "base:folder" ) );
    }

    @Test
    void matches_multiple()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );

        final ApplicationWildcardMatcher<Object> wildcardMatcher = new ApplicationWildcardMatcher<>( applicationKey, Object::toString );

        final Set<String> result =
            List.of( "my.app:typeA", "my.app:typeB", "my.app:typeC", "my.app:typeD", "my.app:typeE", "my.apptypeA", "not.my.app:typeA",
                     "${app}:typeA", "{app}:typeA" )
                .stream()
                .filter( wildcardMatcher.createPredicate( "${app}:typeA|my.app:typeB|${app}:type(C|D)" ) )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );
        assertThat( result ).containsExactly( "my.app:typeA", "my.app:typeB", "my.app:typeC", "my.app:typeD" );
    }

    @Test
    void matches_multiple_wildcards()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );

        final ApplicationWildcardMatcher<Object> wildcardMatcher = new ApplicationWildcardMatcher<>( applicationKey, Object::toString );

        final Set<String> result =
            List.of( "a.b:type", "a.b:sypertype", "my.app:types", "any.app:class", "any.app:class1", "my.app:method", "my.app:method1",
                     "not.my.app:method" )
                .stream()
                .filter( wildcardMatcher.createPredicate( "*type*|*:class|my.app:method*" ) )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );
        assertThat( result ).containsExactly( "a.b:type", "a.b:sypertype", "my.app:types", "any.app:class", "my.app:method",
                                              "my.app:method1" );
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

    @Test
    void matches_legacy()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );

        final ApplicationWildcardMatcher<Object> wildcardMatcher =
            new ApplicationWildcardMatcher<>( applicationKey, Object::toString, ApplicationWildcardMatcher.Mode.LEGACY );
            assertTrue( wildcardMatcher.matches( "${app}:", "my.app:folder" ) );
        assertTrue( wildcardMatcher.matches( "folder", "not.my.app:folder" ) );
        assertTrue( wildcardMatcher.matches( "fold", "not.my.app:folder" ) );
        assertTrue( wildcardMatcher.matches( "${app}:*", "not.my.app:folder" ) );

        assertFalse( wildcardMatcher.matches( "${app}:*", "mytapp:folder" ) );
        assertFalse( wildcardMatcher.matches( "${app}:*", "base:folder" ) );
    }

    @Test
    void matches_multiple_legacy()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );

        final ApplicationWildcardMatcher<Object> wildcardMatcher =
            new ApplicationWildcardMatcher<>( applicationKey, Object::toString, ApplicationWildcardMatcher.Mode.LEGACY );

        final Set<String> result =
            List.of( "my.app:typeA", "my.app:typeB", "my.app:typeC", "my.app:typeD", "my.app:typeE", "my.apptypeA", "not.my.app:typeA",
                     "${app}:typeA", "{app}:typeA", "not.my.app:typeABC" )
                .stream()
                .filter( wildcardMatcher.createPredicate( "${app}:typeA|my.app:typeB|${app}:type(C|D)" ) )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );
        assertThat( result ).containsExactly( "my.app:typeA", "my.app:typeB", "my.app:typeC", "my.app:typeD", "not.my.app:typeA",
                                              "not.my.app:typeABC" );
    }

    @Test
    void matches_multiple_wildcards_legacy()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "my.app" );

        final ApplicationWildcardMatcher<Object> wildcardMatcher =
            new ApplicationWildcardMatcher<>( applicationKey, Object::toString, ApplicationWildcardMatcher.Mode.LEGACY );

        final Set<String> result =
            List.of( "a.b:type", "a.b:sypertype", "my.app:types", "any.app:class", "any.app:class1", "my.app:method", "my.app:method1",
                     "not.my.app:method" )
                .stream()
                .filter( wildcardMatcher.createPredicate( "*type*|*:class|my.app:method*" ) )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );

        assertThat( result ).containsExactly( "a.b:type", "a.b:sypertype", "my.app:types", "any.app:class", "any.app:class1",
                                              "my.app:method", "my.app:method1", "not.my.app:method" );
    }
}
