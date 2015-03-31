package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.core.impl.image.effect.ScaleBlockFunction;
import com.enonic.xp.image.ImageScaleFunction;

public class ScaleBlockFunctionCommand
        extends ScaleCommand
{
    public ScaleBlockFunctionCommand()
    {
        super( "block" );
    }

    @Override
    protected ImageScaleFunction doBuild( Object[] args )
    {
        return new ScaleBlockFunction( getIntArg( args, 0, 0 ), getIntArg( args, 1, 0 ), getFloatArg( args, 2, 0.5f ), getFloatArg( args, 3, 0.5f ) );
    }
}
