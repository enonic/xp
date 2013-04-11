package com.enonic.wem.api.query;

import java.util.Set;

import com.google.common.collect.Sets;

public class TermsFacetResultSet
    extends AbstractFacetResultSet
    implements FacetResultSet
{
    private Long total;

    private Long missing;

    private Long other;

    private Set<TermsFacetResultSet.TermFacetResult> results = Sets.newLinkedHashSet();

    public Set<TermFacetResult> getResults()
    {
        return results;
    }

    public void addResult( String term, Integer count )
    {
        results.add( new TermFacetResult( term, count ) );
    }

    public void addResult( String term, String displayName, Integer count )
    {
        results.add( new TermFacetResult( term, displayName, count ) );
    }

    public Long getTotal()
    {
        return total;
    }

    public void setTotal( final Long total )
    {
        this.total = total;
    }

    public Long getMissing()
    {
        return missing;
    }

    public void setMissing( final Long missing )
    {
        this.missing = missing;
    }

    public Long getOther()
    {
        return other;
    }

    public void setOther( final Long other )
    {
        this.other = other;
    }

    public class TermFacetResult
    {
        private String term;

        private String displayName;

        private Integer count;

        private TermFacetResult( final String term, final Integer count )
        {
            this.term = term;
            this.count = count;
            this.displayName = term;
        }

        private TermFacetResult( final String term, final String displayName, final Integer count )
        {
            this.term = term;
            this.displayName = displayName;
            this.count = count;
        }

        public String getTerm()
        {
            return term;
        }

        public String getDisplayName()
        {
            return displayName;
        }

        public void setDisplayName( final String displayName )
        {
            this.displayName = displayName;
        }

        public Integer getCount()
        {
            return count;
        }

    }

}
