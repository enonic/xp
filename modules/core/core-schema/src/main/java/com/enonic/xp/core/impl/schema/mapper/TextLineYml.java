package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.PropertyValue;

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
    public void customizeInputType( final InputTypeConfig.Builder configBuilder )
    {
        if ( maxLength != null )
        {
            configBuilder.property( InputTypeProperty.create( "maxLength", PropertyValue.longValue( maxLength ) ).build() );
        }

        if ( regexp != null )
        {
            configBuilder.property( InputTypeProperty.create( "regexp", PropertyValue.stringValue( regexp ) ).build() ).build();
        }
    }
}
