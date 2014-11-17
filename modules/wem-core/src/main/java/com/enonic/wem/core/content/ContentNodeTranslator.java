package com.enonic.wem.core.content;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.Name;
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
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.core.content.serializer.ThumbnailAttachmentSerializer;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeEditor;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.UpdateNodeParams;

public class ContentNodeTranslator
{
    private ContentTypeService contentTypeService;

    private BlobService blobService;

    private static final NodePath CONTENTS_ROOT_PATH = NodePath.newPath( "/content" ).build();

    private static final String THUMBNAIL_MIME_TYPE = "image/png";

    private static final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    private final ContentDataSerializer CONTENT_SERIALIZER = new ContentDataSerializer();

    public CreateNodeParams toCreateNode( final CreateContentParams params )
    {
        if ( params.getName() == null || StringUtils.isEmpty( params.getName().toString() ) )
        {
            params.name( Name.ensureValidName( params.getDisplayName() ) );
        }
        final RootDataSet contentAsData = CONTENT_SERIALIZER.toData( params );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create();

        Attachments contentAttachments = params.getAttachments();

        final com.enonic.wem.api.node.Attachments.Builder nodeAttachmentsBuilder = com.enonic.wem.api.node.Attachments.builder().
            addAll( CONTENT_ATTACHMENT_NODE_TRANSLATOR.toNodeAttachments( contentAttachments ) );

        final Thumbnail thumbnail = resolveThumbnailAttachment( params );

        if ( thumbnail != null )
        {
            nodeAttachmentsBuilder.add( ThumbnailAttachmentSerializer.toAttachment( thumbnail ) );
        }

        return CreateNodeParams.create().
            name( resolveNodeName( params.getName() ) ).
            parent( resolveParentNodePath( params.getParentContentPath() ) ).
            data( contentAsData ).
            attachments( nodeAttachmentsBuilder.build() ).
            indexConfigDocument( indexConfigDocument ).
            build();
    }

    public UpdateNodeParams toUpdateNodeCommand( final Content content, final Attachments attachments )
    {
        return new UpdateNodeParams().
            id( NodeId.from( content.getId() ) ).
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

        final com.enonic.wem.api.node.Attachments nodeAttachments = node.attachments();

        final com.enonic.wem.api.node.Attachment thumbnailAttachment =
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
            id( ContentId.from( node.id().toString() ) ).
            parentPath( parentContentPath ).
            name( node.name().toString() ).
            createdTime( node.getCreatedTime() ).
            creator( node.getCreator() ).
            modifiedTime( node.getModifiedTime() ).
            modifier( node.getModifier() ).
            hasChildren( node.getHasChildren() ).
            childOrder( node.getChildOrder() ).
            accessControlList( node.getAccessControlList() ).
            effectiveAccessControlList( node.getEffectiveAccessControlList() ).
            thumbnail( thumbnail );

        return builder.build();
    }

    private NodeEditor toNodeEditor( final Content content, final Attachments attachments )
    {
        final RootDataSet rootDataSet = CONTENT_SERIALIZER.toData( content );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create();

        return toBeEdited -> {

            final com.enonic.wem.api.node.Attachments contentAttachmentsAsNodeAttachments =
                CONTENT_ATTACHMENT_NODE_TRANSLATOR.toNodeAttachments( attachments );

            final com.enonic.wem.api.node.Attachments.Builder nodeAttachmentsBuilder = com.enonic.wem.api.node.Attachments.builder().
                addAll( contentAttachmentsAsNodeAttachments );

            final com.enonic.wem.api.node.Attachment thumbnailAttachment =
                ThumbnailAttachmentSerializer.toAttachment( content.getThumbnail() );

            if ( thumbnailAttachment != null )
            {
                nodeAttachmentsBuilder.add( thumbnailAttachment );
            }

            return Node.editNode( toBeEdited ).
                name( NodeName.from( content.getName().toString() ) ).
                attachments( nodeAttachmentsBuilder.build() ).
                indexConfigDocument( indexConfigDocument ).
                rootDataSet( rootDataSet ).
                accessControlList( content.getAccessControlList() ).
                effectiveAcl( content.getEffectiveAccessControlList() );
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
        final ByteSource source = ThumbnailFactory.resolve( originalImage );
        final Blob thumbnailBlob;
        try
        {
            thumbnailBlob = blobService.create( source.openStream() );
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

    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public void setBlobService( final BlobService blobService )
    {
        this.blobService = blobService;
    }
}
