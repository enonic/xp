package com.enonic.xp.portal.url;

import java.util.Objects;

import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class BaseUrlParams
{
    private final String urlType;

    private final String projectName;

    private final String branch;

    private final String id;

    private final String path;

    private BaseUrlParams( final Builder builder )
    {
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.id = builder.id;
        this.path = builder.path;
    }

    public String getUrlType()
    {
        return urlType;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public String getBranch()
    {
        return branch;
    }

    public String getId()
    {
        return id;
    }

    public String getPath()
    {
        return path;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String urlType;

        private String projectName;

        private String branch;

        private String id;

        private String path;

        public Builder setUrlType( final String urlType )
        {
            this.urlType = urlType;
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

        public Builder setId( final String id )
        {
            this.id = id;
            return this;
        }

        public Builder setPath( final String path )
        {
            this.path = path;
            return this;
        }

        public BaseUrlParams build()
        {
            return new BaseUrlParams( this );
        }
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        helper.omitNullValues();
        helper.add( "type", this.urlType );
        helper.add( "id", this.id );
        helper.add( "path", this.path );
        helper.add( "project", this.projectName );
        helper.add( "branch", this.branch );
        return helper.toString();
    }
}
