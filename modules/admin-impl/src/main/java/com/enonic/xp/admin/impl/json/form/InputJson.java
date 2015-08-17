package com.enonic.xp.admin.impl.json.form;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;

import com.enonic.xp.form.Input;

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
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        for ( final Map.Entry<String, Collection<String>> entry : this.input.getInputTypeConfig().asMap().entrySet() )
        {
            json.set( entry.getKey(), toJson( entry.getValue() ) );
        }

        return json;
    }

    private static JsonNode toJson( final Collection<String> values )
    {
        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for ( final String value : values )
        {
            json.add( value );
        }

        return json;
    }
}
