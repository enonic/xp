package com.enonic.xp.lib.node;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.script.ScriptValue;

public class QueryNodeHandlerParams
{
    private Integer start;

    private Integer count;

    private String query;

    private String sort;

    private Map<String, Object> aggregations;


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

    public String getQuery()
    {
        return query;
    }

    public void setQuery( final String query )
    {
        this.query = query;
    }

    public String getSort()
    {
        return sort;
    }

    public void setSort( final String sort )
    {
        this.sort = sort;
    }

    public Map<String, Object> getAggregations()
    {
        return aggregations;
    }

    public void setAggregations( final ScriptValue aggregations )
    {
        this.aggregations = aggregations != null ? aggregations.getMap() : Maps.newHashMap();
    }

}
