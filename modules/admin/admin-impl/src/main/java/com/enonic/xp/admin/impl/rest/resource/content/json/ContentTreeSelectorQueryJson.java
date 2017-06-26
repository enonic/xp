package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.schema.content.ContentTypeNames;

public class ContentTreeSelectorQueryJson
    extends ContentSelectorQueryJson
{
    private ContentPath parentPath;

    @JsonCreator
    public ContentTreeSelectorQueryJson( @JsonProperty("queryExpr") final String queryExprString, //
                                         @JsonProperty("from") final Integer from, //
                                         @JsonProperty("size") final Integer size, //
                                         @JsonProperty("expand") final String expand, @JsonProperty("contentId") final String contentId,
                                         @JsonProperty("inputName") final String inputName,
                                         @JsonProperty("contentTypeNames") final List<String> contentTypeNamesString,
                                         @JsonProperty("allowedContentPaths") final List<String> allowedContentPaths,
                                         @JsonProperty("relationshipType") final String relationshipType,
                                         @JsonProperty("parentPath") final String parentPath )
    {
        super( queryExprString, from, size, expand, contentId, inputName, contentTypeNamesString, allowedContentPaths, relationshipType );

        if(parentPath != null)
        {
            this.parentPath = ContentPath.from( parentPath );
        }
    }

    @JsonIgnore
    public ContentPath getParentPath()
    {
        return parentPath;
    }

}
