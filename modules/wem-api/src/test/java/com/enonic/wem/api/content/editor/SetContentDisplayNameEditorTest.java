package com.enonic.wem.api.content.editor;


import org.junit.Test;

import com.enonic.wem.api.content.Content;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class SetContentDisplayNameEditorTest
{

    @Test
    public void given_source_which_equals_toBeEdited_when_edit_then_null_is_returned()
        throws Exception
    {
        // setup
        SetContentDisplayNameEditor editor = new SetContentDisplayNameEditor( "Unchanged Display Name" );
        Content toBeEdited = Content.newContent().name( "myContent" ).displayName( "Unchanged Display Name" ).build();

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
        SetContentDisplayNameEditor editor = new SetContentDisplayNameEditor( "Changed Display Name" );
        Content toBeEdited = Content.newContent().name( "myContent" ).displayName( "Unchanged Display Name" ).build();

        // exercise
        Content updatedContent = editor.edit( toBeEdited );

        // verify
        assertNotNull( updatedContent );
        assertEquals( "Changed Display Name", updatedContent.getDisplayName() );
    }
}
