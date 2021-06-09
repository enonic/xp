package com.enonic.xp.core.impl.image;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.enonic.xp.core.impl.image.effect.ImageScaleFunction;
import com.enonic.xp.core.impl.image.effect.ImageScales;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ScaleParams;

@Component(immediate = true, configurationPid = "com.enonic.xp.image")
public class ImageScaleFunctionBuilderImpl
    implements ImageScaleFunctionBuilder
{
    private volatile Map<String, ScaleCommand> scaleCommandRegistry;

    @Activate
    @Modified
    public void activate( final ImageConfig config )
    {
        final ImageScales scaleFunctions = new ImageScales( config.scale_maxDimension() );

        Map<String, ScaleCommand> map = new HashMap<>();
        map.put( "height", scaleFunctions::height );
        map.put( "max", scaleFunctions::max );
        map.put( "square", scaleFunctions::square );
        map.put( "wide", scaleFunctions::wide );
        map.put( "width", scaleFunctions::width );
        map.put( "block", scaleFunctions::block );
        scaleCommandRegistry = map;
    }

    @Override
    public ImageScaleFunction build( final ScaleParams scaleParams, final FocalPoint focalPoint )
    {
        final ScaleCommand scaleCommand = scaleCommandRegistry.get( scaleParams.getName() );

        if ( scaleCommand == null )
        {
            throw new IllegalArgumentException( "Unknown scale " + scaleParams.getName() );
        }
        return scaleCommand.build( focalPoint, scaleParams.getArguments() );
    }
}
