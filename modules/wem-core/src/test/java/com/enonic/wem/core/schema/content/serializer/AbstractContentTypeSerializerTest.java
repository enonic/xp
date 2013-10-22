package com.enonic.wem.core.schema.content.serializer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.acme.DummyCustomInputType;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.form.FieldSet;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.Layout;
import com.enonic.wem.api.schema.content.form.MixinReference;
import com.enonic.wem.api.schema.content.form.inputtype.ComboBoxConfig;
import com.enonic.wem.api.schema.content.form.inputtype.ImageSelectorConfig;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.form.inputtype.RelationshipConfig;
import com.enonic.wem.api.schema.content.form.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.schema.content.form.inputtype.TextAreaConfig;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MockMixinFetcher;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.AbstractSerializerTest;
import com.enonic.wem.core.schema.content.form.inputtype.InputTypeExtensions;
import com.enonic.wem.core.schema.content.form.inputtype.InputTypeResolver;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.schema.content.form.inputtype.ImageSelectorConfig.newImageSelectorConfig;
import static com.enonic.wem.api.schema.content.form.inputtype.RelationshipConfig.newRelationshipConfig;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;


public abstract class AbstractContentTypeSerializerTest
    extends AbstractSerializerTest
{
    private ContentTypeSerializer serializer;

    abstract ContentTypeSerializer getSerializer();

    private ContentType contentTypeWithAllFormItemTypes;

    private static final DummyCustomInputType MY_TYPE = new DummyCustomInputType();

    @Before
    public void before()
    {
        InputTypeExtensions inputTypeExtensions = Mockito.mock( InputTypeExtensions.class );
        Mockito.when( inputTypeExtensions.getInputType( MY_TYPE.getName() ) ).thenReturn( MY_TYPE );
        InputTypeResolver.get().setInputTypeExtensions( inputTypeExtensions );

        this.serializer = getSerializer();
        contentTypeWithAllFormItemTypes = createContentTypeWithAllInputFormItemTypes();
    }

    abstract void assertSerializedResult( String fileNameForExpected, String actualSerialization );

    @Test
    public void given_all_schemas_when_parsed_then_paths_are_as_expected()
        throws Exception
    {
        Mixin inputMixin = newMixin().name( "my_shared_input" ).addFormItem(
            Input.newInput().name( "my_shared_input" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        FormItemSet set = newFormItemSet().name( "mySet" ).build();
        Layout layout = FieldSet.newFieldSet().label( "My field set" ).name( "myFieldSet" ).add(
            newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        set.add( layout );
        set.add( newMixinReference().name( "myCommonInput" ).mixin( inputMixin ).build() );

        ContentType.Builder contentTypeBuilder = newContentType().name( "all_schemas" );
        contentTypeBuilder.addFormItem( set );
        contentTypeBuilder.displayName( "All the Base Types" );
        contentTypeBuilder.contentDisplayNameScript( "$('firstName') + ' ' + $('lastName')" );
        contentTypeBuilder.superType( QualifiedContentTypeName.from( "content" ) );
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
        assertNotNull( actualContentType.form().getFormItem( "myComboBox" ) );
        assertNotNull( actualContentType.form().getFormItem( "myDate" ) );
        assertNotNull( actualContentType.form().getFormItem( "myDecimalNumber" ) );
        assertNotNull( actualContentType.form().getFormItem( "myImage" ) );
        assertNotNull( actualContentType.form().getFormItem( "myGeoLocation" ) );
        assertNotNull( actualContentType.form().getFormItem( "myHtmlArea" ) );
        assertNotNull( actualContentType.form().getFormItem( "myMoney" ) );
        assertNotNull( actualContentType.form().getFormItem( "myPhone" ) );
        assertNotNull( actualContentType.form().getFormItem( "myRelationship" ) );
        assertNotNull( actualContentType.form().getFormItem( "mySingleSelector" ) );
        assertNotNull( actualContentType.form().getFormItem( "myTags" ) );
        assertNotNull( actualContentType.form().getFormItem( "myTextLine" ) );
        assertNotNull( actualContentType.form().getFormItem( "myTextArea_default" ) );
        assertNotNull( actualContentType.form().getFormItem( "myTextArea_10cols_10_rows" ) );
        assertNotNull( actualContentType.form().getFormItem( "myWholeNumber" ) );
        assertNotNull( actualContentType.form().getFormItem( "myXml" ) );
        assertNotNull( actualContentType.form().getFormItem( "myCustomInput" ) );
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
        assertEquals( "myComboBox", actualContentType.form().getFormItem( "myComboBox" ).getPath().toString() );
        assertEquals( "myDate", actualContentType.form().getFormItem( "myDate" ).getPath().toString() );
        assertEquals( "myDecimalNumber", actualContentType.form().getFormItem( "myDecimalNumber" ).getPath().toString() );
        assertEquals( "myGeoLocation", actualContentType.form().getFormItem( "myGeoLocation" ).getPath().toString() );
        assertEquals( "myHtmlArea", actualContentType.form().getFormItem( "myHtmlArea" ).getPath().toString() );
        assertEquals( "myMoney", actualContentType.form().getFormItem( "myMoney" ).getPath().toString() );
        assertEquals( "myPhone", actualContentType.form().getFormItem( "myPhone" ).getPath().toString() );
        assertEquals( "mySingleSelector", actualContentType.form().getFormItem( "mySingleSelector" ).getPath().toString() );
        assertEquals( "myTags", actualContentType.form().getFormItem( "myTags" ).getPath().toString() );
        assertEquals( "myTextLine", actualContentType.form().getFormItem( "myTextLine" ).getPath().toString() );
        assertEquals( "myTextArea_default", actualContentType.form().getFormItem( "myTextArea_default" ).getPath().toString() );
        assertEquals( "myTextArea_10cols_10_rows",
                      actualContentType.form().getFormItem( "myTextArea_10cols_10_rows" ).getPath().toString() );
        assertEquals( "myWholeNumber", actualContentType.form().getFormItem( "myWholeNumber" ).getPath().toString() );
        assertEquals( "myXml", actualContentType.form().getFormItem( "myXml" ).getPath().toString() );
        assertEquals( "myCustomInput", actualContentType.form().getFormItem( "myCustomInput" ).getPath().toString() );
    }


    @Test
    public void given_input_in_a_set_when_parsed_then_paths_are_as_expected()
    {
        FormItemSet set = newFormItemSet().name( "mySet" ).build();
        set.add( newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() );

        ContentType.Builder contentTypeBuilder = newContentType().name( "type_with_set" );
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
        Mixin mixin = newMixin().name( "address" ).addFormItem( newFormItemSet().name( "address" ).addFormItem(
            newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType.Builder contentTypeBuilder = newContentType().name( "test" );
        contentTypeBuilder.addFormItem( newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() );
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
        Input myInnerInput = newInput().name( "my-inner-input" ).inputType( InputTypes.TEXT_LINE ).build();
        FormItemSet myInnerSet = newFormItemSet().name( "my-inner-set" ).addFormItem( myInnerInput ).build();
        Input myOuterInput = newInput().name( "my-outer-input" ).inputType( InputTypes.TEXT_LINE ).build();
        FormItemSet myOuterSet = newFormItemSet().name( "my-outer-set" ).addFormItem( myOuterInput ).addFormItem( myInnerSet ).build();
        final ContentType contentType = newContentType().
            name( "my_type" ).
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
        fieldSetBuilder.add( newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).build() );
        FieldSet layout = fieldSetBuilder.build();

        ContentType.Builder contentTypeBuilder = newContentType().name( "test" );
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
        ContentType.Builder contentTypeBuilder = newContentType().name( "test" );
        contentTypeBuilder.addFormItem( newInput().name( "myText" ).inputType( InputTypes.TEXT_LINE ).validationRegexp( "a*c" ).build() );
        String serialized = toString( contentTypeBuilder.build() );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "a*c", parsedContentType.form().getInput( "myText" ).getValidationRegexp().toString() );
    }

    @Test
    public void xxxx()
    {
        // setup
        Input input = newInput().name( "myCustomType" ).inputType( MY_TYPE ).build();
        ContentType contentType = newContentType().
            name( "test" ).
            addFormItem( input ).build();
        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertSame( MY_TYPE, parsedContentType.form().getInput( "myCustomType" ).getInputType() );
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
        ComboBoxConfig comboBoxConfig = ComboBoxConfig.newComboBoxConfig().
            addOption( "myOption 1", "o1" ).
            addOption( "myOption 2", "o2" ).
            build();

        SingleSelectorConfig singleSelectorConfig = SingleSelectorConfig.newSingleSelectorConfig().
            typeDropdown().
            addOption( "myOption 1", "o1" ).
            addOption( "myOption 2", "o2" ).
            build();

        RelationshipConfig relationshipConfig = newRelationshipConfig().
            relationshipType( QualifiedRelationshipTypeName.LIKE ).
            build();

        ImageSelectorConfig imageSelectorConfig = newImageSelectorConfig().
            relationshipType( QualifiedRelationshipTypeName.DEFAULT ).
            build();

        ContentType.Builder contentTypeBuilder = newContentType().
            name( "all_input_types" ).
            displayName( "All the Input Types" ).
            superType( QualifiedContentTypeName.structured() ).
            setAbstract( false ).
            setFinal( true );

        contentTypeBuilder.addFormItem( newInput().name( "myColor" ).inputType( InputTypes.COLOR ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myDate" ).inputType( InputTypes.DATE ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myDecimalNumber" ).inputType( InputTypes.DECIMAL_NUMBER ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myGeoLocation" ).inputType( InputTypes.GEO_LOCATION ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myHtmlArea" ).inputType( InputTypes.HTML_AREA ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myMoney" ).inputType( InputTypes.MONEY ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myPhone" ).inputType( InputTypes.PHONE ).build() );
        contentTypeBuilder.addFormItem(
            newInput().name( "myComboBox" ).inputType( InputTypes.COMBO_BOX ).inputTypeConfig( comboBoxConfig ).build() );
        contentTypeBuilder.addFormItem(
            newInput().name( "mySingleSelector" ).inputType( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myTags" ).inputType( InputTypes.TAGS ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myTextArea_default" ).inputType( InputTypes.TEXT_AREA ).inputTypeConfig(
            InputTypes.TEXT_AREA.getDefaultConfig() ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myTextArea_10cols_10_rows" ).inputType( InputTypes.TEXT_AREA ).inputTypeConfig(
            TextAreaConfig.newTextAreaConfig().rows( 10 ).columns( 10 ).build() ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myWholeNumber" ).inputType( InputTypes.WHOLE_NUMBER ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "myXml" ).inputType( InputTypes.XML ).build() );
        contentTypeBuilder.addFormItem(
            newInput().name( "myRelationship" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( relationshipConfig ).build() );
        contentTypeBuilder.addFormItem(
            newInput().name( "myImage" ).inputType( InputTypes.IMAGE_SELECTOR ).inputTypeConfig( imageSelectorConfig ).build() );

        contentTypeBuilder.addFormItem( newInput().name( "myCustomInput" ).inputType( new DummyCustomInputType() ).build() );

        return contentTypeBuilder.build();
    }


}
