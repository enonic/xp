package com.enonic.wem.core.content.type.configitem;

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
import com.enonic.wem.core.content.type.configitem.fieldtype.DropdownConfig;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypeConfig;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.configitem.fieldtype.RadioButtonsConfig;

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
        Component.Builder builder = Component.newBuilder();
        builder.type( FieldTypes.TEXT_LINE );
        builder.name( "myTextLine" );
        builder.required( true );
        builder.immutable( true );
        builder.indexed( true );
        builder.helpText( null );
        builder.customText( "Custom text" );
        builder.occurrences( 1, 100 );
        Component component = builder.build();

        String json = fieldToJson( component );
        System.out.println( json );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        FormItem formItem = new ConfigItemSerializerJson().parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( formItem instanceof Component );
        assertEquals( "myTextLine", formItem.getName() );
        Component parsedComponent = (Component) formItem;
        assertEquals( true, parsedComponent.isRequired() );
        assertEquals( false, parsedComponent.isIndexed() );
        assertEquals( true, parsedComponent.isImmutable() );
        assertEquals( null, parsedComponent.getLabel() );
        assertEquals( null, parsedComponent.getHelpText() );
        assertEquals( "Custom text", parsedComponent.getCustomText() );
        assertEquals( new Occurrences( 1, 100 ), parsedComponent.getOccurrences() );
        assertEquals( FieldTypes.TEXT_LINE, parsedComponent.getFieldType() );
        assertNull( parsedComponent.getFieldTypeConfig() );
    }

    @Test
    public void dropdown()
        throws IOException
    {
        // setup
        Component.Builder builder = Component.newBuilder();
        builder.type( FieldTypes.DROPDOWN );
        builder.name( "myDropdown" );
        builder.label( "My Dropdown" );
        builder.fieldTypeConfig( DropdownConfig.newBuilder().addOption( "Option 1", "o1" ).addOption( "Option 2", "o2" ).build() );
        Component component = builder.build();

        String json = fieldToJson( component );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        FormItem formItem = new ConfigItemSerializerJson().parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( formItem instanceof Component );
        assertEquals( "myDropdown", formItem.getName() );
        Component parsedComponent = (Component) formItem;
        assertEquals( "My Dropdown", parsedComponent.getLabel() );
        assertEquals( false, parsedComponent.isRequired() );
        assertEquals( false, parsedComponent.isIndexed() );
        assertEquals( false, parsedComponent.isImmutable() );
        assertEquals( FieldTypes.DROPDOWN, parsedComponent.getFieldType() );
        FieldTypeConfig fieldTypeConfig = parsedComponent.getFieldTypeConfig();
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
        Component.Builder builder = Component.newBuilder();
        builder.type( FieldTypes.RADIO_BUTTONS );
        builder.name( "myRadioButtons" );
        builder.label( "My Radio buttons" );
        builder.fieldTypeConfig( RadioButtonsConfig.newBuilder().addOption( "Option 1", "o1" ).addOption( "Option 2", "o2" ).build() );
        Component component = builder.build();

        String json = fieldToJson( component );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        FormItem formItem = new ConfigItemSerializerJson().parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( formItem instanceof Component );
        assertEquals( "myRadioButtons", formItem.getName() );
        Component parsedComponent = (Component) formItem;
        assertEquals( "My Radio buttons", parsedComponent.getLabel() );
        assertEquals( false, parsedComponent.isRequired() );
        assertEquals( false, parsedComponent.isIndexed() );
        assertEquals( false, parsedComponent.isImmutable() );
        assertEquals( FieldTypes.RADIO_BUTTONS, parsedComponent.getFieldType() );
        FieldTypeConfig fieldTypeConfig = parsedComponent.getFieldTypeConfig();
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
    public void fieldSet()
        throws IOException
    {
        // setup
        FieldSet.Builder builder = FieldSet.newBuilder();
        builder.name( "mySubType" );
        builder.label( "My sub type" );
        builder.immutable( true );
        builder.required( true );
        builder.occurrences( 1, 100 );
        builder.customText( "Custom text" );
        builder.helpText( "Help text" );
        FieldSet fieldSet = builder.build();

        String json = fieldSetToJson( fieldSet );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        FormItem formItem = new ConfigItemSerializerJson().parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( formItem instanceof FieldSet );
        assertEquals( "mySubType", formItem.getName() );
        FieldSet parsedFieldSet = (FieldSet) formItem;
        assertEquals( "My sub type", parsedFieldSet.getLabel() );
        assertEquals( true, parsedFieldSet.isRequired() );
        assertEquals( true, parsedFieldSet.isImmutable() );
    }

    private String fieldSetToJson( FieldSet field )
        throws IOException
    {
        new ConfigItemSerializerJson().generate( field, g );
        g.close();
        return sw.toString();
    }

    private String fieldToJson( Component component )
        throws IOException
    {
        new ConfigItemSerializerJson().generate( component, g );
        g.close();
        return sw.toString();
    }
}
