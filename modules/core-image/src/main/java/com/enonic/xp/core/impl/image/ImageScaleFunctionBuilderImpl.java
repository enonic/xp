package com.enonic.xp.core.impl.image;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.core.impl.image.command.ScaleCommand;
import com.enonic.xp.core.impl.image.command.ScaleCommandRegistry;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ScaleParams;

@Component(immediate = true)
public class ImageScaleFunctionBuilderImpl
    implements ImageScaleFunctionBuilder
{
    private final ScaleCommandRegistry scaleCommandRegistry;

    public ImageScaleFunctionBuilderImpl()
    {
        this.scaleCommandRegistry = new ScaleCommandRegistry();
    }

    @Override
    public ImageScaleFunction build( final ScaleParams scaleParams, final FocalPoint focalPoint )
    {
        ScaleCommand scaleCommand = this.scaleCommandRegistry.getCommand( scaleParams.getName() );
        if ( scaleCommand != null )
        {
            return scaleCommand.build( scaleParams.getArguments(), focalPoint );
        }
        else
        {
            return null;
        }
    }
}
