package com.enonic.wem.core.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataVisitor;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.api.content.schema.content.validator.DataValidationError;
import com.enonic.wem.api.content.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.relationship.RelationshipFactory;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.relationship.RelationshipKey.newRelationshipKey;
import static com.enonic.wem.core.content.relationship.RelationshipFactory.newRelationshipFactory;

@Component
public class UpdateContentHandler
    extends CommandHandler<UpdateContent>
{
    private ContentDao contentDao;

    private RelationshipDao relationshipDao;

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
        final Content persistedContent = contentDao.select( command.getSelector(), context.getJcrSession() );

        Content modifiedContent = command.getEditor().edit( persistedContent );
        if ( modifiedContent != null )
        {
            validateContentData( context, modifiedContent );

            new SyncRelationships( context, persistedContent, modifiedContent ).invoke();

            modifiedContent = newContent( modifiedContent ).
                modifiedTime( DateTime.now() ).
                modifier( command.getModifier() ).build();

            final boolean createNewVersion = true;
            contentDao.update( modifiedContent, createNewVersion, context.getJcrSession() );
            context.getJcrSession().save();

            try
            {
                // TODO: Temporary easy solution. The index logic should eventually not be here anyway
                indexService.indexContent( modifiedContent );
            }
            catch ( Exception e )
            {
                LOG.error( "Index content failed", e );
            }

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
    public void setRelationshipDao( final RelationshipDao relationshipDao )
    {
        this.relationshipDao = relationshipDao;
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    class SyncRelationships
    {
        private final CommandContext context;

        private final Content contentToUpdate;

        private final Map<EntryPath, Data> referencesBeforeEditing;

        private final Map<EntryPath, Data> referencesAfterEditing;

        SyncRelationships( final CommandContext context, final Content contentBeforeEditing, final Content contentAfterEditing )
        {
            this.context = context;
            this.contentToUpdate = contentAfterEditing;
            this.referencesBeforeEditing = resolveReferences( contentBeforeEditing.getRootDataSet() );
            this.referencesAfterEditing = resolveReferences( contentAfterEditing.getRootDataSet() );
        }

        void invoke()
        {
            deleteRemovedRelationships();
            createAddedReferences();
        }

        private void deleteRemovedRelationships()
        {
            final List<Data> removedReferences = resolveRemovedReferences();
            for ( Data removedReference : removedReferences )
            {
                final RelationshipKey relationshipKey = newRelationshipKey().
                    type( QualifiedRelationshipTypeName.DEFAULT ).
                    fromContent( contentToUpdate.getId() ).
                    toContent( ContentId.from( removedReference.getString() ) ).
                    managingData( removedReference.getPath() ).
                    build();
                relationshipDao.delete( relationshipKey, context.getJcrSession() );
            }
        }

        private void createAddedReferences()
        {
            final RelationshipFactory relationshipFactory = newRelationshipFactory().
                creator( AccountKey.anonymous() ).
                createdTime( DateTime.now() ).
                fromContent( contentToUpdate.getId() ).
                type( QualifiedRelationshipTypeName.DEFAULT ).
                build();

            final List<Data> addedReferences = resolveAddedReferences();
            for ( Data addedReference : addedReferences )
            {
                Relationship relationship = relationshipFactory.create( addedReference );
                relationshipDao.create( relationship, context.getJcrSession() );
            }
        }

        private List<Data> resolveAddedReferences()
        {
            final List<Data> addedReferences = new ArrayList<>();
            for ( Map.Entry<EntryPath, Data> referenceAfterEditing : referencesAfterEditing.entrySet() )
            {
                if ( !referencesBeforeEditing.containsKey( referenceAfterEditing.getKey() ) )
                {
                    addedReferences.add( referenceAfterEditing.getValue() );
                }
            }
            return addedReferences;
        }

        private List<Data> resolveRemovedReferences()
        {
            final List<Data> removedReferences = new ArrayList<>();
            for ( Map.Entry<EntryPath, Data> referenceBeforeEditing : referencesBeforeEditing.entrySet() )
            {
                if ( !referencesAfterEditing.containsKey( referenceBeforeEditing.getKey() ) )
                {
                    removedReferences.add( referenceBeforeEditing.getValue() );
                }
            }
            return removedReferences;
        }

        private Map<EntryPath, Data> resolveReferences( final RootDataSet rootDataSet )
        {
            final Map<EntryPath, Data> references = new LinkedHashMap<>();
            final DataVisitor dataVisitor = new DataVisitor()
            {
                @Override
                public void visit( final Data reference )
                {
                    references.put( reference.getPath(), reference );
                }
            };
            dataVisitor.restrictType( DataTypes.CONTENT_REFERENCE );
            dataVisitor.traverse( rootDataSet );
            return references;
        }
    }
}
