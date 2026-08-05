package com.enonic.xp.portal.url;

import java.util.function.Supplier;

import com.google.common.base.MoreObjects;

import com.enonic.xp.content.Content;
import com.enonic.xp.descriptor.DescriptorKey;

import static java.util.Objects.requireNonNullElse;


public final class BaseUrlParams
{
    private final String urlType;

    private final String projectName;

    private final String branch;

    private final String id;

    private final String path;

    private final Supplier<Content> contentSupplier;

    private final DescriptorKey api;

    private BaseUrlParams( final Builder builder )
    {
        this.urlType = requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.id = builder.id;
        this.path = builder.path;
        this.contentSupplier = builder.contentSupplier;
        this.api = builder.api;
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

    public DescriptorKey getApi()
    {
        return api;
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

        private DescriptorKey api;

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
         * Sets the content anchor directly, as an alternative to {@link #setId(String)} and {@link #setPath(String)}.
         * Useful when the caller already holds the content: no extra lookup is made.
         *
         * @param contentSupplier supplier of the anchor content
         * @return this builder
         */
        public Builder setContent( final Supplier<Content> contentSupplier )
        {
            this.contentSupplier = contentSupplier;
            return this;
        }

        /**
         * Requests the base URL of an API mount instead of the content base URL.
         * <p>
         * The result is the prefix that the API descriptor ({@code <application>:<name>}) gets appended to.
         * It is resolved to {@code <baseUrl>/_} when a Base URL is configured for the anchored site (or project)
         * and the API is mounted on the site. Media APIs fall back to the {@code media.defaultBaseUrl}
         * configuration, when set. Otherwise the result is {@code null}: URLs should then stay request-based.
         *
         * @param api descriptor key of the API
         * @return this builder
         */
        public Builder setApi( final DescriptorKey api )
        {
            this.api = api;
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
        helper.add( "api", this.api );
        return helper.toString();
    }
}
