package com.enonic.wem.core.relationship;


import javax.jcr.Session;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypeConfig;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.form.inputtype.RelationshipConfig;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.core.relationship.dao.RelationshipDao;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;

public class RelationshipServiceImplTest
{

    private static final DateTime NOW = new DateTime( 2013, 1, 1, 13, 0, 0, 0 );

    private RelationshipDao relationshipDao;

    private RelationshipServiceImpl relationshipService;

    private Session jcrSession;

    private ContentTypeService contentTypeService;

    @Before
    public void before()
    {
        jcrSession = Mockito.mock( Session.class );
        relationshipDao = Mockito.mock( RelationshipDao.class );
        contentTypeService = Mockito.mock( ContentTypeService.class );

        relationshipService = new RelationshipServiceImpl();
        relationshipService.setRelationshipDao( relationshipDao );
        relationshipService.setContentTypeService( contentTypeService );
    }

    @Test
    public void update_syncReferences_one_added()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( NOW.getMillis() );

        // setup: content type
        InputTypeConfig inputTypeConfig =
            RelationshipConfig.newRelationshipConfig().relationshipType( RelationshipTypeName.DEFAULT ).build();

        Form form = Form.newForm().
            addFormItem( newInput().name( "myRelated1" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( inputTypeConfig ).build() ).
            addFormItem( newInput().name( "myRelated2" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( inputTypeConfig ).build() ).
            addFormItem(
                newInput().name( "myRelated3" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( inputTypeConfig ).build() ).build();
        ContentType contentType = newContentType().name( "my_type" ).form( form ).build();

        Mockito.when( contentTypeService.getByNames( Mockito.any( GetContentTypesParams.class ) ) ).thenReturn( ContentTypes.from( contentType ) );

        // setup: content before editing
        ContentData dataBefore = new ContentData();
        dataBefore.add( new Property.ContentId( "myRelated1", ContentId.from( "111" ) ) );
        dataBefore.add( new Property.ContentId( "myRelated2", ContentId.from( "222" ) ) );

        // setup: content after editing
        ContentData dataAfter = new ContentData();
        dataAfter.add( new Property.ContentId( "myRelated1", ContentId.from( "111" ) ) );
        dataAfter.add( new Property.ContentId( "myRelated2", ContentId.from( "222" ) ) );
        dataAfter.add( new Property.ContentId( "myRelated3", ContentId.from( "333" ) ) );

        // exercise
        SyncRelationshipsCommand command = new SyncRelationshipsCommand();
        command.jcrSession( jcrSession );
        command.contentType( ContentTypeName.from( "my_relations" ) );
        command.contentToUpdate( ContentId.from( "1" ) );
        command.contentBeforeEditing( dataBefore );
        command.contentAfterEditing( dataAfter );
        relationshipService.syncRelationships( command );

        // verify
        Relationship createdRelationship = Relationship.newRelationship().
            creator( AccountKey.anonymous() ).
            createdTime( NOW ).
            type( RelationshipTypeName.DEFAULT ).
            fromContent( ContentId.from( "1" ) ).
            toContent( ContentId.from( "333" ) ).
            managed( DataPath.from( "myRelated3" ) ).
            build();
        Mockito.verify( relationshipDao, Mockito.times( 1 ) ).create( Mockito.refEq( createdRelationship ), Mockito.any( Session.class ) );
    }

}
