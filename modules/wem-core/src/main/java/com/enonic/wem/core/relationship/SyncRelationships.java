package com.enonic.wem.core.relationship;

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
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItemPath;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.RelationshipConfig;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipKey;

import static com.enonic.wem.api.relationship.RelationshipKey.newRelationshipKey;
import static com.enonic.wem.core.relationship.RelationshipFactory.newRelationshipFactory;

class SyncRelationships
{
    private final Form form;

    private final ContentId contentToUpdate;

    private final Map<DataPath, Property> referencesBeforeEditing;

    private final Map<DataPath, Property> referencesAfterEditing;

    private final ImmutableList.Builder<Relationship> relationshipsToAddBuilder = ImmutableList.builder();

    private ImmutableList<Relationship> relationshipsToAdd;

    private final ImmutableList.Builder<RelationshipKey> relationshipsToDeleteBuilder = ImmutableList.builder();

    private ImmutableList<RelationshipKey> relationshipsToDelete;

    SyncRelationships( final Form form, final ContentId contentToUpdate, final ContentData contentBeforeEditing,
                       final ContentData contentAfterEditing )
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
            if ( relationshipConfig != null )
            {
                final Relationship relationship = relationshipFactory.create( addedReference, relationshipConfig.getRelationshipType() );
                relationshipsToAddBuilder.add( relationship );
            }
        }
    }

    private List<Property> resolveAddedReferences()
    {
        final List<Property> addedReferences = new ArrayList<>();
        for ( Map.Entry<DataPath, Property> referenceAfterEditing : referencesAfterEditing.entrySet() )
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
        for ( Map.Entry<DataPath, Property> referenceBeforeEditing : referencesBeforeEditing.entrySet() )
        {
            if ( !referencesAfterEditing.containsKey( referenceBeforeEditing.getKey() ) )
            {
                removedReferences.add( referenceBeforeEditing.getValue() );
            }
        }
        return removedReferences;
    }

    private Map<DataPath, Property> resolveReferences( final ContentData contentData )
    {
        final Map<DataPath, Property> references = new LinkedHashMap<>();
        final PropertyVisitor propertyVisitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property reference )
            {
                references.put( reference.getPath(), reference );
            }
        };
        propertyVisitor.restrictType( ValueTypes.CONTENT_ID );
        propertyVisitor.traverse( contentData );
        return references;
    }
}
