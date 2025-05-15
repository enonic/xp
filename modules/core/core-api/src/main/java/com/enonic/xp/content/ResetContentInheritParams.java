package com.enonic.xp.content;

import java.util.Collection;
import java.util.EnumSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.project.ProjectName;

@PublicApi
public final class ResetContentInheritParams
{
    private final ContentId contentId;

    private final ProjectName projectName;

    private final EnumSet<ContentInheritType> inherit;

    private ResetContentInheritParams( Builder builder )
    {
        contentId = builder.contentId;
        projectName = builder.projectName;
        inherit = builder.inherit;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }

    public EnumSet<ContentInheritType> getInherit()
    {
        return inherit;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private ProjectName projectName;

        private EnumSet<ContentInheritType> inherit;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder projectName( ProjectName projectName )
        {
            this.projectName = projectName;
            return this;
        }

        public Builder inherit( Collection<ContentInheritType> inherit )
        {
            this.inherit = inherit.isEmpty() ? EnumSet.noneOf( ContentInheritType.class ) : EnumSet.copyOf( inherit );
            return this;
        }

        public ResetContentInheritParams build()
        {
            return new ResetContentInheritParams( this );
        }
    }
}
