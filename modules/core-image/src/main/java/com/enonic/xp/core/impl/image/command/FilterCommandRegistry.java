/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

public final class FilterCommandRegistry extends ImageCommandRegistry<FilterCommand>
{

    public FilterCommandRegistry()
    {
        register( new BlockFilterCommand() );
        register( new BlurFilterCommand() );
        register( new BorderFilterCommand() );
        register( new EmbossFilterCommand() );
        register( new GrayscaleFilterCommand() );
        register( new InvertFilterCommand() );
        register( new RoundedFilterCommand() );
        register( new SharpenFilterCommand() );
        register( new RGBAdjustFilterCommand() );
        register( new HSBAdjustFilterCommand() );
        register( new EdgeFilterCommand() );
        register( new GammaFilterCommand() );
        register( new BumpFilterCommand() );
        register( new SepiaFilterCommand() );
        register( new Rotate90Command() );
        register( new Rotate180Command() );
        register( new Rotate270Command() );
        register( new FlipHorizontalCommand() );
        register( new FlipVerticalCommand() );
        register( new ColorizeFilterCommand() );
        register( new HSBColorizeFilterCommand() );
    }
}
