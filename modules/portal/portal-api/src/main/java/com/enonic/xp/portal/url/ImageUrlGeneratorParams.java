package com.enonic.xp.portal.url;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

public class ImageUrlGeneratorParams
{
    private final BaseUrlStrategy baseUrlStrategy;

    private final PathPrefixStrategy pathPrefixStrategy;

    private final RewritePathStrategy rewritePathStrategy;

    private final Media media;

    private final ProjectName projectName;

    private final Branch branch;

    private final String background;

    private final Integer quality;

    private final String filter;

    private final String format;

    private final String scale;

    private ImageUrlGeneratorParams( final Builder builder )
    {
        this.baseUrlStrategy = Objects.requireNonNull( builder.baseUrlStrategy );
        this.pathPrefixStrategy = Objects.requireNonNull( builder.pathPrefixStrategy );
        this.rewritePathStrategy = Objects.requireNonNull( builder.rewritePathStrategy );
        this.media = Objects.requireNonNull( builder.media );
        this.projectName = Objects.requireNonNull( builder.projectName );
        this.branch = Objects.requireNonNull( builder.branch );
        this.scale = Objects.requireNonNull( builder.scale );
        this.background = builder.background;
        this.quality = builder.quality;
        this.filter = builder.filter;
        this.format = builder.format;
    }

    public BaseUrlStrategy getBaseUrlStrategy()
    {
        return baseUrlStrategy;
    }

    public PathPrefixStrategy getPathPrefixStrategy()
    {
        return pathPrefixStrategy;
    }

    public RewritePathStrategy getRewritePathStrategy()
    {
        return rewritePathStrategy;
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private BaseUrlStrategy baseUrlStrategy;

        private PathPrefixStrategy pathPrefixStrategy;

        private RewritePathStrategy rewritePathStrategy;

        private Media media;

        private ProjectName projectName;

        private Branch branch;

        private String background;

        private Integer quality;

        private String filter;

        private String format;

        private String scale;

        public Builder setBaseUrlStrategy( final BaseUrlStrategy baseUrlStrategy )
        {
            this.baseUrlStrategy = baseUrlStrategy;
            return this;
        }

        public Builder setPathPrefixStrategy( final PathPrefixStrategy pathPrefixStrategy )
        {
            this.pathPrefixStrategy = pathPrefixStrategy;
            return this;
        }

        public Builder setRewritePathStrategy( final RewritePathStrategy rewritePathStrategy )
        {
            this.rewritePathStrategy = rewritePathStrategy;
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

        public ImageUrlGeneratorParams build()
        {
            return new ImageUrlGeneratorParams( this );
        }
    }
}
