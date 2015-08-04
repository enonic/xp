package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

@Beta
public final class InputTypeResolver
{
    private final static InputTypeResolver INSTANCE = new InputTypeResolver();

    public static InputTypeResolver get()
    {
        return INSTANCE;
    }

    public InputType resolve( final String inputTypeName )
    {
        return resolve( InputTypeName.from( inputTypeName ) );
    }

    public InputType resolve( final InputTypeName inputTypeName )
    {
        final InputType foundType = InputTypes.find( inputTypeName.getName() );
        if ( foundType == null )
        {
            throw new IllegalArgumentException( "InputType not found: " + inputTypeName );
        }

        return foundType;
    }
}
