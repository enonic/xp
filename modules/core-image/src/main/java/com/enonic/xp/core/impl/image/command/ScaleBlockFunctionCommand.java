package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.core.impl.image.effect.ScaleBlockFunction;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageScaleFunction;

public class ScaleBlockFunctionCommand
    extends ScaleCommand
{
    public ScaleBlockFunctionCommand()
    {
        super( "block" );
    }

    @Override
    protected ImageScaleFunction doBuild( Object[] args, FocalPoint focalPoint )
    {
        return new ScaleBlockFunction( getIntArg( args, 0, 0 ), getIntArg( args, 1, 0 ), getDoubleArg( args, 2, focalPoint.xOffset() ),
                                       getDoubleArg( args, 3, focalPoint.yOffset() ) );
    }
}
