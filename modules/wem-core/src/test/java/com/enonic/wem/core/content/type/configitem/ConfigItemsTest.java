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
        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        SubType subType = subTypeBuilder.build();
        configItems.addConfig( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        // exercise & verify
        ConfigItem personaliaConfig = configItems.getConfig( new FieldPath( "personalia" ).getLastElement() );
        assertEquals( "personalia", personaliaConfig.getPath().toString() );
    }

    @Test
    public void getConfig2()
    {
        ConfigItems configItems = new ConfigItems();
        SubType subType = SubType.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        configItems.addConfig( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        // exercise & verify
        ConfigItem personaliaEyeColourConfig = subType.getConfigItems().getConfig( "eyeColour" );
        assertEquals( "personalia.eyeColour", personaliaEyeColourConfig.getPath().toString() );
    }
}
