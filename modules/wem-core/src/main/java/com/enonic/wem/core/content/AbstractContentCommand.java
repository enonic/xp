package com.enonic.wem.core.content;

import org.elasticsearch.common.Strings;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ValidateContentData;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

abstract class AbstractContentCommand
{
    private static final String NON_CONTENT_NODE_PREFIX = "__";

    final ContentNodeTranslator translator;

    final NodeService nodeService;

    final ContentTypeService contentTypeService;

    final BlobService blobService;

    final Context context;

    AbstractContentCommand( final Builder builder )
    {
        this.blobService = builder.blobService;
        this.contentTypeService = builder.contentTypeService;
        this.nodeService = builder.nodeService;
        this.context = builder.context;
        this.translator = builder.translator;
    }

    Nodes removeNonContentNodes( final Nodes nodes )
    {
        Nodes.Builder filtered = new Nodes.Builder();

        for ( final Node node : nodes )
        {
            if ( !Strings.startsWithIgnoreCase( node.name().toString(), AbstractContentCommand.NON_CONTENT_NODE_PREFIX ) )
            {
                filtered.add( node );
            }
        }

        return filtered.build();
    }

    Content getContent( final ContentId contentId )
    {
        return GetContentByIdCommand.create( contentId ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            context( this.context ).
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

        private Context context;

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

        @SuppressWarnings("unchecked")
        public B context( final Context context )
        {
            this.context = context;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( nodeService );
            Preconditions.checkNotNull( contentTypeService );
            Preconditions.checkNotNull( blobService );
            Preconditions.checkNotNull( context );
            Preconditions.checkNotNull( translator );
        }
    }

}
