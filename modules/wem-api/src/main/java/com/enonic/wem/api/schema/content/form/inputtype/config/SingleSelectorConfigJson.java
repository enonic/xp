package com.enonic.wem.api.schema.content.form.inputtype.config;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.schema.content.form.inputtype.SingleSelectorConfig;

public class SingleSelectorConfigJson
    extends AbstractInputTypeConfigJson
{
    private final SingleSelectorConfig config;

    private final ImmutableList<OptionJson> list;

    public SingleSelectorConfigJson( final SingleSelectorConfig config )
    {
        this.config = config;

        final ImmutableList.Builder<OptionJson> builder = ImmutableList.builder();
        for ( final SingleSelectorConfig.Option option : config.getOptions() )
        {
            builder.add( new OptionJson( option ) );
        }

        this.list = builder.build();
    }

    public List<OptionJson> getOptions()
    {
        return this.list;
    }

    public String getSelectorType()
    {
        return this.config.getType().toString();
    }
}
