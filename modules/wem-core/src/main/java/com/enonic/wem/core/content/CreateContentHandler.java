package com.enonic.wem.core.content;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.CreateContentResult;
import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.CreateContentException;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataVisitor;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.validator.DataValidationError;
import com.enonic.wem.api.content.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.relationship.RelationshipService;
import com.enonic.wem.core.content.relationship.SyncRelationshipsCommand;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;
import com.enonic.wem.core.index.IndexService;

@Component
public class CreateContentHandler
    extends CommandHandler<CreateContent>
{
    private static final ContentPath TEMPORARY_PARENT_PATH = ContentPath.rootOf( SpaceName.temporary() );

    private static final ContentPathNameGenerator CONTENT_PATH_NAME_GENERATOR = new ContentPathNameGenerator();

    private ContentDao contentDao;

    private ContentTypeDao contentTypeDao;

    private RelationshipService relationshipService;

    private IndexService indexService;

    private final static Logger LOG = LoggerFactory.getLogger( CreateContentHandler.class );

    public CreateContentHandler()
    {
        super( CreateContent.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateContent command )
        throws Exception
    {
        try
        {
            final Session session = context.getJcrSession();

            final Content.Builder builder = Content.newContent();
            final String displayName = command.getDisplayName();
            final ContentPath parentContentPath = command.isTemporary() ? TEMPORARY_PARENT_PATH : command.getParentContentPath();
            final ContentPath contentPath = resolvePathForNewContent( parentContentPath, displayName, session );
            if ( !command.isTemporary() )
            {
                checkParentContentAllowsChildren( parentContentPath, session );
            }

            final List<Content> temporaryContents = resolveTemporaryContents( command, session );

            builder.path( contentPath );
            builder.displayName( displayName );
            builder.rootDataSet( command.getRootDataSet() );
            builder.type( command.getContentType() );
            builder.createdTime( DateTime.now() );
            builder.modifiedTime( DateTime.now() );
            builder.owner( command.getOwner() );
            builder.modifier( command.getOwner() );

            final Content content = builder.build();

            validateContentData( context.getClient(), content );

            final ContentId contentId = contentDao.create( content, session );
            session.save();

            try
            {

                for ( Content tempContent : temporaryContents )
                {
                    final ContentPath pathToEmbeddedContent = ContentPath.createPathToEmbeddedContent( contentPath, tempContent.getName() );
                    createEmbeddedContent( tempContent, pathToEmbeddedContent, session );
                }

                relationshipService.syncRelationships( new SyncRelationshipsCommand().
                    client( context.getClient() ).
                    jcrSession( session ).
                    contentType( content.getType() ).
                    contentToUpdate( contentId ).
                    contentAfterEditing( content.getRootDataSet() ) );
                session.save();
            }
            catch ( Exception e )
            {
                // Temporary way of rollback: try delete content if any failure
                contentDao.forceDelete( contentId, session );
                session.save();
                throw e;
            }

            final Content storedContent = builder.id( contentId ).build();
            indexService.indexContent( storedContent );

            command.setResult( new CreateContentResult( contentId, contentPath ) );
        }
        catch ( final Exception e )
        {
            throw new CreateContentException(
                "Failed to create content [" + command.getDisplayName() + "] at path [" + command.getParentContentPath() + "]: " +
                    e.getMessage(), e );
        }
    }

    private void checkParentContentAllowsChildren( final ContentPath parentContentPath, final Session session )
    {
        final Content content = contentDao.select( parentContentPath, session );
        if ( content != null )
        {
            final ContentType contentType = contentTypeDao.select( content.getType(), session );
            if ( !contentType.allowChildren() )
            {
                throw new SystemException( "Content [{0}] of type [{1}] does not allow children", parentContentPath,
                                           contentType.getQualifiedName() );
            }
        }
    }

    private void createEmbeddedContent( final Content tempContent, final ContentPath pathToEmbeddedContent, final Session session )
        throws RepositoryException
    {
        contentDao.moveContent( tempContent.getId(), pathToEmbeddedContent, session );
        session.save();
    }

    private List<Content> resolveTemporaryContents( final CreateContent command, final Session session )
    {
        final List<Content> temporaryContents = new ArrayList<>();
        if ( command.getRootDataSet() == null )
        {
            return temporaryContents;
        }
        final DataVisitor dataVisitor = new DataVisitor()
        {
            @Override
            public void visit( final Data data )
            {
                final Content content = contentDao.select( data.getContentId(), session );
                if ( content != null )
                {
                    if ( content.isTemporary() )
                    {
                        temporaryContents.add( content );
                    }
                }
            }
        }.restrictType( DataTypes.CONTENT_ID );
        dataVisitor.traverse( command.getRootDataSet() );
        return temporaryContents;
    }

    private ContentPath resolvePathForNewContent( final ContentPath parentPath, final String displayName, final Session session )
    {
        ContentPath possibleNewPath = ContentPath.from( parentPath, CONTENT_PATH_NAME_GENERATOR.generatePathName( displayName ) );
        int i = 1;
        while ( contentExists( possibleNewPath, session ) )
        {
            i++;
            possibleNewPath = ContentPath.from( parentPath, CONTENT_PATH_NAME_GENERATOR.generatePathName( displayName + "-" + i ) );
        }
        return possibleNewPath;
    }

    private boolean contentExists( final ContentPath contentPath, final Session session )
    {
        final Content content = contentDao.select( contentPath, session );
        return content != null;
    }


    private void validateContentData( final Client client, final Content content )
    {
        final ValidateRootDataSet validateRootDataSet = Commands.content().validate();
        validateRootDataSet.contentType( content.getType() );
        validateRootDataSet.rootDataSet( content.getRootDataSet() );
        final DataValidationErrors dataValidationErrors = client.execute( validateRootDataSet );

        for ( DataValidationError error : dataValidationErrors )
        {
            LOG.info( "*** DataValidationError: " + error.getErrorMessage() );
        }
        if ( dataValidationErrors.hasErrors() )
        {
            throw new ContentDataValidationException( dataValidationErrors.getFirst().getErrorMessage() );
        }
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
