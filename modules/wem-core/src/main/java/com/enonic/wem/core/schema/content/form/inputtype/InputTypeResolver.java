package com.enonic.wem.core.schema.content.form.inputtype;


import com.enonic.wem.api.schema.content.form.inputtype.InputType;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypeName;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;

public class InputTypeResolver
{
    private final static InputTypeResolver instance = new InputTypeResolver();

    private InputTypeExtensions inputTypeExtensions;

    public static InputTypeResolver get()
    {
        return instance;
    }

    public InputType resolve( final String inputTypeName )
    {
        return resolve( InputTypeName.from( inputTypeName ) );
    }

    public InputType resolve( final InputTypeName inputTypeName )
    {
        final InputType foundType;
        if ( inputTypeName.isCustom() )
        {
            foundType = inputTypeExtensions.getInputType( inputTypeName.toString() );
        }
        else
        {
            foundType = InputTypes.parse( inputTypeName.getName() );
        }
        if ( foundType == null )
        {
            throw new IllegalArgumentException( "InputType not found: " + inputTypeName );
        }

        return foundType;
    }

    public void setInputTypeExtensions( final InputTypeExtensions inputTypeExtensions )
    {
        this.inputTypeExtensions = inputTypeExtensions;
    }
}
