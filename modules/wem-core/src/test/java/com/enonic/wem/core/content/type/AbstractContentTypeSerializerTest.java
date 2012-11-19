package com.enonic.wem.core.content.type;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.form.ComponentSetSubType;
import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputSubType;
import com.enonic.wem.api.content.type.form.Layout;
import com.enonic.wem.api.content.type.form.MockSubTypeFetcher;
import com.enonic.wem.api.content.type.form.SubTypeReference;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.content.type.form.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.form.ComponentSetSubType.newComponentSetSubType;
import static com.enonic.wem.api.content.type.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.SubTypeReference.newSubTypeReference;
import static com.enonic.wem.api.module.Module.newModule;
import static org.junit.Assert.*;


public abstract class AbstractContentTypeSerializerTest
{
    private static final Module myModule = newModule().name( "myModule" ).build();

    private ContentTypeSerializer serializer;

    abstract ContentTypeSerializer getSerializer();

    private ContentType contentTypeWithAllComponentTypes;

    @Before
    public void before()
    {
        this.serializer = getSerializer();
        contentTypeWithAllComponentTypes = createContentTypeWithAllInputComponentTypes();
    }

    abstract void assertSerializedResult( String fileNameForExpected, String actualSerialization );

    @Test
    public void given_all_base_types_when_parsed_then_paths_are_as_expected()
        throws Exception
    {
        InputSubType inputSubType =
            InputSubType.newInputSubType().input( Input.newInput().name( "mySharedInput" ).type( InputTypes.TEXT_LINE ).build() ).module(
                Module.SYSTEM ).build();
        FormItemSet set = newFormItemSet().name( "mySet" ).build();
        Layout layout = FieldSet.newFieldSet().label( "My field set" ).name( "myFieldSet" ).add(
            newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() ).build();
        set.add( layout );
        set.add( newSubTypeReference().name( "myCommonInput" ).subType( inputSubType ).build() );

        ContentType.Builder contentTypeBuilder = ContentType.newContentType().name( "AllBaseTypes" ).module( myModule );
        contentTypeBuilder.add( set );
        ContentType contentType = contentTypeBuilder.build();

        String actualSerialization = toString( contentType );

        // exercise
        ContentType actualContentType = toContentType( actualSerialization );

        // verify serialization
        assertSerializedResult( "contentType-allBaseTypes", actualSerialization );

        // verify
        assertNotNull( actualContentType.getFormItem( "mySet" ) );
        assertEquals( "mySet", actualContentType.getFormItem( "mySet" ).getPath().toString() );
        assertNotNull( actualContentType.getFormItem( "mySet.myTextLine" ) );
        assertEquals( "mySet.myTextLine", actualContentType.getFormItem( "mySet.myTextLine" ).getPath().toString() );
        assertEquals( "mySet.myCommonInput", actualContentType.getFormItem( "mySet.myCommonInput" ).getPath().toString() );
    }

    @Test
    public void toString_all_input_types()
    {
        String actualSerialization = toString( contentTypeWithAllComponentTypes );

        // exercise
        ContentType actualContentType = toContentType( actualSerialization );

        // verify:
        assertNotNull( actualContentType );

        // verify serialization
        assertSerializedResult( "contentType-allInputTypes", actualSerialization );

        assertNotNull( actualContentType.getFormItem( "myColor" ) );
        assertNotNull( actualContentType.getFormItem( "myDate" ) );
        assertNotNull( actualContentType.getFormItem( "myDecimalNumber" ) );
        assertNotNull( actualContentType.getFormItem( "myGeoLocation" ) );
        assertNotNull( actualContentType.getFormItem( "myHtmlArea" ) );
        assertNotNull( actualContentType.getFormItem( "myMoney" ) );
        assertNotNull( actualContentType.getFormItem( "myPhone" ) );
        assertNotNull( actualContentType.getFormItem( "mySingleSelector" ) );
        assertNotNull( actualContentType.getFormItem( "myTags" ) );
        assertNotNull( actualContentType.getFormItem( "myTextLine" ) );
        assertNotNull( actualContentType.getFormItem( "myTextArea" ) );
        assertNotNull( actualContentType.getFormItem( "myWholeNumber" ) );
        assertNotNull( actualContentType.getFormItem( "myXml" ) );
    }

    @Test
    public void given_all_input_types_when_parsed_then_paths_are_as_expected()
    {
        String serialized = toString( contentTypeWithAllComponentTypes );

        // exercise
        ContentType actualContentType = toContentType( serialized );

        // verify
        assertNotNull( actualContentType );

        assertEquals( "myColor", actualContentType.getFormItem( "myColor" ).getPath().toString() );
        assertEquals( "myDate", actualContentType.getFormItem( "myDate" ).getPath().toString() );
        assertEquals( "myDecimalNumber", actualContentType.getFormItem( "myDecimalNumber" ).getPath().toString() );
        assertEquals( "myGeoLocation", actualContentType.getFormItem( "myGeoLocation" ).getPath().toString() );
        assertEquals( "myHtmlArea", actualContentType.getFormItem( "myHtmlArea" ).getPath().toString() );
        assertEquals( "myMoney", actualContentType.getFormItem( "myMoney" ).getPath().toString() );
        assertEquals( "myPhone", actualContentType.getFormItem( "myPhone" ).getPath().toString() );
        assertEquals( "mySingleSelector", actualContentType.getFormItem( "mySingleSelector" ).getPath().toString() );
        assertEquals( "myTags", actualContentType.getFormItem( "myTags" ).getPath().toString() );
        assertEquals( "myTextLine", actualContentType.getFormItem( "myTextLine" ).getPath().toString() );
        assertEquals( "myTextArea", actualContentType.getFormItem( "myTextArea" ).getPath().toString() );
        assertEquals( "myWholeNumber", actualContentType.getFormItem( "myWholeNumber" ).getPath().toString() );
        assertEquals( "myXml", actualContentType.getFormItem( "myXml" ).getPath().toString() );
    }


    @Test
    public void given_input_in_a_set_when_parsed_then_paths_are_as_expected()
    {
        FormItemSet set = newFormItemSet().name( "mySet" ).build();
        set.add( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );

        ContentType.Builder contentTypeBuilder = ContentType.newContentType().name( "TypeWithSet" ).module( myModule );
        contentTypeBuilder.add( set );
        ContentType contentType = contentTypeBuilder.build();

        String serialized = toString( contentType );

        // exercise
        ContentType actualContentType = toContentType( serialized );
        assertNotNull( actualContentType.getFormItem( "mySet" ) );
        assertEquals( "mySet", actualContentType.getFormItem( "mySet" ).getPath().toString() );
        assertNotNull( actualContentType.getFormItem( "mySet.myTextLine" ) );
        assertEquals( "mySet.myTextLine", actualContentType.getFormItem( "mySet.myTextLine" ).getPath().toString() );
    }

    @Test
    public void parse_subType()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        ComponentSetSubType subType = newComponentSetSubType().module( module ).componentSet(
            newFormItemSet().name( "address" ).add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType.Builder contentTypeBuilder = ContentType.newContentType().name( "test" ).module( myModule );
        contentTypeBuilder.add( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        contentTypeBuilder.add( newSubTypeReference( subType ).name( "home" ).build() );
        contentTypeBuilder.add( newSubTypeReference( subType ).name( "cabin" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        String serialized = toString( contentTypeBuilder.build() );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify references
        assertEquals( SubTypeReference.class, parsedContentType.getFormItem( "home" ).getClass() );
        assertEquals( SubTypeReference.class, parsedContentType.getFormItem( "cabin" ).getClass() );
    }

    @Test
    public void given_content_type_with_componentSet_inside_componentSet_and_component_in_both_when_parse_then_paths_are_correct()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        Input myInnerInput = newInput().name( "my-inner-input" ).type( InputTypes.TEXT_LINE ).build();
        FormItemSet myInnerSet = newFormItemSet().name( "my-inner-set" ).add( myInnerInput ).build();
        Input myOuterInput = newInput().name( "my-outer-input" ).type( InputTypes.TEXT_LINE ).build();
        FormItemSet myOuterSet = newFormItemSet().name( "my-outer-set" ).add( myOuterInput ).add( myInnerSet ).build();
        contentType.addFormItem( myOuterSet );

        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "my-outer-set", parsedContentType.getFormItemSet( "my-outer-set" ).getPath().toString() );
        assertEquals( "my-outer-set.my-outer-input", parsedContentType.getInput( "my-outer-set.my-outer-input" ).getPath().toString() );
        assertEquals( "my-outer-set.my-inner-set", parsedContentType.getFormItemSet( "my-outer-set.my-inner-set" ).getPath().toString() );
        assertEquals( "my-outer-set.my-inner-set.my-inner-input",
                      parsedContentType.getInput( "my-outer-set.my-inner-set.my-inner-input" ).getPath().toString() );
    }

    @Test
    public void given_layout_with_component_inside_when_parsed_it_exists()
    {
        // setup
        FieldSet.Builder fieldSetBuilder = newFieldSet().label( "Label" ).name( "myFieldSet" );
        fieldSetBuilder.add( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() );
        FieldSet layout = fieldSetBuilder.build();

        ContentType.Builder contentTypeBuilder = ContentType.newContentType().name( "test" ).module( myModule );
        contentTypeBuilder.add( layout );

        String serialized = toString( contentTypeBuilder.build() );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "myInput", parsedContentType.getInput( "myInput" ).getPath().toString() );
        FieldSet fieldSet = (FieldSet) parsedContentType.formItemIterable().iterator().next();
        assertEquals( "myInput", fieldSet.getInput( "myInput" ).getPath().toString() );
    }

    @Test
    public void given_component_with_validationRegex_when_parsed_then_it_exists()
    {
        // setup
        ContentType.Builder contentTypeBuilder = ContentType.newContentType().name( "test" ).module( myModule );
        contentTypeBuilder.add( newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).validationRegexp( "a*c" ).build() );
        String serialized = toString( contentTypeBuilder.build() );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "a*c", parsedContentType.getInput( "myText" ).getValidationRegexp().toString() );
    }

    private ContentType toContentType( final String serialized )
    {
        return serializer.toContentType( serialized );
    }

    private String toString( final ContentType type )
    {
        String serialized = getSerializer().toString( type );
        System.out.println( "Content Type:" );
        System.out.println( serialized );
        return serialized;
    }

    protected String getJsonAsString( String fileName )
    {
        try
        {
            return toJsonString( getJson( fileName ) );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private JsonNode getJson( String fileName )
    {
        try
        {
            final ObjectMapper mapper = createObjectMapper();
            final JsonFactory factory = mapper.getJsonFactory();
            final JsonParser parser = factory.createJsonParser( getClass().getResource( fileName ) );
            return parser.readValueAsTree();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    private String toJsonString( final JsonNode value )
        throws Exception
    {
        final ObjectMapper mapper = createObjectMapper();
        return mapper.defaultPrettyPrintingWriter().writeValueAsString( value );
    }

    private static ObjectMapper createObjectMapper()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        return mapper;
    }

    private ContentType createContentTypeWithAllInputComponentTypes()
    {
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "myOption 1", "o1" ).addOption( "myOption 2",
                                                                                                                     "o2" ).build();

        ContentType.Builder contentTypeBuilder = ContentType.newContentType().name( "AllTypes" ).module( myModule );

        contentTypeBuilder.add( newInput().name( "myColor" ).type( InputTypes.COLOR ).build() );
        contentTypeBuilder.add( newInput().name( "myDate" ).type( InputTypes.DATE ).build() );
        contentTypeBuilder.add( newInput().name( "myDecimalNumber" ).type( InputTypes.DECIMAL_NUMBER ).build() );
        contentTypeBuilder.add( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() );
        contentTypeBuilder.add( newInput().name( "myHtmlArea" ).type( InputTypes.HTML_AREA ).build() );
        contentTypeBuilder.add( newInput().name( "myMoney" ).type( InputTypes.MONEY ).build() );
        contentTypeBuilder.add( newInput().name( "myPhone" ).type( InputTypes.PHONE ).build() );
        contentTypeBuilder.add(
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );
        contentTypeBuilder.add( newInput().name( "myTags" ).type( InputTypes.TAGS ).build() );
        contentTypeBuilder.add( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        contentTypeBuilder.add( newInput().name( "myTextArea" ).type( InputTypes.TEXT_AREA ).build() );
        contentTypeBuilder.add( newInput().name( "myWholeNumber" ).type( InputTypes.WHOLE_NUMBER ).build() );
        contentTypeBuilder.add( newInput().name( "myXml" ).type( InputTypes.XML ).build() );

        return contentTypeBuilder.build();
    }

}
