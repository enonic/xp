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

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.UpdateContentResult;
import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.UpdateContentException;
import com.enonic.wem.api.content.data.DataVisitor;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.type.PropertyTypes;
import com.enonic.wem.api.content.schema.content.validator.DataValidationError;
import com.enonic.wem.api.content.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.relationship.RelationshipService;
import com.enonic.wem.core.content.relationship.SyncRelationshipsCommand;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.api.content.Content.newContent;

@Component
public class UpdateContentHandler
    extends CommandHandler<UpdateContent>
{
    private ContentDao contentDao;

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
                new DataVisitor()
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
                }.restrictType( PropertyTypes.CONTENT_ID ).traverse( edited.getRootDataSet() );

                relationshipService.syncRelationships( new SyncRelationshipsCommand().
                    client( context.getClient() ).
                    jcrSession( session ).
                    contentType( persistedContent.getType() ).
                    contentToUpdate( persistedContent.getId() ).
                    contentBeforeEditing( persistedContent.getRootDataSet() ).
                    contentAfterEditing( edited.getRootDataSet() ) );

                edited = newContent( edited ).
                    modifiedTime( DateTime.now() ).
                    modifier( command.getModifier() ).build();

                final boolean createNewVersion = true;
                contentDao.update( edited, createNewVersion, session );
                session.save();

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
        new DataVisitor()
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
        }.restrictType( PropertyTypes.CONTENT_ID ).traverse( persistedContent.getRootDataSet() );
        return embeddedContent;
    }

    private void validateContentData( final CommandContext context, final Content modifiedContent )
    {
        final ValidateRootDataSet validateRootDataSet = Commands.content().validate();
        validateRootDataSet.contentType( modifiedContent.getType() );
        validateRootDataSet.rootDataSet( modifiedContent.getRootDataSet() );
        final DataValidationErrors dataValidationErrors = context.getClient().execute( validateRootDataSet );
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
