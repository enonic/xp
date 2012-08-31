package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class ConfigItemsTest
{
    @Test
    public void getConfig()
    {
        ConfigItems configItems = new ConfigItems();
        FieldSet fieldSet = FieldSet.newBuilder().name( "personalia" ).build();
        configItems.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise & verify
        DirectAccessibleConfigItem personaliaConfig =
            configItems.getDirectAccessibleConfigItem( new ConfigItemPath( "personalia" ).getLastElement() );
        assertEquals( "personalia", personaliaConfig.getPath().toString() );
    }

    @Test
    public void getConfig2()
    {
        ConfigItems configItems = new ConfigItems();
        FieldSet fieldSet = FieldSet.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        configItems.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise & verify
        DirectAccessibleConfigItem personaliaEyeColourConfig = fieldSet.getConfigItems().getDirectAccessibleConfigItem( "eyeColour" );
        assertEquals( "personalia.eyeColour", personaliaEyeColourConfig.getPath().toString() );
    }

    @Test
    public void toString_with_two_fields()
    {
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "hairColour" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise & verify
        assertEquals( "eyeColour, hairColour", configItems.toString() );
    }

    @Test
    public void toString_with_visualFieldSet()
    {
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( Field.newBuilder().name( "name" ).type( FieldTypes.TEXT_LINE ).build() );
        configItems.addConfigItem( VisualFieldSet.newVisualFieldSet().label( "Visual" ).name( "visual" ).add(
            Field.newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() ).add(
            Field.newBuilder().name( "hairColour" ).type( FieldTypes.TEXT_LINE ).build() ).build() );

        // exercise & verify
        assertEquals( "name, visual{eyeColour, hairColour}", configItems.toString() );
    }
}
