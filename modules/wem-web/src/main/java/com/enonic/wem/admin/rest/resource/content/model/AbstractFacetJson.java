package com.enonic.wem.admin.rest.resource.content.model;


import com.enonic.wem.api.facet.AbstractFacet;

public abstract class AbstractFacetJson
{
    private String name;

    private String displayName;

    public AbstractFacetJson( final AbstractFacet facet )
    {
        this.name = facet.getName();
        this.displayName = facet.getDisplayName();
    }

    protected AbstractFacetJson( final String name, final String displayName )
    {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
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
