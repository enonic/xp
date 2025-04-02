package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.project.ProjectName;

@PublicApi
public final class AttachmentUrlGeneratorParams
{
    private final String baseUrl;

    private final String urlType;

    private final Supplier<Content> contentSupplier;

    private final Supplier<ProjectName> projectName;

    private final Supplier<Branch> branch;

    private final boolean download;

    private final String name;

    private final String label;

    private final Multimap<String, String> queryParams;

    private AttachmentUrlGeneratorParams( final Builder builder )
    {
        this.baseUrl = builder.baseUrl;
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.contentSupplier = Objects.requireNonNull( builder.mediaSupplier );
        this.projectName = Objects.requireNonNull( builder.projectNameSupplier );
        this.branch = Objects.requireNonNull( builder.branchSupplier );
        this.download = builder.download;
        this.name = builder.name;
        this.label = builder.label;
        this.queryParams = builder.queryParams;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public String getUrlType()
    {
        return urlType;
    }

    public Supplier<Content> getContentSupplier()
    {
        return contentSupplier;
    }

    public Supplier<ProjectName> getProjectName()
    {
        return projectName;
    }

    public Supplier<Branch> getBranch()
    {
        return branch;
    }

    public boolean isDownload()
    {
        return download;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public Map<String, Collection<String>> getQueryParams()
    {
        return queryParams.asMap();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String baseUrl;

        private String urlType;

        private Supplier<Content> mediaSupplier;

        private Supplier<ProjectName> projectNameSupplier;

        private Supplier<Branch> branchSupplier;

        private boolean download;

        private String name;

        private String label;

        private final Multimap<String, String> queryParams = HashMultimap.create();

        public Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setUrlType( final String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        public Builder setContent( final Supplier<Content> mediaSupplier )
        {
            this.mediaSupplier = mediaSupplier;
            return this;
        }

        public Builder setProjectName( final Supplier<ProjectName> projectNameSupplier )
        {
            this.projectNameSupplier = projectNameSupplier;
            return this;
        }

        public Builder setBranch( final Supplier<Branch> branchSupplier )
        {
            this.branchSupplier = branchSupplier;
            return this;
        }

        public Builder setDownload( final boolean download )
        {
            this.download = download;
            return this;
        }

        public Builder setName( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder setLabel( final String label )
        {
            this.label = label;
            return this;
        }

        public Builder addQueryParams( final Map<String, Collection<String>> queryParams )
        {
            queryParams.forEach( this.queryParams::putAll );
            return this;
        }

        public Builder addQueryParam( final String key, final String value )
        {
            this.queryParams.put( key, value );
            return this;
        }

        public AttachmentUrlGeneratorParams build()
        {
            return new AttachmentUrlGeneratorParams( this );
        }
    }
}
