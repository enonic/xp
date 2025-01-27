package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

@PublicApi
public final class AttachmentUrlGeneratorParams
{
    private final BaseUrlStrategy baseUrlStrategy;

    private final Media media;

    private final ProjectName projectName;

    private final Branch branch;

    private final boolean download;

    private final String name;

    private final String label;

    private final Multimap<String, String> queryParams;

    private AttachmentUrlGeneratorParams( final Builder builder )
    {
        this.baseUrlStrategy = Objects.requireNonNull( builder.baseUrlStrategy );
        this.media = Objects.requireNonNull( builder.media );
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.download = builder.download;
        this.name = builder.name;
        this.label = builder.label;
        this.queryParams = builder.queryParams;
    }

    public BaseUrlStrategy getBaseUrlStrategy()
    {
        return baseUrlStrategy;
    }

    public Media getMedia()
    {
        return media;
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }

    public Branch getBranch()
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

    public Multimap<String, String> getQueryParams()
    {
        return queryParams;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private BaseUrlStrategy baseUrlStrategy;

        private Media media;

        private ProjectName projectName;

        private Branch branch;

        private boolean download;

        private String name;

        private String label;

        private final Multimap<String, String> queryParams = HashMultimap.create();

        public Builder setBaseUrlStrategy( final BaseUrlStrategy baseUrlStrategy )
        {
            this.baseUrlStrategy = baseUrlStrategy;
            return this;
        }

        public Builder setMedia( final Media media )
        {
            this.media = media;
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
