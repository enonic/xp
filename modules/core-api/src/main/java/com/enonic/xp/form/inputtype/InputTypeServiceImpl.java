package com.enonic.xp.form.inputtype;

final class InputTypeServiceImpl
    implements InputTypeService
{
    @Override
    public InputType get( final InputTypeName name )
    {
        final InputType type = InputTypes.find( name.toString() );
        if ( type != null )
        {
            return type;
        }

        throw new InputTypeNotFoundException( name );
    }
}
