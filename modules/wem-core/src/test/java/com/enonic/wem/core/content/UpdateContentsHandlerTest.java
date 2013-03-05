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
import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.editor.ContentEditors;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;
import com.enonic.wem.core.index.IndexService;

public class UpdateContentsHandlerTest
    extends AbstractCommandHandlerTest
{
    private static final DateTime CREATED_TIME = new DateTime( 2013, 1, 1, 12, 0, 0, 0 );

    private static final DateTime UPDATED_TIME = new DateTime( 2013, 1, 1, 13, 0, 0, 0 );

    private UpdateContentsHandler handler;

    private ContentDao contentDao;

    private RelationshipDao relationshipDao;

    @Before
    public void before()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        contentDao = Mockito.mock( ContentDao.class );
        relationshipDao = Mockito.mock( RelationshipDao.class );
        IndexService indexService = Mockito.mock( IndexService.class );

        handler = new UpdateContentsHandler();
        handler.setContentDao( contentDao );
        handler.setRelationshipDao( relationshipDao );
        handler.setIndexService( indexService );

        Mockito.when( super.client.execute( Mockito.isA( ValidateRootDataSet.class ) ) ).thenReturn( DataValidationErrors.empty() );
    }

    @Test
    public void contentDao_update_not_invoked_when_nothing_is_changed()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.getMillis() );

        RootDataSet existingContentData = new RootDataSet();
        existingContentData.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "aaa" ).build() );

        Content existingContent = createContent( existingContentData );
        ContentPaths existingContentPaths = ContentPaths.from( existingContent.getPath() );

        Mockito.when( contentDao.select( Mockito.eq( existingContentPaths ), Mockito.any( Session.class ) ) ).thenReturn(
            Contents.from( existingContent ) );

        RootDataSet unchangedContentData = new RootDataSet();
        unchangedContentData.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "aaa" ).build() );

        UpdateContents command = new UpdateContents().
            modifier( AccountKey.superUser() ).
            selectors( ContentPaths.from( existingContent.getPath() ) ).
            editor( ContentEditors.setContentData( unchangedContentData ) );

        // exercise
        handler.handle( context, command );

        // verify
        Mockito.verify( contentDao, Mockito.times( 0 ) ).update( Mockito.any( Content.class ), Mockito.eq( true ),
                                                                 Mockito.any( Session.class ) );
    }

    @Test
    public void modifiedTime_updated_when_something_is_changed()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.getMillis() );

        RootDataSet existingContentData = new RootDataSet();
        existingContentData.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "aaa" ).build() );

        Content existingContent = createContent( existingContentData );
        ContentPaths existingContentPaths = ContentPaths.from( existingContent.getPath() );

        Mockito.when( contentDao.select( Mockito.eq( existingContentPaths ), Mockito.any( Session.class ) ) ).thenReturn(
            Contents.from( existingContent ) );

        RootDataSet changedContentData = new RootDataSet();
        changedContentData.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "bbb" ).build() );

        UpdateContents command = new UpdateContents().
            modifier( AccountKey.superUser() ).
            selectors( ContentPaths.from( existingContent.getPath() ) ).
            editor( ContentEditors.setContentData( changedContentData ) );

        // exercise
        handler.handle( context, command );

        // verify
        Content storedContent = Content.newContent( createContent( existingContentData ) ).
            modifiedTime( UPDATED_TIME ).
            modifier( AccountKey.superUser() ).
            rootDataSet( changedContentData ).
            build();
        Mockito.verify( contentDao, Mockito.times( 1 ) ).update( Mockito.refEq( storedContent ), Mockito.eq( true ),
                                                                 Mockito.any( Session.class ) );
    }

    @Test
    public void update_syncReferences_one_added()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.getMillis() );

        RootDataSet existingContentData = new RootDataSet();
        existingContentData.add( Data.newData().name( "myRelated1" ).type( DataTypes.CONTENT_REFERENCE ).value( "111" ).build() );
        existingContentData.add( Data.newData().name( "myRelated2" ).type( DataTypes.CONTENT_REFERENCE ).value( "222" ).build() );

        Content existingContent = createContent( existingContentData );
        ContentPaths existingContentPaths = ContentPaths.from( existingContent.getPath() );

        Mockito.when( contentDao.select( Mockito.eq( existingContentPaths ), Mockito.any( Session.class ) ) ).thenReturn(
            Contents.from( existingContent ) );

        RootDataSet changedContentData = new RootDataSet();
        changedContentData.add( Data.newData().name( "myRelated1" ).type( DataTypes.CONTENT_REFERENCE ).value( "111" ).build() );
        changedContentData.add( Data.newData().name( "myRelated2" ).type( DataTypes.CONTENT_REFERENCE ).value( "222" ).build() );
        changedContentData.add( Data.newData().name( "myRelated3" ).type( DataTypes.CONTENT_REFERENCE ).value( "333" ).build() );

        UpdateContents command = new UpdateContents().
            modifier( AccountKey.superUser() ).
            selectors( ContentPaths.from( existingContent.getPath() ) ).
            editor( ContentEditors.setContentData( changedContentData ) );

        // exercise
        handler.handle( context, command );

        // verify
        Relationship createdRelationship = Relationship.newRelationship().
            creator( AccountKey.anonymous() ).
            createdTime( UPDATED_TIME ).
            type( QualifiedRelationshipTypeName.DEFAULT ).
            fromContent( ContentId.from( "1" ) ).
            toContent( ContentId.from( "333" ) ).
            managed( EntryPath.from( "myRelated3" ) ).
            build();
        Mockito.verify( relationshipDao, Mockito.times( 1 ) ).create( Mockito.refEq( createdRelationship ), Mockito.any( Session.class ) );
    }

    private Content createContent( final RootDataSet rootDataSet )
    {
        return Content.newContent().
            id( ContentId.from( "1" ) ).
            name( "myContent" ).
            createdTime( CREATED_TIME ).
            displayName( "MyContent" ).
            owner( UserKey.superUser() ).
            rootDataSet( rootDataSet ).
            build();
    }
}
