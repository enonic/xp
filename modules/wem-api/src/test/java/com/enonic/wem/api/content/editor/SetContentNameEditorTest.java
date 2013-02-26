package com.enonic.wem.api.content.editor;


import org.junit.Test;

import com.enonic.wem.api.content.Content;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class SetContentNameEditorTest
{

    @Test
    public void given_source_which_equals_toBeEdited_when_edit_then_null_is_returned()
        throws Exception
    {
        // setup
        SetContentNameEditor editor = new SetContentNameEditor( "unchangedName" );
        Content toBeEdited = Content.newContent().name( "unchangedName" ).build();

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
        SetContentNameEditor editor = new SetContentNameEditor( "changedName" );
        Content toBeEdited = Content.newContent().name( "name" ).build();

        // exercise
        Content updatedContent = editor.edit( toBeEdited );

        // verify
        assertNotNull( updatedContent );
        assertEquals( "changedName", updatedContent.getName() );
    }
}
