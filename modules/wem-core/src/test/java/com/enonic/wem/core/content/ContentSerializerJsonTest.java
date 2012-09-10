package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.core.content.data.MockBlobKeyResolver;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.MockContentTypeFetcher;
import com.enonic.wem.core.content.type.formitem.Component;
import com.enonic.wem.core.content.type.formitem.FormItemSet;
import com.enonic.wem.core.content.type.formitem.VisualFieldSet;
import com.enonic.wem.core.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.core.content.type.formitem.comptype.RadioButtonsConfig;
import com.enonic.wem.core.module.Module;

import com.enonic.cms.framework.blob.BlobKeyCreator;

import static com.enonic.wem.core.content.type.formitem.Component.newComponent;
import static com.enonic.wem.core.content.type.formitem.FormItemSet.newBuilder;
import static com.enonic.wem.core.content.type.formitem.FormItemSet.newFormItemSet;
import static com.enonic.wem.core.content.type.formitem.VisualFieldSet.newVisualFieldSet;
import static org.junit.Assert.*;

public class ContentSerializerJsonTest
{
    private Module myModule = Module.newModule().name( "myModule" ).build();

    private MockContentTypeFetcher contentTypeFetcher = new MockContentTypeFetcher();

    private ContentSerializerJson serializer = new ContentSerializerJson( contentTypeFetcher );

    @Test
    public void text()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( Component.newBuilder().name( "myTextArea" ).type( ComponentTypes.TEXT_AREA ).required( true ).build() );
        contentType.addFormItem( Component.newBuilder().name( "myPhone" ).type( ComponentTypes.PHONE ).build() );
        contentTypeFetcher.add( contentType );

        Content content = new Content();
        content.setName( "myContent" );
        content.setType( contentType );
        content.setData( "myTextArea", "My test\n text." );
        content.setData( "myPhone", "+4712123123" );

        String json = serializer.toJson( content );
        System.out.println( json );
        // exercise
        Content parsedContent = serializer.parse( json );

        // verify
        assertEquals( "myContent", parsedContent.getName() );
        assertEquals( "My test\n" + " text.", parsedContent.getData( "myTextArea" ).getValue() );
        assertEquals( "+4712123123", parsedContent.getData( "myPhone" ).getValue() );
    }

    @Test
    public void radiobuttons()
    {
        RadioButtonsConfig radioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "Norway", "NO" ).addOption( "South Africa", "ZA" ).build();

        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem(
            Component.newBuilder().name( "myRadiobuttons" ).type( ComponentTypes.RADIO_BUTTONS ).required( true ).componentTypeConfig(
                radioButtonsConfig ).build() );
        contentTypeFetcher.add( contentType );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myRadiobuttons", "NO" );

        String json = serializer.toJson( content );

        /// exercise
        Content parsedContent = serializer.parse( json );

        // verify
        assertEquals( "NO", parsedContent.getData( "myRadiobuttons" ).getValue() );
    }

    @Test
    public void multiple_textlines()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( Component.newBuilder().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.addFormItem(
            Component.newBuilder().name( "myMultipleTextLine" ).type( ComponentTypes.TEXT_LINE ).required( false ).multiple(
                true ).build() );
        contentTypeFetcher.add( contentType );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myTextLine", "A single line" );
        content.setData( "myMultipleTextLine[0]", "First line" );
        content.setData( "myMultipleTextLine[1]", "Second line" );

        String json = serializer.toJson( content );
        Content parsedContent = serializer.parse( json );

        assertEquals( "A single line", parsedContent.getData( "myTextLine" ).getValue() );
        assertEquals( "First line", parsedContent.getData( "myMultipleTextLine[0]" ).getValue() );
        assertEquals( "Second line", parsedContent.getData( "myMultipleTextLine[1]" ).getValue() );
    }

    @Test
    public void groupedFieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( Component.newBuilder().name( "name" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() );

        FormItemSet formItemSet = newBuilder().name( "personalia" ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.addItem( Component.newBuilder().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newBuilder().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentTypeFetcher.add( contentType );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "name", "Ola Nordmann" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );

        String json = serializer.toJson( content );

        // exercise
        Content actualContent = serializer.parse( json );

        // verify
        assertEquals( "Blue", actualContent.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "Blonde", actualContent.getData( "personalia.hairColour" ).getValue() );
    }

    @Test
    public void multiple_subtype()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        Component nameComponent = Component.newBuilder().name( "name" ).type( ComponentTypes.TEXT_LINE ).required( true ).build();
        contentType.addFormItem( nameComponent );
        contentTypeFetcher.add( contentType );

        FormItemSet formItemSet = newFormItemSet().name( "personalia" ).label( "Personalia" ).multiple( true ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.addItem( Component.newBuilder().name( "name" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newBuilder().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newBuilder().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "name", "Norske" );
        content.setData( "personalia[0].name", "Ola Nordmann" );
        content.setData( "personalia[0].eyeColour", "Blue" );
        content.setData( "personalia[0].hairColour", "Blonde" );
        content.setData( "personalia[1].name", "Kari Trestakk" );
        content.setData( "personalia[1].eyeColour", "Green" );
        content.setData( "personalia[1].hairColour", "Brown" );

        String json = serializer.toJson( content );

        System.out.println( json );

        // exercise
        Content parsedContent = serializer.parse( json );

        // verify
        assertEquals( "Norske", parsedContent.getData( "name" ).getValue() );
        assertEquals( "Ola Nordmann", parsedContent.getData( "personalia[0].name" ).getValue() );
        assertEquals( "Blue", parsedContent.getData( "personalia[0].eyeColour" ).getValue() );
        assertEquals( "Blonde", parsedContent.getData( "personalia[0].hairColour" ).getValue() );
        assertEquals( "Kari Trestakk", parsedContent.getData( "personalia[1].name" ).getValue() );
        assertEquals( "Green", parsedContent.getData( "personalia[1].eyeColour" ).getValue() );
        assertEquals( "Brown", parsedContent.getData( "personalia[1].hairColour" ).getValue() );
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

        String json = serializer.toJson( data );

        // exercise
        Content parsedContent = serializer.parse( json );

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

        String json = serializer.toJson( content );

        // exercise
        Content parsedContent = serializer.parse( json );

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

        String json = serializer.toJson( content );

        // exercise
        Content parsedContent = serializer.parse( json );

        // verify
        assertEquals( "Thomas", parsedContent.getData( "company.names[0]" ).getValue() );
        assertEquals( "Sten Roger", parsedContent.getData( "company.names[1]" ).getValue() );
        assertEquals( "Alex", parsedContent.getData( "company.names[2]" ).getValue() );
    }

    @Test
    public void visualFieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "MyContentType" );
        contentType.addFormItem( newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build() );
        VisualFieldSet visualFieldSet = newVisualFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newComponent().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
            newComponent().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() ).build();
        contentType.addFormItem( visualFieldSet );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myField", "myValue" );
        content.setData( "eyeColour", "Blue" );
        content.setData( "hairColour", "Blonde" );

        String json = serializer.toJson( content );

        // exercise
        Content parsedContent = serializer.parse( json );

        // verify
        assertEquals( "myValue", parsedContent.getValueAsString( "myField" ) );
        assertEquals( "Blue", parsedContent.getValueAsString( "eyeColour" ) );
        assertEquals( "Blonde", parsedContent.getValueAsString( "hairColour" ) );
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

        String json = serializer.toJson( content );
        System.out.println( json );
        Content parsedContent = serializer.parse( json );

        //System.out.println(json);
        assertEquals( "Arn", parsedContent.getData( "name" ).getValue() );
        assertEquals( "Caption", parsedContent.getData( "image.caption" ).getValue() );
        assertEquals( BlobKeyCreator.createKey( bytes ), parsedContent.getData( "image.bytes" ).getValue() );
    }

}
