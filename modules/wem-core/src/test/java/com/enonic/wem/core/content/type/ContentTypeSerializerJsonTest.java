package com.enonic.wem.core.content.type;

import org.junit.Test;

import com.enonic.wem.core.content.type.formitem.Component;
import com.enonic.wem.core.content.type.formitem.FormItemPath;
import com.enonic.wem.core.content.type.formitem.FormItemSet;
import com.enonic.wem.core.content.type.formitem.FormItemSetTemplate;
import com.enonic.wem.core.content.type.formitem.FormItemSetTemplateBuilder;
import com.enonic.wem.core.content.type.formitem.FormItemType;
import com.enonic.wem.core.content.type.formitem.FormItems;
import com.enonic.wem.core.content.type.formitem.MockTemplateFetcher;
import com.enonic.wem.core.content.type.formitem.VisualFieldSet;
import com.enonic.wem.core.content.type.formitem.fieldtype.DropdownConfig;
import com.enonic.wem.core.content.type.formitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.formitem.fieldtype.RadioButtonsConfig;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.formitem.Component.newBuilder;
import static com.enonic.wem.core.content.type.formitem.Component.newComponent;
import static com.enonic.wem.core.content.type.formitem.FormItemSet.newFormItemTest;
import static com.enonic.wem.core.content.type.formitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.content.type.formitem.VisualFieldSet.newVisualFieldSet;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;


public class ContentTypeSerializerJsonTest
{
    @Test
    public void generate_all_types()
    {
        // setup
        DropdownConfig dropdownConfig =
            DropdownConfig.newBuilder().addOption( "My Option 1", "o1" ).addOption( "My Option 2", "o2" ).build();
        RadioButtonsConfig myRadioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "myFirstChoice", "c1" ).addOption( "mySecondChoice", "c2" ).build();

        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myDate" ).type( FieldTypes.DATE ).build() );
        contentType.addFormItem(
            newComponent().name( "myDropdown" ).type( FieldTypes.DROPDOWN ).fieldTypeConfig( dropdownConfig ).build() );
        contentType.addFormItem( newBuilder().name( "myTextLine" ).type( FieldTypes.TEXT_LINE ).build() );
        contentType.addFormItem( newBuilder().name( "myTextArea" ).type( FieldTypes.TEXT_AREA ).build() );
        contentType.addFormItem(
            newComponent().name( "myRadiobuttons" ).type( FieldTypes.RADIO_BUTTONS ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        contentType.addFormItem( newComponent().name( "myPhone" ).type( FieldTypes.PHONE ).build() );
        contentType.addFormItem( newComponent().name( "myXml" ).type( FieldTypes.XML ).build() );

        FormItemSet formItemSet = newFormItemTest().name( "personalia" ).build();
        formItemSet.addItem( newComponent().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addItem( newComponent().name( "hairColour" ).occurrences( 1, 3 ).type( FieldTypes.TEXT_LINE ).build() );
        contentType.addFormItem( formItemSet );

        ContentTypeSerializerJson serializer = new ContentTypeSerializerJson();
        String json = serializer.toJson( contentType );

        // exercise
        ContentType parsedContentType = serializer.parse( json );

        // verify
        Component parsedMyDate = parsedContentType.getField( "myDate" );
        assertEquals( "myDate", parsedMyDate.getPath().toString() );
        assertEquals( "myDate", parsedMyDate.getName() );
        assertEquals( "com.enonic.wem.core.content.type.formitem.fieldtype.Date", parsedMyDate.getFieldType().getClassName() );
        assertEquals( "date", parsedMyDate.getFieldType().getName() );

        Component parsedMyDropdown = parsedContentType.getField( "myDropdown" );
        assertEquals( "myDropdown", parsedMyDropdown.getPath().toString() );
        assertEquals( "My Option 1", ( (DropdownConfig) parsedMyDropdown.getFieldTypeConfig() ).getOptions().get( 0 ).getLabel() );
        assertEquals( "My Option 2", ( (DropdownConfig) parsedMyDropdown.getFieldTypeConfig() ).getOptions().get( 1 ).getLabel() );
    }

    @Test
    public void generate_subtype()
    {
        FormItems formItems = new FormItems();

        formItems.addFormItem( newBuilder().name( "name" ).type( FieldTypes.TEXT_LINE ).required( true ).build() );

        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        formItems.addFormItem( formItemSet );
        formItemSet.addItem( newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addItem( newBuilder().name( "hairColour" ).occurrences( 1, 3 ).type( FieldTypes.TEXT_LINE ).build() );

        ContentType contentType = new ContentType();
        contentType.setFormItems( formItems );

        ContentTypeSerializerJson generator = new ContentTypeSerializerJson();
        String json = generator.toJson( contentType );
    }

    @Test
    public void parse_all_types()
    {
        DropdownConfig dropdownConfig = DropdownConfig.newBuilder().addOption( "myOption 1", "o1" ).addOption( "myOption 2", "o2" ).build();
        RadioButtonsConfig myRadioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "myFirstChoice", "c1" ).addOption( "mySecondChoice", "c2" ).build();

        ContentType contentType = new ContentType();
        FormItems formItems = new FormItems();
        contentType.setFormItems( formItems );
        formItems.addFormItem( newBuilder().name( "myDate" ).type( FieldTypes.DATE ).build() );
        formItems.addFormItem( newBuilder().name( "myDropdown" ).type( FieldTypes.DROPDOWN ).fieldTypeConfig( dropdownConfig ).build() );
        formItems.addFormItem( newBuilder().name( "myTextLine" ).type( FieldTypes.TEXT_LINE ).build() );
        formItems.addFormItem( newBuilder().name( "myTextArea" ).type( FieldTypes.TEXT_AREA ).build() );
        formItems.addFormItem(
            newBuilder().name( "myRadioButtons" ).type( FieldTypes.RADIO_BUTTONS ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        formItems.addFormItem( newBuilder().name( "myPhone" ).type( FieldTypes.PHONE ).build() );
        formItems.addFormItem( newBuilder().name( "myXml" ).type( FieldTypes.XML ).build() );

        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        formItems.addFormItem( formItemSet );
        formItemSet.addItem( newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addItem( newBuilder().name( "hairColour" ).occurrences( 1, 3 ).type( FieldTypes.TEXT_LINE ).build() );

        String json = new ContentTypeSerializerJson().toJson( contentType );

        // exercise
        ContentType actualContentType = new ContentTypeSerializerJson().parse( json );

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
            newFormItemTest().name( "address" ).add(
                newBuilder().name( "label" ).label( "Label" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newBuilder().name( "street" ).label( "Street" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newBuilder().name( "postalNo" ).label( "Postal No" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newBuilder().name( "country" ).label( "Country" ).type( FieldTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addFormItem( newBuilder().name( "myTextLine" ).type( FieldTypes.TEXT_LINE ).build() );
        cty.addFormItem( newTemplateReference( template ).name( "home" ).build() );
        cty.addFormItem( newTemplateReference( template ).name( "cabin" ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( template );

        String json = new ContentTypeSerializerJson().toJson( cty );

        // exercise
        ContentType parsedContentType = new ContentTypeSerializerJson().parse( json );

        // verify references
        assertEquals( FormItemType.REFERENCE, parsedContentType.getFormItems().getFormItem( "home" ).getFormItemType() );
        assertEquals( FormItemType.REFERENCE, parsedContentType.getFormItems().getFormItem( "cabin" ).getFormItemType() );

        // verify items past the reference is null
        assertEquals( null, parsedContentType.getFormItems().getFormItem( "home.street" ) );
    }

    @Test
    public void parseFieldSet_in_FieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        FormItemSet formItemSet =
            newFormItemTest().name( "top-fieldSet" ).add( newComponent().name( "myField" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newFormItemTest().name( "inner-fieldSet" ).add(
                    newComponent().name( "myInnerField" ).type( FieldTypes.TEXT_LINE ).build() ).build() ).build();
        contentType.addFormItem( formItemSet );

        String json = new ContentTypeSerializerJson().toJson( contentType );

        ContentType parsedContentType = new ContentTypeSerializerJson().parse( json );
        assertEquals( "top-fieldSet", parsedContentType.getFormItemSet( "top-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.myField", parsedContentType.getField( "top-fieldSet.myField" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet",
                      parsedContentType.getFormItemSet( "top-fieldSet.inner-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet.myInnerField",
                      parsedContentType.getField( "top-fieldSet.inner-fieldSet.myInnerField" ).getPath().toString() );
    }

    @Test
    public void visualFieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        VisualFieldSet visualFieldSet = newVisualFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newComponent().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() ).add(
            newComponent().name( "hairColour" ).type( FieldTypes.TEXT_LINE ).build() ).build();
        contentType.addFormItem( visualFieldSet );

        String json = new ContentTypeSerializerJson().toJson( contentType );

        ContentType parsedContentType = new ContentTypeSerializerJson().parse( json );
        assertEquals( "eyeColour", parsedContentType.getField( "eyeColour" ).getPath().toString() );
        assertEquals( "hairColour", parsedContentType.getField( "hairColour" ).getPath().toString() );

        assertNotNull( parsedContentType.getFormItems().getFormItem( "personalia" ) );
    }
}
