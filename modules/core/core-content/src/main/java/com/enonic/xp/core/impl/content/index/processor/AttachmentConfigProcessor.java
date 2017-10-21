package com.enonic.xp.core.impl.content.index.processor;

import com.google.common.base.Preconditions;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.schema.content.ContentTypeName;

import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT_TEXT_COMPONENT;

public class AttachmentConfigProcessor
    implements ContentIndexConfigProcessor
{
    private ContentTypeName contentTypeName;

    public AttachmentConfigProcessor( final Builder builder) {
        this.contentTypeName = builder.contentTypeName;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        if ( contentTypeName.isTextualMedia() )
        {
            builder.add( ATTACHMENT_TEXT_COMPONENT, IndexConfig.create().
                enabled( true ).
                fulltext( true ).
                includeInAllText( true ).
                nGram( true ).
                decideByType( false ).
                build() );
        }
        else
        {
            builder.add( ATTACHMENT_TEXT_COMPONENT, IndexConfig.create().
                enabled( true ).
                fulltext( true ).
                includeInAllText( false ).
                nGram( true ).
                decideByType( false ).
                build() );
        }

        return builder;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private ContentTypeName contentTypeName;

        public Builder contentTypeName( final ContentTypeName value )
        {
            this.contentTypeName = value;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( contentTypeName );
        }

        public AttachmentConfigProcessor build()
        {
            validate();
            return new AttachmentConfigProcessor( this );
        }
    }
}
