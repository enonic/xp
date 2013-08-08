package com.enonic.wem.api.schema.content.form.inputtype.config;

import com.enonic.wem.api.schema.content.form.inputtype.SingleSelectorConfig;

public class SingleSelectorConfigJsonGenerator
    extends AbstractInputTypeConfigJsonGenerator<SingleSelectorConfig>
{
    public static final SingleSelectorConfigJsonGenerator DEFAULT = new SingleSelectorConfigJsonGenerator();

    @Override
    public AbstractInputTypeConfigJson generate( final SingleSelectorConfig config )
    {
        return new SingleSelectorConfigJson( config );
    }
}
