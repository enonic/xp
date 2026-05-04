package com.enonic.xp.core.impl.content.index.processor;

import org.junit.jupiter.api.Test;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.schema.content.ContentTypeName;

import static com.enonic.xp.content.ContentPropertyNames.MEDIA_TEXT_COMPONENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AttachmentConfigProcessorTest
{
    @Test
    void test_textual_media()
    {
        final ContentTypeName contentTypeName = ContentTypeName.textMedia();
        final AttachmentConfigProcessor configProcessor = new AttachmentConfigProcessor( contentTypeName );

        final PatternIndexConfigDocument config = configProcessor.processDocument( PatternIndexConfigDocument.empty() );

        assertEquals( 1, config.getPathIndexConfigs().size() );
        assertEquals( IndexConfig.create().
            enabled( true ).
            fulltext( true ).
            includeInAllText( true ).
            nGram( true ).
            decideByType( false ).build(), config.getConfigForPath( IndexPath.from( MEDIA_TEXT_COMPONENT ) ) );

    }

    @Test
    void test_non_textual_media()
    {
        final ContentTypeName contentTypeName = ContentTypeName.folder();
        final AttachmentConfigProcessor configProcessor = new AttachmentConfigProcessor( contentTypeName );

        final PatternIndexConfigDocument config = configProcessor.processDocument( PatternIndexConfigDocument.empty() );

        assertEquals( 0, config.getPathIndexConfigs().size() );
    }
}
