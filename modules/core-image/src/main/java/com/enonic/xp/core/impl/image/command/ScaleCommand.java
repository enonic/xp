package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.image.ImageScaleFunction;

public abstract class ScaleCommand extends BaseImageCommand
{

    public ScaleCommand( String name )
    {
        super( name );
    }

    @Override
    public final ImageScaleFunction build( Object[] args )
    {
       return doBuild( args );
    }

    protected abstract ImageScaleFunction doBuild( Object[] args );

}
