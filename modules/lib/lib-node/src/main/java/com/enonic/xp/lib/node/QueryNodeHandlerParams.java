package com.enonic.xp.lib.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.xp.script.ScriptValue;

@SuppressWarnings("unused")
public class QueryNodeHandlerParams
{
    private Integer start;

    private Integer count;

    private ScriptValue query;

    private String sort;

    private Map<String, Object> aggregations;

    private Map<String, Object> suggestions;

    private Map<String, Object> highlight;

    private List<Map<String, Object>> filters;

    private boolean explain;

    List<Map<String, Object>> getFilters()
    {
        return filters;
    }

    @SuppressWarnings("unused")
    public void setFilters( final ScriptValue filters )
    {
        this.filters = doSetFilters( filters );
    }

    private List<Map<String, Object>> doSetFilters( final ScriptValue filters )
    {
        List<Map<String, Object>> filterList = new ArrayList<>();

        if ( filters == null )
        {
            return filterList;
        }

        if ( filters.isObject() )
        {
            filterList.add( filters.getMap() );
        }
        else if ( filters.isArray() )
        {
            filters.getArray().forEach( sv -> {

                if ( !sv.isObject() )
                {
                    throw new IllegalArgumentException( "Array elements not of type objects" );
                }

                filterList.add( sv.getMap() );
            } );
        }

        return filterList;
    }

    Integer getStart()
    {
        return start;
    }

    public boolean isExplain()
    {
        return explain;
    }

    @SuppressWarnings("unused")
    public void setStart( final Integer start )
    {
        this.start = start;
    }

    Integer getCount()
    {
        return count;
    }

    public void setCount( final Integer count )
    {
        this.count = count;
    }

    ScriptValue getQuery()
    {
        return query;
    }

    @SuppressWarnings("unused")
    public void setQuery( final ScriptValue query )
    {
        this.query = query;
    }

    String getSort()
    {
        return sort;
    }

    @SuppressWarnings("unused")
    public void setSort( final String sort )
    {
        this.sort = sort;
    }

    Map<String, Object> getAggregations()
    {
        return aggregations;
    }

    @SuppressWarnings("unused")
    public void setAggregations( final ScriptValue aggregations )
    {
        this.aggregations = aggregations != null ? aggregations.getMap() : new HashMap<>();
    }

    Map<String, Object> getSuggestions()
    {
        return suggestions;
    }

    @SuppressWarnings("unused")
    public void setSuggestions( final ScriptValue suggestions )
    {
        this.suggestions = suggestions != null ? suggestions.getMap() : new HashMap<>();
    }

    Map<String, Object> getHighlight()
    {
        return highlight;
    }

    @SuppressWarnings("unused")
    public void setHighlight( final ScriptValue highlight )
    {
        this.highlight = highlight != null ? highlight.getMap() : new HashMap<>();
    }

    public void setExplain( final boolean explain )
    {
        this.explain = explain;
    }
}
