package com.enonic.xp.core.impl.content.index.processor;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static com.enonic.xp.content.ContentPropertyNames.DATA;

public class DataConfigProcessor
    implements ContentIndexConfigProcessor
{
    private ContentTypeService contentTypeService;

    private ContentTypeName contentTypeName;

    public DataConfigProcessor( final Builder builder) {
        this.contentTypeService = builder.contentTypeService;
        this.contentTypeName = builder.contentTypeName;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        builder.add( DATA, IndexConfig.BY_TYPE );

        final Form dataForm = getForm( contentTypeService, contentTypeName );

        if(dataForm != null && dataForm.getFormItems().size() > 0)
        {
            final IndexConfigVisitor indexConfigVisitor = new IndexConfigVisitor( DATA, builder );
            indexConfigVisitor.traverse( dataForm );
        }

        return builder;
    }

    private Form getForm( final ContentTypeService contentTypeService, final ContentTypeName contentTypeName )
    {
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ).getForm();
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private ContentTypeService contentTypeService;

        private ContentTypeName contentTypeName;

        public Builder contentTypeService( final ContentTypeService value )
        {
            this.contentTypeService = value;
            return this;
        }

        public Builder contentTypeName( final ContentTypeName value )
        {
            this.contentTypeName = value;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( contentTypeService );
            Preconditions.checkNotNull( contentTypeName );
        }

        public DataConfigProcessor build()
        {
            validate();
            return new DataConfigProcessor( this );
        }
    }
}
