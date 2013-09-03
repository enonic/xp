package com.enonic.wem.admin.rest.resource.content.model;


import com.enonic.wem.api.facet.AbstractFacet;

public abstract class AbstractFacetJson
{
    private String name;

    public AbstractFacetJson( final AbstractFacet facet )
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
