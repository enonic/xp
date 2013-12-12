package com.enonic.wem.core.content;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.inject.Inject;
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
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
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

    @Override
    public void handle()
        throws Exception
    {

        final Session session = context.getJcrSession();
        final Content persistedContent = contentDao.selectById( command.getContentId(), session );
        if ( persistedContent != null )
        {
            throw new ContentNotFoundException( command.getContentId() );
        }

        final Client client = context.getClient();
        final ContentId contentId = persistedContent.getId();
        final Map<ContentId, Content> embeddedContentsBeforeEdit = resolveEmbeddedContent( session, persistedContent.getContentData() );

        //TODO: the result is null if nothing was edited, but should be SUCCESS ?
        Content.EditBuilder editBuilder = command.getEditor().edit( persistedContent );

        //TODO: Attachments have no editor and thus need to be checked separately, but probably should have one ?
        if ( !command.getAttachments().isEmpty() )
        {
            addAttachments( client, contentId, command.getAttachments() );
            addMediaThumbnail( client, session, command, persistedContent, contentId );
        }

        if ( editBuilder.isChanges() )
        {
            Content edited = editBuilder.build();
            persistedContent.checkIllegalEdit( edited );

            if ( !edited.isDraft() )
            {
                validateContentData( context, edited );
            }

            final Map<ContentId, Content> embeddedContentsToKeep = resolveEmbeddedContent( session, edited.getContentData() );

            relationshipService.syncRelationships( new SyncRelationshipsCommand().
                client( client ).
                jcrSession( session ).
                contentType( persistedContent.getType() ).
                contentToUpdate( contentId ).
                contentBeforeEditing( persistedContent.getContentData() ).
                contentAfterEditing( edited.getContentData() ) );

            edited = newContent( edited ).
                modifiedTime( DateTime.now() ).
                modifier( command.getModifier() ).build();

            final boolean createNewVersion = true;
            final Content updatedContent = contentDao.update( edited, createNewVersion, session );
            session.save();

            // delete embedded contents not longer to keep
            for ( Content embeddedContentBeforeEdit : embeddedContentsBeforeEdit.values() )
            {
                if ( !embeddedContentsToKeep.containsKey( embeddedContentBeforeEdit.getId() ) )
                {
                    contentDao.deleteById( embeddedContentBeforeEdit.getId(), session );
                    session.save();
                }
            }

            try
            {
                // TODO: Temporary easy solution. The index logic should eventually not be here anyway
                indexService.indexContent( edited );
            }
            catch ( Exception e )
            {
                LOG.error( "Index content failed", e );
            }
            command.setResult( updatedContent );
        }
    }

    private void addMediaThumbnail( final Client client, final Session session, final UpdateContent command, final Content content,
                                    final ContentId contentId )
        throws Exception
    {
        final ContentType contentType =
            context.getClient().execute( Commands.contentType().get().byName().contentTypeName( content.getType() ) );

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

    private ImmutableMap<ContentId, Content> resolveEmbeddedContent( final Session session, final ContentData contentData )
    {
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

    private void addAttachments( final Client client, final ContentId contentId, final Collection<Attachment> attachments )
    {
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
