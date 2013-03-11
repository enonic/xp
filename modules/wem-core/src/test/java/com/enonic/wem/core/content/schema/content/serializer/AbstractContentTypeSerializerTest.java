package com.enonic.wem.core.content.schema.content.serializer;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.form.FieldSet;
import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.Layout;
import com.enonic.wem.api.content.schema.content.form.MixinReference;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.content.schema.content.form.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.MockMixinFetcher;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.AbstractSerializerTest;

import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static com.enonic.wem.api.content.schema.content.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.content.schema.mixin.Mixin.newMixin;
import static com.enonic.wem.api.module.Module.newModule;
import static org.junit.Assert.*;


public abstract class AbstractContentTypeSerializerTest
    extends AbstractSerializerTest
{
    private static final Module myModule = newModule().name( "myModule" ).build();

    private ContentTypeSerializer serializer;

    abstract ContentTypeSerializer getSerializer();

    private ContentType contentTypeWithAllFormItemTypes;

    @Before
    public void before()
    {
        this.serializer = getSerializer();
        contentTypeWithAllFormItemTypes = createContentTypeWithAllInputFormItemTypes();
    }

    abstract void assertSerializedResult( String fileNameForExpected, String actualSerialization );

    @Test
    public void given_all_schemas_when_parsed_then_paths_are_as_expected()
        throws Exception
    {
        Mixin inputMixin = newMixin().formItem( Input.newInput().name( "mySharedInput" ).type( InputTypes.TEXT_LINE ).build() ).module(
            Module.SYSTEM.getName() ).build();
        FormItemSet set = newFormItemSet().name( "mySet" ).build();
        Layout layout = FieldSet.newFieldSet().label( "My field set" ).name( "myFieldSet" ).add(
            newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() ).build();
        set.add( layout );
        set.add( newMixinReference().name( "myCommonInput" ).mixin( inputMixin ).build() );

        ContentType.Builder contentTypeBuilder = newContentType().name( "AllSchemas" ).module( myModule.getName() );
        contentTypeBuilder.addFormItem( set );
        contentTypeBuilder.displayName( "All the Base Types" );
        contentTypeBuilder.contentDisplayNameScript( "$('firstName') + ' ' + $('lastName')" );
        contentTypeBuilder.superType( new QualifiedContentTypeName( "System:Content" ) );
        contentTypeBuilder.setAbstract( false );
        contentTypeBuilder.setFinal( true );

        ContentType contentType = contentTypeBuilder.build();

        String actualSerialization = toString( contentType );

        // exercise
        ContentType actualContentType = toContentType( actualSerialization );

        // verify serialization
        assertSerializedResult( "contentType-allSchemaKinds", actualSerialization );

        // verify
        assertNotNull( actualContentType.form().getFormItem( "mySet" ) );
        assertEquals( "mySet", actualContentType.form().getFormItem( "mySet" ).getPath().toString() );
        assertNotNull( actualContentType.form().getFormItem( "mySet.myTextLine" ) );
        assertEquals( "mySet.myTextLine", actualContentType.form().getFormItem( "mySet.myTextLine" ).getPath().toString() );
        assertEquals( "mySet.myCommonInput", actualContentType.form().getFormItem( "mySet.myCommonInput" ).getPath().toString() );
    }

    @Test
    public void toString_all_input_types()
    {
        String actualSerialization = toString( contentTypeWithAllFormItemTypes );

        // exercise
        ContentType actualContentType = toContentType( actualSerialization );

        // verify:
        assertNotNull( actualContentType );

        // verify serialization
        assertSerializedResult( "contentType-allInputTypes", actualSerialization );

        assertNotNull( actualContentType.form().getFormItem( "myColor" ) );
        assertNotNull( actualContentType.form().getFormItem( "myDate" ) );
        assertNotNull( actualContentType.form().getFormItem( "myDecimalNumber" ) );
        assertNotNull( actualContentType.form().getFormItem( "myGeoLocation" ) );
        assertNotNull( actualContentType.form().getFormItem( "myHtmlArea" ) );
        assertNotNull( actualContentType.form().getFormItem( "myMoney" ) );
        assertNotNull( actualContentType.form().getFormItem( "myPhone" ) );
        assertNotNull( actualContentType.form().getFormItem( "mySingleSelector" ) );
        assertNotNull( actualContentType.form().getFormItem( "myTags" ) );
        assertNotNull( actualContentType.form().getFormItem( "myTextLine" ) );
        assertNotNull( actualContentType.form().getFormItem( "myTextArea" ) );
        assertNotNull( actualContentType.form().getFormItem( "myWholeNumber" ) );
        assertNotNull( actualContentType.form().getFormItem( "myXml" ) );
    }

    @Test
    public void given_all_input_types_when_parsed_then_paths_are_as_expected()
    {
        String serialized = toString( contentTypeWithAllFormItemTypes );

        // exercise
        ContentType actualContentType = toContentType( serialized );

        // verify
        assertNotNull( actualContentType );

        assertEquals( "myColor", actualContentType.form().getFormItem( "myColor" ).getPath().toString() );
        assertEquals( "myDate", actualContentType.form().getFormItem( "myDate" ).getPath().toString() );
        assertEquals( "myDecimalNumber", actualContentType.form().getFormItem( "myDecimalNumber" ).getPath().toString() );
        assertEquals( "myGeoLocation", actualContentType.form().getFormItem( "myGeoLocation" ).getPath().toString() );
        assertEquals( "myHtmlArea", actualContentType.form().getFormItem( "myHtmlArea" ).getPath().toString() );
        assertEquals( "myMoney", actualContentType.form().getFormItem( "myMoney" ).getPath().toString() );
        assertEquals( "myPhone", actualContentType.form().getFormItem( "myPhone" ).getPath().toString() );
        assertEquals( "mySingleSelector", actualContentType.form().getFormItem( "mySingleSelector" ).getPath().toString() );
        assertEquals( "myTags", actualContentType.form().getFormItem( "myTags" ).getPath().toString() );
        assertEquals( "myTextLine", actualContentType.form().getFormItem( "myTextLine" ).getPath().toString() );
        assertEquals( "myTextArea", actualContentType.form().getFormItem( "myTextArea" ).getPath().toString() );
        assertEquals( "myWholeNumber", actualContentType.form().getFormItem( "myWholeNumber" ).getPath().toString() );
        assertEquals( "myXml", actualContentType.form().getFormItem( "myXml" ).getPath().toString() );
    }


    @Test
    public void given_input_in_a_set_when_parsed_then_paths_are_as_expected()
    {
        FormItemSet set = newFormItemSet().name( "mySet" ).build();
        set.add( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );

        ContentType.Builder contentTypeBuilder = newContentType().name( "TypeWithSet" ).module( myModule.getName() );
        contentTypeBuilder.addFormItem( set );
        ContentType contentType = contentTypeBuilder.build();

        String serialized = toString( contentType );

        // exercise
        ContentType actualContentType = toContentType( serialized );
        assertNotNull( actualContentType.form().getFormItem( "mySet" ) );
        assertEquals( "mySet", actualContentType.form().getFormItem( "mySet" ).getPath().toString() );
        assertNotNull( actualContentType.form().getFormItem( "mySet.myTextLine" ) );
        assertEquals( "mySet.myTextLine", actualContentType.form().getFormItem( "mySet.myTextLine" ).getPath().toString() );
    }

    @Test
    public void parse_mixin()
    {
        // setup
        Mixin mixin = newMixin().module( ModuleName.from( "myModule" ) ).formItem(
            newFormItemSet().name( "address" ).add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType.Builder contentTypeBuilder = newContentType().name( "test" ).module( myModule.getName() );
        contentTypeBuilder.addFormItem( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        contentTypeBuilder.addFormItem( MixinReference.newMixinReference( mixin ).name( "home" ).build() );
        contentTypeBuilder.addFormItem( MixinReference.newMixinReference( mixin ).name( "cabin" ).build() );

        MockMixinFetcher mixinFetcher = new MockMixinFetcher();
        mixinFetcher.add( mixin );

        String serialized = toString( contentTypeBuilder.build() );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify references
        assertEquals( MixinReference.class, parsedContentType.form().getFormItem( "home" ).getClass() );
        assertEquals( MixinReference.class, parsedContentType.form().getFormItem( "cabin" ).getClass() );
    }

    @Test
    public void given_content_type_with_formItemSet_inside_formItemSet_and_formItem_in_both_when_parse_then_paths_are_correct()
    {
        // setup
        Input myInnerInput = newInput().name( "my-inner-input" ).type( InputTypes.TEXT_LINE ).build();
        FormItemSet myInnerSet = newFormItemSet().name( "my-inner-set" ).add( myInnerInput ).build();
        Input myOuterInput = newInput().name( "my-outer-input" ).type( InputTypes.TEXT_LINE ).build();
        FormItemSet myOuterSet = newFormItemSet().name( "my-outer-set" ).add( myOuterInput ).add( myInnerSet ).build();
        final ContentType contentType = newContentType().
            name( "myType" ).
            module( myModule.getName() ).
            addFormItem( myOuterSet ).
            build();

        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "my-outer-set", parsedContentType.form().getFormItemSet( "my-outer-set" ).getPath().toString() );
        assertEquals( "my-outer-set.my-outer-input",
                      parsedContentType.form().getInput( "my-outer-set.my-outer-input" ).getPath().toString() );
        assertEquals( "my-outer-set.my-inner-set",
                      parsedContentType.form().getFormItemSet( "my-outer-set.my-inner-set" ).getPath().toString() );
        assertEquals( "my-outer-set.my-inner-set.my-inner-input",
                      parsedContentType.form().getInput( "my-outer-set.my-inner-set.my-inner-input" ).getPath().toString() );
    }

    @Test
    public void given_layout_with_formItem_inside_when_parsed_it_exists()
    {
        // setup
        FieldSet.Builder fieldSetBuilder = newFieldSet().label( "Label" ).name( "myFieldSet" );
        fieldSetBuilder.add( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() );
        FieldSet layout = fieldSetBuilder.build();

        ContentType.Builder contentTypeBuilder = newContentType().name( "test" ).module( myModule.getName() );
        contentTypeBuilder.addFormItem( layout );

        String serialized = toString( contentTypeBuilder.build() );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "myInput", parsedContentType.form().getInput( "myInput" ).getPath().toString() );
        FieldSet fieldSet = (FieldSet) parsedContentType.form().iterator().next();
        assertEquals( "myInput", fieldSet.getInput( "myInput" ).getPath().toString() );
    }

    @Test
    public void given_formItem_with_validationRegex_when_parsed_then_it_exists()
    {
        // setup
        ContentType.Builder contentTypeBuilder = newContentType().name( "test" ).module( myModule.getName() );
        contentTypeBuilder.addFormItem( newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).validationRegexp( "a*c" ).build() );
        String serialized = toString( contentTypeBuilder.build() );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "a*c", parsedContentType.form().getInput( "myText" ).getValidationRegexp().toString() );
    }

    private ContentType toContentType( final String serialized )
    {
        return serializer.toContentType( serialized );
    }

    private String toString( final ContentType type )
    {
        String serialized = getSerializer().toString( type );
        System.out.println( "ContentType:" );
        System.out.println( serialized );
        return serialized;
    }

    private ContentType createContentTypeWithAllInputFormItemTypes()
    {
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "myOption 1", "o1" ).addOption( "myOption 2",
                                                                                                                     "o2" ).build();

        ContentType.Builder contentTypeBuilder = newContentType().
            name( "AllInputTypes" ).
            module( myModule.getName() ).
            displayName( "All the Input Types" ).
            superType( new QualifiedContentTypeName( "System:Content" ) ).
            setAbstract( false ).
            setFinal( true );

        contentTypeBuilder.addFormItem( newInput().name( "myColor" ).type( InputTypes.COLOR ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myDate" ).type( InputTypes.DATE ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myDecimalNumber" ).type( InputTypes.DECIMAL_NUMBER ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myHtmlArea" ).type( InputTypes.HTML_AREA ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myMoney" ).type( InputTypes.MONEY ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myPhone" ).type( InputTypes.PHONE ).build() );
        contentTypeBuilder.addFormItem(
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myTags" ).type( InputTypes.TAGS ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myTextArea" ).type( InputTypes.TEXT_AREA ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myWholeNumber" ).type( InputTypes.WHOLE_NUMBER ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myXml" ).type( InputTypes.XML ).build() );

        return contentTypeBuilder.build();
    }

}
