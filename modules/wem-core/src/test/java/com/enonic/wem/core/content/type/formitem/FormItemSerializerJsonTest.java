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

import com.enonic.wem.api.content.type.formitem.FormItem;
import com.enonic.wem.api.content.type.formitem.FormItemSet;
import com.enonic.wem.api.content.type.formitem.Input;
import com.enonic.wem.api.content.type.formitem.Occurrences;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypeConfig;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.api.content.type.formitem.comptype.SingleSelectorConfig;
import com.enonic.wem.core.content.JsonFactoryHolder;

import static com.enonic.wem.api.content.type.formitem.Input.newInput;
import static com.enonic.wem.api.content.type.formitem.comptype.SingleSelectorConfig.newSingleSelectorConfig;
import static org.junit.Assert.*;

public class FormItemSerializerJsonTest
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
        builder.type( ComponentTypes.TEXT_LINE );
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
        FormItem formItem = new FormItemSerializerJson().parse( objectMapper.readValue( jp, JsonNode.class ) );

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
        assertEquals( ComponentTypes.TEXT_LINE, parsedInput.getComponentType() );
        assertNull( parsedInput.getComponentTypeConfig() );
    }

    @Test
    public void singleSelector()
        throws IOException
    {
        // setup
        Input.Builder builder = newInput();
        builder.type( ComponentTypes.SINGLE_SELECTOR );
        builder.name( "mySingleSelector" );
        builder.label( "My SingleSelector" );
        builder.componentTypeConfig(
            newSingleSelectorConfig().typeDropdown().addOption( "Option 1", "o1" ).addOption( "Option 2", "o2" ).build() );
        Input input = builder.build();

        String json = fieldToJson( input );
        JsonParser jp = jsonFactory.createJsonParser( json );

        // exercise
        FormItem formItem = new FormItemSerializerJson().parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        assertTrue( formItem instanceof Input );
        assertEquals( "mySingleSelector", formItem.getName() );
        Input parsedInput = (Input) formItem;
        assertEquals( "My SingleSelector", parsedInput.getLabel() );
        assertEquals( false, parsedInput.isRequired() );
        assertEquals( false, parsedInput.isIndexed() );
        assertEquals( false, parsedInput.isImmutable() );
        assertEquals( ComponentTypes.SINGLE_SELECTOR, parsedInput.getComponentType() );
        ComponentTypeConfig componentTypeConfig = parsedInput.getComponentTypeConfig();
        assertNotNull( componentTypeConfig );
        assertTrue( componentTypeConfig instanceof SingleSelectorConfig );
        SingleSelectorConfig singleSelectorConfig = (SingleSelectorConfig) componentTypeConfig;
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
        FormItemSet.Builder builder = FormItemSet.newBuilder();
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
        FormItem formItem = new FormItemSerializerJson().parse( objectMapper.readValue( jp, JsonNode.class ) );

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
        new FormItemSerializerJson().generate( formItemSet, g );
        g.close();
        return sw.toString();
    }

    private String fieldToJson( Input input )
        throws IOException
    {
        new FormItemSerializerJson().generate( input, g );
        g.close();
        return sw.toString();
    }
}
