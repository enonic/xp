package com.enonic.wem.core.content;


import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.ValidateContentData;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

import static com.enonic.wem.api.content.Content.editContent;
import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static org.mockito.Matchers.isA;

public class UpdateContentCommandTest
{
    private static final DateTime CREATED_TIME = new DateTime( 2013, 1, 1, 12, 0, 0, 0 );

    private static final DateTime UPDATED_TIME = new DateTime( 2013, 1, 1, 13, 0, 0, 0 );

    private UpdateContentCommand command;

    @Before
    public void before()
        throws Exception
    {
        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );
        final AttachmentService attachmentService = Mockito.mock( AttachmentService.class );
        final ContentService contentService = Mockito.mock( ContentService.class );

        command = new UpdateContentCommand();
        command.contentTypeService( contentTypeService );
        command.attachmentService( attachmentService );

        Mockito.when( contentService.validate( isA( ValidateContentData.class ) ) ).thenReturn( DataValidationErrors.empty() );

        final ContentTypeName myContentTypeName = ContentTypeName.from( "my_content_type" );
        final ContentType myContentType = newContentType().
            name( myContentTypeName ).
            superType( ContentTypeName.structured() ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( myContentType );
    }

    @Ignore // Rewriting content stuff to node
    @Test(expected = ContentNotFoundException.class)
    public void given_content_not_found_when_handle_then_NOT_FOUND_is_returned()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.getMillis() );

        ContentData existingContentData = new ContentData();
        existingContentData.add( Property.newString( "myData", "aaa" ) );

        final ContentData unchangedContentData = new ContentData();
        unchangedContentData.add( Property.newString( "myData", "aaa" ) );

        UpdateContentParams params = new UpdateContentParams().
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
        this.command.execute();
    }


    @Ignore // Rewriting content stuff to node
    @Test
    public void contentDao_update_not_invoked_when_nothing_is_changed()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( UPDATED_TIME.getMillis() );

        ContentData existingContentData = new ContentData();
        existingContentData.add( Property.newString( "myData", "aaa" ) );

        Content existingContent = createContent( existingContentData );

        final ContentData unchangedContentData = new ContentData();
        unchangedContentData.add( Property.newString( "myData", "aaa" ) );

        UpdateContentParams params = new UpdateContentParams().
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
        this.command.execute();

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
        existingContentData.add( Property.newString( "myData", "aaa" ) );

        Content existingContent = createContent( existingContentData );

        final ContentData changedContentData = new ContentData();
        changedContentData.add( Property.newString( "myData", "bbb" ) );

        UpdateContentParams params = new UpdateContentParams().
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
        this.command.execute();

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
