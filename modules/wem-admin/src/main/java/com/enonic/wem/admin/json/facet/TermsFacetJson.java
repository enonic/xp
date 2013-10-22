package com.enonic.wem.admin.json.facet;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.facet.TermsFacet;
import com.enonic.wem.api.facet.TermsFacetEntry;

public class TermsFacetJson
    extends FacetJson
{
    private final String type = "terms";

    private final List<TermFacetEntryJson> entries;

    public TermsFacetJson( final TermsFacet facet )
    {
        super( facet );

        ImmutableList.Builder<TermFacetEntryJson> builder = ImmutableList.builder();
        for ( TermsFacetEntry result : facet.getResults() )
        {
            builder.add( new TermFacetEntryJson( result ) );
        }
        this.entries = builder.build();
    }

    @JsonProperty(value = "_type")
    public String getType()
    {
        return type;
    }

    public List<TermFacetEntryJson> getEntries()
    {
        return entries;
    }

    public class TermFacetEntryJson
        extends FacetJson.FacetEntryJson
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
