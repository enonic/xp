package com.enonic.wem.core.content.type;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeSerializer;
import com.enonic.wem.api.content.type.formitem.FieldSet;
import com.enonic.wem.api.content.type.formitem.FormItemPath;
import com.enonic.wem.api.content.type.formitem.FormItemSet;
import com.enonic.wem.api.content.type.formitem.FormItemSetSubType;
import com.enonic.wem.api.content.type.formitem.FormItemSetSubTypeBuilder;
import com.enonic.wem.api.content.type.formitem.FormItems;
import com.enonic.wem.api.content.type.formitem.Input;
import com.enonic.wem.api.content.type.formitem.MockSubTypeFetcher;
import com.enonic.wem.api.content.type.formitem.SubTypeReference;
import com.enonic.wem.api.content.type.formitem.inputtype.InputTypes;
import com.enonic.wem.api.content.type.formitem.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.formitem.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.formitem.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.formitem.Input.newInput;
import static com.enonic.wem.api.content.type.formitem.SubTypeReference.newSubTypeReference;
import static com.enonic.wem.api.module.Module.newModule;
import static org.junit.Assert.*;


public abstract class AbstractContentTypeSerializerTest
{
    private static final Module myModule = newModule().name( "myModule" ).build();

    private ContentTypeSerializer serializer;

    abstract ContentTypeSerializer getSerializer();

    @Before
    public void before()
    {
        this.serializer = getSerializer();
    }

    @Test
    public void parse_all_types()
    {
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "myOption 1", "o1" ).addOption( "myOption 2",
                                                                                                                     "o2" ).build();

        ContentType contentType = new ContentType();
        contentType.setName( "MyContentType" );
        contentType.setModule( myModule );
        FormItems formItems = new FormItems();
        contentType.setFormItems( formItems );
        formItems.addFormItem( newInput().name( "myDate" ).type( InputTypes.DATE ).build() );
        formItems.addFormItem(
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );
        formItems.addFormItem( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        formItems.addFormItem( newInput().name( "myTextArea" ).type( InputTypes.TEXT_AREA ).build() );
        formItems.addFormItem( newInput().name( "myPhone" ).type( InputTypes.PHONE ).build() );
        formItems.addFormItem( newInput().name( "myXml" ).type( InputTypes.XML ).build() );

        FormItemSet formItemSet = FormItemSet.newBuilder().name( "mySet" ).label( "My set" ).build();
        formItems.addFormItem( formItemSet );
        formItemSet.addItem( newInput().name( "myText1" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.addItem( newInput().name( "myText2" ).occurrences( 1, 3 ).type( InputTypes.TEXT_LINE ).build() );

        String serialized = toString( contentType );

        // exercise
        ContentType actualContentType = toContentType( serialized );

        // verify
        assertNotNull( actualContentType );
        FormItems actualFormItems = actualContentType.getFormItems();

        assertNotNull( actualFormItems );
        assertEquals( 7, actualFormItems.size() );

        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myDate" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "mySingleSelector" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myTextLine" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myTextArea" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myPhone" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myXml" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "mySet" ).getLastElement() ) );
        assertNotNull( actualFormItems.getInput( new FormItemPath( "mySet.myText1" ) ) );
        assertNotNull( actualFormItems.getInput( new FormItemPath( "mySet.myText2" ) ) );
    }

    @Test
    public void parse_subType()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        FormItemSetSubType subType = FormItemSetSubTypeBuilder.newFormItemSetSubType().module( module ).formItemSet(
            newFormItemSet().name( "address" ).add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.setModule( myModule );
        cty.addFormItem( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        cty.addFormItem( newSubTypeReference( subType ).name( "home" ).build() );
        cty.addFormItem( newSubTypeReference( subType ).name( "cabin" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        String serialized = toString( cty );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify references
        assertEquals( SubTypeReference.class, parsedContentType.getFormItems().getFormItem( "home" ).getClass() );
        assertEquals( SubTypeReference.class, parsedContentType.getFormItems().getFormItem( "cabin" ).getClass() );

        // verify items past the reference is null
        assertEquals( null, parsedContentType.getFormItems().getFormItem( "home.street" ) );
    }

    @Test
    public void given_content_type_with_formItemSet_inside_formItemSet_and_component_in_both_when_parse_then_paths_are_correct()
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
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "test" );
        FieldSet layout = newFieldSet().label( "Label" ).name( "fieldSet" ).add(
            newInput().name( "myComponent" ).type( InputTypes.TEXT_LINE ).build() ).build();
        contentType.addFormItem( layout );

        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "myComponent", parsedContentType.getInput( "myComponent" ).getPath().toString() );
        assertNotNull( parsedContentType.getFormItems().getFormItem( "fieldSet" ) );
        assertEquals( FieldSet.class, parsedContentType.getFormItems().getFormItem( "fieldSet" ).getClass() );
    }

    @Test
    public void given_component_with_validationRegex_when_parsed_then_it_exists()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "test" );
        contentType.addFormItem( newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).validationRegexp( "a*c" ).build() );
        String serialized = toString( contentType );

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
        System.out.println( serialized );
        return serialized;
    }
}
