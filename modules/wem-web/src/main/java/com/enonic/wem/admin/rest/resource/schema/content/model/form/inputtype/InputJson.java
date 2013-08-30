package com.enonic.wem.admin.rest.resource.schema.content.model.form.inputtype;

import com.enonic.wem.admin.rest.resource.schema.content.model.form.FormItemJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.OccurrencesJson;
import com.enonic.wem.api.schema.content.form.Input;

public class InputJson
    extends FormItemJson
{
    private final Input input;

    private final OccurrencesJson occurrences;

    private final InputTypeJson inputType;

    public InputJson( final Input input )
    {
        this.input = input;

        this.occurrences = new OccurrencesJson( input.getOccurrences() );

        this.inputType = new InputTypeJson( input.getInputType() );

        if ( input.getInputType().requiresConfig() && input.getInputTypeConfig() != null )
        {
            this.inputType.setConfig( input.getInputTypeConfig() );
        }
    }

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

    public InputTypeJson getType()
    {
        return this.inputType;
    }
}
