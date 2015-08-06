package com.enonic.xp.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;

import com.enonic.xp.form.inputtype.ConfigurableInputType;
import com.enonic.xp.form.inputtype.InputType;
import com.enonic.xp.form.inputtype.InputTypeConfig;

@Beta
public class InputJson
    extends FormItemJson<Input>
{
    private final Input input;

    private final OccurrencesJson occurrences;

    private final InputTypeJson inputType;

    private final ObjectNode configJson;

    public InputJson( final Input input )
    {
        this.input = input;

        this.occurrences = new OccurrencesJson( input.getOccurrences() );

        this.inputType = new InputTypeJson( input.getInputType() );

        final InputType type = input.getInputType();
        final ConfigurableInputType configurableType = ( type instanceof ConfigurableInputType ) ? (ConfigurableInputType) type : null;
        final InputTypeConfig config = input.getInputTypeConfig();

        if ( ( configurableType != null ) && ( config != null ) )
        {
            this.configJson = configurableType.serializeConfig( config );
        }
        else
        {
            this.configJson = JsonNodeFactory.instance.objectNode();
        }
    }

    @JsonIgnore
    @Override
    public Input getFormItem()
    {
        return input;
    }

    @JsonIgnore
    public Input getInput()
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

    public InputTypeJson getInputType()
    {
        return this.inputType;
    }

    public ObjectNode getConfig()
    {
        return configJson;
    }
}
