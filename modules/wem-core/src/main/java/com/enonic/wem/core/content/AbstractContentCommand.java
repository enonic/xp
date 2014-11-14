package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ValidateContentData;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.repo.NodeService;

abstract class AbstractContentCommand
{
    final ContentNodeTranslator translator;

    final NodeService nodeService;

    final ContentTypeService contentTypeService;

    final BlobService blobService;

    AbstractContentCommand( final Builder builder )
    {
        this.blobService = builder.blobService;
        this.contentTypeService = builder.contentTypeService;
        this.nodeService = builder.nodeService;
        this.translator = builder.translator;
    }

    Content getContent( final ContentId contentId )
    {
        return GetContentByIdCommand.create( contentId ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            translator( this.translator ).
            build().
            execute();
    }

    DataValidationErrors validate( final ContentTypeName contentType, final ContentData contentData )
    {
        final ValidateContentData data = new ValidateContentData().contentType( contentType ).contentData( contentData );

        return new ValidateContentDataCommand().contentTypeService( this.contentTypeService ).data( data ).execute();
    }

    public static class Builder<B extends Builder>
    {
        private NodeService nodeService;

        private ContentTypeService contentTypeService;

        private BlobService blobService;

        private ContentNodeTranslator translator;

        @SuppressWarnings("unchecked")
        public B nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B translator( final ContentNodeTranslator translator )
        {
            this.translator = translator;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B contentTypeService( final ContentTypeService contentTypeService )
        {
            this.contentTypeService = contentTypeService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B blobService( final BlobService blobService )
        {
            this.blobService = blobService;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( nodeService );
            Preconditions.checkNotNull( contentTypeService );
            Preconditions.checkNotNull( blobService );
            Preconditions.checkNotNull( translator );
        }
    }

}
