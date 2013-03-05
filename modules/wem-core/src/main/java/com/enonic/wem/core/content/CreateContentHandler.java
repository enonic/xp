package com.enonic.wem.core.content;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.jcr.Session;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataVisitor;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.schema.content.validator.DataValidationError;
import com.enonic.wem.api.content.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.relationship.RelationshipFactory;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.core.content.relationship.RelationshipFactory.newRelationshipFactory;

@Component
public class CreateContentHandler
    extends CommandHandler<CreateContent>
{
    private ContentDao contentDao;

    private RelationshipDao relationshipDao;

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
        final Content.Builder builder = Content.newContent();
        builder.path( command.getContentPath() );
        builder.rootDataSet( command.getRootDataSet() );
        builder.type( command.getContentType() );
        builder.displayName( command.getDisplayName() );
        builder.createdTime( DateTime.now() );
        builder.modifiedTime( DateTime.now() );
        builder.owner( command.getOwner() );
        builder.modifier( command.getOwner() );

        final Content content = builder.build();

        final Session session = context.getJcrSession();
        final ContentId contentId = contentDao.create( content, session );
        session.save();

        final List<Data> references = resolveReferences( content.getRootDataSet() );

        validateContentData( context.getClient(), content );

        createRelationships( context, contentId, references );

        try
        {
            // TODO: Temporary easy solution to get Id. The index logic should eventually not be here anyway
            final Content storedContent = builder.id( contentId ).build();
            indexService.indexContent( storedContent );
        }
        catch ( Exception e )
        {
            LOG.error( "Index content failed", e );
        }

        command.setResult( contentId );
    }

    private void createRelationships( final CommandContext context, final ContentId contentId, final List<Data> references )
    {
        final RelationshipFactory relationshipFactory = newRelationshipFactory().
            creator( AccountKey.anonymous() ).
            createdTime( DateTime.now() ).
            fromContent( contentId ).
            type( QualifiedRelationshipTypeName.DEFAULT ).
            build();

        for ( Data reference : references )
        {
            final Relationship relationship = relationshipFactory.create( reference );
            relationshipDao.create( relationship, context.getJcrSession() );
        }
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
            // TODO: Throw exception or return rich result instead when GUI can display error message
        }
    }

    private List<Data> resolveReferences( final RootDataSet rootDataSet )
    {
        final List<Data> references = new ArrayList<>();
        final DataVisitor dataVisitor = new DataVisitor()
        {
            @Override
            public void visit( final Data reference )
            {
                references.add( reference );
            }
        };
        dataVisitor.restrictType( DataTypes.CONTENT_REFERENCE );
        dataVisitor.traverse( rootDataSet );
        return references;
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Inject
    public void setRelationshipDao( final RelationshipDao relationshipDao )
    {
        this.relationshipDao = relationshipDao;
    }


    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
