package com.enonic.wem.admin.json.facet;


import com.enonic.wem.api.facet.AbstractFacet;

public abstract class FacetJson
{
    private String name;

    public FacetJson( final AbstractFacet facet )
    {
        this.name = facet.getName();
    }

    public String getName()
    {
        return name;
    }

    public class FacetEntryJson
    {
        protected Long count;

        public FacetEntryJson( final Long count )
        {
            this.count = count;
        }

        public Long getCount()
        {
            return count;
        }
    }

}
