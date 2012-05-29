package com.enonic.wem.web.jsp;

public final class SiteInfoBean
    implements Comparable<SiteInfoBean>
{
    private int key;

    private String name;

    private String url;

    public int getKey()
    {
        return key;
    }

    public void setKey( final int key )
    {
        this.key = key;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( final String url )
    {
        this.url = url;
    }

    @Override
    public int compareTo( final SiteInfoBean other )
    {
        return this.name.compareTo( other.name );
    }
}
