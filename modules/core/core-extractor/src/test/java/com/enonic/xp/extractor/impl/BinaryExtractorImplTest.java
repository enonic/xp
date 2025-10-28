package com.enonic.xp.extractor.impl;

import java.util.List;
import java.util.Map;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.parser.DefaultParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.extractor.impl.config.ExtractorConfig;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BinaryExtractorImplTest
{
    private BinaryExtractorImpl extractor;

    @BeforeEach
    void setup()
    {
        final ExtractorConfig extractorConfig = mock( ExtractorConfig.class );
        when( extractorConfig.body_size_limit() ).thenReturn( 200000 );
        this.extractor = new BinaryExtractorImpl( new DefaultDetector(), new DefaultParser() );
        this.extractor.activate( extractorConfig );
    }

    @Test
    void extract_image()
    {
        final ExtractedData extractedData = this.extractor.extract(
            Resources.asByteSource( BinaryExtractorImplTest.class.getResource( "Multiple-colorSpace-entries.jpg" ) ) );

        final Map<String, List<String>> metadata = extractedData.getMetadata();

        assertEquals( "image/jpeg", metadata.get( HttpHeaders.CONTENT_TYPE ).iterator().next() );
    }

    @Test
    @Disabled("Requires PDFBox Tika Parser in classpath. But tika-parsers 1.x heavily pollutes classpath with other jars")
    public void extract_pdf()
    {
        final ExtractedData extractedData =
            this.extractor.extract( Resources.asByteSource( BinaryExtractorImplTest.class.getResource( "sommerfest.pdf" ) ) );

        final Map<String, List<String>> metadata = extractedData.getMetadata();

        assertEquals( "application/pdf", metadata.get( HttpHeaders.CONTENT_TYPE ).iterator().next() );
        final String extractedText = extractedData.getText();
        assertFalse( isNullOrEmpty( extractedText ) );
        assertTrue( extractedText.contains( "Velkommen" ) );
    }

}
