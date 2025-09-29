package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.DoublePropertyValue;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

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
    public void customizeInputType( final InputTypeConfig.Builder configBuilder )
    {
        if ( min != null )
        {
            configBuilder.property( InputTypeProperty.create( "min", new DoublePropertyValue( min ) ).build() );
        }
        if ( max != null )
        {
            configBuilder.property( InputTypeProperty.create( "max", new DoublePropertyValue( max ) ).build() );
        }
    }
}
