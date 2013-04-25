package com.enonic.wem.core.content.relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.DataVisitor;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.PropertyTypes;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.api.content.schema.content.form.Form;
import com.enonic.wem.api.content.schema.content.form.FormItemPath;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.inputtype.RelationshipConfig;

import static com.enonic.wem.api.content.relationship.RelationshipKey.newRelationshipKey;
import static com.enonic.wem.core.content.relationship.RelationshipFactory.newRelationshipFactory;

class SyncRelationships
{
    private final Form form;

    private final ContentId contentToUpdate;

    private final Map<EntryPath, Property> referencesBeforeEditing;

    private final Map<EntryPath, Property> referencesAfterEditing;

    private final ImmutableList.Builder<Relationship> relationshipsToAddBuilder = ImmutableList.builder();

    private ImmutableList<Relationship> relationshipsToAdd;

    private final ImmutableList.Builder<RelationshipKey> relationshipsToDeleteBuilder = ImmutableList.builder();

    private ImmutableList<RelationshipKey> relationshipsToDelete;

    SyncRelationships( final Form form, final ContentId contentToUpdate, final RootDataSet contentBeforeEditing,
                       final RootDataSet contentAfterEditing )
    {
        this.form = form;
        this.contentToUpdate = contentToUpdate;
        if ( contentBeforeEditing != null )
        {
            this.referencesBeforeEditing = resolveReferences( contentBeforeEditing );
        }
        else
        {

            this.referencesBeforeEditing = new HashMap<>();
        }
        this.referencesAfterEditing = resolveReferences( contentAfterEditing );
    }

    void invoke()
    {
        deleteRemovedRelationships();
        createAddedReferences();

        relationshipsToAdd = relationshipsToAddBuilder.build();
        relationshipsToDelete = relationshipsToDeleteBuilder.build();
    }

    ImmutableList<Relationship> getRelationshipsToAdd()
    {
        return relationshipsToAdd;
    }

    ImmutableList<RelationshipKey> getRelationshipsToDelete()
    {
        return relationshipsToDelete;
    }

    private void deleteRemovedRelationships()
    {
        final List<Property> removedReferences = resolveRemovedReferences();
        for ( Property removedReference : removedReferences )
        {
            final Input relationshipInput = form.getInput( FormItemPath.from( removedReference.getPath().resolvePathElementNames() ) );
            Preconditions.checkNotNull( relationshipInput, "No Input found for Property: %s ", removedReference.getPath() );
            final RelationshipConfig relationshipConfig = (RelationshipConfig) relationshipInput.getInputTypeConfig();

            final RelationshipKey relationshipKey = newRelationshipKey().
                type( relationshipConfig.getRelationshipType() ).
                fromContent( contentToUpdate ).
                toContent( removedReference.getContentId() ).
                managingData( removedReference.getPath() ).
                build();
            relationshipsToDeleteBuilder.add( relationshipKey );
        }
    }

    private void createAddedReferences()
    {
        final RelationshipFactory relationshipFactory = newRelationshipFactory().
            creator( AccountKey.anonymous() ).
            createdTime( DateTime.now() ).
            fromContent( contentToUpdate ).
            build();

        final List<Property> addedReferences = resolveAddedReferences();
        for ( Property addedReference : addedReferences )
        {
            final Input relationshipInput = form.getInput( FormItemPath.from( addedReference.getPath().resolvePathElementNames() ) );
            Preconditions.checkNotNull( relationshipInput, "No Input found for Property: %s ", addedReference.getPath() );
            final RelationshipConfig relationshipConfig = (RelationshipConfig) relationshipInput.getInputTypeConfig();
            final Relationship relationship = relationshipFactory.create( addedReference, relationshipConfig.getRelationshipType() );
            relationshipsToAddBuilder.add( relationship );
        }
    }

    private List<Property> resolveAddedReferences()
    {
        final List<Property> addedReferences = new ArrayList<>();
        for ( Map.Entry<EntryPath, Property> referenceAfterEditing : referencesAfterEditing.entrySet() )
        {
            if ( !referencesBeforeEditing.containsKey( referenceAfterEditing.getKey() ) )
            {
                addedReferences.add( referenceAfterEditing.getValue() );
            }
        }
        return addedReferences;
    }

    private List<Property> resolveRemovedReferences()
    {
        final List<Property> removedReferences = new ArrayList<>();
        for ( Map.Entry<EntryPath, Property> referenceBeforeEditing : referencesBeforeEditing.entrySet() )
        {
            if ( !referencesAfterEditing.containsKey( referenceBeforeEditing.getKey() ) )
            {
                removedReferences.add( referenceBeforeEditing.getValue() );
            }
        }
        return removedReferences;
    }

    private Map<EntryPath, Property> resolveReferences( final RootDataSet rootDataSet )
    {
        final Map<EntryPath, Property> references = new LinkedHashMap<>();
        final DataVisitor dataVisitor = new DataVisitor()
        {
            @Override
            public void visit( final Property reference )
            {
                references.put( reference.getPath(), reference );
            }
        };
        dataVisitor.restrictType( PropertyTypes.CONTENT_ID );
        dataVisitor.traverse( rootDataSet );
        return references;
    }
}
