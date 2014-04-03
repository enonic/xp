package com.enonic.wem.core.content;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.relationship.RelationshipService;
import com.enonic.wem.core.relationship.SyncRelationshipsCommand;

public class CreateContentHandler
    extends CommandHandler<CreateContent>
{
    private RelationshipService relationshipService;

    private ContentTypeService contentTypeService;

    private final static Logger LOG = LoggerFactory.getLogger( CreateContentHandler.class );

    private NodeService nodeService;

    @Override
    public void handle()
        throws Exception
    {
        // TODO: Add later
        //verifyParentAllowsChildren();

        ContentNodeTranslator translator = new ContentNodeTranslator( context.getClient(), contentTypeService );

        if ( !command.isDraft() )
        {
            validateContentData( command );
        }

        final CreateNodeParams createNodeParams = translator.toCreateNode( command );

        final Node createdNode = nodeService.create( createNodeParams ).getPersistedNode();

        final Content storedContent = translator.fromNode( createdNode );

        command.setResult( storedContent );
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
            //contentDao.forceDelete( storedContent.getId(), session );
            session.save();
            throw e;
        }
    }

    private void verifyParentAllowsChildren()
    {
        if ( !command.isDraft() && !command.getParentContentPath().isRoot() )
        {
            checkParentContentAllowsChildren( command.getParentContentPath(), context.getJcrSession() );
        }
    }

    private void checkParentContentAllowsChildren( final ContentPath parentContentPath, final Session session )
    {
     /*   //TODO: Rewrite this to NODE
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
        */
    }

    private void validateContentData( final CreateContent content )
    {
        final ValidateContentData validateContentData = Commands.content().validate();
        validateContentData.contentType( content.getContentType() );
        validateContentData.contentData( content.getContentData() );
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


    @Inject
    public void setRelationshipService( final RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Inject
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
