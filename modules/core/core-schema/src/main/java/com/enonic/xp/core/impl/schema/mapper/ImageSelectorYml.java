package com.enonic.xp.core.impl.schema.mapper;

import java.util.List;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class ImageSelectorYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.IMAGE_SELECTOR;

    public List<String> allowPath;

    public Boolean treeMode;

    public Boolean hideToggleIcon;

    public ImageSelectorYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( hideToggleIcon != null )
        {
            configBuilder.property( InputTypeProperty.create( "hideToggleIcon", hideToggleIcon.toString() ).build() );
        }

        if ( treeMode != null )
        {
            configBuilder.property( InputTypeProperty.create( "treeMode", treeMode.toString() ).build() );
        }

        if ( allowPath != null )
        {
            allowPath.forEach( allowPath -> configBuilder.property( InputTypeProperty.create( "allowPath", allowPath ).build() ) );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
