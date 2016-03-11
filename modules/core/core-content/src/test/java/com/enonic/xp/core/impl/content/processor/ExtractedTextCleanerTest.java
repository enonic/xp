package com.enonic.xp.core.impl.content.processor;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.*;

public class ExtractedTextCleanerTest
{

    @Test
    public void strip_consecutive_linebreaks_and_whitespaces()
        throws Exception
    {
        final String toBeCleaned = Resources.toString( this.getClass().getResource( "linebreaked.txt" ), Charsets.UTF_8 );

        final String cleanedText = ExtractedTextCleaner.clean( toBeCleaned );

        final String expected = Resources.toString( this.getClass().getResource( "linebreaked-clean.txt" ), Charsets.UTF_8 );

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