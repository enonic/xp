package com.enonic.wem.core.content;


import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.core.content.data.MockBlobKeyResolver;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.MockContentTypeFetcher;
import com.enonic.wem.core.content.type.formitem.Component;
import com.enonic.wem.core.content.type.formitem.FormItemSet;
import com.enonic.wem.core.content.type.formitem.VisualFieldSet;
import com.enonic.wem.core.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.core.module.Module;

import com.enonic.cms.framework.blob.BlobKeyCreator;

import static com.enonic.wem.core.content.type.formitem.Component.newComponent;
import static com.enonic.wem.core.content.type.formitem.FormItemSet.newBuilder;
import static com.enonic.wem.core.content.type.formitem.FormItemSet.newFormItemSet;
import static com.enonic.wem.core.content.type.formitem.VisualFieldSet.newVisualFieldSet;
import static org.junit.Assert.*;

public abstract class AbstractContentSerializerTest
{
    private Module myModule = Module.newModule().name( "myModule" ).build();

    protected MockContentTypeFetcher contentTypeFetcher = new MockContentTypeFetcher();

    private ContentSerializer serializer;

    abstract ContentSerializer getSerializer();

    @Before
    public void before()
    {
        this.serializer = getSerializer();
    }

    @Test
    public void given_content_with_name_when_parsed_then_name_is_as_expected()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( Component.newBuilder().name( "myComponent" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() );
        contentTypeFetcher.add( contentType );

        Content content = new Content();
        content.setName( "myContent" );
        content.setData( "myComponent", "A value" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "myContent", parsedContent.getName() );
    }

    @Test
    public void given_content_with_name_and_a_component_when_parsed_then_path_and_value_are_as_expected()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( Component.newBuilder().name( "myComponent" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() );
        contentTypeFetcher.add( contentType );

        Content content = new Content();
        content.setData( "myComponent", "A value" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "A value", parsedContent.getData( "myComponent" ).getValue() );
    }

    @Test
    public void given_array_of_component_when_parsed_then_paths_and_values_are_as_expected()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem(
            Component.newBuilder().name( "myComponent" ).type( ComponentTypes.TEXT_LINE ).required( false ).multiple( true ).build() );
        contentTypeFetcher.add( contentType );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myComponent[0]", "Value 1" );
        content.setData( "myComponent[1]", "Value 2" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        assertEquals( "Value 1", parsedContent.getData( "myComponent[0]" ).getValue() );
        assertEquals( "Value 2", parsedContent.getData( "myComponent[1]" ).getValue() );
    }

    @Test
    public void given_component_and_formItemSet_when_parsed_then_paths_and_values_are_as_expected()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( Component.newBuilder().name( "component" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() );

        FormItemSet formItemSet = newBuilder().name( "formItemSet" ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.addItem( Component.newBuilder().name( "component" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentTypeFetcher.add( contentType );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "component", "A value" );
        content.setData( "formItemSet.component", "A another value" );

        String serialized = toString( content );

        // exercise
        Content actualContent = toContent( serialized );

        // verify
        assertEquals( "component", actualContent.getData( "component" ).getPath().toString() );
        assertEquals( "formItemSet.component", actualContent.getData( "formItemSet.component" ).getPath().toString() );
        assertEquals( "A value", actualContent.getData( "component" ).getValue() );
        assertEquals( "A another value", actualContent.getData( "formItemSet.component" ).getValue() );
    }

    @Test
    public void given_array_of_formItemSet_when_parsed_then_paths_and_values_are_as_expected()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentTypeFetcher.add( contentType );

        FormItemSet formItemSet = newFormItemSet().name( "formItemSet" ).label( "FormItemSet" ).multiple( true ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.addItem( Component.newBuilder().name( "component" ).type( ComponentTypes.TEXT_LINE ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "formItemSet[0].component", "Value 1" );
        content.setData( "formItemSet[1].component", "Value 2" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "formItemSet[0].component", parsedContent.getData( "formItemSet[0].component" ).getPath().toString() );
        assertEquals( "formItemSet[1].component", parsedContent.getData( "formItemSet[1].component" ).getPath().toString() );
        assertEquals( "Value 1", parsedContent.getData( "formItemSet[0].component" ).getValue() );
        assertEquals( "Value 2", parsedContent.getData( "formItemSet[1].component" ).getValue() );
    }

    @Test
    public void given_component_inside_visualFieldSet_when_parse_then_component_path_is_affected_by_name_of_visualFieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build() );
        VisualFieldSet visualFieldSet = newVisualFieldSet().label( "Label" ).name( "visualFieldSet" ).add(
            newComponent().name( "component" ).type( ComponentTypes.TEXT_LINE ).build() ).build();
        contentType.addFormItem( visualFieldSet );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "component", "A value" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "A value", parsedContent.getValueAsString( "component" ) );
        assertEquals( "component", parsedContent.getData( "component" ).getPath().toString() );
    }


    @Test
    public void unstructured_with_subTypes()
    {
        Content data = new Content();
        data.setData( "name", "Thomas" );
        data.setData( "child[0].name", "Joachim" );
        data.setData( "child[0].age", "9" );
        data.setData( "child[0].features.eyeColour", "Blue" );
        data.setData( "child[0].features.hairColour", "Blonde" );
        data.setData( "child[1].name", "Madeleine" );
        data.setData( "child[1].age", "7" );
        data.setData( "child[1].features.eyeColour", "Brown" );
        data.setData( "child[1].features.hairColour", "Black" );

        String serialized = toString( data );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "Thomas", parsedContent.getData( "name" ).getValue() );
        assertEquals( "Joachim", parsedContent.getData( "child[0].name" ).getValue() );
        assertEquals( "9", parsedContent.getData( "child[0].age" ).getValue() );
        assertEquals( "Blue", parsedContent.getData( "child[0].features.eyeColour" ).getValue() );
        assertEquals( "Blonde", parsedContent.getData( "child[0].features.hairColour" ).getValue() );
        assertEquals( "Madeleine", parsedContent.getData( "child[1].name" ).getValue() );
        assertEquals( "7", parsedContent.getData( "child[1].age" ).getValue() );
        assertEquals( "Brown", parsedContent.getData( "child[1].features.eyeColour" ).getValue() );
        assertEquals( "Black", parsedContent.getData( "child[1].features.hairColour" ).getValue() );
    }

    @Test
    public void unstructured_with_arrays()
    {
        Content content = new Content();
        content.setData( "names[0]", "Thomas" );
        content.setData( "names[1]", "Sten Roger" );
        content.setData( "names[2]", "Alex" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "Thomas", parsedContent.getData( "names[0]" ).getValue() );
        assertEquals( DataTypes.TEXT, parsedContent.getData( "names[0]" ).getDataType() );
        assertEquals( "Sten Roger", parsedContent.getData( "names[1]" ).getValue() );
        assertEquals( "Alex", parsedContent.getData( "names[2]" ).getValue() );
    }

    @Test
    public void unstructured_with_arrays_within_subType()
    {
        Content content = new Content();
        content.setData( "company.names[0]", "Thomas" );
        content.setData( "company.names[1]", "Sten Roger" );
        content.setData( "company.names[2]", "Alex" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "Thomas", parsedContent.getData( "company.names[0]" ).getValue() );
        assertEquals( "Sten Roger", parsedContent.getData( "company.names[1]" ).getValue() );
        assertEquals( "Alex", parsedContent.getData( "company.names[2]" ).getValue() );
    }

    @Test
    public void xxx()
    {
        byte[] bytes = new byte[]{1, 2, 3};
        Content content = new Content();
        content.setName( "My content" );
        content.setData( "name", "Arn", DataTypes.TEXT );
        content.setData( "image.bytes", bytes, DataTypes.BLOB );
        content.setData( "image.caption", "Caption", DataTypes.TEXT );

        MockBlobKeyResolver blobToKeyReplacer = new MockBlobKeyResolver();
        content.replaceBlobsWithKeys( blobToKeyReplacer );

        String serialized = toString( content );
        Content parsedContent = toContent( serialized );

        assertEquals( "Arn", parsedContent.getData( "name" ).getValue() );
        assertEquals( "Caption", parsedContent.getData( "image.caption" ).getValue() );
        assertEquals( BlobKeyCreator.createKey( bytes ), parsedContent.getData( "image.bytes" ).getValue() );
    }

    private Content toContent( final String serialized )
    {
        return serializer.toContent( serialized );
    }

    private String toString( final Content content )
    {
        String serialized = getSerializer().toString( content );
        System.out.println( serialized );
        return serialized;
    }
}
