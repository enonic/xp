package com.enonic.wem.api.content.query;

import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.collect.Sets;

import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.space.SpaceNames;

public class ContentIndexQuery
{

    private final static int DEFAULT_COUNT = 100;

    private String fullTextSearchString;

    private boolean includeFacets = false;

    private String facets;

    private QualifiedContentTypeNames contentTypeNames;

    private SpaceNames spaceNames;

    private Set<Range> ranges = Sets.newHashSet();

    private int count = DEFAULT_COUNT;

    public void addRange( final DateTime lower, final DateTime upper )
    {
        ranges.add( new Range( lower, upper ) );
    }

    public Set<Range> getRanges()
    {
        return ranges;
    }

    public void setFullTextSearchString( final String fullTextSearchString )
    {
        this.fullTextSearchString = fullTextSearchString;
    }

    public String getFullTextSearchString()
    {
        return fullTextSearchString;
    }

    public boolean isIncludeFacets()
    {
        return includeFacets;
    }

    public void setIncludeFacets( final boolean includeFacets )
    {
        this.includeFacets = includeFacets;
    }

    public void setFacets( final String facets )
    {
        this.facets = facets;
    }

    public String getFacets()
    {
        return facets;
    }

    public QualifiedContentTypeNames getContentTypeNames()
    {
        return contentTypeNames;
    }

    public void setContentTypeNames( final QualifiedContentTypeNames contentTypeNames )
    {
        this.contentTypeNames = contentTypeNames;
    }

    public SpaceNames getSpaceNames()
    {
        return spaceNames;
    }

    public void setSpaceNames( final SpaceNames spaceNames )
    {
        this.spaceNames = spaceNames;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount( final int count )
    {
        this.count = count;
    }

    public class Range
    {
        DateTime lower;

        DateTime upper;

        boolean includeLower = true;

        boolean includeUpper = true;

        private Range( final DateTime lower, final DateTime upper )
        {
            this.lower = lower;
            this.upper = upper;
        }

        public DateTime getLower()
        {
            return lower;
        }

        public DateTime getUpper()
        {
            return upper;
        }

        public boolean isIncludeLower()
        {
            return includeLower;
        }

        public boolean isIncludeUpper()
        {
            return includeUpper;
        }

        public void setIncludeLower( final boolean includeLower )
        {
            this.includeLower = includeLower;
        }

        public void setIncludeUpper( final boolean includeUpper )
        {
            this.includeUpper = includeUpper;
        }
    }

}
