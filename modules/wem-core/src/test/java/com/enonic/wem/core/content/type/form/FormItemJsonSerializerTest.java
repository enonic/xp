package com.enonic.wem.core.content.type.form;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.Occurrences;
import com.enonic.wem.api.content.type.form.inputtype.InputTypeConfig;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.content.type.form.inputtype.SingleSelectorConfig;
import com.enonic.wem.core.content.JsonFactoryHolder;

import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.inputtype.SingleSelectorConfig.newSingleSelectorConfig;
import static org.junit.Assert.*;

public class FormItemJsonSerializerTest
{
    private final JsonFactory jsonFactory = JsonFactoryHolder.DEFAULT_FACTORY;

    private JsonGenerator g;

    private StringWriter sw;

    private ObjectMapper objectMapper = new ObjectMapper();

    private FormItemsJsonSerializer formItemsJsonSerializer = new FormItemsJsonSerializer();

    @Before
    public void before()
        throws IOException
    {
        sw = new StringWriter();
        g = JsonFactoryHolder.DEFAULT_FACTORY.createJsonGenerator( sw );
        g.useDefaultPrettyPrinter();
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
        FormItem formItem = new FormItemJsonSerializer( formItemsJsonSerializer ).parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( formItem instanceof Input );
        assertEquals( "myTextLine", formItem.getName() );
        Input parsedInput = (Input) formItem;
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
        FormItem formItem = new FormItemJsonSerializer( formItemsJsonSerializer ).parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( formItem instanceof Input );
        assertEquals( "mySingleSelector", formItem.getName() );
        Input parsedInput = (Input) formItem;
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
        FormItemSet.Builder builder = newFormItemSet();
        builder.name( "mySubType" );
        builder.label( "My sub type" );
        builder.immutable( true );
        builder.required( true );
        builder.occurrences( 1, 100 );
        builder.customText( "Custom text" );
        builder.helpText( "Help text" );
        FormItemSet formItemSet = builder.build();

        String json = fieldSetToJson( formItemSet );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        FormItem formItem = new FormItemJsonSerializer( formItemsJsonSerializer ).parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( formItem instanceof FormItemSet );
        assertEquals( "mySubType", formItem.getName() );
        FormItemSet parsedFormItemSet = (FormItemSet) formItem;
        assertEquals( "My sub type", parsedFormItemSet.getLabel() );
        assertEquals( true, parsedFormItemSet.isRequired() );
        assertEquals( true, parsedFormItemSet.isImmutable() );
    }

    private String fieldSetToJson( FormItemSet formItemSet )
        throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();
        return new FormItemJsonSerializer( formItemsJsonSerializer ).serialize( formItemSet, mapper ).toString();
    }

    private String fieldToJson( Input input )
        throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();
        return new FormItemJsonSerializer( formItemsJsonSerializer ).serialize( input, mapper ).toString();
    }
}
