package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.inputtype.InputTypeName;

public class TextLineYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.TEXT_LINE;

    public Integer maxLength;

    public String regexp;

    public TextLineYml()
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

        if ( regexp != null )
        {
            builder.inputTypeProperty( "regexp", GenericValue.stringValue( regexp ) );
        }
    }
}
