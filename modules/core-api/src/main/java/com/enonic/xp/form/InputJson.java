package com.enonic.xp.form;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;

@Beta
public class InputJson
    extends FormItemJson<Input>
{
    private final Input input;

    private final OccurrencesJson occurrences;

    private final InputTypeJson inputType;

    private final ObjectNode configJson;

    @JsonCreator
    public InputJson( @JsonProperty("name") String name, @JsonProperty("label") String label, @JsonProperty("customText") String customText,
                      @JsonProperty("helpText") String helpText, @JsonProperty("validationRegexp") String validationRegexp,
                      @JsonProperty("immutable") boolean immutable, @JsonProperty("indexed") boolean indexed,
                      @JsonProperty("inputType") InputTypeJson inputType, @JsonProperty("occurrences") OccurrencesJson occurrences,
                      @JsonProperty("config") ObjectNode configObject, @JsonProperty("maximizeUIInputWidth") boolean maximizeUIInputWidth )
    {
        this.occurrences = occurrences;
        this.inputType = inputType;
        this.configJson = configObject;

        final Input.Builder builder = Input.create();
        builder.name( name );
        builder.label( label );
        builder.immutable( immutable );
        builder.indexed( indexed );
        builder.customText( customText );
        builder.helpText( helpText );
        builder.validationRegexp( validationRegexp );
        builder.occurrences( occurrences.getOccurrences() );
        builder.inputType( inputType.getInputType() );
        builder.maximizeUIInputWidth( maximizeUIInputWidth );
        if ( inputType.getInputType().hasConfig() && inputType.getInputType().getInputTypeConfigJsonSerializer() != null)
        {
            builder.inputTypeConfig( inputType.getInputType().getInputTypeConfigJsonSerializer().parseConfig( configObject ) );
        }
        this.input = builder.build();
    }

    public InputJson( final Input input )
    {
        this.input = input;

        this.occurrences = new OccurrencesJson( input.getOccurrences() );

        this.inputType = new InputTypeJson( input.getInputType() );

        final ObjectMapper objectMapper = new ObjectMapper();
        if ( input.getInputType().hasConfig() && input.getInputTypeConfig() != null )
        {

            this.configJson =
                (ObjectNode) this.input.getInputType().getInputTypeConfigJsonSerializer().serializeConfig( this.input.getInputTypeConfig(),
                                                                                                           objectMapper );
        }
        else
        {
            this.configJson = objectMapper.createObjectNode();
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
        return input.getValidationRegexp() != null ? input.getValidationRegexp().toString() : null;
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
