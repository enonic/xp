package com.enonic.wem.core.content.type.item;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.core.content.JsonFactoryHolder;
import com.enonic.wem.core.content.type.item.field.type.DropdownConfig;
import com.enonic.wem.core.content.type.item.field.type.FieldTypeConfig;
import com.enonic.wem.core.content.type.item.field.type.FieldTypes;
import com.enonic.wem.core.content.type.item.field.type.RadioButtonsConfig;

import static org.junit.Assert.*;

public class ConfigItemSerializerJsonTest
{
    private final JsonFactory jsonFactory = JsonFactoryHolder.DEFAULT_FACTORY;

    private JsonGenerator g;

    private StringWriter sw;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void before()
        throws IOException
    {
        sw = new StringWriter();
        g = JsonFactoryHolder.DEFAULT_FACTORY.createJsonGenerator( sw );
    }

    @Test
    public void textline()
        throws IOException
    {
        // setup
        Field.Builder builder = Field.newBuilder();
        builder.type( FieldTypes.textline );
        builder.name( "myTextLine" );
        builder.required( true );
        builder.immutable( true );
        builder.indexed( true );
        builder.helpText( null );
        builder.customText( "Custom text" );
        builder.multiple( 1, 100 );
        Field field = builder.build();

        String json = fieldToJson( field );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        ConfigItem configItem = ConfigItemSerializerJson.parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( configItem instanceof Field );
        assertEquals( "myTextLine", configItem.getName() );
        Field parsedField = (Field) configItem;
        assertEquals( true, parsedField.isRequired() );
        assertEquals( false, parsedField.isIndexed() );
        assertEquals( true, parsedField.isImmutable() );
        assertEquals( null, parsedField.getLabel() );
        assertEquals( null, parsedField.getHelpText() );
        assertEquals( "Custom text", parsedField.getCustomText() );
        assertEquals( new Multiple( 1, 100 ), parsedField.getMultiple() );
        assertEquals( FieldTypes.textline, parsedField.getFieldType() );
        assertNull( parsedField.getFieldTypeConfig() );
    }

    @Test
    public void dropdown()
        throws IOException
    {
        // setup
        Field.Builder builder = Field.newBuilder();
        builder.type( FieldTypes.dropdown );
        builder.name( "myDropdown" );
        builder.label( "My Dropdown" );
        builder.fieldTypeConfig( DropdownConfig.newBuilder().addOption( "Option 1", "o1" ).addOption( "Option 2", "o2" ).build() );
        Field field = builder.build();

        String json = fieldToJson( field );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        ConfigItem configItem = ConfigItemSerializerJson.parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( configItem instanceof Field );
        assertEquals( "myDropdown", configItem.getName() );
        Field parsedField = (Field) configItem;
        assertEquals( "My Dropdown", parsedField.getLabel() );
        assertEquals( false, parsedField.isRequired() );
        assertEquals( false, parsedField.isIndexed() );
        assertEquals( false, parsedField.isImmutable() );
        assertEquals( FieldTypes.dropdown, parsedField.getFieldType() );
        FieldTypeConfig fieldTypeConfig = parsedField.getFieldTypeConfig();
        assertNotNull( fieldTypeConfig );
        assertTrue( fieldTypeConfig instanceof DropdownConfig );
        DropdownConfig dropdownConfig = (DropdownConfig) fieldTypeConfig;
        assertEquals( 2, dropdownConfig.getOptions().size() );
        assertEquals( "o1", dropdownConfig.getOptions().get( 0 ).getValue() );
        assertEquals( "Option 1", dropdownConfig.getOptions().get( 0 ).getLabel() );
        assertEquals( "o2", dropdownConfig.getOptions().get( 1 ).getValue() );
        assertEquals( "Option 2", dropdownConfig.getOptions().get( 1 ).getLabel() );
    }

    @Test
    public void radioButtons()
        throws IOException
    {
        // setup
        Field.Builder builder = Field.newBuilder();
        builder.type( FieldTypes.radioButtons );
        builder.name( "myRadioButtons" );
        builder.label( "My Radio buttons" );
        builder.fieldTypeConfig( RadioButtonsConfig.newBuilder().addOption( "Option 1", "o1" ).addOption( "Option 2", "o2" ).build() );
        Field field = builder.build();

        String json = fieldToJson( field );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        ConfigItem configItem = ConfigItemSerializerJson.parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( configItem instanceof Field );
        assertEquals( "myRadioButtons", configItem.getName() );
        Field parsedField = (Field) configItem;
        assertEquals( "My Radio buttons", parsedField.getLabel() );
        assertEquals( false, parsedField.isRequired() );
        assertEquals( false, parsedField.isIndexed() );
        assertEquals( false, parsedField.isImmutable() );
        assertEquals( FieldTypes.radioButtons, parsedField.getFieldType() );
        FieldTypeConfig fieldTypeConfig = parsedField.getFieldTypeConfig();
        assertNotNull( fieldTypeConfig );
        assertTrue( fieldTypeConfig instanceof RadioButtonsConfig );
        RadioButtonsConfig radioButtonsConfig = (RadioButtonsConfig) fieldTypeConfig;
        assertEquals( 2, radioButtonsConfig.getOptions().size() );
        assertEquals( "o1", radioButtonsConfig.getOptions().get( 0 ).getValue() );
        assertEquals( "Option 1", radioButtonsConfig.getOptions().get( 0 ).getLabel() );
        assertEquals( "o2", radioButtonsConfig.getOptions().get( 1 ).getValue() );
        assertEquals( "Option 2", radioButtonsConfig.getOptions().get( 1 ).getLabel() );
    }

    @Test
    public void subType()
        throws IOException
    {
        // setup
        SubType.Builder builder = SubType.newBuilder();
        builder.name( "mySubType" );
        builder.label( "My sub type" );
        builder.immutable( true );
        builder.required( true );
        builder.multiple( 1, 100 );
        builder.customText( "Custom text" );
        builder.helpText( "Help text" );
        SubType subType = builder.build();

        String json = subTypeToJson( subType );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        ConfigItem configItem = ConfigItemSerializerJson.parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( configItem instanceof SubType );
        assertEquals( "mySubType", configItem.getName() );
        SubType parsedSubType = (SubType) configItem;
        assertEquals( "My sub type", parsedSubType.getLabel() );
        assertEquals( true, parsedSubType.isRequired() );
        assertEquals( true, parsedSubType.isImmutable() );
    }

    private String subTypeToJson( SubType field )
        throws IOException
    {
        SubTypeSerializerJson.DEFAULT.generate( field, g );
        g.close();
        return sw.toString();
    }

    private String fieldToJson( Field field )
        throws IOException
    {
        FieldSerializerJson.DEFAULT.generate( field, g );
        g.close();
        return sw.toString();
    }
}
