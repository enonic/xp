package com.enonic.wem.core.schema.content;

import org.junit.Test;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;

import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.ABSTRACT_PROPERTY;
import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.ALLOW_CHILD_CONTENT_PROPERTY;
import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.BUILT_IN_PROPERTY;
import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.FINAL_PROPERTY;
import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.FORMITEMS_FULL_PATH;
import static org.junit.Assert.*;

public class ContentTypeNodeTranslatorTest
{

    private ContentTypeNodeTranslator translator = new ContentTypeNodeTranslator();

    @Test
    public void toCreateNode_given_displayName()
        throws Exception
    {
        // Setup:
        final String myDisplayNameValue = "My display name";
        CreateContentType command = Commands.contentType().create().
            name( "myName" ).
            displayName( myDisplayNameValue ).
            form( Form.newForm().build() );

        final CreateNode createNode = translator.toCreateNodeCommand( command );

        final String displayNamePropertyName = "displayName";

        verifyPropertyValue( myDisplayNameValue, createNode, displayNamePropertyName );
    }

    @Test
    public void toCreateNode_given_booleans()
        throws Exception
    {
        // Setup:

        CreateContentType command = Commands.contentType().create().
            name( "myName" ).
            displayName( "myDisplayName" ).
            allowChildContent( true ).
            builtIn( true ).
            setFinal( true ).
            setAbstract( true ).
            form( Form.newForm().build() );

        final CreateNode createNode = translator.toCreateNodeCommand( command );

        verifyPropertyValue( "true", createNode, ALLOW_CHILD_CONTENT_PROPERTY );
        verifyPropertyValue( "true", createNode, BUILT_IN_PROPERTY );
        verifyPropertyValue( "true", createNode, FINAL_PROPERTY );
        verifyPropertyValue( "true", createNode, ABSTRACT_PROPERTY );
    }

    @Test
    public void toCreateNode_formItems()
        throws Exception
    {
        // Setup:

        CreateContentType command = Commands.contentType().create().
            name( "myName" ).
            displayName( "myDisplayName" ).
            form( Form.newForm().
                addFormItem( Input.
                    newInput().
                    inputType( InputTypes.TEXT_LINE ).
                    name( "myFormItem1" ).
                    build() ).
                build() );

        final CreateNode createNode = translator.toCreateNodeCommand( command );

        final DataSet formItems = createNode.getData().getDataSet( FORMITEMS_FULL_PATH );
        assertNotNull( formItems );

        final Data input = formItems.getDataSet( "Input", 0 );
        assertNotNull( input );
    }


    private void verifyPropertyValue( final String myExpectedValue, final CreateNode createNode, final String propertyNameToCheck )
    {
        final Property foundProperty = createNode.getData().getProperty( propertyNameToCheck );
        assertNotNull( foundProperty );
        assertEquals( foundProperty.getValue().asString(), myExpectedValue );
    }
}
