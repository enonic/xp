package com.enonic.xp.admin.impl.rest.resource.content.json;

public final class LoadImageJson
{
    private String url;

    private String parent;

    private String name;

    public String getUrl()
    {
        return url;
    }

    public void setUrl( final String url )
    {
        this.url = url;
    }

    public String getParent()
    {
        return parent;
    }

    public void setParent( final String parent )
    {
        this.parent = parent;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

}
