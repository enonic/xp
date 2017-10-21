package com.enonic.xp.core.impl.content.index;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.impl.content.index.processor.AttachmentConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.BaseConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.ContentIndexConfigProcessors;
import com.enonic.xp.core.impl.content.index.processor.DataConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;

public class ContentIndexConfigFactory
{
    private final ContentIndexConfigProcessors indexConfigProcessors = new ContentIndexConfigProcessors();

    public ContentIndexConfigFactory( final Builder builder )
    {
        indexConfigProcessors.add( new BaseConfigProcessor() );

        if ( builder.contentTypeName != null )
        {
            indexConfigProcessors.
                add( DataConfigProcessor.create().
                    contentTypeName( builder.contentTypeName ).
                    contentTypeService( builder.contentTypeService ).
                    build() ).
                add( AttachmentConfigProcessor.create().
                    contentTypeName( builder.contentTypeName ).
                    build() );
        }

        if ( builder.descriptorKey != null && builder.pageDescriptorService != null)
        {
            indexConfigProcessors.add( PageConfigProcessor.create().
                pageDescriptorService( builder.pageDescriptorService ).
                descriptorKey( builder.descriptorKey ).
                build() );
        }
    }

    public IndexConfigDocument produce()
    {
        final PatternIndexConfigDocument.Builder configDocumentBuilder = PatternIndexConfigDocument.create();

        indexConfigProcessors.forEach(
            contentIndexConfigProcessor -> contentIndexConfigProcessor.processDocument( configDocumentBuilder ) );

        return configDocumentBuilder.build();
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder
    {
        private ContentTypeService contentTypeService;

        private PageDescriptorService pageDescriptorService;

        private ContentTypeName contentTypeName;

        private DescriptorKey descriptorKey;


        public Builder contentTypeService( final ContentTypeService value )
        {
            this.contentTypeService = value;
            return this;
        }

        public Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        public Builder contentTypeName( final ContentTypeName value )
        {
            this.contentTypeName = value;
            return this;
        }

        public Builder descriptorKey( final DescriptorKey value )
        {
            this.descriptorKey = value;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentTypeService );
        }

        public ContentIndexConfigFactory build()
        {
            validate();
            return new ContentIndexConfigFactory( this );
        }
    }
}
