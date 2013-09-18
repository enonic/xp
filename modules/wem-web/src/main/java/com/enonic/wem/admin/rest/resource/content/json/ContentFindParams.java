package com.enonic.wem.admin.rest.resource.content.json;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.DateTimeFormatter;

@XmlRootElement
public class ContentFindParams
{
    private String fulltext;

    private boolean includeFacets;

    private Set<String> contentTypes;

    private Set<String> spaces;

    private Set<Range> ranges;

    private String facets;

    private String expand = "none";

    public String getFulltext()
    {
        return fulltext;
    }

    public void setFulltext( final String fulltext )
    {
        this.fulltext = fulltext;
    }

    @JsonProperty("include")
    public Boolean isIncludeFacets()
    {
        return includeFacets;
    }

    public void setIncludeFacets( final String includeFacets )
    {
        this.includeFacets = Boolean.valueOf( includeFacets );
    }

    public Set<String> getContentTypes()
    {
        return contentTypes;
    }

    public void setContentTypes( final Set<String> contentTypes )
    {
        this.contentTypes = contentTypes;
    }

    public Set<String> getSpaces()
    {
        return spaces;
    }

    public void setSpaces( final Set<String> spaces )
    {
        this.spaces = spaces;
    }

    public Set<Range> getRanges()
    {
        return ranges;
    }

    public void setRanges( final Set<Range> ranges )
    {
        this.ranges = ranges;
    }

    public String getFacets()
    {
        return facets;
    }

    public void setFacets( final ObjectNode facets )
    {
        this.facets = facets.toString();
    }

    public String getExpand()
    {
        return expand;
    }

    public void setExpand( final String expand )
    {
        this.expand = expand;
    }

    public class Range
    {
        DateTime lower;

        DateTime upper;

        public DateTime getLower()
        {
            return lower;
        }

        public void setLower( final String lower )
        {
            this.lower = DateTimeFormatter.parse( lower );
        }

        public DateTime getUpper()
        {
            return upper;
        }

        public void setUpper( final String upper )
        {
            this.upper = DateTimeFormatter.parse( upper );
        }
    }

}
