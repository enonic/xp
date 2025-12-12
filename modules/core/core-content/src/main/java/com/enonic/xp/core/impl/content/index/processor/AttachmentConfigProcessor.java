package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.schema.content.ContentTypeName;

import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT_TEXT_COMPONENT;

public class AttachmentConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final ContentTypeName contentTypeName;

    public AttachmentConfigProcessor( final ContentTypeName contentTypeName) {
        this.contentTypeName = contentTypeName;
    }

    @Override
    public PatternIndexConfigDocument processDocument( final PatternIndexConfigDocument builder )
    {
        final PatternIndexConfigDocument.Builder configBuilder = PatternIndexConfigDocument.create( builder );

        if ( contentTypeName.isTextualMedia() )
        {
            configBuilder.add( ATTACHMENT_TEXT_COMPONENT, IndexConfig.create().
                enabled( true ).
                fulltext( true ).
                includeInAllText( true ).
                nGram( true ).
                decideByType( false ).
                build() );
        }
        else
        {
            configBuilder.add( ATTACHMENT_TEXT_COMPONENT, IndexConfig.create().
                enabled( true ).
                fulltext( true ).
                includeInAllText( false ).
                nGram( true ).
                decideByType( false ).
                build() );
        }

        return configBuilder.build();
    }
}
