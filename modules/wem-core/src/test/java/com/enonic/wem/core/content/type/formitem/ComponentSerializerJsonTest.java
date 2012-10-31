package com.enonic.wem.core.content.type.formitem;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.type.formitem.Component;
import com.enonic.wem.api.content.type.formitem.ComponentSet;
import com.enonic.wem.api.content.type.formitem.Input;
import com.enonic.wem.api.content.type.formitem.Occurrences;
import com.enonic.wem.api.content.type.formitem.inputtype.InputTypeConfig;
import com.enonic.wem.api.content.type.formitem.inputtype.InputTypes;
import com.enonic.wem.api.content.type.formitem.inputtype.SingleSelectorConfig;
import com.enonic.wem.core.content.JsonFactoryHolder;

import static com.enonic.wem.api.content.type.formitem.Input.newInput;
import static com.enonic.wem.api.content.type.formitem.inputtype.SingleSelectorConfig.newSingleSelectorConfig;
import static org.junit.Assert.*;

public class ComponentSerializerJsonTest
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
        Input.Builder builder = newInput();
        builder.type( InputTypes.TEXT_LINE );
        builder.name( "myTextLine" );
        builder.required( true );
        builder.immutable( true );
        builder.indexed( true );
        builder.helpText( null );
        builder.customText( "Custom text" );
        builder.occurrences( 1, 100 );
        Input input = builder.build();

        String json = fieldToJson( input );
        System.out.println( json );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        Component component = new ComponentSerializerJson().parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( component instanceof Input );
        assertEquals( "myTextLine", component.getName() );
        Input parsedInput = (Input) component;
        assertEquals( true, parsedInput.isRequired() );
        assertEquals( false, parsedInput.isIndexed() );
        assertEquals( true, parsedInput.isImmutable() );
        assertEquals( null, parsedInput.getLabel() );
        assertEquals( null, parsedInput.getHelpText() );
        assertEquals( "Custom text", parsedInput.getCustomText() );
        assertEquals( new Occurrences( 1, 100 ), parsedInput.getOccurrences() );
        assertEquals( InputTypes.TEXT_LINE, parsedInput.getInputType() );
        assertNull( parsedInput.getInputTypeConfig() );
    }

    @Test
    public void singleSelector()
        throws IOException
    {
        // setup
        Input.Builder builder = newInput();
        builder.type( InputTypes.SINGLE_SELECTOR );
        builder.name( "mySingleSelector" );
        builder.label( "My SingleSelector" );
        builder.inputTypeConfig(
            newSingleSelectorConfig().typeDropdown().addOption( "Option 1", "o1" ).addOption( "Option 2", "o2" ).build() );
        Input input = builder.build();

        String json = fieldToJson( input );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        Component component = new ComponentSerializerJson().parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( component instanceof Input );
        assertEquals( "mySingleSelector", component.getName() );
        Input parsedInput = (Input) component;
        assertEquals( "My SingleSelector", parsedInput.getLabel() );
        assertEquals( false, parsedInput.isRequired() );
        assertEquals( false, parsedInput.isIndexed() );
        assertEquals( false, parsedInput.isImmutable() );
        assertEquals( InputTypes.SINGLE_SELECTOR, parsedInput.getInputType() );
        InputTypeConfig inputTypeConfig = parsedInput.getInputTypeConfig();
        assertNotNull( inputTypeConfig );
        assertTrue( inputTypeConfig instanceof SingleSelectorConfig );
        SingleSelectorConfig singleSelectorConfig = (SingleSelectorConfig) inputTypeConfig;
        assertEquals( 2, singleSelectorConfig.getOptions().size() );
        assertEquals( "o1", singleSelectorConfig.getOptions().get( 0 ).getValue() );
        assertEquals( "Option 1", singleSelectorConfig.getOptions().get( 0 ).getLabel() );
        assertEquals( "o2", singleSelectorConfig.getOptions().get( 1 ).getValue() );
        assertEquals( "Option 2", singleSelectorConfig.getOptions().get( 1 ).getLabel() );
    }

    @Test
    public void fieldSet()
        throws IOException
    {
        // setup
        ComponentSet.Builder builder = ComponentSet.newBuilder();
        builder.name( "mySubType" );
        builder.label( "My sub type" );
        builder.immutable( true );
        builder.required( true );
        builder.occurrences( 1, 100 );
        builder.customText( "Custom text" );
        builder.helpText( "Help text" );
        ComponentSet componentSet = builder.build();

        String json = fieldSetToJson( componentSet );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        Component component = new ComponentSerializerJson().parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( component instanceof ComponentSet );
        assertEquals( "mySubType", component.getName() );
        ComponentSet parsedComponentSet = (ComponentSet) component;
        assertEquals( "My sub type", parsedComponentSet.getLabel() );
        assertEquals( true, parsedComponentSet.isRequired() );
        assertEquals( true, parsedComponentSet.isImmutable() );
    }

    private String fieldSetToJson( ComponentSet componentSet )
        throws IOException
    {
        new ComponentSerializerJson().generate( componentSet, g );
        g.close();
        return sw.toString();
    }

    private String fieldToJson( Input input )
        throws IOException
    {
        new ComponentSerializerJson().generate( input, g );
        g.close();
        return sw.toString();
    }
}
