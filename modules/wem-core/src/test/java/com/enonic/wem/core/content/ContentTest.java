package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplate;
import com.enonic.wem.core.content.type.configitem.FieldTemplate;
import com.enonic.wem.core.content.type.configitem.MockTemplateReferenceFetcher;
import com.enonic.wem.core.content.type.configitem.VisualFieldSet;
import com.enonic.wem.core.content.type.configitem.fieldtype.DropdownConfig;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.datatype.BasalValueType;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.Field.newField;
import static com.enonic.wem.core.content.type.configitem.FieldSet.newFieldSet;
import static com.enonic.wem.core.content.type.configitem.FieldSetTemplateBuilder.newFieldSetTemplate;
import static com.enonic.wem.core.content.type.configitem.FieldTemplateBuilder.newFieldTemplate;
import static com.enonic.wem.core.content.type.configitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.content.type.configitem.VisualFieldSet.newVisualFieldSet;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;

public class ContentTest
{
    @Test
    public void given_unstructured_content_when_getting_values_then_they_are_returned()
    {
        // setup
        Content content = new Content();
        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );

        assertNull( content.getData().setData( "personalia.eyeColour" ).getField() );
        assertEquals( BasalValueType.STRING, content.getData().setData( "personalia.eyeColour" ).getBasalValueType() );
        assertEquals( "Blue", content.getData().setData( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData().setData( "personalia.eyeColour" ).getPath().toString() );
    }

    @Test
    public void given_unstructured_content_when_setting_type_that_fits_then_everything_is_ok()
    {
        // setup
        Content content = new Content();
        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );
        content.setData( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.setData( "crimes[0].year", "1989" );
        content.setData( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.setData( "crimes[1].year", "1990" );

        assertNull( content.getData().setData( "personalia.eyeColour" ).getField() );
        assertEquals( BasalValueType.STRING, content.getData().setData( "personalia.eyeColour" ).getBasalValueType() );
        assertEquals( "Blue", content.getData().setData( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData().setData( "personalia.eyeColour" ).getPath().toString() );

        // exercise
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( FieldSet.newBuilder().name( "personalia" ).multiple( false ).build() );
        configItems.getFieldSet( "personalia" ).addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        configItems.getFieldSet( "personalia" ).addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( FieldSet.newBuilder().name( "crimes" ).multiple( true ).build() );
        configItems.getFieldSet( "crimes" ).addField( Field.newBuilder().name( "description" ).type( FieldTypes.textline ).build() );
        configItems.getFieldSet( "crimes" ).addField( Field.newBuilder().name( "year" ).type( FieldTypes.textline ).build() );
        ContentType type = new ContentType();
        type.setConfigItems( configItems );
        content.setType( type );

        assertEquals( BasalValueType.STRING, content.getData().setData( "personalia.eyeColour" ).getBasalValueType() );
        assertEquals( "Blue", content.getData().setData( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData().setData( "personalia.eyeColour" ).getField().getPath().toString() );
        assertEquals( "personalia.hairColour", content.getData().setData( "personalia.hairColour" ).getField().getPath().toString() );
        assertEquals( "crimes.description", content.getData().setData( "crimes[1].description" ).getField().getPath().toString() );
        assertEquals( "crimes.year", content.getData().setData( "crimes[1].year" ).getField().getPath().toString() );
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
            newFieldSet().name( "address" ).add( newField().name( "street" ).type( FieldTypes.textline ).build() ).add(
                newTemplateReference( postalCodeTemplate ).name( "postalCode" ).build() ).add(
                newField().name( "postalPlace" ).type( FieldTypes.textline ).build() ).add(
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
        content.setData( "name", "Ola Normann" );
        content.setData( "address.street", "Bakkebygrenda 1" );
        content.setData( "address.postalCode", "2676" );
        content.setData( "address.postalPlace", "Heidal" );
        content.setData( "address.country", "NO" );

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
            newFieldSet().name( "address" ).multiple( true ).add( newField().type( FieldTypes.textline ).name( "label" ).build() ).add(
                newField().type( FieldTypes.textline ).name( "street" ).build() ).add(
                newField().type( FieldTypes.textline ).name( "postalCode" ).build() ).add(
                newField().type( FieldTypes.textline ).name( "postalPlace" ).build() ).add(
                newField().type( FieldTypes.textline ).name( "country" ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        contentType.addConfigItem( newTemplateReference( addressTemplate ).name( "address" ).build() );

        MockTemplateReferenceFetcher templateReferenceFetcher = new MockTemplateReferenceFetcher();
        templateReferenceFetcher.add( addressTemplate );
        contentType.templateReferencesToConfigItems( templateReferenceFetcher );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "address[0].label", "Home" );
        content.setData( "address[0].street", "Bakkebygrenda 1" );
        content.setData( "address[0].postalCode", "2676" );
        content.setData( "address[0].postalPlace", "Heidal" );
        content.setData( "address[0].country", "NO" );
        content.setData( "address[1].label", "Cabin" );
        content.setData( "address[1].street", "Heia" );
        content.setData( "address[1].postalCode", "2676" );
        content.setData( "address[1].postalPlace", "Gjende" );
        content.setData( "address[1].country", "NO" );

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
        content.setData( "name", "Ola Normann" );
        try
        {
            content.setData( "address.street", "Norvegen 99" );
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

        FieldSet personaliaFieldSet = newFieldSet().name( "personalia" ).multiple( false ).required( true ).build();
        personaliaFieldSet.addField( newField().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        personaliaFieldSet.addField( newField().name( "hairColour" ).type( FieldTypes.textline ).build() );
        contentType.addConfigItem( personaliaFieldSet );

        FieldSet crimesFieldSet = newFieldSet().name( "crimes" ).multiple( true ).build();
        contentType.addConfigItem( crimesFieldSet );
        crimesFieldSet.addField( newField().name( "description" ).type( FieldTypes.textline ).build() );
        crimesFieldSet.addField( newField().name( "year" ).type( FieldTypes.textline ).build() );

        Content content = new Content();
        content.setType( contentType );

        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );
        content.setData( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.setData( "crimes[0].year", "1989" );
        content.setData( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.setData( "crimes[1].year", "1990" );

        content.checkBreaksRequiredContract();

        // exercise
        // TODO
    }

    @Test
    public void visualFieldSet()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        VisualFieldSet visualFieldSet =
            newVisualFieldSet().label( "Personalia" ).add( newField().name( "eyeColour" ).type( FieldTypes.textline ).build() ).add(
                newField().name( "hairColour" ).type( FieldTypes.textline ).build() ).build();
        contentType.addConfigItem( visualFieldSet );

        Content content = new Content();
        content.setType( contentType );

        // exercise
        content.setData( "eyeColour", "Blue" );
        content.setData( "hairColour", "Blonde" );

        // verify
        assertEquals( "Blue", content.getValueAsString( "eyeColour" ) );
        assertEquals( "Blonde", content.getValueAsString( "hairColour" ) );
    }
}
