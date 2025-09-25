package com.enonic.xp.core.impl.schema.mapper;

import java.util.List;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.BooleanPropertyValue;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.StringPropertyValue;

public class ContentSelectorYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.CONTENT_SELECTOR;

    public List<String> allowContentType;

    public List<String> allowPath;

    public Boolean treeMode;

    public Boolean hideToggleIcon;

    protected ContentSelectorYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( hideToggleIcon != null )
        {
            configBuilder.property( InputTypeProperty.create( "hideToggleIcon", new BooleanPropertyValue( hideToggleIcon ) ).build() );
        }
        if ( treeMode != null )
        {
            configBuilder.property( InputTypeProperty.create( "treeMode", new BooleanPropertyValue( treeMode ) ).build() );
        }
        if ( allowContentType != null )
        {
            allowContentType.forEach( allowType -> configBuilder.property(
                InputTypeProperty.create( "allowContentType", new StringPropertyValue( allowType ) ).build() ) );
        }
        if ( allowPath != null )
        {
            allowPath.forEach( allowPath -> configBuilder.property(
                InputTypeProperty.create( "allowPath", new StringPropertyValue( allowPath ) ).build() ) );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
