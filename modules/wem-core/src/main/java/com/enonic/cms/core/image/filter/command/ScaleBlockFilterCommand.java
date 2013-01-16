package com.enonic.cms.core.image.filter.command;

import com.enonic.cms.core.image.filter.BuilderContext;
import com.enonic.cms.core.image.filter.effect.ScaleBlockFilter;

public class ScaleBlockFilterCommand
        extends FilterCommand
{
    public ScaleBlockFilterCommand()
    {
        super( "scaleblock" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new ScaleBlockFilter( getIntArg( args, 0, 0 ), getIntArg( args, 1, 0 ), getFloatArg( args, 2, 0.5f ), getFloatArg( args, 3, 0.5f ) );
    }
}
