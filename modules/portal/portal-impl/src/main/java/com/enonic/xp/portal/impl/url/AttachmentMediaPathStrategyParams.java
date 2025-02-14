package com.enonic.xp.portal.impl.url;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

final class AttachmentMediaPathStrategyParams
{
    private final Supplier<Media> mediaSupplier;

    private final ProjectName projectName;

    private final Branch branch;

    private final boolean download;

    private final String name;

    private final String label;

    private final Multimap<String, String> queryParams;

    private AttachmentMediaPathStrategyParams( final Builder builder )
    {
        this.mediaSupplier = builder.mediaSupplier;
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.download = builder.download;
        this.name = builder.name;
        this.label = builder.label;
        this.queryParams = builder.queryParams;
    }

    public Supplier<Media> getMediaSupplier()
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

    static class Builder
    {
        private Supplier<Media> mediaSupplier;

        private ProjectName projectName;

        private Branch branch;

        private boolean download;

        private String name;

        private String label;

        private final Multimap<String, String> queryParams = HashMultimap.create();

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

        public AttachmentMediaPathStrategyParams build()
        {
            return new AttachmentMediaPathStrategyParams( this );
        }
    }
}
