package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DuplicateContentJson
{
    private String contentId;

    private Boolean includeChildren;

    @JsonCreator
    public DuplicateContentJson( @JsonProperty("contentId") final String contentId,
                                 @JsonProperty("includeChildren") final Boolean includeChildren )
    {
        this.contentId = contentId;
        this.includeChildren = includeChildren;
    }

    public String getContentId()
    {
        return contentId;
    }

    public void setContentId( final String contentId )
    {
        this.contentId = contentId;
    }

    public Boolean getIncludeChildren()
    {
        return includeChildren;
    }

    public void setIncludeChildren( final Boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final DuplicateContentJson that = (DuplicateContentJson) o;
        return Objects.equals( contentId, that.contentId ) && Objects.equals( includeChildren, that.includeChildren );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( contentId, includeChildren );
    }
}
