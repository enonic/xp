package com.enonic.wem.web.rest.common;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public abstract class LoadStoreRequest
{
    @DefaultValue("0")
    @QueryParam("start")
    private int start = 0;

    @DefaultValue("10")
    @QueryParam("limit")
    private int limit = 10;

    @QueryParam("sort")
    private String sort;

    @DefaultValue("ASC")
    @QueryParam("dir")
    private String sortDir = "ASC";

    @DefaultValue("")
    @QueryParam("query")
    private String query = "";

    public int getStart()
    {
        return this.start;
    }

    public void setStart( final int start )
    {
        this.start = start;
    }

    public int getLimit()
    {
        return this.limit;
    }

    public void setLimit( final int limit )
    {
        this.limit = limit;
    }

    public String getSort()
    {
        return this.sort;
    }

    public void setSort( final String sort )
    {
        this.sort = sort;
    }

    /**
     * Get sorting direction.
     * @return sorting direction: ASC or DESC.
     */
    public String getDir()
    {
        return this.sortDir;
    }

    public void setDir( final String dir )
    {
        if ( "DESC".equalsIgnoreCase( dir ) )
        {
            this.sortDir = "DESC";
        }
        else
        {
            this.sortDir = "ASC";
        }
    }

    public String getQuery()
    {
        return this.query;
    }

    public void setQuery( final String query )
    {
        this.query = query;
    }
}
