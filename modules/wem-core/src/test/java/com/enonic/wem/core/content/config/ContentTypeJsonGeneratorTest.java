package com.enonic.wem.core.content.config;

import org.junit.Test;

import com.enonic.wem.core.content.config.field.ConfigItems;
import com.enonic.wem.core.content.config.field.Field;
import com.enonic.wem.core.content.config.field.SubType;
import com.enonic.wem.core.content.config.field.type.BuiltInFieldTypes;


public class ContentTypeJsonGeneratorTest
{
    @Test
    public void all_types()
    {
        ContentType contentType = new ContentType();
        ConfigItems configItems = new ConfigItems();
        contentType.setConfigItems( configItems );
        configItems.addField( Field.newBuilder().name( "myTextLine" ).type( BuiltInFieldTypes.textline ).required( true ).build() );
        configItems.addField( Field.newBuilder().name( "myTextArea" ).type( BuiltInFieldTypes.textarea ).required( false ).build() );
        configItems.addField( Field.newBuilder().name( "myXml" ).type( BuiltInFieldTypes.xml ).required( false ).build() );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        SubType subType = subTypeBuilder.build();
        configItems.addField( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( BuiltInFieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( BuiltInFieldTypes.textline ).build() );

        ContentTypeJsonGenerator generator = new ContentTypeJsonGenerator();
        String json = generator.toJson( contentType );
        System.out.println( json );
    }

    @Test
    public void subtype()
    {
        ContentType contentType = new ContentType();
        ConfigItems configItems = new ConfigItems();
        contentType.setConfigItems( configItems );
        configItems.addField( Field.newBuilder().name( "name" ).type( BuiltInFieldTypes.textline ).required( true ).build() );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        SubType subType = subTypeBuilder.build();
        configItems.addField( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( BuiltInFieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( BuiltInFieldTypes.textline ).build() );

        ContentTypeJsonGenerator generator = new ContentTypeJsonGenerator();
        String json = generator.toJson( contentType );
        System.out.println( json );
    }
}
