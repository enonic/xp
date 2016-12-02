package com.enonic.xp.lib.node;

public class GetChildrenHandlerParams
{
    private String parentKey;

    private String childOrder;

    private boolean recursive;

    private boolean countOnly;

    private Integer count;

    private Integer start;

    public String getParentKey()
    {
        return parentKey;
    }

    public void setParentKey( final String parentKey )
    {
        this.parentKey = parentKey;
    }

    public String getChildOrder()
    {
        return childOrder;
    }

    public void setChildOrder( final String childOrder )
    {
        this.childOrder = childOrder;
    }

    public boolean isRecursive()
    {
        return recursive;
    }

    public void setRecursive( final boolean recursive )
    {
        this.recursive = recursive;
    }

    public boolean isCountOnly()
    {
        return countOnly;
    }

    public void setCountOnly( final boolean countOnly )
    {
        this.countOnly = countOnly;
    }

    public Integer getCount()
    {
        return count;
    }

    public void setCount( final Integer count )
    {
        this.count = count;
    }

    public Integer getStart()
    {
        return start;
    }

    public void setStart( final Integer start )
    {
        this.start = start;
    }
}
