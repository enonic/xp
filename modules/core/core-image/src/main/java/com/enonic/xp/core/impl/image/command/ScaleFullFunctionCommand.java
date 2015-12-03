package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.core.impl.image.ImageScaleFunction;
import com.enonic.xp.core.impl.image.ScaleFullFunction;
import com.enonic.xp.image.FocalPoint;

public final class ScaleFullFunctionCommand
    extends ScaleCommand
{
    public ScaleFullFunctionCommand()
    {
        super( "full" );
    }

    @Override
    protected ImageScaleFunction doBuild( Object[] args, FocalPoint focalPoint )
    {
        return new ScaleFullFunction();
    }
}