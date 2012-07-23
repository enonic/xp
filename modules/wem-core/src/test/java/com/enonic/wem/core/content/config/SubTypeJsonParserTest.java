package com.enonic.wem.core.content.config;

import org.junit.Test;

import com.enonic.wem.core.content.config.field.ConfigItems;
import com.enonic.wem.core.content.config.field.Field;
import com.enonic.wem.core.content.config.field.SubType;
import com.enonic.wem.core.content.config.field.type.FieldTypes;

import static org.junit.Assert.*;


public class SubTypeJsonParserTest
{
    @Test
    public void subtype()
    {
        ContentType contentType = new ContentType();
        ConfigItems configItems = new ConfigItems();
        contentType.setConfigItems( configItems );
        configItems.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        SubType subType = subTypeBuilder.build();
        configItems.addField( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        ContentTypeJsonGenerator generator = new ContentTypeJsonGenerator();
        String json = generator.toJson( contentType );
        System.out.println( json );

        ContentType actualContentType = ContentTypeJsonParser.parse( json );
        ConfigItems actualConfigItems = actualContentType.getConfigItems();
        assertEquals( 2, actualConfigItems.size() );
    }
}
