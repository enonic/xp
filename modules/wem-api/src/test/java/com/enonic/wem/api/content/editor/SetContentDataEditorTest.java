package com.enonic.wem.api.content.editor;


import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class SetContentDataEditorTest
{
    @Test
    public void given_source_which_equals_toBeEdited_when_edit_then_null_is_returned()
        throws Exception
    {
        // setup
        ContentData originalContentData = new ContentData();
        originalContentData.add( new Property.String( "myData", "abc" ) );

        ContentData unchangedContentData = new ContentData();
        unchangedContentData.add( new Property.String( "myData", "abc" ) );

        SetContentDataEditor editor = new SetContentDataEditor( unchangedContentData );
        Content toBeEdited = Content.newContent().name( "myContent" ).contentData( originalContentData ).build();

        // exercise
        Content updatedContent = editor.edit( toBeEdited );

        // verify
        assertNull( updatedContent );
    }

    @Test
    public void given_source_which_not_equals_toBeEdited_when_edit_then_updated_content_is_returned()
        throws Exception
    {
        // setup
        ContentData originalContentData = new ContentData();
        originalContentData.add( new Property.String( "myData", "abc" ) );

        ContentData changedContentData = new ContentData();
        changedContentData.add( new Property.String( "myData", "123" ) );

        SetContentDataEditor editor = new SetContentDataEditor( changedContentData );
        Content toBeEdited = Content.newContent().name( "myContent" ).contentData( originalContentData ).build();

        // exercise
        Content updatedContent = editor.edit( toBeEdited );

        // verify
        assertNotNull( updatedContent );
        assertEquals( "123", updatedContent.getContentData().getProperty( "myData" ).getString() );
    }

}
