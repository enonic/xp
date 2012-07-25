package com.enonic.wem.core.content.type;

import org.junit.Test;

import com.enonic.wem.core.content.type.item.ConfigItems;
import com.enonic.wem.core.content.type.item.Field;
import com.enonic.wem.core.content.type.item.SubType;
import com.enonic.wem.core.content.type.item.field.type.DropdownConfig;
import com.enonic.wem.core.content.type.item.field.type.FieldTypes;
import com.enonic.wem.core.content.type.item.field.type.RadioButtonsConfig;


public class ContentTypeJsonGeneratorTest
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
        configItems.addConfig( Field.newBuilder().name( "myDate" ).type( FieldTypes.date ).build() );
        configItems.addConfig(
            Field.newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build() );
        configItems.addConfig( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        configItems.addConfig( Field.newBuilder().name( "myTextArea" ).type( FieldTypes.textarea ).build() );
        configItems.addConfig(
            Field.newBuilder().name( "myRadiobuttons" ).type( FieldTypes.radioButtons ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        configItems.addConfig( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).build() );
        configItems.addConfig( Field.newBuilder().name( "myXml" ).type( FieldTypes.xml ).build() );

        SubType subType = SubType.newBuilder().name( "personalia" ).build();
        configItems.addConfig( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        ContentTypeSerializerJson generator = new ContentTypeSerializerJson();
        String json = generator.toJson( contentType );
        System.out.println( json );
    }

    @Test
    public void subtype()
    {
        ConfigItems configItems = new ConfigItems();

        configItems.addConfig( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        SubType subType = SubType.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        configItems.addConfig( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        ContentType contentType = new ContentType();
        contentType.setConfigItems( configItems );

        ContentTypeSerializerJson generator = new ContentTypeSerializerJson();
        String json = generator.toJson( contentType );
        System.out.println( json );
    }
}
