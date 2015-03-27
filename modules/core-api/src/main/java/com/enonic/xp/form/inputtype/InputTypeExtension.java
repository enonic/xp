package com.enonic.xp.form.inputtype;


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
