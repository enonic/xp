package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.GenericValue;
import com.enonic.xp.inputtype.InputTypeName;

public class TextAreaYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.TEXT_AREA;

    public Integer maxLength;

    public TextAreaYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        if ( maxLength != null )
        {
            builder.inputTypeProperty( "maxLength", GenericValue.longValue( maxLength ) );
        }
    }
}
