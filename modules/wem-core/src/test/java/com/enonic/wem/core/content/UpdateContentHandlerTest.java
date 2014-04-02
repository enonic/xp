package com.enonic.wem.core.content;


import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.command.content.attachment.AttachmentService;
import com.enonic.wem.api.command.schema.content.GetContentType;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.relationship.RelationshipService;

import static com.enonic.wem.api.content.Content.editContent;
import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static org.mockito.Matchers.isA;

public class UpdateContentHandlerTest
    extends AbstractCommandHandlerTest
{
    private static final DateTime CREATED_TIME = new DateTime( 2013, 1, 1, 12, 0, 0, 0 );

    private static final DateTime UPDATED_TIME = new DateTime( 2013, 1, 1, 13, 0, 0, 0 );

    private UpdateContentHandler handler;


    private RelationshipService relationshipService;

    @Before
    public void before()
        throws Exception
    {
        super.initialize();

        relationshipService = Mockito.mock( RelationshipService.class );
        AttachmentService attachmentService = Mockito.mock( AttachmentService.class );

        handler = new UpdateContentHandler();
        handler.setContext( this.context );
        handler.setAttachmentService( attachmentService );

        Mockito.when( client.execute( isA( ValidateContentData.class ) ) ).thenReturn( DataValidationErrors.empty() );

        final ContentTypeName myContentTypeName = ContentTypeName.from( "my_content_type" );
        final ContentType myContentType = newContentType().
            name( myContentTypeName ).
            superType( ContentTypeName.structured() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetContentType.class ) ) ).thenReturn( myContentType );

    }


    @Ignore // Rewriting content stuff to node
    @Test(expected = ContentNotFoundException.class)
    public void given_content_not_found_when_handle_then_NOT_FOUND_is_returned()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.getMillis() );

        ContentData existingContentData = new ContentData();
        existingContentData.add( new Property.String( "myData", "aaa" ) );

        final ContentData unchangedContentData = new ContentData();
        unchangedContentData.add( new Property.String( "myData", "aaa" ) );

        UpdateContent command = new UpdateContent().
            modifier( AccountKey.superUser() ).
            contentId( ContentId.from( "mycontent" ) ).
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
    }


    @Ignore // Rewriting content stuff to node
    @Test
    public void contentDao_update_not_invoked_when_nothing_is_changed()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.getMillis() );

        ContentData existingContentData = new ContentData();
        existingContentData.add( new Property.String( "myData", "aaa" ) );

        Content existingContent = createContent( existingContentData );

        final ContentData unchangedContentData = new ContentData();
        unchangedContentData.add( new Property.String( "myData", "aaa" ) );

        UpdateContent command = new UpdateContent().
            modifier( AccountKey.superUser() ).
            contentId( existingContent.getId() ).
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
    }

    @Ignore // Rewriting content stuff to node
    @Test
    public void modifiedTime_updated_when_something_is_changed()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.getMillis() );

        ContentData existingContentData = new ContentData();
        existingContentData.add( new Property.String( "myData", "aaa" ) );

        Content existingContent = createContent( existingContentData );

        final ContentData changedContentData = new ContentData();
        changedContentData.add( new Property.String( "myData", "bbb" ) );

        UpdateContent command = new UpdateContent().
            modifier( AccountKey.superUser() ).
            contentId( existingContent.getId() ).
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
    }

    private Content createContent( final ContentData contentData )
    {
        return newContent().
            id( ContentId.from( "1" ) ).
            parentPath( ContentPath.ROOT ).
            name( "mycontent" ).
            createdTime( CREATED_TIME ).
            displayName( "MyContent" ).
            owner( UserKey.superUser() ).
            contentData( contentData ).
            build();
    }
}
