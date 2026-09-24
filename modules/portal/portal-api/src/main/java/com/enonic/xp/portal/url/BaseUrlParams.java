package com.enonic.xp.portal.url;

import java.util.function.Supplier;

import com.google.common.base.MoreObjects;

import com.enonic.xp.content.Content;

import static java.util.Objects.requireNonNullElse;

/**
 * Names a content, and through it the level of the content tree its URLs belong to: the nearest
 * site at or above it, or the project when no site is. That level carries the Base URL
 * configuration, so it decides both the base URL and what content paths are relative to.
 */
public final class BaseUrlParams
{
    private final String urlType;

    private final String projectName;

    private final String branch;

    private final String id;

    private final String path;

    private final Supplier<Content> contentSupplier;

    private BaseUrlParams( final Builder builder )
    {
        this.urlType = requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.id = builder.id;
        this.path = builder.path;
        this.contentSupplier = builder.contentSupplier;
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

    public Supplier<Content> getContent()
    {
        return contentSupplier;
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

        private Supplier<Content> contentSupplier;

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

        /**
         * Names the content directly, as an alternative to {@link #setId(String)} and {@link #setPath(String)}.
         * Useful when the caller already holds the content: no extra lookup is made.
         *
         * @param contentSupplier supplier of the content
         * @return this builder
         */
        public Builder setContent( final Supplier<Content> contentSupplier )
        {
            this.contentSupplier = contentSupplier;
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
