package com.enonic.wem.core.content.type;

import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItemType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplate;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplateBuilder;
import com.enonic.wem.core.content.type.configitem.MockTemplateReferenceFetcher;
import com.enonic.wem.core.content.type.configitem.fieldtype.DropdownConfig;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.configitem.fieldtype.RadioButtonsConfig;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.Field.newBuilder;
import static com.enonic.wem.core.content.type.configitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;


public class ContentTypeSerializerJsonTest
{
    @Test
    public void generate_all_types()
    {
        DropdownConfig dropdownConfig = DropdownConfig.newBuilder().addOption( "myOption 1", "o1" ).addOption( "myOption 2", "o2" ).build();
        RadioButtonsConfig myRadioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "myFirstChoice", "c1" ).addOption( "mySecondChoice", "c2" ).build();

        ContentType contentType = new ContentType();
        ConfigItems configItems = new ConfigItems();
        contentType.setConfigItems( configItems );
        configItems.addConfigItem( newBuilder().name( "myDate" ).type( FieldTypes.date ).build() );
        configItems.addConfigItem(
            newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build() );
        configItems.addConfigItem( newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( newBuilder().name( "myTextArea" ).type( FieldTypes.textarea ).build() );
        configItems.addConfigItem(
            newBuilder().name( "myRadiobuttons" ).type( FieldTypes.radioButtons ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        configItems.addConfigItem( newBuilder().name( "myPhone" ).type( FieldTypes.phone ).build() );
        configItems.addConfigItem( newBuilder().name( "myXml" ).type( FieldTypes.xml ).build() );

        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "personalia" ).build();
        configItems.addConfigItem( fieldSet );
        fieldSet.addField( newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        ContentTypeSerializerJson generator = new ContentTypeSerializerJson();
        String json = generator.toJson( contentType );
    }

    @Test
    public void generate_subtype()
    {
        ConfigItems configItems = new ConfigItems();

        configItems.addConfigItem( newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "personalia" ).label( "Personalia" ).build();
        configItems.addConfigItem( fieldSet );
        fieldSet.addField( newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

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
        configItems.addConfigItem( newBuilder().name( "myDate" ).type( FieldTypes.date ).build() );
        configItems.addConfigItem(
            newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build() );
        configItems.addConfigItem( newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( newBuilder().name( "myTextArea" ).type( FieldTypes.textarea ).build() );
        configItems.addConfigItem(
            newBuilder().name( "myRadioButtons" ).type( FieldTypes.radioButtons ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        configItems.addConfigItem( newBuilder().name( "myPhone" ).type( FieldTypes.phone ).build() );
        configItems.addConfigItem( newBuilder().name( "myXml" ).type( FieldTypes.xml ).build() );

        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "personalia" ).label( "Personalia" ).build();
        configItems.addConfigItem( fieldSet );
        fieldSet.addField( newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        String json = ContentTypeSerializerJson.toJson( contentType );

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

        FieldSetTemplate template = FieldSetTemplateBuilder.create().module( module ).fieldSet(
            FieldSet.newFieldSet().typeGroup().name( "address" ).addConfigItem(
                newBuilder().name( "label" ).label( "Label" ).type( FieldTypes.textline ).build() ).addConfigItem(
                newBuilder().name( "street" ).label( "Street" ).type( FieldTypes.textline ).build() ).addConfigItem(
                newBuilder().name( "postalNo" ).label( "Postal No" ).type( FieldTypes.textline ).build() ).addConfigItem(
                newBuilder().name( "country" ).label( "Country" ).type( FieldTypes.textline ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addConfigItem( newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        cty.addConfigItem( newTemplateReference( template ).name( "home" ).build() );
        cty.addConfigItem( newTemplateReference( template ).name( "cabin" ).build() );

        MockTemplateReferenceFetcher templateReferenceFetcher = new MockTemplateReferenceFetcher();
        templateReferenceFetcher.add( template );

        String json = ContentTypeSerializerJson.toJson( cty );

        // exercise
        ContentType parsedContentType = new ContentTypeSerializerJson().parse( json );

        // verify references
        assertEquals( ConfigItemType.REFERENCE, parsedContentType.getConfigItems().getConfigItem( "home" ).getConfigItemType() );
        assertEquals( ConfigItemType.REFERENCE, parsedContentType.getConfigItems().getConfigItem( "cabin" ).getConfigItemType() );

        // verify items past the reference is null
        assertEquals( null, parsedContentType.getConfigItems().getConfigItem( "home.street" ) );
    }
}
