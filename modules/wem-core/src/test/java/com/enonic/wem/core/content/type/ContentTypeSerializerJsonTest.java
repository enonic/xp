package com.enonic.wem.core.content.type;

import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.Component;
import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItemType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplate;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplateBuilder;
import com.enonic.wem.core.content.type.configitem.FormItemSet;
import com.enonic.wem.core.content.type.configitem.MockTemplateFetcher;
import com.enonic.wem.core.content.type.configitem.VisualFieldSet;
import com.enonic.wem.core.content.type.configitem.fieldtype.DropdownConfig;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.configitem.fieldtype.RadioButtonsConfig;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.Component.newBuilder;
import static com.enonic.wem.core.content.type.configitem.Component.newField;
import static com.enonic.wem.core.content.type.configitem.FormItemSet.newFieldSet;
import static com.enonic.wem.core.content.type.configitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.content.type.configitem.VisualFieldSet.newVisualFieldSet;
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
        contentType.addConfigItem( newField().name( "myDate" ).type( FieldTypes.DATE ).build() );
        contentType.addConfigItem( newField().name( "myDropdown" ).type( FieldTypes.DROPDOWN ).fieldTypeConfig( dropdownConfig ).build() );
        contentType.addConfigItem( newBuilder().name( "myTextLine" ).type( FieldTypes.TEXT_LINE ).build() );
        contentType.addConfigItem( newBuilder().name( "myTextArea" ).type( FieldTypes.TEXT_AREA ).build() );
        contentType.addConfigItem(
            newField().name( "myRadiobuttons" ).type( FieldTypes.RADIO_BUTTONS ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        contentType.addConfigItem( newField().name( "myPhone" ).type( FieldTypes.PHONE ).build() );
        contentType.addConfigItem( newField().name( "myXml" ).type( FieldTypes.XML ).build() );

        FormItemSet formItemSet = newFieldSet().name( "personalia" ).build();
        formItemSet.addField( newField().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addField( newField().name( "hairColour" ).occurrences( 1, 3 ).type( FieldTypes.TEXT_LINE ).build() );
        contentType.addConfigItem( formItemSet );

        ContentTypeSerializerJson serializer = new ContentTypeSerializerJson();
        String json = serializer.toJson( contentType );

        // exercise
        ContentType parsedContentType = serializer.parse( json );

        // verify
        Component parsedMyDate = parsedContentType.getField( "myDate" );
        assertEquals( "myDate", parsedMyDate.getPath().toString() );
        assertEquals( "myDate", parsedMyDate.getName() );
        assertEquals( "com.enonic.wem.core.content.type.configitem.fieldtype.Date", parsedMyDate.getFieldType().getClassName() );
        assertEquals( "date", parsedMyDate.getFieldType().getName() );

        Component parsedMyDropdown = parsedContentType.getField( "myDropdown" );
        assertEquals( "myDropdown", parsedMyDropdown.getPath().toString() );
        assertEquals( "My Option 1", ( (DropdownConfig) parsedMyDropdown.getFieldTypeConfig() ).getOptions().get( 0 ).getLabel() );
        assertEquals( "My Option 2", ( (DropdownConfig) parsedMyDropdown.getFieldTypeConfig() ).getOptions().get( 1 ).getLabel() );
    }

    @Test
    public void generate_subtype()
    {
        ConfigItems configItems = new ConfigItems();

        configItems.addConfigItem( newBuilder().name( "name" ).type( FieldTypes.TEXT_LINE ).required( true ).build() );

        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        configItems.addConfigItem( formItemSet );
        formItemSet.addField( newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addField( newBuilder().name( "hairColour" ).occurrences( 1, 3 ).type( FieldTypes.TEXT_LINE ).build() );

        ContentType contentType = new ContentType();
        contentType.setConfigItems( configItems );

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
        ConfigItems configItems = new ConfigItems();
        contentType.setConfigItems( configItems );
        configItems.addConfigItem( newBuilder().name( "myDate" ).type( FieldTypes.DATE ).build() );
        configItems.addConfigItem(
            newBuilder().name( "myDropdown" ).type( FieldTypes.DROPDOWN ).fieldTypeConfig( dropdownConfig ).build() );
        configItems.addConfigItem( newBuilder().name( "myTextLine" ).type( FieldTypes.TEXT_LINE ).build() );
        configItems.addConfigItem( newBuilder().name( "myTextArea" ).type( FieldTypes.TEXT_AREA ).build() );
        configItems.addConfigItem(
            newBuilder().name( "myRadioButtons" ).type( FieldTypes.RADIO_BUTTONS ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        configItems.addConfigItem( newBuilder().name( "myPhone" ).type( FieldTypes.PHONE ).build() );
        configItems.addConfigItem( newBuilder().name( "myXml" ).type( FieldTypes.XML ).build() );

        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        configItems.addConfigItem( formItemSet );
        formItemSet.addField( newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addField( newBuilder().name( "hairColour" ).occurrences( 1, 3 ).type( FieldTypes.TEXT_LINE ).build() );

        String json = new ContentTypeSerializerJson().toJson( contentType );

        // exercise
        ContentType actualContentType = new ContentTypeSerializerJson().parse( json );

        // verify
        assertNotNull( actualContentType );
        ConfigItems actualConfigItems = actualContentType.getConfigItems();

        assertNotNull( actualConfigItems );
        assertEquals( 8, actualConfigItems.size() );

        assertNotNull( actualConfigItems.getConfigItem( new ConfigItemPath( "myDate" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new ConfigItemPath( "myDropdown" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new ConfigItemPath( "myTextLine" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new ConfigItemPath( "myTextArea" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new ConfigItemPath( "myRadioButtons" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new ConfigItemPath( "myPhone" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new ConfigItemPath( "myXml" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new ConfigItemPath( "personalia" ).getLastElement() ) );

    }

    @Test
    public void parse_template()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        FieldSetTemplate template = FieldSetTemplateBuilder.newFieldSetTemplate().module( module ).fieldSet(
            newFieldSet().name( "address" ).add( newBuilder().name( "label" ).label( "Label" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newBuilder().name( "street" ).label( "Street" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newBuilder().name( "postalNo" ).label( "Postal No" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newBuilder().name( "country" ).label( "Country" ).type( FieldTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addConfigItem( newBuilder().name( "myTextLine" ).type( FieldTypes.TEXT_LINE ).build() );
        cty.addConfigItem( newTemplateReference( template ).name( "home" ).build() );
        cty.addConfigItem( newTemplateReference( template ).name( "cabin" ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( template );

        String json = new ContentTypeSerializerJson().toJson( cty );

        // exercise
        ContentType parsedContentType = new ContentTypeSerializerJson().parse( json );

        // verify references
        assertEquals( ConfigItemType.REFERENCE, parsedContentType.getConfigItems().getConfigItem( "home" ).getConfigItemType() );
        assertEquals( ConfigItemType.REFERENCE, parsedContentType.getConfigItems().getConfigItem( "cabin" ).getConfigItemType() );

        // verify items past the reference is null
        assertEquals( null, parsedContentType.getConfigItems().getConfigItem( "home.street" ) );
    }

    @Test
    public void parseFieldSet_in_FieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        FormItemSet formItemSet =
            newFieldSet().name( "top-fieldSet" ).add( newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newFieldSet().name( "inner-fieldSet" ).add(
                    newField().name( "myInnerField" ).type( FieldTypes.TEXT_LINE ).build() ).build() ).build();
        contentType.addConfigItem( formItemSet );

        String json = new ContentTypeSerializerJson().toJson( contentType );

        ContentType parsedContentType = new ContentTypeSerializerJson().parse( json );
        assertEquals( "top-fieldSet", parsedContentType.getFieldSet( "top-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.myField", parsedContentType.getField( "top-fieldSet.myField" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet", parsedContentType.getFieldSet( "top-fieldSet.inner-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet.myInnerField",
                      parsedContentType.getField( "top-fieldSet.inner-fieldSet.myInnerField" ).getPath().toString() );
    }

    @Test
    public void visualFieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        VisualFieldSet visualFieldSet = newVisualFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newField().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() ).add(
            newField().name( "hairColour" ).type( FieldTypes.TEXT_LINE ).build() ).build();
        contentType.addConfigItem( visualFieldSet );

        String json = new ContentTypeSerializerJson().toJson( contentType );

        ContentType parsedContentType = new ContentTypeSerializerJson().parse( json );
        assertEquals( "eyeColour", parsedContentType.getField( "eyeColour" ).getPath().toString() );
        assertEquals( "hairColour", parsedContentType.getField( "hairColour" ).getPath().toString() );

        assertNotNull( parsedContentType.getConfigItems().getConfigItem( "personalia" ) );
    }
}
