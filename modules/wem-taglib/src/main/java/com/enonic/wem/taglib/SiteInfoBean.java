package com.enonic.wem.taglib;

public final class SiteInfoBean
    implements Comparable<SiteInfoBean>
{
    private int key;

    private String name;

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

    @Override
    public int compareTo( final SiteInfoBean other )
    {
        return this.name.compareTo( other.name );
    }
}
