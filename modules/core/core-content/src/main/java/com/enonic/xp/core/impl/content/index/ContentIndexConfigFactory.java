package com.enonic.xp.core.impl.content.index;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.impl.content.index.processor.AttachmentConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.BaseConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.ContentIndexConfigProcessors;
import com.enonic.xp.core.impl.content.index.processor.DataConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

public class ContentIndexConfigFactory
{
    private final ContentIndexConfigProcessors indexConfigProcessors = new ContentIndexConfigProcessors();

    public ContentIndexConfigFactory( final Builder builder )
    {
        indexConfigProcessors.add( new BaseConfigProcessor() );

        indexConfigProcessors.add( new AttachmentConfigProcessor( builder.contentTypeName ) );

        indexConfigProcessors.add( new DataConfigProcessor( getDataForm( builder.contentTypeService, builder.contentTypeName ) ) );

        indexConfigProcessors.add( new PageConfigProcessor( getPageConfigForm( builder.pageDescriptorService, builder.descriptorKey ) ) );
    }

    private Form getDataForm( final ContentTypeService contentTypeService, final ContentTypeName contentTypeName )
    {
        if ( contentTypeName == null || contentTypeService == null )
        {
            return null;
        }
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ).getForm();
    }

    private Form getPageConfigForm( final PageDescriptorService pageDescriptorService, final DescriptorKey descriptorKey )
    {
        if ( pageDescriptorService == null || descriptorKey == null )
        {
            return null;
        }
        return pageDescriptorService.getByKey( descriptorKey ).getConfig();
    }

    public IndexConfigDocument produce()
    {
        final PatternIndexConfigDocument.Builder configDocumentBuilder = PatternIndexConfigDocument.create();

        indexConfigProcessors.forEach(
            contentIndexConfigProcessor -> contentIndexConfigProcessor.processDocument( configDocumentBuilder ) );

        return configDocumentBuilder.build();
    }

    public static Builder create()
    {
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
