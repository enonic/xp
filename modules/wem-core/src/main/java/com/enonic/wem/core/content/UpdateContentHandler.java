package com.enonic.wem.core.content;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.UpdateContentResult;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.UpdateContentException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.data.data.Property;
import com.enonic.wem.api.data.data.PropertyVisitor;
import com.enonic.wem.api.data.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.image.filter.effect.ScaleMaxFilter;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.relationship.RelationshipService;
import com.enonic.wem.core.relationship.SyncRelationshipsCommand;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;


public class UpdateContentHandler
    extends CommandHandler<UpdateContent>
{
    private static final int THUMBNAIL_SIZE = 512;

    private static final String THUMBNAIL_MIME_TYPE = "image/png";

    private ContentDao contentDao;

    private ContentTypeDao contentTypeDao;

    private RelationshipService relationshipService;

    private IndexService indexService;

    private final static Logger LOG = LoggerFactory.getLogger( UpdateContentHandler.class );


    public UpdateContentHandler()
    {
        super( UpdateContent.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateContent command )
        throws Exception
    {
        try
        {
            final Session session = context.getJcrSession();
            final Content persistedContent = contentDao.select( command.getSelector(), session );
            if ( persistedContent == null )
            {
                throw new ContentNotFoundException( command.getSelector() );
            }

            final List<Content> embeddedContentsBeforeEdit = resolveEmbeddedContent( session, persistedContent );

            Content edited = command.getEditor().edit( persistedContent );
            if ( edited != null )
            {
                persistedContent.checkIllegalEdit( edited );

                validateContentData( context, edited );

                final List<ContentId> embeddedContentsToKeep = new ArrayList<>();
                final List<Content> temporaryContents = new ArrayList<>();
                new PropertyVisitor()
                {
                    @Override
                    public void visit( final Property property )
                    {
                        final Content content = contentDao.select( property.getContentId(), session );
                        if ( content != null )
                        {
                            if ( content.isTemporary() )
                            {
                                temporaryContents.add( content );
                            }
                            else if ( content.isEmbedded() )
                            {
                                embeddedContentsToKeep.add( content.getId() );
                            }
                        }
                    }
                }.restrictType( ValueTypes.CONTENT_ID ).traverse( edited.getContentData() );

                final Client client = context.getClient();
                final ContentId contentId = persistedContent.getId();
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
                contentDao.update( edited, createNewVersion, session );
                session.save();
                addAttachments( client, contentId, command.getAttachments() );
                addMediaThumbnail( client, session, command, edited, contentId );

                createEmbeddedContents( session, edited, temporaryContents );

                // delete embedded contents not longer to keep
                for ( Content embeddedContentBeforeEdit : embeddedContentsBeforeEdit )
                {
                    if ( !embeddedContentsToKeep.contains( embeddedContentBeforeEdit.getId() ) )
                    {
                        contentDao.delete( embeddedContentBeforeEdit.getId(), session );
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
                command.setResult( UpdateContentResult.SUCCESS );
            }
        }
        catch ( ContentNotFoundException | IllegalEditException e )
        {
            command.setResult( UpdateContentResult.from( e ) );
        }
        catch ( Exception e )
        {
            throw new UpdateContentException( command, e );
        }
    }

    private void addMediaThumbnail( final Client client, final Session session, final UpdateContent command, final Content content,
                                    final ContentId contentId )
        throws Exception
    {
        final ContentType contentType = contentTypeDao.select( content.getType(), session );
        if ( ( contentType.getSuperType() != null ) && contentType.getSuperType().isMedia() )
        {
            Attachment mediaAttachment = command.getAttachment( content.getName() );
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

    private void createEmbeddedContents( final Session session, final Content edited, final List<Content> temporaryContents )
        throws RepositoryException
    {
        for ( Content tempContent : temporaryContents )
        {
            final ContentPath pathToEmbeddedContent = ContentPath.createPathToEmbeddedContent( edited.getPath(), tempContent.getName() );
            createEmbeddedContent( tempContent, pathToEmbeddedContent, session );
        }
    }

    private void createEmbeddedContent( final Content tempContent, final ContentPath pathToEmbeddedContent, final Session session )
        throws RepositoryException
    {
        contentDao.moveContent( tempContent.getId(), pathToEmbeddedContent, session );
        session.save();
    }

    private List<Content> resolveEmbeddedContent( final Session session, final Content persistedContent )
    {
        final List<Content> embeddedContent = new ArrayList<>();
        new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                final Content content = contentDao.select( property.getContentId(), session );
                if ( content != null )
                {
                    if ( content.isEmbedded() )
                    {
                        embeddedContent.add( content );
                    }
                }
            }
        }.restrictType( ValueTypes.CONTENT_ID ).traverse( persistedContent.getContentData() );
        return embeddedContent;
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
            client.execute( Commands.attachment().create().contentSelector( contentId ).attachment( attachment ) );
        }
    }

    private void addThumbnail( final Client client, final ContentId contentId, final Attachment attachment )
        throws Exception
    {
        final Binary thumbnailBinary = createImageThumbnail( attachment.getBinary(), THUMBNAIL_SIZE );
        final Attachment thumbnailAttachment = newAttachment( attachment ).
            binary( thumbnailBinary ).
            name( CreateContent.THUMBNAIL_NAME ).
            mimeType( THUMBNAIL_MIME_TYPE ).
            build();
        client.execute( Commands.attachment().create().contentSelector( contentId ).attachment( thumbnailAttachment ) );
    }

    public Binary createImageThumbnail( final Binary binary, final int size )
        throws Exception
    {
        final BufferedImage image = ImageIO.read( binary.asInputStream() );
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final BufferedImage scaledImage = new ScaleMaxFilter( size ).filter( image );
        ImageIO.write( scaledImage, "png", outputStream );
        return Binary.from( outputStream.toByteArray() );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
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
