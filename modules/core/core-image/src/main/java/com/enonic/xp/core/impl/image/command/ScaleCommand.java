package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.core.impl.image.ImageScaleFunction;
import com.enonic.xp.image.FocalPoint;

public abstract class ScaleCommand
    extends BaseImageCommand
{

    public ScaleCommand( String name )
    {
        super( name );
    }

    public final ImageScaleFunction build( Object[] args )
    {
        return doBuild( args, FocalPoint.DEFAULT );
    }

    public final ImageScaleFunction build( Object[] args, FocalPoint focalPoint )
    {
        return doBuild( args, focalPoint );
    }

    protected abstract ImageScaleFunction doBuild( Object[] args, FocalPoint focalPoint );

}
