package com.enonic.wem.core.content.type;

import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.SubType;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;


public class SubTypeJsonParserTest
{
    @Test
    public void subtype()
    {
        ContentType contentType = new ContentType();
        ConfigItems configItems = new ConfigItems();
        contentType.setConfigItems( configItems );
        configItems.addConfig( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        SubType subType = subTypeBuilder.build();
        configItems.addConfig( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        ContentTypeSerializerJson generator = new ContentTypeSerializerJson();
        String json = generator.toJson( contentType );
        System.out.println( json );

        ContentType actualContentType = ContentTypeSerializerJson.parse( json );
        ConfigItems actualConfigItems = actualContentType.getConfigItems();
        assertEquals( 2, actualConfigItems.size() );
    }
}
