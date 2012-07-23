package com.enonic.wem.core.content.config.field;


import org.junit.Test;

import com.enonic.wem.core.content.config.field.type.FieldTypes;

public class FieldConfigTest
{
    @Test
    public void asdfad()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "myText" ).type( FieldTypes.textline ).build() );
        dataConfig.addField( Field.newBuilder().name( "myTextArea" ).type( FieldTypes.textarea ).build() );
    }

    @Test
    public void tags()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "myTags" ).type( FieldTypes.textline ).multiple( true ).build() );
    }
}
