package com.enonic.wem.core.schema.content;

import org.junit.Test;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.SchemaId;
import com.enonic.wem.api.schema.content.ContentType;

import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.ABSTRACT_PROPERTY;
import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.ALLOW_CHILD_CONTENT_PROPERTY;
import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.BUILT_IN_PROPERTY;
import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY;
import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.DISPLAY_NAME_PROPERTY;
import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.FINAL_PROPERTY;
import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.FORMITEMS_FULL_PATH;
import static com.enonic.wem.core.schema.content.ContentTypeNodeTranslator.SUPER_TYPE_PROPERTY;
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

    @Test
    public void fromNode_nodeProperties()
        throws Exception
    {
        final NodePath parentPath = NodePath.newPath( "my/test/parent" ).build();
        Node node = Node.newNode().
            id( EntityId.from( "1" ) ).
            name( "my-name" ).
            build();

        final ContentType contentType = translator.fromNode( node );

        assertEquals( new SchemaId( "1" ), contentType.getId() );
        assertEquals( ContentTypesInitializer.UNSTRUCTURED, contentType.getSuperType() );
    }


    @Test
    public void fromNode_rootDataSet()
        throws Exception
    {

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.addProperty( DataPath.from( ALLOW_CHILD_CONTENT_PROPERTY ), new Value.String( "true" ) );
        rootDataSet.addProperty( DataPath.from( BUILT_IN_PROPERTY ), new Value.String( "true" ) );
        rootDataSet.addProperty( DataPath.from( FINAL_PROPERTY ), new Value.String( "true" ) );
        rootDataSet.addProperty( DataPath.from( ABSTRACT_PROPERTY ), new Value.String( "true" ) );
        rootDataSet.addProperty( DataPath.from( DISPLAY_NAME_PROPERTY ), new Value.String( "myDisplayName" ) );
        rootDataSet.addProperty( DataPath.from( CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY ), new Value.String( "myDisplayNameScript" ) );
        rootDataSet.addProperty( DataPath.from( SUPER_TYPE_PROPERTY ), new Value.String( "my-super-type" ) );

        final NodePath parentPath = NodePath.newPath( "my/test/parent" ).build();
        Node node = Node.newNode().
            id( EntityId.from( "1" ) ).
            name( "my-name" ).
            rootDataSet( rootDataSet ).
            build();

        final ContentType contentType = translator.fromNode( node );

        assertEquals( new SchemaId( "1" ), contentType.getId() );
        assertEquals( "my-super-type", contentType.getSuperType().getContentTypeName().toString() );
        assertEquals( true, contentType.allowChildContent() );
        assertEquals( true, contentType.isFinal() );
        assertEquals( true, contentType.isBuiltIn() );
        assertEquals( true, contentType.isAbstract() );
        assertEquals( "myDisplayNameScript", contentType.getContentDisplayNameScript() );
        assertEquals( "myDisplayName", contentType.getDisplayName() );
    }

    private void verifyPropertyValue( final String myExpectedValue, final CreateNode createNode, final String propertyNameToCheck )
    {
        final Property foundProperty = createNode.getData().getProperty( propertyNameToCheck );
        assertNotNull( foundProperty );
        assertEquals( foundProperty.getValue().asString(), myExpectedValue );
    }
}
