package com.enonic.wem.core.content.config;

import org.junit.Test;

import com.enonic.wem.core.content.config.field.ConfigItems;
import com.enonic.wem.core.content.config.field.Field;
import com.enonic.wem.core.content.config.field.FieldPath;
import com.enonic.wem.core.content.config.field.SubType;
import com.enonic.wem.core.content.config.field.type.DropdownConfig;
import com.enonic.wem.core.content.config.field.type.FieldTypes;
import com.enonic.wem.core.content.config.field.type.RadioButtonsConfig;

import static org.junit.Assert.*;


public class ContentTypeJsonParserTest
{
    @Test
    public void all_types()
    {
        DropdownConfig dropdownConfig = DropdownConfig.newBuilder().addOption( "myOption 1", "o1" ).addOption( "myOption 2", "o2" ).build();
        RadioButtonsConfig myRadioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "myFirstChoice", "c1" ).addOption( "mySecondChoice", "c2" ).build();

        ContentType contentType = new ContentType();
        ConfigItems configItems = new ConfigItems();
        contentType.setConfigItems( configItems );
        configItems.addField( Field.newBuilder().name( "myDate" ).type( FieldTypes.date ).build() );
        configItems.addField(
            Field.newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build() );
        configItems.addField( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        configItems.addField( Field.newBuilder().name( "myTextArea" ).type( FieldTypes.textarea ).build() );
        configItems.addField(
            Field.newBuilder().name( "myRadioButtons" ).type( FieldTypes.radioButtons ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        configItems.addField( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).build() );
        configItems.addField( Field.newBuilder().name( "myXml" ).type( FieldTypes.xml ).build() );

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

        // exercise
        ContentType actualContentType = ContentTypeJsonParser.parse( json );

        // verify
        assertNotNull( actualContentType );
        ConfigItems actualConfigItems = actualContentType.getConfigItems();

        assertNotNull( actualConfigItems );
        assertEquals( 8, actualConfigItems.size() );

        assertNotNull( actualConfigItems.getConfig( new FieldPath( "myDate" ) ) );
        assertNotNull( actualConfigItems.getConfig( new FieldPath( "myDropdown" ) ) );
        assertNotNull( actualConfigItems.getConfig( new FieldPath( "myTextLine" ) ) );
        assertNotNull( actualConfigItems.getConfig( new FieldPath( "myTextArea" ) ) );
        assertNotNull( actualConfigItems.getConfig( new FieldPath( "myRadioButtons" ) ) );
        assertNotNull( actualConfigItems.getConfig( new FieldPath( "myPhone" ) ) );
        assertNotNull( actualConfigItems.getConfig( new FieldPath( "myXml" ) ) );
        assertNotNull( actualConfigItems.getConfig( new FieldPath( "personalia" ) ) );

    }

}
