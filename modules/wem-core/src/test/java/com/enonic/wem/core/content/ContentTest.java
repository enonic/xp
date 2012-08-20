package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplate;
import com.enonic.wem.core.content.type.configitem.FieldTemplate;
import com.enonic.wem.core.content.type.configitem.MockTemplateReferenceFetcher;
import com.enonic.wem.core.content.type.configitem.fieldtype.DropdownConfig;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.valuetype.BasalValueType;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.Field.newField;
import static com.enonic.wem.core.content.type.configitem.FieldSet.newFieldSet;
import static com.enonic.wem.core.content.type.configitem.FieldSetTemplateBuilder.newFieldSetTemplate;
import static com.enonic.wem.core.content.type.configitem.FieldTemplateBuilder.newFieldTemplate;
import static com.enonic.wem.core.content.type.configitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;

public class ContentTest
{
    @Test
    public void given_unstructured_content_when_getting_values_then_they_are_returned()
    {
        // setup
        Content content = new Content();
        content.setValue( "name", "Thomas" );
        content.setValue( "personalia.eyeColour", "Blue" );
        content.setValue( "personalia.hairColour", "Blonde" );

        assertNull( content.getData().getValue( "personalia.eyeColour" ).getField() );
        assertEquals( BasalValueType.STRING, content.getData().getValue( "personalia.eyeColour" ).getBasalValueType() );
        assertEquals( "Blue", content.getData().getValue( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData().getValue( "personalia.eyeColour" ).getPath().toString() );
    }

    @Test
    public void given_unstructured_content_when_setting_type_that_fits_then_everything_is_ok()
    {
        // setup
        Content content = new Content();
        content.setValue( "name", "Thomas" );
        content.setValue( "personalia.eyeColour", "Blue" );
        content.setValue( "personalia.hairColour", "Blonde" );
        content.setValue( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.setValue( "crimes[0].year", "1989" );
        content.setValue( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.setValue( "crimes[1].year", "1990" );

        assertNull( content.getData().getValue( "personalia.eyeColour" ).getField() );
        assertEquals( BasalValueType.STRING, content.getData().getValue( "personalia.eyeColour" ).getBasalValueType() );
        assertEquals( "Blue", content.getData().getValue( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData().getValue( "personalia.eyeColour" ).getPath().toString() );

        // exercise
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( FieldSet.newBuilder().typeGroup().name( "personalia" ).multiple( false ).build() );
        configItems.getFieldSet( "personalia" ).addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        configItems.getFieldSet( "personalia" ).addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( FieldSet.newBuilder().typeGroup().name( "crimes" ).multiple( true ).build() );
        configItems.getFieldSet( "crimes" ).addField( Field.newBuilder().name( "description" ).type( FieldTypes.textline ).build() );
        configItems.getFieldSet( "crimes" ).addField( Field.newBuilder().name( "year" ).type( FieldTypes.textline ).build() );
        ContentType type = new ContentType();
        type.setConfigItems( configItems );
        content.setType( type );

        assertEquals( BasalValueType.STRING, content.getData().getValue( "personalia.eyeColour" ).getBasalValueType() );
        assertEquals( "Blue", content.getData().getValue( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData().getValue( "personalia.eyeColour" ).getField().getPath().toString() );
        assertEquals( "personalia.hairColour", content.getData().getValue( "personalia.hairColour" ).getField().getPath().toString() );
        assertEquals( "crimes.description", content.getData().getValue( "crimes[1].description" ).getField().getPath().toString() );
        assertEquals( "crimes.year", content.getData().getValue( "crimes[1].year" ).getField().getPath().toString() );
    }

    @Test
    public void templates()
    {
        Module module = newModule().name( "myModule" ).build();

        FieldTemplate postalCodeTemplate =
            newFieldTemplate().module( module ).field( Field.newField().name( "postalCode" ).type( FieldTypes.textline ).build() ).build();
        FieldTemplate countryTemplate = newFieldTemplate().module( module ).field(
            Field.newField().name( "country" ).type( FieldTypes.dropdown ).fieldTypeConfig(
                DropdownConfig.newBuilder().addOption( "Norway", "NO" ).build() ).build() ).build();

        FieldSetTemplate addressTemplate = newFieldSetTemplate().module( module ).fieldSet(
            newFieldSet().typeGroup().name( "address" ).addConfigItem(
                newField().name( "street" ).type( FieldTypes.textline ).build() ).addConfigItem(
                newTemplateReference( postalCodeTemplate ).name( "postalCode" ).build() ).addConfigItem(
                newField().name( "postalPlace" ).type( FieldTypes.textline ).build() ).addConfigItem(
                newTemplateReference( countryTemplate ).name( "country" ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.setName( "person" );
        contentType.addConfigItem( newField().type( FieldTypes.textline ).name( "name" ).build() );
        contentType.addConfigItem( newTemplateReference( addressTemplate ).name( "address" ).build() );

        MockTemplateReferenceFetcher templateReferenceFetcher = new MockTemplateReferenceFetcher();
        templateReferenceFetcher.add( postalCodeTemplate );
        templateReferenceFetcher.add( countryTemplate );
        templateReferenceFetcher.add( addressTemplate );
        contentType.templateReferencesToConfigItems( templateReferenceFetcher );

        Content content = new Content();
        content.setType( contentType );
        content.setValue( "name", "Ola Normann" );
        content.setValue( "address.street", "Bakkebygrenda 1" );
        content.setValue( "address.postalCode", "2676" );
        content.setValue( "address.postalPlace", "Heidal" );
        content.setValue( "address.country", "NO" );

        assertEquals( "Ola Normann", content.getValueAsString( "name" ) );
        assertEquals( "Bakkebygrenda 1", content.getValueAsString( "address.street" ) );
        assertEquals( "2676", content.getValueAsString( "address.postalCode" ) );
        assertEquals( "Heidal", content.getValueAsString( "address.postalPlace" ) );
        assertEquals( "NO", content.getValueAsString( "address.country" ) );
    }

    @Test
    public void templates_multiple()
    {
        Module module = newModule().name( "myModule" ).build();

        FieldSetTemplate addressTemplate = newFieldSetTemplate().module( module ).fieldSet(
            newFieldSet().typeGroup().name( "address" ).multiple( true ).addConfigItem(
                newField().type( FieldTypes.textline ).name( "label" ).build() ).addConfigItem(
                newField().type( FieldTypes.textline ).name( "street" ).build() ).addConfigItem(
                newField().type( FieldTypes.textline ).name( "postalCode" ).build() ).addConfigItem(
                newField().type( FieldTypes.textline ).name( "postalPlace" ).build() ).addConfigItem(
                newField().type( FieldTypes.textline ).name( "country" ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        contentType.addConfigItem( newTemplateReference( addressTemplate ).name( "address" ).build() );

        MockTemplateReferenceFetcher templateReferenceFetcher = new MockTemplateReferenceFetcher();
        templateReferenceFetcher.add( addressTemplate );
        contentType.templateReferencesToConfigItems( templateReferenceFetcher );

        Content content = new Content();
        content.setType( contentType );
        content.setValue( "address[0].label", "Home" );
        content.setValue( "address[0].street", "Bakkebygrenda 1" );
        content.setValue( "address[0].postalCode", "2676" );
        content.setValue( "address[0].postalPlace", "Heidal" );
        content.setValue( "address[0].country", "NO" );
        content.setValue( "address[1].label", "Cabin" );
        content.setValue( "address[1].street", "Heia" );
        content.setValue( "address[1].postalCode", "2676" );
        content.setValue( "address[1].postalPlace", "Gjende" );
        content.setValue( "address[1].country", "NO" );

        assertEquals( "Home", content.getValueAsString( "address[0].label" ) );
        assertEquals( "Bakkebygrenda 1", content.getValueAsString( "address[0].street" ) );
        assertEquals( "2676", content.getValueAsString( "address[0].postalCode" ) );
        assertEquals( "Heidal", content.getValueAsString( "address[0].postalPlace" ) );
        assertEquals( "NO", content.getValueAsString( "address[0].country" ) );

        assertEquals( "Cabin", content.getValueAsString( "address[1].label" ) );
        assertEquals( "Heia", content.getValueAsString( "address[1].street" ) );
        assertEquals( "2676", content.getValueAsString( "address[1].postalCode" ) );
        assertEquals( "Gjende", content.getValueAsString( "address[1].postalPlace" ) );
        assertEquals( "NO", content.getValueAsString( "address[1].country" ) );
    }

    @Test
    public void trying_to_set_data_to_a_fieldSetTemplate_when_template_is_missing()
    {
        ContentType contentType = new ContentType();
        contentType.addConfigItem( newField().type( FieldTypes.textline ).name( "name" ).build() );
        contentType.addConfigItem( newTemplateReference().name( "address" ).typeField().template( "myModule:myAddressTemplate" ).build() );

        contentType.templateReferencesToConfigItems( new MockTemplateReferenceFetcher() );

        Content content = new Content();
        content.setType( contentType );
        content.setValue( "name", "Ola Normann" );
        try
        {
            content.setValue( "address.street", "Norvegen 99" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "ConfigItem at path [address.street] expected to be of type FieldSet: REFERENCE", e.getMessage() );
        }
    }

    @Test
    public void required()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.addConfigItem( newField().name( "name" ).type( FieldTypes.textline ).build() );

        FieldSet personaliaFieldSet = newFieldSet().typeGroup().name( "personalia" ).multiple( false ).required( true ).build();
        personaliaFieldSet.addField( newField().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        personaliaFieldSet.addField( newField().name( "hairColour" ).type( FieldTypes.textline ).build() );
        contentType.addConfigItem( personaliaFieldSet );

        FieldSet crimesFieldSet = newFieldSet().typeGroup().name( "crimes" ).multiple( true ).build();
        contentType.addConfigItem( crimesFieldSet );
        crimesFieldSet.addField( newField().name( "description" ).type( FieldTypes.textline ).build() );
        crimesFieldSet.addField( newField().name( "year" ).type( FieldTypes.textline ).build() );

        Content content = new Content();
        content.setType( contentType );

        content.setValue( "name", "Thomas" );
        content.setValue( "personalia.eyeColour", "Blue" );
        content.setValue( "personalia.hairColour", "Blonde" );
        content.setValue( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.setValue( "crimes[0].year", "1989" );
        content.setValue( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.setValue( "crimes[1].year", "1990" );

        content.checkBreaksRequiredContract();

        // exercise

    }
}
