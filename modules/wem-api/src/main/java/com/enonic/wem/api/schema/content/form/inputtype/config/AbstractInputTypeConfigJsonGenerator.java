package com.enonic.wem.api.schema.content.form.inputtype.config;

import com.enonic.wem.api.schema.content.form.inputtype.InputTypeConfig;

public abstract class AbstractInputTypeConfigJsonGenerator<T extends InputTypeConfig>
{
    public abstract AbstractInputTypeConfigJson generate( final T config );
}
