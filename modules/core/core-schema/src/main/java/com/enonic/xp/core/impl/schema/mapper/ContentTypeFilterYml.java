package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.BooleanPropertyValue;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class ContentTypeFilterYml
    extends InputYml
{

    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.CONTENT_TYPE_FILTER;

    public Boolean context;

    public ContentTypeFilterYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( context != null )
        {
            configBuilder.property( InputTypeProperty.create( "context", new BooleanPropertyValue( context ) ).build() );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
