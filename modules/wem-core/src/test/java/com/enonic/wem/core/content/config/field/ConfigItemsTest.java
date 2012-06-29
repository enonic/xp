package com.enonic.wem.core.content.config.field;


import org.junit.Test;

import com.enonic.wem.core.content.config.field.type.BuiltInFieldTypes;

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
        configItems.addField( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( BuiltInFieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).type( BuiltInFieldTypes.textline ).build() );

        // exercise & verify
        ConfigItem personaliaConfig = configItems.getConfig( new FieldPath( "personalia" ) );
        assertEquals( "personalia", personaliaConfig.getPath().toString() );
    }

    @Test
    public void getConfig2()
    {
        ConfigItems configItems = new ConfigItems();
        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        SubType subType = subTypeBuilder.build();
        configItems.addField( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( BuiltInFieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).type( BuiltInFieldTypes.textline ).build() );

        // exercise & verify
        ConfigItem personaliaEyeColourConfig = configItems.getConfig( new FieldPath( "personalia.eyeColour" ) );
        assertEquals( "personalia", personaliaEyeColourConfig.getPath().toString() );
    }
}
