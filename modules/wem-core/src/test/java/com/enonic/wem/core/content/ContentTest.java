package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.valuetype.BasalValueType;

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
}
