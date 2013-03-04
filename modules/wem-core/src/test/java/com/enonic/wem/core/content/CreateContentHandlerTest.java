package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;
import com.enonic.wem.core.index.IndexService;

import static junit.framework.Assert.assertNotNull;

public class CreateContentHandlerTest
    extends AbstractCommandHandlerTest
{
    private static final DateTime CREATED_TIME = new DateTime( 2013, 1, 1, 12, 0, 0, 0 );

    private CreateContentHandler handler;

    private ContentDao contentDao;

    private RelationshipDao relationshipDao;

    private IndexService indexService;


    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );

        super.initialize();

        contentDao = Mockito.mock( ContentDao.class );
        relationshipDao = Mockito.mock( RelationshipDao.class );
        indexService = Mockito.mock( IndexService.class );

        handler = new CreateContentHandler();
        handler.setContentDao( contentDao );
        handler.setRelationshipDao( relationshipDao );
        handler.setIndexService( indexService );

        Mockito.when( super.client.execute( Mockito.isA( ValidateRootDataSet.class ) ) ).thenReturn( DataValidationErrors.empty() );
    }

    @Test
    public void createContent()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( CREATED_TIME.getMillis() );

        Mockito.when( contentDao.create( Mockito.isA( Content.class ), Mockito.any( Session.class ) ) ).thenReturn(
            ContentId.from( "100" ) );

        CreateContent command = Commands.content().create();
        command.displayName( "My Content" );
        command.contentPath( ContentPath.from( "myContent" ) );
        command.owner( UserKey.from( "myStore:myUser" ) );
        command.contentType( new QualifiedContentTypeName( ModuleName.SYSTEM, "MyContentType" ) );
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setData( "myText", "abc" );
        rootDataSet.setData( "myReference", DataTypes.CONTENT_REFERENCE, "123" );
        rootDataSet.setData( "mySet.myRelatedContent", DataTypes.CONTENT_REFERENCE, "124" );
        command.rootDataSet( rootDataSet );

        // exercise
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( contentDao, Mockito.atLeastOnce() ).create( Mockito.isA( Content.class ), Mockito.any( Session.class ) );
        Mockito.verify( indexService, Mockito.atLeastOnce() ).indexContent( Mockito.isA( Content.class ) );

        Relationship createdRelationship1 = Relationship.newRelationship().
            creator( AccountKey.anonymous() ).
            createdTime( CREATED_TIME ).
            type( QualifiedRelationshipTypeName.DEFAULT ).
            fromContent( ContentId.from( "100" ) ).
            toContent( ContentId.from( "123" ) ).
            managed( EntryPath.from( "myReference" ) ).
            build();
        Mockito.verify( relationshipDao, Mockito.times( 1 ) ).create( Mockito.refEq( createdRelationship1 ), Mockito.any( Session.class ) );

        Relationship createdRelationship2 = Relationship.newRelationship().
            creator( AccountKey.anonymous() ).
            createdTime( CREATED_TIME ).
            type( QualifiedRelationshipTypeName.DEFAULT ).
            fromContent( ContentId.from( "100" ) ).
            toContent( ContentId.from( "124" ) ).
            managed( EntryPath.from( "mySet.myRelatedContent" ) ).
            build();
        Mockito.verify( relationshipDao, Mockito.times( 1 ) ).create( Mockito.refEq( createdRelationship2 ), Mockito.any( Session.class ) );

        ContentId contentId = command.getResult();
        assertNotNull( contentId );
    }

}
