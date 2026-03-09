package com.enonic.xp.repo.impl.index;

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
            assertTrue( IndexLanguageController.isSupported( lang ), "Expected isSupported=true for: " + lang );
        }
    }

    @Test
    void isSupported_returns_false_for_unknown_languages()
    {
        assertFalse( IndexLanguageController.isSupported( "xyz" ) );
        assertFalse( IndexLanguageController.isSupported( "rr" ) );
        assertFalse( IndexLanguageController.isSupported( "" ) );
        assertFalse( IndexLanguageController.isSupported( null ) );
    }

    @Test
    void isSupported_is_case_insensitive()
    {
        assertTrue( IndexLanguageController.isSupported( "pt-BR" ) );
        assertTrue( IndexLanguageController.isSupported( "pt-br" ) );
        assertTrue( IndexLanguageController.isSupported( "EN" ) );
        assertTrue( IndexLanguageController.isSupported( "en" ) );
        assertTrue( IndexLanguageController.isSupported( "De" ) );
    }

    @Test
    void resolveAnalyzer_returns_correct_analyzer()
    {
        assertEquals( "english", IndexLanguageController.resolveAnalyzer( "en" ) );
        assertEquals( "german", IndexLanguageController.resolveAnalyzer( "de" ) );
        assertEquals( "norwegian", IndexLanguageController.resolveAnalyzer( "no" ) );
        assertEquals( "norwegian", IndexLanguageController.resolveAnalyzer( "nb" ) );
        assertEquals( "swedish", IndexLanguageController.resolveAnalyzer( "sv" ) );
    }

    @Test
    void resolveAnalyzer_normalizes_pt_BR()
    {
        assertEquals( "brazilian", IndexLanguageController.resolveAnalyzer( "pt-BR" ) );
        assertEquals( "brazilian", IndexLanguageController.resolveAnalyzer( "pt-br" ) );
    }

    @Test
    void resolveAnalyzer_returns_null_for_unknown()
    {
        assertNull( IndexLanguageController.resolveAnalyzer( "xyz" ) );
        assertNull( IndexLanguageController.resolveAnalyzer( null ) );
    }

    @Test
    void resolveStemmedIndexValueType_returns_value_type_for_known_language()
    {
        assertNotNull( IndexLanguageController.resolveStemmedIndexValueType( "en" ) );
        assertNotNull( IndexLanguageController.resolveStemmedIndexValueType( "pt-br" ) );
        assertNotNull( IndexLanguageController.resolveStemmedIndexValueType( "pt-BR" ) );
    }

    @Test
    void resolveStemmedIndexValueType_returns_null_for_unknown()
    {
        assertThrows( IllegalArgumentException.class, () -> IndexLanguageController.resolveStemmedIndexValueType( "xyz" ) );
        assertThrows( IllegalArgumentException.class, () -> IndexLanguageController.resolveStemmedIndexValueType( null ) );
    }
}
