package com.enonic.xp.repo.impl.index;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IndexLanguageControllerTest
{
    @Test
    void isSupported_returns_true_for_all_known_languages()
    {
        final String[] knownLanguages =
            {"ar", "hy", "eu", "bn", "pt-br", "bg", "ca", "zh", "ja", "ko", "cs", "da", "nl", "en", "fi", "fr", "gl", "de", "el", "hi",
                "hu", "id", "ga", "it", "lv", "lt", "no", "nb", "nn", "fa", "pt", "ro", "ru", "ku", "es", "sv", "tr", "th"};
        for ( final String lang : knownLanguages )
        {
            assertTrue( IndexLanguageController.isSupported( Locale.forLanguageTag(lang) ), "Expected isSupported=true for: " + lang );
        }
    }

    @Test
    void isSupported_returns_false_for_unknown_languages()
    {
        assertFalse( IndexLanguageController.isSupported( Locale.forLanguageTag("xyz") ) );
        assertFalse( IndexLanguageController.isSupported( Locale.forLanguageTag("rr") ) );
        assertFalse( IndexLanguageController.isSupported( Locale.forLanguageTag("") ) );
        assertFalse( IndexLanguageController.isSupported( null ) );
    }

    @Test
    void isSupported_is_case_insensitive()
    {
        assertTrue( IndexLanguageController.isSupported( Locale.forLanguageTag( "pt-BR" ) ) );
        assertTrue( IndexLanguageController.isSupported( Locale.forLanguageTag( "pt-br" ) ) );
        assertTrue( IndexLanguageController.isSupported( Locale.forLanguageTag( "EN" ) ) );
        assertTrue( IndexLanguageController.isSupported( Locale.forLanguageTag( "en" ) ) );
        assertTrue( IndexLanguageController.isSupported( Locale.forLanguageTag( "De" ) ) );
    }

    @Test
    void resolveAnalyzer_returns_correct_analyzer()
    {
        assertEquals( "english", IndexLanguageController.resolveAnalyzer( Locale.forLanguageTag( "en" ) ) );
        assertEquals( "german", IndexLanguageController.resolveAnalyzer( Locale.forLanguageTag( "de" ) ) );
        assertEquals( "norwegian", IndexLanguageController.resolveAnalyzer( Locale.forLanguageTag( "no" ) ) );
        assertEquals( "norwegian", IndexLanguageController.resolveAnalyzer( Locale.forLanguageTag( "nb" ) ) );
        assertEquals( "swedish", IndexLanguageController.resolveAnalyzer( Locale.forLanguageTag( "sv" ) ) );
    }

    @Test
    void resolveAnalyzer_normalizes_pt_BR()
    {
        assertEquals( "brazilian", IndexLanguageController.resolveAnalyzer( Locale.forLanguageTag( "pt-BR" ) ) );
        assertEquals( "brazilian", IndexLanguageController.resolveAnalyzer( Locale.forLanguageTag( "pt-br" ) ) );
    }

    @Test
    void resolveAnalyzer_returns_null_for_unknown()
    {
        assertNull( IndexLanguageController.resolveAnalyzer( Locale.forLanguageTag( "xyz" ) ) );
        assertNull( IndexLanguageController.resolveAnalyzer( null ) );
    }

    @Test
    void resolveStemmedIndexValueType_returns_value_type_for_known_language()
    {
        assertNotNull( IndexLanguageController.resolveStemmedIndexValueType( Locale.forLanguageTag( "en" ) ) );
        assertNotNull( IndexLanguageController.resolveStemmedIndexValueType( Locale.forLanguageTag( "pt-br" ) ) );
        assertNotNull( IndexLanguageController.resolveStemmedIndexValueType( Locale.forLanguageTag( "pt-BR" ) ) );
    }

    @Test
    void resolveStemmedIndexValueType_returns_null_for_unknown()
    {
        assertThrows( IllegalArgumentException.class,
                      () -> IndexLanguageController.resolveStemmedIndexValueType( Locale.forLanguageTag( "xyz" ) ) );
        assertThrows( IllegalArgumentException.class, () -> IndexLanguageController.resolveStemmedIndexValueType( null ) );
    }
}
