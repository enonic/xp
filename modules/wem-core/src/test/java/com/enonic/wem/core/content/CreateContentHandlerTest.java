package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.CreateContentResult;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.command.schema.content.GetContentType;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.relationship.RelationshipService;
import com.enonic.wem.core.relationship.SyncRelationshipsCommand;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class CreateContentHandlerTest
    extends AbstractCommandHandlerTest
{
    private static final DateTime CREATED_TIME = new DateTime( 2013, 1, 1, 12, 0, 0, 0 );

    private CreateContentHandler handler;

    private ContentDao contentDao;

    private RelationshipService relationshipService;

    private IndexService indexService;


    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );

        super.initialize();

        contentDao = Mockito.mock( ContentDao.class );
        relationshipService = Mockito.mock( RelationshipService.class );
        indexService = Mockito.mock( IndexService.class );

        handler = new CreateContentHandler();
        handler.setContext( this.context );
        handler.setContentDao( contentDao );
        handler.setRelationshipService( relationshipService );
        handler.setIndexService( indexService );

        Mockito.when( super.client.execute( Mockito.isA( ValidateContentData.class ) ) ).thenReturn( DataValidationErrors.empty() );

        final ContentTypeName myContentTypeName = ContentTypeName.from( "my_content_type" );
        final ContentType myContentType = newContentType().
            name( myContentTypeName ).
            superType( ContentTypeName.structured() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetContentType.class ) ) ).thenReturn( myContentType );

        DateTimeUtils.setCurrentMillisFixed( CREATED_TIME.getMillis() );
    }

    @AfterClass
    public static void tearDown()
    {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void createContent()
        throws Exception
    {
        // setup
        Mockito.when( contentDao.create( Mockito.isA( Content.class ), Mockito.any( Session.class ) ) ).thenReturn(
            com.enonic.wem.api.content.ContentId.from( "100" ) );

        CreateContent command = Commands.content().create();
        command.displayName( "My Content" );
        command.parent( ContentPath.from( "/" ) );
        command.owner( UserKey.from( "myStore:myUser" ) );
        command.contentType( ContentTypeName.from( "my_content_type" ) );
        ContentData contentData = new ContentData();
        contentData.setProperty( "myText", new Value.String( "abc" ) );
        contentData.setProperty( "myReference", new Value.ContentId( ContentId.from( "123" ) ) );
        contentData.setProperty( "mySet.myRelatedContent", new Value.ContentId( ContentId.from( "124" ) ) );
        command.contentData( contentData );

        Mockito.when( client.execute( Mockito.isA( GetContentType.class ) ) ).thenReturn(
            ContentType.newContentType().name( "my_content_type" ).build() );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( contentDao, Mockito.times( 1 ) ).create( Mockito.isA( Content.class ), Mockito.any( Session.class ) );
        Mockito.verify( indexService, Mockito.times( 1 ) ).indexContent( Mockito.isA( Content.class ) );
        Mockito.verify( relationshipService, Mockito.times( 1 ) ).syncRelationships( Mockito.isA( SyncRelationshipsCommand.class ) );

        final CreateContentResult result = command.getResult();
        assertNotNull( result );
    }

    @Test
    public void createContent_generated_path()
        throws Exception
    {
        // setup
        Mockito.when( contentDao.create( Mockito.isA( Content.class ), Mockito.any( Session.class ) ) ).thenReturn(
            com.enonic.wem.api.content.ContentId.from( "100" ) );

        CreateContent command = Commands.content().create();
        final String displayName = "My Content";
        command.displayName( displayName );
        final String rootPath = "/rootcontent";
        command.parent( ContentPath.from( rootPath ) );
        command.owner( UserKey.from( "myStore:myUser" ) );
        command.contentType( ContentTypeName.from( "my_content_type" ) );

        Mockito.when( client.execute( Mockito.isA( GetContentType.class ) ) ).thenReturn(
            ContentType.newContentType().name( "my_content_type" ).build() );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( contentDao, Mockito.times( 1 ) ).create( Mockito.isA( Content.class ), Mockito.any( Session.class ) );
        Mockito.verify( indexService, Mockito.times( 1 ) ).indexContent( Mockito.isA( Content.class ) );
        Mockito.verify( relationshipService, Mockito.times( 1 ) ).syncRelationships( Mockito.isA( SyncRelationshipsCommand.class ) );

        final CreateContentResult result = command.getResult();
        assertNotNull( result );
        assertEquals( "/rootcontent/" + new ContentPathNameGenerator().generatePathName( displayName ),
                      result.getContentPath().toString() );
    }

}
