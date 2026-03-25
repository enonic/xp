package com.enonic.xp.index;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class IndexConfigTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( IndexConfig.class ).withNonnullFields( "languages", "indexValueProcessors" ).verify();
    }

    @Test
    void unsupported_language_throws_at_node_creation()
    {
        assertThrows( IllegalArgumentException.class,
                      () -> IndexConfig.create().enabled( true ).addLanguage( Locale.forLanguageTag( "en_US" ) ).build() );
    }
}
