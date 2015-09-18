package com.enonic.xp.core.impl.image.command;

public class ScaleCommandRegistry extends ImageCommandRegistry<ScaleCommand>
{

    public ScaleCommandRegistry()
    {
        register( new ScaleHeightFunctionCommand() );
        register( new ScaleMaxFunctionCommand() );
        register( new ScaleSquareFunctionCommand() );
        register( new ScaleWideFunctionCommand() );
        register( new ScaleWidthFunctionCommand() );
        register( new ScaleBlockFunctionCommand() );
    }

}
