package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;

public class ContentSelectorQueryJson
{
    private final String expand;

    private final String queryExprString;

    private final ContentId contentId;

    private final String inputName;

    private final List<String> contentTypeNames;

    private final List<String> allowedContentPaths;

    private final String relationshipType;

    private Integer from;

    private Integer size;

    @JsonCreator
    public ContentSelectorQueryJson( @JsonProperty("queryExpr") final String queryExprString, //
                                     @JsonProperty("from") final Integer from, //
                                     @JsonProperty("size") final Integer size, //
                                     @JsonProperty("expand") final String expand, @JsonProperty("contentId") final String contentId,
                                     @JsonProperty("inputName") final String inputName,
                                     @JsonProperty("contentTypeNames") final List<String> contentTypeNamesString,
                                     @JsonProperty("allowedContentPaths") final List<String> allowedContentPaths,
                                     @JsonProperty("relationshipType") final String relationshipType )
    {

        this.from = from;
        this.size = size;
        this.queryExprString = queryExprString;
        this.contentId = contentId != null ? ContentId.from( contentId ) : null;
        this.expand = expand != null ? expand : "none";
        this.inputName = inputName;
        this.contentTypeNames = contentTypeNamesString;
        this.allowedContentPaths = allowedContentPaths;
        this.relationshipType = relationshipType;
    }

    @JsonIgnore
    public String getQueryExprString()
    {
        return queryExprString;
    }

    @JsonIgnore
    public Integer getFrom()
    {
        return from;
    }

    @JsonIgnore
    public Integer getSize()
    {
        return size;
    }

    @JsonIgnore
    public ContentId getContentId()
    {
        return contentId;
    }

    @JsonIgnore
    public String getExpand()
    {
        return expand;
    }


    public String getInputName()
    {
        return inputName;
    }

    @JsonIgnore
    public List<String> getContentTypeNames()
    {
        return contentTypeNames;
    }

    @JsonIgnore
    public List<String> getAllowedContentPaths()
    {
        return allowedContentPaths;
    }

    @JsonIgnore
    public String getRelationshipType()
    {
        return relationshipType;
    }

    public void setFrom( final Integer from )
    {
        this.from = from;
    }

    public void setSize( final Integer size )
    {
        this.size = size;
    }
}
