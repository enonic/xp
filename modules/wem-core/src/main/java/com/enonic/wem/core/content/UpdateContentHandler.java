package com.enonic.wem.core.content;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.command.content.blob.CreateBlob;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.entity.DeleteNodeByPathHandler;
import com.enonic.wem.core.entity.GetNodeByPathService;
import com.enonic.wem.core.entity.UpdateNodeHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.image.filter.effect.ScaleMaxFilter;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.relationship.RelationshipService;
import com.enonic.wem.core.relationship.SyncRelationshipsCommand;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;


public class UpdateContentHandler
    extends CommandHandler<UpdateContent>
{
    private static final int THUMBNAIL_SIZE = 512;

    private static final String THUMBNAIL_MIME_TYPE = "image/png";

    private ContentDao contentDao;

    private RelationshipService relationshipService;

    private IndexService indexService;

    private final static Logger LOG = LoggerFactory.getLogger( UpdateContentHandler.class );

    private final static ContentNodeTranslator CONTENT_NODE_TRANSLATOR = new ContentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final Client client = context.getClient();

        // TODO: Fetch Node, use path or id?
        final Content persistedContent = getPersistedContent( session );

        //TODO: the result is null if nothing was edited, but should be SUCCESS ?
        Content.EditBuilder editBuilder = command.getEditor().edit( persistedContent );
        if ( !editBuilder.isChanges() )
        {
            command.setResult( null );
            return;
        }

        //TODO: Attachments have no editor and thus need to be checked separately, but probably should have one ?
        if ( !command.getAttachments().isEmpty() )
        {
            final ContentId contentId = persistedContent.getId();
            addAttachments( contentId, command.getAttachments() );
            addMediaThumbnail( persistedContent, contentId );
        }

        final Content tempEditedContent = editBuilder.build();
        validateEditedContent( persistedContent, tempEditedContent );

        syncRelationships( persistedContent, tempEditedContent );

        final Content editedContent = newContent( tempEditedContent ).
            modifiedTime( DateTime.now() ).
            modifier( command.getModifier() ).build();

        final boolean createNewVersion = true;
        final Content updatedContent = contentDao.update( editedContent, createNewVersion, session );
        session.save();

        // This must be placed before the deleteRemoveEmbedded since we use persisted and edited both for node and content for now
        // Hackyhackyhacky
        updateContentAsNode( persistedContent.getPath() );

        deleteRemovedEmbeddedContent( session, persistedContent, editedContent );

        indexService.indexContent( tempEditedContent );

        command.setResult( updatedContent );
    }

    private void updateContentAsNode( final ContentPath contentPath )
    {
        final NodePath nodePathToContent = translateContentPathToNodePath( contentPath );

        final Node oldPersistedNode = getNodeByPath( nodePathToContent );

        final Content persistedNodeAsContent = CONTENT_NODE_TRANSLATOR.fromNode( oldPersistedNode );

        Content.EditBuilder editBuilder = command.getEditor().edit( persistedNodeAsContent );

        if ( !editBuilder.isChanges() )
        {
            return;
        }

        final Content tempEditedContent = editBuilder.build();

        final Content editedContent = newContent( tempEditedContent ).
            modifiedTime( DateTime.now() ).
            modifier( command.getModifier() ).build();

        final NodeEditor nodeEditor = CONTENT_NODE_TRANSLATOR.toNodeEditor( editedContent, this.command );
        final UpdateNode updateNodeCommand = CONTENT_NODE_TRANSLATOR.toUpdateNodeCommand( persistedNodeAsContent.getId(), nodeEditor );

        final UpdateNodeHandler updateNodeHandler = UpdateNodeHandler.create().
            context( this.context ).
            command( updateNodeCommand ).
            indexService( this.indexService ).
            build();
        try
        {
            updateNodeHandler.handle();

            final Node editedNode = updateNodeCommand.getResult().getPersistedNode();
            final Content editedNodeAsContent = CONTENT_NODE_TRANSLATOR.fromNode( editedNode );

            deleteRemovedEmbeddedContentAsNode( persistedNodeAsContent, editedNodeAsContent );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to store content as node", e );
        }
    }

    private NodePath translateContentPathToNodePath( final ContentPath contentPath )
    {
        return new NodePath( NodeJcrDao.CONTENT_NODE_ROOT + "/" + contentPath.toString() ).asAbsolute();
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
                NodePath nodePathToEmbeddedContentNode =
                    EmbeddedNodePathFactory.create( embeddedContentBeforeEdit.getParentPath(), embeddedContentBeforeEdit.getName() );

                DeleteNodeByPath deleteNodeByPathCommand = new DeleteNodeByPath( nodePathToEmbeddedContentNode );

                deleteNodeByPath( deleteNodeByPathCommand );
            }
        }
    }

    private Node getNodeByPath( final NodePath nodePathToContent )
    {
        return new GetNodeByPathService( this.context.getJcrSession(), new GetNodeByPath( nodePathToContent ) ).execute();
    }

    private void deleteNodeByPath( final DeleteNodeByPath deleteNodeByPathCommand )
        throws Exception
    {
        final DeleteNodeByPathHandler deleteNodeByPathHandler =
            DeleteNodeByPathHandler.create().indexService( this.indexService ).context( this.context ).command(
                deleteNodeByPathCommand ).build();

        deleteNodeByPathHandler.handle();
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

    private void deleteRemovedEmbeddedContent( final Session session, final Content persistedContent, final Content editedContent )
        throws RepositoryException
    {
        final Map<ContentId, Content> embeddedContentsBeforeEdit = resolveEmbeddedContent( persistedContent.getContentData() );

        final Map<ContentId, Content> embeddedContentsToKeep = resolveEmbeddedContent( editedContent.getContentData() );
        // delete embedded contents not longer to keep

        for ( Content embeddedContentBeforeEdit : embeddedContentsBeforeEdit.values() )
        {
            if ( !embeddedContentsToKeep.containsKey( embeddedContentBeforeEdit.getId() ) )
            {
                contentDao.deleteById( embeddedContentBeforeEdit.getId(), session );
                session.save();
            }
        }
    }

    private void validateEditedContent( final Content persistedContent, final Content edited )
    {
        persistedContent.checkIllegalEdit( edited );

        if ( !edited.isDraft() )
        {
            validateContentData( context, edited );
        }
    }

    private Content getPersistedContent( final Session session )
    {
        final Content persistedContent = contentDao.selectById( command.getContentId(), session );

        if ( persistedContent == null )
        {
            throw new ContentNotFoundException( command.getContentId() );
        }
        return persistedContent;
    }

    private void addMediaThumbnail( final Content content, final ContentId contentId )
        throws Exception
    {

        final Client client = context.getClient();

        // TODO: Fetch through Node-handler instead?
        final ContentType contentType = client.execute( Commands.contentType().get().byName().contentTypeName( content.getType() ) );

        if ( ( contentType.getSuperType() != null ) && contentType.getSuperType().isMedia() )
        {
            Attachment mediaAttachment = command.getAttachment( content.getName().toString() );
            if ( ( mediaAttachment == null ) && ( !command.getAttachments().isEmpty() ) )
            {
                mediaAttachment = command.getAttachments().iterator().next();
            }
            if ( mediaAttachment != null )
            {
                addThumbnail( client, contentId, mediaAttachment );
            }
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
                final Content content = contentDao.selectById( property.getContentId(), session );
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

    private void addAttachments( final ContentId contentId, final Collection<Attachment> attachments )
    {
        final Client client = context.getClient();

        for ( Attachment attachment : attachments )
        {
            client.execute( Commands.attachment().create().contentId( contentId ).attachment( attachment ) );
        }
    }

    private void addThumbnail( final Client client, final ContentId contentId, final Attachment attachment )
        throws Exception
    {
        final Blob thumbnailBlob = createImageThumbnail( attachment.getBlobKey(), THUMBNAIL_SIZE );
        final Attachment thumbnailAttachment = newAttachment( attachment ).
            blobKey( thumbnailBlob.getKey() ).
            name( CreateContent.THUMBNAIL_NAME ).
            mimeType( THUMBNAIL_MIME_TYPE ).
            build();
        client.execute( Commands.attachment().create().contentId( contentId ).attachment( thumbnailAttachment ) );
    }

    public Blob createImageThumbnail( final BlobKey originalImageBlobKey, final int size )
        throws Exception
    {
        final Blob originalImage = context.getClient().execute( Commands.blob().get( originalImageBlobKey ) );
        final BufferedImage image = ImageIO.read( originalImage.getStream() );
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final BufferedImage scaledImage = new ScaleMaxFilter( size ).filter( image );
        ImageIO.write( scaledImage, "png", outputStream );
        CreateBlob createBlob = Commands.blob().create( ByteStreams.newInputStreamSupplier( outputStream.toByteArray() ).getInput() );
        return context.getClient().execute( createBlob );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

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
