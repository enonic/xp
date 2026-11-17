package com.enonic.xp.core.impl.schema;

import java.io.UncheckedIOException;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKeys;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@NullMarked
class YmlParserBaseTest
{
    @Test
    void duplicateKeysAllowedByDefault()
    {
        final var parser = new YmlParserBase();

        final Object result = parser.parse( "name: first\nname: second\n", Object.class, ApplicationKey.from( "test" ) );

        assertThat( result ).isEqualTo( Map.of( "name", "second" ) );
    }

    @Test
    void trailingDocumentsAllowedByDefault()
    {
        final var parser = new YmlParserBase();

        final Object result = parser.parse( "name: first\n---\nname: second\n", Object.class, ApplicationKey.from( "test" ) );

        assertThat( result ).isEqualTo( Map.of( "name", "first" ) );
    }

    @Test
    void strictParsingRejectsDuplicateKeys()
    {
        final var parser = new YmlParserBase();
        parser.enableStrictParsing();

        assertThatThrownBy( () -> parser.parse( "name: first\nname: second\n", Object.class, ApplicationKey.from( "test" ) ) )
            .isInstanceOf( UncheckedIOException.class );
    }

    @Test
    void strictParsingRejectsTrailingDocuments()
    {
        final var parser = new YmlParserBase();
        parser.enableStrictParsing();

        assertThatThrownBy( () -> parser.parse( "name: first\n---\nname: second\n", Object.class, ApplicationKey.from( "test" ) ) )
            .isInstanceOf( UncheckedIOException.class );
    }

    @Test
    void strictParsingReadsNestedPrincipalsBeforeOtherFields()
    {
        final var parser = new YmlParserBase();
        parser.enableStrictParsing();

        final var result = parser.parse( "allow: ['role:system.admin']\nname: example\n", Group.class,
                                         ApplicationKey.from( "test" ) );

        assertThat( result.allow() ).isEqualTo( PrincipalKeys.from( "role:system.admin" ) );
        assertThat( result.name() ).isEqualTo( "example" );
    }

    @Test
    void strictParsingPreservesNulls()
    {
        final var parser = new YmlParserBase();
        parser.enableStrictParsing();

        final Object result = parser.parse( "name: test\noptions:\n  value: null\n", Object.class, ApplicationKey.from( "test" ) );

        assertThat( result ).isEqualTo( Map.of( "name", "test", "options", singletonMap( "value", null ) ) );
    }

    public record Group(PrincipalKeys allow, String name)
    {
    }
}
