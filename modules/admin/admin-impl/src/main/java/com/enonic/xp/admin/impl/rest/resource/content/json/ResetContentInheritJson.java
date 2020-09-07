package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.project.ProjectName;

public final class ResetContentInheritJson
{
    private final ContentId contentId;

    private final ProjectName projectName;

    private final Set<ContentInheritType> inherit;

    @JsonCreator
    ResetContentInheritJson( @JsonProperty("contentId") final String contentId, @JsonProperty("project") final String projectName,
                             @JsonProperty("inherit") final List<String> inherit )
    {
        this.contentId = ContentId.from( contentId );
        this.projectName = ProjectName.from( projectName );
        this.inherit = inherit.stream().map( ContentInheritType::valueOf ).collect( Collectors.toSet() );
    }

    public ResetContentInheritParams toParams()
    {
        return ResetContentInheritParams.create().
            contentId( contentId ).
            projectName( projectName ).
            inherit( inherit ).
            build();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }

    public Collection<ContentInheritType> getInherit()
    {
        return inherit;
    }
}
