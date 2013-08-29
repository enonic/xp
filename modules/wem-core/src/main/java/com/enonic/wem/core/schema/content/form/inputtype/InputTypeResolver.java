package com.enonic.wem.core.schema.content.form.inputtype;


import com.enonic.wem.api.schema.content.form.inputtype.BaseInputType;
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

    public BaseInputType resolve( final String inputTypeName )
    {
        return resolve( InputTypeName.from( inputTypeName ) );
    }

    public BaseInputType resolve( final InputTypeName inputTypeName )
    {
        final BaseInputType foundType;
        if ( inputTypeName.isCustom() )
        {
            foundType = (BaseInputType) inputTypeExtensions.getInputType( inputTypeName.getName() );
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
