package com.enonic.wem.core.content.type;

import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldPath;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.fieldtype.DropdownConfig;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.configitem.fieldtype.RadioButtonsConfig;

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
        configItems.addConfigItem( Field.newBuilder().name( "myDate" ).type( FieldTypes.date ).build() );
        configItems.addConfigItem(
            Field.newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myTextArea" ).type( FieldTypes.textarea ).build() );
        configItems.addConfigItem(
            Field.newBuilder().name( "myRadioButtons" ).type( FieldTypes.radioButtons ).fieldTypeConfig( myRadioButtonsConfig ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myXml" ).type( FieldTypes.xml ).build() );

        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "personalia" ).label( "Personalia" ).build();
        configItems.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).multiple( 1, 3 ).type( FieldTypes.textline ).build() );

        ContentTypeSerializerJson generator = new ContentTypeSerializerJson();
        String json = generator.toJson( contentType );

        System.out.println( json );

        // exercise
        ContentType actualContentType = ContentTypeSerializerJson.parse( json );

        // verify
        assertNotNull( actualContentType );
        ConfigItems actualConfigItems = actualContentType.getConfigItems();

        assertNotNull( actualConfigItems );
        assertEquals( 8, actualConfigItems.size() );

        assertNotNull( actualConfigItems.getConfigItem( new FieldPath( "myDate" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new FieldPath( "myDropdown" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new FieldPath( "myTextLine" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new FieldPath( "myTextArea" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new FieldPath( "myRadioButtons" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new FieldPath( "myPhone" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new FieldPath( "myXml" ).getLastElement() ) );
        assertNotNull( actualConfigItems.getConfigItem( new FieldPath( "personalia" ).getLastElement() ) );

    }

}
