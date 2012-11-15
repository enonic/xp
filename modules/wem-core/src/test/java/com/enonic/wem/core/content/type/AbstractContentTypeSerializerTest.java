package com.enonic.wem.core.content.type;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.ComponentSetSubType;
import com.enonic.wem.api.content.type.component.FieldSet;
import com.enonic.wem.api.content.type.component.Input;
import com.enonic.wem.api.content.type.component.InputSubType;
import com.enonic.wem.api.content.type.component.Layout;
import com.enonic.wem.api.content.type.component.MockSubTypeFetcher;
import com.enonic.wem.api.content.type.component.SubTypeReference;
import com.enonic.wem.api.content.type.component.inputtype.InputTypes;
import com.enonic.wem.api.content.type.component.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.ComponentSetSubType.newComponentSetSubType;
import static com.enonic.wem.api.content.type.component.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.component.Input.newInput;
import static com.enonic.wem.api.content.type.component.SubTypeReference.newSubTypeReference;
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

    private ContentType createContentTypeWithAllInputComponentTypes()
    {
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "myOption 1", "o1" ).addOption( "myOption 2",
                                                                                                                     "o2" ).build();

        ContentType.Builder contentTypeBuilder = ContentType.newComponentType().name( "AllTypes" ).module( myModule );

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

    @Test
    public void toString_all_input_types()
    {
        String serialized = toString( contentTypeWithAllComponentTypes );

        // exercise
        ContentType actualContentType = toContentType( serialized );

        // verify:
        assertNotNull( actualContentType );

        assertNotNull( actualContentType.getComponent( "myColor" ) );
        assertNotNull( actualContentType.getComponent( "myDate" ) );
        assertNotNull( actualContentType.getComponent( "myDecimalNumber" ) );
        assertNotNull( actualContentType.getComponent( "myGeoLocation" ) );
        assertNotNull( actualContentType.getComponent( "myHtmlArea" ) );
        assertNotNull( actualContentType.getComponent( "myMoney" ) );
        assertNotNull( actualContentType.getComponent( "myPhone" ) );
        assertNotNull( actualContentType.getComponent( "mySingleSelector" ) );
        assertNotNull( actualContentType.getComponent( "myTags" ) );
        assertNotNull( actualContentType.getComponent( "myTextLine" ) );
        assertNotNull( actualContentType.getComponent( "myTextArea" ) );
        assertNotNull( actualContentType.getComponent( "myWholeNumber" ) );
        assertNotNull( actualContentType.getComponent( "myXml" ) );
    }

    @Test
    public void given_all_input_types_when_parsed_then_paths_are_as_expected()
    {
        String serialized = toString( contentTypeWithAllComponentTypes );

        // exercise
        ContentType actualContentType = toContentType( serialized );

        // verify
        assertNotNull( actualContentType );

        assertEquals( "myColor", actualContentType.getComponent( "myColor" ).getPath().toString() );
        assertEquals( "myDate", actualContentType.getComponent( "myDate" ).getPath().toString() );
        assertEquals( "myDecimalNumber", actualContentType.getComponent( "myDecimalNumber" ).getPath().toString() );
        assertEquals( "myGeoLocation", actualContentType.getComponent( "myGeoLocation" ).getPath().toString() );
        assertEquals( "myHtmlArea", actualContentType.getComponent( "myHtmlArea" ).getPath().toString() );
        assertEquals( "myMoney", actualContentType.getComponent( "myMoney" ).getPath().toString() );
        assertEquals( "myPhone", actualContentType.getComponent( "myPhone" ).getPath().toString() );
        assertEquals( "mySingleSelector", actualContentType.getComponent( "mySingleSelector" ).getPath().toString() );
        assertEquals( "myTags", actualContentType.getComponent( "myTags" ).getPath().toString() );
        assertEquals( "myTextLine", actualContentType.getComponent( "myTextLine" ).getPath().toString() );
        assertEquals( "myTextArea", actualContentType.getComponent( "myTextArea" ).getPath().toString() );
        assertEquals( "myWholeNumber", actualContentType.getComponent( "myWholeNumber" ).getPath().toString() );
        assertEquals( "myXml", actualContentType.getComponent( "myXml" ).getPath().toString() );
    }

    @Test
    public void given_all_base_types_when_parsed_then_paths_are_as_expected()
    {
        InputSubType inputSubType =
            InputSubType.newInputSubType().input( Input.newInput().name( "mySharedInput" ).type( InputTypes.TEXT_LINE ).build() ).module(
                Module.SYSTEM ).build();
        ComponentSet set = newComponentSet().name( "mySet" ).build();
        Layout layout = FieldSet.newFieldSet().label( "My field set" ).name( "myFieldSet" ).add(
            newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() ).build();
        set.add( layout );
        set.add( newSubTypeReference().name( "myCommonInput" ).subType( inputSubType ).build() );

        ContentType.Builder contentTypeBuilder = ContentType.newComponentType().name( "AllBaseTypes" ).module( myModule );
        contentTypeBuilder.add( set );
        ContentType contentType = contentTypeBuilder.build();

        String serialized = toString( contentType );

        // exercise
        ContentType actualContentType = toContentType( serialized );
        assertNotNull( actualContentType.getComponent( "mySet" ) );
        assertEquals( "mySet", actualContentType.getComponent( "mySet" ).getPath().toString() );
        assertNotNull( actualContentType.getComponent( "mySet.myTextLine" ) );
        assertEquals( "mySet.myTextLine", actualContentType.getComponent( "mySet.myTextLine" ).getPath().toString() );
        assertEquals( "mySet.myCommonInput", actualContentType.getComponent( "mySet.myCommonInput" ).getPath().toString() );
    }

    @Test
    public void given_input_in_a_set_when_parsed_then_paths_are_as_expected()
    {
        ComponentSet set = newComponentSet().name( "mySet" ).build();
        set.add( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );

        ContentType.Builder contentTypeBuilder = ContentType.newComponentType().name( "TypeWithSet" ).module( myModule );
        contentTypeBuilder.add( set );
        ContentType contentType = contentTypeBuilder.build();

        String serialized = toString( contentType );

        // exercise
        ContentType actualContentType = toContentType( serialized );
        assertNotNull( actualContentType.getComponent( "mySet" ) );
        assertEquals( "mySet", actualContentType.getComponent( "mySet" ).getPath().toString() );
        assertNotNull( actualContentType.getComponent( "mySet.myTextLine" ) );
        assertEquals( "mySet.myTextLine", actualContentType.getComponent( "mySet.myTextLine" ).getPath().toString() );
    }

    @Test
    public void parse_subType()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        ComponentSetSubType subType = newComponentSetSubType().module( module ).componentSet(
            newComponentSet().name( "address" ).add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType.Builder contentTypeBuilder = ContentType.newComponentType().name( "test" ).module( myModule );
        contentTypeBuilder.add( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        contentTypeBuilder.add( newSubTypeReference( subType ).name( "home" ).build() );
        contentTypeBuilder.add( newSubTypeReference( subType ).name( "cabin" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        String serialized = toString( contentTypeBuilder.build() );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify references
        assertEquals( SubTypeReference.class, parsedContentType.getComponent( "home" ).getClass() );
        assertEquals( SubTypeReference.class, parsedContentType.getComponent( "cabin" ).getClass() );
    }

    @Test
    public void given_content_type_with_componentSet_inside_componentSet_and_component_in_both_when_parse_then_paths_are_correct()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        Input myInnerInput = newInput().name( "my-inner-input" ).type( InputTypes.TEXT_LINE ).build();
        ComponentSet myInnerSet = newComponentSet().name( "my-inner-set" ).add( myInnerInput ).build();
        Input myOuterInput = newInput().name( "my-outer-input" ).type( InputTypes.TEXT_LINE ).build();
        ComponentSet myOuterSet = newComponentSet().name( "my-outer-set" ).add( myOuterInput ).add( myInnerSet ).build();
        contentType.addComponent( myOuterSet );

        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "my-outer-set", parsedContentType.getComponentSet( "my-outer-set" ).getPath().toString() );
        assertEquals( "my-outer-set.my-outer-input", parsedContentType.getInput( "my-outer-set.my-outer-input" ).getPath().toString() );
        assertEquals( "my-outer-set.my-inner-set", parsedContentType.getComponentSet( "my-outer-set.my-inner-set" ).getPath().toString() );
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

        ContentType.Builder contentTypeBuilder = ContentType.newComponentType().name( "test" ).module( myModule );
        contentTypeBuilder.add( layout );

        String serialized = toString( contentTypeBuilder.build() );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "myInput", parsedContentType.getInput( "myInput" ).getPath().toString() );
        FieldSet fieldSet = (FieldSet) parsedContentType.componentIterable().iterator().next();
        assertEquals( "myInput", fieldSet.getInput( "myInput" ).getPath().toString() );
    }

    @Test
    public void given_component_with_validationRegex_when_parsed_then_it_exists()
    {
        // setup
        ContentType.Builder contentTypeBuilder = ContentType.newComponentType().name( "test" ).module( myModule );
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
}
