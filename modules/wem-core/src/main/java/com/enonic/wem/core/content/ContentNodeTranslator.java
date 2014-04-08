package com.enonic.wem.core.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.google.common.io.InputSupplier;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.core.content.serializer.ThumbnailAttachmentSerializer;

public class ContentNodeTranslator
{
    private static final NodePath CONTENTS_ROOT_PATH = NodePath.newPath( "/content" ).build();

    public static final String THUMBNAIL_MIME_TYPE = "image/png";

    public static final String FORM_PATH = "form";

    private static final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();


    private final ContentDataSerializer CONTENT_SERIALIZER = new ContentDataSerializer();

    private ContentTypeService contentTypeService;

    private BlobService blobService;

    public ContentNodeTranslator( final BlobService blobService, final ContentTypeService contentTypeService )
    {
        this.blobService = blobService;
        this.contentTypeService = contentTypeService;
    }

    public CreateNodeParams toCreateNode( final CreateContentParams params )
    {
        final RootDataSet contentAsData = CONTENT_SERIALIZER.toData( params );

        final EntityIndexConfig entityIndexConfig = ContentEntityIndexConfigFactory.create();

        Attachments contentAttachments = params.getAttachments();

        final com.enonic.wem.api.entity.Attachments.Builder nodeAttachmentsBuilder = com.enonic.wem.api.entity.Attachments.builder().
            addAll( CONTENT_ATTACHMENT_NODE_TRANSLATOR.toNodeAttachments( contentAttachments ) );

        final Thumbnail thumbnail = resolveThumbnailAttachment( params );

        if ( thumbnail != null )
        {
            nodeAttachmentsBuilder.add( ThumbnailAttachmentSerializer.toAttachment( thumbnail ) );
        }

        return new CreateNodeParams().
            name( resolveNodeName( params.getName() ) ).
            parent( resolveParentNodePath( params.getParentContentPath() ) ).
            embed( params.isEmbed() ).
            data( contentAsData ).
            attachments( nodeAttachmentsBuilder.build() ).
            entityIndexConfig( entityIndexConfig );
    }

    public UpdateNodeParams toUpdateNodeCommand( final Content content, final Attachments attachments )
    {
        return new UpdateNodeParams().
            id( EntityId.from( content.getId() ) ).
            editor( toNodeEditor( content, attachments ) );
    }

    public Contents fromNodes( final Nodes nodes )
    {
        final Contents.Builder contents = Contents.builder();

        for ( final Node node : nodes )
        {
            contents.add( doGetFromNode( node ) );
        }

        return contents.build();
    }

    public Content fromNode( final Node node )
    {
        return doGetFromNode( node );
    }


    private Content doGetFromNode( final Node node )
    {
        final NodePath parentNodePath = node.path().getParentPath();
        final NodePath parentContentPathAsNodePath = parentNodePath.removeFromBeginning( CONTENTS_ROOT_PATH );
        final ContentPath parentContentPath = ContentPath.from( parentContentPathAsNodePath.toString() );

        final com.enonic.wem.api.entity.Attachments nodeAttachments = node.attachments();

        final com.enonic.wem.api.entity.Attachment thumbnailAttachment =
            nodeAttachments.getAttachment( ThumbnailAttachmentSerializer.THUMB_NAME );

        final Thumbnail thumbnail;

        if ( thumbnailAttachment != null )
        {
            thumbnail = ThumbnailAttachmentSerializer.toThumbnail( thumbnailAttachment );
        }
        else
        {
            thumbnail = null;
        }

        final Content.Builder builder = CONTENT_SERIALIZER.fromData( node.data() );
        builder.
            id( ContentId.from( node.id() ) ).
            parentPath( parentContentPath ).
            name( node.name().toString() ).
            createdTime( node.getCreatedTime() ).
            creator( node.getCreator() ).
            modifiedTime( node.getModifiedTime() ).
            modifier( node.getModifier() ).
            thumbnail( thumbnail );

        return builder.build();
    }

    private NodeEditor toNodeEditor( final Content content, final Attachments attachments )
    {
        final RootDataSet rootDataSet = CONTENT_SERIALIZER.toData( content );

        final EntityIndexConfig entityIndexConfig = ContentEntityIndexConfigFactory.create();

        return new NodeEditor()
        {
            @Override
            public Node.EditBuilder edit( final Node toBeEdited )
            {

                final com.enonic.wem.api.entity.Attachments contentAttachmentsAsNodeAttachments =
                    CONTENT_ATTACHMENT_NODE_TRANSLATOR.toNodeAttachments( attachments );

                final com.enonic.wem.api.entity.Attachments.Builder nodeAttachmentsBuilder =
                    com.enonic.wem.api.entity.Attachments.builder().
                        addAll( contentAttachmentsAsNodeAttachments );

                final com.enonic.wem.api.entity.Attachment thumbnailAttachment =
                    ThumbnailAttachmentSerializer.toAttachment( content.getThumbnail() );

                if ( thumbnailAttachment != null )
                {
                    nodeAttachmentsBuilder.add( thumbnailAttachment );
                }

                return Node.editNode( toBeEdited ).
                    name( NodeName.from( content.getName().toString() ) ).
                    attachments( nodeAttachmentsBuilder.build() ).
                    entityIndexConfig( entityIndexConfig ).
                    rootDataSet( rootDataSet );
            }
        };
    }

    private String resolveNodeName( final ContentName name )
    {
        if ( name instanceof ContentName.Unnamed )
        {
            ContentName.Unnamed unnammed = (ContentName.Unnamed) name;
            if ( !unnammed.hasUniqueness() )
            {
                return ContentName.Unnamed.withUniqueness().toString();
            }
        }
        return name.toString();
    }

    private NodePath resolveParentNodePath( final ContentPath parentContentPath )
    {
        return NodePath.newPath( CONTENTS_ROOT_PATH ).elements( parentContentPath.toString() ).build();
    }

    private Thumbnail resolveThumbnailAttachment( final CreateContentParams params )
    {
        final ContentType contentType = getContentType( params.getContentType() );
        if ( contentType.getSuperType() == null )
        {
            return null;
        }

        if ( contentType.getSuperType().isMedia() )
        {
            Attachment mediaAttachment = params.getAttachment( params.getName().toString() );
            if ( mediaAttachment == null )
            {
                mediaAttachment = params.getAttachments().first();
            }
            if ( mediaAttachment != null )
            {
                return createThumbnail( mediaAttachment );
            }
        }
        return null;
    }

    private Thumbnail createThumbnail( final Attachment origin )
    {
        final Blob originalImage = blobService.get( origin.getBlobKey() );
        final InputSupplier<ByteArrayInputStream> inputSupplier = ThumbnailFactory.resolve( originalImage );
        final Blob thumbnailBlob;
        try
        {
            thumbnailBlob = blobService.create( inputSupplier.getInput() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to create blob for thumbnail attachment: " + e.getMessage() );
        }
        return Thumbnail.from( thumbnailBlob.getKey(), THUMBNAIL_MIME_TYPE, thumbnailBlob.getLength() );
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );
    }
}
