package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.inputtype.InputTypeName;

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
    public void customizeInputType( final Input.Builder builder )
    {
        if ( min != null )
        {
            builder.inputTypeProperty( "min", GenericValue.longValue( min ) );
        }
        if ( max != null )
        {
            builder.inputTypeProperty( "max", GenericValue.longValue( max ) );
        }
    }
}
