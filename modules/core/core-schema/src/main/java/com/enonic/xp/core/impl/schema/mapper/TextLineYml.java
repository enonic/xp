package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.BooleanPropertyValue;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.IntegerPropertyValue;
import com.enonic.xp.inputtype.StringPropertyValue;

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
            configBuilder.property( InputTypeProperty.create( "maxLength", new IntegerPropertyValue( maxLength ) ).build() );
        }

        if ( regexp != null )
        {
            configBuilder.property( InputTypeProperty.create( "regexp", new StringPropertyValue( regexp ) ).build() ).build();
        }

        if ( showCounter != null )
        {
            configBuilder.property( InputTypeProperty.create( "showCounter", new BooleanPropertyValue( showCounter ) ).build() ).build();
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
