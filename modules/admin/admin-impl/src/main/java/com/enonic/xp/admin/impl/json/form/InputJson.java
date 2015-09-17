package com.enonic.xp.admin.impl.json.form;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeProperty;

@Beta
public class InputJson
    extends FormItemJson<Input>
{
    private final Input input;

    private final OccurrencesJson occurrences;

    private final String inputType;

    public InputJson( final Input input )
    {
        this.input = input;
        this.occurrences = new OccurrencesJson( input.getOccurrences() );
        this.inputType = input.getInputType().toString();
    }

    @JsonIgnore
    @Override
    public Input getFormItem()
    {
        return input;
    }

    @Override
    public String getName()
    {
        return input.getName();
    }

    public String getLabel()
    {
        return input.getLabel();
    }

    public boolean isImmutable()
    {
        return input.isImmutable();
    }

    public boolean isIndexed()
    {
        return input.isIndexed();
    }

    public boolean isMaximizeUIInputWidth()
    {
        return input.isMaximizeUIInputWidth();
    }

    public String getCustomText()
    {
        return input.getCustomText();
    }

    public String getHelpText()
    {
        return input.getHelpText();
    }

    public String getValidationRegexp()
    {
        return input.getValidationRegexp();
    }

    public OccurrencesJson getOccurrences()
    {
        return occurrences;
    }

    public String getInputType()
    {
        return this.inputType;
    }

    public ObjectNode getConfig()
    {
        final InputTypeConfig config = this.input.getInputTypeConfig();

        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        for ( final String name : config.getNames() )
        {
            json.set( name, toJson( config.getProperties( name ) ) );
        }

        return json;
    }

    private static ArrayNode toJson( final Collection<InputTypeProperty> properties )
    {
        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for ( final InputTypeProperty property : properties )
        {
            json.add( toJson( property ) );
        }

        return json;
    }

    private static ObjectNode toJson( final InputTypeProperty property )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "value", property.getValue() );

        for ( final Map.Entry<String, String> attribute : property.getAttributes().entrySet() )
        {
            json.put( "@" + attribute.getKey(), attribute.getValue() );
        }

        return json;
    }
}
