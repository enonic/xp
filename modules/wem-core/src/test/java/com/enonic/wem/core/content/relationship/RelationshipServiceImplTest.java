package com.enonic.wem.core.content.relationship;


import javax.jcr.Session;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.content.schema.content.GetContentTypes;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.form.Form;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypeConfig;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.content.schema.content.form.inputtype.RelationshipConfig;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;

import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;

public class RelationshipServiceImplTest
{

    private static final DateTime NOW = new DateTime( 2013, 1, 1, 13, 0, 0, 0 );

    private RelationshipDao relationshipDao;

    private RelationshipServiceImpl relationshipService;

    private Session jcrSession;

    private Client client;

    @Before
    public void before()
    {
        client = Mockito.mock( Client.class );
        jcrSession = Mockito.mock( Session.class );
        relationshipDao = Mockito.mock( RelationshipDao.class );

        relationshipService = new RelationshipServiceImpl();
        relationshipService.setRelationshipDao( relationshipDao );
    }

    @Test
    public void update_syncReferences_one_added()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( NOW.getMillis() );

        // setup: content type
        InputTypeConfig inputTypeConfig =
            RelationshipConfig.newRelationshipConfig().relationshipType( QualifiedRelationshipTypeName.DEFAULT ).build();

        Form form = Form.newForm().
            addFormItem( newInput().name( "myRelated1" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( inputTypeConfig ).build() ).
            addFormItem( newInput().name( "myRelated2" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( inputTypeConfig ).build() ).
            addFormItem(
                newInput().name( "myRelated3" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( inputTypeConfig ).build() ).build();
        ContentType contentType = newContentType().name( "myType" ).module( ModuleName.SYSTEM ).form( form ).build();

        Mockito.when( client.execute( Mockito.any( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( contentType ) );

        // setup: content before editing
        RootDataSet dataBefore = new RootDataSet();
        dataBefore.add( Data.newData().name( "myRelated1" ).type( DataTypes.CONTENT_REFERENCE ).value( ContentId.from( "111" ) ).build() );
        dataBefore.add( Data.newData().name( "myRelated2" ).type( DataTypes.CONTENT_REFERENCE ).value( ContentId.from( "222" ) ).build() );

        // setup: content after editing
        RootDataSet dataAfter = new RootDataSet();
        dataAfter.add( Data.newData().name( "myRelated1" ).type( DataTypes.CONTENT_REFERENCE ).value( ContentId.from( "111" ) ).build() );
        dataAfter.add( Data.newData().name( "myRelated2" ).type( DataTypes.CONTENT_REFERENCE ).value( ContentId.from( "222" ) ).build() );
        dataAfter.add( Data.newData().name( "myRelated3" ).type( DataTypes.CONTENT_REFERENCE ).value( ContentId.from( "333" ) ).build() );

        // exercise
        SyncRelationshipsCommand command = new SyncRelationshipsCommand();
        command.client( client );
        command.jcrSession( jcrSession );
        command.contentType( QualifiedContentTypeName.from( ModuleName.SYSTEM, "myRelations" ) );
        command.contentToUpdate( ContentId.from( "1" ) );
        command.contentBeforeEditing( dataBefore );
        command.contentAfterEditing( dataAfter );
        relationshipService.syncRelationships( command );

        // verify
        Relationship createdRelationship = Relationship.newRelationship().
            creator( AccountKey.anonymous() ).
            createdTime( NOW ).
            type( QualifiedRelationshipTypeName.DEFAULT ).
            fromContent( ContentId.from( "1" ) ).
            toContent( ContentId.from( "333" ) ).
            managed( EntryPath.from( "myRelated3" ) ).
            build();
        Mockito.verify( relationshipDao, Mockito.times( 1 ) ).create( Mockito.refEq( createdRelationship ), Mockito.any( Session.class ) );
    }

}
