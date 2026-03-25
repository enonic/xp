package com.enonic.xp.index;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertThrows;


class AllTextIndexConfigTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( AllTextIndexConfig.class ).withNonnullFields( "languages" ).verify();
    }

    @Test
    void unsupported_language_throws_at_node_creation()
    {
        assertThrows( IllegalArgumentException.class,
                      () -> AllTextIndexConfig.create().addLanguage( Locale.forLanguageTag( "en_US" ) ).build() );
    }
}
