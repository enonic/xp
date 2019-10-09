package com.enonic.xp.lib.node;

@SuppressWarnings("unused")
public class FindVersionsHandlerParams
{
    private String key;

    private Integer start;

    private Integer count;

    public String getKey()
    {
        return key;
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public Integer getStart()
    {
        return start;
    }

    public void setStart( final Integer start )
    {
        this.start = start;
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
