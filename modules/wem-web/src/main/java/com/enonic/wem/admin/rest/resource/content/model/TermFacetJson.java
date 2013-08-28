package com.enonic.wem.admin.rest.resource.content.model;


import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.facet.TermsFacet;
import com.enonic.wem.api.facet.TermsFacetEntry;

public class TermFacetJson
    extends AbstractFacetJson
{

    private String type;

    private List<TermFacetEntryJson> terms;

    public TermFacetJson( final TermsFacet facet )
    {
        super( facet );

        this.type = "terms";

        ImmutableList.Builder<TermFacetEntryJson> builder = ImmutableList.builder();
        for ( TermsFacetEntry result : facet.getResults() )
        {
            builder.add( new TermFacetEntryJson( result ) );
        }
        this.terms = builder.build();
    }

    @JsonProperty(value = "_type")
    public String getType()
    {
        return type;
    }

    public List<TermFacetEntryJson> getTerms()
    {
        return terms;
    }

    public class TermFacetEntryJson
        extends AbstractFacetJson.FacetEntryJson
    {

        private String name;

        private String displayName;

        public TermFacetEntryJson( final TermsFacetEntry term )
        {
            super( Long.valueOf( term.getCount() ) );
            name = term.getTerm();
            displayName = term.getDisplayName();
        }

        public String getName()
        {
            return name;
        }

        public String getDisplayName()
        {
            return displayName;
        }
    }
}
