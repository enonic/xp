package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.GenericValue;
import com.enonic.xp.inputtype.InputTypeName;

public class DoubleYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.DOUBLE;

    public Double min;

    public Double max;

    public DoubleYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        if ( min != null )
        {
            builder.inputTypeProperty( "min", GenericValue.doubleValue( min ) );
        }
        if ( max != null )
        {
            builder.inputTypeProperty( "max", GenericValue.doubleValue( max ) );
        }
    }
}
