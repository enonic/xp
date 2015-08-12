package com.enonic.xp.admin.impl.json.form;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.Beta;

import com.enonic.xp.form.Input;

@Beta
public class InputJson
    extends FormItemJson<Input>
{
    private final Input input;

    private final OccurrencesJson occurrences;

    private final String inputType;

    private final Map<String, String> inputTypeConfig;

    public InputJson( final Input input )
    {
        this.input = input;
        this.occurrences = new OccurrencesJson( input.getOccurrences() );
        this.inputType = input.getInputType().getName();
        this.inputTypeConfig = input.getInputTypeConfig().asMap();
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

    public String getInputType()
    {
        return this.inputType;
    }

    public Map<String, String> getConfig()
    {
        return inputTypeConfig;
    }
}
