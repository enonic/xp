package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class TextAreaYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.TEXT_AREA;

    public Integer maxLength;

    public Boolean showCounter;

    public TextAreaYml()
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

        if ( showCounter != null )
        {
            configBuilder.property( InputTypeProperty.create( "showCounter", showCounter.toString() ).build() ).build();
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
