/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.command;

import java.util.HashMap;

public final class FilterCommandRegistry
{
    private final HashMap<String, FilterCommand> map;

    public FilterCommandRegistry()
    {
        this.map = new HashMap<String, FilterCommand>();
        register( new BlockFilterCommand() );
        register( new BlurFilterCommand() );
        register( new BorderFilterCommand() );
        register( new EmbossFilterCommand() );
        register( new GrayscaleFilterCommand() );
        register( new InvertFilterCommand() );
        register( new RoundedFilterCommand() );
        register( new ScaleHeightFilterCommand() );
        register( new ScaleMaxFilterCommand() );
        register( new ScaleSquareFilterCommand() );
        register( new ScaleWideFilterCommand() );
        register( new ScaleWidthFilterCommand() );
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
        register( new ScaleBlockFilterCommand() );
    }

    private void register( FilterCommand command )
    {
        this.map.put( command.getName().toLowerCase(), command );
    }

    public FilterCommand getCommand( String name )
    {
        return this.map.get( name.toLowerCase() );
    }
}
