package com.enonic.xp.form.inputtype;


import com.google.common.annotations.Beta;

@Beta
public abstract class InputTypeExtension
    extends InputType
{
    protected InputTypeExtension()
    {
        super();
    }

    protected InputTypeExtension( final Class configClass )
    {
        super( configClass, true );
    }

}
