package com.enonic.wem.core.content.config.field;


public abstract class ConfigItem
{
    private String name;

    private FieldPath path;

    void setName( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    void setPath( final FieldPath path )
    {
        this.path = path;
    }

    public FieldPath getPath()
    {
        return path;
    }

    abstract ConfigItemJsonGenerator getJsonGenerator();

}
