package com.enonic.xp.extractor.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExtractedTextCleanerTest
{
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