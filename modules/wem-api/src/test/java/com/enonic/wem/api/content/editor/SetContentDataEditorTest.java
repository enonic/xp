package com.enonic.wem.api.content.editor;


import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.PropertyTypes;

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
        RootDataSet originalRootDataSet = new RootDataSet();
        originalRootDataSet.add( Property.newProperty().name( "myData" ).type( PropertyTypes.TEXT ).value( "abc" ).build() );

        RootDataSet unchangedRootDataSet = new RootDataSet();
        unchangedRootDataSet.add( Property.newProperty().name( "myData" ).type( PropertyTypes.TEXT ).value( "abc" ).build() );

        SetContentDataEditor editor = new SetContentDataEditor( unchangedRootDataSet );
        Content toBeEdited = Content.newContent().name( "myContent" ).rootDataSet( originalRootDataSet ).build();

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
        RootDataSet originalRootDataSet = new RootDataSet();
        originalRootDataSet.add( Property.newProperty().name( "myData" ).type( PropertyTypes.TEXT ).value( "abc" ).build() );

        RootDataSet changedRootDataSet = new RootDataSet();
        changedRootDataSet.add( Property.newProperty().name( "myData" ).type( PropertyTypes.TEXT ).value( "123" ).build() );

        SetContentDataEditor editor = new SetContentDataEditor( changedRootDataSet );
        Content toBeEdited = Content.newContent().name( "myContent" ).rootDataSet( originalRootDataSet ).build();

        // exercise
        Content updatedContent = editor.edit( toBeEdited );

        // verify
        assertNotNull( updatedContent );
        assertEquals( "123", updatedContent.getRootDataSet().getProperty( "myData" ).getString() );
    }

}
