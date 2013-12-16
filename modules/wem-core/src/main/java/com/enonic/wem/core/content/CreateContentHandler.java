package com.enonic.wem.core.content;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.command.content.blob.CreateBlob;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.entity.CreateNodeHandler;
import com.enonic.wem.core.image.filter.effect.ScaleMaxFilter;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.relationship.RelationshipService;
import com.enonic.wem.core.relationship.SyncRelationshipsCommand;

import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;

public class CreateContentHandler
    extends CommandHandler<CreateContent>
{
    private static final int THUMBNAIL_SIZE = 512;

    private static final String THUMBNAIL_MIME_TYPE = "image/png";

    private ContentDao contentDao;

    private RelationshipService relationshipService;

    private IndexService indexService;

    private final static Logger LOG = LoggerFactory.getLogger( CreateContentHandler.class );

    private final static ContentNodeTranslator CONTENT_NODE_TRANSLATOR = new ContentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();

        verifyParentAllowsChildren();

        final Content builtContent = buildContent();

        if ( !command.isDraft() )
        {
            validateContentData( context.getClient(), builtContent );
        }

        final Content storedContent = contentDao.create( builtContent, session );
        session.save();

        addAttachments( builtContent, storedContent );

        addRelationships( session, builtContent, storedContent );

        final CreateNode createNodeCommand = CONTENT_NODE_TRANSLATOR.toCreateNode( builtContent, command );
        createNode( createNodeCommand );

        indexService.indexContent( storedContent );

        command.setResult( storedContent );
    }

    private CreateNodeResult createNode( final CreateNode createNodeCommand )
        throws Exception
    {
        CreateNodeHandler createNodeHandler = CreateNodeHandler.create().
            command( createNodeCommand ).
            indexService( indexService ).
            context( this.context ).
            build();
        createNodeHandler.handle();

        return createNodeCommand.getResult();
    }


    private void addRelationships( final Session session, final Content content, final Content storedContent )
        throws RepositoryException
    {
        try
        {
            /*TODO: Remove
            for ( Content tempContent : temporaryContents )
            {
                final ContentPath pathToEmbeddedContent = ContentPath.createPathToEmbeddedContent( contentPath, tempContent.getName() );
                createEmbeddedContent( tempContent, pathToEmbeddedContent, session );
            }*/

            relationshipService.syncRelationships( new SyncRelationshipsCommand().
                client( context.getClient() ).
                jcrSession( session ).
                contentType( content.getType() ).
                contentToUpdate( storedContent.getId() ).
                contentAfterEditing( content.getContentData() ) );
            session.save();
        }
        catch ( Exception e )
        {
            // Temporary way of rollback: try delete content if any failure
            contentDao.forceDelete( storedContent.getId(), session );
            session.save();
            throw e;
        }
    }

    private Content buildContent()
    {
        final Content.Builder builder = Content.newContent();

        builder.name( resolveName( command.getName() ) );
        builder.parentPath( resolveParentContentPath() );
        builder.embedded( command.isEmbed() );
        builder.displayName( command.getDisplayName() );
        builder.form( command.getForm() );
        builder.contentData( command.getContentData() );
        builder.type( command.getContentType() );
        builder.createdTime( DateTime.now() );
        builder.modifiedTime( DateTime.now() );
        builder.owner( command.getOwner() );
        builder.modifier( command.getOwner() );
        builder.draft( command.isDraft() );

        return builder.build();
    }

    private ContentName resolveName( final ContentName name )
    {
        if ( name instanceof ContentName.Unnamed )
        {
            ContentName.Unnamed unnammed = (ContentName.Unnamed) name;
            if ( !unnammed.hasUniqueness() )
            {
                return ContentName.Unnamed.withUniqueness();
            }
        }
        return name;
    }

    private ContentPath resolveParentContentPath()
    {
        return command.getParentContentPath();
    }

    private void addAttachments( final Content content, final Content storedContent )
        throws Exception
    {
        addAttachments( context.getClient(), storedContent.getId(), command.getAttachments() );

        final Attachment thumbnailAttachment = resolveThumbnailAttachment( content );
        if ( thumbnailAttachment != null )
        {
            context.getClient().execute(
                Commands.attachment().create().contentId( storedContent.getId() ).attachment( thumbnailAttachment ) );
        }
    }

    private void verifyParentAllowsChildren()
    {
        if ( !command.isDraft() && !command.getParentContentPath().isRoot() )
        {
            checkParentContentAllowsChildren( command.getParentContentPath(), context.getJcrSession() );
        }
    }


    private Attachment resolveThumbnailAttachment( final Content content )
        throws Exception
    {
        final ContentType contentType = getContentType( content );

        if ( ( contentType.getSuperType() != null ) && contentType.getSuperType().isMedia() )
        {
            Attachment mediaAttachment = command.getAttachment( content.getName().toString() );
            if ( mediaAttachment == null )
            {
                if ( !command.getAttachments().isEmpty() )
                {
                    mediaAttachment = command.getAttachments().iterator().next();
                }
            }
            if ( mediaAttachment != null )
            {
                return createThumbnailAttachment( mediaAttachment );
            }
        }
        return null;
    }

    private ContentType getContentType( final Content content )
    {
        return context.getClient().execute( Commands.contentType().get().byName().contentTypeName( content.getType() ) );
    }

    private void checkParentContentAllowsChildren( final ContentPath parentContentPath, final Session session )
    {
        final Content content = contentDao.selectByPath( parentContentPath, session );
        if ( content != null )
        {
            final ContentType contentType = getContentType( content );
            if ( !contentType.allowChildContent() )
            {
                throw new SystemException( "Content [{0}] of type [{1}] does not allow children", parentContentPath,
                                           contentType.getName() );
            }
        }
    }


    private void validateContentData( final Client client, final Content content )
    {
        final ValidateContentData validateContentData = Commands.content().validate();
        validateContentData.contentType( content.getType() );
        validateContentData.contentData( content.getContentData() );
        final DataValidationErrors dataValidationErrors = client.execute( validateContentData );

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

    private Attachment createThumbnailAttachment( final Attachment origin )
        throws Exception
    {
        final Blob thumbnailBlob = createImageThumbnail( origin.getBlobKey(), THUMBNAIL_SIZE );
        return newAttachment( origin ).
            blobKey( thumbnailBlob.getKey() ).
            name( CreateContent.THUMBNAIL_NAME ).
            mimeType( THUMBNAIL_MIME_TYPE ).
            build();
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
