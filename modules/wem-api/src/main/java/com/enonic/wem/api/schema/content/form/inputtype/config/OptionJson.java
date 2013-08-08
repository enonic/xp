package com.enonic.wem.api.schema.content.form.inputtype.config;

import com.enonic.wem.api.schema.content.form.inputtype.SingleSelectorConfig;

public class OptionJson
{
    private final SingleSelectorConfig.Option option;

    public OptionJson( final SingleSelectorConfig.Option option )
    {
        this.option = option;
    }

    public String getLabel()
    {
        return option.getLabel();
    }

    public String getValue()
    {
        return option.getValue();
    }
}
