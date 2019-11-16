package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.index.ChildOrder;

public class ContentTreeSelectorQueryJson
    extends ContentSelectorQueryJson
{
    private ContentPath parentPath;

    private ChildOrder childOrder;

    @JsonCreator
    public ContentTreeSelectorQueryJson( @JsonProperty("queryExpr") final String queryExprString, //
                                         @JsonProperty("from") final Integer from, //
                                         @JsonProperty("size") final Integer size, //
                                         @JsonProperty("expand") final String expand, @JsonProperty("contentId") final String contentId,
                                         @JsonProperty("inputName") final String inputName,
                                         @JsonProperty("contentTypeNames") final List<String> contentTypeNamesString,
                                         @JsonProperty("allowedContentPaths") final List<String> allowedContentPaths,
                                         @JsonProperty("relationshipType") final String relationshipType,
                                         @JsonProperty("parentPath") final String parentPath,
                                         @JsonProperty("childOrder") final String childOrder)
    {
        super( queryExprString, from, size, expand, contentId, inputName, contentTypeNamesString, allowedContentPaths, relationshipType );

        if(parentPath != null)
        {
            this.parentPath = ContentPath.from( parentPath );
        }

        if( !Strings.nullToEmpty( childOrder ).isBlank() ) {
            this.childOrder = ChildOrder.from( childOrder );
        }
    }

    @JsonIgnore
    public ContentPath getParentPath()
    {
        return parentPath;
    }

    @JsonIgnore
    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

}
