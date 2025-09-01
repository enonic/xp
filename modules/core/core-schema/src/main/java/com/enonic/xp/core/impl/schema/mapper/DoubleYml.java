package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
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
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( min != null )
        {
            configBuilder.property( InputTypeProperty.create( "min", min.toString() ).build() );
        }
        if ( max != null )
        {
            configBuilder.property( InputTypeProperty.create( "max", max.toString() ).build() );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
