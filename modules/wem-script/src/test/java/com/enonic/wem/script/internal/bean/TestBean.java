package com.enonic.wem.script.internal.bean;

import java.util.Map;

public class TestBean
{
    protected String name;

    protected int age;

    protected Map<String, Object> properties;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setAge( final int age )
    {
        this.age = age;
    }

    public void setProperties( final Map<String, Object> properties )
    {
        this.properties = properties;
    }
}
