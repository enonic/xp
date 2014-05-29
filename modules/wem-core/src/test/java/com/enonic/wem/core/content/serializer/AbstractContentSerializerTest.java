package com.enonic.wem.core.content.serializer;


import java.time.Instant;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.DataSetArray;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyArray;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.AbstractSerializerTest;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public abstract class AbstractContentSerializerTest
    extends AbstractSerializerTest
{
    private static final ContentPath MY_CONTENT_PATH = ContentPath.from( "/mycontent" );

    private ContentSerializer serializer;

    abstract ContentSerializer getSerializer();

    @Before
    public void before()
    {
        this.serializer = getSerializer();
    }

    abstract void assertSerializedResult( String fileNameForExpected, String actualSerialization );

    @Test
    public void property()
    {
        Content content = newContent().path( MY_CONTENT_PATH ).build();
        content.getContentData().setProperty( "myInput", Value.newString( "A value" ) );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-data", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        ContentData parsedContentData = parsedContent.getContentData();
        assertEquals( "A value", parsedContentData.getProperty( "myInput" ).getObject() );
        assertEquals( DataPath.from( "myInput" ), parsedContentData.getProperty( "myInput" ).getPath() );
    }

    @Test
    public void set()
    {
        Content content = newContent().path( MY_CONTENT_PATH ).type( ContentTypeName.from( "my_type" ) ).build();
        content.getContentData().setProperty( "mySet.myInput", Value.newString( "1" ) );
        content.getContentData().setProperty( "mySet.myOtherInput", Value.newString( "2" ) );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-set", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        ContentData parsedContentData = parsedContent.getContentData();
        assertEquals( "1", parsedContentData.getProperty( "mySet.myInput" ).getObject() );
        assertEquals( "2", parsedContentData.getProperty( "mySet.myOtherInput" ).getObject() );

        assertEquals( "mySet.myInput", parsedContentData.getProperty( "mySet.myInput" ).getPath().toString() );
        assertEquals( "mySet.myOtherInput", parsedContentData.getProperty( "mySet.myOtherInput" ).getPath().toString() );
    }

    @Test
    public void array_of_values()
    {
        Content content = newContent().path( MY_CONTENT_PATH ).type( ContentTypeName.from( "my_type" ) ).build();
        content.getContentData().setProperty( "myArray[0]", Value.newString( "1" ) );
        content.getContentData().setProperty( "myArray[1]", Value.newString( "2" ) );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-array", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        ContentData parsedContentData = parsedContent.getContentData();
        assertEquals( "1", parsedContentData.getProperty( "myArray[0]" ).getObject() );
        assertEquals( "2", parsedContentData.getProperty( "myArray[1]" ).getObject() );

        assertEquals( "myArray[0]", parsedContentData.getProperty( "myArray[0]" ).getPath().toString() );
        assertEquals( "myArray[1]", parsedContentData.getProperty( "myArray[1]" ).getPath().toString() );
    }

    @Test
    public void array_within_set()
    {
        Content content = newContent().path( MY_CONTENT_PATH ).type( ContentTypeName.from( "my_type" ) ).build();
        content.getContentData().setProperty( "mySet.myArray[0]", Value.newString( "1" ) );
        content.getContentData().setProperty( "mySet.myArray[1]", Value.newString( "2" ) );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-array-within-set", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        ContentData parsedContentData = parsedContent.getContentData();
        assertEquals( "1", parsedContentData.getProperty( "mySet.myArray[0]" ).getObject() );
        assertEquals( "2", parsedContentData.getProperty( "mySet.myArray[1]" ).getObject() );

        assertEquals( "mySet.myArray[0]", parsedContentData.getProperty( "mySet.myArray[0]" ).getPath().toString() );
        assertEquals( "mySet.myArray[1]", parsedContentData.getProperty( "mySet.myArray[1]" ).getPath().toString() );

        DataSet mySet = parsedContentData.getData( "mySet" ).toDataSet();
        assertEquals( "mySet", mySet.toDataSet().getPath().toString() );

        Property mySet_myArray = mySet.getProperty( "myArray" );
        assertSame( mySet_myArray, parsedContentData.getProperty( "mySet.myArray" ) );
        assertEquals( ValueTypes.STRING, mySet_myArray.getValueType() );
        assertEquals( "mySet.myArray[0]", mySet_myArray.getPath().toString() );

        PropertyArray mySet_myArray_Array = mySet_myArray.getArray();
        assertEquals( ValueTypes.STRING, mySet_myArray_Array.getType() );
        assertEquals( "1", mySet_myArray_Array.getValue( 0 ).asString() );
        assertEquals( "2", mySet_myArray_Array.getValue( 1 ).asString() );
        assertEquals( "mySet.myArray[0]", mySet_myArray_Array.getData( 0 ).getPath().toString() );
        assertEquals( "mySet.myArray[1]", mySet_myArray_Array.getData( 1 ).getPath().toString() );
    }

    @Test
    public void array_of_set()
    {
        Content content = newContent().path( MY_CONTENT_PATH ).type( ContentTypeName.from( "my_type" ) ).build();
        content.getContentData().setProperty( "mySet[0].myInput", Value.newString( "1" ) );
        content.getContentData().setProperty( "mySet[0].myOtherInput", Value.newString( "a" ) );
        content.getContentData().setProperty( "mySet[1].myInput", Value.newString( "2" ) );
        content.getContentData().setProperty( "mySet[1].myOtherInput", Value.newString( "b" ) );

        assertEquals( "mySet[1].myInput", content.getContentData().getProperty( "mySet[1].myInput" ).getPath().toString() );
        assertEquals( "mySet[0].myInput", content.getContentData().getProperty( "mySet[0].myInput" ).getPath().toString() );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-array-of-set", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        ContentData parsedContentData = parsedContent.getContentData();
        assertEquals( "1", parsedContentData.getProperty( "mySet[0].myInput" ).getObject() );
        assertEquals( "a", parsedContentData.getProperty( "mySet[0].myOtherInput" ).getObject() );
        assertEquals( "2", parsedContentData.getProperty( "mySet[1].myInput" ).getObject() );
        assertEquals( "b", parsedContentData.getProperty( "mySet[1].myOtherInput" ).getObject() );

        assertEquals( "mySet[0].myInput", parsedContentData.getProperty( "mySet[0].myInput" ).getPath().toString() );
        assertEquals( "mySet[0].myOtherInput", parsedContentData.getProperty( "mySet[0].myOtherInput" ).getPath().toString() );
        assertEquals( "mySet[1].myInput", parsedContentData.getProperty( "mySet[1].myInput" ).getPath().toString() );
        assertEquals( "mySet[1].myOtherInput", parsedContentData.getProperty( "mySet[1].myOtherInput" ).getPath().toString() );
    }

    @Test
    public void insertion_order_of_entries_within_a_DataSet_is_preserved()
    {
        Content content = newContent().path( MY_CONTENT_PATH ).type( ContentTypeName.from( "my_type" ) ).build();
        content.getContentData().setProperty( "mySet.myArray[0]", Value.newString( "1" ) );
        content.getContentData().setProperty( "mySet.myInput", Value.newString( "a" ) );
        content.getContentData().setProperty( "mySet.myArray[1]", Value.newString( "2" ) );
        content.getContentData().setProperty( "mySet.myOtherInput", Value.newString( "b" ) );
        content.getContentData().setProperty( "mySet.myArray[2]", Value.newString( "3" ) );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        DataSet mySet = parsedContent.getContentData().getDataSet( "mySet" );
        Iterator<Data> entries = mySet.iterator();
        assertEquals( "mySet.myArray[0]", entries.next().getPath().toString() );
        assertEquals( "mySet.myInput", entries.next().getPath().toString() );
        assertEquals( "mySet.myArray[1]", entries.next().getPath().toString() );
        assertEquals( "mySet.myOtherInput", entries.next().getPath().toString() );
        assertEquals( "mySet.myArray[2]", entries.next().getPath().toString() );
    }

    @Test
    public void array_within_array()
    {
        Content content = newContent().path( MY_CONTENT_PATH ).type( ContentTypeName.from( "my_type" ) ).build();
        content.getContentData().setProperty( "mySet[0].myArray[0]", Value.newString( "1" ) );
        content.getContentData().setProperty( "mySet[0].myArray[1]", Value.newString( "2" ) );
        content.getContentData().setProperty( "mySet[1].myArray[0]", Value.newString( "3" ) );
        content.getContentData().setProperty( "mySet[1].myArray[1]", Value.newString( "4" ) );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-array-within-array", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        ContentData parsedContentData = parsedContent.getContentData();
        assertEquals( "1", parsedContentData.getProperty( "mySet[0].myArray[0]" ).getObject() );
        assertEquals( "2", parsedContentData.getProperty( "mySet[0].myArray[1]" ).getObject() );
        assertEquals( "3", parsedContentData.getProperty( "mySet[1].myArray[0]" ).getObject() );
        assertEquals( "4", parsedContentData.getProperty( "mySet[1].myArray[1]" ).getObject() );

        DataSet mySet = parsedContentData.getDataSet( "mySet" );

        assertEquals( true, mySet.isArray() );
        DataSetArray mySet_array = mySet.getArray();

        assertEquals( "mySet", mySet_array.getPath().toString() );

        DataSet mySet_0 = mySet_array.getData( 0 );
        assertEquals( "mySet[0]", mySet_0.getPath().toString() );

        Property mySet_0_myArray = mySet_0.getProperty( "myArray" );
        assertEquals( true, mySet_0_myArray.isArray() );
        assertEquals( "mySet[0].myArray[0]", mySet_0_myArray.getPath().toString() );

        PropertyArray mySet_0_myArray_array = mySet_0_myArray.getArray();
        assertEquals( ValueTypes.STRING, mySet_0_myArray_array.getType() );
        assertEquals( "1", mySet_0_myArray_array.getValue( 0 ).asString() );
        assertEquals( "2", mySet_0_myArray_array.getValue( 1 ).asString() );

        DataSet mySet_1 = mySet_array.getData( 1 );
        assertEquals( "mySet[1]", mySet_1.getPath().toString() );

        Property mySet_1_myArray = mySet_1.getProperty( "myArray" );
        assertEquals( true, mySet_1_myArray.isArray() );
        assertEquals( "mySet[1].myArray[0]", mySet_1_myArray.getPath().toString() );

        Property mySet_1_myArray_1 = mySet_1.getProperty( "myArray[1]" );
        assertEquals( true, mySet_1_myArray_1.isArray() );
        assertEquals( "mySet[1].myArray[1]", mySet_1_myArray_1.getPath().toString() );

        PropertyArray mySet_1_myArray_array = mySet_1_myArray.getArray();
        assertEquals( ValueTypes.STRING, mySet_1_myArray_array.getType() );
        assertEquals( "3", mySet_1_myArray_array.getValue( 0 ).asString() );
        assertEquals( "4", mySet_1_myArray_array.getValue( 1 ).asString() );

        assertEquals( "mySet[0].myArray[0]", parsedContentData.getProperty( "mySet[0].myArray[0]" ).getPath().toString() );
        assertEquals( "mySet[0].myArray[1]", parsedContentData.getProperty( "mySet[0].myArray[1]" ).getPath().toString() );
        assertEquals( "mySet[1].myArray[0]", parsedContentData.getProperty( "mySet[1].myArray[0]" ).getPath().toString() );
        assertEquals( "mySet[1].myArray[1]", parsedContentData.getProperty( "mySet[1].myArray[1]" ).getPath().toString() );
    }

    @Test
    public void given_formItem_and_formItemSet_when_parsed_then_paths_and_values_are_as_expected()
    {
        final FormItemSet formItemSet = newFormItemSet().name( "formItemSet" ).build();
        formItemSet.add( newInput().name( "myText" ).inputType( InputTypes.TEXT_LINE ).build() );
        final ContentType contentType = newContentType().
            name( "my_content_type" ).
            addFormItem( newInput().name( "myText" ).inputType( InputTypes.TEXT_LINE ).required( true ).build() ).
            addFormItem( formItemSet ).
            build();
        //contentTypeFetcher.add( contentType );

        Content content = newContent().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getContentData().setProperty( "myText", Value.newString( "A value" ) );
        content.getContentData().setProperty( "formItemSet.myText", Value.newString( "A another value" ) );

        String serialized = toString( content );

        // exercise
        Content actualContent = toContent( serialized );

        // verify
        ContentData actualRootContentSet = actualContent.getContentData();
        assertEquals( "myText", actualRootContentSet.getProperty( "myText" ).getPath().toString() );
        assertEquals( "formItemSet.myText", actualRootContentSet.getProperty( "formItemSet.myText" ).getPath().toString() );
        assertEquals( "A value", actualRootContentSet.getProperty( "myText" ).getObject() );
        assertEquals( "A another value", actualRootContentSet.getProperty( "formItemSet.myText" ).getObject() );
    }

    @Test
    public void given_array_of_formItemSet_when_parsed_then_paths_and_values_are_as_expected()
    {
        final FormItemSet formItemSet = newFormItemSet().name( "formItemSet" ).label( "FormItemSet" ).multiple( true ).build();
        formItemSet.add( newInput().name( "myText" ).inputType( InputTypes.TEXT_LINE ).build() );

        final ContentType contentType = newContentType().
            name( "my_content_type" ).
            addFormItem( formItemSet ).
            build();
        //contentTypeFetcher.add( contentType );

        Content content = newContent().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getContentData().setProperty( "formItemSet[0].myText", Value.newString( "Value 1" ) );
        content.getContentData().setProperty( "formItemSet[1].myText", Value.newString( "Value 2" ) );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        ContentData parsedContentData = parsedContent.getContentData();
        assertEquals( "formItemSet[0].myText", parsedContentData.getProperty( "formItemSet[0].myText" ).getPath().toString() );
        assertEquals( "formItemSet[1].myText", parsedContentData.getProperty( "formItemSet[1].myText" ).getPath().toString() );
        assertEquals( "Value 1", parsedContentData.getProperty( "formItemSet[0].myText" ).getObject() );
        assertEquals( "Value 2", parsedContentData.getProperty( "formItemSet[1].myText" ).getObject() );
    }

    @Test
    public void given_formItem_inside_layout_when_parse_then_formItem_path_is_affected_by_name_of_layout()
    {
        final FieldSet layout = newFieldSet().label( "Label" ).name( "fieldSet" ).addFormItem(
            newInput().name( "myText" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        final ContentType contentType = newContentType().
            name( "my_content_type" ).
            addFormItem( newInput().name( "myField" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( layout ).
            build();

        Content content = newContent().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getContentData().setProperty( "myText", Value.newString( "A value" ) );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        ContentData parsedContentData = parsedContent.getContentData();
        assertEquals( "A value", parsedContentData.getProperty( "myText" ).getString() );
        assertEquals( "myText", parsedContentData.getProperty( "myText" ).getPath().toString() );
    }

    @Test
    public void unstructured_with_arrays()
    {
        Content content = newContent().path( MY_CONTENT_PATH ).build();
        content.getContentData().setProperty( "names[0]", Value.newString( "Thomas" ) );
        content.getContentData().setProperty( "names[1]", Value.newString( "Sten Roger" ) );
        content.getContentData().setProperty( "names[2]", Value.newString( "Alex" ) );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        ContentData parsedContentData = parsedContent.getContentData();
        assertEquals( "Thomas", parsedContentData.getProperty( "names[0]" ).getObject() );
        assertEquals( ValueTypes.STRING, parsedContentData.getProperty( "names[0]" ).getValueType() );
        assertEquals( "Sten Roger", parsedContentData.getProperty( "names[1]" ).getObject() );
        assertEquals( "Alex", parsedContentData.getProperty( "names[2]" ).getObject() );
    }

    @Test
    public void content_serialize_parse_serialize_roundTrip()
    {
        final Instant time = Instant.now();
        final Content content = newContent().
            type( ContentTypeName.from( "my_type" ) ).
            createdTime( time ).
            modifiedTime( time ).
            owner( AccountKey.superUser() ).
            creator( AccountKey.superUser() ).
            modifier( AccountKey.anonymous() ).
            displayName( "My content" ).
            path( ContentPath.from( "/site1/mycontent" ) ).
            build();
        content.getContentData().setProperty( "mySet[0].myArray[0]", Value.newString( "1" ) );
        content.getContentData().setProperty( "mySet[0].myArray[1]", Value.newString( "2" ) );
        content.getContentData().setProperty( "mySet[1].myArray[0]", Value.newString( "3" ) );
        content.getContentData().setProperty( "mySet[1].myArray[1]", Value.newString( "4" ) );

        final String serialized = toString( content );

        // exercise
        final Content parsedContent = toContent( serialized );
        final String serializedAfterParsing = toString( parsedContent );

        // verify
        assertEquals( serialized, serializedAfterParsing );
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
