package com.enonic.wem.api.content.editor;


import org.junit.Test;

import com.enonic.wem.api.content.Content;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class CompositeEditorTest
{

    @Test
    public void given_source_which_equals_toBeEdited_when_edit_then_null_is_returned()
        throws Exception
    {
        // setup
        SetContentDisplayNameEditor setContentDisplayNameEditor = new SetContentDisplayNameEditor( "Unchanged Display Name" );
        SetContentNameEditor setContentNameEditor = new SetContentNameEditor( "unchangedName" );

        Content toBeEdited = Content.newContent().name( "unchangedName" ).displayName( "Unchanged Display Name" ).build();

        // exercise
        Content editedContent = new CompositeEditor( setContentDisplayNameEditor, setContentNameEditor ).edit( toBeEdited );

        // verify
        assertNull( editedContent );
    }

    @Test
    public void given_source_which_not_equals_toBeEdited_when_edit_then_updated_content_is_returned()
        throws Exception
    {
        // setup
        SetContentDisplayNameEditor setContentDisplayNameEditor = new SetContentDisplayNameEditor( "Changed Display Name" );
        SetContentNameEditor setContentNameEditor = new SetContentNameEditor( "changedName" );

        Content toBeEdited = Content.newContent().name( "name" ).displayName( "Display Name" ).build();

        // exercise
        Content editedContent = new CompositeEditor( setContentDisplayNameEditor, setContentNameEditor ).edit( toBeEdited );

        // verify
        assertNotNull( editedContent );
        assertEquals( "changedName", editedContent.getName() );
        assertEquals( "Changed Display Name", editedContent.getDisplayName() );
    }
}
