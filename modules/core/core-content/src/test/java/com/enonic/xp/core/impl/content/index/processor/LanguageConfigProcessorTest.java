package com.enonic.xp.core.impl.content.index.processor;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.content.ContentPropertyNames.DISPLAY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LanguageConfigProcessorTest
{
    @Test
    public void test_language_set_adds_display_name_config()
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();

        new LanguageConfigProcessor( "en" ).processDocument( builder );

        final IndexConfig config = builder.build().getConfigForPath( PropertyPath.from( DISPLAY_NAME ) );

        assertTrue( config.getLanguages().contains( "en" ) );
    }

    @Test
    public void test_language_set_adds_alltext_language()
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();

        new LanguageConfigProcessor( "en" ).processDocument( builder );

        assertTrue( builder.build().getAllTextConfig().getLanguages().contains( "en" ) );
    }

    @Test
    public void test_null_language_skips_display_name_config()
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();

        new LanguageConfigProcessor( null ).processDocument( builder );

        final IndexConfig config = builder.build().getConfigForPath( PropertyPath.from( DISPLAY_NAME ) );

        assertEquals( builder.build().getDefaultConfig(), config );
    }

    @Test
    public void test_blank_language_skips_display_name_config()
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();

        new LanguageConfigProcessor( "" ).processDocument( builder );

        final IndexConfig config = builder.build().getConfigForPath( PropertyPath.from( DISPLAY_NAME ) );

        assertEquals( builder.build().getDefaultConfig(), config );
        assertTrue( builder.build().getAllTextConfig().getLanguages().isEmpty() );
    }
}
