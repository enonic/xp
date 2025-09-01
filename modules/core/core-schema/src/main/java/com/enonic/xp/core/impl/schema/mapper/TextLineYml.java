package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class TextLineYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.TEXT_LINE;

    public Integer maxLength;

    public String regexp;

    public Boolean showCounter;

    public TextLineYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( maxLength != null )
        {
            configBuilder.property( InputTypeProperty.create( "maxLength", maxLength.toString() ).build() );
        }

        if ( regexp != null )
        {
            configBuilder.property( InputTypeProperty.create( "regexp", regexp ).build() ).build();
        }

        if ( showCounter != null )
        {
            configBuilder.property( InputTypeProperty.create( "showCounter", showCounter.toString() ).build() ).build();
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
