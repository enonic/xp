package com.enonic.wem.core.content.serializer;


import java.util.Iterator;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataArray;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.DataSetArray;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.datatype.DataTypes;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.MockContentTypeFetcher;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.form.FieldSet;
import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.AbstractSerializerTest;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static org.junit.Assert.*;

public abstract class AbstractContentSerializerTest
    extends AbstractSerializerTest
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

    abstract void assertSerializedResult( String fileNameForExpected, String actualSerialization );

    @Test
    public void data()
    {
        Content content = newContent().build();
        content.setData( "myInput", "A value" );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-data", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "A value", parsedContent.getData( "myInput" ).getObject() );
        assertEquals( EntryPath.from( "myInput" ), parsedContent.getData( "myInput" ).getPath() );
    }

    @Test
    public void set()
    {
        Content content = newContent().type( new QualifiedContentTypeName( "myModule:myType" ) ).build();
        content.setData( "mySet.myInput", "1" );
        content.setData( "mySet.myOtherInput", "2" );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-set", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "1", parsedContent.getData( "mySet.myInput" ).getObject() );
        assertEquals( "2", parsedContent.getData( "mySet.myOtherInput" ).getObject() );

        assertEquals( "mySet.myInput", parsedContent.getData( "mySet.myInput" ).getPath().toString() );
        assertEquals( "mySet.myOtherInput", parsedContent.getData( "mySet.myOtherInput" ).getPath().toString() );
    }

    @Test
    public void array_of_values()
    {
        Content content = newContent().type( new QualifiedContentTypeName( "myModule:myType" ) ).build();
        content.setData( "myArray[0]", "1" );
        content.setData( "myArray[1]", "2" );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-array", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "1", parsedContent.getData( "myArray[0]" ).getObject() );
        assertEquals( "2", parsedContent.getData( "myArray[1]" ).getObject() );

        assertEquals( "myArray[0]", parsedContent.getData( "myArray[0]" ).getPath().toString() );
        assertEquals( "myArray[1]", parsedContent.getData( "myArray[1]" ).getPath().toString() );
    }

    @Test
    public void array_within_set()
    {
        Content content = newContent().type( new QualifiedContentTypeName( "myModule:myType" ) ).build();
        content.setData( "mySet.myArray[0]", "1" );
        content.setData( "mySet.myArray[1]", "2" );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-array-within-set", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "1", parsedContent.getData( "mySet.myArray[0]" ).getObject() );
        assertEquals( "2", parsedContent.getData( "mySet.myArray[1]" ).getObject() );

        assertEquals( "mySet.myArray[0]", parsedContent.getData( "mySet.myArray[0]" ).getPath().toString() );
        assertEquals( "mySet.myArray[1]", parsedContent.getData( "mySet.myArray[1]" ).getPath().toString() );

        DataSet mySet = parsedContent.getEntry( "mySet" ).toDataSet();
        assertEquals( "mySet", mySet.toDataSet().getPath().toString() );

        Data mySet_myArray = mySet.getData( "myArray" );
        assertSame( mySet_myArray, parsedContent.getData( "mySet.myArray" ) );
        assertEquals( DataTypes.TEXT, mySet_myArray.getType() );
        assertEquals( "mySet.myArray[0]", mySet_myArray.getPath().toString() );

        DataArray mySet_myArray_Array = mySet_myArray.getArray();
        assertEquals( DataTypes.TEXT, mySet_myArray_Array.getType() );
        assertEquals( "1", mySet_myArray_Array.getValue( 0 ).asString() );
        assertEquals( "2", mySet_myArray_Array.getValue( 1 ).asString() );
        assertEquals( "mySet.myArray[0]", mySet_myArray_Array.getData( 0 ).getPath().toString() );
        assertEquals( "mySet.myArray[1]", mySet_myArray_Array.getData( 1 ).getPath().toString() );
    }

    @Test
    public void array_of_set()
    {
        Content content = newContent().type( new QualifiedContentTypeName( "myModule:myType" ) ).build();
        content.setData( "mySet[0].myInput", "1" );
        content.setData( "mySet[0].myOtherInput", "a" );
        content.setData( "mySet[1].myInput", "2" );
        content.setData( "mySet[1].myOtherInput", "b" );

        assertEquals( "mySet[1].myInput", content.getData( "mySet[1].myInput" ).getPath().toString() );
        assertEquals( "mySet[0].myInput", content.getData( "mySet[0].myInput" ).getPath().toString() );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-array-of-set", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "1", parsedContent.getData( "mySet[0].myInput" ).getObject() );
        assertEquals( "a", parsedContent.getData( "mySet[0].myOtherInput" ).getObject() );
        assertEquals( "2", parsedContent.getData( "mySet[1].myInput" ).getObject() );
        assertEquals( "b", parsedContent.getData( "mySet[1].myOtherInput" ).getObject() );

        assertEquals( "mySet[0].myInput", parsedContent.getData( "mySet[0].myInput" ).getPath().toString() );
        assertEquals( "mySet[0].myOtherInput", parsedContent.getData( "mySet[0].myOtherInput" ).getPath().toString() );
        assertEquals( "mySet[1].myInput", parsedContent.getData( "mySet[1].myInput" ).getPath().toString() );
        assertEquals( "mySet[1].myOtherInput", parsedContent.getData( "mySet[1].myOtherInput" ).getPath().toString() );
    }

    @Test
    public void insertion_order_of_entries_within_a_DataSet_is_preserved()
    {
        Content content = newContent().type( new QualifiedContentTypeName( "myModule:myType" ) ).build();
        content.setData( "mySet.myArray[0]", "1" );
        content.setData( "mySet.myInput", "a" );
        content.setData( "mySet.myArray[1]", "2" );
        content.setData( "mySet.myOtherInput", "b" );
        content.setData( "mySet.myArray[2]", "3" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        DataSet mySet = parsedContent.getDataSet( "mySet" );
        Iterator<Entry> entries = mySet.iterator();
        assertEquals( "mySet.myArray[0]", entries.next().getPath().toString() );
        assertEquals( "mySet.myInput", entries.next().getPath().toString() );
        assertEquals( "mySet.myArray[1]", entries.next().getPath().toString() );
        assertEquals( "mySet.myOtherInput", entries.next().getPath().toString() );
        assertEquals( "mySet.myArray[2]", entries.next().getPath().toString() );
    }

    @Test
    public void array_within_array()
    {
        Content content = newContent().type( new QualifiedContentTypeName( "myModule:myType" ) ).build();
        content.setData( "mySet[0].myArray[0]", "1" );
        content.setData( "mySet[0].myArray[1]", "2" );
        content.setData( "mySet[1].myArray[0]", "3" );
        content.setData( "mySet[1].myArray[1]", "4" );

        String serialized = toString( content );

        // verify
        assertSerializedResult( "content-array-within-array", serialized );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "1", parsedContent.getData( "mySet[0].myArray[0]" ).getObject() );
        assertEquals( "2", parsedContent.getData( "mySet[0].myArray[1]" ).getObject() );
        assertEquals( "3", parsedContent.getData( "mySet[1].myArray[0]" ).getObject() );
        assertEquals( "4", parsedContent.getData( "mySet[1].myArray[1]" ).getObject() );

        DataSet mySet = parsedContent.getDataSet( "mySet" );

        assertEquals( true, mySet.isArray() );
        DataSetArray mySet_array = mySet.getArray();

        assertEquals( "mySet", mySet_array.getPath().toString() );

        DataSet mySet_0 = mySet_array.getDataSet( 0 );
        assertEquals( "mySet[0]", mySet_0.getPath().toString() );

        Data mySet_0_myArray = mySet_0.getData( "myArray" );
        assertEquals( true, mySet_0_myArray.isArray() );
        assertEquals( "mySet[0].myArray[0]", mySet_0_myArray.getPath().toString() );

        DataArray mySet_0_myArray_array = mySet_0_myArray.getArray();
        assertEquals( DataTypes.TEXT, mySet_0_myArray_array.getType() );
        assertEquals( "1", mySet_0_myArray_array.getValue( 0 ).asString() );
        assertEquals( "2", mySet_0_myArray_array.getValue( 1 ).asString() );

        DataSet mySet_1 = mySet_array.getDataSet( 1 );
        assertEquals( "mySet[1]", mySet_1.getPath().toString() );

        Data mySet_1_myArray = mySet_1.getData( "myArray" );
        assertEquals( true, mySet_1_myArray.isArray() );
        assertEquals( "mySet[1].myArray[0]", mySet_1_myArray.getPath().toString() );

        Data mySet_1_myArray_1 = mySet_1.getData( "myArray[1]" );
        assertEquals( true, mySet_1_myArray_1.isArray() );
        assertEquals( "mySet[1].myArray[1]", mySet_1_myArray_1.getPath().toString() );

        DataArray mySet_1_myArray_array = mySet_1_myArray.getArray();
        assertEquals( DataTypes.TEXT, mySet_1_myArray_array.getType() );
        assertEquals( "3", mySet_1_myArray_array.getValue( 0 ).asString() );
        assertEquals( "4", mySet_1_myArray_array.getValue( 1 ).asString() );

        assertEquals( "mySet[0].myArray[0]", parsedContent.getData( "mySet[0].myArray[0]" ).getPath().toString() );
        assertEquals( "mySet[0].myArray[1]", parsedContent.getData( "mySet[0].myArray[1]" ).getPath().toString() );
        assertEquals( "mySet[1].myArray[0]", parsedContent.getData( "mySet[1].myArray[0]" ).getPath().toString() );
        assertEquals( "mySet[1].myArray[1]", parsedContent.getData( "mySet[1].myArray[1]" ).getPath().toString() );
    }

    @Test
    public void given_formItem_and_formItemSet_when_parsed_then_paths_and_values_are_as_expected()
    {
        final FormItemSet formItemSet = newFormItemSet().name( "formItemSet" ).build();
        formItemSet.add( newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).build() );
        final ContentType contentType = newContentType().
            module( myModule.getName() ).
            name( "MyContentType" ).
            addFormItem( newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).
            addFormItem( formItemSet ).
            build();
        contentTypeFetcher.add( contentType );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myText", "A value" );
        content.setData( "formItemSet.myText", "A another value" );

        String serialized = toString( content );

        // exercise
        Content actualContent = toContent( serialized );

        // verify
        assertEquals( "myText", actualContent.getData( "myText" ).getPath().toString() );
        assertEquals( "formItemSet.myText", actualContent.getData( "formItemSet.myText" ).getPath().toString() );
        assertEquals( "A value", actualContent.getData( "myText" ).getObject() );
        assertEquals( "A another value", actualContent.getData( "formItemSet.myText" ).getObject() );
    }

    @Test
    public void given_array_of_formItemSet_when_parsed_then_paths_and_values_are_as_expected()
    {
        final FormItemSet formItemSet = newFormItemSet().name( "formItemSet" ).label( "FormItemSet" ).multiple( true ).build();
        formItemSet.add( newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).build() );

        final ContentType contentType = newContentType().
            module( myModule.getName() ).
            name( "MyContentType" ).
            addFormItem( formItemSet ).
            build();
        contentTypeFetcher.add( contentType );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "formItemSet[0].myText", "Value 1" );
        content.setData( "formItemSet[1].myText", "Value 2" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "formItemSet[0].myText", parsedContent.getData( "formItemSet[0].myText" ).getPath().toString() );
        assertEquals( "formItemSet[1].myText", parsedContent.getData( "formItemSet[1].myText" ).getPath().toString() );
        assertEquals( "Value 1", parsedContent.getData( "formItemSet[0].myText" ).getObject() );
        assertEquals( "Value 2", parsedContent.getData( "formItemSet[1].myText" ).getObject() );
    }

    @Test
    public void given_formItem_inside_layout_when_parse_then_formItem_path_is_affected_by_name_of_layout()
    {
        final FieldSet layout = newFieldSet().label( "Label" ).name( "fieldSet" ).add(
            newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).build() ).build();

        final ContentType contentType = newContentType().
            module( myModule.getName() ).
            name( "MyContentType" ).
            addFormItem( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() ).
            addFormItem( layout ).
            build();

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myText", "A value" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "A value", parsedContent.getData( "myText" ).asString() );
        assertEquals( "myText", parsedContent.getData( "myText" ).getPath().toString() );
    }

    @Test
    public void unstructured_with_arrays()
    {
        Content content = newContent().build();
        content.setData( "names[0]", "Thomas" );
        content.setData( "names[1]", "Sten Roger" );
        content.setData( "names[2]", "Alex" );

        String serialized = toString( content );

        // exercise
        Content parsedContent = toContent( serialized );

        // verify
        assertEquals( "Thomas", parsedContent.getData( "names[0]" ).getObject() );
        assertEquals( DataTypes.TEXT, parsedContent.getData( "names[0]" ).getType() );
        assertEquals( "Sten Roger", parsedContent.getData( "names[1]" ).getObject() );
        assertEquals( "Alex", parsedContent.getData( "names[2]" ).getObject() );
    }

    @Test
    public void content_serialize_parse_serialize_roundTrip()
    {
        final DateTime time = DateTime.now();
        final Content content = newContent().
            type( new QualifiedContentTypeName( "myModule:myType" ) ).
            createdTime( time ).
            modifiedTime( time ).
            owner( AccountKey.superUser() ).
            modifier( AccountKey.superUser() ).
            displayName( "My content" ).
            path( ContentPath.from( "site1/mycontent" ) ).
            build();
        content.setData( "mySet[0].myArray[0]", "1" );
        content.setData( "mySet[0].myArray[1]", "2" );
        content.setData( "mySet[1].myArray[0]", "3" );
        content.setData( "mySet[1].myArray[1]", "4" );

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
