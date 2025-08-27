package com.enonic.xp.core.impl.schema.mapper;

import java.util.List;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class ContentSelectorYml
    extends InputYml
{
    public List<String> allowContentType;

    public List<String> allowPath;

    public Boolean treeMode;

    public Boolean hideToggleIcon;

    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.CONTENT_SELECTOR;
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
        if ( allowContentType != null )
        {
            allowContentType.forEach(
                allowType -> configBuilder.property( InputTypeProperty.create( "allowContentType", allowType ).build() ) );
        }
        if ( allowPath != null )
        {
            allowPath.forEach( allowPath -> configBuilder.property( InputTypeProperty.create( "allowPath", allowPath ).build() ) );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
