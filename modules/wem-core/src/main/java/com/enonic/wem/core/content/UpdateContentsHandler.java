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

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.command.content.relationship.CreateRelationship;
import com.enonic.wem.api.command.content.relationship.DeleteRelationships;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataVisitor;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.relationship.RelationshipKeys;
import com.enonic.wem.api.content.schema.content.validator.DataValidationError;
import com.enonic.wem.api.content.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.dao.ContentIdFactory;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.relationship.RelationshipKey.newRelationshipKey;

@Component
public class UpdateContentsHandler
    extends CommandHandler<UpdateContents>
{
    private ContentDao contentDao;

    private IndexService indexService;

    private final static Logger LOG = LoggerFactory.getLogger( UpdateContentsHandler.class );


    public UpdateContentsHandler()
    {
        super( UpdateContents.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateContents command )
        throws Exception
    {
        final Contents contents = contentDao.select( command.getSelectors(), context.getJcrSession() );
        for ( Content contentToUpdate : contents )
        {
            handleContent( context, command, contentToUpdate );
        }
    }

    private void handleContent( final CommandContext context, final UpdateContents command, Content persistedContent )
        throws Exception
    {
        Content modifiedContent = command.getEditor().edit( persistedContent );
        if ( modifiedContent != null )
        {
            modifiedContent = newContent( modifiedContent ).
                modifiedTime( DateTime.now() ).
                modifier( command.getModifier() ).build();

            validateContentData( context, modifiedContent );

            new SyncRelationships( context.getClient(), persistedContent, modifiedContent ).invoke();

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
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    class SyncRelationships
    {
        private final Client client;

        private final Content contentToUpdate;

        private final Map<EntryPath, Data> referencesBeforeEditing;

        private final Map<EntryPath, Data> referencesAfterEditing;

        SyncRelationships( final Client client, final Content contentBeforeEditing, final Content contentAfterEditing )
        {
            this.client = client;
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
            final RelationshipKeys.Builder relationshipsToDelete = RelationshipKeys.newRelationshipKeys();
            for ( Data removedReference : removedReferences )
            {
                relationshipsToDelete.add( newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( contentToUpdate.getId() ).
                    toContent( ContentIdFactory.from( removedReference.getString() ) ).
                    managingData( removedReference.getPath() ).
                    build() );

                final DeleteRelationships deleteRelationships = Commands.relationship().delete();
                deleteRelationships.relationships( relationshipsToDelete.build() );
                client.execute( deleteRelationships );
            }
        }

        private void createAddedReferences()
        {
            final List<Data> addedReferences = resolveAddedReferences();
            for ( Data addedReference : addedReferences )
            {
                final CreateRelationship createRelationship = Commands.relationship().create().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( contentToUpdate.getId() ).
                    toContent( ContentIdFactory.from( addedReference.getString() ) ).
                    managed( addedReference.getPath() );
                client.execute( createRelationship );
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
