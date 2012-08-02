package com.enonic.wem.core.content.type;

import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItemType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.TemplateReference;
import com.enonic.wem.core.content.type.configitem.fieldtype.DropdownConfig;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.configitem.fieldtype.RadioButtonsConfig;
import com.enonic.wem.core.module.Module;

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
        configItems.addConfigItem( Field.newBuilder().name( "myDate" ).type( FieldTypes.date ).build() );
        configItems.addConfigItem(
            Field.newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myTextArea" ).type( FieldTypes.textarea ).build() );
        configItems.addConfigItem(
            Field.newBuilder().name( "myRadiobuttons" ).type( FieldTypes.radioButtons ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myXml" ).type( FieldTypes.xml ).build() );

        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "personalia" ).build();
        configItems.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        ContentTypeSerializerJson generator = new ContentTypeSerializerJson();
        String json = generator.toJson( contentType );
        System.out.println( json );
    }

    @Test
    public void generate_subtype()
    {
        ConfigItems configItems = new ConfigItems();

        configItems.addConfigItem( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "personalia" ).label( "Personalia" ).build();
        configItems.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        ContentType contentType = new ContentType();
        contentType.setConfigItems( configItems );

        ContentTypeSerializerJson generator = new ContentTypeSerializerJson();
        String json = generator.toJson( contentType );
        System.out.println( json );
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
        configItems.addConfigItem( Field.newBuilder().name( "myDate" ).type( FieldTypes.date ).build() );
        configItems.addConfigItem(
            Field.newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myTextArea" ).type( FieldTypes.textarea ).build() );
        configItems.addConfigItem(
            Field.newBuilder().name( "myRadioButtons" ).type( FieldTypes.radioButtons ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myXml" ).type( FieldTypes.xml ).build() );

        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "personalia" ).label( "Personalia" ).build();
        configItems.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        String json = ContentTypeSerializerJson.toJson( contentType );

        System.out.println( json );

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
        Module module = new Module();
        module.setName( "myModule" );

        FieldSetTemplate template = FieldSetTemplateBuilder.create().name( "address" ).module( module ).build();

        template.addField( Field.newBuilder().name( "label" ).label( "Label" ).type( FieldTypes.textline ).build() );
        template.addField( Field.newBuilder().name( "street" ).label( "Street" ).type( FieldTypes.textline ).build() );
        template.addField( Field.newBuilder().name( "postalNo" ).label( "Postal No" ).type( FieldTypes.textline ).build() );
        template.addField( Field.newBuilder().name( "country" ).label( "Country" ).type( FieldTypes.textline ).build() );

        ContentType cty = new ContentType();
        cty.addConfigItem( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        cty.addConfigItem( TemplateReference.newBuilder().name( "home" ).template( template.getTemplateQualifiedName() ).build() );
        cty.addConfigItem( TemplateReference.newBuilder().name( "cabin" ).template( template.getTemplateQualifiedName() ).build() );

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
