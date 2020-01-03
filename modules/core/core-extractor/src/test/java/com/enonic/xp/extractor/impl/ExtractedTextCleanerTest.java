package com.enonic.xp.extractor.impl;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtractedTextCleanerTest
{
    @Test
    public void strip_consecutive_linebreaks_and_whitespaces()
        throws Exception
    {
        final String toBeCleaned;
        final String expected;
        try (final InputStream stream = this.getClass().getResourceAsStream( "linebreaked.txt" ))
        {
            toBeCleaned = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        try (final InputStream stream = this.getClass().getResourceAsStream( "linebreaked-clean.txt" ))
        {
            expected = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        final String cleanedText = ExtractedTextCleaner.clean( toBeCleaned );
        assertEquals( expected.trim(), cleanedText.trim() );
    }

    @Test
    public void strip_consecutive_linebreaks_and_whitespaces_2()
        throws Exception
    {
        final String toBeCleaned;
        final String expected;
        try (final InputStream stream = this.getClass().getResourceAsStream( "consecutive-linebreaks.txt" ))
        {
            toBeCleaned = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        try (final InputStream stream = this.getClass().getResourceAsStream( "consecutive-linebreaks-cleaned.txt" ))
        {
            expected = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }

        final String cleanedText = ExtractedTextCleaner.clean( toBeCleaned );

        assertEquals( expected, cleanedText );
    }

    @Test
    public void strip_control_characters()
        throws Exception
    {

        final String nastyString =
            "\u001A\u007f\u00c6\u00d8\u00c5\u00e6\u00f8\u00e5\u306d\u304e\u30de\u30e8\u713c\u304d\u0082\u0099\u009f\u001A";

        final String cleanedText = ExtractedTextCleaner.clean( nastyString );

        final String expected = "\u00c6\u00d8\u00c5\u00e6\u00f8\u00e5\u306d\u304e\u30de\u30e8\u713c\u304d";

        assertEquals( expected, cleanedText );
    }

}
