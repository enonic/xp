package com.enonic.wem.api.schema.content.form.inputtype;


import com.enonic.wem.api.plugin.ext.Extension;

public abstract class InputTypeExtension
    extends InputType
    implements Extension
{
    protected InputTypeExtension()
    {
        super();
    }

    protected InputTypeExtension( final Class configClass )
    {
        super( configClass );
    }

}
