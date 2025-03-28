package com.enonic.xp.portal.impl.url;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.Site;

final class BaseUrlMetadata
{
    private final String baseUrl;

    private final Site nearestSite;

    private final Content content;

    private final ProjectName projectName;

    private final Branch branch;

    private BaseUrlMetadata( final Builder builder )
    {
        this.baseUrl = builder.baseUrl;
        this.nearestSite = builder.nearestSite;
        this.content = builder.content;
        this.projectName = builder.projectName;
        this.branch = builder.branch;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public Site getNearestSite()
    {
        return nearestSite;
    }

    public Content getContent()
    {
        return content;
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private String baseUrl;

        private Site nearestSite;

        private Content content;

        private ProjectName projectName;

        private Branch branch;

        public Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setNearestSite( final Site nearestSite )
        {
            this.nearestSite = nearestSite;
            return this;
        }

        public Builder setContent( final Content content )
        {
            this.content = content;
            return this;
        }

        public Builder setProjectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return this;
        }

        public Builder setBranch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public BaseUrlMetadata build()
        {
            return new BaseUrlMetadata( this );
        }
    }
}
