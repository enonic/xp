package com.enonic.wem.core.content;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.UpdateContentResult;
import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
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
            final Content persistedContent = contentDao.select( command.getSelector(), context.getJcrSession() );
            if ( persistedContent == null )
            {
                throw new ContentNotFoundException( command.getSelector() );
            }

            Content edited = command.getEditor().edit( persistedContent );
            if ( edited != null )
            {
                persistedContent.checkIllegalEdit( edited );

                validateContentData( context, edited );

                // walk trough edited.ContentData
                // if value is ContentId
                // if contentId exists as TemporaryEmbeddedContent
                //    temporaryEmbeddedContents.add( contentId )
                // else if contentId exists as persisted embedded content
                //    embeddedContentToKeep.add( contentId )

                // find embeddedContentsToDelete: persistedEmbeddedContent not in embeddedContentToKeep

                // createEmbeddedContent( temporaryEmbeddedContents, parent )
                //    new path for embedded content: <parentPath>/_embedded/<name>
                //    move content under parent under node "_embedded"
                //

                //
                // deleteEmbeddedContent( embeddedContentsToDelete )

                relationshipService.syncRelationships( new SyncRelationshipsCommand().
                    client( context.getClient() ).
                    jcrSession( context.getJcrSession() ).
                    contentType( persistedContent.getType() ).
                    contentToUpdate( persistedContent.getId() ).
                    contentBeforeEditing( persistedContent.getRootDataSet() ).
                    contentAfterEditing( edited.getRootDataSet() ) );

                edited = newContent( edited ).
                    modifiedTime( DateTime.now() ).
                    modifier( command.getModifier() ).build();

                final boolean createNewVersion = true;
                contentDao.update( edited, createNewVersion, context.getJcrSession() );
                context.getJcrSession().save();

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
            // TODO: Throw exception or return rich result instead when GUI can display error message
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
