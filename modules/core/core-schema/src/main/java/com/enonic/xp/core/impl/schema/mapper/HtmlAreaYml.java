package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.StringPropertyValue;

public class HtmlAreaYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.HTML_AREA;

    public String exclude;

    public String include;

    public String allowHeadings;

    public HtmlAreaYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( exclude != null )
        {
            configBuilder.property( InputTypeProperty.create( "exclude", new StringPropertyValue( exclude ) ).build() );
        }

        if ( include != null )
        {
            configBuilder.property( InputTypeProperty.create( "include", new StringPropertyValue( include ) ).build() ).build();
        }

        if ( allowHeadings != null )
        {
            configBuilder.property( InputTypeProperty.create( "allowHeadings", new StringPropertyValue( allowHeadings ) ).build() ).build();
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
