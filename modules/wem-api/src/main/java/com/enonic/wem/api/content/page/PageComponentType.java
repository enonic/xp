package com.enonic.wem.api.content.page;

import java.util.LinkedHashMap;

public abstract class PageComponentType<COMPONENT extends PageComponent>
{
    private final static LinkedHashMap<String, PageComponentType> bySimpleClassName = new LinkedHashMap<>();

    private String shortName;


    public PageComponentType( final String shortName, final Class clazz )
    {
        this.shortName = shortName;
        bySimpleClassName.put( clazz.getSimpleName(), this );
    }

    public static PageComponentType bySimpleClassName( final String simpleClassName )
    {

        return bySimpleClassName.get( simpleClassName );
    }

    public abstract AbstractPageComponentXml toXml( final COMPONENT component );

    public String toString()
    {
        return shortName;
    }

    public abstract AbstractPageComponentJson toJson( final COMPONENT component );

    public abstract AbstractPageComponentDataSerializer getDataSerializer();


}
