package com.enonic.xp.core.impl.content.index.processor;

import org.junit.jupiter.api.Test;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.schema.content.ContentTypeName;

import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT_TEXT_COMPONENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AttachmentConfigProcessorTest
{
    private ContentTypeName contentTypeName;

    private AttachmentConfigProcessor configProcessor;

    @Test
    void test_textual_media()
    {
        this.contentTypeName = ContentTypeName.textMedia();
        this.configProcessor = new AttachmentConfigProcessor( contentTypeName );

        final PatternIndexConfigDocument config = configProcessor.processDocument( PatternIndexConfigDocument.empty() );

        assertEquals( 1, config.getPathIndexConfigs().size() );
        assertEquals( IndexConfig.create().
            enabled( true ).
            fulltext( true ).
            includeInAllText( true ).
            nGram( true ).
            decideByType( false ).build(), config.getConfigForPath( IndexPath.from( ATTACHMENT_TEXT_COMPONENT ) ) );

    }

    @Test
    void test_non_textual_media()
    {
        this.contentTypeName = ContentTypeName.folder();
        this.configProcessor = new AttachmentConfigProcessor( contentTypeName );

        final PatternIndexConfigDocument config = configProcessor.processDocument( PatternIndexConfigDocument.empty() );

        assertEquals( 1, config.getPathIndexConfigs().size() );
        assertEquals( IndexConfig.create().
            enabled( true ).
            fulltext( true ).
            includeInAllText( false ).
            nGram( true ).
            decideByType( false ).build(), config.getConfigForPath( IndexPath.from( ATTACHMENT_TEXT_COMPONENT ) ) );

    }
}
