package com.enonic.wem.api.content.page;


public final class ControllerParam
{
    private final String name;

    private String value;

    public ControllerParam( final String name, final String value )
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }
}
