package com.enonic.wem.admin.rest.resource.content.model;


import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.facet.QueryFacet;


public class QueryFacetJson
    extends AbstractFacetJson
{
    private String type;

    private List<QueryFacetEntryJson> terms;

    public QueryFacetJson( final List<QueryFacet> facet )
    {
        super( "ranges", "Last Modified" );
        this.type = "terms";

        ImmutableList.Builder<QueryFacetEntryJson> builder = ImmutableList.builder();
        for ( QueryFacet result : facet )
        {
            builder.add( new QueryFacetEntryJson( result ) );
        }
        this.terms = builder.build();
    }

    @JsonProperty(value = "_type")
    public String getType()
    {
        return type;
    }

    public List<QueryFacetEntryJson> getTerms()
    {
        return terms;
    }

    public class QueryFacetEntryJson
        extends AbstractFacetJson.FacetEntryJson
    {

        private String name;

        private String type;

        public QueryFacetEntryJson( final QueryFacet term )
        {
            super( term.getCount() );
            name = term.getName();
            type = "query";
        }

        public String getName()
        {
            return name;
        }

        public String getType()
        {
            return type;
        }
    }

}
