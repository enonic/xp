package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplate;
import com.enonic.wem.core.content.type.configitem.FieldTemplate;
import com.enonic.wem.core.content.type.configitem.MockTemplateReferenceFetcher;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.valuetype.BasalValueType;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.Field.newField;
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

        FieldTemplate ageTemplate =
            newFieldTemplate().module( module ).name( "age" ).field( newField().name( "age" ).type( FieldTypes.textline ).build() ).build();

        FieldSetTemplate personTemplate = newFieldSetTemplate().name( "person" ).module( module ).build();
        personTemplate.addField( newField().name( "name" ).type( FieldTypes.textline ).build() );
        personTemplate.addTemplateReference( newTemplateReference( ageTemplate ).name( "age" ).build() );

        FieldSetTemplate addressTemplate = newFieldSetTemplate().module( module ).name( "myAddressTemplate" ).build();
        addressTemplate.addField( newField().type( FieldTypes.textline ).name( "street" ).build() );
        addressTemplate.addField( newField().type( FieldTypes.textline ).name( "postalCode" ).build() );
        addressTemplate.addField( newField().type( FieldTypes.textline ).name( "postalPlace" ).build() );

        personTemplate.addConfigItem( newTemplateReference( addressTemplate ).name( "address" ).build() );

        ContentType contentType = new ContentType();
        contentType.setName( "person" );
        contentType.addConfigItem( newField().type( FieldTypes.textline ).name( "id" ).build() );
        contentType.addConfigItem( newTemplateReference( personTemplate ).name( "person" ).build() );

        MockTemplateReferenceFetcher templateReferenceFetcher = new MockTemplateReferenceFetcher();
        templateReferenceFetcher.add( ageTemplate );
        templateReferenceFetcher.add( personTemplate );
        templateReferenceFetcher.add( addressTemplate );
        contentType.templateReferencesToConfigItems( templateReferenceFetcher );

        Content content = new Content();
        content.setType( contentType );
        content.setValue( "id", "1" );
        content.setValue( "person.name", "Ola Normann" );
        content.setValue( "person.age", "37" );
        content.setValue( "person.address.street", "Norvegen 99" );
        content.setValue( "person.address.postalCode", "0001" );
        content.setValue( "person.address.postalPlace", "Kaupang" );

        assertEquals( "1", content.getValueAsString( "id" ) );
        assertEquals( "Ola Normann", content.getValueAsString( "person.name" ) );
        assertEquals( "37", content.getValueAsString( "person.age" ) );
        assertEquals( "Norvegen 99", content.getValueAsString( "person.address.street" ) );
        assertEquals( "0001", content.getValueAsString( "person.address.postalCode" ) );
        assertEquals( "Kaupang", content.getValueAsString( "person.address.postalPlace" ) );
    }

    @Test
    public void trying_to_set_data_to_a_fieldSetTemplate_when_template_is_missing()
    {
        Module module = newModule().name( "myModule" ).build();
        FieldSetTemplate fieldSetTemplate = newFieldSetTemplate().module( module ).name( "myAddressTemplate" ).build();
        fieldSetTemplate.addField( newField().type( FieldTypes.textline ).name( "street" ).build() );
        fieldSetTemplate.addField( newField().type( FieldTypes.textline ).name( "postalCode" ).build() );
        fieldSetTemplate.addField( newField().type( FieldTypes.textline ).name( "postalPlace" ).build() );

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
        }

    }
}
