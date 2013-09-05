package com.enonic.wem.admin.json.facet;


import org.codehaus.jackson.annotate.JsonProperty;

import com.enonic.wem.api.facet.QueryFacet;


public class QueryFacetJson
    extends FacetJson
{
    private final String type = "query";

    private final QueryFacet queryFacet;

    public QueryFacetJson( final QueryFacet facet )
    {
        super( facet );
        this.queryFacet = facet;
    }

    @JsonProperty(value = "_type")
    public String getType()
    {
        return type;
    }

    public Long getCount()
    {
        return queryFacet.getCount();
    }

}
