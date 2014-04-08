package com.enonic.wem.core.content;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ValidateContentData;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

abstract class AbstractContentCommand<T extends AbstractContentCommand>
{
    public static final String NON_CONTENT_NODE_PREFIX = "__";

    private ContentNodeTranslator translator;

    NodeService nodeService;

    ContentTypeService contentTypeService;

    BlobService blobService;

    ContentNodeTranslator getTranslator()
    {
        if ( this.translator == null )
        {
            this.translator = new ContentNodeTranslator( blobService, contentTypeService );
        }

        return this.translator;
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
        return new GetContentByIdCommand().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            blobService( this.blobService ).
            contentId( contentId ).
            execute();
    }

    DataValidationErrors validate( final ContentTypeName contentType, final ContentData contentData )
    {
        final ValidateContentData data = new ValidateContentData().contentType( contentType ).contentData( contentData );

        return new ValidateContentDataCommand().contentTypeService( this.contentTypeService ).data( data ).execute();
    }

    T nodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
        return (T)this;
    }

    T contentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        return (T)this;
    }

    T blobService( final BlobService blobService )
    {
        this.blobService = blobService;
        return (T)this;
    }
}
