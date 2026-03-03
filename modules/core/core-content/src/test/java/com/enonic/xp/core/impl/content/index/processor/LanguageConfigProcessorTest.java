package com.enonic.xp.core.impl.content.index.processor;

import org.junit.jupiter.api.Test;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.content.ContentPropertyNames.DISPLAY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LanguageConfigProcessorTest
{
    @Test
    public void test_language_set_adds_display_name_config()
    {
        final PatternIndexConfigDocument result =
            new LanguageConfigProcessor( "en" ).processDocument( PatternIndexConfigDocument.create().build() );

        final IndexConfig config = result.getConfigForPath( IndexPath.from( DISPLAY_NAME ) );

        assertTrue( config.getLanguages().contains( "en" ) );
    }

    @Test
    public void test_language_set_adds_alltext_language()
    {
        final PatternIndexConfigDocument result =
            new LanguageConfigProcessor( "en" ).processDocument( PatternIndexConfigDocument.create().build() );

        assertTrue( result.getAllTextConfig().getLanguages().contains( "en" ) );
    }

    @Test
    public void test_null_language_skips_display_name_config()
    {
        final PatternIndexConfigDocument input = PatternIndexConfigDocument.create().build();
        final PatternIndexConfigDocument result = new LanguageConfigProcessor( null ).processDocument( input );

        final IndexConfig config = result.getConfigForPath( IndexPath.from( DISPLAY_NAME ) );

        assertEquals( result.getDefaultConfig(), config );
    }

    @Test
    public void test_blank_language_skips_display_name_config()
    {
        final PatternIndexConfigDocument input = PatternIndexConfigDocument.create().build();
        final PatternIndexConfigDocument result = new LanguageConfigProcessor( "" ).processDocument( input );

        final IndexConfig config = result.getConfigForPath( IndexPath.from( DISPLAY_NAME ) );

        assertEquals( result.getDefaultConfig(), config );
        assertTrue( result.getAllTextConfig().getLanguages().isEmpty() );
    }
}
