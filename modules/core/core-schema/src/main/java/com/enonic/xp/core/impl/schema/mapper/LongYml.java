package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.PropertyValue;

public class LongYml
    extends InputYml
{

    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.LONG;

    public Long min;

    public Long max;

    public LongYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final InputTypeConfig.Builder configBuilder )
    {
        if ( min != null )
        {
            configBuilder.property( InputTypeProperty.create( "min", PropertyValue.longValue( min ) ).build() );
        }
        if ( max != null )
        {
            configBuilder.property( InputTypeProperty.create( "max", PropertyValue.longValue( max ) ).build() );
        }
    }
}
