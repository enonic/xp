package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

@PublicApi
public final class ImageUrlGeneratorParams
{
    private final String baseUrl;

    private final String urlType;

    private final Supplier<Media> mediaSupplier;

    private final Supplier<ProjectName> projectNameSupplier;

    private final Supplier<Branch> branchSupplier;

    private final String background;

    private final Integer quality;

    private final String filter;

    private final String format;

    private final String scale;

    private final Multimap<String, String> queryParams;

    private ImageUrlGeneratorParams( final Builder builder )
    {
        this.baseUrl = builder.baseUrl;
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.mediaSupplier = Objects.requireNonNull( builder.mediaSupplier );
        this.projectNameSupplier = Objects.requireNonNull( builder.projectNameSupplier );
        this.branchSupplier = Objects.requireNonNull( builder.branchSupplier );
        this.scale = Objects.requireNonNull( builder.scale );
        this.background = builder.background;
        this.quality = builder.quality;
        this.filter = builder.filter;
        this.format = builder.format;
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

    public Supplier<Media> getMedia()
    {
        return mediaSupplier;
    }

    public Supplier<ProjectName> getProjectName()
    {
        return projectNameSupplier;
    }

    public Supplier<Branch> getBranch()
    {
        return branchSupplier;
    }

    public String getBackground()
    {
        return background;
    }

    public Integer getQuality()
    {
        return quality;
    }

    public String getFilter()
    {
        return filter;
    }

    public String getFormat()
    {
        return format;
    }

    public String getScale()
    {
        return scale;
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

        private Supplier<Media> mediaSupplier;

        private Supplier<ProjectName> projectNameSupplier;

        private Supplier<Branch> branchSupplier;

        private String background;

        private Integer quality;

        private String filter;

        private String format;

        private String scale;

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

        public Builder setMedia( final Supplier<Media> mediaSupplier )
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

        public Builder setBackground( final String background )
        {
            this.background = background;
            return this;
        }

        public Builder setQuality( final Integer quality )
        {
            this.quality = quality;
            return this;
        }

        public Builder setFilter( final String filter )
        {
            this.filter = filter;
            return this;
        }

        public Builder setFormat( final String format )
        {
            this.format = format;
            return this;
        }

        public Builder setScale( final String scale )
        {
            this.scale = scale;
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

        public ImageUrlGeneratorParams build()
        {
            return new ImageUrlGeneratorParams( this );
        }
    }
}
