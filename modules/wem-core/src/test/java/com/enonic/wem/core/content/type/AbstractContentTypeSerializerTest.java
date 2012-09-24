package com.enonic.wem.core.content.type;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeSerializer;
import com.enonic.wem.api.content.type.formitem.Component;
import com.enonic.wem.api.content.type.formitem.FieldSet;
import com.enonic.wem.api.content.type.formitem.FormItemPath;
import com.enonic.wem.api.content.type.formitem.FormItemSet;
import com.enonic.wem.api.content.type.formitem.FormItemSetTemplate;
import com.enonic.wem.api.content.type.formitem.FormItemSetTemplateBuilder;
import com.enonic.wem.api.content.type.formitem.FormItems;
import com.enonic.wem.api.content.type.formitem.MockTemplateFetcher;
import com.enonic.wem.api.content.type.formitem.TemplateReference;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.api.content.type.formitem.comptype.DropdownConfig;
import com.enonic.wem.api.content.type.formitem.comptype.RadioButtonsConfig;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.formitem.Component.newBuilder;
import static com.enonic.wem.api.content.type.formitem.Component.newComponent;
import static com.enonic.wem.api.content.type.formitem.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.formitem.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.formitem.TemplateReference.newTemplateReference;
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
    public void generate_all_types()
    {
        // setup
        DropdownConfig dropdownConfig =
            DropdownConfig.newBuilder().addOption( "My Option 1", "o1" ).addOption( "My Option 2", "o2" ).build();
        RadioButtonsConfig myRadioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "myFirstChoice", "c1" ).addOption( "mySecondChoice", "c2" ).build();

        ContentType contentType = new ContentType();
        contentType.setModule( myModule );

        contentType.addFormItem( newComponent().name( "myDate" ).type( ComponentTypes.DATE ).build() );
        contentType.addFormItem(
            newComponent().name( "myDropdown" ).type( ComponentTypes.DROPDOWN ).componentTypeConfig( dropdownConfig ).build() );
        contentType.addFormItem( newBuilder().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.addFormItem( newBuilder().name( "myTextArea" ).type( ComponentTypes.TEXT_AREA ).build() );
        contentType.addFormItem( newComponent().name( "myRadiobuttons" ).type( ComponentTypes.RADIO_BUTTONS ).componentTypeConfig(
            myRadioButtonsConfig ).build() );
        contentType.addFormItem( newComponent().name( "myPhone" ).type( ComponentTypes.PHONE ).build() );
        contentType.addFormItem( newComponent().name( "myXml" ).type( ComponentTypes.XML ).build() );

        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        Component parsedMyDate = parsedContentType.getComponent( "myDate" );
        assertEquals( "myDate", parsedMyDate.getPath().toString() );
        assertEquals( "myDate", parsedMyDate.getName() );
        assertEquals( "com.enonic.wem.core.content.type.formitem.comptype.Date", parsedMyDate.getComponentType().getClassName() );
        assertEquals( "date", parsedMyDate.getComponentType().getName() );

        Component parsedMyDropdown = parsedContentType.getComponent( "myDropdown" );
        assertEquals( "myDropdown", parsedMyDropdown.getPath().toString() );
        assertEquals( "My Option 1", ( (DropdownConfig) parsedMyDropdown.getComponentTypeConfig() ).getOptions().get( 0 ).getLabel() );
        assertEquals( "My Option 2", ( (DropdownConfig) parsedMyDropdown.getComponentTypeConfig() ).getOptions().get( 1 ).getLabel() );
    }

    @Test
    public void parse_all_types()
    {
        DropdownConfig dropdownConfig = DropdownConfig.newBuilder().addOption( "myOption 1", "o1" ).addOption( "myOption 2", "o2" ).build();
        RadioButtonsConfig myRadioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "myFirstChoice", "c1" ).addOption( "mySecondChoice", "c2" ).build();

        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        FormItems formItems = new FormItems();
        contentType.setFormItems( formItems );
        formItems.addFormItem( newBuilder().name( "myDate" ).type( ComponentTypes.DATE ).build() );
        formItems.addFormItem(
            newBuilder().name( "myDropdown" ).type( ComponentTypes.DROPDOWN ).componentTypeConfig( dropdownConfig ).build() );
        formItems.addFormItem( newBuilder().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItems.addFormItem( newBuilder().name( "myTextArea" ).type( ComponentTypes.TEXT_AREA ).build() );
        formItems.addFormItem( newBuilder().name( "myRadioButtons" ).type( ComponentTypes.RADIO_BUTTONS ).componentTypeConfig(
            myRadioButtonsConfig ).build() );
        formItems.addFormItem( newBuilder().name( "myPhone" ).type( ComponentTypes.PHONE ).build() );
        formItems.addFormItem( newBuilder().name( "myXml" ).type( ComponentTypes.XML ).build() );

        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        formItems.addFormItem( formItemSet );
        formItemSet.addItem( newBuilder().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( newBuilder().name( "hairColour" ).occurrences( 1, 3 ).type( ComponentTypes.TEXT_LINE ).build() );

        String serialized = toString( contentType );

        // exercise
        ContentType actualContentType = toContentType( serialized );

        // verify
        assertNotNull( actualContentType );
        FormItems actualFormItems = actualContentType.getFormItems();

        assertNotNull( actualFormItems );
        assertEquals( 8, actualFormItems.size() );

        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myDate" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myDropdown" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myTextLine" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myTextArea" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myRadioButtons" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myPhone" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "myXml" ).getLastElement() ) );
        assertNotNull( actualFormItems.getFormItem( new FormItemPath( "personalia" ).getLastElement() ) );
    }

    @Test
    public void parse_template()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        FormItemSetTemplate template = FormItemSetTemplateBuilder.newFormItemSetTemplate().module( module ).formItemSet(
            newFormItemSet().name( "address" ).add(
                newBuilder().name( "label" ).label( "Label" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                newBuilder().name( "street" ).label( "Street" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                newBuilder().name( "postalNo" ).label( "Postal No" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                newBuilder().name( "country" ).label( "Country" ).type( ComponentTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.setModule( myModule );
        cty.addFormItem( newBuilder().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).build() );
        cty.addFormItem( newTemplateReference( template ).name( "home" ).build() );
        cty.addFormItem( newTemplateReference( template ).name( "cabin" ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( template );

        String serialized = toString( cty );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify references
        assertEquals( TemplateReference.class, parsedContentType.getFormItems().getFormItem( "home" ).getClass() );
        assertEquals( TemplateReference.class, parsedContentType.getFormItems().getFormItem( "cabin" ).getClass() );

        // verify items past the reference is null
        assertEquals( null, parsedContentType.getFormItems().getFormItem( "home.street" ) );
    }

    @Test
    public void given_content_type_with_formItemSet_inside_formItemSet_and_component_in_both_when_parse_then_paths_are_correct()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        Component myInnerComponent = newComponent().name( "my-inner-component" ).type( ComponentTypes.TEXT_LINE ).build();
        FormItemSet myInnerSet = newFormItemSet().name( "my-inner-set" ).add( myInnerComponent ).build();
        Component myOuterComponent = newComponent().name( "my-outer-component" ).type( ComponentTypes.TEXT_LINE ).build();
        FormItemSet myOuterSet = newFormItemSet().name( "my-outer-set" ).add( myOuterComponent ).add( myInnerSet ).build();
        contentType.addFormItem( myOuterSet );

        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "my-outer-set", parsedContentType.getFormItemSet( "my-outer-set" ).getPath().toString() );
        assertEquals( "my-outer-set.my-outer-component",
                      parsedContentType.getComponent( "my-outer-set.my-outer-component" ).getPath().toString() );
        assertEquals( "my-outer-set.my-inner-set", parsedContentType.getFormItemSet( "my-outer-set.my-inner-set" ).getPath().toString() );
        assertEquals( "my-outer-set.my-inner-set.my-inner-component",
                      parsedContentType.getComponent( "my-outer-set.my-inner-set.my-inner-component" ).getPath().toString() );
    }

    @Test
    public void given_layout_with_component_inside_when_parsed_it_exists()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "test" );
        FieldSet layout = newFieldSet().label( "Label" ).name( "fieldSet" ).add(
            newComponent().name( "myComponent" ).type( ComponentTypes.TEXT_LINE ).build() ).build();
        contentType.addFormItem( layout );

        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "myComponent", parsedContentType.getComponent( "myComponent" ).getPath().toString() );
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
        contentType.addFormItem( newComponent().name( "myText" ).type( ComponentTypes.TEXT_LINE ).validationRegexp( "a*c" ).build() );
        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "a*c", parsedContentType.getComponent( "myText" ).getValidationRegexp().toString() );
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
