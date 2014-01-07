package com.enonic.wem.core.content;

import java.util.Map;

import javax.inject.Inject;
import javax.jcr.Session;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.DeleteNodeByPathService;
import com.enonic.wem.core.entity.UpdateNodeHandler;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.relationship.RelationshipService;
import com.enonic.wem.core.relationship.SyncRelationshipsCommand;

import static com.enonic.wem.api.content.Content.newContent;

public class UpdateContentHandler
    extends CommandHandler<UpdateContent>
{
    private static final int THUMBNAIL_SIZE = 512;

    private static final String THUMBNAIL_MIME_TYPE = "image/png";

    private RelationshipService relationshipService;

    private IndexService indexService;

    private final static Logger LOG = LoggerFactory.getLogger( UpdateContentHandler.class );


    @Override
    public void handle()
        throws Exception
    {
        command.setResult( updateContentAsNode() );
    }

    private Content updateContentAsNode()
    {
        ContentNodeTranslator translator = new ContentNodeTranslator( this.context.getClient() );

        final GetContentById getContentByIdCommand = new GetContentById( command.getContentId() );
        final Content persistedContent = new GetContentByIdService( this.context, getContentByIdCommand ).execute();

        Content.EditBuilder editBuilder = command.getEditor().edit( persistedContent );

        if ( !editBuilder.isChanges() )
        {
            return persistedContent;
        }

        final Content tempEditedContent = editBuilder.build();

        // TODO: Fix this
        //validateEditedContent( persistedContent, tempEditedContent );

        final Content editedContent = newContent( tempEditedContent ).
            modifiedTime( DateTime.now() ).
            modifier( command.getModifier() ).build();

        // TODO: Rewrite to use service instead?
        final NodeEditor nodeEditor = translator.toNodeEditor( editedContent, this.command );
        final UpdateNode updateNodeCommand = translator.toUpdateNodeCommand( persistedContent.getId(), nodeEditor );

        final UpdateNodeHandler updateNodeHandler = UpdateNodeHandler.create().
            context( this.context ).
            command( updateNodeCommand ).
            indexService( this.indexService ).
            build();
        try
        {
            updateNodeHandler.handle();

            final Node editedNode = updateNodeCommand.getResult().getPersistedNode();
            final Content editedNodeAsContent = translator.fromNode( editedNode );

            deleteRemovedEmbeddedContentAsNode( persistedContent, editedNodeAsContent );

            return editedNodeAsContent;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to update content as node", e );
        }
    }

    private void deleteRemovedEmbeddedContentAsNode( final Content persistedContent, final Content editedContent )
        throws Exception
    {
        final Map<ContentId, Content> embeddedContentsBeforeEdit = resolveEmbeddedContent( persistedContent.getContentData() );

        final Map<ContentId, Content> embeddedContentsToKeep = resolveEmbeddedContent( editedContent.getContentData() );

        // delete embedded contents not longer to keep
        for ( Content embeddedContentBeforeEdit : embeddedContentsBeforeEdit.values() )
        {
            if ( !embeddedContentsToKeep.containsKey( embeddedContentBeforeEdit.getId() ) )
            {
                final NodePath nodePathToEmbeddedContentNode =
                    EmbeddedNodePathFactory.create( embeddedContentBeforeEdit.getParentPath(), embeddedContentBeforeEdit.getName() );
                final DeleteNodeByPath deleteNodeByPathCommand = new DeleteNodeByPath( nodePathToEmbeddedContentNode );
                new DeleteNodeByPathService( this.context.getJcrSession(), indexService, deleteNodeByPathCommand ).execute();
            }
        }
    }


    private void syncRelationships( final Content persistedContent, final Content temporaryContent )
    {
        final Session session = context.getJcrSession();
        final Client client = context.getClient();

        final ContentId contentId = persistedContent.getId();

        relationshipService.syncRelationships( new SyncRelationshipsCommand().
            client( client ).
            jcrSession( session ).
            contentType( persistedContent.getType() ).
            contentToUpdate( contentId ).
            contentBeforeEditing( persistedContent.getContentData() ).
            contentAfterEditing( temporaryContent.getContentData() ) );
    }

    private void validateEditedContent( final Content persistedContent, final Content edited )
    {
        persistedContent.checkIllegalEdit( edited );

        if ( !edited.isDraft() )
        {
            validateContentData( context, edited );
        }
    }


    private ImmutableMap<ContentId, Content> resolveEmbeddedContent( final ContentData contentData )
    {

        final Session session = this.context.getJcrSession();

        final ImmutableMap.Builder<ContentId, Content> embeddedContent = new ImmutableMap.Builder<>();
        new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {

                final Content content = new GetContentByIdService( context, new GetContentById( property.getContentId() ) ).execute();

                if ( content != null )
                {
                    if ( content.isEmbedded() )
                    {
                        embeddedContent.put( content.getId(), content );
                    }
                }

            }
        }.restrictType( ValueTypes.CONTENT_ID ).traverse( contentData );

        return embeddedContent.build();
    }

    //TODO: Rewrite after mocing attachment
    private void validateContentData( final CommandContext context, final Content modifiedContent )
    {
        final ValidateContentData validateContentData = Commands.content().validate();
        validateContentData.contentType( modifiedContent.getType() );
        validateContentData.contentData( modifiedContent.getContentData() );

        final DataValidationErrors dataValidationErrors = context.getClient().execute( validateContentData );

        for ( DataValidationError error : dataValidationErrors )
        {
            LOG.info( "*** DataValidationError: " + error.getErrorMessage() );
        }
        if ( dataValidationErrors.hasErrors() )
        {
            throw new ContentDataValidationException( dataValidationErrors.getFirst().getErrorMessage() );
        }
    }

    /*private void addThumbnail( final Client client, final ContentId contentId, final Attachment attachment )
        throws Exception
    {
        final Blob thumbnailBlob = createImageThumbnail( attachment.getBlobKey(), THUMBNAIL_SIZE );
        final Attachment thumbnailAttachment = newAttachment( attachment ).
            blobKey( thumbnailBlob.getKey() ).
            name( CreateContent.THUMBNAIL_NAME ).
            mimeType( THUMBNAIL_MIME_TYPE ).
            build();
        client.execute( Commands.attachment().create().contentId( contentId ).attachment( thumbnailAttachment ) );
    }*/

    /*public Blob createImageThumbnail( final BlobKey originalImageBlobKey, final int size )
        throws Exception
    {
        final Blob originalImage = context.getClient().execute( Commands.blob().get( originalImageBlobKey ) );
        final BufferedImage image = ImageIO.read( originalImage.getStream() );
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final BufferedImage scaledImage = new ScaleMaxFilter( size ).filter( image );
        ImageIO.write( scaledImage, "png", outputStream );
        CreateBlob createBlob = Commands.blob().create( ByteStreams.newInputStreamSupplier( outputStream.toByteArray() ).getInput() );
        return context.getClient().execute( createBlob );
    }*/

    @Inject
    public void setRelationshipService( final RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

}
