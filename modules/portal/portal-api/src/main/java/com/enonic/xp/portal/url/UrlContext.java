package com.enonic.xp.portal.url;

import com.enonic.xp.web.WebRequest;

public final class UrlContext
{

    private final String projectName;

    private final String branch;

    private final String siteKey;

    public UrlContext( final Builder builder )
    {
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.siteKey = builder.siteKey;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public String getBranch()
    {
        return branch;
    }

    public String getSiteKey()
    {
        return siteKey;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private WebRequest webRequest;

        private String projectName;

        private String branch;

        private String siteKey;

        public Builder setWebRequest( final WebRequest webRequest )
        {
            this.webRequest = webRequest;
            return this;
        }

        public Builder setProjectName( final String projectName )
        {
            this.projectName = projectName;
            return this;
        }

        public Builder setBranch( final String branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder setSiteKey( final String siteKey )
        {
            this.siteKey = siteKey;
            return this;
        }

        public UrlContext build()
        {
            return new UrlContext( this );
        }
    }
}
