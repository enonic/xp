package com.enonic.wem.core.content.config.field;


import org.junit.Test;

import com.enonic.wem.core.content.config.field.type.BuiltInFieldTypes;

public class FieldConfigTest
{
    @Test
    public void asdfad()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "myText" ).type( BuiltInFieldTypes.textline ).build() );
        dataConfig.addField( Field.newBuilder().name( "myTextArea" ).type( BuiltInFieldTypes.textarea ).build() );
    }

    @Test
    public void tags()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "myTags" ).type( BuiltInFieldTypes.textline ).multiple( true ).build() );
    }
}
