package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.FieldSetTemplate;
import com.enonic.wem.core.content.type.FieldSetTemplateBuilder;
import com.enonic.wem.core.content.type.MockTemplateReferenceFetcher;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.TemplateReference;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.valuetype.BasalValueType;
import com.enonic.wem.core.module.Module;

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
    public void fieldSetTemplate()
    {
        Module module = new Module();
        module.setName( "myModule" );
        FieldSetTemplate fieldSetTemplate = FieldSetTemplateBuilder.create().module( module ).name( "myAddressTemplate" ).build();
        fieldSetTemplate.addField( Field.newBuilder().type( FieldTypes.textline ).name( "street" ).build() );
        fieldSetTemplate.addField( Field.newBuilder().type( FieldTypes.textline ).name( "postalCode" ).build() );
        fieldSetTemplate.addField( Field.newBuilder().type( FieldTypes.textline ).name( "postalPlace" ).build() );

        ContentType contentType = new ContentType();
        contentType.addConfigItem( Field.newBuilder().type( FieldTypes.textline ).name( "name" ).build() );
        contentType.addConfigItem( TemplateReference.newBuilder().name( "address" ).template( "myModule:myAddressTemplate" ).build() );

        MockTemplateReferenceFetcher templateReferenceFetcher = new MockTemplateReferenceFetcher();
        templateReferenceFetcher.add( fieldSetTemplate );
        contentType.templateReferencesToConfigItems( templateReferenceFetcher );

        Content content = new Content();
        content.setType( contentType );
        content.setValue( "name", "Ola Normann" );
        content.setValue( "address.street", "Norvegen 99" );
        content.setValue( "address.postalCode", "0001" );
        content.setValue( "address.postalPlace", "Kaupang" );

        assertEquals( "Ola Normann", content.getValueAsString( "name" ) );
        assertEquals( "Norvegen 99", content.getValueAsString( "address.street" ) );
        assertEquals( "0001", content.getValueAsString( "address.postalCode" ) );
        assertEquals( "Kaupang", content.getValueAsString( "address.postalPlace" ) );
    }
}
