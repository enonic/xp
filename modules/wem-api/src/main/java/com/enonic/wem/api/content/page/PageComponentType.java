package com.enonic.wem.api.content.page;

import java.util.ArrayList;
import java.util.List;

public abstract class PageComponentType<T extends PageComponent>
{
    private final static List<PageComponentType> all = new ArrayList<>();

    private String shortName;

    public PageComponentType( final String shortName )
    {
        this.shortName = shortName;
        register( this );
    }

    public static void register( final PageComponentType type )
    {
        all.add( type );
    }

    public abstract PageComponentXml toXml( final PageComponent component );

    public String toString()
    {
        return shortName;
    }

    public abstract PageComponentJson toJson( final T component );
}
