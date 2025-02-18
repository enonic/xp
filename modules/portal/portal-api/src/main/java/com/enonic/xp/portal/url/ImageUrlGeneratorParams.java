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
    private final BaseUrlStrategy baseUrlStrategy;

    private final Supplier<Media> mediaSupplier;

    private final ProjectName projectName;

    private final Branch branch;

    private final String background;

    private final Integer quality;

    private final String filter;

    private final String format;

    private final String scale;

    private final Multimap<String, String> queryParams;

    private ImageUrlGeneratorParams( final Builder builder )
    {
        this.baseUrlStrategy = Objects.requireNonNull( builder.baseUrlStrategy );
        this.mediaSupplier = Objects.requireNonNull( builder.mediaSupplier );
        this.projectName = Objects.requireNonNull( builder.projectName );
        this.branch = Objects.requireNonNull( builder.branch );
        this.scale = Objects.requireNonNull( builder.scale );
        this.background = builder.background;
        this.quality = builder.quality;
        this.filter = builder.filter;
        this.format = builder.format;
        this.queryParams = builder.queryParams;
    }

    public BaseUrlStrategy getBaseUrlStrategy()
    {
        return baseUrlStrategy;
    }

    public Supplier<Media> getMedia()
    {
        return mediaSupplier;
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }

    public Branch getBranch()
    {
        return branch;
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
        private BaseUrlStrategy baseUrlStrategy;

        private Supplier<Media> mediaSupplier;

        private ProjectName projectName;

        private Branch branch;

        private String background;

        private Integer quality;

        private String filter;

        private String format;

        private String scale;

        private final Multimap<String, String> queryParams = HashMultimap.create();

        public Builder setBaseUrlStrategy( final BaseUrlStrategy baseUrlStrategy )
        {
            this.baseUrlStrategy = baseUrlStrategy;
            return this;
        }

        public Builder setMedia( final Supplier<Media> mediaSupplier )
        {
            this.mediaSupplier = mediaSupplier;
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
