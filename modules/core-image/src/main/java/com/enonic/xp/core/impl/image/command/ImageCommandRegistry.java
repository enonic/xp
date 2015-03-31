package com.enonic.xp.core.impl.image.command;


import java.util.HashMap;

public abstract class ImageCommandRegistry<T extends BaseImageCommand>
{
    private final HashMap<String, T> map;

    public ImageCommandRegistry() {
        this.map = new HashMap<>();
    }

    protected void register( T command )
    {
        this.map.put( command.getName().toLowerCase(), command );
    }

    public T getCommand( String name )
    {
        return this.map.get( name.toLowerCase() );
    }
}
