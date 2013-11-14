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
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.UpdateContentResult;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.command.schema.content.GetContentType;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.relationship.RelationshipService;

import static com.enonic.wem.api.content.Content.editContent;
import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class UpdateContentHandlerTest
    extends AbstractCommandHandlerTest
{
    private static final DateTime CREATED_TIME = new DateTime( 2013, 1, 1, 12, 0, 0, 0 );

    private static final DateTime UPDATED_TIME = new DateTime( 2013, 1, 1, 13, 0, 0, 0 );

    private UpdateContentHandler handler;

    private ContentDao contentDao;

    private RelationshipService relationshipService;

    @Before
    public void before()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        contentDao = Mockito.mock( ContentDao.class );
        relationshipService = Mockito.mock( RelationshipService.class );
        IndexService indexService = Mockito.mock( IndexService.class );

        handler = new UpdateContentHandler();
        handler.setContext( this.context );
        handler.setContentDao( contentDao );
        handler.setRelationshipService( relationshipService );
        handler.setIndexService( indexService );

        Mockito.when( super.client.execute( isA( ValidateContentData.class ) ) ).thenReturn( DataValidationErrors.empty() );

        final ContentTypeName myContentTypeName = ContentTypeName.from( "my_content_type" );
        final ContentType myContentType = newContentType().
            name( myContentTypeName ).
            superType( ContentTypeName.structured() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetContentType.class ) ) ).thenReturn( myContentType );

    }

    @Test
    public void given_content_not_found_when_handle_then_NOT_FOUND_is_returned()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.getMillis() );

        ContentData existingContentData = new ContentData();
        existingContentData.add( new Property.String( "myData", "aaa" ) );

        Mockito.when( contentDao.select( eq( ContentPath.from( "myContent" ) ), Mockito.any( Session.class ) ) ).thenReturn( null );

        final ContentData unchangedContentData = new ContentData();
        unchangedContentData.add( new Property.String( "myData", "aaa" ) );

        UpdateContent command = new UpdateContent().
            modifier( AccountKey.superUser() ).
            selector( ContentPath.from( "myContent" ) ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).contentData( unchangedContentData );
                }
            } );

        // exercise
        this.handler.setCommand( command );
        handler.handle();

        // verify
        UpdateContentResult result = command.getResult();
        assertEquals( UpdateContentResult.Type.NOT_FOUND, result.getType() );
        Mockito.verify( contentDao, Mockito.times( 0 ) ).update( Mockito.any( Content.class ), eq( true ), Mockito.any( Session.class ) );
    }

    @Test
    public void contentDao_update_not_invoked_when_nothing_is_changed()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.getMillis() );

        ContentData existingContentData = new ContentData();
        existingContentData.add( new Property.String( "myData", "aaa" ) );

        Content existingContent = createContent( existingContentData );

        Mockito.when( contentDao.select( eq( existingContent.getPath() ), Mockito.any( Session.class ) ) ).thenReturn( existingContent );

        final ContentData unchangedContentData = new ContentData();
        unchangedContentData.add( new Property.String( "myData", "aaa" ) );

        UpdateContent command = new UpdateContent().
            modifier( AccountKey.superUser() ).
            selector( existingContent.getPath() ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).contentData( unchangedContentData );
                }
            } );

        // exercise
        this.handler.setCommand( command );
        handler.handle();

        // verify
        Mockito.verify( contentDao, Mockito.times( 0 ) ).update( Mockito.any( Content.class ), eq( true ), Mockito.any( Session.class ) );
    }

    @Test
    public void modifiedTime_updated_when_something_is_changed()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.getMillis() );

        ContentData existingContentData = new ContentData();
        existingContentData.add( new Property.String( "myData", "aaa" ) );

        Content existingContent = createContent( existingContentData );

        Mockito.when( contentDao.select( eq( existingContent.getPath() ), Mockito.any( Session.class ) ) ).thenReturn( existingContent );

        final ContentData changedContentData = new ContentData();
        changedContentData.add( new Property.String( "myData", "bbb" ) );

        UpdateContent command = new UpdateContent().
            modifier( AccountKey.superUser() ).
            selector( existingContent.getPath() ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).contentData( changedContentData );
                }
            } );

        // exercise
        this.handler.setCommand( command );
        handler.handle();

        // verify
        Content storedContent = newContent( createContent( existingContentData ) ).
            modifiedTime( UPDATED_TIME ).
            modifier( AccountKey.superUser() ).
            contentData( changedContentData ).
            build();

        Mockito.verify( contentDao, Mockito.times( 1 ) ).update( Mockito.refEq( storedContent ), eq( true ), Mockito.any( Session.class ) );
    }

    private Content createContent( final ContentData contentData )
    {
        return newContent().
            id( ContentId.from( "1" ) ).
            name( "myContent" ).
            createdTime( CREATED_TIME ).
            displayName( "MyContent" ).
            owner( UserKey.superUser() ).
            contentData( contentData ).
            build();
    }
}
