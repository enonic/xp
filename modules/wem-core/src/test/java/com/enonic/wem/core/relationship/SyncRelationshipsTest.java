package com.enonic.wem.core.relationship;


import org.junit.Test;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.data.ContentData;
import com.enonic.wem.api.data.data.DataPath;
import com.enonic.wem.api.data.data.DataSet;
import com.enonic.wem.api.data.data.Property;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.schema.content.form.Form;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.form.inputtype.RelationshipConfig;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static junit.framework.Assert.assertEquals;

public class SyncRelationshipsTest
{

    private static final com.enonic.wem.api.content.ContentId CONTENT_TO_UPDATE = com.enonic.wem.api.content.ContentId.from( "100" );

    private static final RelationshipConfig RELATIONSHIP_CONFIG_LIKE =
        RelationshipConfig.newRelationshipConfig().relationshipType( QualifiedRelationshipTypeName.LIKE ).build();

    private static final ContentId CONTENT_ID_201 = ContentId.from( "201" );

    @Test
    public void given_before_as_null_and_after_with_one_ContentReference_when_invoke_then_one_relationship_to_add()
    {
        // setup
        Form form = Form.newForm().build();

        form.addFormItem(
            newInput().name( "myRelated" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( RELATIONSHIP_CONFIG_LIKE ).build() );

        ContentData after = new ContentData();
        after.add( new Property.ContentId( "myRelated", CONTENT_ID_201 ) );

        // exercise
        SyncRelationships syncRelationships = new SyncRelationships( form, CONTENT_TO_UPDATE, null, after );
        syncRelationships.invoke();

        // verify
        assertEquals( 0, syncRelationships.getRelationshipsToDelete().size() );
        assertEquals( 1, syncRelationships.getRelationshipsToAdd().size() );

        // verify: relationship
        Relationship relationship = syncRelationships.getRelationshipsToAdd().get( 0 );

        // verify: relationshipKey
        RelationshipKey relationshipKey = relationship.getKey();
        assertEquals( QualifiedRelationshipTypeName.LIKE, relationshipKey.getType() );
        assertEquals( DataPath.from( "myRelated" ), relationshipKey.getManagingData() );
        assertEquals( CONTENT_TO_UPDATE, relationshipKey.getFromContent() );
        assertEquals( CONTENT_ID_201, relationshipKey.getToContent() );
    }

    @Test
    public void given_before_with_no_ContentReference_and_after_with_one_ContentReference_when_invoke_then_one_relationship_to_add()
    {
        // setup
        Form form = Form.newForm().build();
        form.addFormItem(
            newInput().name( "myRelated" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( RELATIONSHIP_CONFIG_LIKE ).build() );

        ContentData before = new ContentData();
        ContentData after = new ContentData();
        after.add( new Property.ContentId( "myRelated", CONTENT_ID_201 ) );

        // exercise
        SyncRelationships syncRelationships = new SyncRelationships( form, CONTENT_TO_UPDATE, before, after );
        syncRelationships.invoke();

        // verify
        assertEquals( 0, syncRelationships.getRelationshipsToDelete().size() );
        assertEquals( 1, syncRelationships.getRelationshipsToAdd().size() );

        // verify: relationship
        Relationship relationship = syncRelationships.getRelationshipsToAdd().get( 0 );

        // verify: relationshipKey
        RelationshipKey relationshipKey = relationship.getKey();
        assertEquals( QualifiedRelationshipTypeName.LIKE, relationshipKey.getType() );
        assertEquals( DataPath.from( "myRelated" ), relationshipKey.getManagingData() );
        assertEquals( CONTENT_TO_UPDATE, relationshipKey.getFromContent() );
        assertEquals( CONTENT_ID_201, relationshipKey.getToContent() );
    }

    @Test
    public void given_before_with_no_ContentReference_and_after_with_one_ContentReference_inside_set_when_invoke_then_one_relationship_to_add()
    {
        // setup
        Form form = Form.newForm().build();
        FormItemSet myFormItemSet = FormItemSet.newFormItemSet().name( "mySet" ).label( "My set" ).build();
        myFormItemSet.add(
            newInput().name( "myRelated" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( RELATIONSHIP_CONFIG_LIKE ).build() );
        form.addFormItem( myFormItemSet );

        ContentData before = new ContentData();
        ContentData after = new ContentData();
        DataSet mySet = DataSet.newDataSet().name( "mySet" ).build();
        mySet.add( new Property.ContentId( "myRelated", CONTENT_ID_201 ) );
        after.add( mySet );

        // exercise
        SyncRelationships syncRelationships = new SyncRelationships( form, CONTENT_TO_UPDATE, before, after );
        syncRelationships.invoke();

        // verify
        assertEquals( 0, syncRelationships.getRelationshipsToDelete().size() );
        assertEquals( 1, syncRelationships.getRelationshipsToAdd().size() );

        // verify: relationship
        Relationship relationship = syncRelationships.getRelationshipsToAdd().get( 0 );

        // verify: relationshipKey
        RelationshipKey relationshipKey = relationship.getKey();
        assertEquals( QualifiedRelationshipTypeName.LIKE, relationshipKey.getType() );
        assertEquals( DataPath.from( "mySet.myRelated" ), relationshipKey.getManagingData() );
        assertEquals( CONTENT_TO_UPDATE, relationshipKey.getFromContent() );
        assertEquals( CONTENT_ID_201, relationshipKey.getToContent() );
    }

    @Test
    public void given_before_with_one_ContentReference_and_after_with_none_ContentReference_when_invoke_then_one_relationship_to_delete()
    {
        // setup
        Form form = Form.newForm().build();
        form.addFormItem(
            newInput().name( "myRelated" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( RELATIONSHIP_CONFIG_LIKE ).build() );

        ContentData before = new ContentData();
        ContentData after = new ContentData();
        before.add( new Property.ContentId( "myRelated", CONTENT_ID_201 ) );

        // exercise
        SyncRelationships syncRelationships = new SyncRelationships( form, CONTENT_TO_UPDATE, before, after );
        syncRelationships.invoke();

        // verify
        assertEquals( 0, syncRelationships.getRelationshipsToAdd().size() );
        assertEquals( 1, syncRelationships.getRelationshipsToDelete().size() );

        // verify: relationship
        RelationshipKey relationshipKey = syncRelationships.getRelationshipsToDelete().get( 0 );

        // verify: relationshipKey
        assertEquals( QualifiedRelationshipTypeName.LIKE, relationshipKey.getType() );
        assertEquals( DataPath.from( "myRelated" ), relationshipKey.getManagingData() );
        assertEquals( CONTENT_TO_UPDATE, relationshipKey.getFromContent() );
        assertEquals( CONTENT_ID_201, relationshipKey.getToContent() );
    }
}
