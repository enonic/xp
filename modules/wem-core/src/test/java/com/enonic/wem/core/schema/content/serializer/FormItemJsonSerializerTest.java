package com.enonic.wem.core.schema.content.serializer;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.Assert;

import com.enonic.wem.api.schema.content.form.FormItem;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypeConfig;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.form.inputtype.SingleSelectorConfig;
import com.enonic.wem.core.content.JsonFactoryHolder;

import static com.enonic.wem.api.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.Occurrences.newOccurrences;
import static com.enonic.wem.api.schema.content.form.inputtype.SingleSelectorConfig.newSingleSelectorConfig;

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
        g = JsonFactoryHolder.DEFAULT_FACTORY.createGenerator( sw );
        g.useDefaultPrettyPrinter();
    }

    @Test
    public void textline()
        throws IOException
    {
        // setup
        Input.Builder builder = newInput();
        builder.inputType( InputTypes.TEXT_LINE );
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
        JsonParser jp = jsonFactory.createParser( json );

        // exercise
        FormItem formItem = new FormItemJsonSerializer( formItemsJsonSerializer ).parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify

        Assert.assertTrue( formItem instanceof Input );
        Assert.assertEquals( "myTextLine", formItem.getName() );
        Input parsedInput = (Input) formItem;
        Assert.assertEquals( true, parsedInput.isRequired() );
        Assert.assertEquals( true, parsedInput.isIndexed() );
        Assert.assertEquals( true, parsedInput.isImmutable() );
        Assert.assertEquals( null, parsedInput.getLabel() );
        Assert.assertEquals( null, parsedInput.getHelpText() );
        Assert.assertEquals( "Custom text", parsedInput.getCustomText() );
        Assert.assertEquals( newOccurrences().minimum( 1 ).maximum( 100 ).build(), parsedInput.getOccurrences() );
        Assert.assertEquals( InputTypes.TEXT_LINE, parsedInput.getInputType() );
        Assert.assertNull( parsedInput.getInputTypeConfig() );
    }

    @Test
    public void singleSelector()
        throws IOException
    {
        // setup
        Input.Builder builder = newInput();
        builder.inputType( InputTypes.SINGLE_SELECTOR );
        builder.name( "mySingleSelector" );
        builder.label( "My SingleSelector" );
        builder.inputTypeConfig(
            newSingleSelectorConfig().typeDropdown().addOption( "Option 1", "o1" ).addOption( "Option 2", "o2" ).build() );
        Input input = builder.build();

        String json = fieldToJson( input );
        JsonParser jp = jsonFactory.createParser( json );

        // exercise
        FormItem formItem = new FormItemJsonSerializer( formItemsJsonSerializer ).parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        Assert.assertTrue( formItem instanceof Input );
        Assert.assertEquals( "mySingleSelector", formItem.getName() );
        Input parsedInput = (Input) formItem;
        Assert.assertEquals( "My SingleSelector", parsedInput.getLabel() );
        Assert.assertEquals( false, parsedInput.isRequired() );
        Assert.assertEquals( false, parsedInput.isIndexed() );
        Assert.assertEquals( false, parsedInput.isImmutable() );
        Assert.assertEquals( InputTypes.SINGLE_SELECTOR, parsedInput.getInputType() );
        InputTypeConfig inputTypeConfig = parsedInput.getInputTypeConfig();
        Assert.assertNotNull( inputTypeConfig );
        Assert.assertTrue( inputTypeConfig instanceof SingleSelectorConfig );
        SingleSelectorConfig singleSelectorConfig = (SingleSelectorConfig) inputTypeConfig;
        Assert.assertEquals( 2, singleSelectorConfig.getOptions().size() );
        Assert.assertEquals( "o1", singleSelectorConfig.getOptions().get( 0 ).getValue() );
        Assert.assertEquals( "Option 1", singleSelectorConfig.getOptions().get( 0 ).getLabel() );
        Assert.assertEquals( "o2", singleSelectorConfig.getOptions().get( 1 ).getValue() );
        Assert.assertEquals( "Option 2", singleSelectorConfig.getOptions().get( 1 ).getLabel() );
    }

    @Test
    public void fieldSet()
        throws IOException
    {
        // setup
        FormItemSet.Builder builder = newFormItemSet();
        builder.name( "myMixin" );
        builder.label( "My mixin" );
        builder.immutable( true );
        builder.required( true );
        builder.occurrences( 1, 100 );
        builder.customText( "Custom text" );
        builder.helpText( "Help text" );
        FormItemSet formItemSet = builder.build();

        String json = fieldSetToJson( formItemSet );
        JsonParser jp = jsonFactory.createParser( json );

        // exercise
        FormItem formItem = new FormItemJsonSerializer( formItemsJsonSerializer ).parse( objectMapper.readValue( jp, JsonNode.class ) );

        // verify
        Assert.assertTrue( formItem instanceof FormItemSet );
        Assert.assertEquals( "myMixin", formItem.getName() );
        FormItemSet parsedFormItemSet = (FormItemSet) formItem;
        Assert.assertEquals( "My mixin", parsedFormItemSet.getLabel() );
        Assert.assertEquals( true, parsedFormItemSet.isRequired() );
        Assert.assertEquals( true, parsedFormItemSet.isImmutable() );
    }

    private String fieldSetToJson( FormItemSet formItemSet )
        throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();
        return new FormItemJsonSerializer( formItemsJsonSerializer ).serialize( formItemSet ).toString();
    }

    private String fieldToJson( Input input )
        throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();
        return new FormItemJsonSerializer( formItemsJsonSerializer ).serialize( input ).toString();
    }
}
