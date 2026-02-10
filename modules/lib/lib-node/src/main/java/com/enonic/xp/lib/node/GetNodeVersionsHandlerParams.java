package com.enonic.xp.lib.node;

@SuppressWarnings("unused")
public class GetNodeVersionsHandlerParams
{
    private String key;

    private String cursor;

    private Integer count;

    public String getKey()
    {
        return key;
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public String getCursor()
    {
        return cursor;
    }

    public void setCursor( final String cursor )
    {
        this.cursor = cursor;
    }

    public Integer getCount()
    {
        return count;
    }

    public void setCount( final Integer count )
    {
        this.count = count;
    }
}
